package kr.co.diet.utils;

public class DietUtils {
	/*
	distance = calform.distance.value;
	weight = calform.weight.value;
	
	calform.calory.value = Math.floor(distance * weight * 1.036)
	*/
	
	/**
	 * � �Ÿ��� ���� Į�θ� ���
	 * @param distance
	 * 	� �Ÿ�
	 * @param weight
	 * 	������
	 * @return
	 * 	Į�θ�
	 */
	public static int calcurateCal(int distance, int weight){
		if(distance <= 0 || weight <= 0){
			return 0;
		}
		distance /= 1000;
		return (int)Math.floor(distance * weight * 1.036);
	}

}
