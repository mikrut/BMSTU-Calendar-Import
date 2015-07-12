package ru.bmstu.schedule.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.bmstu.schedule.calendar.helpers.CalendarSaver;
import ru.bmstu.schedule.calendar.helpers.Logger;
import ru.bmstu.schedule.calendar.helpers.ModelsInitializer;
import ru.bmstu.schedule.models.Auditorium;
import ru.bmstu.schedule.models.Group;
import ru.bmstu.schedule.models.Lecturer;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.Lesson.DescriptionBuilder;
import ru.bmstu.schedule.models.Stream;
import ru.bmstu.schedule.models.Lesson.ActivityType;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	Logger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		logger = new Logger((TextView) findViewById(R.id.hello));
		logger.clear();
        
		java.util.Calendar semesterStart = java.util.Calendar.getInstance();
		java.util.Calendar semesterEnd = java.util.Calendar.getInstance();
		semesterEnd.add(java.util.Calendar.WEEK_OF_YEAR, 18);
		semesterEnd.set(java.util.Calendar.DAY_OF_WEEK, semesterEnd.getFirstDayOfWeek());
		semesterEnd.set(java.util.Calendar.HOUR, 0);
		semesterEnd.set(java.util.Calendar.MINUTE, 0);
		semesterEnd.set(java.util.Calendar.SECOND, 0);
		
		CalendarSaver cs = new CalendarSaver(semesterStart, semesterEnd, "Расписание МГТУ", "mihanik001@gmail.com", getContentResolver());
		try {
			cs.saveToCalendar(getAssets().open("rasp.json"), "example@example.com", "РЛ1-52");
		} catch (IOException e) {
			e.printStackTrace();
		}
   	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
