package kr.co.diet;

import kr.co.diet.dao.MyInfoData;
import kr.co.utils.BaseActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


/**
 * 기본 설정 클래스
 *
 */
public class ConstantActivity extends BaseActivity {
	public final static String DB_NAME = "diet";
	public final static String DEBUG_TAG = "diet";
	/*	preperence */
	public final static String IS_JOINED = "joined";

	public final static String PREFERENCE = "diet_preference";
	public static final String ERROR_MESSAGE = "error";
    /* open api */
    public static final String MAP_KEY = "0ba9dad212cac5f575cc01ff121323295e8dc343";
    public static final String DAUM_LOCAL_KEY = "1a4150ac00469d2392fab7b8c0ff9b076dc07ad1";	
	// google weather xml url
	public final static String GOOGLE_URL = "http://www.google.co.kr/";
	// google weather xml url
	public final static String GOOGLE_WEATHER_URL = "http://www.google.co.kr/ig/api?weather=";	
	
	// 연결시도 최대 시간
	public final static int CONNECTION_TIME_OUT = 5000;	
    
    /**
     * 안드로이드에서 제공하는 가이드에 따라 해당 위치정보에 대한 최소한의 신뢰성을 보장하기 위해
     * 정보간의 시간차는 2분 이상 경과로 한다.
     */
    public static final int TWO_MINUTES = 1000 * 60 * 2;
    public static final int ACCURATE_VALUE = 200;
    public static final int START_TAG = 1;
    public static final int END_TAG = 2;
    
    public static final String EXERCISE_LIST_URL = "exercise_list.html";
    public static final String TIP_URL = "tip_info.html";
    public static final String CAL_URL = "main_info.html";
    // 좌표 설정 값
    public static final String DIET_PREF = "diet preference";
    public static final String LAST_LAT = "last latitude";
    public static final String LAST_LNG = "last longitude";    

    public static final String DEFAULT_LAT = "37.566528";
    public static final String DEFAULT_LNG = "126.978031";

    
	protected static MyInfoData myInfoData;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//	사용자정보가 없으면 사용자 정보를 불러온다.
		if(myInfoData == null){
			DbHelper db = new DbHelper(this);
			myInfoData = db.loadUserInfo();
			db.close();
		}
	}

	/** 옵션 메뉴 만들기 */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
    	super.onCreateOptionsMenu(menu);
    	menu.add(0,1,0, "사용자정보");
    	menu.add(0,2,0, "일일관리");
    	menu.add(0,3,0, "다이어트식단");
    	menu.add(0,4,0, "운동방법");
    	menu.add(0,5,0, "도움말");

    	return true;
    }

    /** 옵션 메뉴 선택에 따라 해당 처리를 해줌 */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item){
    	Intent intent = null;
    	switch(item.getItemId()){
	    	case 1:	// 사용자 정보
	    		break;
	    	case 2:	// 일일관리
	    		break;
    		case 3:		// 다이어트 식단

				return true;
    		case 4:		// 운동방법
				return true;

			case 5:		// 도움말 액티비티로 이동
				intent = new Intent(getBaseContext(), HelpActivity.class);
				startActivity(intent);	// 특별한 요청코드는 필요없음
				return true;

    	}
    	return false;
    }
    

	/**
	 * 뒤로 가기를 누르면 종료시킨다.
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//finishDialog(this);
		
	}

	/**
	 * 종료 confirm 다이얼로그 창
	 * @param context
	 */
	public void finishDialog(Context context){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle("").setMessage("프로그램을 종료하시겠습니까?")
		.setPositiveButton("종료", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				moveTaskToBack(true);moveTaskToBack(true);
                finish();
			}
		}).setNegativeButton("취소",null).show();
    }
}
