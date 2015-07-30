package ru.bmstu.schedule.calendar;

import java.io.IOException;

import ru.bmstu.schedule.calendar.helpers.CalendarSaver;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.media.MediaDescriptionCompatApi21.Builder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SuggestDoImportActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_view);
        
        final Button button = (Button) findViewById(R.id.buttonDoImport);
        final Activity act = this;
        button.setOnClickListener(new View.OnClickListener() {
        	final Activity activity = act;
			@Override
			public void onClick(View v) {
				java.util.Calendar semesterStart = java.util.Calendar.getInstance();
				java.util.Calendar semesterEnd = java.util.Calendar.getInstance();
				semesterEnd.add(java.util.Calendar.WEEK_OF_YEAR, 18);
				semesterEnd.set(java.util.Calendar.DAY_OF_WEEK, semesterEnd.getFirstDayOfWeek());
				semesterEnd.set(java.util.Calendar.HOUR, 0);
				semesterEnd.set(java.util.Calendar.MINUTE, 0);
				semesterEnd.set(java.util.Calendar.SECOND, 0);
				CalendarSaver cs = new CalendarSaver(semesterStart, semesterEnd, getString(R.string.calendar_name), "mihanik001@gmail.com", getContentResolver());
				try {
					Bundle extras = activity.getIntent().getExtras();
					if (null != extras) {
						cs.saveToCalendar(getAssets().open("rasp.json"), "example@example.com", extras.getString("CHOOSEN_GROUP_NAME"));
					}
					
					long startMillis = System.currentTimeMillis();
					Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
					builder.appendPath("time");
					ContentUris.appendId(builder, startMillis);
					Intent openCalendar = new Intent(Intent.ACTION_VIEW).setData(builder.build());
					startActivity(openCalendar);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Bundle extras = this.getIntent().getExtras();
		if (null != extras) {
			TextView txt = (TextView) this.findViewById(R.id.textViewGroupHelper);
	        txt.setText(this.getResources().getString(R.string.your_choice)+" "+extras.getString("CHOOSEN_GROUP_NAME"));
		}
	}
	
	
}
