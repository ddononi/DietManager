package kr.co.diet.dao;

/**
 * 내 정보
 */
public class MyInfoData {
	private String name; // 이름
	private String sex; // 성별	
	private String activeType; // 활동타입
	private int age; // 나이
	private int weight; // 체중
	private int height; // 키
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActiveType() {
		return activeType;
	}

	public void setActiveType(String activeType) {
		this.activeType = activeType;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * 활동량에 따라 다른 칼로리 계산 타입
	 * @return
	 */
	public double getCalType() {
		if(activeType.equals("가벼운")){
			return 0.6;
		}else if(activeType.equals("보통")){
			return 0.7;
		}else if(activeType.equals("심한")){
			return 0.8;
		}else{
			return 0.9;
		}
	}

	
	

}
