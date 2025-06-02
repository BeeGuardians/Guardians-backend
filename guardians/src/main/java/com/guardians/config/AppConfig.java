package com.guardians.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.File;
import java.security.KeyStore;

@Configuration
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public RestTemplate restTemplate() {
        String trustStorePath = "/usr/local/openjdk-17/lib/security/cacerts";
        String trustStorePassword = "changeit";

        File trustStoreFile = new File(trustStorePath);
        if (trustStoreFile.exists()) {
            logger.info("Using custom trustStore at {}", trustStorePath);
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                try (FileInputStream fis = new FileInputStream(trustStoreFile)) {
                    trustStore.load(fis, trustStorePassword.toCharArray());
                }

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);

                HttpComponentsClientHttpRequestFactory requestFactory =
                        new HttpComponentsClientHttpRequestFactory();
                requestFactory.setHttpClient(HttpClients.custom()
                        .setSSLContext(sslContext)
                        .build());

                return new RestTemplate(requestFactory);
            } catch (Exception e) {
                logger.error("Failed to configure custom trustStore: {}", e.getMessage(), e);
                return new RestTemplate(); // fallback
            }
        } else {
            logger.info("TrustStore not found at {}, using default RestTemplate", trustStorePath);
            return new RestTemplate();
        }
    }
}
