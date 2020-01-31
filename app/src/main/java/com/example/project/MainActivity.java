package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.app.MyApp;
import com.example.project.base.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ImageView leftmenuBtn;
    DrawerLayout drawerLayout;
    View drawerView;
    private ListView mNavBodyListView;
    private EditText edittext;
    private TextView textname;

    private Toast toast;
    private long backKeyPressedTime = 0;

    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    TalkFragment fragment4;
    My fragment5;

    ViewPager pager;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp.appInstance = this;

        leftmenuBtn = (ImageView) findViewById(R.id.leftmenuBtn);
        leftmenuBtn.setOnClickListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerView = (View) findViewById(R.id.nav_view);
        textname = (TextView) findViewById(R.id.textname);
        textname.setText(MyApp.app.getEmail()+"님 반갑습니다.");

        // Array of strings...
        String[] menuArray = {"마이페이지", "화장품 리뷰 영상", "로그아웃"};

        mNavBodyListView = findViewById(R.id.nav_body_listview);
        ArrayAdapter Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                switch (position) {
                    case 0:
                        //view.setText("sss");
                        break;
                    case 1:
                        //view.setText("sssㄴ");
                        break;

                }
                return view;
            }
        };
        mNavBodyListView.setAdapter(Adapter);
        /* 아이템 클릭시 작동 */
        mNavBodyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(MyApp.appInstance, ReviewActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        drawerLayout.closeDrawer(drawerView);
                        pager.setCurrentItem(2);
                        break;
                    case 2:
                        SharedPreferences pref = MyApp.mContext.getSharedPreferences("login", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor prefEdit = pref.edit();
                        // 기존 내역 삭제 하기
                        prefEdit.remove("email");
                        prefEdit.remove("password");
                        prefEdit.commit();
                        intent = new Intent(MyApp.appInstance, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0); //화면이동 애니메이션 효과
                        finish();
                        break;
                }
            }
        });
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new TalkFragment();
        fragment5 = new My();

        //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        // 밑에 테이블
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {  // 클릭 되었을때 이벤트 처리
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab1:
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.container, fragment1).commit();
                        pager.setCurrentItem(0);
                        return true;
                    case R.id.tab2:
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.container, fragment2).commit();
                        pager.setCurrentItem(1);

                        return true;
                    case R.id.tab3:
                        pager.setCurrentItem(2);
                        return true;
                    case R.id.tab4:
                        pager.setCurrentItem(3);
                        return true;
                    case R.id.tab5:
                        pager.setCurrentItem(4);
                        return true;
                }

                return false;
            }
        });

        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        adapter.addItem(fragment1);
        adapter.addItem(fragment2);
        adapter.addItem(fragment3);
        adapter.addItem(fragment4);
        adapter.addItem(fragment5);

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            MenuItem prevBottomNavigation;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevBottomNavigation != null) {
                    prevBottomNavigation.setChecked(false);
                }
                prevBottomNavigation = bottomNavigation.getMenu().getItem(position);
                prevBottomNavigation.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.leftmenuBtn:
                drawerLayout.openDrawer(drawerView);    // 열릴때
//                 drawerLayout.closeDrawer(drawerView);    // 닫힐때
                break;
        }
    }

    // 뒤로 가기 버튼 눌렀을때 종료하기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            drawerLayout.closeDrawer(drawerView);    // 닫힐때
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this,
                    "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel(); // 토스트 없애기
            // this.finish();
            android.os.Process.killProcess(android.os.Process.myPid()); // 프로세스 죽이기
        }
    }
}
