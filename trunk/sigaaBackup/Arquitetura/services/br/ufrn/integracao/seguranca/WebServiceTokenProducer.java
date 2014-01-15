/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/04/2011
 */
package br.ufrn.integracao.seguranca;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;

/**
 * Acrescenta ao cliente de um web service a necessidade de 
 * criar um token de autenticação para que o endpoint verifique 
 * se o token foi realmente gerado e possa executar. Necessário 
 * para melhorar o aspecto da segurança da integração entre os sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class WebServiceTokenProducer extends AbstractPhaseInterceptor<Message> implements ApplicationContextAware {

	private TokenGenerator generator;
	
	private Integer sistema;

	private ApplicationContext ac;
	
	public WebServiceTokenProducer() {
		super(Phase.PREPARE_SEND);
	}
	
	private TokenGenerator generator() {
		if (generator == null) generator = (TokenGenerator) ac.getBean("tokenGenerator");
		return generator;
	}
	
	@Override
	public void handleMessage(Message message) throws Fault {
		TokenAutenticacao token = generator().generateToken(getRegistroEntrada(), getSistema(), getInfo(message));
		message.put(Message.QUERY_STRING, token.getKey() + "|" + token.getId());
	}
	
	@SuppressWarnings("unchecked")
	private String getInfo(Message message) {
		Map<String, Object> mapa = (Map<String, Object>) message.get(Message.INVOCATION_CONTEXT);
		Map<String, Object> reqContext = (Map<String, Object>) mapa.get("RequestContext");
		Method method = (Method) reqContext.get("java.lang.reflect.Method");
		String endpointAddress = (String) message.get(Message.ENDPOINT_ADDRESS);
		return method + "_" + getSistema() + "_" + endpointAddress + "_" + getRegistroEntrada();
	}

	private Integer getSistema() {
		try {
			if (sistema == null) {
				Object session = RequestContextHolder.currentRequestAttributes().getSessionMutex();
				Object servletContext = PropertyUtils.getProperty(session, "servletContext");
				Object sistema = MethodUtils.invokeExactMethod(servletContext, "getAttribute", "sistema");
				
				return Integer.valueOf(sistema.toString());
			} else {
				return sistema;
			}
		} catch(Exception e) {
			return null;
		}
	}

	private Integer getRegistroEntrada() {
		try {
			Object usuario = RequestContextHolder.currentRequestAttributes().getAttribute("usuario", RequestAttributes.SCOPE_SESSION);
			return (Integer) PropertyUtils.getProperty(usuario, "registroEntrada.id");
		} catch(Exception e) {
			return null;
		}
	}

	public void setSistema(Integer sistema) {
		this.sistema = sistema;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
}
