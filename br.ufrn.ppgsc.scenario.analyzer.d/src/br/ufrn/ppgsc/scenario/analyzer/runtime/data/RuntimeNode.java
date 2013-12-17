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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeUtil;
import br.ufrn.ppgsc.scenario.analyzer.util.MemberUtil;

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

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "root", fetch = FetchType.LAZY)
	private RuntimeScenario scenario;

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

	public RuntimeNode() {

	}

	public RuntimeNode(Member member) {
		memberSignature = MemberUtil.getStandartMethodSignature(member);
		children = new ArrayList<RuntimeNode>();
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

	public RuntimeScenario getScenario() {
		return scenario;
	}

	public void setScenario(RuntimeScenario scenario) {
		this.scenario = scenario;
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
