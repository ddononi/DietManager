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
 *	� ��� ����Ʈ ��Ƽ��Ƽ
 */
public class ExerciseListActivity extends BaseActivity {
	private ListView listview;
	private ArrayList<RunningData> arrayList;	// ���� ����Ʈ
	private ExerciseListAdapter adapter;		// ����Ʈ �����
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_list_layout);
		initLayout();
	}
	
	/*
	 * Layout �ʱ�ȭ
	 */
	private void initLayout() {
		listview = (ListView)findViewById(R.id.list_view);
		// ��񿡼� ���� ����Ʈ�� �����´�.
		DbHelper db = new DbHelper(this);
		arrayList = db.selectRunningList();
		db.close();
		if(arrayList == null){
			Toast.makeText(this, "� ������ �����ϴ�.", Toast.LENGTH_SHORT).show();
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
