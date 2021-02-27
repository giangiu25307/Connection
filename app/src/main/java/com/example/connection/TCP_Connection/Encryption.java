package com.example.connection.TCP_Connection;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static java.security.KeyFactory.getInstance;

public class Encryption {

    public static PrivateKey privateKey;
    public static PublicKey publicKey;
    private SecretKey secretKey;
    byte[] IV;
    private Cipher cipher;
    private KeyStore ks;

    public Encryption() {

    }

    public void generateAsymmetricKeys() {
        try {
            //Generate asymmetric keys
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            keyPairGenerator.initialize(new KeyGenParameterSpec.Builder("key1", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setUserAuthenticationRequired(false)
                    .setKeySize(2048)
                    .build());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public void generateAES() {
        //Generate symmetric key
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            secretKey = keyGenerator.generateKey();
            IV = new byte[16];
            SecureRandom random;
            random = new SecureRandom();
            random.nextBytes(IV);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String msg, PublicKey publicKey) {
        String result = "";
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            result = Base64.encodeToString(cipher.doFinal(msg.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String decrypt(String msg) {
        String result = "";
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            result = new String(cipher.doFinal(Base64.decode(msg.replace("/n", ""), Base64.DEFAULT)), "UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String convertPublicKeyToString() {
        //try {
            return Base64.encodeToString(/*ks.getCertificate("key1").getPublicKey()*/publicKey.getEncoded(), Base64.DEFAULT);
        /*} catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }*/
    }

    public PublicKey convertStringToPublicKey(String key) throws GeneralSecurityException, IOException {
        byte[] data = Base64.decode(key.getBytes(), Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = getInstance("RSA");
        return fact.generatePublic(spec);
    }

    public SecretKey convertStringToSecretKey(String key) throws GeneralSecurityException, IOException {
        byte[] decodedKey = Base64.decode(key.getBytes(), Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public String convertSecretKeyToString(SecretKey secretKey) throws GeneralSecurityException, IOException {
        return Base64.encodeToString(secretKey.getEncoded(),Base64.DEFAULT);
    }

    public byte[] encryptAES(String plaintext, SecretKey secretKey) {
        try {
            cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(plaintext.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptAES(byte[] cipherText, SecretKey secretKey)  {
        try {
            cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(cipher.doFinal(cipherText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SecretKey getSecretKey(){
        return secretKey;
    }

    /*public void setSecretKey(SecretKey secretKey){
        this.secretKey=secretKey;
    }

    public void setPublic_PrivateKey() {
        try {
            ks = KeyStore.getInstance("AndroidKeyStore");

            ks.load(null);
            Enumeration<String> aliases = ks.aliases();
            KeyStore.Entry entry = ks.getEntry("key1", null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                return;
            }
            privateKey = (PrivateKey) ks.getKey("key1", null);
            publicKey = ks.getCertificate("key1").getPublicKey();
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | UnrecoverableEntryException e) {
            e.printStackTrace();
            return;
        }
    }*/
}
