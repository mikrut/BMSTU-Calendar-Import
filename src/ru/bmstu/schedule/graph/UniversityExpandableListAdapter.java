package ru.bmstu.schedule.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class UniversityExpandableListAdapter extends BaseExpandableListAdapter {

    List<String> keys = new ArrayList<String>();
    List<List<String>> values = new ArrayList<List<String>>();
    Context context;
    List<ExpandableListAdapter> adapters = new ArrayList<ExpandableListAdapter>(); 

    public UniversityExpandableListAdapter(Context context, Map<String, List<String>> structure, String json) throws IOException {
	Set<String> keys = structure.keySet();
	for(String key: keys) {
	    this.keys.add(key);
	    values.add(structure.get(key));
	    adapters.add(new FacultyExpandableListAdapter(context, json, structure.get(key)));
	}
	this.context = context;
    }

    @Override
    public int getGroupCount() {
	return keys.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
	return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
	return keys.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
	return values.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
	return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
	return childPosition;
    }

    @Override
    public boolean hasStableIds() {
	return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	if (convertView == null)
	    convertView = new TextView(context);
	((TextView) convertView).setText(keys.get(groupPosition));;
	((TextView) convertView).setGravity(Gravity.CENTER);
	return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
	    ViewGroup parent) {
	if (null == convertView)
	    convertView = new SecondLvlExpandableListView(context);
	((ExpandableListView) convertView).setAdapter(adapters.get(groupPosition));
	return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
	return true;
    }

}
