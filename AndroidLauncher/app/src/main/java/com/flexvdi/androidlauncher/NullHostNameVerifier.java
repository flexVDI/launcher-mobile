package com.flexvdi.androidlauncher;

import javax.net.ssl.HostnameVerifier ;
import javax.net.ssl.SSLSession;

public class NullHostNameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }

}