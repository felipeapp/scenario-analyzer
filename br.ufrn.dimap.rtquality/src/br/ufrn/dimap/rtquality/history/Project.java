package br.ufrn.dimap.rtquality.history;

import java.util.Set;

import org.eclipse.core.resources.IProject;

import br.ufrn.dimap.ttracker.data.Task;

public class Project {

	private String projectPath;
	private String projectName;
	private IProject iProject;
	private boolean aspectJNature;
	private boolean executeTests;
	private Set<String> packagesToTest;

	public Project(String projectPath, String projectName, IProject iProject, boolean aspectJNature) {
		this.projectPath = projectPath;
		this.projectName = projectName;
		this.iProject = iProject;
		this.aspectJNature = aspectJNature;
		this.executeTests = false;
		this.packagesToTest = null;
	}

	public Project(String projectPath, String projectName, IProject iProject, boolean aspectJNature, Set<String> packagesToTest) throws Exception {
		this.projectPath = projectPath;
		this.projectName = projectName;
		this.iProject = iProject;
		this.aspectJNature = aspectJNature;
		if(packagesToTest == null || packagesToTest.isEmpty())
			throw new Exception("The packagesToTest can not be null neither empty.");
		this.executeTests = true;
		this.packagesToTest = packagesToTest;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public IProject getIProject() {
		return iProject;
	}

	public void setIProject(IProject iProject) {
		this.iProject = iProject;
	}

	public boolean isAspectJNature() {
		return aspectJNature;
	}

	public void setAspectJNature(boolean aspectJNature) {
		this.aspectJNature = aspectJNature;
	}

	public boolean isExecuteTests() {
		return executeTests;
	}

	public void setExecuteTests(boolean executeTests) {
		this.executeTests = executeTests;
	}

	public Set<String> getPackagesToTest() {
		return packagesToTest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((projectPath == null) ? 0 : projectPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (projectPath == null) {
			if (other.projectPath != null)
				return false;
		} else if (!projectPath.equals(other.projectPath))
			return false;
		return true;
	}

	public int compareTo(Project other) {
        return projectPath.compareTo(other.projectPath);
    }
	
}
