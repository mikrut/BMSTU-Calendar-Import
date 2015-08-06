package ru.bmstu.schedule.graph;

import java.util.Locale;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.models.Lesson;
import android.content.Context;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DayView extends TableLayout {
	private TextView[] pairsTexts = new TextView[Lesson.getTimetable().length];

	public DayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.day_view, this, true);
		
		if (!isInEditMode()) {
			TextView dayCaption = (TextView) findViewById(R.id.title);
			for (int i = 0; i < attrs.getAttributeCount(); i++)
				if (attrs.getAttributeName(i).equals("dayName"))
					dayCaption.setText(attrs.getAttributeValue(i));
			
			int[][][] timetable = Lesson.getTimetable();
			TableLayout table = (TableLayout) this.getChildAt(0);
			for (int i = 1; i < table.getChildCount(); i++) {
				View child = table.getChildAt(i);
				if (child instanceof TableRow
						&& ((TableRow) child).getChildCount() == 2) {
					TableRow row = (TableRow) child;
					TextView timeText = (TextView) row.getChildAt(0);
					TextView pairText = (TextView) row.getChildAt(1);
					
					String text = String.format(Locale.getDefault(), "%d:%02d - %d:%02d",
							timetable[i-1][0][0], timetable[i-1][0][1],
							timetable[i-1][1][0], timetable[i-1][1][1]);
					pairsTexts[i-1] = pairText;
					timeText.setText(text);
				}
			}
		}
	}
	
	public void setPairText(int pairIndex, Spanned text) {
		if (0 <= pairIndex && pairIndex <= pairsTexts.length)
			pairsTexts[pairIndex].setText(text);
	}
	
	public void setPairText(int pairIndex, String text) {
		if (0 <= pairIndex && pairIndex <= pairsTexts.length)
			pairsTexts[pairIndex].setText(text);
	}

}
