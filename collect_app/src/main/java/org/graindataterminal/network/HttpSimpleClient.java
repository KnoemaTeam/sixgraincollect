package org.graindataterminal.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

public class HttpSimpleClient {
    protected static final String CHARSET_NAME = "UTF-8";
    protected static final int READ_TIMEOUT = 60000;
    protected static final int CONNECT_TIMEOUT = 60000;

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

    public String post(String url, String data) {
        String result = null;
        try {
            HttpURLConnection conn = getConnection(new URL(url));

            try {
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json; charset=utf-8");

                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                out.write(data.getBytes(CHARSET_NAME));
                out.flush();
                out.close();

                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    result = getStringFromInputStream(in);
                    in.close();
                }
            } finally {
                conn.disconnect();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public String postImage(String url, String imagePath) {
        String result = null;
        try {
            HttpURLConnection conn = getConnection(new URL(url));

            try {
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "image/jpeg");
                conn.setRequestProperty("Accept", "application/json; charset=utf-8");

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);

                byteArrayOutputStream.flush();
                byte[] data = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();

                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                out.write(data);
                out.flush();
                out.close();

                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    result = getStringFromInputStream(in);
                    in.close();
                }
            } finally {
                conn.disconnect();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
