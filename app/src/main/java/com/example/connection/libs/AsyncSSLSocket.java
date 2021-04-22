package com.example.connection.libs;

import com.example.connection.libs.AsyncSocket;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;

public interface AsyncSSLSocket extends AsyncSocket {
    public X509Certificate[] getPeerCertificates();
    public SSLEngine getSSLEngine();
}
