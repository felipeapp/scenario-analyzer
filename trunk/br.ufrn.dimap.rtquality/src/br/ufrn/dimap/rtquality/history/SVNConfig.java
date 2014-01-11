package br.ufrn.dimap.rtquality.history;

import java.util.List;

public class SVNConfig {
	private String svnUrl;
	private List<Project> projects;
	private String userName;
	private String password;
	
	public SVNConfig(String svnUrl, List<Project> projects, String userName, String password) throws Exception {
		this.svnUrl = svnUrl;
		this.projects = projects;
		this.userName = userName;
		this.password = password;
	}

	public String getSvnUrl() {
		return svnUrl;
	}

	public void setSvnUrl(String svnUrl) {
		this.svnUrl = svnUrl;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
