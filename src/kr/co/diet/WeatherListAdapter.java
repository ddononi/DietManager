package kr.co.diet;

import java.util.ArrayList;

import kr.co.diet.dao.ForecastData;
import kr.co.diet.widget.WebImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeatherListAdapter extends BaseAdapter {
	private ArrayList<ForecastData> list = null;
	private final LayoutInflater inflater;

	public WeatherListAdapter(final ArrayList<ForecastData> list, final Context mContext) {
		this.list = list;
		inflater = LayoutInflater.from(mContext);
	}

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
		return 0;
	}

	/** list �� �� view ���� */
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewGroup item = getViewGroup(convertView, parent);
		ForecastData data = (ForecastData)getItem(position);
		// ������Ʈ ��ŷ
		TextView dayOfWeekTv = (TextView) item.findViewById(R.id.list_dayofweek);
		TextView conditionTv = (TextView) item.findViewById(R.id.list_condition);
		TextView maxTempTv = (TextView) item.findViewById(R.id.list_max_temp);
		TextView minTempTv = (TextView) item.findViewById(R.id.list_min_temp);
		WebImageView imgTv = (WebImageView) item.findViewById(R.id.icon);
		// ������Ʈ�� ���� set���ش�,
		dayOfWeekTv.setText(data.getDayOfWeek()+"����");
		conditionTv.setText(data.getCondition());
		maxTempTv.setText(data.getHighTemp() +"��");
		minTempTv.setText(data.getLowTemp() +"��");
		imgTv.setImasgeUrl(ConstantActivity.GOOGLE_URL + data.getWeatherImgUrl());
		return item;
	}

	/**
	 * ���� ���� üũ�� custom list�� �� ��ȯ
	 *
	 * @param reuse
	 *            ��ȯ�� ��
	 * @param parent
	 *            �θ��
	 * @return ������ ����� ��
	 */
	private ViewGroup getViewGroup(final View reuse, final ViewGroup parent) {
		/*
		 * if(reuse instanceof ViewGroup){ // ������ �����ϸ� �並 �����Ѵ�. return
		 * (ViewGroup)reuse; }
		 */
		//Context context = parent.getContext(); // �θ��κ��� ���ý�Ʈ�� ���´�.
		ViewGroup item = (ViewGroup) inflater.inflate(R.layout.weather_list_item, null);
		return item;
	}

}
