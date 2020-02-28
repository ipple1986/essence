package spring.microservice.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RSAEncrptor {
    public static void main(String ...args){
        try {
            MessageDigest messageDigest =  MessageDigest.getInstance("RSA");
            //messageDigest.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
