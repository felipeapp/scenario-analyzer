package br.ufrn.ppgsc.scenario.analyzer.cdynamic.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "parameter")
public class RuntimeParameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "arg", columnDefinition = "text")
	private String callParameter;

	public RuntimeParameter() {

	}

	public RuntimeParameter(String callParameter) {
		this.callParameter = callParameter;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCallParameter() {
		return callParameter;
	}

	public void setCallParameter(String callParameter) {
		this.callParameter = callParameter;
	}

}
