package pt.ulisboa.tecnico.cmu.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.cmu.util.SerializationUtils;


public abstract class SecurityManager {

    public static PublicKey serverPubKey;

    public static void importSecretKey(String alias, byte[] key){
        try{
            SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            keyStore.setEntry(
                    alias,
                    new KeyStore.SecretKeyEntry(secretKey),
                    new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT )
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
        }catch (IOException | NoSuchAlgorithmException| CertificateException | KeyStoreException e){
            e.printStackTrace();
        }
    }

    public static void generateKeyPair(String alias){
        try{
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            kpg.initialize(new KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_DECRYPT)
                    .build());
            kpg.generateKeyPair();

        }catch(NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }
    }

    public static PublicKey getPublicKey(String alias){
        try{
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            Certificate certificate = (Certificate) keyStore.getCertificate(alias);
            return certificate.getPublicKey();
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Cipher getPrivateKeyCipher(String alias, int opmode){
        try{
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PrivateKey key = (PrivateKey) keyStore.getKey(alias, null);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(opmode, key);
            return cipher;
        }catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException |
                UnrecoverableKeyException | NoSuchPaddingException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public static Cipher getServerPublicKeyCipher(int opmode){
        try{
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(opmode, serverPubKey);
            return cipher;
        }catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public static Cipher getSecretKeyCipher(String alias, int opmode){
        try{
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(alias, null);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(opmode, key);
            return cipher;
        }catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException |
                UnrecoverableKeyException | NoSuchPaddingException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }


    public static void clearAlias(String alias) {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            ks.deleteEntry(alias);
        } catch(KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            e.printStackTrace();
        }
    }

    public static boolean keyExists(String alias){
        try{
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            return ks.containsAlias(alias);
        }catch (KeyStoreException e){
            e.printStackTrace();
            return false;
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
