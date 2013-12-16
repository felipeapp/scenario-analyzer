package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Execution implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private Date date;

	@Column(name = "system_name")
	private String systemName;
	
	@Column(name = "system_version")
	private String systemVersion;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "execution", fetch = FetchType.LAZY)
	private List<RuntimeScenario> scenarios;

	public Execution() {
		scenarios = new Vector<RuntimeScenario>();
		date = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<RuntimeScenario> getScenarios() {
		return Collections.unmodifiableList(scenarios);
	}

	public void setScenarios(List<RuntimeScenario> scenarios) {
		this.scenarios = scenarios;
	}

	public void addRuntimeScenario(RuntimeScenario rs) {
		scenarios.add(rs);
	}
	
	public void clearScenarios() {
		scenarios.clear();
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

}
