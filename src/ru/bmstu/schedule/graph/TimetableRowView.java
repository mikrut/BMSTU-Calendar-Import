package ru.bmstu.schedule.graph;

import java.util.Locale;

import ru.bmstu.schedule.calendar.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimetableRowView extends LinearLayout {
	private TextView nameView, timeView, lecturerView, auditoriumView, lecturerHelper, auditoriumHelper, pairTypeView;

	public TimetableRowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(HORIZONTAL);
		this.setPadding(0, 2, 0, 0);
		this.setBackgroundColor(0xff000000);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.timetable_row, this, true);
		//View v = inflate(getContext(), R.layout.timetable_row, this);
		
		nameView = (TextView) v.findViewById(R.id.timetable_row_name);
		timeView = (TextView) v.getRootView().findViewById(R.id.timetable_row_time);
		lecturerView = (TextView) v.findViewById(R.id.timetable_row_lecturer);
		auditoriumView = (TextView) v.findViewById(R.id.timetable_row_auditorium);
		lecturerHelper = (TextView) v.findViewById(R.id.timetable_row_lecturerHelper);
		auditoriumHelper = (TextView) v.findViewById(R.id.timetable_row_auditoriumHelper);
		pairTypeView = (TextView) v.findViewById(R.id.timetable_row_pairType);
		
		if (attrs != null) {
			for (int i = 0; i < attrs.getAttributeCount(); i++) {
				String attr = attrs.getAttributeName(i);
				
				if (attr != null) {
					if(attr.equals("time"))
						setTime(attrs.getAttributeValue(i));
					else if (attr.equals("name"))
						setName(attrs.getAttributeValue(i));
					else if (attr.equals("lecturer"))
						setLecturer(attrs.getAttributeValue(i));
					else if (attr.equals("auditorium"))
						setAuditorium(attrs.getAttributeValue(i));
					else if (attr.equals("pairType"))
						setPairType(attrs.getAttributeValue(i));
				}
			}
		}
	}
	
	public void setName(String name) {
		nameView.setText(name);
	}
	
	public String getName() {
		return nameView.getText().toString();
	}
	
	public void setTime(String time) {
		timeView.setText(time);
	}
	
	public void setTime(int[][] time) {
		String text = String.format(Locale.getDefault(), "%d:%02d %d:%02d",
				time[0][0], time[0][1],
				time[1][0], time[1][1]);
		setTime(text);
	}
	
	public void setPairType(String pairType) {
		pairTypeView.setText(pairType);
		showPairType();
	}
	
	public void setLecturer(String lecturer) {
		lecturerView.setText(lecturer);
		showLecturer();
	}
	
	public void setAuditorium(String auditorium) {
		auditoriumView.setText(auditorium);
		showAuditorium();
	}
	
	public void hideAuditorium() {
		auditoriumView.setVisibility(GONE);
		auditoriumHelper.setVisibility(GONE);
	}
	
	public void showAuditorium() {
		auditoriumView.setVisibility(VISIBLE);
		auditoriumHelper.setVisibility(VISIBLE);
	}
	
	public void hideLecturer() {
		lecturerView.setVisibility(GONE);
		lecturerHelper.setVisibility(GONE);
	}
	
	public void showLecturer() {
		lecturerView.setVisibility(VISIBLE);
		lecturerHelper.setVisibility(VISIBLE);
	}
	
	public void hidePairType() {
		pairTypeView.setVisibility(GONE);
	}
	
	public void showPairType() {
		pairTypeView.setVisibility(VISIBLE);
	}
	
	public void clearInfo() {
		hideLecturer();
		hideAuditorium();
		hidePairType();
		setName("");
	}
}
