package kr.co.diet;

import java.util.ArrayList;

import kr.co.diet.dao.DayCalData;
import kr.co.diet.dao.RunningData;
import kr.co.utils.BaseActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *	운동 결과 리스트 엑티비티
 */
public class ExerciseListActivity extends BaseActivity {
	private ListView listview;
	private ArrayList<RunningData> arrayList;
	private ExerciseListAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_list_layout);
		initLayout();
	}
	
	/*
	 * Layout
	 */
	private void initLayout() {
		listview = (ListView)findViewById(R.id.list_view);
		DbHelper db = new DbHelper(this);
		arrayList = db.selectRunningList();
		db.close();
		if(arrayList == null){
			Toast.makeText(this, "운동 내역이 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		adapter = new ExerciseListAdapter(arrayList);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,
					long arg3) {
				RunningData data = (RunningData) adapter.getItem(pos);
				Toast.makeText(ExerciseListActivity.this, data.getIndex() + " ", Toast.LENGTH_SHORT);
			}
			
		});

	}	
	

	
}
