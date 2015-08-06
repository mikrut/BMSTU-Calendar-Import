package ru.bmstu.schedule.graph.preferences;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.graph.FacultyExpandableListAdapter;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import ru.bmstu.schedule.models.readers.UniversityStructureReader;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupPreference extends DialogPreference {
	private Button buttonOk;
	private Button[] facultyButtons;
	private ExpandableListView cathedraPicker;
	private TextView helperText;
	
	private Map<String, List<String>> universityStructure;
	
	private String choosenFaculty;
	private boolean isSecondStep = false;
	
	public GroupPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		setSummary(pref.getString("pref_group", ""));
		
		setDialogLayoutResource(R.layout.faculty_picker);
		try {
			universityStructure = UniversityStructureReader.getFaculties(context.getAssets().open("university.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private OnClickListener cathedraPickerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			TextView element = (TextView) v;
			String groupName = element.getText().toString();
			persistString(groupName);
			setSummary(groupName);
			getDialog().dismiss();
		}
	};
	
	private OnClickListener facultyButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Button clicked = (Button) v;
			choosenFaculty = clicked.getText().toString();
			
			for (int i = 0; i < facultyButtons.length; i++)
				facultyButtons[i].setVisibility(View.GONE);
			cathedraPicker.setVisibility(View.VISIBLE);
			isSecondStep = true;
			String json;
			try {
				json = ModelsInitializer.fileToString(
						getContext().getAssets().open("rasp.json"), "UTF-8");
				ExpandableListAdapter facultyAdapter = new FacultyExpandableListAdapter(
						getContext(),
						json,
						universityStructure.get(choosenFaculty),
						cathedraPickerListener);
				cathedraPicker.setAdapter(facultyAdapter);
				helperText.setText(getContext().getString(R.string.choose_group));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	public void onPrepareDialogBuilder (AlertDialog.Builder builder) {
		builder.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (isSecondStep) {
                    	for (int i = 0; i < facultyButtons.length; i++)
            				facultyButtons[i].setVisibility(View.VISIBLE);
            			cathedraPicker.setVisibility(View.GONE);
            			helperText.setText(getContext().getString(R.string.choose_faculty));
                    }
                }
                return true;
            }
        });
	}
	
	@Override
	protected void showDialog (Bundle state) {
		super.showDialog(state);
		
		buttonOk = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
		buttonOk.setVisibility(View.GONE);
	
		Button buttonCancel = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
		buttonCancel.setVisibility(View.GONE);
	}
	
	@Override
	public void onBindDialogView(View view){
		LinearLayout wrapper = (LinearLayout) view;
		facultyButtons = new Button[wrapper.getChildCount() - 2];
		int j = 0;
		for (int i = 0; i < wrapper.getChildCount(); i++) {
			if (wrapper.getChildAt(i) instanceof Button) {
				Button b = (Button) wrapper.getChildAt(i);
				facultyButtons[j++] = b;
				b.setOnClickListener(facultyButtonListener);
			}
		}
		
		cathedraPicker = (ExpandableListView) view.findViewById(R.id.cathedraPicker);
		helperText = (TextView) view.findViewById(R.id.helperText);
		
		helperText.setText(getContext().getString(R.string.choose_faculty));
	}
	
	public void show() {
		onClick();
	}

}
