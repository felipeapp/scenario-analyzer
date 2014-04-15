package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.SystemExecution;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraph;
import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario;

/*
 * Ter uma anotção de scenario é caso base para iniciar a construção da estrutura.
 * Cada vez que uma anotação de cenário é encontrada ela entra na lista de caminhos,
 * e todas as próximas invocações são subchamadas desse cenário. Se encontrar uma nova
 * anotação de cenário no caminho, cria-se um subcenário e considera mais uma execução
 * para o subcenário como uma nova execução de cenário.
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
 * TODO: limitação para quando o cenário já iniciou e o mesmo se divide em threads.
 * Testar isso http://dev.eclipse.org/mhonarc/lists/aspectj-users/msg12554.html
 * 
 */
public aspect AspectScenario {
	
	private pointcut executionIgnored() : within(br.ufrn.ppgsc.scenario.analyzer..*);
	
	private pointcut executionFlow() :
		cflow(execution(@br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario * *(..)));
	
	/*
	 * Todas as execuções de métodos e construtores
	 * que estão dentro do fluxo de execução de métodos anotados
	 */
	private pointcut scenarioExecution() :
//		within(br.ufrn.sigaa.biblioteca..*) &&
		executionFlow() && (execution(* *(..)) || execution(*.new(..)));
	
	Object around() : scenarioExecution() && !executionIgnored() {
		long begin, end;
		
		SystemExecution execution = RuntimeCallGraph.getInstance().getCurrentExecution();
		
		Stack<RuntimeScenario> scenarios_stack = AspectsUtil.getOrCreateRuntimeScenarioStack();
		Stack<RuntimeNode> nodes_stack = AspectsUtil.getOrCreateRuntimeNodeStack();
		
		Member member = AspectsUtil.getMember(thisJoinPoint.getSignature());
		
		RuntimeNode node = new RuntimeNode(member);
		
		/*
		 * Se achou a anotação de cenário, começa a criar as estruturas para o elemento
		 * Depois adiciona para a execução atual
		 */
		if (AspectsUtil.isScenarioEntryPoint(member)) {
			Scenario ann_scenario = ((Method)member).getAnnotation(Scenario.class);
			
			RuntimeScenario scenario = new RuntimeScenario(
					ann_scenario.name(), node, AspectsUtil.getContextParameterMap());
			
			execution.addRuntimeScenario(scenario);
			scenarios_stack.push(scenario);
		}
		else if (nodes_stack.empty()) {
			/* Se a pilha estiver vazia e a anotação não existe neste ponto é porque
			 * estamos executando um método que não faz parte de cenário anotado.
			 * Considerando que pegamos o fluxo de uma execução anotada, isto nunca
			 * deveria acontecer. Se o return abaixo executar, algum problema ocorreu.
			 */
			System.err.println("AspectScenario: stack of nodes is empty!");
			return proceed();
		}
		
		/*
		 * Se já existe alguma coisa na pilha, então o método atual
		 * foi invocado pelo último método que está na pilha
		 */
		if (!nodes_stack.empty()) {
			RuntimeNode parent = nodes_stack.peek();
			
			parent.addChild(node);
			node.setParent(parent);
		}
		
		node.setScenarios(new ArrayList<RuntimeScenario>(scenarios_stack));
		nodes_stack.push(node);
		
		begin = System.currentTimeMillis();
		Object o = proceed();
		end = System.currentTimeMillis();
		
		/* 
		 * Retira os elementos das pilhas e salva as informações no banco de dados
		 * Observe que este método também é chamado quando ocorrem falhas
		 */
		AspectsUtil.popStacksAndPersistData(end - begin, member);
		
		return o;
	}
	
	// Intercepta lançamentos de exceções
	after() throwing(Throwable t) : scenarioExecution() && !executionIgnored()  {
		Member member = AspectsUtil.getMember(thisJoinPoint.getSignature());
		AspectsUtil.setException(t, member);
		AspectsUtil.popStacksAndPersistData(-1, member);
	}
	
	// Intercepta capturas de exceções
	before(Throwable t) : handler(Throwable+) && args(t) && executionFlow() && !executionIgnored() {
		AspectsUtil.setException(t, AspectsUtil.getMember(thisEnclosingJoinPointStaticPart.getSignature()));
	}
	
}
