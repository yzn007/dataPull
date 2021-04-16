package com.springboot.common;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by yzn00 on 2021/4/16.
 */
public class IgnoreSSL {
    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new MyTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public static void ignoreSsl() throws Exception{
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        System.out.println("忽略HTTPS请求的SSL证书");
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
}


 class MyTM implements TrustManager, X509TrustManager {
     @Override
     public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
         return;
     }

     @Override
     public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        return;
     }

     @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
