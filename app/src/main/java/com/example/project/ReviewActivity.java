package com.example.project;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;
import com.example.project.base.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ReviewActivity extends BaseActivity {
    TextView txt_likeCount;   /* Declare xml components */
    ImageView img_detailPerfImg;
    TextView txt_detailPerfBrand, txt_detailPerfName, txt_detailPerfEngName, txt_detailPerfAddition;
    RelativeLayout btn_priceCompare, btn_likeThis, btn_askRevision;
    TextView txt_perfType, txt_perfCountry, txt_perfIntro, txt_flavors_info, txt_components;

    String perfCapacity, perfType, perfImage, perfIntro, perfLike; //  데이터 임시 저장
    int tempLike;
    String vLike, getString, getkey;

    /* 네트워크 통신 */
    perfServer task;
    int phpChk; // 0: 향수추천, 1: 향수보관, 2: 추천취소, 3: 보관취소


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review2);

        /* Init xml widgets */
        txt_likeCount = (TextView) findViewById(R.id.txt_likeCount);
        img_detailPerfImg = (ImageView) findViewById(R.id.img_detailPerfImg);
        txt_detailPerfBrand = (TextView) findViewById(R.id.txt_detailPerfBrand);
        txt_detailPerfName = (TextView) findViewById(R.id.txt_detailPerfName);
        btn_priceCompare = (RelativeLayout) findViewById(R.id.btn_priceCompare);
        btn_likeThis = (RelativeLayout) findViewById(R.id.btn_likeThis);
        btn_askRevision = (RelativeLayout) findViewById(R.id.btn_askRevision);
//        txt_perfType = (TextView) findViewById(R.id.txt_perfType);
//        txt_perfCountry = (TextView) findViewById(R.id.txt_perfCountry);
//        txt_perfIntro = (TextView) findViewById(R.id.txt_perfIntro);
//        txt_flavors_info = (TextView) findViewById(R.id.txt_flavors_info);
//        txt_components = (TextView) findViewById(R.id.txt_components);

        getString = getIntent().getStringExtra("itemName");
        getkey = getIntent().getStringExtra("key");

        /* 화면에 데이터 적용하기 */
        putPerfumeInfo();

        /* 가격비교 버튼 클릭 */
        btn_priceCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://m.shopping.naver.com/search/all.nhn?query=" + getString);   // 네이버 쇼핑몰
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });
        btn_askRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApp.appInstance, ReviewActivity2.class);
                intent.putExtra("itemName", (String) getString);    // 이름
                intent.putExtra("key", getkey);
                startActivity(intent);
            }
        });
        /* 추천하기 버튼 클릭 */
        btn_likeThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vLike.equals("1")) { // 이미 추천이 되어있는 상태
                    AlertDialog dialog = deleteLike();
                    dialog.show();
                } else {
                    /* 추천수 증가시키기 */
                    tempLike += 1;
                    /* 서버 연결 */
                    phpChk = 0; // 추천
                    task = new perfServer();
                    task.execute(ServerInterface.Like);
                }

            }

            private AlertDialog deleteLike() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity.this);
                builder.setTitle("추천취소");
                builder.setMessage("추천을 취소하시겠습니까?");

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempLike -= 1;
                        phpChk = 2;
                        task = new perfServer();
                        task.execute(ServerInterface.DeleteLike);
                    }
                });

                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                return dialog;
            }
        });

    }
    private void putPerfumeInfo() {
        String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
        if (!Network.equals("")) {    //  네트워크 확인
            Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
            return;
        }
        // 이미지 가져오기
        Glide.with(ReviewActivity.this).load(ServerInterface.domain+"/img/"+ getString +".png").override(100, 100).into(img_detailPerfImg);

        try{
            if(getkey.equals("0")) {
                JSON taker = new JSON();
                String json = taker.execute("CleansingFoam", getString).get();
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jObject = jsonArray.getJSONObject(0);
                txt_detailPerfBrand.setText((jObject.getString("companyName").equals("null"))? "":jObject.getString("companyName"));
                txt_detailPerfName.setText(getString);

                JSON taker1 = new JSON();
                String json1 = taker1.execute("likeView", MyApp.app.getEmail(), getString).get();
                JSONArray jsonArray1 = new JSONArray(json1);
                JSONObject jObject1 = jsonArray1.getJSONObject(0);
                txt_likeCount.setText("추천 " + jObject1.getString("product"));
                vLike = jObject1.getString("user");
            } else if(getkey.equals("1")) {
                JSON taker = new JSON();
                String json = taker.execute("cleansingOil", getString).get();
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jObject = jsonArray.getJSONObject(0);
                txt_detailPerfBrand.setText((jObject.getString("companyName").equals("null"))? "":jObject.getString("companyName"));
                txt_detailPerfName.setText(getString);

                JSON taker1 = new JSON();
                String json1 = taker1.execute("likeView", MyApp.app.getEmail(), getString).get();
                JSONArray jsonArray1 = new JSONArray(json1);
                JSONObject jObject1 = jsonArray1.getJSONObject(0);
                txt_likeCount.setText("추천 " + jObject1.getString("product"));
                vLike = jObject1.getString("user");
            }else if(getkey.equals("2")) {
                JSON taker = new JSON();
                String json = taker.execute("CleansingMilk", getString).get();
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jObject = jsonArray.getJSONObject(0);
                txt_detailPerfBrand.setText((jObject.getString("companyName").equals("null"))? "":jObject.getString("companyName"));
                txt_detailPerfName.setText(getString);

                JSON taker1 = new JSON();
                String json1 = taker1.execute("likeView", MyApp.app.getEmail(), getString).get();
                JSONArray jsonArray1 = new JSONArray(json1);
                JSONObject jObject1 = jsonArray1.getJSONObject(0);
                txt_likeCount.setText("추천 " + jObject1.getString("product"));
                vLike = jObject1.getString("user");
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /* 이미 등록된 경우에는 선택된 버튼으로 변경 */
        if (vLike.equals("1"))
            btn_likeThis.setBackgroundResource(R.drawable.sbtn_samplechecked);

        /* 레이아웃 가로 크기 */
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int width = dm.widthPixels;

        /* // 레이아웃에 불러온 정보 적용하기
        //fid_txtDetail.setText(tempDetail);
        txt_flavors_info.setText(TextViewHelper.shrinkWithWordUnit(txt_flavors_info, perfFla, width - 150));
        //txt_flavors_info.setText(perfFla);
        //txt_perfIntro.setText(perfIntro);
        txt_perfIntro.setText(TextViewHelper.shrinkWithWordUnit(txt_perfIntro, perfIntro, width - 150));*/
    }
    private class perfServer extends AsyncTask<String, Integer, String> {
        ProgressDialog asyncDialog = new ProgressDialog(ReviewActivity.this);

        /* 연결 전 */
        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("..로딩중..");
            asyncDialog.show();
            super.onPreExecute();
        }

        /* 연결 시 */
        @Override
        protected String doInBackground(String... urls) {
            try {
                if (phpChk == 0 || phpChk == 2) { // 추천
                    JSONObject jo = new JSONObject();
                    jo.put("product", getString);
                    jo.put("user", MyApp.app.getEmail());
                    return Http.requestWithJson(urls[0], jo.toString(), ServerInterface.HTTP_POST);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String stri = "0";
            return stri;
        }

        protected void onPostExecute(String str) {
            asyncDialog.dismiss();

            if (phpChk == 0) { // 추천
                if (str.trim().equals("100")) {
                    Toast.makeText(ReviewActivity.this, "추천되었습니다", Toast.LENGTH_SHORT).show();
                    txt_likeCount.setText("추천 " + String.valueOf(tempLike));
                    btn_likeThis.setBackgroundResource(R.drawable.sbtn_samplechecked);
                    vLike = "1";

                } else {
                    Toast.makeText(ReviewActivity.this, "실패하였습니다", Toast.LENGTH_SHORT).show();
                }
            } else if (phpChk == 2) {
                if (str.trim().equals("100")) {
                    Toast.makeText(ReviewActivity.this, "추천을 취소하였습니다", Toast.LENGTH_SHORT).show();
                    txt_likeCount.setText("추천 " + String.valueOf(tempLike));
                    btn_likeThis.setBackgroundResource(0);
                    vLike = "0";
                } else {
                    Toast.makeText(ReviewActivity.this, "실패하였습니다", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class JSON extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            switch ((String) params[0]) {
                case "CleansingFoam":
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("keyid", "CleansingFoam");
                        jo.put("keyName", (String) params[1].toString());
                        return Http.requestWithJson(ServerInterface.ProductItem, (String) jo.toString(), ServerInterface.HTTP_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case "cleansingOil":
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("keyid", "cleansingOil");
                        jo.put("keyName", (String) params[1].toString());
                        return Http.requestWithJson(ServerInterface.ProductItem, (String) jo.toString(), ServerInterface.HTTP_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case "CleansingMilk":
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("keyid", "CleansingMilk");
                        jo.put("keyName", (String) params[1].toString());
                        return Http.requestWithJson(ServerInterface.ProductItem, (String) jo.toString(), ServerInterface.HTTP_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case "likeView":
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("likeUser", (String) params[1].toString());
                        jo.put("likeProduct", (String) params[2].toString());
                        return Http.requestWithJson(ServerInterface.likeView, (String) jo.toString(), ServerInterface.HTTP_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            return null;
        }
    }
}
