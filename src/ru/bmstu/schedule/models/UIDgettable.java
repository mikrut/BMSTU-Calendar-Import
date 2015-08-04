package ru.bmstu.schedule.models;

import java.util.HashMap;
import java.util.Map;

public abstract class UIDgettable {
	private static Map<String, Object> objectMap = new HashMap<String, Object>();

	protected static void addObject(String uid, Object obj) {
		objectMap.put(uid, obj);
	}

	public static Object getByUID(String uid) {
		return objectMap.get(uid);
	}
}
