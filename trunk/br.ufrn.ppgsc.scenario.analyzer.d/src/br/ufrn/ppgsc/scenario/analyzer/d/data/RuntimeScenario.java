package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.util.Collections;
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
import javax.swing.SwingUtilities;

import br.ufrn.ppgsc.scenario.analyzer.d.gui.CGConsole;

@Entity
public class RuntimeScenario {

	/*
	 * TODO: Ver como retirar este código daqui depois posso criar um arquivo
	 * jsp para visualizar as informações do grafo
	 */
	static {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CGConsole().setVisible(true);
			}
		});
	}

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String scenarioName;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "execution_id")
	private Execution execution;

	@Column(name = "thread_id")
	private long threadId;
	@Transient
	private Thread thread;

	@OneToOne(cascade = CascadeType.ALL)
	private RuntimeNode root;

	@Transient
	private Map<String, String> context;

	public RuntimeScenario() {
		
	}
	
	public RuntimeScenario(String scenarioName, RuntimeNode root, Map<String, String> context) {
		this.thread = Thread.currentThread();
		this.threadId = thread.getId();
		this.root = root;
		this.scenarioName = scenarioName;
		this.context = context == null ? null : Collections.unmodifiableMap(context);
	}

	public Execution getExecution() {
		return execution;
	}

	public void setExecution(Execution execution) {
		this.execution = execution;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public RuntimeNode getRoot() {
		return root;
	}

	public long getThreadId() {
		return threadId;
	}

	public long getId() {
		return id;
	}

}
