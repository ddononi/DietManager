package kr.co.diet.dao;

/**
 * ���� ������
 */
public class RunningData {
	private int index;		// �ε���
	private String date; // ��¥
	private String distance; // �̵��Ÿ�
	private String startPlace; // ���� ���
	private String endPlace; // �������
	private String latitude; // ����
	private String longitude; // �浵
	private String dateMillis; // �и�����
	private String flag; // ��� ���� �÷���
	private int cal = 0; // Į�θ�

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getStartPlace() {
		return startPlace;
	}

	public void setStartPlace(String startPlace) {
		this.startPlace = startPlace;
	}

	public String getEndPlace() {
		return endPlace;
	}

	public void setEndPlace(String endPlace) {
		this.endPlace = endPlace;
	}

	public int getCal() {
		return cal;
	}

	public void setCal(int cal) {
		this.cal = cal;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getDateMillis() {
		return dateMillis;
	}

	public void setDateMillis(String dateMillis) {
		this.dateMillis = dateMillis;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
