package com.example.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class My extends Fragment {
    /* XML 선언 */
    private RelativeLayout fm_lyTutorial, fm_lyContact, fm_lyWithdraw;
    private TextView fm_txtId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyApp.appInstance = getContext();
        View v = inflater.inflate(R.layout.frag_my, container, false);

        fm_lyTutorial = v.findViewById(R.id.fm_lyTutorial);
        fm_lyContact = v.findViewById(R.id.fm_lyContact);
        fm_txtId = v.findViewById(R.id.fm_txtId);
        fm_txtId.setText(MyApp.app.getEmail());
        fm_lyWithdraw = v.findViewById(R.id.fm_lyWithdraw);

        init();
        return v;
    }
    private void init(){
        fm_lyTutorial.setOnClickListener(new View.OnClickListener() {      // 게시물 삭제
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), activity_my.class);
                startActivity(intent);
            }
        });
        fm_lyContact.setOnClickListener(new View.OnClickListener() {    // 오류 신고
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Email.class);
                startActivity(intent);
            }
        });
        fm_lyWithdraw.setOnClickListener(new View.OnClickListener() {       // 탈퇴하기
            @Override
            public void onClick(View view) {
                AlertDialog dialog = deleteLike();
                dialog.show();
            }
        });
    }
    private AlertDialog deleteLike() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("탈퇴 학인");
        builder.setMessage("탈퇴를 하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "탈퇴되었습니다....", Toast.LENGTH_SHORT).show();
                Intent registActivity = new Intent(getContext(), LoginActivity.class);
                startActivity(registActivity);
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
