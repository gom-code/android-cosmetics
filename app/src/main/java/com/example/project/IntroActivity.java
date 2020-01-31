package com.example.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;
import com.example.project.base.BaseActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IntroActivity extends BaseActivity {
    protected ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        MyApp.appInstance =this;
        MyApp.mContext = this;
        //loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);
        checkLinkInternet();
    }

    public void checkLinkInternet() {
        String status = NetworkUtil.getConnectivityStatusString(this);  // 네트워크 확인
        if (!status.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(null)        // 제목 설정
                    .setMessage("인터넷 연결상태가 불안정합니다.\n사용중인 네트워크 상태를 확인하신 후 앱을 재실행 해주시기 바랍니다.")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("웹으로 가기", new DialogInterface.OnClickListener() {
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            Uri u = Uri.parse("https://ssoss8866.cafe24.com");
                            i.setData(u);
                            startActivity(i);
                            finish();
                        }
                    })
                    .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                        // 취소 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기
        } else {
            try {
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        requestCheckExistingMember();
                    }
                }, 2000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 자동 로그인 처리를 위한 스마트폰 내부 회원정보 존재 유무 확인
     */
    public void requestCheckExistingMember() {
        // mPresenter.checkReq();

        try {
            SharedPreferences pref = MyApp.mContext.getSharedPreferences("login", MODE_PRIVATE);
            String loginEmail=  pref.getString("email", null);
            String loginPwd = pref.getString("password", null);

            // 값을 가져와 i이고 pwd가 네이버 이면 자동적으로 액티비티 이동.
            if(loginEmail !=null && loginPwd != null) {
                Map<String, String> para = new HashMap<>();
                para.put("email", loginEmail);				// 이메일
                para.put("password", loginPwd);		// 비밀번호

                Log.i("dd", "sss".toString());
                JSONObject json = new JSONObject(para);

                final RequestQueue requestQueue = Volley.newRequestQueue(MyApp.appInstance);
                JsonObjectRequest postJsonRequset = new JsonObjectRequest
                        (Request.Method.POST, ServerInterface.emailLogin, json, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response", response.toString());
                                try {
                                    String date = response.toString();
                                    JSONObject jsonObject = new JSONObject(date);
                                    Log.i("dd", jsonObject.toString());
                                    if(jsonObject.getString("rsltCode").equals("1") || jsonObject.getString("rsltCode").equals("2")){
                                        SharedPreferences pref = MyApp.mContext.getSharedPreferences("login", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor prefEdit = pref.edit();
                                        // 기존 내역 삭제 하기
                                        prefEdit.remove("email");
                                        prefEdit.remove("password");
                                        prefEdit.commit();
                                        Intent intent = new Intent(MyApp.appInstance, LoginActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(0,0); //화면이동 애니메이션 효과
                                        finish();
                                    } else {
                                        SharedPreferences pref = MyApp.mContext.getSharedPreferences("login", Activity.MODE_PRIVATE);
                                        MyApp.app.setEmail(pref.getString("email", "0"));
                                        showMainActivity();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("error:", error.toString());
                            }
                        });
                requestQueue.add(postJsonRequset);

            }
            //id와 pwd가 null이면 Mainactivity가 보여짐.
            else if(loginEmail == null && loginPwd == null){
                Intent intent = new Intent(MyApp.appInstance, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0); //화면이동 애니메이션 효과
                this.finish();
            }
        } catch(Exception e){
            e.printStackTrace();
        }


    }

    public void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0); //화면이동 애니메이션 효과
        this.finish();
    }
}
