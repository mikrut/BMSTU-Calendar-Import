package ru.bmstu.schedule.calendar;

import java.io.IOException;

import ru.bmstu.schedule.calendar.helpers.FacultyExpandableListAdapter;
import ru.bmstu.schedule.calendar.helpers.Logger;
import ru.bmstu.schedule.calendar.helpers.UniversityStructureReader;
import android.support.v7.app.ActionBarActivity;
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
		
		ExpandableListView lv = (ExpandableListView) this.findViewById(R.id.expandableGroupList);
		try {
			ExpandableListAdapter adapter = new FacultyExpandableListAdapter(this, getAssets().open("rasp.json"));
			lv.setAdapter(adapter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		java.util.Map<String, java.util.List<String>> un;
		try {
			un = UniversityStructureReader.getFaculties(getAssets().open("university.xml"));
		
			for (String key: un.keySet()) {
				android.util.Log.v("parser", un.get(key).get(0));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
