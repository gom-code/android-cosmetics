package com.example.project;


import android.Manifest;
import android.app.Activity;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.app.Key;
import com.example.project.app.MyApp;
import com.example.project.app.ServerInterface;
import com.example.project.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistActivity extends BaseActivity {
	private LinearLayout registLayout;

	private EditText email;
	private EditText nickName;
	private EditText password;
	private EditText password2;
	private Button nextButton;
	private TextView checkedEmail;
	private TextView checkedNickName;

	private String _email = "";
	private String _nickName = "";
	private String _password = "";

	// 서버로 전송해서 값이 있는지 확인용
	private boolean checkEmail = false;
	private boolean checkNickName = false;

	/*
	회원가입 2단계
	 */
	/* 사용자에게 받은 */
	private TextView email2;
	private Button female;
	private Button male;
	private Spinner job;
	//	private Spinner year;
//	private Spinner month;
//	private Spinner day;
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
	private String _job = "";
	private String _address1 = "";
	private String _address2 = "";
	private String _skinType = "";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.default_cyan));  // 상태바 색

		MyApp.appInstance = this;
		registLayout = findViewById(R.id.registLayout);

		firstRegistOpen();

	}
	@Override
	protected void onRestart() {
		super.onRestart();  // Always call the superclass method first
		MyApp.appInstance =this;
	}

	private void firstRegistOpen() {
		firstRegistFindView();
		setFirstRegistListener();

		email.setText(_email);
		nickName.setText(_nickName);
		password.setText(_password);

		// ↓ 사용 이유 ↓
		// 자동으로 키보드가 올라오는 경우가 있는데 이를 방지하기 위해
		// 다른 View에 focus를 줘서 키보드가 올라오지 않도록 많이들 쓰곤 합니다.

		// getCurrentFocus()라는 현재 Focus를 받는 View를 알려주는 메서드
		// btn1.setFocusableInTouchMode(true);	// 포커스 등록을 해 줘야 한다.
		// btn1.requestFocus();	// 강제 포커스 들어간다.
	}

	private void firstRegistFindView() {
		email = findViewById(R.id.registEmail);
		nickName = findViewById(R.id.registNickName);
		password = findViewById(R.id.password1);
		password2 = findViewById(R.id.password2);


		checkedEmail = findViewById(R.id.checkedEmail);
		checkedNickName = findViewById(R.id.checkedNickName);

		nextButton = findViewById(R.id.nextButton);
	}

	private boolean isPerfect() {
		if (email.getText().toString().trim().isEmpty()) {
			Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			if (!validateEmail(email.getText().toString().trim())) {
				Toast.makeText(getApplicationContext(), "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		if (nickName.getText().toString().isEmpty()) {
			Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.getText().toString().isEmpty()) {
			Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			if (password.length() >= 4) {
				if (validatePassword(password.getText().toString().trim())) {
					Toast.makeText(getApplicationContext(), "비밀번호 형식을 지켜주세요.", Toast.LENGTH_SHORT).show();
				}
			}
		}
		if (password2.getText().toString().isEmpty()) {
			Toast.makeText(getApplicationContext(), "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (password.getText().toString().equals(password2.getText().toString())) {
		} else {
			Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	// 이메일 정규식
	public final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	// 이메일 검사
	public boolean validateEmail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	// 비밀번호 정규식
	public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");  // 정규식 (영문(대소문자 구분), 숫자, 특수문자 조합, 9~12자리)

	// 비밀번호 검사
	public static boolean validatePassword(String pwStr) {
		Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
		return !matcher.matches();    // matches()는 알파벳+숫자로 이루어진 길이 9~12자의 스트링일 경우 true를 리턴하는 함수
	}

	private void setFirstRegistListener() {

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPerfect() == false) {
					return;
				} else {
					setFirstRegistInfo();    // 정보 넘김

					View viewInflater;
					registLayout.removeAllViews();
					viewInflater = getLayoutInflater().inflate(R.layout.regist_second, registLayout, false);
					registLayout.addView(viewInflater);

					secondRegistOpen();        // 바뀐 뷰에 대한 정보
				}
			}
		});

		email.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			//EditText에 변화가 있을 때
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
				if (!Network.equals("")) {    //  네트워크 확인
					Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
					return;
				}
				String __email = email.getText().toString();
				if (__email.equals("")) {
					checkedEmail.setText("");
					checkEmail = false;
				}
				// 이메일 형식 체크
				else if (validateEmail(__email)) {
					connect taker = new connect();

					String decodeStr = taker.requestEmail(__email);

					Log.i("dar", "" + decodeStr);

					if (decodeStr.equals(Key.accept)) {
						checkedEmail.setText("가능한 이메일입니다");
						checkEmail = true;
					} else {
						checkedEmail.setText("중복된 이메일입니다");
						checkEmail = false;
					}
				} else {
					checkedEmail.setText("불가능한 이메일입니다.");
					checkEmail = false;
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


		password.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
				if (!Network.equals("")) {    //  네트워크 확인
					Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
					return;
				}
				_password = password.getText().toString();
				if (_password.length() >= 4) {
					if (validatePassword(_password)) {
						Toast.makeText(getApplicationContext(), "비밀번호 형식을 지켜주세요.", Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
		//addTextChangeListener는 EditText에 추가적인 글자 변화가 있는지 항상 듣고 있는 리스너입니다.
		//TextWatcher는 인터페이스로써 3단계(글자변화 전, 중, 후)로 구성된 글자 변화의 시점의 메서드를 갖고있습니다.
		nickName.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {     //글자 변화되기 전

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {    // 글자 변화되는 중
				String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
				if (!Network.equals("")) {    //  네트워크 확인
					Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
					return;
				}
				String __nickName = nickName.getText().toString();

				if (__nickName.equals("")) {
					checkedNickName.setText("");
					checkNickName = false;
				} else {
					connect taker = new connect();

					String decodeStr = taker.requestNickName(__nickName);

					Log.i("dar", "" + decodeStr);
					if (decodeStr.equals("accept")) {
						checkedNickName.setText("가능한 닉네임입니다");
						checkNickName = true;
					} else {
						checkedNickName.setText("중복된 닉네임입니다");
						checkNickName = false;
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {        // 글자 변화 후

			}
		});
	}

	private void secondRegistOpen() {
		secondRegistFindView();    // 다음 뷰를 등록

		//setBirthdaySpinner();
		setSecondRegistListener();    //리스너 등록

		email2.setText(_email);
		female.setEnabled(false);    	//여자

		male.setEnabled(true);			// 남자
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
		female = findViewById(R.id.female);
		male = findViewById(R.id.male);
		job = findViewById(R.id.job);
		job.setVisibility(View.GONE);    // 직업 뷰 감추기
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
				male.setEnabled(false);    // 객체 비활성화
				female.setEnabled(true);    // 객체 활성화
				male.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_regist_gender_button));
				male.setTextColor(Color.parseColor("#2DBCDE"));
				female.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_regist_gender_button));
				female.setTextColor(Color.parseColor("#D5D5D5"));

				_gender = "남자";
				Toast.makeText(getApplicationContext(), "남자", Toast.LENGTH_SHORT).show();
			}
		});
		/*job.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_job = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});*/
/*
		zipCode.setOnClickListener(new View.OnClickListener() {	// 우편 번호
			@Override
			public void onClick(View v) {

				//requestWindowFeature(Window.FEATURE_NO_TITLE);	// 툴바 제거방식 1
				//setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);	// 툴바 제거방식 2

				//Intent intent = new Intent(RegistActivity.this, PopupAddressActivity.class);
				//startActivityForResult(intent, 1);
			}
		});
*/
		SkinTypeListener skinListener = new SkinTypeListener();
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
				onBackPressed(); // 전 액티비티로 전환(로그인 화면)
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View viewInflater;
				registLayout.removeAllViews();
				viewInflater = getLayoutInflater().inflate(R.layout.regist_first, registLayout, false);
				registLayout.addView(viewInflater);

				firstRegistOpen();
			}
		});

		submitButton.setOnClickListener(new View.OnClickListener() {		// 확인(회원가입 등록)
			@Override
			public void onClick(View v) {
				if (checkSubmit() == false) {
					return;
				}
				String Network = NetworkUtil.getConnectivityStatusString(MyApp.appInstance);
				if(!Network.equals("")){	//  네트워크 확인
					Toast.makeText(getApplicationContext(), Network, Toast.LENGTH_SHORT).show();
					return;
				}
				Map<String, String> para = new HashMap<>();
				para.put("email", _email);				// 이메일
				para.put("nickName", _nickName);		// 닉네임
				para.put("password", _password);		// 비밀번호
				para.put("birthday", _year + "-" + _month + "-" + _day);		// 생년월일
				para.put("address1", _address1);		// 우편번호
				para.put("address2", _address2);		// 나머지 주소
				para.put("skinType", _skinType);		// 피부타
				para.put("gender", _gender);			// 성별

				JSONObject json = new JSONObject(para);


				final ProgressDialog dialog = ProgressDialog.show(MyApp.appInstance,"서버와 접속중.. 잠시만 기달려주세요","회원가입중...",false);

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
									if(jsonObject.getString("rsltCode").equals("0")){
										Toast.makeText(MyApp.appInstance, "회원가입 성공!", Toast.LENGTH_SHORT).show();
										onBackPressed();
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
				// 퍼미션 체크 ActivityCompat.checkSelfPermission(Context, String)
				// 퍼미션 요청 ActivityCompat.requestPermissions(Activity, String[], int)
				// 퍼미션 요청 콜백함수 ActivityCompat.OnRequestPermissionsResultCallback
				// 사용자 권한 요청
				if (ActivityCompat.checkSelfPermission(RegistActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegistActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(getApplicationContext(), "위치정보 권한을 확인해주세요.", Toast.LENGTH_SHORT).show();
					// 퍼미션이 허용되어 있는 상태라면 PackageManager.PERMISSION_GRANTED를 리턴하고
					// 거부 되어 있는 상태라면  PackageManager.PERMISSION_DENIED를 리턴합니다.
				}
				GpsTracker gpsTracker = new GpsTracker(MyApp.appInstance);
				Geocoder geocoder = new Geocoder(MyApp.appInstance, Locale.KOREA);

				if (gpsTracker.getLatitude() == 0 | gpsTracker.getLongitude() == 0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(RegistActivity.this);
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

				//Toast.makeText(MyApp.appInstance, "현재위치 \n위도 " + gpsTracker.getLatitude() + "\n경도 " + gpsTracker.getLongitude(), Toast.LENGTH_LONG).show();

				List<Address> addresses;

				try {
					addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 7);
					_address1 = addresses.get(0).getPostalCode();
					if (_address1 != null) {
						_address1 = _address1.replaceFirst("대한민국 ", "");	// 문자 치환
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

					// getDrawable
					// drawable.xml에 있는 값에 접급하기 위해 아래의 코드를 사용
					// ContextCompat
					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));

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

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));

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

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));

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

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));

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

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));

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

					gunsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					jungsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					gisung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					bokhapsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					mingamsung.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_selected_regist_skintype_button));
					morum.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.selected_regist_skintype_button));

					_skinType = "모름";
					Toast.makeText(getApplicationContext(), "모름", Toast.LENGTH_SHORT).show();
					break;
			}
		}
	}
	// 생년월일
	public void onClickDate(View v){
		Calendar c = Calendar.getInstance();
		int nYear = 2000;
		int nMon = c.get(Calendar.MONTH);
		int nDay = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog.OnDateSetListener mDateSetListener =
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear,
										  int dayOfMonth) {
						String strDate = String.valueOf(year) + "-";
						strDate += String.valueOf(monthOfYear+1) + "-";
						strDate += String.valueOf(dayOfMonth);

						_year = String.valueOf(year);
						_month = (monthOfYear+1)<10?"0"+String.valueOf(monthOfYear+1):String.valueOf(monthOfYear+1);
						_day = (dayOfMonth)<10?"0"+String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);

						EditText et = findViewById(R.id.etBirthday);
						et.setText(_year+"-"+_month+"-"+_day);

					}
				};

		DatePickerDialog oDialog = new DatePickerDialog(this,
				android.R.style.Theme_DeviceDefault_Light_Dialog,
				mDateSetListener, nYear, nMon, nDay);
		oDialog.show();
	}
	private void setFirstRegistInfo() {
		_email = email.getText().toString();
		_password = password.getText().toString();
		_nickName = nickName.getText().toString();
	}
	private boolean checkSubmit() {
		if (!checkEmail || _email.equals("")) {
			Toast.makeText(getApplicationContext(), "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!checkNickName || _nickName.equals("")) {
			Toast.makeText(getApplicationContext(), "닉네임을 확인해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (_password.equals("") || _gender.equals("") || _year.equals("") || _month.equals("") || _day.equals("") || _skinType.equals("")) {
			Toast.makeText(getApplicationContext(), "모든칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

}