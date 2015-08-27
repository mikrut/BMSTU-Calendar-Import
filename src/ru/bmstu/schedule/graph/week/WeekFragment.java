package ru.bmstu.schedule.graph.week;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.graph.DayView;
import ru.bmstu.schedule.graph.TimetableRowView;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.Lesson.LessonType;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeekFragment extends Fragment {
	public static final String ARG_WEEK_NUMBER = "weekNumber";
	
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		View weekView = inflater.inflate(R.layout.week_timetable, container, false);
		clearWeekInfo(weekView);
		
		Bundle args = getArguments();
		int weekNumber = args.getInt(ARG_WEEK_NUMBER);
			
		try {
			String jsonData = ModelsInitializer.fileToString(getActivity().getAssets().open("rasp.json"), "UTF-8");
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			List<Lesson> lessons = ModelsInitializer.readLessonsFromJSON(jsonData, pref.getString("pref_group", null));
			
			displayWeekInfo(weekNumber, lessons, weekView);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return weekView;
	}
	
	private void clearWeekInfo(View view) {
		LinearLayout elements = (LinearLayout) view.findViewById(R.id.linLayout);
		for (int i = 0; i < elements.getChildCount(); i++) {
			View v = elements.getChildAt(i);
			if (v instanceof DayView)
				((DayView) v).clearPairsInfos();
		}
	}
	
	private void displayWeekInfo(int weekNumber, List<Lesson> lessons, View view) {
		setHeaderWeek(weekNumber, view);
		
		LinearLayout elements = (LinearLayout) view.findViewById(R.id.linLayout);
		
		for (Lesson lesson : lessons) {
			int weekIndex = (weekNumber - 1) % 2;
			int wtype = (lesson.getRepeatType().ordinal() - Lesson.RepeatType.NUMERATOR.ordinal() + 2) % 2;
			if (lesson.getRepeatType() == Lesson.RepeatType.ALL || wtype == weekIndex) {
				displayLessonInfo(lesson, elements);
			}
		}
	}
	
	private void setHeaderWeek(Integer weekNumberOrNull, View view) {
		TextView weekHeader = (TextView) view.findViewById(R.id.weekHeader);

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
