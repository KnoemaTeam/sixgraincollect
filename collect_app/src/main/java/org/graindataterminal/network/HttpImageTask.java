package org.graindataterminal.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class HttpImageTask extends HttpBaseTask {

    @Override
    protected String doInBackground(String... params) {
        String result = null;

        try {
            URL url = new URL(params[0]);
            String imagePath = params[1];

            if (TextUtils.isEmpty(imagePath))
                return null;

            HttpURLConnection conn = getConnection(url);

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

                responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    result = getStringFromInputStream(in);
                    in.close();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return result;
    }
}
