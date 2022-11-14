package com.ConnectionProject.connection.TCP_Connection;
import com.brettywhite.cryptomessenger.KeyManager;
import com.ConnectionProject.connection.View.Connection;

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

    /**
     * Encrypt a message
     * @param msg        msg to encrypted
     * @param publicKey  publick key used to encrypt
     * @return the message encrypted
     */
    public String encrypt(String msg, PublicKey publicKey) {
        String result= km.encrypt(msg,publicKey);
        return result;
    }

    /**
     * Decrypt a message
     * @param msg message to be decrypted
     * @return message decrypted
     */
    public String decrypt(String msg) {
        String result = km.decrypt(msg);
        return result;
    }

    /**
     * Convert the public key to a string
     * @return the public key in string format
     */
    public String convertPublicKeyToString() {
 return km.convertPublicKeyToString(km.getPublicKey());
    }

    /**
     * Convert a string to a public key
     * @param key string to be converted
     * @return the key in key format
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public PublicKey convertStringToPublicKey(String key) throws GeneralSecurityException, IOException {
            return km.convertStringToPublicKey(key);
    }

    /**
     * @return the public key
     */
    public PublicKey getPublicKey(){
        return km.getPublicKey();
    }

    /**
     * Generate AES keys
     */
    public void generateAES() {
        try {
            keys = AesCbcWithIntegrity.generateKey();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypt a message with AES
     * @param plaintext  text to be encrypted
     * @param secretKeys key to use for encrypt the message
     * @return the text encrypted
     */
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

    /**
     * Decrypt a message with AES
     * @param cipherText crypt text
     * @param secretKeys key to use for decrypt the message
     * @return text decrypted
     */
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

    /**
     * Convert string to AES key
     * @param key key to be converted in key format
     * @return key in key format
     * @throws InvalidKeyException
     */
    public AesCbcWithIntegrity.SecretKeys convertStringToSecretKey(String key) throws InvalidKeyException {
        return  AesCbcWithIntegrity.keys(key);
    }

    /**
     * Convert AES key to string
     * @param keys key to be converted in string format
     * @return key in string format
     * @throws GeneralSecurityException
     */
    public String convertSecretKeyToString(AesCbcWithIntegrity.SecretKeys keys) throws GeneralSecurityException {
        return AesCbcWithIntegrity.keyString(keys);
    }

    /**
     * @return the AES key
     */
    public AesCbcWithIntegrity.SecretKeys getSecretKey() {
        return keys;
    }
}
