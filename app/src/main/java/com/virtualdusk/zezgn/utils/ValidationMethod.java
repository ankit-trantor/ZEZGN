package com.virtualdusk.zezgn.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationMethod {
	
	static Matcher m;
	static String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	static Pattern emailPattern = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE);
	static String passwordExpression ="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,20})";
	static Pattern passwordPattern= Pattern.compile(passwordExpression);
	
	public static boolean emailValidation(String s)
	{
		if( s == null)
		{
			return false; 
		}
		else
		{
			m = emailPattern.matcher(s);
			return m.matches();
		}
	}
	
	public static boolean passwordValidation(String s)
	{
		if( s == null)
		{
			return false; 
		}
		else
		{
			m = passwordPattern.matcher(s);
			return m.matches();
		}
	}
	
	public static boolean emailValidation2(String s)
	{
		m = emailPattern.matcher(s);
		return m.matches();
	}

	public static String parseDateToSimpleFormat(String date)
	{
		String oldDate = date;//"2013-01-04T15:51:45+0530";

		String[] parts = oldDate.split("-");
		String part1 = parts[0]; // YYYY
		String part2 = parts[1]; // MM
		String part3 = parts[2]; // DD

		String[] parts_Time = part3.split("T");
		String part_Day = parts_Time[0]; // DD

		String finalDate=part2+"-"+part_Day+"-"+part1;
//Month-Date-YYYY

		Log.e("******", "onCreateView: DATE::"+finalDate);
		return finalDate;
	}

	public static String parseDateToSimpleFormat1(String date)
	{
		String oldDate = date;//"2013-01-04T15:51:45+0530";

		String[] parts = oldDate.split("-");
		String part1 = parts[0]; // YYYY
		String part2 = parts[1]; // MM
		String part3 = parts[2]; // DD

		String[] parts_Time = part3.split("T");
		String part_Day = parts_Time[0]; // DD

		String finalDate=part1+"-"+part2+"-"+part_Day;
//Month-Date-YYYY

		Log.e("******", "onCreateView: DATE::"+finalDate);
		return finalDate;
	}


}
