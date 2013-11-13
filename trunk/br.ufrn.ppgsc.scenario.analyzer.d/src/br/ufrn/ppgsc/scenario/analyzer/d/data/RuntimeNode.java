package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.util.MemberUtil;

@Entity
public class RuntimeNode {

	/* TODO: seria runtimeproperty não?
	 * a modelagem está incompleta
	 * todo runtimenode deve ter uma lista de runtimeqaattributes
	 * esta classe não está sendo usado 
	 */
	
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

	@Column
	private long time;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "node")
	private List<RuntimeProperty> properties;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "parent", referencedColumnName = "id")
	private RuntimeNode parent;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "parent", referencedColumnName = "id")
	private List<RuntimeNode> children;

	public RuntimeNode() {
		
	}
	
	public RuntimeNode(Member member) {
		this.memberSignature = MemberUtil.getStandartMethodSignature(member);
		this.children = new Vector<RuntimeNode>();
		this.properties = new Vector<RuntimeProperty>();
		
		/* TODO:
		 * - Depois ver como retirar essa fixação das anotações
		 * - Mover para dentro de MemberUtil
		 */
		Annotation annotation = ((AnnotatedElement) member).getAnnotation(Performance.class);
		
		if (annotation != null) {
			Performance pann = (Performance) annotation;
			
			RuntimeProperty pname = new RuntimeProperty(
					Performance.class.getName(), "name", "String", pann.name(), this);
			
			RuntimeProperty ptime = new RuntimeProperty(
					Performance.class.getName(), "limit_time", "long", String.valueOf(pann.limit_time()), this);
			
			properties.add(pname);
			properties.add(ptime);
		}
		
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public List<RuntimeProperty> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	public void setProperties(List<RuntimeProperty> properties) {
		this.properties = properties;
	}

	public RuntimeNode getParent() {
		return parent;
	}

	public void setParent(RuntimeNode parent) {
		this.parent = parent;
	}

	public List<RuntimeNode> getChildren() {
		return children;
	}

	public void setChildren(List<RuntimeNode> children) {
		this.children = children;
	}
	
	public void addChild(RuntimeNode node) {
		children.add(node);
		node.setParent(this);
	}

}
