package com.astromyllc.astroorb.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
@Slf4j

public class HttpClientClass  {
    private static final String TAG = "HTTP CALL";

    protected String doInBackground(String... params) {
        String data = "";

        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params[1]);
            wr.flush();
            wr.close();

            InputStream inputStream;

            int status = httpURLConnection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK)
                inputStream = httpURLConnection.getErrorStream();
            else
                inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            httpURLConnection.disconnect();
            };
        return data;
    }

}
