package br.ufrn.ppgsc.scenario.analyzer.d.data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class RuntimeProperty {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String annotation;

	@Column
	private String name;

	@Column
	private String type;

	@Column
	private String value;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "node_id")
	private RuntimeNode node;

	public RuntimeProperty() {

	}

	public RuntimeProperty(String annotation, String name, String type, String value, RuntimeNode node) {
		this.annotation = annotation;
		this.name = name;
		this.type = type;
		this.value = value;
		this.node = node;
	}

}
