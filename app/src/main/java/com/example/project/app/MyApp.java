package com.example.project.app;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

// Android 5.0 미만에서 multidex 지원해준다. 그냥 써보고 싶었음...  메서드의 총 개수를 65,536으로 제한했었다고...
public class MyApp extends MultiDexApplication {
    public static Context appInstance;  // 이동할때 마다
    public static Context mContext;     // 자동로그인을 위해
    private String email ="";
    private String name;
    public static MyApp app = new MyApp();

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;

    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String str){
        this.email = str;
    }
    public void setName(String str){
        this.name = str;
    }
    public String getName(){
        return this.name;
    }
    public enum TrackerName {
        APP_TRACKER,           // 앱 별로 트래킹
        GLOBAL_TRACKER,        // 모든 앱을 통틀어 트래킹
        ECOMMERCE_TRACKER,     // 아마 유료 결재 트래킹 개념 같음
    }
}
