package ru.bmstu.schedule.models.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class UniversityStructureReader {
	public static Map<String, List<String>> getFaculties(InputStream dataFile) {
		Map<String, List<String>> faculties = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document data = builder.parse(dataFile);
			NodeList facultyNodes = data.getElementsByTagName("faculty");

			faculties = new HashMap<String, List<String>>(
					facultyNodes.getLength());
			for (int i = 0; i < facultyNodes.getLength(); i++) {
				Node faculty = facultyNodes.item(i);
				String facultyName = faculty.getAttributes()
						.getNamedItem("name").getNodeValue();

				List<String> cathedras = getCathedras(faculty);
				faculties.put(facultyName, cathedras);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return faculties;
	}
	
	public static List<String> getCathedras(String faculty, InputStream dataFile) {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document data = builder.parse(dataFile);
			NodeList facultyNodes = data.getElementsByTagName("faculty");
			for (int i = 0; i < facultyNodes.getLength(); i++) {
				Node facultyNode = facultyNodes.item(i);
				String facultyName = facultyNode.getAttributes()
						.getNamedItem("name").getNodeValue();
				if (facultyName.equals(faculty))
					return getCathedras(facultyNode);
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getCathedras(Node faculty) {
		NodeList cathedraNodes = faculty.getChildNodes();

		List<String> cathedras = new ArrayList<String>(
				cathedraNodes.getLength() / 2);
		for (int j = 0; j < cathedraNodes.getLength(); j++) {
			Node cath = cathedraNodes.item(j);
			if (cath.getNodeType() == Node.ELEMENT_NODE) {
				String cathedraName = cath.getAttributes()
						.getNamedItem("name").getNodeValue();
				cathedras.add(cathedraName);
			}
		}
		return cathedras;
	}
}
