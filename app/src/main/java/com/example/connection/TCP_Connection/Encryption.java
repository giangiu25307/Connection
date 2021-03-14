package com.example.connection.TCP_Connection;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.brettywhite.cryptomessenger.KeyManager;
import com.example.connection.View.Connection;

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
    Connection connection;
    PublicKey userPubKey;
    String pubKeyString;
    String cipherText;
    KeyManager km;
    public Encryption(Connection connection) {
        this.connection=connection;
        km= new KeyManager(this.connection);
        if (!km.keyExists()) {
           km.createKeys(false);
        }

        IV = new byte[16];
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
        String result= km.encrypt(msg,publicKey);
        return result;
    }

    public String decrypt(String msg) {
        String result = km.decrypt(msg);
        return result;
    }

    public String convertPublicKeyToString() {
 return km.convertPublicKeyToString(km.getPublicKey());
    }

    public PublicKey convertStringToPublicKey(String key) throws GeneralSecurityException, IOException {
            return km.convertStringToPublicKey(key);
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
    public PublicKey getPublicKey(){
        return km.getPublicKey();
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
