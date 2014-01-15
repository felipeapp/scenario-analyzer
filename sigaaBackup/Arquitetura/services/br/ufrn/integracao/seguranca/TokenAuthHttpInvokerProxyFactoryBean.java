/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.seguranca;

import java.io.IOException;
import java.io.InvalidClassException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import br.ufrn.arq.erros.TokenNaoValidadoException;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;

/**
 * Proxy para um serviço disponibilizado pelo
 * Spring Http Invoker. Expõe o serviço como um bean do
 * Spring utilizando a interface injetada no atributo
 * serviceInterface. 
 * 
 * Acrescenta ao {@link HttpInvokerProxyFactoryBean} do Spring 
 * a necessidade de criar um token de autenticação para que o 
 * cliente verifique se o token foi realmente gerado e possa 
 * executar. Necessário para melhorar o aspecto da segurança 
 * da integração entre os sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class TokenAuthHttpInvokerProxyFactoryBean extends HttpInvokerProxyFactoryBean implements ApplicationContextAware {

	private Integer sistema;
	
	private ApplicationContext ac;
	
	public TokenAuthHttpInvokerProxyFactoryBean() {
		setHttpInvokerRequestExecutor(new SimpleHttpInvokerRequestExecutor() {
			@Override
			protected HttpURLConnection openConnection( HttpInvokerClientConfiguration config) throws IOException {
				TokenGenerator generator = (TokenGenerator) ac.getBean("tokenGenerator");
				TokenAutenticacao token = generator.generateToken(getRegistroEntrada(), getSistema(), getInfo());
				String url = getServiceUrl() + "?key=" + token.getKey() + "&id=" + token.getId(); // mudar para id
				URLConnection con = new URL(url).openConnection();
				return (HttpURLConnection) con;
			}
		});
	}

	private String getInfo() {
		return getServiceInterface().getSimpleName() + "_" + getSistema() + "_" + getServiceUrl() + "_" + getRegistroEntrada();
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

	@Override
	protected RemoteAccessException convertHttpInvokerAccessException(Throwable ex) {
		if (ex instanceof ConnectException) {
			throw new RemoteConnectFailureException("Could not connect to HTTP invoker remote service at [" + getServiceUrl() + "]", ex);
		} else if (ex instanceof ClassNotFoundException || ex instanceof NoClassDefFoundError ||ex instanceof InvalidClassException) {
			throw new RemoteAccessException("Could not deserialize result from HTTP invoker remote service [" + getServiceUrl() + "]", ex);
		} else if (ex instanceof IOException && ex.getMessage().indexOf("status code = 403") != -1) {
			throw new TokenNaoValidadoException("Token de autenticação não validado. Usuário não autorizado a executar a operação.");
		} else {
			throw new RemoteAccessException("Could not access HTTP invoker remote service at [" + getServiceUrl() + "]", ex);
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
