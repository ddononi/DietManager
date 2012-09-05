package kr.co.diet;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

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

		case R.id.day_cal_manage : 			// 일일 칼로리 계산내역
			intent.setClass(this, MyCalListActivity.class);
			break;				

		case R.id.cal_info_btn : 			// 칼로리 정보
			intent.setClass(this, CalWebViewActivity.class);
			break;				
		
		case R.id.tip_info_btn : 			// 상식 및 식습관 정보
			intent.setClass(this, TipWebViewActivity.class);
			break;		
		
		case R.id.exercise_cal_info_btn :	// 운동 칼로리 정보
			intent.setClass(this, ExerciseWebViewActivity.class);
			break;		
		
		case R.id.exercise_run_btn :		// 운동방법
			intent.setClass(this, ExerciseRunActivity.class);
			break;
			
		
		case R.id.weather_info : 			// 날씨정보
			intent.setClass(this, WeatherActivity.class);
			break;	
			
		case R.id.user_info :				// 사용자정보
			intent.setClass(this, UserInfoActivity.class);
			break;
						
				
		case R.id.help_btn :				// 도움말
			intent.setClass(this, HelpActivity.class);
			break;
		}
		
		startActivity(intent);
	}
	
	
	private boolean isTwoClickBack = false;
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		/*
		 * back 버튼이면 타이머(2초)를 이용하여 다시한번 뒤로 가기를
		 * 누르면 어플리케이션이 종료 되도록한다.
		 */
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (!isTwoClickBack) {
					Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.",
							Toast.LENGTH_SHORT).show();
					CntTimer timer = new CntTimer(2000, 1);
					timer.start();
				} else {
					moveTaskToBack(true);
	                finish();
					return true;
				}

			}
		}
		return false;
	}

	// 뒤로가기 종료를 위한 타이머
	class CntTimer extends CountDownTimer {
		public CntTimer(final long millisInFuture, final long countDownInterval) {
			super(millisInFuture, countDownInterval);
			isTwoClickBack = true;
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			isTwoClickBack = false;
		}

		@Override
		public void onTick(final long millisUntilFinished) {
		}
	}
	
	
}
