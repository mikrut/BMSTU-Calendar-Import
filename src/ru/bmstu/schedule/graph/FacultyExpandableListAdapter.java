package ru.bmstu.schedule.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.bmstu.schedule.models.Cathedra;
import ru.bmstu.schedule.models.Group;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class FacultyExpandableListAdapter extends BaseExpandableListAdapter {

	List<Cathedra> cathedras = new ArrayList<Cathedra>();
	private Context context;
	private OnClickListener elementListener;
	
	public FacultyExpandableListAdapter(Context context, String json,
			List<String> cathedraNames) throws IOException {
		this.context = context;	
		for (String name : cathedraNames) {
			Cathedra cathedra = new Cathedra(name);
			cathedra.setGroupsList(ModelsInitializer.getGroupsForCathedra(json,
					name));
			cathedras.add(cathedra);
		}
	}

	public FacultyExpandableListAdapter(Context context, String json,
			List<String> cathedraNames, OnClickListener elementListener) throws IOException {
		this(context, json, cathedraNames);
		this.elementListener = elementListener;
	}

	@Override
	public int getGroupCount() {
		return cathedras.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return cathedras.get(groupPosition).size();
	}

	@Override
	public Cathedra getGroup(int groupPosition) {
		return cathedras.get(groupPosition);
	}

	@Override
	public Group getChild(int groupPosition, int childPosition) {
		return cathedras.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO: GroupId - cathedra index
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO: ChildId - learning group index (e.g.: IU1-23 -> 23)
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = new TextView(context);
		}
		((TextView) convertView).setText(cathedras.get(groupPosition).toString());
		((TextView) convertView).setGravity(Gravity.CENTER);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = new TextView(context);
		}
		((TextView) convertView).setText(cathedras.get(groupPosition).get(childPosition).toString());
		if (null != elementListener)
			((TextView) convertView).setOnClickListener(elementListener);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
