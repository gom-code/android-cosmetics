package com.example.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Fragment1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    MyBeautyTemListViewAdapter myReviewListAdapter;
    private ArrayList<MyReviewListViewItem> myReviewListViewItemData;

    back task;
    String json = "";
    JSONArray jsonArray = null;
    SwipeRefreshLayout mSwipeRefreshLayout;     // 새로고침
    TextView weekTrueBtn, todaySaleBtn, directfarmBtn;            //  상단 메뉴
    int i = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefresh);     //새로고침
        mSwipeRefreshLayout.setOnRefreshListener(this);

        weekTrueBtn = (TextView) layout.findViewById(R.id.weekTrueBtn);
        todaySaleBtn = (TextView) layout.findViewById(R.id.todaySaleBtn);
        directfarmBtn = (TextView) layout.findViewById(R.id.directfarmBtn);

        MyApp.appInstance = getContext();
        if (myReviewListViewItemData == null) {
            myReviewListViewItemData = new ArrayList<>();
        } else {
            myReviewListViewItemData.clear();
        }
        listView = (ListView) layout.findViewById(R.id.listView);
        listView.setDivider(null);
       // final ScrollView main_box_scrollview = layout.findViewById(R.id.main_box_scrollview);   // 스크롤하기 위한

        try {
            JSONTaker taker = new JSONTaker();
            json = taker.execute("CleansingFoam").get();
            jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                MyReviewListViewItem item = new MyReviewListViewItem("null", jObject.getString("brandName"), jObject.getString("productName"), ""+(i+1), jObject.getInt("countlike"));
                myReviewListViewItemData.add(item);
                myReviewListAdapter = new MyBeautyTemListViewAdapter(MyApp.appInstance,  myReviewListViewItemData, R.layout.product_list_item);
                listView.setAdapter(myReviewListAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        weekTrueBtn.setOnClickListener(new View.OnClickListener() { //클렌징폼 메뉴 선택시
            @Override
            public void onClick(View view) {
                i= 0;
                new Handler().postDelayed(new Runnable() {
                    ProgressDialog dialog = ProgressDialog.show(MyApp.appInstance,"서버에 접속중.. 잠시만 기달려주세요","정보를 가져오는중...",false);
                    @Override
                    public void run() {
                        try {
                            myReviewListViewItemData.clear();
                            JSONTaker taker = new JSONTaker();
                            json = taker.execute("CleansingFoam").get();
                            jsonArray = new JSONArray(json);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObject = jsonArray.getJSONObject(i);
                                MyReviewListViewItem item = new MyReviewListViewItem("null", jObject.getString("brandName"), jObject.getString("productName") , ""+(i+1), jObject.getInt("countlike")) ;
                                myReviewListViewItemData.add(item);
                                myReviewListAdapter = new MyBeautyTemListViewAdapter(MyApp.appInstance,  myReviewListViewItemData, R.layout.product_list_item);
                                listView.setAdapter(myReviewListAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        myReviewListAdapter.notifyDataSetChanged(); // listview 갱신
                        dialog.dismiss();
                    }
                }, 1000);
            }
        });

        todaySaleBtn.setOnClickListener(new View.OnClickListener() {    // 클렌징 오일 메뉴 선택시
            @Override
            public void onClick(View view) {
                i = 1;
                new Handler().postDelayed(new Runnable() {
                    ProgressDialog dialog = ProgressDialog.show(MyApp.appInstance,"서버에 접속중.. 잠시만 기달려주세요","정보를 가져오는중...",false);
                    @Override
                    public void run() {
                        try {
                            myReviewListViewItemData.clear();
                            JSONTaker taker = new JSONTaker();
                            json = taker.execute("cleansingOil").get();
                            jsonArray = new JSONArray(json);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObject = jsonArray.getJSONObject(i);
                                MyReviewListViewItem item = new MyReviewListViewItem("null", jObject.getString("brandName"), jObject.getString("productName") , ""+(i+1), jObject.getInt("countlike")) ;
                                myReviewListViewItemData.add(item);
                                myReviewListAdapter = new MyBeautyTemListViewAdapter(MyApp.appInstance,  myReviewListViewItemData, R.layout.product_list_item);
                                listView.setAdapter(myReviewListAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        myReviewListAdapter.notifyDataSetChanged(); // listview 갱신
                        dialog.dismiss();
                    }
                }, 1000);
            }
        });

        directfarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i= 2;
                new Handler().postDelayed(new Runnable() {
                    ProgressDialog dialog = ProgressDialog.show(MyApp.appInstance,"서버에 접속중.. 잠시만 기달려주세요","정보를 가져오는중...",false);
                    @Override
                    public void run() {
                        try {
                            myReviewListViewItemData.clear();
                            JSONTaker taker = new JSONTaker();
                            json = taker.execute("CleansingMilk").get();
                            jsonArray = new JSONArray(json);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObject = jsonArray.getJSONObject(i);
                                MyReviewListViewItem item = new MyReviewListViewItem("null", jObject.getString("brandName"), jObject.getString("productName") , ""+(i+1), jObject.getInt("countlike")) ;
                                myReviewListViewItemData.add(item);
                                myReviewListAdapter = new MyBeautyTemListViewAdapter(MyApp.appInstance,  myReviewListViewItemData, R.layout.product_list_item);
                                listView.setAdapter(myReviewListAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        myReviewListAdapter.notifyDataSetChanged(); // listview 갱신
                        dialog.dismiss();
                    }
                }, 1000);
            }
        });
        return layout;
    }

    // 새로고침
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    myReviewListViewItemData.clear();
                    JSONTaker taker = new JSONTaker();
                    json = taker.execute("CleansingFoam").get();
                    jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        MyReviewListViewItem item = new MyReviewListViewItem("null", jObject.getString("brandName"), jObject.getString("productName") , ""+(i+1), jObject.getInt("countlike")) ;
                        myReviewListViewItemData.add(item);
                        myReviewListAdapter = new MyBeautyTemListViewAdapter(MyApp.appInstance,  myReviewListViewItemData, R.layout.product_list_item);
                        listView.setAdapter(myReviewListAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                myReviewListAdapter.notifyDataSetChanged(); // listview 갱신
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    // 어뎁터
    private class MyBeautyTemListViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<MyReviewListViewItem> data;   // 데이터
        private int layout;
        private ViewHolder viewHolder;

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

                viewHolder = new ViewHolder();

                viewHolder.itemLayout = convertView.findViewById(R.id.itemId);
                viewHolder.itemNumber = convertView.findViewById(R.id.itemNumber);
                viewHolder.itemImg = convertView.findViewById(R.id.itemImg);
                viewHolder.itemBrand = convertView.findViewById(R.id.itemBrand);
                viewHolder.itemName = convertView.findViewById(R.id.itemName);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final MyReviewListViewItem ProductListViewItem = data.get(position);
            if (ProductListViewItem != null) {
                //viewHolder.itemLayout.setTag(ProductListViewItem.getItemId());
                viewHolder.itemNumber.setText(ProductListViewItem.getItemId());
                try {
                    task = new back();

                    viewHolder.itemImg.setImageBitmap(task.execute(ServerInterface.domain+"/img/"+ProductListViewItem.getItemName()+".png").get());
                    viewHolder.itemImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MyApp.appInstance, (String)ProductListViewItem.getItemName(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                viewHolder.itemBrand.setText(ProductListViewItem.getItemBrand());
                viewHolder.itemName.setText(ProductListViewItem.getItemName());
                viewHolder.itemName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MyApp.appInstance, ReviewActivity.class);
                        intent.putExtra("itemName", (String) ProductListViewItem.getItemName());    // 이름
                        intent.putExtra("countlike", ProductListViewItem.getCountlike());
                        intent.putExtra("key", ""+i);
                        startActivity(intent);
                    }
                });
            }
            return convertView;
        }
        class ViewHolder {
            LinearLayout itemLayout;
            ImageView itemImg;
            TextView itemBrand;
            TextView itemName;
            TextView itemNumber;
        }
    }
    private class MyReviewListViewItem{
        private String itemId;
        private String itemName;
        private String itemImg;
        private String itemBrand;
        private int countlike;

        public MyReviewListViewItem(String itemImg, String itemBrand, String itemName, String itemId, int countlike) {
            this.itemImg = itemImg;
            this.itemBrand = itemBrand;
            this.itemName = itemName;
            this.itemId = itemId;
            this.countlike = countlike;
        }

        public String getItemImg() {
            return itemImg;
        }

        public String getItemBrand() {
            return itemBrand;
        }

        public String getItemName() {
            return itemName;
        }

        public String getItemId() {
            return itemId;
        }

        public int getCountlike(){ return countlike; }

    }
    public class JSONTaker extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            switch ((String) params[0]) {
                case "CleansingFoam":
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("keyid", "CleansingFoam");
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
                        return Http.requestWithJson(ServerInterface.ProductItem, (String) jo.toString(), ServerInterface.HTTP_POST);
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


