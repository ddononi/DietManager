package kr.co.diet.dao;

import java.util.ArrayList;

/**
 * ? ì”¨?•ë³´ë¥??´ì„ ?°ì´???´ë˜??
 */
public class WeatherData {
	private String local; // ì§?—­
	private String currTemp; // ?„ì¬ ?¨ë„
	private String currHumidify; // ?„ì¬ ?µë„
	private String currWeatherImgUrl; //
	private ArrayList<ForecastData> forecasts = new ArrayList<ForecastData>();

	public String getCurrTemp() {
		return currTemp;
	}

	public void setCurrTemp(String currTemp) {
		this.currTemp = currTemp;
	}

	public String getCurrHumidify() {
		return currHumidify;
	}

	public void setCurrHumidify(String currHumidify) {
		this.currHumidify = currHumidify;
	}

	public String getCurrWeatherImgUrl() {
		return currWeatherImgUrl;
	}

	public void setCurrWeatherImgUrl(String currWeatherImgUrl) {
		this.currWeatherImgUrl = currWeatherImgUrl;
	}

	public ArrayList<ForecastData> getForecasts() {
		return forecasts;
	}

	public void setForecasts(ArrayList<ForecastData> forecasts) {
		this.forecasts = forecasts;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

}
