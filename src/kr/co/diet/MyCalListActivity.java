package kr.co.diet;

import java.util.ArrayList;

import kr.co.diet.dao.DayCalData;

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
 *	� ��� ��Ƽ��Ƽ
 */
public class MyCalListActivity extends BaseActivity {
	private ListView listview;
	private ArrayList<DayCalData> list;
	private DayCalAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_cal_diary_layout);
		initLayout();
	}
	
	/*
	 * Layout
	 */
	private void initLayout() {
		listview = (ListView)findViewById(R.id.list_view);
		DbHelper db = new DbHelper(this);
		list = db.selectDayCalories();
		db.close();
		if(list == null){
			Toast.makeText(this, "���� Į�θ� ������ �����ϴ�.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		adapter = new DayCalAdapter(list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,
					long arg3) {
				DayCalData data = (DayCalData) adapter.getItem(pos);
				detailDialog(data);
			}
			
		});

	}	
	
	
	/**
	 * Į�θ� ��� ȭ������ �̵�
	 * @param v
	 */
	public void mOnClick(View v){
		Intent intent = new Intent(this, MyCalActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * ���γ��� ���̾�α�
	 * @param c
	 * 	�������� ��ü
	 */
	private void detailDialog(final DayCalData data) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.detail_info);
		dialog.setTitle("���� Į�θ� ����");
		// ���̾�α� ��ŷ
		int totalIntake = data.getBreakfast() + data.getLunch() + data.getDinner() + data.getSnake();
		((TextView)dialog.findViewById(R.id.total_cal)).setText(totalIntake + "Kcal");		
		((TextView)dialog.findViewById(R.id.breakfast)).setText(data.getBreakfast() + "Kcal");
		((TextView)dialog.findViewById(R.id.lunch)).setText(data.getLunch() + "Kcal");
		((TextView)dialog.findViewById(R.id.dinner)).setText(data.getDinner() + "Kcal");
		((TextView)dialog.findViewById(R.id.snack)).setText(data.getSnake() +"Kcal");

		dialog.show();
	}	
	

	
}
