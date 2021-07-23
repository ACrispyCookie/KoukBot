package me.acrispycookie.utility;

public class Time {
	
	public static String formatted(long diff) {
		String formatted = "";
		long years = diff/31536000;
		diff = diff - years * 31536000;
		long months = diff/2592000;
		diff = diff - months * 2592000;
		long weeks = diff/604800;
		diff = diff - weeks * 604800;
		long days = diff / 86400;
		diff = diff - days * 86400;
		long hours = diff / 3600;
		diff = diff - hours * 3600;
		long minutes = diff / 60;
		diff = diff - minutes * 60;
		long seconds = diff;
		if(years >= 1) {
			formatted = formatted + years + " χρόνια ";
		}
		if(months >= 1) {
			formatted = formatted + months + " μήνες ";
		}
		if(weeks >= 1) {
			formatted = formatted + weeks + " εβδομάδες ";
		}
		if(days >= 1) {
			formatted = formatted + days + " ημέρες ";
		}
		if(hours >= 1) {
			formatted = formatted + hours + " ώρες ";
		}
		if(minutes >= 1) {
			formatted = formatted + minutes + " λεπτά ";
		}
		if(seconds >= 1) {
			formatted = formatted + seconds + " δευτερόλεπτα ";
		}
		if(formatted.isEmpty()) {
			formatted = "0 δευτερόλεπτα";
		}
		else {
			if(formatted.endsWith(" ")) {
				formatted = formatted.substring(0, formatted.length() - 1);
			}
		}
		return formatted;
	}

}
