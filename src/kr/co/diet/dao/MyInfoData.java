package kr.co.diet.dao;

/**
 * �� ����
 */
public class MyInfoData {
	private String name; // �̸�
	private String sex; // ����	
	private String activeType; // Ȱ��Ÿ��
	private int age; // ����
	private int weight; // ü��
	private int height; // Ű
	
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
	 * Ȱ������ ���� �ٸ� Į�θ� ��� Ÿ��
	 * @return
	 */
	public double getCalType() {
		if(activeType.equals("������")){
			return 0.6;
		}else if(activeType.equals("����")){
			return 0.7;
		}else if(activeType.equals("����")){
			return 0.8;
		}else{
			return 0.9;
		}
	}

	
	

}
