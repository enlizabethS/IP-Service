package testTask.ip;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CertificateScanner {
    public List<String> scanCertificate(String ipAddress) {
        List<String> domainNames = new ArrayList<>();
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial((TrustStrategy) (chain, authType) -> true)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLHostnameVerifier((s, sslSession) -> true)
                    .setSSLContext(sslContext)
                    .build();

            String url = "https://" + ipAddress;
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                SSLSession sslSession = (SSLSession) response.getEntity().getContentEncoding();

                if (sslSession != null) {
                    Certificate[] certificates = sslSession.getPeerCertificates();

                    for (Certificate cert : certificates) {
                        if (cert instanceof X509Certificate) {
                            X509Certificate x509Certificate = (X509Certificate) cert;
                            Collection<List<?>> subjectAlternativeNames = x509Certificate.getSubjectAlternativeNames();
                            if (subjectAlternativeNames != null) {
                                for (List<?> san : subjectAlternativeNames) {
                                    domainNames.add(san.toString());
                                }
                            }
                        }
                    }
                }
            }
            EntityUtils.consume(response.getEntity());
            return domainNames;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return domainNames;
    }
}