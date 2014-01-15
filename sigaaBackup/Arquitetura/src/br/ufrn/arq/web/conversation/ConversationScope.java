/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 10/02/2009
 */
package br.ufrn.arq.web.conversation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Implementa o escopo conversation, para
 * ser utilizado por managed beans.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Component
public class ConversationScope implements Scope {

	private final String SCOPE_KEY = ConversationScope.class.getName() + ".SCOPEDMAP";
	
	public Object get(final String name, final ObjectFactory objectFactory) {
		Object scopedObject = getScopedObjects().get(name);
		if ((scopedObject == null) && (objectFactory != null)) {
			scopedObject = objectFactory.getObject();
			getScopedObjects().put(name, scopedObject);
		}
		return scopedObject;
	}

	public Object remove(final String name) {
		final Object scopedObject = getScopedObjects().get(name);
		if (scopedObject != null) {
			getScopedObjects().remove(name);
			return scopedObject;
		}
		return null;
	}
	
	public String getConversationId() {
		return "conversation";
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getScopedObjects() {
		Map<String, Object> scopedObjects = (Map<String, Object>) RequestContextHolder.currentRequestAttributes().getAttribute(SCOPE_KEY, RequestAttributes.SCOPE_SESSION);
		if (scopedObjects == null) {
			scopedObjects = new HashMap<String, Object>();
			RequestContextHolder.currentRequestAttributes().setAttribute(SCOPE_KEY, scopedObjects, RequestAttributes.SCOPE_SESSION);
		}
		return scopedObjects;
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		
	}

	public void clear() {
		RequestContextHolder.currentRequestAttributes().setAttribute(SCOPE_KEY, new HashMap<String, Object>(), RequestAttributes.SCOPE_SESSION);
	}


	@Override
	public Object resolveContextualObject(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
