package kr.co.diet;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

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

		case R.id.day_cal_manage : 			// ���� Į�θ� ��곻��
			intent.setClass(this, MyCalListActivity.class);
			break;				

		case R.id.cal_info_btn : 			// Į�θ� ����
			intent.setClass(this, CalWebViewActivity.class);
			break;				
		
		case R.id.tip_info_btn : 			// ��� �� �Ľ��� ����
			intent.setClass(this, TipWebViewActivity.class);
			break;		
		
		case R.id.exercise_cal_info_btn :	// � Į�θ� ����
			intent.setClass(this, ExerciseWebViewActivity.class);
			break;		
		
		case R.id.exercise_run_btn :		// ����
			intent.setClass(this, ExerciseRunActivity.class);
			break;
			
		
		case R.id.weather_info : 			// ��������
			intent.setClass(this, WeatherActivity.class);
			break;	
			
		case R.id.user_info :				// ���������
			intent.setClass(this, UserInfoActivity.class);
			break;
						
				
		case R.id.help_btn :				// ����
			intent.setClass(this, HelpActivity.class);
			break;
		}
		
		startActivity(intent);
	}
	
	
	private boolean isTwoClickBack = false;
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		/*
		 * back ��ư�̸� Ÿ�̸�(2��)�� �̿��Ͽ� �ٽ��ѹ� �ڷ� ���⸦
		 * ������ ���ø����̼��� ���� �ǵ����Ѵ�.
		 */
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (!isTwoClickBack) {
					Toast.makeText(this, "'�ڷ�' ��ư�� �ѹ� �� ������ ����˴ϴ�.",
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

	// �ڷΰ��� ���Ḧ ���� Ÿ�̸�
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
