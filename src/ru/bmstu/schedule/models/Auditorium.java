package ru.bmstu.schedule.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Auditorium extends UIDgettable {
	private String name;
	private int capacity;
	
	public Auditorium(JSONObject aud) throws JSONException {
		name = aud.getString("name");
		capacity = aud.getInt("capacity");
		
		UIDgettable.addObject(aud.getString("uid"), this);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
