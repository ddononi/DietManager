package kr.co.diet;

import kr.co.diet.map.MapActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 *	���� �޴� ���� ȭ��
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

		case R.id.day_cal_manage : 		// ���� Į�θ� ���
			intent.setClass(this, MyCalActivity.class);
			break;				

		case R.id.cal_info_btn : 				// Į�θ� ����
			intent.setClass(this, CalWebViewActivity.class);
			break;				
		
		case R.id.tip_info_btn : 				// ��� �� �Ľ��� ����
			intent.setClass(this, TipWebViewActivity.class);
			break;		
		
		case R.id.exercise_cal_info_btn :	// � Į�θ� ����
			intent.setClass(this, ExerciseWebViewActivity.class);
			break;		
		
		case R.id.exercise_run_btn :	// ����
			intent.setClass(this, MapActivity.class);
			break;
			
		
		case R.id.weather_info : 		// ��������
			intent.setClass(this, WeatherActivity.class);
			break;	
			
		case R.id.user_info :	// ���������
			intent.setClass(this, UserInfoActivity.class);
			break;
						
				
		case R.id.help_btn :	// ����
			intent.setClass(this, HelpActivity.class);
			break;
		}
		
		startActivity(intent);
	}
}
