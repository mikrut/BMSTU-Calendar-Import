package ru.bmstu.schedule.activities;

import java.util.List;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.graph.preferences.ScheduleFragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
    
    @Override
    protected boolean isValidFragment(String fragmentName) {
	  return ScheduleFragment.class.getName().equals(fragmentName);
	}
}
