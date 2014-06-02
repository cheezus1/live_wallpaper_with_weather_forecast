package com.bk.theweatherlive;

public class WeatherCodeParser {
	public int getIcon(int code) {
		if(code >= 200 && code <= 232) {
			return R.drawable.thunderstorm;
		} else if ((code >= 300 && code <= 321) || (code >= 500 && code <= 504) || (code >= 520 && code <= 531)) {
			return R.drawable.rain;
		} else if ((code == 511) || (code >= 600 && code <= 622) || (code == 906)) {
			return R.drawable.snow;
		} else if (code >= 700 && code <= 762) {
			return R.drawable.fog;
		} else if ((code == 771)) {
			return R.drawable.wind;
		} else if (code == 781 || code == 900 || code == 902 || code == 962) {
			return R.drawable.tornado;
		} else if (code == 800 || code == 952) {
			return R.drawable.clear_day;
		} else if (code >= 801 && code <= 803) {
			return R.drawable.cloudy_day;
		} else if (code == 804) {
			return R.drawable.overcast;
		} else if (code == 903) {
			return R.drawable.cold;
		} else if (code == 904) {
			return R.drawable.hot;
		} else if ((code == 901) || (code == 905) || (code >= 951 && code <= 961)) {
			return R.drawable.wind;
		}
		return R.drawable.clear_day;
	}
	
	public int getWallpaper(int code) {
		if(code >= 200 && code <= 232) {
			return R.drawable.wall_thunderstorm;
		} else if ((code >= 300 && code <= 321) || (code >= 500 && code <= 504) || (code >= 520 && code <= 531)) {
			return R.drawable.wall_rain;
		} else if ((code == 511) || (code >= 600 && code <= 622) || (code == 906)) {
			return R.drawable.wall_snow;
		} else if (code >= 700 && code <= 762) {
			return R.drawable.wall_fog;
		} else if ((code == 771)) {
			return R.drawable.wall_wind;
		} else if (code == 781 || code == 900 || code == 902 || code == 962) {
			return R.drawable.wall_tornado;
		} else if (code == 800 || code == 952) {
			return R.drawable.wall_clear;
		} else if (code >= 801 && code <= 803) {
			return R.drawable.wall_cloudy;
		} else if (code == 804) {
			return R.drawable.wall_overcast;
		} else if (code == 903) {
			return R.drawable.wall_overcast;
		} else if (code == 904) {
			return R.drawable.wall_clear;
		} else if ((code == 901) || (code == 905) || (code >= 951 && code <= 961)) {
			return R.drawable.wall_wind;
		}
		return R.drawable.wall_clear;
	}
}
