package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "execution")
	private List<RuntimeScenario> scenarios;

	private static final Execution instance = new Execution();

	private Execution() {
		scenarios = new Vector<RuntimeScenario>();
		date = new Date();
	}

	public List<RuntimeScenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(List<RuntimeScenario> scenarios) {
		this.scenarios = scenarios;
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

	public static Execution getInstance() {
		return instance;
	}

	public void addRuntimeScenario(RuntimeScenario s) {
		scenarios.add(s);
	}

	public RuntimeScenario getLastRuntimeScenario() {
		return scenarios.get(scenarios.size() - 1);
	}

	public List<RuntimeScenario> getAllRuntimeCallGraph() {
		return Collections.unmodifiableList(scenarios);
	}

	public List<RuntimeScenario> getRuntimeScenarioByScenarioName(
			String scenario_name) {
		List<RuntimeScenario> list = new ArrayList<RuntimeScenario>();

		for (RuntimeScenario s : scenarios)
			if (s.getScenarioName().equals(scenario_name))
				list.add(s);

		return Collections.unmodifiableList(list);
	}

}
