package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Stack;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.SuppressAjWarnings;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.SystemExecution;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraph;

/*
 * Ter uma anotção de scenario é caso base para iniciar a construção da estrutura.
 * Cada vez que uma anotação de cenário é encontrada ela é interceptada e todas as
 * próximas invocações são subchamadas desse cenário. Se encontrar uma nova anotação
 * de cenário no caminho, cria-se um subcenário, considerando-se mais uma execução para
 * o mesmo. A cenário externo não será afetado.
 * 
 * TODO: verificar o quanto as interceptações e operações feitas dentro dos aspectos estão
 * influenciando negativamente na medição do tempo para executar um nó ou um cenário inteiro.
 * 
 * TODO: limitação para quando o cenário já iniciou e o mesmo se divide em threads.
 * Testar isso http://dev.eclipse.org/mhonarc/lists/aspectj-users/msg12554.html
 */
@Aspect
public abstract class AbstractAspectEntryPoint {
	
	// Deve retornar o Class da anotação que será interceptada
	public Class<? extends Annotation> getAnnotationClass() {
		return null;
	}
	
	// Deve indicar as execuções dos pontos de entrada anotados
	@Pointcut
	public abstract void entryPoint();
	
	@Pointcut("within(br.ufrn.ppgsc.scenario.analyzer..*)")
	private void ignore() { }
	
	@Pointcut("cflow(entryPoint()) && (execution(* *(..)) || execution(*.new(..)))")
	private void entryPointFlow() {	}
	
	@SuppressAjWarnings
	@Around("entryPointFlow() && !ignore()")
	public final Object cgbuilding(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		long begin, end;
		
		SystemExecution execution = RuntimeCallGraph.getInstance().getCurrentExecution();
		
		Stack<RuntimeScenario> scenarios_stack = AspectUtil.getOrCreateRuntimeScenarioStack();
		Stack<RuntimeNode> nodes_stack = AspectUtil.getOrCreateRuntimeNodeStack();
		
		Member member = AspectUtil.getMember(thisJoinPoint.getSignature());
		
		RuntimeNode node = new RuntimeNode(member);
		
		/*
		 * Se achou a anotação de cenário, começa a criar as estruturas para o elemento.
		 * Depois adiciona para a execução atual.
		 */
		if (AspectUtil.isScenarioEntryPoint(member, getAnnotationClass(), nodes_stack.empty())) {
			RuntimeScenario scenario = new RuntimeScenario(
					AspectUtil.getEntryPointName(member, getAnnotationClass()),
					node, AspectUtil.getContextParameterMap());
			
			execution.addRuntimeScenario(scenario);
			scenarios_stack.push(scenario);
		}
		else if (nodes_stack.empty()) {
			/* 
			 * Se a pilha estiver vazia e a anotação não existe neste ponto é porque
			 * estamos executando um método que não faz parte de um cenário anotado.
			 * Considerando que pegamos o fluxo de uma execução anotada, isto nunca
			 * deveria acontecer.
			 */
			throw new RuntimeException("AbstractAspectAnnotatedEntryPoint: stack of nodes is empty!");
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
		Object o = thisJoinPoint.proceed();
		end = System.currentTimeMillis();
		
		/* 
		 * Retira os elementos das pilhas e salva as informações no banco de dados
		 * Observe que este método também é chamado quando ocorrem falhas
		 */
		AspectUtil.popStacksAndPersistData(end - begin, member, getAnnotationClass());
		
		return o;
	}
	
	// Intercepta antes do lançamento de exceções
	// after() throwing(Throwable t) : scenarioExecution() && !executionIgnored()  {
	@SuppressAjWarnings
	@AfterThrowing(pointcut = "entryPointFlow() && !ignore()", throwing = "t")
	public final void throwingException(JoinPoint thisJoinPoint, Throwable t) {
		Member member = AspectUtil.getMember(thisJoinPoint.getSignature());
		AspectUtil.setException(t, member);
		AspectUtil.popStacksAndPersistData(-1, member, getAnnotationClass());
	}
	
	// Intercepta capturas de exceções após seu lançamento
	// before(Throwable t) : handler(Throwable+) && args(t) && executionFlow() && !executionIgnored() {
	@SuppressAjWarnings
	@Before("handler(Throwable+) && args(t) && entryPointFlow() && !ignore()")
	public final void handlingException(JoinPoint.EnclosingStaticPart thisEnclosingJoinPointStaticPart, Throwable t) {
		AspectUtil.setException(t, AspectUtil.getMember(thisEnclosingJoinPointStaticPart.getSignature()));
	}
	
}
