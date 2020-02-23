package cn.jim.shua.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class HttpRequestSupport {

    protected static String getRequest(String url,String cookie){
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection)new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpURLConnection.setConnectTimeout(4000);
        httpURLConnection.setReadTimeout(8000);

        try {
            httpURLConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        httpURLConnection.addRequestProperty("Cookie","PHPSESSID=".concat(cookie));
        httpURLConnection.addRequestProperty("Accept","*/*");
        httpURLConnection.addRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 9; SNE-AL00 Build/HUAWEISNE-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/72.0.3626.121 Mobile Safari/537.36");
        httpURLConnection.addRequestProperty("Connection","Keep-Alive");
        httpURLConnection.addRequestProperty("Charset","UTF-8");
        httpURLConnection.addRequestProperty("Accept-Encoding","gzip");
        try {
            httpURLConnection.connect();
            if(200 == httpURLConnection.getResponseCode()){
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                }
                return sbf.toString();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpURLConnection.disconnect();
        }
        return "";
    }
}
