package ru.bmstu.schedule.activities;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.calendar.helpers.ModelsInitializer;
import ru.bmstu.schedule.graph.DayView;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.Lesson.LessonType;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;

public class WeekActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_timetable);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		try {
			Bundle extras = getIntent().getExtras();
			String jsonData = ModelsInitializer.fileToString(getAssets().open("rasp.json"), "UTF-8");
			List<Lesson> lessons = ModelsInitializer.readLessonsFromJSON(jsonData, extras.getString("CHOOSEN_GROUP_NAME"));
			
			java.util.Calendar semesterStart = (java.util.Calendar) extras.get("SEMESTER_START");
			java.util.Calendar now = java.util.Calendar.getInstance();
			
			int weekIndex = (now.get(Calendar.WEEK_OF_YEAR) - semesterStart.get(Calendar.WEEK_OF_YEAR)) % 2;
			
			LinearLayout elements = (LinearLayout) findViewById(R.id.linLayout);
			
			for (Lesson lesson : lessons) {
				int wtype = lesson.getRepeatType().ordinal() - Lesson.RepeatType.NUMERATOR.ordinal();
				if (lesson.getRepeatType() == Lesson.RepeatType.ALL || wtype == weekIndex) {
					DayView dayView = (DayView) elements.getChildAt(lesson.getWday() + 1);
					
					StringBuilder formattedStringBuilder = new StringBuilder();
					
					if (lesson.getLessonType().equals(LessonType.LECTURE)) {
						formattedStringBuilder.append("<b>")
						.append(lesson.getDisciplineName().toUpperCase())
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
					
					dayView.setPairText(lesson.getPairIndex(), Html.fromHtml(formattedStringBuilder.toString()));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
