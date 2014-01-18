package br.ufrn.dimap.ttracker.data;

import java.util.HashSet;
import java.util.Set;

public class TaskTypeSet {
	private TaskType name;
	private Set<Task> tasks;
	private Float inclusion;
	private Float precision;

	public TaskTypeSet(TaskType name) {
		this.name = name;
		this.tasks = new HashSet<Task>();
		this.inclusion = 0F;
		this.precision = 0F;
	}

	public String getName() {
		return name.getName();
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public Float getInclusion() {
		return inclusion;
	}

	public void setInclusion(Float inclusion) {
		this.inclusion = inclusion;
	}

	public Float getPrecision() {
		return precision;
	}

	public void setPrecision(Float precision) {
		this.precision = precision;
	}

}
