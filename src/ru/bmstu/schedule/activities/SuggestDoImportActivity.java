package ru.bmstu.schedule.activities;

import java.io.IOException;
import java.util.List;

import ru.bmstu.schedule.calendar.CalendarSaver;
import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.graph.preferences.ScheduleFragment;
import ru.bmstu.schedule.invisible.DailySetter;
import ru.bmstu.schedule.invisible.SoundManager;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.SemesterInfo;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SuggestDoImportActivity extends Activity {
	
	private class ImportListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			SemesterInfo sem = ModelsInitializer.getSemesterInfo(SuggestDoImportActivity.this);
			CalendarSaver cs = new CalendarSaver(sem.theoryBegin,
					sem.theoryEnd, getString(R.string.calendar_name),
					"mihanik001@gmail.com", getContentResolver());
			try {
				String jsonData = ModelsInitializer.fileToString(getAssets().open("rasp.json"), "UTF-8");
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SuggestDoImportActivity.this);
				List<Lesson> lessons = ModelsInitializer.readLessonsFromJSON(jsonData, pref.getString("pref_group", null));
				cs.saveToCalendar(lessons, "example@example.com");

				long startMillis = System.currentTimeMillis();
				Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
				builder.appendPath("time");
				ContentUris.appendId(builder, startMillis);
				Intent openCalendar = new Intent(Intent.ACTION_VIEW).setData(builder.build());
				startActivity(openCalendar);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_view);
		
		Intent i = new Intent(this, DailySetter.class);
		i.setAction(DailySetter.ACTION_SET_ALL);
		this.sendBroadcast(i);
		
		Lesson.setLectureName(getString(R.string.lecture));
		Lesson.setSeminarName(getString(R.string.seminar));
		Lesson.setLaboratoryName(getString(R.string.laboratory));

		CalendarSaver.setTeacherCall(getString(R.string.teacher));
		CalendarSaver.setAuditoriumCall(getString(R.string.auditorium));
		CalendarSaver.setGroupsCall(getString(R.string.groups));	

		Button button = (Button) findViewById(R.id.buttonDoImport);
		button.setOnClickListener(new ImportListener());
		
		button = (Button) findViewById(R.id.buttonTimetable);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SuggestDoImportActivity.this, WeekActivity.class);
				SuggestDoImportActivity.this.startActivity(i);
			}
		});
		
		button = (Button) findViewById(R.id.buttonSoundOn);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SuggestDoImportActivity.this, SoundManager.class);
				i.setAction(SoundManager.ON_ACTION);
				SuggestDoImportActivity.this.sendBroadcast(i);
			}
		});
		
		button = (Button) findViewById(R.id.buttonSoundOff);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SuggestDoImportActivity.this, SoundManager.class);
				i.setAction(SoundManager.OFF_ACTION);
				SuggestDoImportActivity.this.sendBroadcast(i);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (!pref.contains("pref_group")) {
			Intent i = new Intent(this, SettingsActivity.class);
			i.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, ScheduleFragment.class.getName());
			this.startActivity(i);
		}
		
		TextView txt = (TextView) findViewById(R.id.textViewGroupHelper);
		txt.setText(pref.getString("pref_group", ""));
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
			openSettings();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openSettings() {
		Intent i = new Intent(this, SettingsActivity.class);
		this.startActivity(i);
	}

}
