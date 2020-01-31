package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;

import com.example.project.base.BaseActivity;

public class Email extends BaseActivity {

    EditText etAddr, etTitle, etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        etAddr = (EditText) findViewById(R.id.et_addr);
        etTitle = (EditText) findViewById(R.id.et_title);
        etMessage = (EditText) findViewById(R.id.et_message);
//        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
//
//        actionBar.setTitle("문의하기");  //액션바 제목설정
//
//        actionBar.setDisplayHomeAsUpEnabled(true);   //업버튼 <- 만들기
//        actionBar.setHomeButtonEnabled(true);
    }

    public  void onClickSend(View v){
        sendEmail();
    }

    protected void sendEmail() {
        String to = etAddr.getText().toString();
        String subject = etTitle.getText().toString();
        String message = etMessage.getText().toString();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "이메일 클라이언트 선택하기 :"));
        onBackPressed();
    }
}



