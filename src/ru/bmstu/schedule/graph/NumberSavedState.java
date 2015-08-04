package ru.bmstu.schedule.graph;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference.BaseSavedState;

public class NumberSavedState extends BaseSavedState {
	int value;
	
	public NumberSavedState(Parcelable superState) {
        super(superState);
    }

    public NumberSavedState(Parcel source) {
        super(source);
        // Get the current preference's value
        value = source.readInt();  // Change this to read the appropriate data type
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        // Write the preference's value
        dest.writeInt(value);  // Change this to write the appropriate data type
    }

    // Standard creator object using an instance of this class
    public static final Parcelable.Creator<NumberSavedState> CREATOR =
            new Parcelable.Creator<NumberSavedState>() {

        public NumberSavedState createFromParcel(Parcel in) {
            return new NumberSavedState(in);
        }

        public NumberSavedState[] newArray(int size) {
            return new NumberSavedState[size];
        }
    };

}
