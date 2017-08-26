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

	@Column(name = "tipo", columnDefinition = "text")
	private String tipo;

	public RuntimeParameter() {

	}

	public RuntimeParameter(String callParameter, String tipo) {
		this.callParameter = callParameter;
		this.tipo = tipo;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
