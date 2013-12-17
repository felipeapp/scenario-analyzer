package br.ufrn.ppgsc.scenario.analyzer.runtime.aspects;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Stack;

import br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.DatabaseService;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeUtil;

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
 * TODO: fazer o aspect capturar não apenas a mensagem da exceção, mas também o tipo
 * dela, pois nem toda exceção informa uma mensagem
 */
public aspect AspectScenario {
	
	private pointcut executionIgnored() : within(br.ufrn.ppgsc.scenario.analyzer..*);
	
	/*
	 * Todas as execuções de métodos e construtores
	 * que estão dentro do fluxo de execução de métodos anotados
	 */
	private pointcut scenarioExecution() :
//		within(br.ufrn.sigaa.biblioteca..*) &&
		cflow(execution(@br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario * *(..)))
		&& (execution(* *(..)) || execution(*.new(..)));
	
	Object around() : scenarioExecution() && !executionIgnored() {
		long begin, end;
		
		Execution execution = RuntimeUtil.getCurrentExecution();
		
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
		
		nodes_stack.push(node);
		
		begin = System.currentTimeMillis();
		Object o = proceed();
		end = System.currentTimeMillis();
		
		nodes_stack.pop().setExecutionTime(end - begin);
		
		/*
		 * Caso o método seja um método de entrada de um cenário,
		 * significa que o cenário terminou de executar.
		 * Salvamos as informações coletadas na banco de dados
		 * A limpeza dos cenários é opcional apenas para liberar memória
		 */
		if (AspectsUtil.isScenarioEntryPoint(member)) {
			DatabaseService.saveResults(execution);
			execution.clearScenarios();
		}
		
		return o;
	}
	
	after() throwing(Throwable t) : scenarioExecution() && !executionIgnored()  {
		AspectsUtil.setRobustness(t, AspectsUtil.getMember(thisJoinPoint.getSignature()));
		AspectsUtil.getOrCreateRuntimeNodeStack().pop().setExecutionTime(-1);
	}
	
	before(Throwable t) : handler(Throwable+) && args(t) && !executionIgnored() {
		AspectsUtil.setRobustness(t, AspectsUtil.getMember(thisEnclosingJoinPointStaticPart.getSignature()));
	}
	
}
