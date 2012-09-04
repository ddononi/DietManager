package kr.co.diet;

import java.text.BreakIterator;

import android.content.Context;
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
public class MyCalActivity extends BaseActivity implements OnItemSelectedListener {
	// 컨택스트
	private Context mContext;	
	// elements
	private TextView myCalTv;			// 내 일일 요구 칼로리 텍스트뷰
	// 칼로리 정보 배열
	private String[] calFoodArray; 
	
	// 칼로리 
	private double basis;					// 기초 대사량		
	private double active;					// 활동대사량 구하기
	private double foodEnergy;			// 식품 이용을 위한 에너지
	private double dayEnergy;			// 나의 하루에 필요한 에너지 구하기
	
	// 아침 칼로리
	private int breakfastCalory = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_cal_layout);
		mContext = this;
		calFoodArray = getResources().getStringArray(R.array.food_calories);
		initLayout();
		dayCaloriesCompute();
	}

	/*
	 * Layout
	 */
	private void initLayout() {

		myCalTv = (TextView)findViewById(R.id.my_day_cal);
		// 식사 선택 처리 스피너
		Spinner breakfastSp = (Spinner)findViewById(R.id.breakfast_spinner);
		breakfastSp.setOnItemSelectedListener(this);
	}

	/**
	 * 일일 칼로리 계산
	 */
	private void dayCaloriesCompute() {
		double stature = 181L;
		double vStature = 0;
		
		double weight = 75L;		// 체중
		double myKcal = 0.6;

		
		if (stature >= 160) {
			vStature = (stature - 100) * 0.9;
		} else if (stature >= 150 || stature < 160) {
			vStature = ((stature - 150) * 0.5) + 50;
		} else if (stature < 150) {
			vStature = (stature - 100);
		}
		
		String sex = "남";
		  //기초대사량
		  if(sex.equals("남")){
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

	@Override
	public void onItemSelected(AdapterView<?> av, View v, int pos,
			long arg3) {
			// 아침 스피너
			if(av.getId() == R.id.breakfast_spinner){
				if(calFoodArray[pos].equals("선택")){
					breakfastCalory = 0;
					return;
				}
				// 칼로리만 뽑아온다.
				String cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf(")"));
				breakfastCalory = Integer.valueOf(cal);
			}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
	
	public void mOnClick(View v){
		
	}

}
