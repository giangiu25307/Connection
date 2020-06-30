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
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static java.security.KeyFactory.getInstance;

public class Encryption {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher cipher;
    private KeyStore ks = null;
public Encryption(){
    init();//  System.out.println(encryption.decrypt(encryption.encrypt("ciao",encryption.convertStringToPublicKey(encryption.getStringPublicKey())))); SCRIVERE MESSAGGI AD UN ALTRA PERSONA ATTRAVERSO IL METODO convert
}
    public void init() {
        try {
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


    public String encrypt(String msg, PublicKey publicKey) {
        String result = "";
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
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

            result = new String(cipher.doFinal(Base64.decode(msg.replace("/n",""), Base64.DEFAULT)), "UTF8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getStringPublicKey() {
        try {
            return Base64.encodeToString(ks.getCertificate("key1").getPublicKey().getEncoded(), Base64.DEFAULT);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
    }
    public PublicKey convertStringToPublicKey(String key) throws GeneralSecurityException, IOException {
        byte[] data = Base64.decode(key.getBytes(), Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = getInstance("RSA");
        return fact.generatePublic(spec);

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
    }
}
