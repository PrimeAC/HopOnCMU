package pt.ulisboa.tecnico.cmu.security;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.cmu.util.SerializationUtils;


public abstract class SecurityManager {

    public static PublicKey serverPubKey;
    public static KeyPair keyPair;
    public static SecretKey sessionKey;

    public static SecretKey getKeyFromString(String string){
        byte[] hashmd5 = hashMd5(string.getBytes());
        return new SecretKeySpec(hashmd5, 0, hashmd5.length, "AES");
    }

    public static byte[] hashMd5(byte[] content){
        byte[] result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(content);
            result = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void generateKeyPair(){
        try{
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            keyPair = kpg.generateKeyPair();

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public static SecretKey generateRandomAESKey(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // for example
            return keyGen.generateKey();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }


    public static Cipher getKeyCipher(Key key, int opmode, String algorithm){
        try{
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(opmode, key);
            return cipher;
        }catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyHash(byte[] hash, Serializable object){
        byte[] content = SerializationUtils.serializeObject(object);
        return Arrays.equals(hash, hashSHA256(content));
    }


    public static byte[] hashSHA256(byte[] content) {
        byte[] result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(content);
            result = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
