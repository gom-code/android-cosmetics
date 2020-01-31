package com.example.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;
import com.example.project.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class activityProduct extends BaseActivity {
    private static final int GET_GALLERY_IMAGE = 0;
    String fileName;
    String encodedString;
    // XML 선언
    Button bt_inbody, bt_inbody2;                     // 사진추가 / 저장하기
    ImageView imageViews1;                             // 이미지 뷰
    EditText text1, text2, text3, text4;             // 제품명 / 종류 / 판매처 / 설명

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_schedule_log);
        MyApp.appInstance =this;
        bt_inbody = findViewById(R.id.bt_inbody);
        imageViews1 = findViewById(R.id.imageViews1);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        bt_inbody2 = findViewById(R.id.bt_inbody2);

        clickEvent();
    }
    private void clickEvent(){
        bt_inbody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리 이동
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] oItems = {"클렌징폼", "클렌징 오일", "클렌징 밀크"};

                AlertDialog.Builder oDialog = new AlertDialog.Builder(MyApp.appInstance, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                oDialog.setTitle("종류를 선택하세요").setItems(oItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        text2.setText(oItems[which]);
                    }
                }).setCancelable(false).show();
            }
        });
        bt_inbody2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
                if(!Network.equals("")){	//  네트워크 확인
                    Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> para = new HashMap<>();
                para.put("email", MyApp.app.getEmail());               // 이메일 등록한 놈
                para.put("img", encodedString);				        // 이미지
                para.put("text1",text1.getText().toString());         // 제품명
                para.put("text2",text2.getText().toString());         // 종류
                para.put("text3",text3.getText().toString());         // 판매처
                para.put("text4",text4.getText().toString());         // 설명

                JSONObject json = new JSONObject(para);
                ProgressDialog dialog = ProgressDialog.show(MyApp.appInstance,"서버에 접속중.. 잠시만 기달려주세요","게시물 등록중...",false);
                final RequestQueue requestQueue = Volley.newRequestQueue(MyApp.appInstance);

                JsonObjectRequest postJsonRequset = new JsonObjectRequest
                        (Request.Method.POST, ServerInterface.postsave, json, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dialog.dismiss();
                                Log.d("response", response.toString());
                                try {
                                    String date = response.toString();
                                    JSONObject jsonObject = new JSONObject(date);
                                    if(jsonObject.getString("rsltCode").equals("true")){
                                        Toast.makeText(MyApp.appInstance, "게시물 등록 성공!", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    } else {
                                        Toast.makeText(MyApp.appInstance, "게시물 등록 실패.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        dialog.dismiss();
                                        Log.d("error:", error.toString());
                                    }
                                });
                requestQueue.add(postJsonRequset);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            String fileNameSegments[] = picturePath.split("/");
            fileName = fileNameSegments[fileNameSegments.length - 1];

            Bitmap myImg = BitmapFactory.decodeFile(picturePath);
            imageViews1.setImageBitmap(myImg);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            encodedString = Base64.encodeToString(byte_arr, 0);
        }
    }
}
