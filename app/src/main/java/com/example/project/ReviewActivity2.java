package com.example.project;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;
import com.example.project.base.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ReviewActivity2 extends BaseActivity {
    private back task;
    private ImageView imageViews;
    private TextView textView, textView1, textView2;
    private EditText etMyReview;
    private Button ReviewButton;

    private ListView listView;
    MyBeautyTemListViewAdapter myReviewListAdapter;
    private ArrayList<MyReviewListViewItem> myReviewListViewItemData;

    String json="";
    JSONArray jsonArray = null;
    String getkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        MyApp.appInstance = this;
        if (myReviewListViewItemData == null) {
            myReviewListViewItemData = new ArrayList<>();
        } else {
            myReviewListViewItemData.clear();
        }
        listView = (ListView)findViewById(R.id.listView_reivew);
        listView.setDivider(null);
        final ScrollView main_box_scrollview = findViewById(R.id.review_scrollview);   // 스크롤하기 위한

        final String getString = getIntent().getStringExtra("itemName");
        getkey = getIntent().getStringExtra("key");

        imageViews = (ImageView) findViewById(R.id.imageViews);
        textView = findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        etMyReview = (EditText) findViewById(R.id.etMyReview);
        final RatingBar rb = (RatingBar) findViewById(R.id.ratbar);
        etMyReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final int m_nMaxLengthOfDeviceName = 300;
                etMyReview.setFilters(new InputFilter[] { new InputFilter.LengthFilter(m_nMaxLengthOfDeviceName) });    // 글자수 제한
                // 입력되는 텍스트에 변화가 있을 때
                if(etMyReview.toString().equals("") || etMyReview.getText().toString().length() >= 2){
                    ReviewButton.setEnabled(true);      // 활성화
                }
                String input = etMyReview.getText().toString();
                textView.setText(input.length() + "/ 300글자");
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });


        ReviewButton = (Button) findViewById(R.id.ReviewButton);
        ReviewButton.setEnabled(false);     // 비활성화
        ReviewButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                myReviewListViewItemData.clear();
                final ProgressDialog dialog = ProgressDialog.show(MyApp.appInstance,"서버와 접속중.. 잠시만 기달려주세요","리뷰 등록중...",false);
                    try {
                        JSON taker = new JSON();
                        json = taker.execute("Review", getString, etMyReview.getText().toString(), (int)rb.getRating()).get();

                        JSON taker1 = new JSON();
                        String json1 = taker1.execute("ReviewView", getString).get();
                        JSONArray jsonArray1 = new JSONArray(json1);
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jObject1 = jsonArray1.getJSONObject(i);
                            MyReviewListViewItem item1 = new MyReviewListViewItem(jObject1.getString("email"),jObject1.getString("itemName"), jObject1.getInt("itemNumber"), jObject1.getString("items"));
                            myReviewListViewItemData.add(item1);
                            myReviewListAdapter = new MyBeautyTemListViewAdapter(MyApp.appInstance,  myReviewListViewItemData, R.layout.review_list_item);
                            listView.setAdapter(myReviewListAdapter);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                dialog.dismiss();
            }
        });


        try {
            String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
            if (!Network.equals("")) {    //  네트워크 확인
                Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
                return;
            }
            task = new back();
            imageViews.setImageBitmap(task.execute(ServerInterface.domain+"/img/"+ getString+".png").get());
            if(getkey.equals("0")){
                JSON taker = new JSON();
                json = taker.execute("CleansingFoam", getString).get();
                jsonArray = new JSONArray(json);
                JSONObject jObject = jsonArray.getJSONObject(0);
                textView1.setText((jObject.getString("companyName").equals("null"))? "":jObject.getString("companyName"));
                textView2.setText(jObject.getString("explicate"));
            } else if(getkey.equals("1")){
                JSON taker = new JSON();
                json = taker.execute("cleansingOil", getString).get();
                jsonArray = new JSONArray(json);
                JSONObject jObject = jsonArray.getJSONObject(0);
                textView1.setText((jObject.getString("companyName").equals("null"))? "":jObject.getString("companyName"));
                textView2.setText(jObject.getString("explicate"));
            } else if(getkey.equals("2")){
                JSON taker = new JSON();
                json = taker.execute("CleansingMilk", getString).get();
                jsonArray = new JSONArray(json);
                JSONObject jObject = jsonArray.getJSONObject(0);
                textView1.setText((jObject.getString("companyName").equals("null"))? "":jObject.getString("companyName"));
                textView2.setText(jObject.getString("explicate"));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
            if (!Network.equals("")) {    //  네트워크 확인
                Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
                return;
            }
            JSON taker = new JSON();
            String json1 = taker.execute("ReviewView", getString).get();
            JSONArray jsonArray1 = new JSONArray(json1);
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jObject1 = jsonArray1.getJSONObject(i);
                MyReviewListViewItem item1 = new MyReviewListViewItem(jObject1.getString("email"), jObject1.getString("itemName"), jObject1.getInt("itemNumber"), jObject1.getString("items"));
                myReviewListViewItemData.add(item1);
                myReviewListAdapter = new MyBeautyTemListViewAdapter(MyApp.appInstance,  myReviewListViewItemData, R.layout.review_list_item);
                listView.setAdapter(myReviewListAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 어뎁터
    private class MyBeautyTemListViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<MyReviewListViewItem> data;   // 데이터
        private int layout;
        private MyBeautyTemListViewAdapter.ViewHolder viewHolder;

        public MyBeautyTemListViewAdapter(Context context, ArrayList<MyReviewListViewItem> data, int layout) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data = data;
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position).getItemName();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(layout, parent, false);

                viewHolder = new MyBeautyTemListViewAdapter.ViewHolder();

                //viewHolder.itemLayout = convertView.findViewById(R.id.itemId);
                viewHolder.itemName = convertView.findViewById(R.id.itemName);          // 뷰티무식자(닉네임)
                viewHolder.itemNumber = convertView.findViewById(R.id.itemNumber);     // 3.3
                viewHolder.itembar = convertView.findViewById(R.id.itembar);            // 별모양
                viewHolder.items = convertView.findViewById(R.id.items);                // 리뷰내용
                viewHolder.delete = convertView.findViewById(R.id.delete);              // 휴지통

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyBeautyTemListViewAdapter.ViewHolder) convertView.getTag();
            }

            final MyReviewListViewItem ProductListViewItem = data.get(position);
            if (ProductListViewItem != null) {
                //viewHolder.itemLayout.setTag(ProductListViewItem.getItemId());
                viewHolder.itemName.setText(ProductListViewItem.getItemName());
                viewHolder.itemNumber.setText(""+ProductListViewItem.getItemNumber());
                viewHolder.itembar.setRating(ProductListViewItem.getItemNumber());
                viewHolder.items.setText(ProductListViewItem.getItems());
                if (ProductListViewItem.getEmail().equals(MyApp.app.getEmail())){
                    viewHolder.delete.setVisibility(View.VISIBLE);      // 뷰 보이기
                    viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog dialog = deleteLike(ProductListViewItem.getItems());
                            dialog.show();
                        }
                    });
                } else {
                    viewHolder.delete.setVisibility(View.GONE);    // 직업 뷰 감추기
                }
            }
            return convertView;
        }
        class ViewHolder {
            TextView itemName;
            TextView itemNumber;
            RatingBar itembar;
            TextView items;
            ImageView delete;
        }
        private AlertDialog deleteLike(String str) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity2.this);
            builder.setTitle("리뷰 삭제");
            builder.setMessage("리뷰를 삭제 하시겠습니까?");

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
                    if (!Network.equals("")) {    //  네트워크 확인
                        Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, String> para = new HashMap<>();
                    para.put("email", MyApp.app.getEmail());               // 이메일 등록한 놈
                    para.put("items", str);                                 // 내용

                    JSONObject json = new JSONObject(para);
                    ProgressDialog dialog1 = ProgressDialog.show(MyApp.appInstance,"서버에 접속중.. 잠시만 기달려주세요","삭제중...",false);
                    final RequestQueue requestQueue = Volley.newRequestQueue(MyApp.appInstance);

                    JsonObjectRequest postJsonRequset = new JsonObjectRequest
                            (Request.Method.POST, ServerInterface.rmReview, json, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    dialog1.dismiss();
                                    Log.d("response", response.toString());
                                    try {
                                        String date = response.toString();
                                        JSONObject jsonObject = new JSONObject(date);
                                        if(jsonObject.getString("rsltCode").equals("true")){
                                            Toast.makeText(MyApp.appInstance, "리뷰 삭제 성공", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        } else {
                                            Toast.makeText(MyApp.appInstance, "리뷰 삭제 실패.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            dialog1.dismiss();
                                            Log.d("error:", error.toString());
                                        }
                                    });
                    requestQueue.add(postJsonRequset);
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
    }

    private class MyReviewListViewItem{
        private String email;
        private String itemName;    // 닉네임
        private int itemNumber;  // 3.3 or 별모양 바
        private String items;       // 리뷰내용


        public MyReviewListViewItem(String email, String itemName, int itemNumber, String items) {
            this.itemName = itemName;
            this.itemNumber = itemNumber;
            this.items = items;
            this.email = email;
        }
        public String getEmail() {
            return email;
        }
        public String getItemName() {
            return itemName;
        }

        public int getItemNumber() {
            return itemNumber;
        }

        public String getItems() {
            return items;
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
                case "Review":
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("keyemail", MyApp.app.getEmail());
                        jo.put("keyName", (String) params[1].toString());
                        jo.put("keyReview", (String) params[2].toString());
                        jo.put("keyRatingBar", (int)params[3]);
                        return Http.requestWithJson(ServerInterface.SaveReview, (String) jo.toString(), ServerInterface.HTTP_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NetworkErrorException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
                case "ReviewView":
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("keyId", "ReviewView");
                        jo.put("keyName", (String) params[1].toString());
                        return Http.requestWithJson(ServerInterface.reviewlist, (String) jo.toString(), ServerInterface.HTTP_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NetworkErrorException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
            }
            return null;
        }
    }
}
