package kr.co.diet;

import java.text.BreakIterator;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �� Į�θ� ��� ��Ƽ��Ƽ
 */
public class MyCalActivity extends BaseActivity implements OnItemSelectedListener {
	// ���ý�Ʈ
	private Context mContext;	
	// elements
	private TextView myCalTv;			// �� ���� �䱸 Į�θ� �ؽ�Ʈ��
	// Į�θ� ���� �迭
	private String[] calFoodArray; 
	
	// Į�θ� 
	private double basis;					// ���� ��緮		
	private double active;					// Ȱ����緮 ���ϱ�
	private double foodEnergy;			// ��ǰ �̿��� ���� ������
	private double dayEnergy;			// ���� �Ϸ翡 �ʿ��� ������ ���ϱ�
	
	// ��ħ Į�θ�
	private int breakfastCalory = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_cal_layout);
		mContext = this;
		calFoodArray = getResources().getStringArray(R.array.food_calories);
		initLayout();
		dayCaloriesCompute();
	}

	/*
	 * Layout
	 */
	private void initLayout() {

		myCalTv = (TextView)findViewById(R.id.my_day_cal);
		// �Ļ� ���� ó�� ���ǳ�
		Spinner breakfastSp = (Spinner)findViewById(R.id.breakfast_spinner);
		breakfastSp.setOnItemSelectedListener(this);
	}

	/**
	 * ���� Į�θ� ���
	 */
	private void dayCaloriesCompute() {
		double stature = 181L;
		double vStature = 0;
		
		double weight = 75L;		// ü��
		double myKcal = 0.6;

		
		if (stature >= 160) {
			vStature = (stature - 100) * 0.9;
		} else if (stature >= 150 || stature < 160) {
			vStature = ((stature - 150) * 0.5) + 50;
		} else if (stature < 150) {
			vStature = (stature - 100);
		}
		
		String sex = "��";
		  //���ʴ�緮
		  if(sex.equals("��")){
			  basis = weight * 1.0 * 24;
		  }  else {
		    	basis = weight * 0.9 * 24;
		  }		

		  //Ȱ����緮 ���ϱ�
		active = basis * myKcal;
		  //��ǰ �̿��� ���� ������	���ϱ�
		foodEnergy = ((basis + active) / 0.9) * 0.1;
		  //���� �Ϸ翡 �ʿ��� ������ ���ϱ�
		dayEnergy = basis + active + foodEnergy;		

		myCalTv.setText(String.valueOf((int)dayEnergy) +"Kcal");
	}

	@Override
	public void onItemSelected(AdapterView<?> av, View v, int pos,
			long arg3) {
			// ��ħ ���ǳ�
			if(av.getId() == R.id.breakfast_spinner){
				if(calFoodArray[pos].equals("����")){
					breakfastCalory = 0;
					return;
				}
				// Į�θ��� �̾ƿ´�.
				String cal = calFoodArray[pos].substring( calFoodArray[pos].indexOf("(") + 1, calFoodArray[pos].indexOf(")"));
				breakfastCalory = Integer.valueOf(cal);
			}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
	
	public void mOnClick(View v){
		
	}

}
