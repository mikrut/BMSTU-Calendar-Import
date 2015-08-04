package ru.bmstu.schedule.activities;

import java.io.IOException;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.calendar.R.id;
import ru.bmstu.schedule.calendar.R.layout;
import ru.bmstu.schedule.calendar.R.menu;
import ru.bmstu.schedule.calendar.R.string;
import ru.bmstu.schedule.calendar.helpers.CalendarSaver;
import ru.bmstu.schedule.calendar.helpers.Logger;
import ru.bmstu.schedule.calendar.helpers.ModelsInitializer;
import ru.bmstu.schedule.calendar.helpers.UniversityStructureReader;
import ru.bmstu.schedule.graph.FacultyExpandableListAdapter;
import ru.bmstu.schedule.graph.UniversityExpandableListAdapter;
import ru.bmstu.schedule.models.Lesson;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class MainActivity extends ActionBarActivity {
	Logger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Lesson.setLectureName(getString(R.string.lecture));
		Lesson.setSeminarName(getString(R.string.seminar));

		CalendarSaver.setTeacherCall(getString(R.string.teacher));
		CalendarSaver.setAuditoriumCall(getString(R.string.auditorium));
		CalendarSaver.setGroupsCall(getString(R.string.groups));

		ExpandableListView lv = (ExpandableListView) this
				.findViewById(R.id.expandableGroupList);
		try {
			java.util.Map<String, java.util.List<String>> un = UniversityStructureReader
					.getFaculties(getAssets().open("university.xml"));
			String json = ModelsInitializer.fileToString(
					getAssets().open("rasp.json"), "UTF-8");
			ExpandableListAdapter adapter = new UniversityExpandableListAdapter(
					this, un, json);
			lv.setAdapter(adapter);
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
