package kr.co.diet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import kr.co.diet.map.ExerciseResultMapActivity;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * 달력 메인 엑티비티
 */
public class MonthActivity extends ConstantActivity implements OnClickListener {
	// var
	private ArrayList<Button> list; // 날짜 버튼들
	// date
	private Calendar cal; // 달력 설정을 위한 캘린더
	private int currentMonth, currentYear; // 현재달 및 년
	private final int[] selectedDay = new int[3]; // 선택한 년월일
	private final String[] dayWeek = { // 요일 배열
	"일", "월", "화", "수", "목", "금", "토" };
	// / element
	private TextView monthTV; // 상단 월 텍스트
	private ProgressBar loadingBar; // 날씨에 보여줄 로딩바
	private ViewSwitcher switcher; // 상단 월 에니메이션을 위한 뷰 스위쳐
	private View preBtn; // 이전 선택 버튼

	// animation
	private Animation ani; // 버튼 에니메이션
	// map & geo
	private Location location;
	// 위치 리스너 처리
	private final LocationListener loclistener = new LocationListener() {
		// 위치가 변경되면
		@Override
		public void onLocationChanged(final Location location) {
			// getLocation();
			// 위치 수신
			MonthActivity.this.location = location;
		}

		@Override
		public void onProviderDisabled(final String provider) {
		}

		@Override
		public void onProviderEnabled(final String provider) {
		}

		@Override
		public void onStatusChanged(final String provider, final int status,
				final Bundle extras) {
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.month_layout);

		cal = Calendar.getInstance();
		// 시스템 시간으로 설정
		cal.setTimeInMillis(System.currentTimeMillis());
		currentYear = cal.get(Calendar.YEAR); // 현재 year
		currentMonth = cal.get(Calendar.MONTH) + 1; // 현재달을 저장
		ani = AnimationUtils.loadAnimation(this, R.anim.alpha); // 알파 애니메이션 설정
		// 상단 Month의 뷰 스위쳐를 얻은후 getCurrentView로 Month의 TextView를 얻는다.
		switcher = (ViewSwitcher) findViewById(R.id.switcher_month);
		monthTV = (TextView) switcher.getCurrentView();
		// 이전달 버튼
		ImageButton prevtBtn = (ImageButton) findViewById(R.id.prev);
		// 다음달 버튼
		ImageButton nextBtn = (ImageButton) findViewById(R.id.next);
		// 날씨 로딩바
		loadingBar = (ProgressBar) findViewById(R.id.progressBar);
		// 버튼 이벤트를 위해 스위쳐의 자식 텍스트뷰 후킹
		TextView swticherMonthTV = (TextView) findViewById(R.id.month);
		prevtBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		swticherMonthTV.setOnClickListener(this);

		initElem(); // 날짜 table 설정
		setDate(); // 날짜 설정
		initCheckCalGoal(); // 메모 체크
		ani = AnimationUtils.loadAnimation(this, R.anim.alpha); // 날씨 정보 가져오기
		AsyncTaskWeather asyncWeather = new AsyncTaskWeather();
		asyncWeather.execute();

	}

	/**
	 * 달력 생성 및 초기화
	 */
	private void initElem() {
		list = new ArrayList<Button>(); // 달력 버튼 리스트
		TableLayout table = (TableLayout) findViewById(R.id.days_table);
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		TableRow.LayoutParams btnParams = new TableRow.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		btnParams.setMargins(2, 2, 2, 2); // 마진 넣기
		for (int week = 0; week < 6; week++) { // 한주에 한 열씩
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(params);
			for (int day = 0; day < 7; day++) { // 한주의 날짜 7일
				Button dayBtn = new Button(this);
				// dayBtn.setBackgroundResource(R.drawable.selector);
				if (day == SUNDAY) { // 일요일이면 빨깡 색
					dayBtn.setTextColor(Color.parseColor("#FF4406"));
				} else if (day == SATURDAY) { // 토요일이면 파랑색
					dayBtn.setTextColor(Color.parseColor("#A4CFFF"));
				} else { // 평일이면 흰색
					dayBtn.setTextColor(Color.parseColor("#111111"));
				}
				// 날짜 버튼 레이아웃 설정
				dayBtn.setShadowLayer(1, 1, 1, Color.parseColor("#333333"));
				dayBtn.setTextSize(28);
				dayBtn.setPadding(2, 2, 2, 2);
				dayBtn.setText("0");
				dayBtn.setLayoutParams(btnParams);
				final int weekIndex = day;
				dayBtn.setOnClickListener(new OnClickListener() {

					/**
					 * 선택한 버튼 포커스 주기
					 */
					@Override
					public void onClick(final View v) {

						// 빈 달력 공간이면
						if (TextUtils
								.isEmpty(((Button) v).getText().toString())) {
							return;
						}
						// v.setBackgroundColor(R.color.select);
						// 이전 버튼 포커스 제거
						if (preBtn != null) {
							if (preBtn.getTag() != null) {
								preBtn.setBackgroundResource((Integer) preBtn
										.getTag());
							} else {
								preBtn.setBackgroundResource(R.drawable.selector);
							}
						}
						preBtn = v;
					}
				});

				// 날짜에 이벤트 설정
				dayBtn.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(final View v) {
						// 빈 달력 공간이면
						if (TextUtils
								.isEmpty(((Button) v).getText().toString())) {
							return false;
						}
						// 날짜를 클릭하면 선택한 년월일저장
						selectedDay[0] = cal.get(Calendar.YEAR);
						selectedDay[1] = cal.get(Calendar.MONTH);
						// 선택한 날 저장
						final String day = ((Button) v).getText().toString();
						selectedDay[2] = Integer.valueOf(day);
						int firstWeekDay = cal.get(Calendar.DAY_OF_WEEK);
						showDayDialog(weekIndex, day);
						return true;
					}

				});

				dayBtn.setGravity(Gravity.CENTER_HORIZONTAL);
				tr.addView(dayBtn);
				list.add(dayBtn);
			}
			table.addView(tr);
		}
	}

	/**
	 * 날짜 선택시 선택 다이얼로그 띄우기
	 * 
	 * @param weekIndex
	 * @param day
	 */
	private void showDayDialog(final int weekIndex, final String day) {
		CharSequence date = String.format("%04d년 %02d월 %02d일", selectedDay[0],
				Integer.valueOf(selectedDay[1] + 1), Integer.valueOf(day));
		/*
		 * new AlertDialog.Builder(MonthActivity.this) .setTitle( selectedDay[0]
		 * + "년  " + Integer.valueOf(selectedDay[1] + 1) + "월  " + day + "일  " +
		 * dayWeek[weekIndex] + "요일") // 메뉴 항목 .setMessage("목표치 설정").show();
		 */
		registerGoalCal(date);
	}

	/**
	 * 해당달의 날짜 설정
	 */
	private void setDate() {
		// year 와 moenth를 설정한다.
		// 월은 0~11까지 이다
		CharSequence month = String.format("%04d년 %02d월",
				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
		monthTV.setText(month);
		// 첫번째 주의 시작일
		// int firstWeekDay =cal.getActualMinimum();
		int temp = cal.get(Calendar.DAY_OF_MONTH); // 오늘 날짜를 일단 저장
		// 첫째날의 요일순번을 저장하기 위해 임시로 1일로 설정
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int firstWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
		// 요일의 순번을 구했으면 다시 원래 날짜로
		cal.set(Calendar.DAY_OF_MONTH, temp);
		if (firstWeekDay > 6) {
			firstWeekDay = 0;
		}

		// 리스트를 초기화 시킨다.
		for (int i = 0; i < list.size(); i++) {
			Button b = list.get(i);
			b.setText("");
			b.setBackgroundColor(Color.parseColor("#00000000"));
			b.setClickable(false);
		}

		Log.i("diet", "firstWeekDay ->" + firstWeekDay + "");
		// 오늘 날짜
		int today = cal.get(Calendar.DAY_OF_MONTH);
		Log.i("diet", "Today ->" + today + "");
		int selMonth = cal.get(Calendar.MONTH) + 1;
		int selYear = cal.get(Calendar.YEAR);
		// 날짜 설정

		// 첫번재 시작일부터 그 달의 월마지막 날과 첫번재 시작일을 더한값만큼 날짜를 추가해준다.
		int j = 1;
		for (int days = firstWeekDay; days < cal
				.getActualMaximum(Calendar.DAY_OF_MONTH) + firstWeekDay; days++) {
			list.get(days).setText(j + ""); // 날짜를 넣어준다.
			list.get(days).setClickable(true);
			list.get(days).startAnimation(ani); // 날짜버튼에 애니메이션 시작

			if (currentMonth == selMonth && today == j
					&& currentYear == selYear) { // 오늘날짜에 색 강조
				list.get(days).setBackgroundResource(R.color.today);
				list.get(days).setTag(R.color.today);
			} else {
				list.get(days).setBackgroundResource(R.drawable.selector);
			}
			j++;
		}
	}

	/**
	 * 운동날짜 체크해 달력에 색 강조해 주기
	 */
	private void initCheckCalGoal() {
		DbHelper dbhp = new DbHelper(this);
		SQLiteDatabase db = dbhp.getReadableDatabase();
		Cursor cursor = null;
		// 년월일 조건검색
		String date = cal.get(Calendar.YEAR) + "년 "
				+ String.format("%02d", cal.get(Calendar.MONTH) + 1) + "월";
		Log.i("diet", "date-->" + date);
		String memoDate;
		cursor = db.query("running_record", null, "substr(date, 1,9) = ? ",
				new String[] { date, }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				memoDate = cursor.getString(cursor.getColumnIndex("date"));
				final int index = cursor.getInt(cursor.getColumnIndex("idx"));
				final String distance = cursor.getString(cursor
						.getColumnIndex("distance"));
				final int calory = cursor.getInt(cursor.getColumnIndex("cal"));
				final int gaolCalory = cursor.getInt(cursor
						.getColumnIndex("goalCal"));
				// list에 추가
				String[] arr = memoDate.split(" ");
				memoDate = arr[2].replace("일", ""); // 운동한 날짜 뱨오기
				Log.i("diet", "diet day" + arr[2]);
				int searchDay = Integer.valueOf(memoDate);
				// 오늘날짜는 뺴고
				if (searchDay == cal.get(Calendar.DAY_OF_MONTH)) {
					// continue;
				}
				for (Button btn : list) {
					// 메모가 있으면 색강조
					if (btn.getText().toString().length() > 0) { // 날짜가 있는 버튼만
						if (Integer.valueOf(btn.getText().toString()) == searchDay) {
							Log.i("diet", "write diet day-->" + searchDay);
							// 목표치가 없거나 목표량을 채웠을경우
							if (gaolCalory == 0 || gaolCalory < calory) {
								btn.setBackgroundColor(Color
										.parseColor("#9934AAFE"));
							} else {
								// 목표량을 채우지 못했을경우
								btn.setBackgroundColor(Color
										.parseColor("#99E94D00"));
							}
							btn.setTag(R.drawable.has_memo_selector);
							btn.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (calory <= 0) {
										Toast.makeText(MonthActivity.this,
												"운동내역이 없습니다.",
												Toast.LENGTH_SHORT).show();
										return;
									}
									// 클릭시 해당 운동내역결과로 이동
									Intent intent = new Intent(
											MonthActivity.this,
											ExerciseResultMapActivity.class);
									intent.putExtra("index", index);
									intent.putExtra("distance", distance);
									intent.putExtra("cal", calory);
									startActivity(intent);
								}
							});
						}
					}
				}
			} while (cursor.moveToNext());
		}
		// 디비를 닫아준다.
		cursor.close();
		db.close();

	}

	private void registerGoalCal(final CharSequence date) {
		// 다이얼로그 생성
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.register_goal_cal_dialog);
		final EditText goalEt = (EditText) dialog
				.findViewById(R.id.register_goal_cal);
		Button addBtn = (Button) dialog.findViewById(R.id.add_myplace_btn);
		// 일정추가 이벤트 처리
		addBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// 목표치가 없거나 숫자가 아닐경우
				if (!TextUtils.isDigitsOnly(goalEt.getText())) {
					Toast.makeText(MonthActivity.this, "목표치를 정확히 입력하세요",
							Toast.LENGTH_SHORT).show();
					return;
				}

				DbHelper dbhp = new DbHelper(MonthActivity.this); // 도우미 클래스
				// 목표 칼로리
				int goalCal = Integer.valueOf(goalEt.getText().toString());
				// db에 정상적으로 추가 되었으면 토스트를 굽는다.
				if (dbhp.insertGoalCal(goalCal, date) > 0) {
					Toast.makeText(MonthActivity.this, "목표칼로리를 등록하였습니다.",
							Toast.LENGTH_SHORT).show();
					dialog.dismiss(); // 정상적으로 처리되면 다이얼로그를 닫는다.
					// 달력 갱신
					initCheckCalGoal();
				}
				dbhp.close();
			}
		});

		dialog.show();
	}

	/**
	 * 년(year) 선택 다이얼로그
	 */
	private void selectYear() {
		// TODO Auto-generated method stub
		// 3년 전후로
		final CharSequence[] years = new CharSequence[] {
				cal.get(Calendar.YEAR) - 5 + "",
				cal.get(Calendar.YEAR) - 4 + "",
				cal.get(Calendar.YEAR) - 3 + "",
				cal.get(Calendar.YEAR) - 2 + "",
				cal.get(Calendar.YEAR) - 1 + "", cal.get(Calendar.YEAR) + "",
				cal.get(Calendar.YEAR) + 1 + "",
				cal.get(Calendar.YEAR) + 2 + "",
				cal.get(Calendar.YEAR) + 3 + "",
				cal.get(Calendar.YEAR) + 4 + "",
				cal.get(Calendar.YEAR) + 5 + "" };

		new AlertDialog.Builder(MonthActivity.this).setTitle("년 선택하기")
				.setItems(years, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						int tmpYear = -(years.length / 2);
						tmpYear += which;
						cal.add(Calendar.YEAR, tmpYear);
						setDate();
						initCheckCalGoal();
					}
				}).show();
	}

	/**
	 * resume시 달력날짜에 메모가 있는지 체크
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// 서비스 구동
		super.onResume();
		initCheckCalGoal();
	}

	@Override
	public void onClick(final View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.prev:
			cal.add(Calendar.MONTH, -1);
			setDate();
			initCheckCalGoal();
			switcher.showNext();
			break;

		case R.id.next:
			cal.add(Calendar.MONTH, 1);
			setDate();
			initCheckCalGoal();
			switcher.showNext();
			break;
		case R.id.month:
			selectYear();
			switcher.showNext();
			break;
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// doStartService();
		super.onStop();
	}

	/**
	 * 종료 confirm 다이얼로그 창
	 * 
	 * @param context
	 */
	public void finishDialog(final Context context) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle("").setMessage("프로그램을 종료하시겠습니까?")
				.setPositiveButton("종료", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						// TODO Auto-generated method stub
						moveTaskToBack(true);
						moveTaskToBack(true);
						finish();
					}
				}).setNegativeButton("취소", null).show();
	}

	/**
	 * 서버에 쓰레드로 입력한 아이디를 전송한다.
	 */
	private class AsyncTaskWeather extends AsyncTask<Object, String, String> {
		/**
		 * 쓰레드 처리가 완료되면..
		 */
		@Override
		protected void onPostExecute(String weatherInfo) {
			if (weatherInfo != null) { // 서버 전송 결과에 따라 메세지를 보여준다.
				loadingBar.setVisibility(View.GONE); // progressbar를 숨겨주고
				ScrollView scroll = (ScrollView) findViewById(R.id.weather_scroll);
				TextView weatherTV = (TextView) findViewById(R.id.weather_info);
				weatherInfo = weatherInfo.trim().replace("<br />", "\n");
				scroll.setVisibility(View.VISIBLE);
				weatherTV.setText(weatherInfo); // 날씨정보를 보여준다.
				scroll.scrollTo(0, 0);
			} else {
				Toast.makeText(MonthActivity.this, "날씨정보를 가져오기 못했습니다..",
						Toast.LENGTH_SHORT).show();
			}
		}

		/**
		 * 쓰레드 작업 처리전
		 */
		@Override
		protected void onPreExecute() { // 전송전 프로그래스 다이얼로그로 전송중임을 사용자에게 알린다.

			// mLockScreenRotation(); // 화면회전을 막는다.
		}

		@Override
		protected void onProgressUpdate(final String... values) {
		}

		@Override
		protected String doInBackground(final Object... params) { // 전송중

			// TODO Auto-generated method stub
			String result = null; // 전송 결과 처리
			// http 로 보낼 이름 값 쌍 컬랙션
			try {
				result = parseWeather();
			} catch (ClientProtocolException e) {
				Log.e("diet", "Failed(protocol): ", e);
			} catch (IOException e) {
				Log.e("diet", "Failed (io): ", e);
			} catch (Exception e) {
				Log.e("diet", "파일 업로드 에러", e);
			}

			return result;
		}

		private String parseWeather() throws XmlPullParserException,
				IOException {

			URL url = new URL(WEATHER_URL + "?stnId=109");
			// url로부터 데이터를 읽어오기 위한 스트림
			InputStream in = url.openStream();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			// xml파서팩토리로부터 XmlPullParser얻어오기
			XmlPullParser parser = factory.newPullParser();
			// namespace 지원
			factory.setNamespaceAware(true);
			parser.setInput(in, "utf-8");
			int eventType = -1;
			String weatherInfo = null;
			while (eventType != XmlResourceParser.END_DOCUMENT) { // 문서의 마지막이
																	// 아닐때까지

				if (eventType == XmlResourceParser.START_TAG) { // 이벤트가 시작태그면
					String strName = parser.getName();
					if (strName.equals("wf")) { // message 시작이면 객체생성
						if (parser.getDepth() == 3) { // 주간 날씨 정보
							parser.next();
							weatherInfo = parser.getText();
							Log.i("diet", parser.getText());
						}

					}

				}
				eventType = parser.next(); // 다음이벤트로..
			}
			in.close();
			return weatherInfo;
		}

	}

}