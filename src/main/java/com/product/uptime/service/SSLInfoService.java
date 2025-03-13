package com.product.uptime.service;

import com.product.uptime.entity.SSLInfo;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class SSLInfoService {

    public SSLInfo getSSLInfo(String url) {
        try {
            URL siteURL = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) siteURL.openConnection();
            conn.connect();
            Certificate[] certs = conn.getServerCertificates();

            for (Certificate cert : certs) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    Date expiryDate = x509Cert.getNotAfter();
                    conn.disconnect();
                    SSLInfo sslInfo = new SSLInfo();
                    sslInfo.setCertificateExpiry(expiryDate.toInstant());
                    return sslInfo;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch SSL info for: " + url);
            e.printStackTrace();
        }
        return null;
    }
}
