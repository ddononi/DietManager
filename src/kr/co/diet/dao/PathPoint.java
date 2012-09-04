package kr.co.diet.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class PathPoint implements Parcelable {
	private double latitude;
	private double longitude; //
	private String Date; //
	private String pathFlag; //

	public PathPoint() {
	}

	public PathPoint(Parcel in) {
		readFromParcel(in);
	}

	public PathPoint(double latitude, double longitude, String Date,
			String pathFlagl) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.Date = Date;
		this.pathFlag = pathFlag;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeString(Date);
		dest.writeString(pathFlag);

	}

	private void readFromParcel(Parcel in) {
		latitude = in.readDouble();
		longitude = in.readDouble();
		Date = in.readString();
		pathFlag = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public PathPoint createFromParcel(Parcel in) {
			return new PathPoint(in);
		}

		public PathPoint[] newArray(int size) {
			return new PathPoint[size];
		}
	};

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getDate() {
		return Date;
	}

	public void setLatitude(final double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(final double longitude) {
		this.longitude = longitude;
	}

	public void setDate(final String date) {
		Date = date;
	}

	public String getPathFlag() {
		return pathFlag;
	}

	public void setPathFlag(final String pathFlag) {
		this.pathFlag = pathFlag;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
