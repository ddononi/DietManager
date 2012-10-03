package kr.co.diet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.diet.dao.DayCalData;
import kr.co.diet.dao.MyInfoData;
import kr.co.diet.dao.RunningData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *	디비 설정 클래스
 */
public class DbHelper extends SQLiteOpenHelper {
	public DbHelper(Context context){
		super(context, "diet.db", null, 1);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 회원정보 테이블
		String sql = "CREATE TABLE user_info (idx INTEGER PRIMARY KEY," +
				     " name TEXT NOT NULL, " +	// 이름
				     "age INTEGER, " +				// 나이
				     "sex TEXT NOT NULL, " +		// 성별
				     "height INTEGER, " +			// 키
				     "weight INTEGER, " +			// 몸무게
				     "active TEXT NOT NULL " + 			// 활동량
				     ");";
		
		db.execSQL(sql);
		// 일일 칼로리 기록  테이블
		sql = "CREATE TABLE cal_day_list (idx INTEGER PRIMARY KEY," +
			     " date TEXT NOT NULL, " +				// 날짜
			     " breakfast INTEGER NOT NULL, " +		// 아침
			     " lunch INTEGER NOT NULL, " +			// 성별
			     " dinner INTEGER NOT NULL, " + 		// 저녁
			     " snack INTEGER NOT NULL " +			// 간식
			     ");";		
		
		// 운동 기록  테이블
		sql = "CREATE TABLE running_record (idx INTEGER PRIMARY KEY," +
			     " date TEXT NOT NULL, " +				// 날짜
			     " distance TEXT NOT NULL, " +			// 이동거리 
			     " starPlace TEXT NULL, " +				// 출발위치
			     " endPlace TEXT NULL, " + 				// 도착위치
			     " cal INTEGER " +							// 소비 칼로리
			     ");";
		db.execSQL(sql);
		
		// 이동 위치 저장 테이블
		sql = "CREATE TABLE trace_coord(idx INTEGER PRIMARY KEY, " + 
			     " running_record_idx INTEGER NOT NULL, " +  // 운동기록 인덱스
			     " latitude TEXT NOT NULL, " + 						// 위도
			     " longitude TEXT NOT NULL, " +					// 경도
			     " dateMillis TEXT, " +									// 기록 시간	
			     " flag TEXT" +											// 출발 혹은 도착 플레그"
			     ");";
		db.execSQL(sql);
	}
	
	
	/**
	 * 일일 칼로리 정보를 데이터베이스에 삽입한다.
	 * @param breakfast
	 * @param lunch
	 * @param dinner
	 * @param snack
	 */
	public int insertDayCalories(DayCalData DayCalData){
		SQLiteDatabase db =  this.getWritableDatabase();
		// 같은 날짜 일일 식단 기록이 있을수 있기 때문에 먼저 삭체후 다시 삽입 처리를 한다.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
		db.delete("cal_day_list", "date = ?", new String[]{sdf.format(new Date()), });
		
		ContentValues cv = new ContentValues();
		cv.put("date", sdf.format(new Date()));		
		cv.put("breakfast", DayCalData.getBreakfast());
		cv.put("lunch", DayCalData.getLunch());
		cv.put("dinner", DayCalData.getDinner());
		cv.put("snack", DayCalData.getSnake());	
		int result = (int)db.insert("cal_day_list", null, cv);
		return result;
	}
	
	/**
	 * 운동 기록 하기
	 * @param data
	 * @return
	 */
	public int insertRunning(RunningData data){
		SQLiteDatabase db =  this.getWritableDatabase();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
		
		ContentValues cv = new ContentValues();
		cv.put("date", sdf.format(new Date()));		
		cv.put("distance", data.getDistance());
		cv.put("starPlace", data.getStartPlace());
		cv.put("endPlace", data.getEndPlace());
		cv.put("cal", data.getCal());	
		int result = (int)db.insert("running_record", null, cv);
		return result;
	}	
	
	/**
	 * 마지막 운동정보 인덱스 얻기
	 * @return
	 */
	public int getLastRunningIndex(){
		SQLiteDatabase db =  this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM running_record ORDER BY idx DESC", null);
		if(cursor.moveToFirst()){
			int index = cursor.getInt(cursor.getColumnIndex("idx"));
			return index;
		}
		return -1;
	}
	
	/**
	 * 운동 이동 위치 기록 하기
	 * @param data
	 * @return
	 */
	public int insertTraceCoord(int idx, double lat, double lng, String flag){
		SQLiteDatabase db =  this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("flag", flag);		
		cv.put("running_record_idx", String.valueOf(idx));
		cv.put("latitude", String.valueOf(lat));
		cv.put("longitude", String.valueOf(lng));
		cv.put("dateMillis", "" + new Date().getTime());	
		int result = (int)db.insert("trace_coord", null, cv);
		return result;
	}		

	

	/**
	 * 사용자정보를 가져온다.
	 * @return
	 */
	public MyInfoData loadUserInfo(){ 
		SQLiteDatabase db =  this.getReadableDatabase();
		MyInfoData data = null;
		Cursor cursor = db.rawQuery("SELECT * FROM user_info", null);
		if(cursor.moveToNext()){	// 사용자 정보가 있으면
			// 사용자 정보를 가져온다.
			data = new MyInfoData();
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int age = cursor.getInt(cursor.getColumnIndex("age"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			int height = cursor.getInt(cursor.getColumnIndex("height"));
			int weight = cursor.getInt(cursor.getColumnIndex("weight"));		
			String active = cursor.getString(cursor.getColumnIndex("active"));		
			
			data.setName(name);				
			data.setAge(age);
			data.setSex(sex);
			data.setHeight(height);
			data.setWeight(weight);
			data.setActiveType(active);
		}
		return data;
	}	
	
	/**
	 * 운동내역 목록을 가져온다.
	 * @return
	 */
	public ArrayList<RunningData> selectRunningList(){
		SQLiteDatabase db =  this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM running_record ORDER BY idx DESC", null);
		ArrayList<RunningData> list = null;
		if(cursor.moveToFirst()){
			list = new ArrayList<RunningData>();
			do{
				RunningData data = new RunningData();				
				
				int  index = cursor.getInt(cursor.getColumnIndex("idx"));
				data.setIndex(index);
				String date = cursor.getString(cursor.getColumnIndex("date"));
				String distance = cursor.getString(cursor.getColumnIndex("distance"));
				String startPlace = cursor.getString(cursor.getColumnIndex("starPlace"));
				String endPlace = cursor.getString(cursor.getColumnIndex("endPlace"));		
				int cal = cursor.getInt(cursor.getColumnIndex("cal"));					
				
				data.setDate(date);				
				data.setDistance(distance);
				data.setStartPlace(startPlace);
				data.setEndPlace(endPlace);
				data.setCal(cal);
				list.add(data);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	
	/**
	 * 운동기록내역을  가져와 리스트에 담는다.
	 * @return
	 */
	public ArrayList<DayCalData> selectDayCalories(){
		SQLiteDatabase db =  this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM cal_day_list ORDER BY date DESC", null);
		ArrayList<DayCalData> list = null;
		if(cursor.moveToFirst()){
			list = new ArrayList<DayCalData>();
			do{
				DayCalData data = new DayCalData();				
				
				String date = cursor.getString(cursor.getColumnIndex("date"));
				int breakfast = cursor.getInt(cursor.getColumnIndex("breakfast"));
				int lunch = cursor.getInt(cursor.getColumnIndex("lunch"));
				int dinner = cursor.getInt(cursor.getColumnIndex("dinner"));
				int snack = cursor.getInt(cursor.getColumnIndex("snack"));		
				
				data.setDate(date);				
				data.setBreakfast(breakfast);
				data.setLunch(lunch);
				data.setDinner(dinner);				
				data.setSnake(snack);
				Log.i("cal", " "  +breakfast );
				list.add(data);
			}while(cursor.moveToNext());
		}
		return list;
	}	
	

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
	}	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}

