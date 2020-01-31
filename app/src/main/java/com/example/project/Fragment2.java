package com.example.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import com.example.project.app.MyApp;

public class Fragment2 extends Fragment {
    /* XML 선언 */
    ViewFlipper fm_viewFlipper;
    ImageView fm_imgAd1, fm_imgAd2, fm_imgAd3, fm_imgStory1, fm_imgStory2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyApp.appInstance = getContext();
        View v = inflater.inflate(R.layout.fragment2, container, false);

        /* 초기화 */
        fm_viewFlipper = (ViewFlipper)v.findViewById(R.id.fm_viewAd);
        fm_imgAd1 = (ImageView)v.findViewById(R.id.fm_imgAd1);
        fm_imgAd2 = (ImageView)v.findViewById(R.id.fm_imgAd2);
        fm_imgAd3 = (ImageView)v.findViewById(R.id.fm_imgAd3);
        fm_imgStory1 = (ImageView)v.findViewById(R.id.fm_imgStory1);
        fm_imgStory2 = (ImageView)v.findViewById(R.id.fm_imgStory2) ;

        /* 광고 뷰플리퍼 */
        fm_viewFlipper.setFlipInterval(2000);
        fm_viewFlipper.startFlipping();

        fm_imgAd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://department.ssg.com/item/itemView.ssg?itemId=1000019660018");
                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);
            }
        });

        fm_imgAd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.oliveyoungshop.com/prd/detail_cate.jsp?item_cd=38699642");
                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);
            }
        });

        fm_imgAd1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://innisfree.co.kr/event/greenchristmas2016/gatePc.jsp#p2");
                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);
            }
        });

        /* 올리기  */
        fm_imgStory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), activityProduct.class);
                startActivity(i);
            }
        });

        fm_imgStory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getContext(), free.class);
//                startActivity(i);
                Intent intent = new Intent(getActivity(), activity_my.class);   // 게시물 삭제
                startActivity(intent);
            }
        });
        return v;
    }
}
