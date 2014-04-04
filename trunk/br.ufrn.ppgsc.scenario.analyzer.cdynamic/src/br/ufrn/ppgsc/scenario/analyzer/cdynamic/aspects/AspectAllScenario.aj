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


public aspect AspectAllScenario {
	
	// Execuções que serão ignoradas, pacotes do analisador
	private pointcut executionIgnored() : within(br.ufrn.ppgsc.scenario.analyzer..*);
	
	// Todas as execuções de métodos e construtores
	private pointcut executions() : execution(* *(..)) || execution(*.new(..));
	
	Object around() : executions() && !executionIgnored() {
		long begin, end;
		
		SystemExecution execution = RuntimeCallGraph.getInstance().getCurrentExecution();
		
		Stack<RuntimeScenario> scenarios_stack = AspectsUtil.getOrCreateRuntimeScenarioStack();
		Stack<RuntimeNode> nodes_stack = AspectsUtil.getOrCreateRuntimeNodeStack();
		
		Member member = AspectsUtil.getMember(thisJoinPoint.getSignature());
		
		RuntimeNode node = new RuntimeNode(member);
		
		/* 
		 * Se a pilha está vazia é porque estamos començando uma nova execução.
		 * Cria-se o cenário de execução e coloca ele na pilha
		 */
		if (nodes_stack.empty()) {
			RuntimeScenario scenario = new RuntimeScenario("Entry point for " + member.getName(),
					node, AspectsUtil.getContextParameterMap());
			
			execution.addRuntimeScenario(scenario);
			scenarios_stack.push(scenario);
		}
		else {
			/*
			 * Se já existe alguma coisa na pilha, então o método atual
			 * foi invocado pelo último método que está na pilha
			 */
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
	after() throwing(Throwable t) : executions() && !executionIgnored()  {
		Member member = AspectsUtil.getMember(thisJoinPoint.getSignature());
		AspectsUtil.setException(t, member);
		AspectsUtil.popStacksAndPersistData(-1, member);
	}
	
	// Intercepta capturas de exceções
	before(Throwable t) : handler(Throwable+) && args(t) && !executionIgnored() {
		AspectsUtil.setException(t, AspectsUtil.getMember(thisEnclosingJoinPointStaticPart.getSignature()));
	}
	
}
