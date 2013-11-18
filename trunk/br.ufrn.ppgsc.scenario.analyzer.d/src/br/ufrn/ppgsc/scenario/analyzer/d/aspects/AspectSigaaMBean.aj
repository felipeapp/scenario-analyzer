package br.ufrn.ppgsc.scenario.analyzer.d.aspects;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import javax.faces.context.FacesContext;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import br.ufrn.ppgsc.scenario.analyzer.d.data.DatabaseService;
import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.d.util.RuntimeUtil;
import br.ufrn.ppgsc.scenario.analyzer.util.MemberUtil;

/*
 * Ter uma anotção de scenario é caso base para iniciar a construção da estrutura.
 * Cada vez que uma anotação de cenário é encontrada ela entra na lista de caminhos,
 * e todas as próximas invocações são subchamadas desse cenário.
 * 
 * Eu preciso interceptar tudo para pegar invocações dentro do cenário que nao estão
 * anotadas, já que anotamos apenas o método root. O pointcut executionIgnored é usado
 * para ignorar elementos definidos dentro da minha implementação que eventualmente são
 * chamados pelo próprio aspecto e que não deviam ser interceptados, pois não são métodos
 * da aplicação.
 * 
 * TODO: verificar o quanto as interceptações e operações feitas dentro dos aspectos estão
 * influenciando negativamente na medição do tempo para executar um nó ou um cenário inteiro.
 * 
 * TODO: adaptar este aspecto para considerar também construtores, pois eles também podem
 * invocar métodos e devem ser considerados na construção do grafo dinâmico.
 * 
 * TODO: limitação para quando o cenário já iniciou e o mesmo se divide em threads.
 * Testar isso http://dev.eclipse.org/mhonarc/lists/aspectj-users/msg12554.html
 */
public aspect AspectSigaaMBean {
	
	// Cada thread tem uma pilha de execução diferente
	private final Map<Long, Stack<RuntimeNode>> thread_map = new Hashtable<Long, Stack<RuntimeNode>>();
	
	private pointcut executionIgnored() : within(br.ufrn.ppgsc.scenario.analyzer..*);
	
//	private pointcut mBeanExecution() :
//		cflow(within(@org.springframework.stereotype.Component *)) &&
//		execution(* *(..)) &&
//		!execution(* get*(..)) &&
//		!execution(* set*(..)) &&
//		!execution(* is*(..));
	
	private pointcut mBeanExecution() :
		cflow(within(@org.springframework.context.annotation.Scope * ||
				@org.springframework.stereotype.Component *)) &&
			(execution(* *(..)) || execution(*.new(..)));
	
	Object around() : mBeanExecution() && !executionIgnored() {
		Member member = getMember(thisJoinPoint.getSignature());
		
		if (isIgnorableMBeanFlow(member))
			return proceed();
		
		long begin, end;
		
		Execution execution = RuntimeUtil.getCurrentExecution();
		
		Stack<RuntimeNode> nodes_stack = getOrCreateRuntimeNodeStack();
		
		RuntimeNode node = new RuntimeNode(member);
		
		/*
		 * Se achou a anotação de cenário, começa a criar as estruturas para o elemento
		 */
		if (isMBeanEntryPoint(member)) {
			RuntimeScenario scenario_cg = new RuntimeScenario(member.getName(), node, getContextParameterMap());
			execution.addRuntimeScenario(scenario_cg);
		}
		else if (nodes_stack.empty()) {
			/* TODO: decidir o que fazer nesta situação?
			 * Se a pilha estiver vazia e a anotação não existe neste ponto é porque
			 * estamos em uma das sitações abaixo:
			 * - Temos um método que não faz parte de cenário anotado
			 * - O cenário atual está dividindo sua execução em threads
			 */
			return proceed();
		}
		
		/*
		 * Se já existe alguma coisa na pilha, então o método atual
		 * foi invocado pelo último método que está na pilha
		 */
		if (!nodes_stack.empty()) {
			nodes_stack.peek().addChild(node);
		}
		
		nodes_stack.push(node);
		
		begin = System.currentTimeMillis();
		Object o = proceed();
		end = System.currentTimeMillis();
		
		nodes_stack.pop().setExecutionTime(end - begin);

		/*
		 * Caso o método seja um método de entrada de um cenário, as informações
		 * da execução serão salvas no banco de dados.
		 */
		if (isMBeanEntryPoint(member)) {
			DatabaseService.saveResults(execution);
			execution.clearScenarios();
		}
		
		return o;
	}
	
	private boolean isIgnorableMBeanFlow(Member member) {
		return (!(member instanceof Method) || member.getName().startsWith("get") ||
				member.getName().startsWith("set") || member.getName().startsWith("is"))
				&& getOrCreateRuntimeNodeStack().isEmpty();
	}
	
	private Member getMember(Signature sig) {
		if (sig instanceof MethodSignature)
			return ((MethodSignature) sig).getMethod();
		else if (sig instanceof ConstructorSignature)
			return ((ConstructorSignature) sig).getConstructor();
		
		return null;
	}
	
	private boolean isMBeanEntryPoint(Member member) {
		return member instanceof Method && getOrCreateRuntimeNodeStack().isEmpty();
	}
	
	private Stack<RuntimeNode> getOrCreateRuntimeNodeStack() {
		long thread_id = Thread.currentThread().getId();
		Stack<RuntimeNode> nodes_stack = thread_map.get(thread_id);
		
		if (nodes_stack == null) {
			nodes_stack = new Stack<RuntimeNode>();
			thread_map.put(thread_id, nodes_stack);
		}
		
		return nodes_stack;
	}
	
	private Map<String, String> getContextParameterMap() {
		Map<String, String> result = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		
		if (fc != null)
			result = new HashMap<String, String>(fc.getExternalContext().getRequestParameterMap());
		
		return result;
	}
}
