package imd.br.com.borapagar.notice_tracker.helpers;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import imd.br.com.borapagar.notice_tracker.exception.ApiException;

/**
 * Este componente valida os certificados SSL das URL's que queremos conectar.
 * Assim, a conexão utilizando o Jsoup será possível.
 * Este componente confia em todos os certificados SSL, já que confiamos previamente nos sites que
 * iremos acessar.
 */
@Component
public class SSLHelper {

    static public Connection getConnection(String url){
        return Jsoup.connect(url).sslSocketFactory(SSLHelper.socketFactory());
    }

    static private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();

            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("Failed to create a SSL socket factory");
        } catch (KeyManagementException e) {
            throw new ApiException("Failed to create a SSL socket factory");
        }
    }
}
