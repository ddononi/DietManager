package kr.co.diet.utils;

public class DietUtils {
	/*
	distance = calform.distance.value;
	weight = calform.weight.value;
	
	calform.calory.value = Math.floor(distance * weight * 1.036)
	*/
	
	/**
	 * 운동 거리에 따른 칼로리 계산
	 * @param distance
	 * 	운동 거리
	 * @param weight
	 * 	몸무게
	 * @return
	 * 	칼로리
	 */
	public static int calcurateCal(int distance, int weight){
		if(distance <= 0 || weight <= 0){
			return 0;
		}
		distance /= 1000;
		return (int)Math.floor(distance * weight * 1.036);
	}

}
