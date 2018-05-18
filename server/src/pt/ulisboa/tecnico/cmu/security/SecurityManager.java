package pt.ulisboa.tecnico.cmu.security;

import pt.ulisboa.tecnico.cmu.util.SerializationUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.*;
import java.util.Arrays;

/**
 * Created by afonsocaetano on 15/05/2018.
 */
public class SecurityManager {

	public static Cipher getCipher(Key key, int opmode, String transformation){
		try{
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(opmode, key);
			return cipher;
		}catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e){
			e.printStackTrace();
			return null;
		}
	}


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

	public static KeyPair generateKeyPair(){

		try{
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			return kpg.generateKeyPair();
		}catch (NoSuchAlgorithmException e){
			e.printStackTrace();
			return null;
		}
	}

	public static SecretKey generateRandomAESKey(){
		try{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256); // for example
			return keyGen.generateKey();
		}catch(NoSuchAlgorithmException e){
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
