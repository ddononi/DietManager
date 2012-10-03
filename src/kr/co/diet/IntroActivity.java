package kr.co.diet;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

/**
 *	첫 시작 엑티비티
 *
 */
public class IntroActivity extends ConstantActivity {
	private SharedPreferences settings;	
	private Context context;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        settings = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        context = this;
    }

	/**
	 *  공유설정 환경을 가져와  회원 가입을 했는지 체크 
	 * @return
	 * 		회원 가입  여부
	 */
	private boolean checkRegister() {
		// TODO Auto-generated method stub
		// 공유환경 설정 가져오기
        if( !settings.contains(IS_JOINED)) {
        	return false;
        }
        
        return settings.getBoolean(IS_JOINED, true);
	}

	
    // 다음 화면으로 넘기기
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		  if ( event.getAction() == MotionEvent.ACTION_DOWN ){
			  Log.i(DEBUG_TAG,event.toString());
			  
			 Intent intent = null;
			 // 회원가입을 했으면 바로 메인으로 
			 if( checkRegister() ){
				 	intent =  new Intent(context, MainMenuActivity.class);
			 }else{	// 아니면 회원가입 액티비티로
				 intent =  new Intent(context, JoinActivity.class);
			 }
			 startActivity(intent);
			 finish();
			 return true;
		  }
		  
		  return super.onTouchEvent(event);
		  
	}	
	
}