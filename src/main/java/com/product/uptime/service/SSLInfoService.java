package com.product.uptime.service;

import com.product.uptime.entity.SSLInfo;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
                    X509Certificate x509Certificate = (X509Certificate) cert;
                    Date expiryDate = x509Certificate.getNotAfter();
                    Instant expiryInstant = expiryDate.toInstant();
                    Instant reminderDate = expiryInstant.minus(30, ChronoUnit.DAYS);
                    Instant currentDate = Instant.now();
                    SSLInfo sslInfo = new SSLInfo();
                    sslInfo.setCertificateExpiry(expiryInstant);
                    sslInfo.setRemindingDate(reminderDate);
                    sslInfo.setCertificateIssuer(x509Certificate.getIssuerX500Principal().getName());

                    if (currentDate.isBefore(reminderDate)) {
                        sslInfo.setStatus("VALID");
                    } else if (currentDate.isBefore(expiryInstant)) {
                        sslInfo.setStatus("EXPIRING SOON");
                    } else {
                        sslInfo.setStatus("EXPIRED");
                    }

                    conn.disconnect();
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
