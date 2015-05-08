<?php
 
class Rsa
{
private static $PRIVATE_KEY = '-----BEGIN RSA PRIVATE KEY-----
MIICXQIBAAKBgQDHzqnXfyy+OWQhXMmY31BxyfV3wE7pYadj5RqtECGX9/abioF0
eE7VnQVGaPml2rTT/zCksBS7mzMPCMFEOj0zU7P1nkugVRAadp9EUEArf/DDt/yi
0Ryt7UOuL0SmYXksSL1CgEEd0cna373Kj40/PVUEbu6kvEh/qR6v3FJx1QIDAQAB
AoGBAJXXSk+ts+RELe9HInheoHl1N3zC32ruLRYhLkwzGL5a2rnyuPqbbqOLyfTv
rKjQx2NksRoQYrv+u9++dRGxi0qrQuEbgrDesX6BhPd+LoH4W9rk8wlAS5/PAddc
+NlC0/EUydnvdzsB1mlGUJa5Vw3wB1Hnoo2UmMnlDKMyg2HRAkEA6hxddMyVrqwS
UuXlsSVrm/eRO1TVpRivzBUV2PJdqaIcWSNEHprS/UgQdu4dIqDp3vRS033mSC9H
LPUDmoWthwJBANp9Nx70bJaV4j7qo6GteBP9GOW3apAvlDt3nAf7CUGdqYcpPbvh
SMMKM8S0cM5r/lMeyFKj8zuqOC8KBe93nMMCQQCPRgS78mi4mX4tVZ0YqLoOnZg4
I1cMzurnjw/r0YgYxKlss/SAdmghsfTBlEAobSoz1HrMDATesHByxxAJhT4JAkBq
gX18NnQ0l83ZE7I+XvxY6hjX6iglLzxYHL9P2JyzgrKgM4dnbbN1eCSyL6+JwHUS
eb/3IPqxzBFlRMKN8EXXAkBoy+HpCEn0bIIHWilcIrRpVZQmM5mUsnM/D4HUjelM
ZKkFnlEmr9Hmb75WoCJhoLL1PKNpnYJXOAJZnjmlJEJm
-----END RSA PRIVATE KEY-----';
    /**
    *返回对应的私钥
    */
    private static function getPrivateKey(){
    
        $privKey = self::$PRIVATE_KEY;
         
        return openssl_pkey_get_private($privKey);      
    }
 
    /**
     * 私钥加密
     */
    public static function privEncrypt($data)
    {
        if(!is_string($data)){
                return null;
        }           
        return openssl_private_encrypt($data,$encrypted,self::getPrivateKey())? base64_encode($encrypted) : null;
    }
    
    
    /**
     * 私钥解密
     */
    public static function privDecrypt($encrypted)
    {
        if(!is_string($encrypted)){
                return null;
        }
        return (openssl_private_decrypt(base64_decode($encrypted), $decrypted, self::getPrivateKey()))? $decrypted : null;
    }
}
 
?>