package kr.co.diet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.diet.dao.DayCalData;
import kr.co.diet.dao.MyInfoData;

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
	 * 일일 칼로리 소비내역을 가져와 리스트에 담는다.
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

