package kr.co.diet;

import java.util.ArrayList;

import kr.co.diet.dao.RunningData;
import kr.co.diet.map.ExerciseResultMapActivity;
import kr.co.utils.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 *	운동 결과 리스트 엑티비티
 */
public class ExerciseListActivity extends BaseActivity {
	private ListView listview;
	private ArrayList<RunningData> arrayList;	// 운동결과 리스트
	private ExerciseListAdapter adapter;		// 리스트 어댑터
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_list_layout);
		initLayout();
	}
	
	/*
	 * Layout 초기화
	 */
	private void initLayout() {
		listview = (ListView)findViewById(R.id.list_view);
		// 디비에서 운동결과 리스트를 가져온다.
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
				Toast.makeText(ExerciseListActivity.this, data.getIndex() + " ", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ExerciseListActivity.this, ExerciseResultMapActivity.class);
				intent.putExtra("index", data.getIndex());
				intent.putExtra("distance", data.getDistance());
				intent.putExtra("cal", data.getCal());
				startActivity(intent);
			}
			
		});

	}	
	

	
}
