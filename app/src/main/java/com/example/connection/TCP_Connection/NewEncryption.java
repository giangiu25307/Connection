package com.example.connection.TCP_Connection;

import com.brettywhite.cryptomessenger.KeyManager;
import com.example.connection.View.Connection;

import java.security.PublicKey;

public class NewEncryption {
    Connection connection;
    PublicKey userPubKey;
    String pubKeyString;
    String cipherText;
    KeyManager km = new KeyManager(connection);
    public NewEncryption(Connection connection) {
        this.connection=    connection;
        if (!km.keyExists()) {
            userPubKey = km.createKeys(false);
        }
        pubKeyString = km.convertPublicKeyToString(km.getPublicKey());
        System.out.println(pubKeyString);
    }
    public void printpublic(){
        pubKeyString = km.convertPublicKeyToString(km.getPublicKey());
        System.out.println(pubKeyString);
    }
    public void Encrypt(){
        cipherText= km.encrypt("ciao", null);

    }
    public String Decrypt(){
        return km.decrypt(cipherText);

    }

}
