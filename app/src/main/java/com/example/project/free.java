package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.CustomAdapter;
import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class free extends Activity implements View.OnClickListener {
    ListView m_ListView;
    CustomAdapter m_Adapter;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        MyApp.appInstance = this;
        // 커스텀 어댑터 생성
        m_Adapter = new CustomAdapter();

        // Xml에서 추가한 ListView 연결
        m_ListView = (ListView) findViewById(R.id.listView1);

        // ListView에 어댑터 연결
        m_ListView.setAdapter((ListAdapter) m_Adapter);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분", Locale.KOREA);
        Date date = new Date();
        String currentDate = formatter.format(date);

        m_Adapter.add( currentDate,2);
        m_Adapter.add("안녕하세요. 질문을 해주세요^ ^", 0);

        // StrictMode는 개발자가 실수하는 것을 감지하고 해결할 수 있도록 돕는 일종의 개발 툴
        // - 메인 스레드에서 디스크 접근, 네트워크 접근 등 비효율적 작업을 하려는 것을 감지하여
        //   프로그램이 부드럽게 작동하도록 돕고 빠른 응답을 갖도록 함, 즉  Android Not Responding 방지에 도움
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        findViewById(R.id.button).setOnClickListener(this);

    }
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            m_Adapter.add(msg.obj.toString(), 0);
            m_Adapter.notifyDataSetChanged(); //listview 갱신
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.button) {
            EditText editText = (EditText) findViewById(R.id.editText);
            String input = editText.getText().toString();
            editText.setText("");
            m_Adapter.add(input, 1);      // listview 추가.
            m_Adapter.notifyDataSetChanged();   //listview 갱신.

            String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
            if(!Network.equals("")){	//  네트워크 확인
                Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> para = new HashMap<>();
            para.put("chat", input);				//

            JSONObject json = new JSONObject(para);
            final RequestQueue requestQueue = Volley.newRequestQueue(MyApp.appInstance);

            JsonObjectRequest postJsonRequset = new JsonObjectRequest
                    (Request.Method.POST, ServerInterface.dd, json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", response.toString());
                            try {
                                String date = response.toString();
                                JSONObject jsonObject = new JSONObject(date);

                                myHandler = new MyHandler();
                                new Thread() {
                                    public void run(){
                                            try {
                                                // InputStream의 값을 읽어와서 data에 저장
                                                String data = jsonObject.getString("chat");
                                                // Message 객체를 생성, 핸들러에 정보를 보낼 땐 이 메세지 객체를 이용
                                                Message msg = myHandler.obtainMessage();
                                                msg.obj = data;
                                                myHandler.sendMessage(msg);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                    }
                                }.start();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("error:", error.toString());
                                }
                            });
            requestQueue.add(postJsonRequset);
        }
    }
    @Override
    public  void onBackPressed(){	// 뒤로 가기
        super.onBackPressed();
        onBackPressed();
    }
}