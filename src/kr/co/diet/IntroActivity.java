package kr.co.diet;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

/**
 *	ù ���� ��Ƽ��Ƽ
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
	 *  �������� ȯ���� ������  ȸ�� ������ �ߴ��� üũ 
	 * @return
	 * 		ȸ�� ����  ����
	 */
	private boolean checkRegister() {
		// TODO Auto-generated method stub
		// ����ȯ�� ���� ��������
        if( !settings.contains(IS_JOINED)) {
        	return false;
        }
        
        return settings.getBoolean(IS_JOINED, true);
	}

	
    // ���� ȭ������ �ѱ��
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		  if ( event.getAction() == MotionEvent.ACTION_DOWN ){
			  Log.i(DEBUG_TAG,event.toString());
			  
			 Intent intent = null;
			 // ȸ�������� ������ �ٷ� �������� 
			 if( checkRegister() ){
				 	intent =  new Intent(context, MainMenuActivity.class);
			 }else{	// �ƴϸ� ȸ������ ��Ƽ��Ƽ��
				 intent =  new Intent(context, JoinActivity.class);
			 }
			 startActivity(intent);
			 finish();
			 return true;
		  }
		  
		  return super.onTouchEvent(event);
		  
	}	
	
}