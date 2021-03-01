package application;

import java.text.DecimalFormat;

public class Formatter {
	private static double minutes;
	private static double seconds;
	private static String newNumber;

    
    public static double calcTime(double i) {
    	double input = (long) (i * 1e0) / 1e0;
    	String stringInput = new DecimalFormat("#").format(input);
    	double newInput = Double.parseDouble(stringInput);

    	minutes = newInput / 60;
    	seconds = ((newInput % 86400) % 3600) % 60;
    	
    	double tempMinutes = (long) (minutes * 1e0) / 1e0;
    	String stringMinutes = new DecimalFormat("#").format(tempMinutes);

    	double tempSeconds = (long) (seconds * 1e0) / 1e0;
    	String stringSeconds = new DecimalFormat("#").format(tempSeconds);

    	newNumber = stringMinutes + "." + stringSeconds;
    	
    	return Double.parseDouble(newNumber);
    }
}
