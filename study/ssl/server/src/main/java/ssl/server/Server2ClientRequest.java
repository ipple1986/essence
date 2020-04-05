package ssl.server;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class Server2ClientRequest {
    public static void main(String[] args) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        char[] keyStorePass = "123456".toCharArray();
        keyStore.load(new FileInputStream(new File("D:\\github\\jim\\study\\ssl\\server\\src\\main\\resources\\server2.jks")),keyStorePass);

        SSLContext sslContext = SSLContext.getInstance("SSL");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("sunX509");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("sunX509");
        keyManagerFactory.init(keyStore,keyStorePass);
        trustManagerFactory.init(keyStore);
        sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(),new SecureRandom());

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient closeableHttpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        ClientHttpRequestFactory clientHttpRequestFactory =  new HttpComponentsClientHttpRequestFactory(closeableHttpClient);

        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        String response = restTemplate.getForObject("https://localhost:8200/client/service",String.class);
        System.out.println(response);
    }
}
