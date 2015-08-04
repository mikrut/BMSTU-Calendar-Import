package ru.bmstu.schedule.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class Stream extends UIDgettable {
	private Set<Group> groups = new HashSet<Group>();

	public Stream(JSONObject stream) throws JSONException {
		Iterator<?> keys = stream.getJSONObject("group").keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			groups.add((Group) UIDgettable.getByUID(key));
		}
		UIDgettable.addObject(stream.getString("uid"), this);
	}

	@Override
	public String toString() {
		return android.text.TextUtils.join(",", groups);
	}
}
