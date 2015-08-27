package ru.bmstu.schedule.activities;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.graph.DayView;
import ru.bmstu.schedule.graph.TimetableRowView;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.Lesson.LessonType;
import ru.bmstu.schedule.models.SemesterInfo;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeekActivity extends Activity {
	int currentWeekIndex;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_timetable);
	}
	
	private boolean isWeekInRange(int weekNumber) {
		SemesterInfo info = ModelsInitializer.getSemesterInfo(this);
		if (null == info)
			return false;
		int beginIndex = 1;
		int endIndex = info.theoryEnd.get(Calendar.WEEK_OF_YEAR) - info.theoryBegin.get(Calendar.WEEK_OF_YEAR) + 1;
		if (weekNumber >= beginIndex && weekNumber <= endIndex)
			return true;
		return false;
	}
	
	@Nullable
	private Integer getWeekNumber() {
		SemesterInfo info = ModelsInitializer.getSemesterInfo(this);
		if (null == info)
			return null;
		return info.theoryEnd.get(Calendar.WEEK_OF_YEAR) - info.theoryBegin.get(Calendar.WEEK_OF_YEAR) + 1;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		clearWeekInfo();
		
		try {
			SemesterInfo info = ModelsInitializer.getSemesterInfo(this);
			if (info != null) {
				java.util.Calendar semesterStart = info.theoryBegin;
				java.util.Calendar now = java.util.Calendar.getInstance();
				
				int weekNumber = now.get(Calendar.WEEK_OF_YEAR) - semesterStart.get(Calendar.WEEK_OF_YEAR) + 1;
				if (weekNumber <= 0)
					weekNumber = 1;
				
				String jsonData = ModelsInitializer.fileToString(getAssets().open("rasp.json"), "UTF-8");
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				List<Lesson> lessons = ModelsInitializer.readLessonsFromJSON(jsonData, pref.getString("pref_group", null));
				
				displayWeekInfo(weekNumber, lessons);
			} else {
				setHeaderWeek(null);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void clearWeekInfo() {
		LinearLayout elements = (LinearLayout) findViewById(R.id.linLayout);
		for (int i = 0; i < elements.getChildCount(); i++) {
			View v = elements.getChildAt(i);
			if (v instanceof DayView)
				((DayView) v).clearPairsInfos();
		}
	}
	
	private void displayWeekInfo(int weekNumber, List<Lesson> lessons) {
		setHeaderWeek(weekNumber);
		
		LinearLayout elements = (LinearLayout) findViewById(R.id.linLayout);
		
		for (Lesson lesson : lessons) {
			int weekIndex = (weekNumber - 1) % 2;
			int wtype = (lesson.getRepeatType().ordinal() - Lesson.RepeatType.NUMERATOR.ordinal() + 2) % 2;
			if (lesson.getRepeatType() == Lesson.RepeatType.ALL || wtype == weekIndex) {
				displayLessonInfo(lesson, elements);
			}
		}
	}
	
	private void setHeaderWeek(Integer weekNumberOrNull) {
		TextView weekHeader = (TextView) findViewById(R.id.weekHeader);

		if (null != weekNumberOrNull) {
			int weekIndex = (weekNumberOrNull - 1) % 2;
			
			String header = String.format(Locale.getDefault(), "%d неделя (%s)",
					weekNumberOrNull, weekIndex == 0 ? "числитель" : "знаменатель");
			weekHeader.setText(header);
		} else {
			weekHeader.setText("Отсутствует информация о занятиях");
		}
	}
	
	private void displayLessonInfo(Lesson lesson, LinearLayout elements) {
		DayView dayView = (DayView) elements.getChildAt(lesson.getWday() + 1);
		setLessonDisplay(lesson, dayView.getPairView(lesson.getPairIndex()));
	}
	
	private void setLessonDisplay(Lesson lesson, TimetableRowView pairView) {
		
		if (lesson.getLessonType().equals(LessonType.LECTURE)) {
			pairView.setName(lesson.getDisciplineName().toUpperCase(Locale.getDefault()));
		} else {
			pairView.setName(lesson.getDisciplineName());
		}
		pairView.setPairType(lesson.getLessonType().toString());
		
		if (lesson.isAudKnown())
			pairView.setAuditorium(lesson.getAud());
		else
			pairView.hideAuditorium();
		
		if (lesson.isTeacherKnown())
			pairView.setLecturer(lesson.getPub());
		else
			pairView.hideLecturer();
	}
}
