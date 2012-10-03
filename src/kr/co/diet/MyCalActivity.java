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
 * 내 칼로리 계산 엑티비티
 */
public class MyCalActivity extends ConstantActivity implements OnItemSelectedListener {
	// 컨택스트
	private Context mContext;	
	// elements
	private TextView myCalTv;			// 내 일일 요구 칼로리 텍스트뷰
	private TextView intakeCalTv;		// 섭취 칼로리
	// 칼로리 정보 배열
	private String[] calFoodArray; 

	private DayCalData dayCalData = new DayCalData();
	// 칼로리 
	private double basis;					// 기초 대사량		
	private double active;					// 활동대사량 구하기
	private double foodEnergy;			// 식품 이용을 위한 에너지
	private double dayEnergy;			// 나의 하루에 필요한 에너지 구하기
	
	// 아침 칼로리
	private int breakfastCalory = 0;
	// 점심 칼로리
	private int lunchCalory = 0;
	// 저녁 칼로리
	private int dinnerCalory = 0;
	// 간식 칼로리
	private int snackCalory = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_cal_layout);
		mContext = this;
		// 음식 칼로리 xml array 배열
		calFoodArray = getResources().getStringArray(R.array.food_calories);
		initLayout();		// 레이아웃 초기화
		
		loadMyCalInfo();	// 내 칼로리 정보
		
		dayCaloriesCompute();
	}

	/*
	 * Layout
	 */
	private void initLayout() {

		myCalTv = (TextView)findViewById(R.id.my_day_cal);
		intakeCalTv = (TextView)findViewById(R.id.intake_cal);
		TextView dateTv = (TextView)findViewById(R.id.date);
		// 오늘 날짜로 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");	
		dateTv.setText(sdf.format(new Date()));
		// 식사 선택 처리 스피너
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
	 * 일일 칼로리 계산
	 */
	private void dayCaloriesCompute() {

		double stature = myInfoData.getHeight();
		double vStature = 0;
		
		double weight = myInfoData.getWeight();		// 체중
		double myKcal = 0.6;

		
		if (stature >= 160) {
			vStature = (stature - 100) * 0.9;
		} else if (stature >= 150 || stature < 160) {
			vStature = ((stature - 150) * 0.5) + 50;
		} else if (stature < 150) {
			vStature = (stature - 100);
		}
		
		//기초대사량
		if(myInfoData.getSex().equals("남")){
		  basis = weight * 1.0 * 24;
		}  else {
	    	basis = weight * 0.9 * 24;
		}		

		  //활동대사량 구하기
		active = basis * myKcal;
		  //식품 이용을 위한 에너지	구하기
		foodEnergy = ((basis + active) / 0.9) * 0.1;
		  //나의 하루에 필요한 에너지 구하기
		dayEnergy = basis + active + foodEnergy;		

		myCalTv.setText(String.valueOf((int)dayEnergy) +"Kcal");
	}

	/**
	 * 스피너 값 변경시 칼로리 값을 가져온다.
	 */
	@Override
	public void onItemSelected(AdapterView<?> av, View v, int pos,
			long arg3) {
		switch(av.getId()){
		// 아침 스피너		
		case R.id.breakfast_spinner : 
			if(calFoodArray[pos].equals("선택")){
				dayCalData.setBreakfast(0);
				break;
			}
			// 칼로리만 뽑아온다.
			String cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf("K"));
			dayCalData.setBreakfast(Integer.valueOf(cal));
			break;
		//	점심 스피너
		case R.id.lunch_spinner : 
			if(calFoodArray[pos].equals("선택")){
				dayCalData.setLunch(0);
				break;
			}
			// 칼로리만 뽑아온다.
			cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf("K"));
			dayCalData.setLunch(Integer.valueOf(cal));
			break;	
		// 저녁 스피너	
		case R.id.dinner_spinner : 
			if(calFoodArray[pos].equals("선택")){
				dayCalData.setDinner(0);
				break;
			}
			// 칼로리만 뽑아온다.
			cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf("K"));
			dayCalData.setDinner(Integer.valueOf(cal));
			break;	
		// 간식 스피너	
		case R.id.snack_spinner : 
			if(calFoodArray[pos].equals("선택")){
				dayCalData.setSnake(0);
				break;
			}
			// 칼로리만 뽑아온다.
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
	 * 저장 처리
	 * @param v
	 */
	public void mOnClick(View v){
		DbHelper db = new DbHelper(this);
		try{
			if( db.insertDayCalories(dayCalData) > 0){
				Toast.makeText(this, "칼로리 내역이 저장되었습니다.", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this, MyCalListActivity.class);
				startActivity(intent);
				finish();
			}
		}finally{
			db.close();
		}

	}

}
