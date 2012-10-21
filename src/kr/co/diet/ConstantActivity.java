package kr.co.diet;

import kr.co.diet.dao.MyInfoData;
import kr.co.utils.BaseActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * �⺻ ���� Ŭ����
 * 
 */
public class ConstantActivity extends BaseActivity {
	// ������ ��Ÿ���� ���
	public final static int SUNDAY = 0;
	public final static int MONAY = 1;
	public final static int TUESDAY = 2;
	public final static int WEDNESDAY = 3;
	public final static int THURSDAY = 4;
	public final static int FRIDAY = 5;
	public final static int SATURDAY = 6;

	public final static String DB_NAME = "diet";
	public final static String DEBUG_TAG = "diet";
	/* preperence */
	public final static String IS_JOINED = "joined";

	public final static String PREFERENCE = "diet_preference";
	public static final String ERROR_MESSAGE = "error";
	/* open api */
	public static final String MAP_KEY = "0ba9dad212cac5f575cc01ff121323295e8dc343";
	public static final String DAUM_LOCAL_KEY = "1a4150ac00469d2392fab7b8c0ff9b076dc07ad1";
	// msn weather xml url
	public final static String MSN_WEATHER_IMAGE_URL = "http://blu.stc.s-msn.com/as/wea3/i/en/";
	// msn weather xml url
	public final static String MSN_WEATHER_URL = "http://weather.service.msn.com/data.aspx?weadegreetype=C&culture=ko-KR&weasearchstr=";

	// ����õ� �ִ� �ð�
	public final static int CONNECTION_TIME_OUT = 5000;

	/**
	 * �ȵ���̵忡�� �����ϴ� ���̵忡 ���� �ش� ��ġ������ ���� �ּ����� �ŷڼ��� �����ϱ� ���� �������� �ð����� 2�� �̻� �����
	 * �Ѵ�.
	 */
	public static final int TWO_MINUTES = 1000 * 60 * 2;
	public static final int ACCURATE_VALUE = 200;
	public static final int START_TAG = 1;
	public static final int END_TAG = 2;

	public static final String EXERCISE_LIST_URL = "exercise_list.html";
	public static final String TIP_URL = "tip_info.html";
	public static final String CAL_URL = "main_info.html";
	// ��ǥ ���� ��
	public static final String DIET_PREF = "diet preference";
	public static final String LAST_LAT = "last latitude";
	public static final String LAST_LNG = "last longitude";

	public static final String DEFAULT_LAT = "37.566528";
	public static final String DEFAULT_LNG = "126.978031";
	// weather xml url
	public final static String WEATHER_URL = "http://www.kma.go.kr/weather/forecast/mid-term-xml.jsp";

	protected static MyInfoData myInfoData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// ����������� ������ ����� ������ �ҷ��´�.
		if (myInfoData == null) {
			DbHelper db = new DbHelper(this);
			myInfoData = db.loadUserInfo();
			db.close();
		}
	}

	/** �ɼ� �޴� ����� */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "���������");
		menu.add(0, 2, 0, "���ϰ���");
		menu.add(0, 3, 0, "��������");
		menu.add(0, 4, 0, "���̾�Ʈ�Ĵ�");
		menu.add(0, 5, 0, "�����");
		menu.add(0, 6, 0, "����");

		return true;
	}

	/** �ɼ� �޴� ���ÿ� ���� �ش� ó���� ���� */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case 1: // ����� ����
			intent = new Intent(this, UserInfoActivity.class);
			return true;

		case 2: // ���ϰ���
			intent = new Intent(this, MyCalListActivity.class);
			return true;

		case 3: // ��������
			intent = new Intent(this, MonthActivity.class);
			return true;

		case 4: // ���̾�Ʈ �Ĵ�
			intent = new Intent(this, CalWebViewActivity.class);
			return true;

		case 5: // ����
			intent = new Intent(this, ExerciseListActivity.class);
			return true;

		case 6: // ���� ��Ƽ��Ƽ�� �̵�
			intent = new Intent(getBaseContext(), HelpActivity.class);
			startActivity(intent); // Ư���� ��û�ڵ�� �ʿ����
			return true;

		}
		return false;
	}

	/**
	 * �ڷ� ���⸦ ������ �����Ų��.
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// finishDialog(this);

	}

	/**
	 * ���� confirm ���̾�α� â
	 * 
	 * @param context
	 */
	public void finishDialog(Context context) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle("").setMessage("���α׷��� �����Ͻðڽ��ϱ�?")
				.setPositiveButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						moveTaskToBack(true);
						moveTaskToBack(true);
						finish();
					}
				}).setNegativeButton("���", null).show();
	}
}
