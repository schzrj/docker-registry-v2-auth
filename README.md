# registry-auth
Docker Registry V2 Auth Server
基于Docker Registry V2的token认证方式实现，可与java项目进行整合，通过实现接口，与已有项目的用户权限模块进行对接，实现私有docker镜像仓库的认证复用已有应用的用户权限数据。

# Docker Registry V2 token 认证介绍
docker官方文档地址：https://docs.docker.com/registry/spec/auth/token/

#使用方式
1. 生成密钥对 
```
 openssl req -newkey rsa:4096 -nodes -sha256 -keyout auth.key -x509 -days 365 -out auth.crt

```
2. 实现AccountService、ProjectService接口

```
/实现AccountService接口
public class AccountServiceImpl implements AccountService {
  
}
//实现ProjectService接口
public class ProjectServiceImpl implements ProjectService {
 
}

```
3. 添加配置
   
   在项目资源目录添加认证服务器的配置数据
 
 ```
components:
  auth:
    service: Registry auth server
    issuer: Registry Auth service
    #token过期时间（minute）
    tokenExpire: 180
    publicKeyPath: keyfile/auth.crt
    privateKeyPath: keyfile/auth.key
 ```
4. 添加密钥文件
 
 将生成的密钥文件放在项目resources目录，文件路径需与配置中的文件路径保持一致
 
5. 配置镜像仓库的token认证服务器配置

```
auth:
  token:
    realm: ${auth_server_host}/api/registry/auth
    service: Registry auth server
    issuer: Registry Auth service
    rootcertbundle: /etc/registry/auth.crt
```
