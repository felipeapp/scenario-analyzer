package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import br.ufrn.ppgsc.scenario.analyzer.d.util.RuntimeUtil;

@Entity
public class RuntimeScenario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String scenarioName;

	@Column
	private Date date;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "execution_id")
	private Execution execution;

	@Column(name = "thread_id")
	private long threadId;

	@OneToOne(cascade = CascadeType.ALL)
	private RuntimeNode root;

	@Transient
	private Map<String, String> context;

	public RuntimeScenario() {
		
	}
	
	public RuntimeScenario(String scenarioName, RuntimeNode root, Map<String, String> context) {
		this.threadId = Thread.currentThread().getId();
		this.root = root;
		this.scenarioName = scenarioName;
		this.date = new Date();
		this.execution = RuntimeUtil.getCurrentExecution();
		
		if (context != null)
			this.context = Collections.unmodifiableMap(context);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Execution getExecution() {
		return execution;
	}

	public void setExecution(Execution execution) {
		this.execution = execution;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public RuntimeNode getRoot() {
		return root;
	}

	public void setRoot(RuntimeNode root) {
		this.root = root;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public void setContext(Map<String, String> context) {
		this.context = context;
	}

}
