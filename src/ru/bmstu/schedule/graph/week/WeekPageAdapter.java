package ru.bmstu.schedule.graph.week;

import java.util.Calendar;

import ru.bmstu.schedule.models.SemesterInfo;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class WeekPageAdapter extends FragmentStatePagerAdapter {
	private Context context;
	
	public WeekPageAdapter(FragmentManager fManager, Context c) {
		super(fManager);
		context = c;
	}

	@Override
	public Fragment getItem(int position) {
		WeekFragment item = new WeekFragment();
		Bundle args = new Bundle();
		args.putInt(WeekFragment.ARG_WEEK_NUMBER, position + 1);
		item.setArguments(args);
		return item;
	}

	@Override
	public int getCount() {
		Integer count = getWeekAmount();
		return count != null ? count : 0;
	}
	
	@Nullable
	private Integer getWeekAmount() {
		SemesterInfo info = ModelsInitializer.getSemesterInfo(context);
		if (null == info)
			return null;
		return info.theoryEnd.get(Calendar.WEEK_OF_YEAR) - info.theoryBegin.get(Calendar.WEEK_OF_YEAR) + 1;
	}
}
