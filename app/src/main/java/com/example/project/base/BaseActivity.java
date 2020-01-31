package com.example.project.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.project.R;
import com.example.project.app.MyApp;
import com.google.android.gms.analytics.Tracker;

public class BaseActivity extends FragmentActivity {
    protected String TAG = "";
    @Override
    public void onBackPressed(){
        finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.getBoolean("isDialog", false)) {
            //setTheme(ThemeUtil.getSavedTheme());    //0.3~0.5초 지연해준다.
        }
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();

        if (Build.VERSION.SDK_INT >= 21) {   // 버전 체크
            // getWindow는 액티비티 정보를 가져오는데 사용한다.
            // ContextCompat 앱권한을 설정
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.default_cyan));  // 상태바 색/
        }
        //Tracker t = ((MyApp)getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
        //t.setScreenName(TAG);
        //t.send(new HitBuilders.AppViewBuilder().build());
    }
    @Override
    protected void onPause() {
       // MyApp.clearReferences(this);
        super.onPause();
    }

}
