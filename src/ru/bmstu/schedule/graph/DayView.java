package ru.bmstu.schedule.graph;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.models.Lesson;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class DayView extends TableLayout {
	private TimetableRowView[] pairsViews = new TimetableRowView[Lesson.getTimetable().length];

	public DayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mView = mInflater.inflate(R.layout.day_view, this, true);
		
		if (attrs != null) {
			TextView dayCaption = (TextView) mView.findViewById(R.id.dayName);
			for (int i = 0; i < attrs.getAttributeCount(); i++) {
				String attr = attrs.getAttributeName(i);
				if (attr != null && attr.equals("dayName"))
					dayCaption.setText(attrs.getAttributeValue(i));
			}
			
			int[][][] timetable = Lesson.getTimetable();
			int timetableRowViewIndex = 0;
			LinearLayout wrapper = (LinearLayout) this.getChildAt(0);
			for (int i = 0; i < wrapper.getChildCount(); i++) {
				View child = wrapper.getChildAt(i);
				if (child instanceof TimetableRowView) {
					TimetableRowView row = (TimetableRowView) child;
					row.setTime(timetable[i-1]);
					pairsViews[timetableRowViewIndex++] = row;
				}
			}
		}
	}
	
	public TimetableRowView getPairView(int pairIndex) {
		return pairsViews[pairIndex];
	}
	
	public void clearPairsInfos() {
		for (TimetableRowView pair : pairsViews)
			pair.clearInfo();
	}
}
