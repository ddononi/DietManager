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
 * ȸ������ ó�� ��Ƽ��Ƽ
 */
public class JoinActivity extends BaseActivity {
	private EditText nameEt; 		// �̸�
	private EditText agelEt;			// ����
	private EditText heightEt;		// Ű
	private EditText weightEt;		// ü��
	
	private Spinner sexSpinner;	// ���� ���ǳ�
	private Spinner activeSpinner;	// Ȱ���� ���ǳ�
	
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
				if (getFormValue()) { // ������ �Է��������� ����
					// ���� �Է�������� ���� ������ ��� �����Ѵ�.
					saveUserinfo();
					// ����� ������ �Է��ߴٰ� ����ȯ�漳���� �����Ͽ�
					// ��������� �ٽ� ����������� ���� �ʵ��� �Ѵ�.
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
	 * ���̾ƿ� ����
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
	     // ���ǳ� �̺�Ʈ ����
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
	     
		// EditTextd���� ������ �� ��������
		nameEt = (EditText) findViewById(R.id.input_name);
		agelEt = (EditText) findViewById(R.id.age_input);
		heightEt = (EditText) findViewById(R.id.height_input);
		weightEt = (EditText) findViewById(R.id.weight_input);	     
	}

	/**
	 * ��� ȸ�� ���� ó��
	 */
	protected void saveUserinfo() {
		// ��� ����
		SQLiteOpenHelper dbhelper = new DbHelper(this);
		// ���� ����
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		// ������ ���� ����������
		ContentValues cv = new ContentValues();
		cv.put("name", nameEt.getText().toString());
		cv.put("age", Integer.valueOf(agelEt.getText().toString()));
		cv.put("sex", sex);
		cv.put("height", Integer.valueOf(heightEt.getText().toString()));
		cv.put("weight", Integer.valueOf(weightEt.getText().toString()));
		cv.put("active", active);
		// ��� �μ�Ʈ
		db.insert("user_info", null, cv);
		db.close();
		dbhelper.close();
	}

	/**
	 * form ���� ������ ä�� �־��ָ� �Է¿��� ó��
	 * 
	 * @return �Է¿��� boolean
	 */
	private boolean getFormValue() {

		// ������ ä������ �ʾ����� false ó��
		if (TextUtils.isEmpty(nameEt.getText())) {
			Toast.makeText(JoinActivity.this, "�̸��� �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (TextUtils.isEmpty(agelEt.getText())) {
			Toast.makeText(JoinActivity.this, "���� �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (!TextUtils.isDigitsOnly(heightEt.getText())) {
			Toast.makeText(JoinActivity.this, "���� �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (!TextUtils.isDigitsOnly(weightEt.getText())) {
			Toast.makeText(JoinActivity.this, "ü�� �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		
		if (TextUtils.isEmpty(sex)) {
			Toast.makeText(JoinActivity.this, "������ �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}	
		
		if (TextUtils.isEmpty(active)) {
			Toast.makeText(JoinActivity.this, "Ȱ������ �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}		


		return true;

	}


}
