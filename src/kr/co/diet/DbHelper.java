package kr.co.diet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

