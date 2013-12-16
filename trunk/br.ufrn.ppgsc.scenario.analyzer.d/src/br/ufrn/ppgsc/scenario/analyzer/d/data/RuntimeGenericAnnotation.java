package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "annotation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class RuntimeGenericAnnotation {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "annotation_id_gen")
	@SequenceGenerator(name = "annotation_id_gen",
		sequenceName = "annotation_id_seq",
		allocationSize = 1)
	private long id;

	@Column
	private String name;

	@ManyToMany(mappedBy = "annotations", fetch = FetchType.LAZY)
	private Set<RuntimeNode> nodes;

	public RuntimeGenericAnnotation() {

	}

	public RuntimeGenericAnnotation(String name) {
		this.name = name;
		this.nodes = new HashSet<RuntimeNode>();
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

	public Set<RuntimeNode> getNodes() {
		return Collections.unmodifiableSet(nodes);
	}

	public void setNodes(Set<RuntimeNode> nodes) {
		this.nodes = nodes;
	}

	public void addNode(RuntimeNode node) {
		this.nodes.add(node);
	}

}
