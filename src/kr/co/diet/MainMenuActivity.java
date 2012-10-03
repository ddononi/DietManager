package kr.co.diet;

import kr.co.diet.map.MapActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 *	메인 메뉴 선택 화면
 */
public class MainMenuActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu_layout);
		initLayout();
	
}

	private void initLayout() {
		// TODO Auto-generated method stub
		
	}

	public void mOnClick(View v){
		Intent intent = new Intent();
		switch(v.getId()){

		case R.id.day_cal_manage : 		// 일일 칼로리 계산
			intent.setClass(this, MyCalActivity.class);
			break;				

		case R.id.cal_info_btn : 				// 칼로리 정보
			intent.setClass(this, CalWebViewActivity.class);
			break;				
		
		case R.id.tip_info_btn : 				// 상식 및 식습관 정보
			intent.setClass(this, TipWebViewActivity.class);
			break;		
		
		case R.id.exercise_cal_info_btn :	// 운동 칼로리 정보
			intent.setClass(this, ExerciseWebViewActivity.class);
			break;		
		
		case R.id.exercise_run_btn :	// 운동방법
			intent.setClass(this, MapActivity.class);
			break;
			
		
		case R.id.weather_info : 		// 날씨정보
			intent.setClass(this, WeatherActivity.class);
			break;	
			
		case R.id.user_info :	// 사용자정보
			intent.setClass(this, UserInfoActivity.class);
			break;
						
				
		case R.id.help_btn :	// 도움말
			intent.setClass(this, HelpActivity.class);
			break;
		}
		
		startActivity(intent);
	}
}
