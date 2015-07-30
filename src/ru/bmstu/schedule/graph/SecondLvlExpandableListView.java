package ru.bmstu.schedule.graph;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

public class SecondLvlExpandableListView extends ExpandableListView {

    public SecondLvlExpandableListView(Context context) {
	super(context);
    }

    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
	int height = 0;
	ListAdapter adapter = getAdapter();
	for (int i = 0; i < adapter.getCount(); i++) {
	    View v = adapter.getView(i, null, this);
	    v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
		    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	    height += v.getMeasuredHeight() + 1;
	}
	
	heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
