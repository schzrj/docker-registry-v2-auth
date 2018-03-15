# registry-auth
Docker Registry V2 Auth
#使用方式
. 生成密钥对
···
 openssl req -newkey rsa:4096 -nodes -sha256 -keyout auth.key -x509 -days 365 -out auth.crt
Generating a 4096 bit RSA private key
