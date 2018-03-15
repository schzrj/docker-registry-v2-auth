package com.zhangj.registry.auth.service;

import com.zhangj.registry.auth.properties.RegistryAuthProperties;
import com.zhangj.registry.auth.utils.StringUtils;
import lombok.Data;
import net.sf.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Map;

/**
 * @author zhangjun
 * @description
 * @date 2018/1/12
 */
@Data
public class JWTBuilder {
    private String payloadHeader;
    private String payloadClaims;
    private String token;
    private RegistryAuthProperties registryAuthProperties;

    public JWTBuilder setProperties(RegistryAuthProperties registryAuthProperties) {
        this.registryAuthProperties = registryAuthProperties;
        return this;
    }


    public JWTBuilder setHeader(Map<String, Object> payloadHeader) {
        this.setPayloadHeader(StringUtils.trimRight(Base64Utils.encodeToUrlSafeString(JSONObject.fromObject(payloadHeader).toString().getBytes()), '='));
        return this;
    }



    public JWTBuilder setClaims(Map<String, Object> claims) {
        this.setPayloadClaims(StringUtils.trimRight(Base64Utils.encodeToUrlSafeString(JSONObject.fromObject(claims).toString().getBytes()), '='));
        return this;
    }

    public JWTBuilder signWith(String crypto, PrivateKey privateKey) {
        String payload = new StringBuffer().append(this.getPayloadHeader()).append(".").append(this.getPayloadClaims()).toString();
        try {
            Signature signature = Signature.getInstance(crypto);
            signature.initSign(privateKey);
            signature.update(payload.getBytes("utf-8"));
            String signatureStr = StringUtils.trimRight(Base64Utils.encodeToUrlSafeString(signature.sign()), '=');
            this.setToken(new StringBuilder().append(payload).append(".").append(signatureStr).toString());
        } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException | SignatureException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getTokenWebJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", this.getToken());
        jsonObject.put("expires_in", Integer.valueOf(registryAuthProperties.getTokenExpire()) * 60);
        jsonObject.put("issued_at", DateTime.now(DateTimeZone.UTC) + "");
        return jsonObject.toString();
    }
}
