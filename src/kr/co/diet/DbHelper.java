package kr.co.diet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

