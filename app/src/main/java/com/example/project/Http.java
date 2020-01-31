package com.example.project;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.project.app.Key;
import com.example.project.app.ServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Http{

    public static String requestWithJson(String uri, String json, String method) throws NetworkErrorException, IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        URL url = new URL(uri);

        // 해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다.
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // TimeOut 시간 (서버 접속시 연결 시간)
        conn.setConnectTimeout(5000);
        // TimeOut 시간 (Read시 연결 시간)
        conn.setReadTimeout(5000);
        // POST방식으로 요청한다.( 기본값은 GET )
        conn.setRequestMethod(method);
        // InputStream으로 서버로 부터 응답 헤더와 메시지를 읽어들이겠다는 옵션을 정의한다.
        conn.setDoInput(true);
        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션을 정의한다.
        conn.setDoOutput(true);
        // 요청 헤더를 정의한다.( 원래 Content-Length값을 넘겨주어야하는데 넘겨주지 않아도 되는것이 이상하다. )
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.connect();
        // 새로운 OutputStream에 요청할 OutputStream을 넣는다.
        OutputStream os = conn.getOutputStream();

        os.write(json.getBytes("UTF-8"));

        // 그리고 스트림의 버퍼를 비워준다.
        os.flush();
        // 스트림을 닫는다.
        os.close();

        int responseStatusCode = conn.getResponseCode();
        Log.d("Http", "response code - " + responseStatusCode);

        InputStream inputStream;
        if(responseStatusCode == HttpURLConnection.HTTP_OK) {
            inputStream = conn.getInputStream();
        }
        else{
            inputStream = conn.getErrorStream();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        Log.i("서버에서", sb.toString());
        reader.close();
        // 접속 해지
        conn.disconnect();

        return sb.toString().trim();
    }
}
