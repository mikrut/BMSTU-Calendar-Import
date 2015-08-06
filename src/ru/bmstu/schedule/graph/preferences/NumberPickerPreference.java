package ru.bmstu.schedule.graph.preferences;

import ru.bmstu.schedule.calendar.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberPickerPreference extends DialogPreference {

	private static final int DEFAULT_VALUE = 0;
	protected int value;
	protected int minValue = 0;
	protected int maxValue = 10;

	protected TextView summaryView;
	protected NumberPicker edit;

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		//TypedArray a=getContext().obtainStyledAttributes(
		//         attrs,
		//         R.styleable.NumberPickerPreference);
		
		setDialogLayoutResource(R.layout.number_picker);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		setDialogIcon(null);
		setSummary(getSummary());

		setMaxValue(attrs.getAttributeIntValue(R.styleable.NumberPickerPreference_max, 0));
		setMinValue(attrs.getAttributeIntValue(R.styleable.NumberPickerPreference_min, 10));
	}

	public void setSummary(String summary) {
		if (summaryView != null)
			summaryView.setText(summary);
	}
	
	public void setValue(int value) {
		if (minValue <= value && value <= maxValue) {
			this.value = value;
			if (edit != null)
				edit.setValue(value);
		}
	}
	
	public int getValue() {
		if (edit != null) {
			return value = edit.getValue();
		} else {
			return value;
		}
	}

	public void setMinValue(int minValue) {
		int oldValue = getValue();
		if (oldValue < minValue)
			oldValue = minValue;

		this.minValue = minValue;
		if (maxValue < minValue) {
			maxValue = minValue;
		}
		
		if (edit != null) {
			edit.setMaxValue(maxValue);
			edit.setMinValue(minValue);
		}
		setValue(oldValue);
	}

	public void setMaxValue(int maxValue) {
		int oldValue = getValue();
		if (oldValue > maxValue)
			oldValue = maxValue;

		this.maxValue = maxValue;
		if (minValue > maxValue) {
			minValue = maxValue;
		}
		
		if (edit != null) {
			edit.setMaxValue(maxValue);
			edit.setMinValue(minValue);
		}
		setValue(oldValue);
	}

	@Override
	public void onBindDialogView(View view){
		summaryView = (TextView) view.findViewById(R.id.numberPickerText);
		edit = (NumberPicker) view.findViewById(R.id.numberPickerInput);

		edit.setMinValue(minValue);
		edit.setMaxValue(maxValue);
		setValue(value);
		
		summaryView.setText(getSummary());
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			persistInt(getValue());
		}
	}
	
	@Override 
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
	    if (restorePersistedValue) {
	        // Restore existing state
	        setValue(this.getPersistedInt(minValue));
	    } else {
	        // Set default state from the XML attribute
	    	setValue((Integer) defaultValue);
	        persistInt(getValue());
	    }
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
	    return a.getInteger(index, DEFAULT_VALUE);
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
	    final Parcelable superState = super.onSaveInstanceState();
	    // Check whether this Preference is persistent (continually saved)
	    if (isPersistent()) {
	        // No need to save instance state since it's persistent,
	        // use superclass state
	        return superState;
	    }

	    // Create instance of custom BaseSavedState
	    final NumberSavedState myState = new NumberSavedState(superState);
	    // Set the state's value with the class member that holds current
	    // setting value
	    myState.value = getValue();
	    return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
	    // Check whether we saved the state in onSaveInstanceState
	    if (state == null || !state.getClass().equals(NumberSavedState.class)) {
	        // Didn't save the state, so call superclass
	        super.onRestoreInstanceState(state);
	        return;
	    }

	    // Cast state to custom BaseSavedState and pass to superclass
	    NumberSavedState myState = (NumberSavedState) state;
	    super.onRestoreInstanceState(myState.getSuperState());
	    
	    // Set this Preference's widget to reflect the restored state
	    setValue(myState.value);
	}
}
