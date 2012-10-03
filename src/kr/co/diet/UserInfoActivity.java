package kr.co.diet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * ����� ���� ��Ƽ��Ƽ
 */
public class UserInfoActivity extends ConstantActivity {
	private EditText nameEt; 		// �̸�
	private EditText agelEt;		// ����
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
		setContentView(R.layout.user_info_layout);

		initLayout();
		
		Button btn = (Button) findViewById(R.id.modi_btn);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getFormValue()) { // ������ �Է��������� ����
					// ���� �Է�������� ���� ������ ��� �����Ѵ�.
					modifyUserinfo();
					myInfoData = null;	//   ����������� ���� �޾ƿ´�.
					startActivity(new Intent(UserInfoActivity.this, MainMenuActivity.class));
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
	    final ArrayAdapter<?> sexAdapter = ArrayAdapter.createFromResource(
	             this, R.array.sex_chocie, android.R.layout.simple_spinner_item);
	    sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     sexSpinner.setAdapter(sexAdapter);
	     
	     activeSpinner = (Spinner) findViewById(R.id.active_spinner);
	    final  ArrayAdapter<?> activeAdater = ArrayAdapter.createFromResource(
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
		//	�������� ä���
		fillUserinfo();
	}

	/**
	 * ��� ȸ�� ���� ó��
	 */
	protected void modifyUserinfo() {
		// ��� ����
		SQLiteOpenHelper dbhelper = new DbHelper(this);
		// ���� ����
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		// ���� ���� ������ ������ �ٽ� �־��ش�.
		db.delete("user_info", null, null);
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
	 * ��� ȸ�� ���� ó��
	 */
	protected void fillUserinfo() {
		// ��� ����
		SQLiteOpenHelper dbhelper = new DbHelper(this);
		// ���� ����
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.query("user_info", null, null, null, null, null, null);
		// Į���� ������
		// �� Į�� ����  �ش� �ʵ尪�� ���� ���ش�.
		if(cursor.moveToFirst()){
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));			
			int age = cursor.getInt(cursor.getColumnIndex("age"));
			int height = cursor.getInt(cursor.getColumnIndex("height"));
			int weight = cursor.getInt(cursor.getColumnIndex("weight"));
			String active = cursor.getString(cursor.getColumnIndex("active"));					
			// ������ ä���ش�.
			nameEt.setText(name);
			agelEt.setText(String.valueOf(age));
			heightEt.setText(String.valueOf(height));
			weightEt.setText(String.valueOf(weight));
			
			// ���� ����
			String[] sexArray = this.getResources().getStringArray(R.array.sex_chocie);
			for(int i=0; i <sexArray.length; i++){
				if(sexArray[i].equals(sex)){
					sexSpinner.setSelection(i);
					break;
				}
			}
			
			// Ȱ�� ����
			String[] activeArray = this.getResources().getStringArray(R.array.active_chocie);
			for(int i=0; i <activeArray.length; i++){
				if(activeArray[i].equals(active)){
					activeSpinner.setSelection(i);
					break;
				}
			}			
		}
		// ��� �ݾ��ش�.
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
			Toast.makeText(UserInfoActivity.this, "�̸��� �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (TextUtils.isEmpty(agelEt.getText())) {
			Toast.makeText(UserInfoActivity.this, "���� �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (!TextUtils.isDigitsOnly(heightEt.getText())) {
			Toast.makeText(UserInfoActivity.this, "���� �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (!TextUtils.isDigitsOnly(weightEt.getText())) {
			Toast.makeText(UserInfoActivity.this, "ü�� �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		
		if (TextUtils.isEmpty(sex)) {
			Toast.makeText(UserInfoActivity.this, "������ �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}	
		
		if (TextUtils.isEmpty(active)) {
			Toast.makeText(UserInfoActivity.this, "Ȱ������ �Է����ּ���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}		


		return true;

	}


}
