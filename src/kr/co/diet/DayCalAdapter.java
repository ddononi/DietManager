package kr.co.diet;

import java.util.ArrayList;

import kr.co.diet.dao.DayCalData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 *	메모 리스트에 설정할 어댑터 클래스
 */
public class DayCalAdapter extends BaseAdapter {
	private final ArrayList<?> list;
	public DayCalAdapter(final ArrayList<?> list) {
		this.list = list;
	}

	/** 전체갯수 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(final int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(final int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/** list 의 각 view 설정 */
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewGroup item = getViewGroup(convertView, parent);
		DayCalData data = (DayCalData)getItem(position);
		// 엘리먼트 후킹
		TextView dateTV = (TextView) item.findViewById(R.id.date);
		// 엘리먼트 후킹
		TextView calTV = (TextView) item.findViewById(R.id.cal);		
		// 총 칼로리를 보여준다.
		int dayTotal = data.getBreakfast() + data.getDinner() + data.getLunch() + data.getSnake();
		Log.i("cal",   data.getBreakfast() + "dddd");
		Log.i("cal",   data.getDinner() + "dddd");
		Log.i("cal",   data.getLunch() + "dddd");
		dateTV.setText(data.getDate());
		calTV.setText(dayTotal + "Kcal 섭취");
		
		return item;
	}

	/**
	 * 뷰의 재사용 체크후 custom list로 뷰 반환
	 *
	 * @param reuse
	 *            변환될 뷰
	 * @param parent
	 *            부모뷰
	 * @return 전개후 얻어진 뷰
	 */
	private ViewGroup getViewGroup(final View reuse, final ViewGroup parent) {
		/*
		 * if(reuse instanceof ViewGroup){ // 재사용이 가능하면 뷰를 재사용한다. return
		 * (ViewGroup)reuse; }
		 */
		Context context = parent.getContext(); // 부모뷰로부터 컨택스트를 얻어온다.
		LayoutInflater inflater = LayoutInflater.from(context);
		// custom list를 위해 인플레이터로 뷰를 가져온다
		ViewGroup item = (ViewGroup) inflater.inflate(R.layout.day_cal_list_item, null);
		return item;
	}

}