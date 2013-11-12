package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.util.MemberUtil;

@Entity
public class RuntimeNode {

	/* TODO
	 * a modelagem está incompleta
	 * todo runtimenode deve ter uma lista de runtimeqaattributes
	 * esta classe não está sendo usado 
	 */
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "member")
	private String memberSignature;

	@Column(name = "exception")
	private String exceptionMessage;

	@Column
	private long time;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "node")
	private List<RuntimeProperty> properties;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "parent", referencedColumnName = "id")
	private List<RuntimeNode> children;

	public RuntimeNode() {
		
	}
	
	public RuntimeNode(Member member) {
		this.memberSignature = MemberUtil.getStandartMethodSignature(member);
		this.children = new ArrayList<RuntimeNode>();
		this.properties = new ArrayList<RuntimeProperty>();
		
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

	public void addChild(RuntimeNode node) {
		children.add(node);
	}

	public void removeChild(RuntimeNode node) {
		children.remove(node);
	}

	public RuntimeNode[] getChildren() {
		return children.toArray(new RuntimeNode[0]);
	}

	public long getId() {
		return id;
	}

	public String getMemberSignature() {
		return memberSignature;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}
	
	public void setException(Throwable exception) {
		this.exceptionMessage = exception.getMessage();
	}

}
