package com.example.connection.TCP_Connection;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encryption {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher cipher;

    public Encryption() {
        try {
            /*KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            keyPairGenerator.initialize(new KeyGenParameterSpec.Builder("key1", KeyProperties.PURPOSE_SIGN)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(5 * 60)
                    .build());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            */
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
            /*Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());*/
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String msg,PublicKey publicKey) {
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
            result = new String(cipher.doFinal(Base64.decode(msg,Base64.DEFAULT)), "UTF8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public PublicKey getPublicKey(){
        return publicKey;
    }
}
