package com.example.connection.TCP_Connection;
import com.brettywhite.cryptomessenger.KeyManager;
import com.example.connection.View.Connection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.PublicKey;


public class Encryption {
    byte[] IV;
    Connection connection;
    KeyManager km;
    AesCbcWithIntegrity.SecretKeys keys;
    public Encryption(Connection connection) {
        this.connection=connection;
        km= new KeyManager(this.connection);
        if (!km.keyExists()) {
           km.createKeys(false);
        }
        IV = new byte[16];
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
    public PublicKey getPublicKey(){
        return km.getPublicKey();
    }


    public void generateAES() {
        try {
            keys = AesCbcWithIntegrity.generateKey();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
    public String encryptAES(String plaintext,AesCbcWithIntegrity.SecretKeys secretKeys) {
        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = null;
        try {
            cipherTextIvMac = AesCbcWithIntegrity.encrypt(plaintext,secretKeys);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        //store or send to server
        return  cipherTextIvMac.toString();
    }
    
    public String decryptAES(String cipherText,AesCbcWithIntegrity.SecretKeys secretKeys)  {

        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(cipherText);
        String plainText=null;
        try {
            plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac,secretKeys);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return plainText;
    }
    public AesCbcWithIntegrity.SecretKeys convertStringToSecretKey(String key) throws InvalidKeyException {
        return  AesCbcWithIntegrity.keys(key);
    }

    public String convertSecretKeyToString(AesCbcWithIntegrity.SecretKeys keys) throws GeneralSecurityException {
        return AesCbcWithIntegrity.keyString(keys);
    }

    public AesCbcWithIntegrity.SecretKeys getSecretKey() {
        return keys;
    }
}
