package org.graindataterminal.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class HttpBaseTask extends AsyncTask<String, Integer, String> {
    protected static final String CHARSET_NAME = "UTF-8";
    protected static final int READ_TIMEOUT = 60000;
    protected static final int CONNECT_TIMEOUT = 60000;

    protected int responseCode = 0;
    protected Exception exception = null;

    protected final HttpURLConnection getConnection(URL url) throws IOException {
        HttpURLConnection conn;

        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn = httpsConn;
        } else
            conn = (HttpURLConnection) url.openConnection();

        return conn;
    }

    private void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final String getStringFromInputStream(InputStream stream) throws Exception {
        int n;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, CHARSET_NAME);
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);
        }

        reader.close();
        return writer.toString();
    }

    @Override
    protected final void onPostExecute(String result) {
        if (result == null)
            onError(responseCode, null);
        else if (exception != null)
            onError(responseCode, exception);
        else
            onSuccess(result);
    }

    protected abstract void onSuccess(String result);
    protected abstract void onError(int responseCode, Exception exception);
}
