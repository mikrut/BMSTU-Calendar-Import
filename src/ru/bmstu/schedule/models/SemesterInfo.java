package ru.bmstu.schedule.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.w3c.dom.NamedNodeMap;

public class SemesterInfo {
	public SemesterInfo(NamedNodeMap attributes) {
		setInfoValues(attributes);
	}
	
	public int weekAmount;
	public int semesterNumber;
	
	public Calendar theoryBegin;
	public Calendar theoryEnd;
	
	public Calendar testsBegin;
	public Calendar testsEnd;
	
	public Calendar examsBegin;
	public Calendar examsEnd;
	
	public Calendar holidaysBegin;
	public Calendar holidaysEnd;
	
	private void setInfoValues(NamedNodeMap attributes) {
		int week = Integer.parseInt(attributes.getNamedItem("weeks").getNodeValue());
		int semester = Integer.parseInt(attributes.getNamedItem("semester").getNodeValue());
		
		weekAmount = week;
		semesterNumber = semester;
		
		examsBegin = getDateFromNode("exams_begin", attributes);
		examsEnd = getDateFromNode("exams_end", attributes);
		
		theoryBegin = getDateFromNode("theory_begin", attributes);
		theoryEnd = getDateFromNode("theory_end", attributes);
		
		testsBegin = getDateFromNode("tests_begin", attributes);
		testsEnd = getDateFromNode("tests_end", attributes);
		
		holidaysBegin = getDateFromNode("holidays_begin", attributes);
		holidaysEnd = getDateFromNode("holidays_end", attributes);
	}
	
	private static Calendar getDateFromNode(String fieldName, NamedNodeMap attributes) {
		final SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String value = attributes.getNamedItem(fieldName).getNodeValue();
		Calendar result = Calendar.getInstance();
		try {
			result.setTime(parserSDF.parse(value));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}