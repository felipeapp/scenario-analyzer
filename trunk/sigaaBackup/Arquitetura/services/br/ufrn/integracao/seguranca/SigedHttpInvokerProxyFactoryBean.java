/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 17/06/2010
 */
package br.ufrn.integracao.seguranca;

import static br.ufrn.arq.util.ReflectionUtils.getProperty;
import static br.ufrn.arq.util.ReflectionUtils.setProperty;

import java.io.IOException;
import java.io.InvalidClassException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.aopalliance.intercept.MethodInvocation;
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

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.TokenNaoValidadoException;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;
import br.ufrn.comum.dominio.Sistema;

/**
 * Proxy Factory Bean que identifica se o SIGED está ativo ou não.
 * Se não estiver, utiliza uma implementação padrão da arquitetura que
 * simplesmente adiciona o arquivo ao banco de arquivos.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class SigedHttpInvokerProxyFactoryBean extends HttpInvokerProxyFactoryBean implements ApplicationContextAware {

	private ApplicationContext ac;
	
	private Integer sistema;
	
	public SigedHttpInvokerProxyFactoryBean() {
		setHttpInvokerRequestExecutor(new SimpleHttpInvokerRequestExecutor() {
			@Override
			protected HttpURLConnection openConnection( HttpInvokerClientConfiguration config) throws IOException {
				
				System.out.println("Classloader do invoker: " + SigedHttpInvokerProxyFactoryBean.this.getBeanClassLoader());
				
				TokenGenerator generator = (TokenGenerator) ac.getBean("tokenGenerator");
				TokenAutenticacao token = generator.generateToken(getRegistroEntrada(), getSistema(), getInfo());
				String url = getServiceUrl() + "?key=" + token.getKey() + "&id=" + token.getId(); // mudar para id
				URLConnection con = new URL(url).openConnection();
				return (HttpURLConnection) con;
			}
		});
	}
	
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (!Sistema.isSigedAtivo()) {
			String methodName = methodInvocation.getMethod().getName();
			Object[] args = methodInvocation.getArguments();
			
			if (methodName.equals("cadastrarDocumento")) {
				return cadastrarDocumento(args[0], args[1], args[2]);
			} else if (methodName.equals("excluirDocumentoPorIdArquivo")) {
				excluirDocumentoPorIdArquivo(args[0], (Integer) args[1]);
			} else if (methodName.equals("cadastrarPastasDocumento")) {
				return cadastrarDocumento(args[0], args[1], args[2]);
			}
			
			return null;				
		} else {
			return super.invoke(methodInvocation);
		}
	}
	
	private Object cadastrarDocumento(Object documento, Object arquivo, Object usuario) {
		if (arquivo != null) {
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, (byte[]) getProperty(arquivo, "bytes"), (String) getProperty(arquivo, "contentType"), (String) getProperty(arquivo, "name"));

			setProperty(documento, "idArquivo", idArquivo);
			return documento;
		}
			
		return null;
	}
	
	private void excluirDocumentoPorIdArquivo(Object usuario, int idArquivo) {
		EnvioArquivoHelper.removeArquivo(idArquivo);
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
