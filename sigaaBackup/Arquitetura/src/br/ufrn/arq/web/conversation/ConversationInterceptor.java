/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 10/02/2009
 */
package br.ufrn.arq.web.conversation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Aspecto para possibilitar o uso do escopo conversation
 * em managed beans do JSF. 
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Component @Aspect
public class ConversationInterceptor {

	@Autowired
	private ConversationScope scope;
	
	@Pointcut("@within(br.ufrn.arq.web.conversation.ConversationBean)")
	public void inConversationBean() { }
	
	@Pointcut("execution(@br.ufrn.arq.web.conversation.ConversationStart public String *())")
	public void startConversation() { }
	
	@Pointcut("execution(@br.ufrn.arq.web.conversation.ConversationEnd public String *())")
	public void endConversation() { }
	
	@Pointcut("execution(@br.ufrn.arq.web.conversation.NoConversation public * *(..))")
	public void noConversation() { }
	
	@Pointcut("execution(public * *(..))")
	public void anyPublicMethod() { }
	
	@Before("inConversationBean() && startConversation()")
	public void beforeConversationStart(final JoinPoint jp) throws Throwable {
		String className = jp.getTarget().getClass().getName();
		RequestContextHolder.currentRequestAttributes().setAttribute(className + ".CONVERSATION_STARTED", Boolean.TRUE, RequestAttributes.SCOPE_SESSION);
	}
	
	@Before("inConversationBean() && anyPublicMethod() && !startConversation() && !noConversation()")
	public void callingBeanMethod(final JoinPoint jp) throws Throwable {
		String className = jp.getTarget().getClass().getName();
		Boolean conversationStarted = (Boolean) RequestContextHolder.currentRequestAttributes().getAttribute(className + ".CONVERSATION_STARTED", RequestAttributes.SCOPE_SESSION);
		
		if (conversationStarted == null || !conversationStarted) {
			throw new ConversationNotActiveException("A operação não está ativa.");
		}
	}
	
	@After("inConversationBean() && endConversation()")
	public void afterConversationEnd(final JoinPoint jp) throws Throwable {
		scope.clear();
		String className = jp.getTarget().getClass().getName();
		RequestContextHolder.currentRequestAttributes().removeAttribute(className + ".CONVERSATION_STARTED", RequestAttributes.SCOPE_SESSION);
	}
	
	
}
