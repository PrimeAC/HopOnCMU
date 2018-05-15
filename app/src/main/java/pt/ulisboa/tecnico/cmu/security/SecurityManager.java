package pt.ulisboa.tecnico.cmu.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by afonsocaetano on 14/05/2018.
 */

public abstract class SecurityManager {

    //TODO keystore key generation
    public static KeyPair generateKeyPair(String alias){

        KeyPairGenerator kpg = null;

        try{
            kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            kpg.initialize(new KeyGenParameterSpec.Builder(
                    alias,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT |
            KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .build());
        }catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return kpg.generateKeyPair();
    }

    private static void importSecretKey(byte[] key, String alias){
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

    public static Cipher getCipher(String alias){
        try{
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey("AuthenticationKey", null);
            return Cipher.getInstance("AES/CBC/PKCS7Padding");
        }catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException |
                UnrecoverableKeyException | NoSuchPaddingException e){
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] sign(String alias, byte[] data){
        try{
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(alias, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                System.out.println("Not an instance of a PrivateKeyEntry");
                return null;
            }
            Signature s = Signature.getInstance("SHA256withECDSA");
            s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
            s.update(data);
            return s.sign();

        }catch (IOException | CertificateException | NoSuchAlgorithmException | InvalidKeyException
                | UnrecoverableEntryException | SignatureException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verify(String alias, byte[] signature, byte[] data){
        try{
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(alias, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                System.out.println("Not an instance of a PrivateKeyEntry");
                return false;
            }
            Signature s = Signature.getInstance("SHA256withECDSA");
            s.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
            s.update(data);
            return s.verify(signature);

        }catch(IOException | CertificateException | NoSuchAlgorithmException | InvalidKeyException
                | UnrecoverableEntryException | SignatureException | KeyStoreException e){
            e.printStackTrace();
            return false;
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

    //TODO static methods for cipher, hash, etc

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
