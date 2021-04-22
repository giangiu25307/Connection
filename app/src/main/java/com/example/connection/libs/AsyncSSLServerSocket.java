package com.example.connection.libs;

import com.example.connection.libs.AsyncServerSocket;

import java.security.PrivateKey;
import java.security.cert.Certificate;

public interface AsyncSSLServerSocket extends AsyncServerSocket {
    PrivateKey getPrivateKey();
    Certificate getCertificate();
}
