package pt.ulisboa.tecnico.cmu.security;

import pt.ulisboa.tecnico.cmu.util.SerializationUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.util.Arrays;

/**
 * Created by afonsocaetano on 15/05/2018.
 */
public class SecurityManager {
	private static KeyPair kp;
	private static SecretKey ticketKey;
	private static SecretKey sessionKey;

	public static KeyPair getKp() {
		return kp;
	}

	public static SecretKey getTicketKey() {
		return ticketKey;
	}

	public static SecretKey getSessionKey() {
		return sessionKey;
	}

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

	public static void generateTicketKey(byte[] ticketCode){
		ticketKey = new SecretKeySpec(ticketCode, 0, ticketCode.length, "AES");

	}

	public static void generateSessionKey(){
		try {
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			sessionKey = kg.generateKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Load keystore into memory
	public static void generateKeyPair(){

		try{
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			kp = kpg.generateKeyPair();
			exportPublicKey(kp.getPublic());
		}catch (NoSuchAlgorithmException e){
			e.printStackTrace();
		}
	}

	private static void exportPublicKey(Key key){
		try{
			FileOutputStream out = new FileOutputStream("serverKey.pub");
			out.write(key.getEncoded());
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}

	}

	public static boolean verifyHash(byte[] hash, Serializable object){
		byte[] content = SerializationUtils.serializeObject(object);
		return Arrays.equals(hash, content);
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
