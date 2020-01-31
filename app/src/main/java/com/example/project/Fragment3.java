package com.example.project;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.app.MyApp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class  Fragment3 extends Fragment implements ClickListener{
    private static final int PERMISSION_CODE = 101;
    RecyclerView video_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);
        //MyApp.appInstance.getSupportActionBar().hide();
        video_list= layout.findViewById(R.id.video_list);
        if(Build.VERSION.SDK_INT>=23){
            if(checkPermission()){
                readAllFIles();
            }
            else{
                requestPermission();
            }
        }
        else{
            readAllFIles();
        }
        return layout;
    }

    private boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(MyApp.appInstance, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }

    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) MyApp.appInstance,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MyApp.appInstance, "Allow Permission", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions((Activity) MyApp.appInstance,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readAllFIles();
                }
        }
    }

    private void readAllFIles() {
        HashSet<String> hashSet=new HashSet<>();
        String[] projection={MediaStore.Video.VideoColumns.DATA,MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor= MyApp.appInstance.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
        try{
            if(cursor!=null){
                cursor.moveToFirst();
                do{
                    hashSet.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                }
                while (cursor.moveToNext());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        List<String> file_path=new ArrayList<>(hashSet);
        List<VideoModel> videoModelList=new ArrayList<>();
        for (String data:file_path){
            File file=new File(data);
            videoModelList.add(new VideoModel(file.getName(),file.getAbsolutePath()));
        }
        video_list.setLayoutManager(new LinearLayoutManager(MyApp.appInstance));
        VideoItemAdapter videoItemAdapter=new VideoItemAdapter(videoModelList,MyApp.appInstance, Fragment3.this);
        video_list.setAdapter(videoItemAdapter);

        //lets create video player
    }
    @Override
    public void onClickItem(String filePath) {
        startActivity(new Intent(MyApp.appInstance, VideoPlayerActivtit.class).putExtra("path_file",filePath));
    }
}
