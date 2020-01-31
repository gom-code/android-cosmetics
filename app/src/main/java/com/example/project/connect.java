package com.example.project;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.project.app.Key;
import com.example.project.app.ServerInterface;

import org.json.JSONObject;

// 서버에 접속할
public class connect {


    /**
     *  이메일 체크
     */
    public synchronized String requestEmail(String email) {
        try {

            JSONObject jo = new JSONObject();
            jo.put(Key.email, email );
            Log.d("Email_jo", jo.toString());

            EmailTask task = new EmailTask(ServerInterface.exec_ssl, jo.toString());
            //return task.execute();     // AsyncTask실행
            return task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public synchronized String requestNickName(String nickName){
        try {

            JSONObject jo = new JSONObject();
            jo.put(Key.nickName, nickName );
            Log.d("Name_jo", jo.toString());

            EmailTask task = new EmailTask(ServerInterface.name, jo.toString());
            //return task.execute();     // AsyncTask실행
            return task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     *  Email 체크을 위한 서버 요청 AsyncTask
     */
    // AsyncTask클래스는 항상 Subclassing 해서 사용 해야 함.
    // 사용 자료형은
    // background 작업에 사용할 data의 자료형: 1번째 매개변수
    // background 작업 진행 표시를 위해 사용할 인자: 2번째 매개변수
    // background 작업의 결과를 표현할 자료형:  3번째 매개변수
    // 인자를 사용하지 않은 경우 Void Type 으로 지정.
    private class EmailTask extends AsyncTask<Void, Void, String> {
        private String uri;
        private String msg;

        public EmailTask(String uri, String msg) {
            this.uri = uri;
            this.msg = msg;
        }

        @Override
        // 작업 시작전에 UI 작업을 진행 한다.
        protected void onPreExecute() {
            super.onPreExecute();
        }
        // 작업을 진행 한다.
        protected String doInBackground(Void... arg0) {
            try {
                return Http.requestWithJson(uri, msg, ServerInterface.HTTP_POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        // 작업이 끝난 후 UI 작업을 진행 한다.
        protected void onPostExecute(String resBundle) {
            //mPresenter.loginPostExecute(resBundle);
        }
    }
}
