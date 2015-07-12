package ru.bmstu.schedule.calendar.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import ru.bmstu.schedule.models.Auditorium;
import ru.bmstu.schedule.models.Group;
import ru.bmstu.schedule.models.Lecturer;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.Stream;

public class ModelsInitializer {
	private static String auditoriumDefaultName = "aud";
	private static String groupDefaultName      = "group";
	private static String lecturerDefaultName   = "pub";
	private static String streamDefaultName     = "stream";
	private static String lessonDefaultName     = "item";
	
	public static List<Lesson> readLessonsFromJSON(InputStream jsonStream) {
		return readLessonsFromJSON(jsonStream, "UTF-8");
	}
	
	public static List<Lesson> readLessonsFromJSON(InputStream jsonStream, String encoding) {
		String json = fileToString(jsonStream, encoding);
        
        try {
        	JSONObject all = new JSONObject(json);
        	    	
        	ModelsInitializer.extractInfo(all, auditoriumDefaultName, Auditorium.class);
        	ModelsInitializer.extractInfo(all, groupDefaultName, Group.class);
        	ModelsInitializer.extractInfo(all, lecturerDefaultName, Lecturer.class);
        	ModelsInitializer.extractInfo(all, streamDefaultName, Stream.class);
    		        	
        	List<Lesson> lessons = new LinkedList<Lesson>();
        	JSONArray items = all.getJSONArray(lessonDefaultName);
        	
        	for(int i = 0; i < items.length(); i++) {
        		Lesson lesson = new Lesson(items.getJSONObject(i));
        		lessons.add(lesson);
           	}
        	return lessons;
        } catch (JSONException e) {
        	Log.e("Parsing JSON", e.getLocalizedMessage());
        }
        return null;
	}
	
	private static String fileToString(InputStream jsonFile, String encoding) {
		String json = null;
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(jsonFile, encoding));
        	
        	StringBuilder builder = new StringBuilder();
        	String line;
        	
        	while ((line = reader.readLine()) != null) {
        		builder.append(line);
        	}
        	json = builder.toString();
        } catch (IOException e) {
        	Log.e("Reading JSON file", e.getLocalizedMessage());
        	json = null;
        }
        return json;
	}
	
	private interface SimpleHandler<T> {
		public void handle(T object);
	}
	
	private static <T> void extractInfo(JSONObject all,	String classJSONName, Class<T> c)  throws JSONException {
		extractInfo(all, classJSONName, c, null);
	}
	
	private static <T> void extractInfo(
			JSONObject all,
			String classJSONName,
			Class<T> c,
			SimpleHandler<? super T> handler) throws JSONException {
		JSONObject arr = all.getJSONObject(classJSONName);
    	Iterator<?> keys = arr.keys();
		while( keys.hasNext() ) {
		    String key = (String)keys.next();
		    try {
				T instance = (T) c.getConstructor(JSONObject.class).newInstance(arr.getJSONObject(key));
				if (handler != null)
					handler.handle(instance);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getAuditoriumDefaultName() {
		return auditoriumDefaultName;
	}

	public static void setAuditoriumDefaultName(String auditoriumDefaultName) {
		ModelsInitializer.auditoriumDefaultName = auditoriumDefaultName;
	}

	public static String getGroupDefaultName() {
		return groupDefaultName;
	}

	public static void setGroupDefaultName(String groupDefaultName) {
		ModelsInitializer.groupDefaultName = groupDefaultName;
	}

	public static String getLecturerDefaultName() {
		return lecturerDefaultName;
	}

	public static void setLecturerDefaultName(String lecturerDefaultName) {
		ModelsInitializer.lecturerDefaultName = lecturerDefaultName;
	}

	public static String getStreamDefaultName() {
		return streamDefaultName;
	}

	public static void setStreamDefaultName(String streamDefaultName) {
		ModelsInitializer.streamDefaultName = streamDefaultName;
	}

	public static String getLessonDefaultName() {
		return lessonDefaultName;
	}

	public static void setLessonDefaultName(String lessonDefaultName) {
		ModelsInitializer.lessonDefaultName = lessonDefaultName;
	}
}
