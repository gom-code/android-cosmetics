package com.example.project;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.project.app.MyApp;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;


public class TalkFragment extends Fragment implements OnMapReadyCallback {
    //구글맵참조변수
    GoogleMap mMap;
    MapView mapView;
    View mView;
    Polyline polyline = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mView.findViewById(R.id.mapview);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);  // 비동기적 지도 콜백을 등록
        }
    }
    private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
    private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점
    // 구글 맵 초기 세팅
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // 퍼미션 체크 ActivityCompat.checkSelfPermission(Context, String)
        // 퍼미션 요청 ActivityCompat.requestPermissions(Activity, String[], int)
        // 퍼미션 요청 콜백함수 ActivityCompat.OnRequestPermissionsResultCallback
        // 사용자 권한 요청
        if (ActivityCompat.checkSelfPermission(MyApp.appInstance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApp.appInstance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "위치정보 권한을 확인해주세요.", Toast.LENGTH_SHORT).show();
            // 퍼미션이 허용되어 있는 상태라면 PackageManager.PERMISSION_GRANTED를 리턴하고
            // 거부 되어 있는 상태라면  PackageManager.PERMISSION_DENIED를 리턴합니다.
        }
        GpsTracker gpsTracker = new GpsTracker(MyApp.appInstance);
        Geocoder geocoder = new Geocoder(MyApp.appInstance, Locale.KOREA);

        if (gpsTracker.getLatitude() == 0 | gpsTracker.getLongitude() == 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MyApp.appInstance);
            alert.setMessage("현재 위치정보를 읽을 수 없습니다.");
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
            return;
        }
        // 지도타입 - 일반
        mMap.setMapType(mMap.MAP_TYPE_NORMAL);

        // camera 좌표를 현재위치로 출력한다.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())   // 위도, 경도
        ));

        // 구글지도(지구) 에서의 zoom 레벨은 1~23 까지 가능합니다.
        // 여러가지 zoom 레벨은 직접 테스트해보세요
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom);   // moveCamera 는 바로 변경하지만,
        // animateCamera() 는 근거리에선 부드럽게 변경합니다

        // marker 표시
        // market 의 위치, 타이틀, 짧은설명 추가 가능.
        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                .title("현재위치")
                .alpha(0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
        Location location = findGeoPoint(MyApp.appInstance,  "충청북도 청주시 흥덕구 수곡2동 909");
        marker .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("아리따움 - 수곡점")
                .alpha(1f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                //.snippet("Seoul Station");
        mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
        location = findGeoPoint(MyApp.appInstance,  "충청북도 청주시 상당구 북문로1가 97-4");
        marker .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("미샤(청주로데오점)")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
        location = findGeoPoint(MyApp.appInstance,  "충청북도 청주시 흥덕구 분평동 1378");
        marker .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("아리따움 - 분평점")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
        location = findGeoPoint(MyApp.appInstance,  "충청북도 청주시 상당구 서문동 152-2");
        marker .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("토니모리 - 청주 성안점")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
        location = findGeoPoint(MyApp.appInstance,  "충청북도 청주시 상당구 북문로1가 65-2");
        marker .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("더페이스샵 - 청주지점")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
        // 마커클릭 이벤트 처리
        // GoogleMap 에 마커클릭 이벤트 설정 가능.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커 클릭시 호출되는 콜백 메서드
//                Toast.makeText(getApplicationContext(),
//                        marker.getTitle() + " 클릭했음"
//                        , Toast.LENGTH_SHORT).show();
                //선택한 타겟위치
                LatLng location = marker.getPosition();
                String markerId = marker.getId();       // 선택 마커 아이디
                startLatLng = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());        // 현재 위치를 시작점으로 설정
                endLatLng = new LatLng(location.latitude, location.longitude);                     // 선택한 마커
                drawPath();
                marker.setSnippet("거리:"+getDistance(startLatLng, endLatLng)+"km");
                return false;
            }
        });

    }
    private void drawPath() {        // polyline을 그려주는 메소드
        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCVtpoMuKtjD2LGdzr5DjythvlYQPKqRIk")
                .build();
        if(polyline == null){
            PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(5).color(Color.BLACK).geodesic(true);
            polyline= mMap.addPolyline(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 14));
        } else {
            polyline.remove();
            PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(5).color(Color.BLACK).geodesic(true);
            polyline= mMap.addPolyline(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 14));
        }
    }
    // 현재 위치 에서 도착 위치 거리 측정 메소드
    public int getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);

        return (int)distance;
    }
    /**
     * 주소로부터 위치정보 취득
     * @param address 주소
     */
    private Location findGeoPoint(Context mcontext, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mcontext);
        List<Address> addr = null;// 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정

        try {
            addr = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // 위도가져오기
                double lon = lating.getLongitude(); // 경도가져오기
                loc.setLatitude(lat);
                loc.setLongitude(lon);
            }
        }
        return loc;
    }
}