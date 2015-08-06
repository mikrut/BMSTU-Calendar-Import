package ru.bmstu.schedule.models.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import ru.bmstu.schedule.models.Auditorium;
import ru.bmstu.schedule.models.Group;
import ru.bmstu.schedule.models.Lecturer;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.SemesterInfo;
import ru.bmstu.schedule.models.Stream;

public abstract class ModelsInitializer {
	private static String auditoriumDefaultName = "aud";
	private static String groupDefaultName = "group";
	private static String lecturerDefaultName = "pub";
	private static String streamDefaultName = "stream";
	private static String lessonDefaultName = "item";
	
	@Nullable
	public static SemesterInfo getSemesterInfo(Context context) {
		SemesterInfo info = null;
		try {
			InputStream dataFile = context.getAssets().open("dates.xml");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document data = builder.parse(dataFile);
			
			Calendar now = Calendar.getInstance();
			int yearNumber = getLearningYear(now);
			int semester   = getSemester(now);
			
			Node yearNode = findYearNode(data, yearNumber);
			if (yearNode != null) {
				Node semesterNode = findSemesterData(yearNode, semester);
				
				if (semesterNode != null) {
					info = new SemesterInfo(semesterNode.getAttributes());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return info;
	}
	
	private static int getLearningYear(Calendar now) {
		int yearNumber = now.get(Calendar.YEAR);
		if (now.get(Calendar.MONTH) < Calendar.JULY)
			yearNumber--;
		return yearNumber;
	}
	
	private static int getSemester(Calendar now) {
		return now.get(Calendar.MONTH) < Calendar.JULY ? 2 : 1;
	}
	
	private static Node findYearNode(Document data, int learningYearNumber) {
		NodeList years = data.getElementsByTagName("year");
		Node yearNode = null;
		for (int i = years.getLength() - 1; i >= 0; i--) {
			yearNode = years.item(i);
			int nodeYearNumber = Integer.parseInt(yearNode.getAttributes().getNamedItem("year").getNodeValue());
			if (nodeYearNumber == learningYearNumber)
				return yearNode;
		}
		return yearNode;
	}
	
	private static Node findSemesterData(Node yearNode, int semester) {
		NodeList childNodes = yearNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap attributes = childNode.getAttributes();
				int week = Integer.parseInt(attributes.getNamedItem("weeks").getNodeValue());
				int semesterNumber = Integer.parseInt(attributes.getNamedItem("semester").getNodeValue());
				
				if (17 == week && semesterNumber == semester) {				
					return childNode;
				}
			}
		}
		return null;
	}	

	public static List<Lesson> readLessonsFromJSON(String jsonString, String groupName) {
		try {
			JSONObject all = new JSONObject(jsonString);
			
			ModelsInitializer.extractInfo(all, auditoriumDefaultName, Auditorium.class);
			ModelsInitializer.extractInfo(all, groupDefaultName, Group.class);
			ModelsInitializer.extractInfo(all, lecturerDefaultName,	Lecturer.class);
			ModelsInitializer.extractInfo(all, streamDefaultName, Stream.class);
			
			List<Lesson> lessons = new LinkedList<Lesson>();
			JSONArray items = all.getJSONArray(lessonDefaultName);

			for (int i = 0; i < items.length(); i++) {
				Lesson lesson = new Lesson(items.getJSONObject(i));
				if (lesson.hasGroup(groupName))
					lessons.add(lesson);
			}
			return lessons;
		} catch (JSONException e) {
			Log.e("Parsing JSON", e.getLocalizedMessage());
		}
		return null;
	}

	public static List<Group> getGroupsForCathedra(String json,
			final String cathedraName) {
		final ArrayList<Group> groups = new ArrayList<Group>();
		try {
			JSONObject all = new JSONObject(json);
			extractInfo(all, groupDefaultName, Group.class,
					new SimpleHandler<Group>() {
						@Override
						public void handle(Group object) {
							if (object.toString().matches(
									"^" + cathedraName + "-.*$"))
								groups.add(object);
						}
					});
		} catch (JSONException e) {
			Log.e("Parsing JSON", e.getLocalizedMessage());
		}
		return groups;
	}

	public static String fileToString(InputStream jsonFile, String encoding) {
		String json = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					jsonFile, encoding));

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

	private static <T> void extractInfo(JSONObject all, String classJSONName,
			Class<T> c) throws JSONException {
		extractInfo(all, classJSONName, c, null);
	}

	private static <T> void extractInfo(JSONObject all, String classJSONName,
			Class<T> c, SimpleHandler<? super T> handler) throws JSONException {
		JSONObject arr = all.getJSONObject(classJSONName);
		Iterator<?> keys = arr.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				T instance = (T) c.getConstructor(JSONObject.class)
						.newInstance(arr.getJSONObject(key));
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
