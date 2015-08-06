package ru.bmstu.schedule.graph.preferences;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.invisible.DailySetter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class ScheduleFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (! pref.contains("pref_group")) {
			GroupPreference groupPreference = (GroupPreference) getPreferenceScreen().findPreference("pref_group");
			groupPreference.show();
		}
		
		ListPreference prefSound = (ListPreference) getPreferenceScreen().findPreference("pref_sound");
		prefSound.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Intent i = new Intent(ScheduleFragment.this.getActivity(), DailySetter.class);
				i.setAction(DailySetter.ACTION_SET_ALL);
				ScheduleFragment.this.getActivity().sendBroadcast(i);
				return true;
			}
		});
	}
}