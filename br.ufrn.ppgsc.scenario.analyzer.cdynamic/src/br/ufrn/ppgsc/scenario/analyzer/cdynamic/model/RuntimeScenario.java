package br.ufrn.ppgsc.scenario.analyzer.cdynamic.model;

import java.io.Serializable;
import java.util.Date;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.servlet.http.HttpServletRequest;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraph;

@Entity(name = "scenario")
public class RuntimeScenario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String name;

	@Column
	private Date date;

	@Column(name = "thread_id")
	private long threadId;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "execution_id")
	private SystemExecution execution;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private RuntimeNode root;

	@Column(name = "request_url", columnDefinition = "text")
	private String requestURL;

	@Column(name = "request_parameters", columnDefinition = "text")
	private String requestParameters;

	public RuntimeScenario() {

	}

	public RuntimeScenario(String name, RuntimeNode root) {
		this.threadId = Thread.currentThread().getId();
		this.root = root;
		this.name = name;
		this.date = new Date();
		this.execution = RuntimeCallGraph.getInstance().getCurrentExecution();

		FacesContext fc = FacesContext.getCurrentInstance();

		if (fc != null) {
			FacesContext faces_context = FacesContext.getCurrentInstance();
			ExternalContext external_context = faces_context.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) external_context.getRequest();

			this.requestURL = request.getRequestURL().toString();
			this.requestParameters = external_context.getRequestParameterMap().toString();
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SystemExecution getExecution() {
		return execution;
	}

	public void setExecution(SystemExecution execution) {
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

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
	}

	@Override
	public String toString() {
		return getName();
	}

}
