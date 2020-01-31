package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.app.Key;
import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;
import com.example.project.base.BaseActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class LoginActivity extends BaseActivity {
    //로그인 선택 버튼
    private ImageView emailLoginButton;
    private ImageView naverLoginButton;
    private ImageView facebookLoginButton;

    public static OAuthLogin mOAuthLoginModule;
    private OAuthLoginHandler mOAuthLoginHandler;

    private final int PERMISSION = 1;

    private LinearLayout loginLayout; //로그인 레이아웃
    private EditText loginEmail;    //이메일 입력칸
    private EditText loginPassword; //비밀번호 입력칸
    private Button loginSubmitButton; //로그인 버튼
    private Button loginCancelButton; //로그인 취소 버튼
    private TextView registButton;    //회원가입 버튼

    //로그인 선택 레이아웃들
    private int selectedNumber; //로그인 방식 선택
    private RelativeLayout email;
    private RelativeLayout naver;
    private RelativeLayout facebook;

    // 자동로그인
    private CheckBox cbAutoLogin;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private Toast toast;
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, PERMISSION);
        }
        MyApp.appInstance = this;

        findView();         // 뷰 설정
        initNaverLogin();   // 네이버

        //로그인 확인, 취소 리스너
        setLoginButton();

        //회원가입, 계정찾기 리스너
        setRegistButtonListener();

        //로그인 메뉴 선택 리스너
        emailLoginButton.setOnClickListener(new MyLoginClickListener());
        facebookLoginButton.setOnClickListener(new MyLoginClickListener());
        naverLoginButton.setOnClickListener(new MyLoginClickListener());
        initFacebookLogin();

    }
    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        MyApp.appInstance =this;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 사용자가 권한을 거절한 경우
        if ((grantResults.length == 0) || (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "권한이 없으면 정상적인 어플 실행이 불가능합니다.", Toast.LENGTH_SHORT).show();
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }

        // 이후는 권한이 승인된 경우
        switch (requestCode) {
            case PERMISSION:    // 권한 요청시 입력되었던 요청 코드
                break;
        }
    }
    private void findView() {
        loginLayout = findViewById(R.id.loginLayout);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginSubmitButton = findViewById(R.id.loginSubmit);
        loginCancelButton = findViewById(R.id.loginCancel);

        registButton = findViewById(R.id.regist);

        emailLoginButton = findViewById(R.id.emainLogin);
        facebookLoginButton = findViewById(R.id.facebookLogin);
        naverLoginButton = findViewById(R.id.naverLogin);

        email = findViewById(R.id.email);
        naver = findViewById(R.id.naver);
        facebook = findViewById(R.id.facebook);

        cbAutoLogin = findViewById(R.id.cbAutoLogin);
    }
    // 네이버 로그인 연동
    private synchronized void initNaverLogin() {

        ///////////////
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                LoginActivity.this
                , "30HzVop7yXaRcxiu0tTL"
                , "cdbSJE1qw7"
                , "테스트"
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );
		/*
		OAuthLogin.init() 메서드가 여러 번 실행돼도 기존에 저장된 접근 토큰(access token)과 갱신 토큰(refresh token)은 삭제되지 않습니다.
기존에 저장된 접근 토큰과 갱신 토큰을 삭제하려면 OAuthLogin.logout() 메서드나 OAuthLogin.logoutAndDeleteToken() 메서드를 호출합니다.
		 */
        mOAuthLoginHandler = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    String accessToken = mOAuthLoginModule.getAccessToken(MyApp.appInstance);
                    String refreshToken = mOAuthLoginModule.getRefreshToken(MyApp.appInstance);
                    long expiresAt = mOAuthLoginModule.getExpiresAt(MyApp.appInstance);
                    String tokenType = mOAuthLoginModule.getTokenType(MyApp.appInstance);
                    ProfileTask task = new ProfileTask();
                    task.execute(accessToken);
                } else {
                    String errorCode = mOAuthLoginModule.getLastErrorCode(MyApp.appInstance).getCode();
                    String errorDesc = mOAuthLoginModule.getLastErrorDesc(MyApp.appInstance);
                    Toast.makeText(MyApp.appInstance, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                }
            }

            ;
        };

    }
    class ProfileTask extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];// 네이버 로그인 접근 토큰;
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            try {
                String apiURL = "https://openapi.naver.com/v1/nid/me";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                br.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            //result 값은 JSONObject 형태로 넘어옵니다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
              //넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져오면 되는데요.
            // result 를 Log에 찍어보면 어떻게 가져와야할 지 감이 오실거에요.
                JSONObject object = new JSONObject(result);
                Log.d("ddddddddddddddddddddddd","결과 : "+result);

                //System.out.println("--->>" + object.toString());
                if (object.getString("resultcode").equals("00")) {
                    JSONObject jsonObject = new JSONObject(object.getString("response"));

                    String id = jsonObject.getString("id");
                    Log.d("jsonObject", jsonObject.toString());

                    connect taker = new connect();

                    String decodeStr = taker.requestEmail(jsonObject.getString("id"));

                    Log.i("dar", "" + decodeStr);
                    //가입 정보가 없을 경우
                    if (decodeStr.equals(Key.accept)) {
                        Intent intent = new Intent(LoginActivity.this, RegistFromNaver.class);
                        intent.putExtra("id", jsonObject.getString("id"));
                        //intent.putExtra("email", jsonObject.getString("email"));
                        //intent.putExtra("nickname", jsonObject.getString("nickname"));
                        intent.putExtra("email", object.getString("id"));
                        intent.putExtra("nickname", object.getString("id"));
                        startActivity(intent);

                    } else { //예전에 네이버 로그인으로 회원가입하였을 경우
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //intent.putExtra("result", result);
                        //System.out.println("--->>login" + result);
                        if (cbAutoLogin.isChecked()) {
                            SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor prefEdit = pref.edit();

                            prefEdit.putBoolean("flag", true);
                            prefEdit.putString("email", jsonObject.getString("email"));
                            prefEdit.putString("password", jsonObject.getString("id"));
                            prefEdit.commit();
                        } else {
                            SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor prefEdit = pref.edit();

                            prefEdit.putBoolean("flag", false);
                            prefEdit.commit();
                        }

                        startActivity(intent);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 회원가입
    private void setRegistButtonListener() {

        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registActivity = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(registActivity);
            }
        });
    }
    private class MyLoginClickListener implements View.OnClickListener {

        @Override
        public synchronized void onClick(View v) {

            selectedNumber = Integer.parseInt((String) v.getTag()); //0이메일 1네이버 2페이스북

            if (selectedNumber == 0) {
                //로그인 레이아웃이 화면에 없으면, 표시
                if (loginLayout.getVisibility() == View.GONE) {  // View.GONE 뷰가 숨겨졌는지
                    loginLayout.setVisibility(View.VISIBLE);     // View.VISIBLE 뷰 보이기
                    email.setVisibility(View.GONE);
                    naver.setVisibility(View.GONE);
                    facebook.setVisibility(View.GONE);

                }
            } else if (selectedNumber == 1) {
                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);

            } else {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
            }

        }
    }
    private void setLoginButton() {

        loginCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 레이아웃이 화면에 있으면, 없애기
                if (loginLayout.getVisibility() == View.VISIBLE) {  // 뷰가 보이고 있다면
                    loginLayout.setVisibility(View.GONE); // 뷰 숨기기
                    email.setVisibility(View.VISIBLE);
                    naver.setVisibility(View.VISIBLE);
                    facebook.setVisibility(View.VISIBLE);
                }
            }
        });
        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
                if (!Network.equals("")) {    //  네트워크 확인
                    Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
                    return;
                }

                // 하드웨어 키보드는 스마트폰에서 물리적으로 제공하는 키보드로 우리의 앱에서 창을 제어하는 것과 관련이 기능을 제공하는 클래스
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                // showSoftInput(View view, int flags): 키보드 보임
                // hideSoftInputFromWindow(IBinder windowToken, int flags): 키보드 숨김


                /*
                선택된 로그인 방식을 검사하여 로그인 구현
                 */
                switch (selectedNumber) {
                    case 0: //이메일
                        //todo 키보드가 안내려가면 메뉴가 깨져서 나오는 현상 발생하여 임시방편으로 딜레이 줌
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                emailLogin();
                            }
                        }, 300);
                        break;
                    case 1: //네이버
                        break;
                    case 2: //페이스북
                        break;
                }
            }
        });
    }
    private void emailLogin() {
        String _email = loginEmail.getText().toString();
        String _password = loginPassword.getText().toString();

        Map<String, String> para = new HashMap<>();
        para.put("email", _email);				// 이메일
        para.put("password", _password);		// 비밀번호

        JSONObject json = new JSONObject(para);

        final ProgressDialog dialog = ProgressDialog.show(MyApp.appInstance,"서버와 접속중.. 잠시만 기달려주세요","로그인중...",false);

        final RequestQueue requestQueue = Volley.newRequestQueue(MyApp.appInstance);
        JsonObjectRequest postJsonRequset = new JsonObjectRequest
                (Request.Method.POST, ServerInterface.emailLogin, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d("response", response.toString());
                        try {
                            String date = response.toString();
                            JSONObject jsonObject = new JSONObject(date);
                            if(jsonObject.getString("rsltCode").equals("1")){
                                Toast.makeText(getApplicationContext(), "이메일을 확인해주세요.", Toast.LENGTH_LONG).show();
                            } else if (jsonObject.getString("rsltCode").equals("2")){
                                Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다..", Toast.LENGTH_LONG).show();
                            } else {

                                    Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);

                                    if (cbAutoLogin.isChecked()) {
                                        SharedPreferences pref = MyApp.mContext.getSharedPreferences("login", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor prefEdit = pref.edit();
                                        // 기존 내역 삭제 하기
                                        prefEdit.remove("email");
                                        prefEdit.remove("password");
                                        prefEdit.commit();

                                        //prefEdit.putBoolean("flag", true);
                                        prefEdit.putString("email", loginEmail.getText().toString());
                                        prefEdit.putString("password", loginPassword.getText().toString());
                                        prefEdit.commit();
                                    }
                                    MyApp.app.setEmail(loginEmail.getText().toString());
                                    startActivity(mainActivity);
                                    finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                Log.d("error:", error.toString());
                            }
                    });
            requestQueue.add(postJsonRequset);

    }
    // 페이스북 연동
    private synchronized void initFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        System.out.println("--->>success");
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        //System.out.println("--->>cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.println("--->>error");
                    }
                });

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        Log.d("ddddddddddddddddddddddd","결과 : ");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {//로그인 성공시 호출되는 메소드
                // App code
                System.out.println("--->>success1");
                final String id = loginResult.getAccessToken().getUserId();
                //loginResult.getAccessToken() 정보를 가지고 유저 정보를 가져올수 있습니다.
                GraphRequest request =GraphRequest.newMeRequest(loginResult.getAccessToken() ,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
//									System.out.println("--->>"+object.toString());
//									System.out.println("--->>name:"+object.getString("name"));
//									System.out.println("--->>email:"+object.getString("email"));
//									System.out.println("--->>id:"+id);

                                    connect taker = new connect();

                                    String decodeStr = taker.requestEmail(object.getString("email"));

                                    Log.i("dar", "" + decodeStr);

                                    //가입 정보가 없을 경우
                                    if (decodeStr.equals(Key.accept)) {
                                        Intent intent = new Intent(LoginActivity.this, RegistFromNaver.class);
                                        intent.putExtra("id", object.getString("id"));
                                        intent.putExtra("email", object.getString("email"));
                                        intent.putExtra("nickname", object.getString("name"));
                                        startActivity(intent);

                                    } else { //예전에 네이버 로그인으로 회원가입하였을 경우
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("result", decodeStr);
                                        //System.out.println("--->>login" + result);
                                        if (cbAutoLogin.isChecked()) {
                                            SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                                            SharedPreferences.Editor prefEdit = pref.edit();

                                            prefEdit.putBoolean("flag", true);
                                            prefEdit.putString("email", object.getString("email"));
                                            prefEdit.putString("password",object.getString("id"));
                                            prefEdit.commit();
                                        } else {
                                            SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                                            SharedPreferences.Editor prefEdit = pref.edit();

                                            prefEdit.putBoolean("flag", false);
                                            prefEdit.commit();
                                        }

                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Log.d("aaaaaaaaaaaaaaaaaaaa","결과 : ");

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                //System.out.println("--->>cancel1");

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                System.out.println("--->>error1");

            }
        });
    }
    // 뒤로 가기 버튼 눌렀을때 종료하기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this,
                    "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            this.finish();
            toast.cancel();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
