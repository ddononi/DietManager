package kr.co.diet;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 회원가입 처리 엑티비티
 */
public class JoinActivity extends BaseActivity {
	private EditText nameEt; 		// 이름
	private EditText agelEt;			// 나이
	private EditText heightEt;		// 키
	private EditText weightEt;		// 체중
	
	private Spinner sexSpinner;	// 성별 스피너
	private Spinner activeSpinner;	// 활동량 스피너
	
	private String sex;
	private String active;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_activity);

		initLayout();
		
		Button btn = (Button) findViewById(R.id.join_btn);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getFormValue()) { // 폼값을 입력했을때만 전송
					// 저장 입력했을경우 유저 정보를 디비에 저장한다.
					saveUserinfo();
					// 사용자 정보를 입력했다고 공유환경설정에 저장하여
					// 다음실행시 다시 사용자정보를 받지 않도록 한다.
			        SharedPreferences settings = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
			        SharedPreferences.Editor editor = settings.edit();
			        editor.putBoolean(IS_JOINED, true);
			        editor.commit();
					startActivity(new Intent(JoinActivity.this, MainMenuActivity.class));
					finish();
				}
			}
		});

	}
	
	/**
	 * 레이아웃 설정
	 */
	private void initLayout(){
	     sexSpinner = (Spinner) findViewById(R.id.sex_spinner);
	    final ArrayAdapter sexAdapter = ArrayAdapter.createFromResource(
	             this, R.array.sex_chocie, android.R.layout.simple_spinner_item);
	    sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     sexSpinner.setAdapter(sexAdapter);
	     
	     activeSpinner = (Spinner) findViewById(R.id.active_spinner);
	    final  ArrayAdapter activeAdater = ArrayAdapter.createFromResource(
	             this, R.array.active_chocie, android.R.layout.simple_spinner_item);
	     activeAdater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     activeSpinner.setAdapter(activeAdater);	     
	     // 스피너 이벤트 설정
	     activeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v,
					int pos, long arg3) {
				active = (String)activeAdater.getItem(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	     
	    sexSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View v,
						int pos, long arg3) {
				 	sex = (String)sexAdapter.getItem(pos);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});     
	     
		// EditTextd에서 각각의 값 가져오기
		nameEt = (EditText) findViewById(R.id.input_name);
		agelEt = (EditText) findViewById(R.id.age_input);
		heightEt = (EditText) findViewById(R.id.height_input);
		weightEt = (EditText) findViewById(R.id.weight_input);	     
	}

	/**
	 * 디비에 회원 저장 처리
	 */
	protected void saveUserinfo() {
		// 디비 열기
		SQLiteOpenHelper dbhelper = new DbHelper(this);
		// 쓰기 모드로
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		// 정보를 담을 컨텐츠벨류
		ContentValues cv = new ContentValues();
		cv.put("name", nameEt.getText().toString());
		cv.put("age", Integer.valueOf(agelEt.getText().toString()));
		cv.put("sex", sex);
		cv.put("height", Integer.valueOf(heightEt.getText().toString()));
		cv.put("weight", Integer.valueOf(weightEt.getText().toString()));
		cv.put("active", active);
		// 디비에 인서트
		db.insert("user_info", null, cv);
		db.close();
		dbhelper.close();
	}

	/**
	 * form 값을 변수에 채워 넣어주며 입력여부 처리
	 * 
	 * @return 입력여부 boolean
	 */
	private boolean getFormValue() {

		// 내용이 채워지지 않았으면 false 처리
		if (TextUtils.isEmpty(nameEt.getText())) {
			Toast.makeText(JoinActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (TextUtils.isEmpty(agelEt.getText())) {
			Toast.makeText(JoinActivity.this, "나이 입력해주세요", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (!TextUtils.isDigitsOnly(heightEt.getText())) {
			Toast.makeText(JoinActivity.this, "신장 입력해주세요", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (!TextUtils.isDigitsOnly(weightEt.getText())) {
			Toast.makeText(JoinActivity.this, "체중 입력해주세요", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		
		if (TextUtils.isEmpty(sex)) {
			Toast.makeText(JoinActivity.this, "성별을 입력해주세요", Toast.LENGTH_SHORT)
					.show();
			return false;
		}	
		
		if (TextUtils.isEmpty(active)) {
			Toast.makeText(JoinActivity.this, "활동량을 입력해주세요", Toast.LENGTH_SHORT)
					.show();
			return false;
		}		


		return true;

	}


}
