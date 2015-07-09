package ru.bmstu.schedule.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class Group extends UIDgettable {
	private String name;
	private Set<String> streamUIDs = new HashSet<String>();
	
	public Group(JSONObject group) throws JSONException {
		name = group.getString("name");
		
		Iterator<?> keys = group.keys();
		while( keys.hasNext() ) {
		    String key = (String)keys.next();
		    streamUIDs.add(key);
		}
		UIDgettable.addObject(group.getString("uid"), this);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
