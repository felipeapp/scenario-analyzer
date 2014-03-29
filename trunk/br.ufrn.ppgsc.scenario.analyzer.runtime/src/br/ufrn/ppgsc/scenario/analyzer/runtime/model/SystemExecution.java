package br.ufrn.ppgsc.scenario.analyzer.runtime.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name = "execution")
public class SystemExecution implements Serializable {
	
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
	
	@OneToMany(mappedBy = "execution", fetch = FetchType.LAZY)
	private List<RuntimeScenario> scenarios;

	public SystemExecution() {
		scenarios = new LinkedList<RuntimeScenario>();
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

	/*
	 * ATENÇÃO: todo acesso nesta lista pode gerar condição de corrida
	 * O hibernate não está permitindo usar listas sincronizadas.
	 * Ele simplesmente está trocando minha lista por um PersistentBag
	 */
	public synchronized List<RuntimeScenario> getScenarios() {
		return scenarios;
	}

	/*
	 * Isso é necessário, pois o hibernate não está permitindo usar listas sincronizadas.
	 * Ele simplesmente está trocando minha lista por um PersistentBag
	 */
	public synchronized void addRuntimeScenario(RuntimeScenario rs) {
		scenarios.add(rs);
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
