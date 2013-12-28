//package br.ufrn.ppgsc.scenario.analyzer.runtime.aspects;
//
//import java.lang.reflect.Member;
//import java.util.Stack;
//
//import br.ufrn.ppgsc.scenario.analyzer.runtime.data.DatabaseService;
//import br.ufrn.ppgsc.scenario.analyzer.runtime.data.Execution;
//import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeNode;
//import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeScenario;
//import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeUtil;
//
//public aspect AspectMBean {
//	
//	private pointcut executionIgnored() : within(br.ufrn.ppgsc.scenario.analyzer..*);
//	
////	private pointcut mBeanExecution() :
////		cflow(within(@org.springframework.stereotype.Component *)) &&
////		execution(* *(..)) &&
////		!execution(* get*(..)) &&
////		!execution(* set*(..)) &&
////		!execution(* is*(..));
//	
//	/*
//	 * Todas as execuções de métodos e construtores
//	 * que estão dentro do fluxo gerados pelas classes anotadas
//	 */
//	private pointcut mBeanExecution() :
//		cflow(within(@org.springframework.context.annotation.Scope * ||
//				@org.springframework.stereotype.Component *)) &&
//			(execution(* *(..)) || execution(*.new(..)));
//	
//	Object around() : mBeanExecution() && !executionIgnored() {
//		Member member = AspectsUtil.getMember(thisJoinPoint.getSignature());
//		
////		if (AspectsUtil.isIgnorableMBeanFlow(member))
////			return proceed();
//		
//		long begin, end;
//		
//		Execution execution = RuntimeUtil.getCurrentExecution();
//		
//		Stack<RuntimeNode> nodes_stack = AspectsUtil.getOrCreateRuntimeNodeStack();
//		
//		RuntimeNode node = new RuntimeNode(member);
//		
//		/*
//		 * Se a pilha está vazia, estamos iniciando o cenário
//		 * Criamos o cenário e adicionamos para a execução
//		 */
//		if (nodes_stack.isEmpty()) {
//			RuntimeScenario scenario = new RuntimeScenario(
//					member.getName(), node, AspectsUtil.getContextParameterMap());
//			
//			execution.addRuntimeScenario(scenario);
//		}
//		else {
//			/*
//			 * Se já existe alguma coisa na pilha, então o método atual
//			 * foi invocado pelo último método que está na pilha
//			 */
//			RuntimeNode parent = nodes_stack.peek();
//			
//			parent.addChild(node);
//			node.setParent(parent);
//		}
//		
//		nodes_stack.push(node);
//		
//		begin = System.currentTimeMillis();
//		Object o = proceed();
//		end = System.currentTimeMillis();
//		
//		nodes_stack.pop().setExecutionTime(end - begin);
//
//		/*
//		 * Caso a pilha tenha ficado vazia, significa que o cenário terminou de executar
//		 * Salvamos as informações coletadas na banco de dados
//		 * A limpeza dos cenários é opcional apenas para liberar memória
//		 */
//		if (nodes_stack.isEmpty()) {
//			DatabaseService.saveResults(execution);
//			execution.clearScenarios();
//		}
//		
//		return o;
//	}
//	
//	after() throwing(Throwable t) : mBeanExecution() && !executionIgnored()  {
//		AspectsUtil.setRobustness(t, AspectsUtil.getMember(thisJoinPoint.getSignature()));
//		AspectsUtil.getOrCreateRuntimeNodeStack().pop().setExecutionTime(-1);
//	}
//	
//	before(Throwable t) : handler(Throwable+) && args(t) && !executionIgnored() {
//		AspectsUtil.setRobustness(t, AspectsUtil.getMember(thisEnclosingJoinPointStaticPart.getSignature()));
//	}
//	
//}
