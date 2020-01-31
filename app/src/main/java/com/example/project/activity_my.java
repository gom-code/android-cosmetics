package com.example.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.android.gms.maps.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class activity_my extends BaseActivity {
    private ListView listView;
    back task;
    JSONArray jsonArray = null;
    MyBeautyTemListViewAdapter myReviewListAdapter;
    private ArrayList<MyReviewListViewItem> myReviewListViewItemData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        MyApp.appInstance = this;
        if (myReviewListViewItemData == null) {
            myReviewListViewItemData = new ArrayList<>();
        } else {
            myReviewListViewItemData.clear();
        }
        listView = (ListView) findViewById(R.id.listVieww);
        listView.setDivider(null);
        try {
            JSONTaker taker = new JSONTaker();
            String json = taker.execute("item").get();
            jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                MyReviewListViewItem item = new MyReviewListViewItem(jObject.getString("item"));
                myReviewListViewItemData.add(item);
                myReviewListAdapter = new MyBeautyTemListViewAdapter(MyApp.appInstance,  myReviewListViewItemData, R.layout.my_list_item);
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
            return data.get(position).getItem();
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

                viewHolder.itemImg = convertView.findViewById(R.id.itemImg);
                viewHolder.itemBrand = convertView.findViewById(R.id.itemName);
                viewHolder.delete = convertView.findViewById(R.id.delete);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyBeautyTemListViewAdapter.ViewHolder) convertView.getTag();
            }

            final MyReviewListViewItem ProductListViewItem = data.get(position);
            if (ProductListViewItem != null) {
                //viewHolder.itemLayout.setTag(ProductListViewItem.getItemId());
                try {
                    task = new back();

                    viewHolder.itemImg.setImageBitmap(task.execute(ServerInterface.domain+"/img/"+ProductListViewItem.getItem()+".png").get());
                    viewHolder.itemBrand.setText(ProductListViewItem.getItem());
                    viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog dialog = deleteLike(ProductListViewItem.getItem());
                            dialog.show();
                        }
                    });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            return convertView;
        }
        class ViewHolder {
            ImageView itemImg;
            TextView itemBrand;
            ImageView delete;
        }
        private AlertDialog deleteLike(String str) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_my.this);
            builder.setTitle("게시물 삭제");
            builder.setMessage("게시물을 삭제 하시겠습니까?");

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
                    if (!Network.equals("")) {    //  네트워크 확인
                        Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, String> para = new HashMap<>();
                    para.put("keyid", "rm");
                    para.put("email", MyApp.app.getEmail());               // 이메일 등록한 놈
                    para.put("items", str);                                 // 내용

                    JSONObject json = new JSONObject(para);
                    ProgressDialog dialog1 = ProgressDialog.show(MyApp.appInstance,"서버에 접속중.. 잠시만 기달려주세요","삭제중...",false);
                    final RequestQueue requestQueue = Volley.newRequestQueue(MyApp.appInstance);

                    JsonObjectRequest postJsonRequset = new JsonObjectRequest
                            (Request.Method.POST, ServerInterface.Delete, json, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    dialog1.dismiss();
                                    Log.d("response", response.toString());
                                    try {
                                        String date = response.toString();
                                        JSONObject jsonObject = new JSONObject(date);
                                        if(jsonObject.getString("rsltCode").equals("true")){
                                            Toast.makeText(MyApp.appInstance, " 삭제 성공", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        } else {
                                            Toast.makeText(MyApp.appInstance, " 삭제 실패.", Toast.LENGTH_SHORT).show();
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
        private String item;

        public MyReviewListViewItem(String item) {
            this.item = item;

        }
        public String getItem() {
            return item;
        }

    }
    public class JSONTaker extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            switch ((String) params[0]) {
                case "item":
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("keyid", "item");
                        jo.put("email", MyApp.app.getEmail());
                        return Http.requestWithJson(ServerInterface.Delete, (String) jo.toString(), ServerInterface.HTTP_POST);
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
