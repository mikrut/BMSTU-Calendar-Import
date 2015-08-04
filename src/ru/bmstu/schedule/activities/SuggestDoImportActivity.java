package ru.bmstu.schedule.activities;

import java.io.IOException;
import java.util.List;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.calendar.helpers.CalendarSaver;
import ru.bmstu.schedule.calendar.helpers.ModelsInitializer;
import ru.bmstu.schedule.models.Lesson;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SuggestDoImportActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_view);

		Button button = (Button) findViewById(R.id.buttonDoImport);
		final Activity act = this;
		final Bundle extras = getIntent().getExtras();
		button.setOnClickListener(new View.OnClickListener() {
			final Activity activity = act;

			@Override
			public void onClick(View v) {
				java.util.Calendar semesterStart = java.util.Calendar.getInstance();
				java.util.Calendar semesterEnd = java.util.Calendar.getInstance();
				
				semesterEnd.add(java.util.Calendar.WEEK_OF_YEAR, 18);
				semesterEnd.set(java.util.Calendar.DAY_OF_WEEK,	semesterEnd.getFirstDayOfWeek());
				semesterEnd.set(java.util.Calendar.HOUR, 0);
				semesterEnd.set(java.util.Calendar.MINUTE, 0);
				semesterEnd.set(java.util.Calendar.SECOND, 0);
				CalendarSaver cs = new CalendarSaver(semesterStart,
						semesterEnd, getString(R.string.calendar_name),
						"mihanik001@gmail.com", getContentResolver());
				try {
					String jsonData = ModelsInitializer.fileToString(getAssets().open("rasp.json"), "UTF-8");
					if (null != extras) {
						List<Lesson> lessons = ModelsInitializer.readLessonsFromJSON(jsonData, extras.getString("CHOOSEN_GROUP_NAME"));
						cs.saveToCalendar(lessons, "example@example.com");
					}

					long startMillis = System.currentTimeMillis();
					Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
					builder.appendPath("time");
					ContentUris.appendId(builder, startMillis);
					Intent openCalendar = new Intent(Intent.ACTION_VIEW).setData(builder.build());
					startActivity(openCalendar);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		button = (Button) findViewById(R.id.buttonTimetable);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(act, WeekActivity.class);
				i.putExtra("CHOOSEN_GROUP_NAME", extras.getString("CHOOSEN_GROUP_NAME"));
				i.putExtra("SEMESTER_START", java.util.Calendar.getInstance());
				act.startActivity(i);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		Bundle extras = this.getIntent().getExtras();
		if (null != extras) {
			TextView txt = (TextView) this
					.findViewById(R.id.textViewGroupHelper);
			txt.setText(this.getResources().getString(R.string.your_choice)
					+ " " + extras.getString("CHOOSEN_GROUP_NAME"));
		}
	}

}
