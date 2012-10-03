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
 *	��� ���� Ŭ����
 */
public class DbHelper extends SQLiteOpenHelper {
	public DbHelper(Context context){
		super(context, "diet.db", null, 1);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// ȸ������ ���̺�
		String sql = "CREATE TABLE user_info (idx INTEGER PRIMARY KEY," +
				     " name TEXT NOT NULL, " +	// �̸�
				     "age INTEGER, " +				// ����
				     "sex TEXT NOT NULL, " +		// ����
				     "height INTEGER, " +			// Ű
				     "weight INTEGER, " +			// ������
				     "active TEXT NOT NULL " + 			// Ȱ����
				     ");";
		
		db.execSQL(sql);
		// ���� Į�θ� ���  ���̺�
		sql = "CREATE TABLE cal_day_list (idx INTEGER PRIMARY KEY," +
			     " date TEXT NOT NULL, " +				// ��¥
			     " breakfast INTEGER NOT NULL, " +		// ��ħ
			     " lunch INTEGER NOT NULL, " +			// ����
			     " dinner INTEGER NOT NULL, " + 		// ����
			     " snack INTEGER NOT NULL " +			// ����
			     ");";		
		
		// � ���  ���̺�
		sql = "CREATE TABLE running_record (idx INTEGER PRIMARY KEY," +
			     " date TEXT NOT NULL, " +				// ��¥
			     " distance TEXT NOT NULL, " +			// �̵��Ÿ� 
			     " starPlace TEXT NULL, " +				// �����ġ
			     " endPlace TEXT NULL, " + 				// ������ġ
			     " cal INTEGER " +							// �Һ� Į�θ�
			     ");";
		db.execSQL(sql);
		
		// �̵� ��ġ ���� ���̺�
		sql = "CREATE TABLE trace_coord(idx INTEGER PRIMARY KEY, " + 
			     " running_record_idx INTEGER NOT NULL, " +  // ���� �ε���
			     " latitude TEXT NOT NULL, " + 						// ����
			     " longitude TEXT NOT NULL, " +					// �浵
			     " dateMillis TEXT, " +									// ��� �ð�	
			     " flag TEXT" +											// ��� Ȥ�� ���� �÷���"
			     ");";
		db.execSQL(sql);
	}
	
	
	/**
	 * ���� Į�θ� ������ �����ͺ��̽��� �����Ѵ�.
	 * @param breakfast
	 * @param lunch
	 * @param dinner
	 * @param snack
	 */
	public int insertDayCalories(DayCalData DayCalData){
		SQLiteDatabase db =  this.getWritableDatabase();
		// ���� ��¥ ���� �Ĵ� ����� ������ �ֱ� ������ ���� ��ü�� �ٽ� ���� ó���� �Ѵ�.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy�� MM�� dd��");
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
	 * � ��� �ϱ�
	 * @param data
	 * @return
	 */
	public int insertRunning(RunningData data){
		SQLiteDatabase db =  this.getWritableDatabase();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy�� MM�� dd��");
		
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
	 * ������ ����� �ε��� ���
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
	 * � �̵� ��ġ ��� �ϱ�
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
	 * ����������� �����´�.
	 * @return
	 */
	public MyInfoData loadUserInfo(){ 
		SQLiteDatabase db =  this.getReadableDatabase();
		MyInfoData data = null;
		Cursor cursor = db.rawQuery("SELECT * FROM user_info", null);
		if(cursor.moveToNext()){	// ����� ������ ������
			// ����� ������ �����´�.
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
	 * ����� ����� �����´�.
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
	 * ���ϳ�����  ������ ����Ʈ�� ��´�.
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

