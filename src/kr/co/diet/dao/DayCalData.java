package kr.co.diet.dao;

/**
 * 老老 漠肺府 单捞磐
 */
public class DayCalData {
	private String date;
	private int breakfast = 0;
	private int lunch = 0;
	private int dinner = 0;
	private int snake = 0;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getBreakfast() {
		return breakfast;
	}

	public void setBreakfast(int breakfast) {
		this.breakfast = breakfast;
	}

	public int getLunch() {
		return lunch;
	}

	public void setLunch(int lunch) {
		this.lunch = lunch;
	}

	public int getDinner() {
		return dinner;
	}

	public void setDinner(int dinner) {
		this.dinner = dinner;
	}

	public int getSnake() {
		return snake;
	}

	public void setSnake(int snake) {
		this.snake = snake;
	}

}
