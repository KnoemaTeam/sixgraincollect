package org.graindataterminal.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class HttpPostTask extends HttpBaseTask {
    @Override
    protected String doInBackground(String... params) {
        String result = null;
        try {

            URL url = new URL(params[0]);
            String data = params[1];
            HttpURLConnection conn = getConnection(url);

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
