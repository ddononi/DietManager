package kr.co.diet.dao;

/**
 * 운동기록 데이터
 */
public class RunningData {
	private int index;		// 인덱스
	private String date; // 날짜
	private String distance; // 이동거리
	private String startPlace; // 시작 장소
	private String endPlace; // 도착장소
	private String latitude; // 위도
	private String longitude; // 경도
	private String dateMillis; // 밀리세컨
	private String flag; // 출발 도착 플레그
	private int cal = 0; // 칼로리

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
