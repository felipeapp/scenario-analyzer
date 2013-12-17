package br.ufrn.ppgsc.scenario.analyzer.runtime.aspects;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;
import java.lang.Long;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.faces.context.FacesContext;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.util.MemberUtil;

public abstract class AspectsUtil {
	
	// Cada thread tem uma pilha de execução diferente
	private final static Map<Long, Stack<RuntimeNode>> thread_map =
			new Hashtable<Long, Stack<RuntimeNode>>();
	
	protected static boolean isScenarioEntryPoint(Member member) {
		boolean result = false;
		
		if (member instanceof Method) {
			Scenario ann_scenario = ((Method) member).getAnnotation(Scenario.class);
			result = ann_scenario != null;
		}
		
		return result;
	}
	
	protected static boolean isIgnorableMBeanFlow(Member member) {
		return (!(member instanceof Method) || member.getName().startsWith("get") ||
				member.getName().startsWith("set") || member.getName().startsWith("is")
				|| member.getName().startsWith("main"))
				&& getOrCreateRuntimeNodeStack().isEmpty();
	}
	
	protected static Member getMember(Signature sig) {
		if (sig instanceof MethodSignature)
			return ((MethodSignature) sig).getMethod();
		else if (sig instanceof ConstructorSignature)
			return ((ConstructorSignature) sig).getConstructor();
		
		return null;
	}
	
	protected static Stack<RuntimeNode> getOrCreateRuntimeNodeStack() {
		long thread_id = Thread.currentThread().getId();
		Stack<RuntimeNode> nodes_stack = thread_map.get(thread_id);
		
		if (nodes_stack == null) {
			nodes_stack = new Stack<RuntimeNode>();
			thread_map.put(thread_id, nodes_stack);
		}
		
		return nodes_stack;
	}
	
	protected static Map<String, String> getContextParameterMap() {
		Map<String, String> result = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		
		if (fc != null)
			result = new HashMap<String, String>(fc.getExternalContext().getRequestParameterMap());
		
		return result;
	}
	
	protected static void setRobustness(Throwable t, Member m) {
		Stack<RuntimeNode> nodes_stack = getOrCreateRuntimeNodeStack();
		
		// Se estiver vazia é porque o método não faz parte de cenário
		if (!nodes_stack.empty()) {
			RuntimeNode node = nodes_stack.peek();
		
			// Testa se foi o método que capturou ou lançou a exceção
			if (node.getMemberSignature().equals(MemberUtil.getStandartMethodSignature(m)))
				node.setExceptionMessage(t.getMessage());
		}
	}
	
}
