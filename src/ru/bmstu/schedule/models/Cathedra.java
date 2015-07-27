package ru.bmstu.schedule.models;

import java.util.ArrayList;
import java.util.List;

public class Cathedra {
	private String name;
	private List<Group> groups = new ArrayList<Group>();
	
	public Cathedra(String name) {
		this.name = name;
	}
	
	public void setGroupsList(List<Group> list) {
		groups = list;
	}
	
	public void addGroup(Group group) {
		groups.add(group);
	}
	
	public List<Group> getGroups() {
		return groups;
	}
	
	public Group get(int position) {
		return groups.get(position);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int size() {
		return groups.size();
	}
}
