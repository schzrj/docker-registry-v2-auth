package com.zhangj.registry.auth.service;


import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.zhangj.registry.auth.filters.AccessFilter;
import com.zhangj.registry.auth.filters.RegistryFilter;
import com.zhangj.registry.auth.filters.RepositoryFilter;
import com.zhangj.registry.auth.model.AccessScope;
import com.zhangj.registry.auth.model.Account;
import com.zhangj.registry.auth.model.UserInfo;
import com.zhangj.registry.auth.properties.RegistryAuthProperties;
import com.zhangj.registry.auth.token.AccountLoginToken;
import jodd.util.Base32;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.picketlink.json.jose.crypto.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Decoder;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author zhangjun
 * @description
 * @date 2018/1/12
 */
@Component
@Slf4j
public class RegistryAuthService {
    private final AccountService accountService;
    private final RegistryAuthProperties registryAuthProperties;
    private final RepositoryFilter repositoryFilter;
    private final RegistryFilter registryFilter;
    public Map<String, Object> registryFilterMap = new HashMap<String, Object>();


    @Autowired
    public RegistryAuthService(RepositoryFilter repositoryFilter, RegistryFilter registryFilter, RegistryAuthProperties registryAuthProperties, AccountService accountService) {
        this.repositoryFilter = repositoryFilter;
        this.registryFilter = registryFilter;
        this.registryAuthProperties = registryAuthProperties;
        this.accountService = accountService;
        registryFilterMap.put("registry", registryFilter);
        registryFilterMap.put("repository", repositoryFilter);
    }

    public String auth(HttpServletRequest request) {
        String service = request.getParameter("service");
        log.info("[{}]Request service: [{}]", request.getRemoteHost(), service);
        if (StringUtils.isEmpty(service) || !service.equals(registryAuthProperties.getService())) {
            return "";
        }
        AccountLoginToken accountLoginToken = checkAccount(request);
        if (accountLoginToken == null) {
            return "";
        }
        try {
            return getDefaultJwtToken(accountLoginToken.getUserName(), service, getAccess(request, accountLoginToken.getUserName())).getTokenWebJson();
        } catch (Exception e) {
            log.error("Docker auth create jwt failure, cause: {}", Throwables.getStackTraceAsString(e));
        }
        return "";
    }

    private List<AccessScope> getAccess(HttpServletRequest request, String userName) {
        UserInfo userInfoDto = new UserInfo();
        List<AccessScope> list = new ArrayList<>();
        Account account = accountService.findByAccount(userName);
        if (account == null) {
            return Collections.EMPTY_LIST;
        }
        userInfoDto.setUserName(userName);
        userInfoDto.setUserId(account.getId());
        userInfoDto.setIsAdmin(isAdmin(account.getId()));
        String scope = request.getParameter("scope");
        log.info("[{}]Request scope: [{}]", request.getRemoteHost(), scope);
        if (!StringUtils.isEmpty(scope)) {
            String[] scopes = scope.split(" ");
            for (String s : scopes) {
                list.add(new AccessScope(s));
            }
            for (AccessScope accessScope : list) {
                AccessFilter accessFilter = (AccessFilter) registryFilterMap.get(accessScope.getType());
                if (accessFilter == null) {
                    accessScope.setActions(new String[]{});
                    continue;
                }
                accessFilter.filter(userInfoDto, accessScope);
            }
        }
        return list;
    }

    private AccountLoginToken checkAccount(HttpServletRequest request) {
        AccountLoginToken accountLoginToken = new AccountLoginToken(null, null);
        String authorization = request.getHeader("authorization");
        String prefix = "Basic ";

        if (StringUtils.isEmpty(authorization) || !authorization.startsWith(prefix)) {
            return accountLoginToken;
        }
        String auth = authorization.substring(prefix.length());
        String accountInfoStr = new String(Base64Utils.decodeFromString(auth));

        if (StringUtils.isEmpty(accountInfoStr)) {
            return accountLoginToken;
        }
        if (!accountInfoStr.contains(":")) {
            return accountLoginToken;
        }
        accountLoginToken = new AccountLoginToken(accountInfoStr.substring(0, accountInfoStr.indexOf(":")), accountInfoStr.substring(accountInfoStr.indexOf(":") + 1));
        Account account = accountService.login(accountLoginToken.getUserName(), String.valueOf(accountLoginToken.getPassword()));
        if (account == null) {
            return accountLoginToken;
        }
        return accountLoginToken;
    }

    private Boolean isAdmin(Long userId) {
        return accountService.isAdmin(userId);
    }

    private JWTBuilder getDefaultJwtToken(String username, String service, List<AccessScope> access) throws Exception {
        try {
            if (StringUtils.isEmpty(username)) {
                return new JWTBuilder();
            }
            return new JWTBuilder().setProperties(registryAuthProperties)
                    .setHeader(getJWTHeader())
                    .setClaims(getDefaultClaims(username, service, access))
                    .signWith(Algorithm.RS256.getAlgorithm(), getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getDefaultClaims(String username, String service, List<AccessScope> access) {
        Map<String, Object> claims = new HashMap<>();
        DateTime now = DateTime.now(DateTimeZone.UTC);
        long exp = now.plusSeconds(Integer.valueOf(registryAuthProperties.getTokenExpire())).getMillis();
        long iat = now.getMillis();
        claims.put("access", access);
        claims.put("iss", registryAuthProperties.getIssuer());
        claims.put("sub", username);
        claims.put("aud", service);
        claims.put("exp", TimeUnit.MILLISECONDS.toSeconds(exp));
        claims.put("iat", TimeUnit.MILLISECONDS.toSeconds(iat));
        claims.put("jti", UUID.randomUUID().toString().replace("-", ""));
        return claims;
    }

    private Map<String, Object> getJWTHeader() throws Exception {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "RS256");
        header.put("kid", getPublicKeyId());
        return header;
    }

    protected PublicKey getPublicCertKey() throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(formatPublicKey(getResourceBytes(registryAuthProperties.getPublicKeyPath())));
        return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(keyBytes)).getPublicKey();
    }

    protected PrivateKey getPrivateKey() throws Exception {
        PKCS8EncodedKeySpec priPKCS8;
        priPKCS8 = new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(formatPrivateKey(getResourceBytes(registryAuthProperties.getPrivateKeyPath()))));
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        return keyf.generatePrivate(priPKCS8);
    }

    private String formatPrivateKey(byte[] keyBytes) throws UnsupportedEncodingException {
        return new String(keyBytes, "UTF-8")
                .replaceAll("(-+BEGIN PRIVATE KEY-+\\r?\\n|-+END PRIVATE KEY-+\\r?\\n?)", "");
    }

    private String formatPublicKey(byte[] keyBytes) throws IOException {
        return new String(keyBytes, "UTF-8")
                .replaceAll("(-+BEGIN CERTIFICATE-+\\r?\\n|-+END CERTIFICATE-+\\r?\\n?)", "");
    }

    private String getPublicKeyId() {
        return getKeyId();
    }

    private byte[] getResourceBytes(String publicCertKeyFileName) throws IOException {
        String pubkey = Resources.toString(Resources.getResource(publicCertKeyFileName), Charsets.UTF_8);
        return pubkey.getBytes();
    }

    private String getKeyId() {
        String keyId = "";
        try {
            PublicKey publicCertKey = getPublicCertKey();
            if (publicCertKey instanceof RSAPublicKeyImpl) {
                RSAPublicKeyImpl publicKey = (RSAPublicKeyImpl) publicCertKey;
                ASN1EncodableVector derEncodableVector = new ASN1EncodableVector();
                derEncodableVector.add(new ASN1Integer(publicKey.getModulus()));
                derEncodableVector.add(new ASN1Integer(publicKey.getPublicExponent()));
                DERSequence derSequence = new DERSequence(derEncodableVector);
                byte[] publicKeyBytes = derSequence.getEncoded("DER");
                AlgorithmIdentifier algorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
                SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithm, publicKeyBytes);
                byte[] pubDerBytes = subjectPublicKeyInfo.getEncoded("DER");
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(pubDerBytes);
                byte[] pubSha256Bytes = messageDigest.digest();
                byte[] tarpubSha256Bytes = new byte[30];
                System.arraycopy(pubSha256Bytes, 0, tarpubSha256Bytes, 0, 30);
                String pubStr = com.zhangj.registry.auth.utils.StringUtils.trimRight(Base32.encode(tarpubSha256Bytes), '=');
                StringBuffer keyIdStr = new StringBuffer();
                for (int i = 0; i < pubStr.length() / 4; i++) {
                    keyIdStr.append(pubStr.subSequence(i * 4, i * 4 + 4)).append(":");
                }
                keyId = keyIdStr.deleteCharAt(keyIdStr.length() - 1).toString();
            }
        } catch (Exception e) {
            log.error("Docker auth generate the rsa public key id failure, cause: {}", Throwables.getStackTraceAsString(e));
        }
        return keyId;
    }
}
