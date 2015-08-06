package ru.bmstu.schedule.activities;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.graph.DayView;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.Lesson.LessonType;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.LinearLayout;

public class WeekActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_timetable);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		try {
			String jsonData = ModelsInitializer.fileToString(getAssets().open("rasp.json"), "UTF-8");
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			List<Lesson> lessons = ModelsInitializer.readLessonsFromJSON(jsonData, pref.getString("pref_group", null));
			
			java.util.Calendar semesterStart = ModelsInitializer.getSemesterInfo(this).theoryBegin;
			java.util.Calendar now = java.util.Calendar.getInstance();
			
			int weekIndex = (now.get(Calendar.WEEK_OF_YEAR) - semesterStart.get(Calendar.WEEK_OF_YEAR)) % 2;
			
			LinearLayout elements = (LinearLayout) findViewById(R.id.linLayout);
			
			for (Lesson lesson : lessons) {
				int wtype = lesson.getRepeatType().ordinal() - Lesson.RepeatType.NUMERATOR.ordinal();
				if (lesson.getRepeatType() == Lesson.RepeatType.ALL || wtype == weekIndex) {
					displayLessonInfo(lesson, elements);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void displayLessonInfo(Lesson lesson, LinearLayout elements) {
		DayView dayView = (DayView) elements.getChildAt(lesson.getWday() + 1);
		String info = getLessonDisplayString(lesson);
		dayView.setPairText(lesson.getPairIndex(), Html.fromHtml(info));
	}
	
	private String getLessonDisplayString(Lesson lesson) {
		StringBuilder formattedStringBuilder = new StringBuilder();
		
		if (lesson.getLessonType().equals(LessonType.LECTURE)) {
			formattedStringBuilder.append("<b>")
			.append(lesson.getDisciplineName().toUpperCase(Locale.getDefault()))
			.append("</b>");
		} else {
			formattedStringBuilder.append(lesson.getDisciplineName());
		}
		formattedStringBuilder.append("<br/>")
		.append(lesson.getLessonType());
		
		if (lesson.isAudKnown())
			formattedStringBuilder.append(" в ")
			.append(lesson.getAud());
		if (lesson.isTeacherKnown())
			formattedStringBuilder.append("<br/>")
			.append("Проводит ")
			.append(lesson.getPub());
		
		return formattedStringBuilder.toString();
	}
}
