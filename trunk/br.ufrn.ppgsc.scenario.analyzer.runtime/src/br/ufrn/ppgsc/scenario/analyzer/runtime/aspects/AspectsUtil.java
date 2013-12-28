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
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.DatabaseService;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeUtil;
import br.ufrn.ppgsc.scenario.analyzer.util.MemberUtil;

public abstract class AspectsUtil {
	
	/*
	 * Cada thread tem uma pilha de execução diferente,
	 * tanto para cenários quanto para nós
	 */
	private final static Map<Long, Stack<RuntimeScenario>> thread_scenarios_map =
			new Hashtable<Long, Stack<RuntimeScenario>>();
	
	private final static Map<Long, Stack<RuntimeNode>> thread_nodes_map =
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
		Stack<RuntimeNode> nodes_stack = thread_nodes_map.get(thread_id);
		
		if (nodes_stack == null) {
			nodes_stack = new Stack<RuntimeNode>();
			thread_nodes_map.put(thread_id, nodes_stack);
		}
		
		return nodes_stack;
	}
	
	protected static Stack<RuntimeScenario> getOrCreateRuntimeScenarioStack() {
		long thread_id = Thread.currentThread().getId();
		Stack<RuntimeScenario> scenarios_stack = thread_scenarios_map.get(thread_id);
		
		if (scenarios_stack == null) {
			scenarios_stack = new Stack<RuntimeScenario>();
			thread_scenarios_map.put(thread_id, scenarios_stack);
		}
		
		return scenarios_stack;
	}
	
	protected static Map<String, String> getContextParameterMap() {
		Map<String, String> result = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		
		if (fc != null)
			result = new HashMap<String, String>(fc.getExternalContext().getRequestParameterMap());
		
		return result;
	}
	
	protected static void popStacksAndPersistData(long time, Member member) {
		Execution execution = RuntimeUtil.getCurrentExecution();
		
		Stack<RuntimeScenario> scenarios_stack = AspectsUtil.getOrCreateRuntimeScenarioStack();
		Stack<RuntimeNode> nodes_stack = AspectsUtil.getOrCreateRuntimeNodeStack();
		
		// Desempilha o último método e configura o tempo de execução dele
		nodes_stack.pop().setExecutionTime(time);
				
		/*
		 * Caso o método seja um método de entrada de um cenário,
		 * significa que o cenário terminou de executar.
		 * Salvamos as informações coletadas na banco de dados
		 * A limpeza dos cenários é opcional apenas para liberar memória
		 */
		if (AspectsUtil.isScenarioEntryPoint(member))
			scenarios_stack.pop();
		
		// Se a pilha de cenários estiver vazia, salva as informações no banco de dados
		if (scenarios_stack.isEmpty())
			DatabaseService.saveResults(execution);
	}
	
	protected static void setRobustness(Throwable t, Member m) {
		Stack<RuntimeNode> nodes_stack = getOrCreateRuntimeNodeStack();
		
		// Se estiver vazia é porque o método não faz parte de cenário
		if (!nodes_stack.empty()) {
			RuntimeNode node = nodes_stack.peek();
		
			// Testa se foi o método que capturou ou lançou a exceção
			if (node.getMemberSignature().equals(MemberUtil.getStandartMethodSignature(m)))
				node.setExceptionMessage(t.toString());
		}
	}
	
}
