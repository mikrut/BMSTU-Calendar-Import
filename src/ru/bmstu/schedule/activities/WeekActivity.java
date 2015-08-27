package ru.bmstu.schedule.activities;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.graph.week.WeekPageAdapter;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class WeekActivity extends FragmentActivity {
	private WeekPageAdapter adapter;
	private ViewPager mViewPager;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
	}
	
	private boolean haveInfo() {
		return ModelsInitializer.getSemesterInfo(this) != null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (haveInfo()) {
			setContentView(R.layout.slider);
			adapter = new WeekPageAdapter(getSupportFragmentManager(), this);
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(adapter);
		} else {
			setContentView(R.layout.no_semester_info);
		}
	}
	
}
