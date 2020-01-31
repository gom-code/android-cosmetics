package com.example.project;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RegistFromNaver extends AppCompatActivity {
	private String _naverId;
	private String _email = "";
	private String _nickName = "";
	private String _password = "";

	/*
	회원가입 2단계
	 */

	private CheckBox allCheck;
	private CheckBox check1;
	private CheckBox check2;

	private TextView email2;
	private Button female;
	private Button male;


	private TextView nickName2;
	private EditText address1;
	private EditText address2;
	private Button zipCode;
	private Button curAddress;
	private Button gunsung;
	private Button jungsung;
	private Button gisung;
	private Button bokhapsung;
	private Button mingamsung;
	private Button morum;
	private Button cancelButton;
	private Button backButton;
	private Button submitButton;

	private String _gender = "";
	private String _year = "";
	private String _month = "";
	private String _day = "";

	private String _address1 = "";
	private String _address2 = "";
	private String _skinType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist_from_naver);

		Intent intent = getIntent();
		_naverId = intent.getStringExtra("id");
		_email = intent.getStringExtra("email");
		_nickName = intent.getStringExtra("nickname");
		_password = _naverId;

		secondRegistFindView();

		secondRegistOpen();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				String[] addresses = data.getStringArrayExtra("address");
				_address1 = addresses[0];
				_address2 = addresses[1];

				address1.setText(_address1);
				address2.setText(_address2);
			}
		}
	}

	private void secondRegistOpen() {
		secondRegistFindView();
		//setBirthdaySpinner();
		setSecondRegistListener();

		email2.setText(_email);
		nickName2.setText(_nickName);
		female.setEnabled(false);
		male.setEnabled(true);
		_gender = "여자";

		gunsung.setEnabled(false);
		jungsung.setEnabled(true);
		gisung.setEnabled(true);
		bokhapsung.setEnabled(true);
		mingamsung.setEnabled(true);
		morum.setEnabled(true);
		_skinType = "건성";
	}

	private void secondRegistFindView() {

		email2 = findViewById(R.id.email2);
		nickName2 = findViewById(R.id.nickName2);
		female = findViewById(R.id.female);
		male = findViewById(R.id.male);

		address1 = findViewById(R.id.address1);
		address2 = findViewById(R.id.address2);
		zipCode = findViewById(R.id.zipCode);
		curAddress = findViewById(R.id.curAddress);
		gunsung = findViewById(R.id.skinType1);
		jungsung = findViewById(R.id.skinType2);
		gisung = findViewById(R.id.skinType3);
		bokhapsung = findViewById(R.id.skinType4);
		mingamsung = findViewById(R.id.skinType5);
		morum = findViewById(R.id.skinType6);
		cancelButton = findViewById(R.id.bottom_cancel);
		backButton = findViewById(R.id.bottom_back);
		submitButton = findViewById(R.id.bottom_submit);
	}

	private void setSecondRegistListener() {

		female.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				female.setEnabled(false);
				male.setEnabled(true);
				female.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_gender_button));
				female.setTextColor(Color.parseColor("#2DBCDE"));
				male.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_gender_button));
				male.setTextColor(Color.parseColor("#D5D5D5"));

				_gender = "여자";
				Toast.makeText(getApplicationContext(), "여자", Toast.LENGTH_SHORT).show();
			}
		});
		male.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				male.setEnabled(false);
				female.setEnabled(true);
				male.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_gender_button));
				male.setTextColor(Color.parseColor("#2DBCDE"));
				female.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_gender_button));
				female.setTextColor(Color.parseColor("#D5D5D5"));

				_gender = "남자";
				Toast.makeText(getApplicationContext(), "남자", Toast.LENGTH_SHORT).show();
			}
		});


		zipCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				Intent intent = new Intent(RegistFromNaver.this, PopupAddressActivity.class);
				startActivityForResult(intent, 1);*/
			}
		});
		RegistFromNaver.SkinTypeListener skinListener = new RegistFromNaver.SkinTypeListener();
		gunsung.setOnClickListener(skinListener);
		jungsung.setOnClickListener(skinListener);
		gisung.setOnClickListener(skinListener);
		bokhapsung.setOnClickListener(skinListener);
		mingamsung.setOnClickListener(skinListener);
		morum.setOnClickListener(skinListener);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//todo 회원가입 취소 재차 확인 후 종료
				onBackPressed();
			}
		});
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkSubmit() == false) {
					return;
				}
				String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
				if (!Network.equals("")) {    //  네트워크 확인
					Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
					return;
				}

				Map<String, String> para = new HashMap<>();
				para.put("email", _email);                // 이메일
				para.put("nickName", _nickName);        // 닉네임
				para.put("password", _password);        // 비밀번호
				para.put("birthday", _year + "-" + _month + "-" + _day);        // 생년월일
				para.put("address1", _address1);        // 우편번호
				para.put("address2", _address2);        // 나머지 주소
				para.put("skinType", _skinType);        // 피부타
				para.put("gender", _gender);            // 성별

				JSONObject json = new JSONObject(para);


				final ProgressDialog dialog = ProgressDialog.show(MyApp.appInstance, "서버와 접속중.. 잠시만 기달려주세요", "회원가입중...", false);

				final RequestQueue requestQueue = Volley.newRequestQueue(MyApp.appInstance);

				JsonObjectRequest postJsonRequset = new JsonObjectRequest
						(Request.Method.POST, ServerInterface.regist, json, new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								dialog.dismiss();
								Log.d("response", response.toString());
								try {
									String date = response.toString();
									JSONObject jsonObject = new JSONObject(date);
									if (jsonObject.getString("rsltCode").equals("0")) {
										Toast.makeText(MyApp.appInstance, "회원가입 성공!", Toast.LENGTH_SHORT).show();
										Intent intent = new Intent(RegistFromNaver.this, MainActivity.class);
										startActivity(intent);
										finish();
									} else {
										Toast.makeText(MyApp.appInstance, "다시 확인해주세요.", Toast.LENGTH_SHORT).show();
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

		curAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
				if (!Network.equals("")) {    //  네트워크 확인
					Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
					return;
				}
				if (ActivityCompat.checkSelfPermission(RegistFromNaver.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegistFromNaver.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(getApplicationContext(), "위치정보 권한을 확인해주세요.", Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					Geocoder geocoder = new Geocoder(RegistFromNaver.this, Locale.KOREA);
					LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					//Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

					if (location == null) {
						AlertDialog.Builder alert = new AlertDialog.Builder(RegistFromNaver.this);
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
					List<Address> addresses;

					addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
					_address1 = addresses.get(0).getPostalCode();
					if (_address1 != null) {
						_address1 = _address1.replaceFirst("대한민국 ", "");
					}
					_address2 = addresses.get(0).getAddressLine(0);
					if (_address2 != null) {
						_address2 = _address2.replaceFirst("대한민국 ", "");
					}
					address1.setText(_address1);
					address2.setText(_address2);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

	private void setSecondRegistInfo() {

	}

	private boolean checkSubmit() {

		if (_gender.equals("") || _year.equals("") || _month.equals("") || _day.equals("") || _skinType.equals("")) {
			Toast.makeText(getApplicationContext(), "모든칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private class SkinTypeListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Button bt = (Button) v;
			switch ((String) bt.getTag()) {
				case "0":
					gunsung.setEnabled(false);
					jungsung.setEnabled(true);
					gisung.setEnabled(true);
					bokhapsung.setEnabled(true);
					mingamsung.setEnabled(true);
					morum.setEnabled(true);

					gunsung.setTextColor(Color.parseColor("#FFFFFF"));
					jungsung.setTextColor(Color.parseColor("#6E6E6E"));
					gisung.setTextColor(Color.parseColor("#6E6E6E"));
					bokhapsung.setTextColor(Color.parseColor("#6E6E6E"));
					mingamsung.setTextColor(Color.parseColor("#6E6E6E"));
					morum.setTextColor(Color.parseColor("#6E6E6E"));

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));

					_skinType = "건성";
					break;
				case "1":
					gunsung.setEnabled(true);
					jungsung.setEnabled(false);
					gisung.setEnabled(true);
					bokhapsung.setEnabled(true);
					mingamsung.setEnabled(true);
					morum.setEnabled(true);

					gunsung.setTextColor(Color.parseColor("#6E6E6E"));
					jungsung.setTextColor(Color.parseColor("#FFFFFF"));
					gisung.setTextColor(Color.parseColor("#6E6E6E"));
					bokhapsung.setTextColor(Color.parseColor("#6E6E6E"));
					mingamsung.setTextColor(Color.parseColor("#6E6E6E"));
					morum.setTextColor(Color.parseColor("#6E6E6E"));

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));

					_skinType = "중성";
					break;
				case "2":
					gunsung.setEnabled(true);
					jungsung.setEnabled(true);
					gisung.setEnabled(false);
					bokhapsung.setEnabled(true);
					mingamsung.setEnabled(true);
					morum.setEnabled(true);

					gunsung.setTextColor(Color.parseColor("#6E6E6E"));
					jungsung.setTextColor(Color.parseColor("#6E6E6E"));
					gisung.setTextColor(Color.parseColor("#FFFFFF"));
					bokhapsung.setTextColor(Color.parseColor("#6E6E6E"));
					mingamsung.setTextColor(Color.parseColor("#6E6E6E"));
					morum.setTextColor(Color.parseColor("#6E6E6E"));

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));

					_skinType = "지성";
					break;
				case "3":
					gunsung.setEnabled(true);
					jungsung.setEnabled(true);
					gisung.setEnabled(true);
					bokhapsung.setEnabled(false);
					mingamsung.setEnabled(true);
					morum.setEnabled(true);

					gunsung.setTextColor(Color.parseColor("#6E6E6E"));
					jungsung.setTextColor(Color.parseColor("#6E6E6E"));
					gisung.setTextColor(Color.parseColor("#6E6E6E"));
					bokhapsung.setTextColor(Color.parseColor("#FFFFFF"));
					mingamsung.setTextColor(Color.parseColor("#6E6E6E"));
					morum.setTextColor(Color.parseColor("#6E6E6E"));

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));

					_skinType = "복합성";
					break;
				case "4":
					gunsung.setEnabled(true);
					jungsung.setEnabled(true);
					gisung.setEnabled(true);
					bokhapsung.setEnabled(true);
					mingamsung.setEnabled(false);
					morum.setEnabled(true);

					gunsung.setTextColor(Color.parseColor("#6E6E6E"));
					jungsung.setTextColor(Color.parseColor("#6E6E6E"));
					gisung.setTextColor(Color.parseColor("#6E6E6E"));
					bokhapsung.setTextColor(Color.parseColor("#6E6E6E"));
					mingamsung.setTextColor(Color.parseColor("#FFFFFF"));
					morum.setTextColor(Color.parseColor("#6E6E6E"));

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));

					_skinType = "민감성";
					break;
				case "5":
					gunsung.setEnabled(true);
					jungsung.setEnabled(true);
					gisung.setEnabled(true);
					bokhapsung.setEnabled(true);
					mingamsung.setEnabled(true);
					morum.setEnabled(false);

					gunsung.setTextColor(Color.parseColor("#6E6E6E"));
					jungsung.setTextColor(Color.parseColor("#6E6E6E"));
					gisung.setTextColor(Color.parseColor("#6E6E6E"));
					bokhapsung.setTextColor(Color.parseColor("#6E6E6E"));
					mingamsung.setTextColor(Color.parseColor("#6E6E6E"));
					morum.setTextColor(Color.parseColor("#FFFFFF"));

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_skintype_button));

					_skinType = "모름";
					Toast.makeText(getApplicationContext(), "모름", Toast.LENGTH_SHORT).show();
					break;
			}
		}
	}

	public void onClickDate(View v) {
		Calendar c = Calendar.getInstance();
		int nYear = 2000;
		int nMon = c.get(Calendar.MONTH);
		int nDay = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog.OnDateSetListener mDateSetListener =
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear,
										  int dayOfMonth) {
						String strDate = String.valueOf(year) + "-";
						strDate += String.valueOf(monthOfYear + 1) + "-";
						strDate += String.valueOf(dayOfMonth);

						_year = String.valueOf(year);
						_month = (monthOfYear + 1) < 10 ? "0" + String.valueOf(monthOfYear + 1) : String.valueOf(monthOfYear + 1);
						_day = (dayOfMonth) < 10 ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);

						EditText et = findViewById(R.id.etBirthday);
						et.setText(_year + "-" + _month + "-" + _day);

					}
				};

		DatePickerDialog oDialog = new DatePickerDialog(this,
				android.R.style.Theme_DeviceDefault_Light_Dialog,
				mDateSetListener, nYear, nMon, nDay);
		oDialog.show();
	}
}
