package br.ufrn.ppgsc.scenario.analyzer.runtime.data;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeUtil;
import br.ufrn.ppgsc.scenario.analyzer.util.MemberUtil;

// TODO: adicionar um booleano para dizer se o membro é um construtor ou método
@Entity(name = "node")
public class RuntimeNode {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Lob
	@Column(name = "member")
	private String memberSignature;

	@Lob
	@Column(name = "exception")
	private String exceptionMessage;

	@Column(name = "time")
	private long executionTime;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "node_annotation",
		joinColumns = @JoinColumn(name = "node_id"),
		inverseJoinColumns = @JoinColumn(name = "annotation_id"))
	private Set<RuntimeGenericAnnotation> annotations;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", referencedColumnName = "id")
	private RuntimeNode parent;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", referencedColumnName = "id")
	@OrderBy("id asc")
	private List<RuntimeNode> children;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "node_scenario",
		joinColumns = @JoinColumn(name = "node_id"),
		inverseJoinColumns = @JoinColumn(name = "scenario_id"))
	@OrderBy("root asc")
	private List<RuntimeScenario> scenarios;

	public RuntimeNode() {

	}

	public RuntimeNode(Member member) {
		children = new ArrayList<RuntimeNode>();
		scenarios = new ArrayList<RuntimeScenario>();
		memberSignature = MemberUtil.getStandartMethodSignature(member);
		annotations = RuntimeUtil.parseMemberAnnotations(member);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMemberSignature() {
		return memberSignature;
	}

	public void setMemberSignature(String memberSignature) {
		this.memberSignature = memberSignature;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public List<RuntimeScenario> getScenarios() {
		return Collections.unmodifiableList(scenarios);
	}

	public void setScenarios(List<RuntimeScenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	public void addScenario(RuntimeScenario scenario) {
		scenarios.add(scenario);
	}

	public Set<RuntimeGenericAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<RuntimeGenericAnnotation> annotations) {
		this.annotations = Collections.unmodifiableSet(annotations);
	}

	public RuntimeNode getParent() {
		return parent;
	}

	public void setParent(RuntimeNode parent) {
		this.parent = parent;
	}

	public List<RuntimeNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public void setChildren(List<RuntimeNode> children) {
		this.children = children;
	}

	public void addChild(RuntimeNode node) {
		children.add(node);
	}

}