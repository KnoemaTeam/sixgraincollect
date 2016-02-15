package org.graindataterminal.network;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class HttpGetTask extends HttpBaseTask {
    @Override
    protected final String doInBackground(String... params) {
        String result = null;
        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = getConnection(url);

            try {
                if (this.isCancelled())
                    return null;

                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestProperty("Accept", "application/json; charset=utf-8");
                conn.setUseCaches(true);
                conn.setDoInput(true);
                conn.setDoOutput(false);
                conn.connect();

                responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    result = getStringFromInputStream(in);

                    in.close();
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.exception = e;
        }
        return result;
    }
}
