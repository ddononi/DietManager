package kr.co.diet;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.diet.dao.DayCalData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �� Į�θ� ��� ��Ƽ��Ƽ
 */
public class MyCalActivity extends ConstantActivity implements OnItemSelectedListener {
	// ���ý�Ʈ
	private Context mContext;	
	// elements
	private TextView myCalTv;			// �� ���� �䱸 Į�θ� �ؽ�Ʈ��
	private TextView intakeCalTv;		// ���� Į�θ�
	// Į�θ� ���� �迭
	private String[] calFoodArray; 

	private DayCalData dayCalData = new DayCalData();
	// Į�θ� 
	private double basis;					// ���� ��緮		
	private double active;					// Ȱ����緮 ���ϱ�
	private double foodEnergy;			// ��ǰ �̿��� ���� ������
	private double dayEnergy;			// ���� �Ϸ翡 �ʿ��� ������ ���ϱ�
	
	// ��ħ Į�θ�
	private int breakfastCalory = 0;
	// ���� Į�θ�
	private int lunchCalory = 0;
	// ���� Į�θ�
	private int dinnerCalory = 0;
	// ���� Į�θ�
	private int snackCalory = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_cal_layout);
		mContext = this;
		// ���� Į�θ� xml array �迭
		calFoodArray = getResources().getStringArray(R.array.food_calories);
		initLayout();		// ���̾ƿ� �ʱ�ȭ
		
		loadMyCalInfo();	// �� Į�θ� ����
		
		dayCaloriesCompute();
	}

	/*
	 * Layout
	 */
	private void initLayout() {

		myCalTv = (TextView)findViewById(R.id.my_day_cal);
		intakeCalTv = (TextView)findViewById(R.id.intake_cal);
		TextView dateTv = (TextView)findViewById(R.id.date);
		// ���� ��¥�� 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy�� MM�� dd��");	
		dateTv.setText(sdf.format(new Date()));
		// �Ļ� ���� ó�� ���ǳ�
		Spinner breakfastSp = (Spinner)findViewById(R.id.breakfast_spinner);
		Spinner lunchSp = (Spinner)findViewById(R.id.lunch_spinner);
		Spinner dinnerSp = (Spinner)findViewById(R.id.dinner_spinner);
		Spinner snackSp = (Spinner)findViewById(R.id.snack_spinner);
		
		breakfastSp.setOnItemSelectedListener(this);		
		lunchSp.setOnItemSelectedListener(this);
		dinnerSp.setOnItemSelectedListener(this);
		snackSp.setOnItemSelectedListener(this);
	}
	
	private void loadMyCalInfo(){
		
	}

	/**
	 * ���� Į�θ� ���
	 */
	private void dayCaloriesCompute() {

		double stature = myInfoData.getHeight();
		double vStature = 0;
		
		double weight = myInfoData.getWeight();		// ü��
		double myKcal = 0.6;

		
		if (stature >= 160) {
			vStature = (stature - 100) * 0.9;
		} else if (stature >= 150 || stature < 160) {
			vStature = ((stature - 150) * 0.5) + 50;
		} else if (stature < 150) {
			vStature = (stature - 100);
		}
		
		//���ʴ�緮
		if(myInfoData.getSex().equals("��")){
		  basis = weight * 1.0 * 24;
		}  else {
	    	basis = weight * 0.9 * 24;
		}		

		  //Ȱ����緮 ���ϱ�
		active = basis * myKcal;
		  //��ǰ �̿��� ���� ������	���ϱ�
		foodEnergy = ((basis + active) / 0.9) * 0.1;
		  //���� �Ϸ翡 �ʿ��� ������ ���ϱ�
		dayEnergy = basis + active + foodEnergy;		

		myCalTv.setText(String.valueOf((int)dayEnergy) +"Kcal");
	}

	/**
	 * ���ǳ� �� ����� Į�θ� ���� �����´�.
	 */
	@Override
	public void onItemSelected(AdapterView<?> av, View v, int pos,
			long arg3) {
		switch(av.getId()){
		// ��ħ ���ǳ�		
		case R.id.breakfast_spinner : 
			if(calFoodArray[pos].equals("����")){
				dayCalData.setBreakfast(0);
				break;
			}
			// Į�θ��� �̾ƿ´�.
			String cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf("K"));
			dayCalData.setBreakfast(Integer.valueOf(cal));
			break;
		//	���� ���ǳ�
		case R.id.lunch_spinner : 
			if(calFoodArray[pos].equals("����")){
				dayCalData.setLunch(0);
				break;
			}
			// Į�θ��� �̾ƿ´�.
			cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf("K"));
			dayCalData.setLunch(Integer.valueOf(cal));
			break;	
		// ���� ���ǳ�	
		case R.id.dinner_spinner : 
			if(calFoodArray[pos].equals("����")){
				dayCalData.setDinner(0);
				break;
			}
			// Į�θ��� �̾ƿ´�.
			cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf("K"));
			dayCalData.setDinner(Integer.valueOf(cal));
			break;	
		// ���� ���ǳ�	
		case R.id.snack_spinner : 
			if(calFoodArray[pos].equals("����")){
				dayCalData.setSnake(0);
				break;
			}
			// Į�θ��� �̾ƿ´�.
			cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf("K"));
			dayCalData.setSnake(Integer.valueOf(cal));
			break;	
		}
		
		int intakeCal =  dayCalData.getBreakfast() + dayCalData.getDinner() + 
					dayCalData.getLunch() +dayCalData.getSnake();
		intakeCalTv.setText(intakeCal +"Kcal");

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
	
	
	
	/**
	 * ���� ó��
	 * @param v
	 */
	public void mOnClick(View v){
		DbHelper db = new DbHelper(this);
		try{
			if( db.insertDayCalories(dayCalData) > 0){
				Toast.makeText(this, "Į�θ� ������ ����Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this, MyCalListActivity.class);
				startActivity(intent);
				finish();
			}
		}finally{
			db.close();
		}

	}

}
