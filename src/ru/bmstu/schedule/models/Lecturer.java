package ru.bmstu.schedule.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Lecturer extends UIDgettable {
	private String name;
	
	public Lecturer(JSONObject pub) throws JSONException {
		name = pub.getString("name");
		UIDgettable.addObject(pub.getString("uid"), this);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
