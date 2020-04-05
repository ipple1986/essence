package encrypt;


import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Main {
    static byte[] file2Bytes(String filePath)  {
        try(FileInputStream fileInputStream  = new FileInputStream(new File(filePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fileInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }catch (IOException e){ }
        return null;
    }
    static String[] HEX = "0123456789ABCDEF".split("");
    static String sha256AsHex(String filePath) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = file2Bytes(filePath);
        messageDigest.update(bytes);
        bytes = messageDigest.digest();
        StringBuffer sb = new StringBuffer(256);
        for(byte b:bytes){
           sb.append(HEX[(b>>>4)&0x0F]).append(HEX[b&0x0F]);
        }
        return sb.toString();
    }
    static byte[] sha256AsBytes(String filePath) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = file2Bytes(filePath);
        messageDigest.update(bytes);
        bytes = messageDigest.digest();
        System.out.println(bytes.length);
        return bytes;
    }
    static String PrefixPath = "D:\\github\\jim\\study\\loadbalance\\src\\main\\resources\\";
    static String sign(String sha256) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        String keyStoreFilePath = PrefixPath.concat("TH_GW.jks");
        char[] storePass = "storepass".toCharArray();
        String alias = "TH_GW_0001_20200115";

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keyStoreFilePath),storePass);
        Key key = keyStore.getKey(alias,storePass);
        if(key instanceof PrivateKey){
            //Certificate cert = keyStore.getCertificate(alias);//get public key from certificate

           System.out.println(key);
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign((PrivateKey)key);
            signature.update(sha256.getBytes());
            byte[] bytes = signature.sign();
            System.out.println(signature);
            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }
    static String signBytes(byte[] sha256) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        String keyStoreFilePath = PrefixPath.concat("TH_GW.jks");
        char[] storePass = "storepass".toCharArray();
        String alias = "TH_GW_0001_20200115";

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keyStoreFilePath),storePass);
        Key key = keyStore.getKey(alias,storePass);
        if(key instanceof PrivateKey){
            //Certificate cert = keyStore.getCertificate(alias);//get public key from certificate
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key.getEncoded());  //私钥转换成pkcs8格式
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec); // 用key工厂对象生成私钥
            Signature signature = Signature.getInstance("MD5withRSA");  //  md5 RSA签名对象
            signature.initSign(privateKey);  //初始化签名
            signature.update(sha256);
            byte[] result = signature.sign();  //对消息进行签名
            System.out.println("签名结果："+result.length);



            System.out.println(key);
            Signature signature1 = Signature.getInstance("SHA256withRSA");
            signature1.initSign((PrivateKey)key);
            System.out.println(sha256.length);
            signature.update(sha256);
            byte[] bytes = signature1.sign();
            System.out.println(bytes.length);

            Certificate cert = keyStore.getCertificate(alias);
            PublicKey publicKey = cert.getPublicKey();
            Signature signature2 = Signature.getInstance("SHA256withRSA");
            signature2.initVerify(publicKey);
            signature2.update(sha256);
            System.out.println(signature2.verify(bytes)+" ===");

            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidKeySpecException {

        String sha256 = sha256AsHex(PrefixPath.concat("test.xml"));
        // E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855
        // EB6B374ECEE748685244D195662C26760C013870560386D37075C9DCC6F0D24F
        System.out.println(sha256);
        sha256 = "EB6B374ECEE748685244D195662C26760C013870560386D37075C9DCC6F0D24F";
        // keytool -genkeypair -alias TH_GW_0001_20200115 -KEYALG RSA -sigalg SHA512WithRSA -keysize 2048 -validity 1095 -storepass storepass -keystore TH_GW.jks -storetype PKCS12
        // 001-bulk-sign-test_20200115
        // Bulk Signature NonProd
        // Bank Name
        // Districts
        // Province
        // TH
        // yes
    System.out.println(signBytes(sha256AsBytes(PrefixPath.concat("test.xml"))).length());




    }
}
