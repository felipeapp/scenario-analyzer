/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.seguranca;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;

/**
 * HTTP request handler que exporta os métodos de um bean como um serviço do
 * Spring HTTP Invoker. Acrescenta ao {@link HttpInvokerServiceExporter} do
 * Spring a necessidade de verificar se um token de autenticação foi gerado por
 * um cliente para poder executar. Necessário para melhorar o aspecto da
 * segurança da integração entre os sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 * 
 */
public class TokenAuthHttpInvokerServiceExporter extends HttpInvokerServiceExporter implements ApplicationContextAware {

	private ApplicationContext ac;
	
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Classloader do exporter: " + getBeanClassLoader());
		
		TokenGenerator token = (TokenGenerator) ac.getBean("tokenGenerator");
		
		String key = request.getParameter("key");
		Integer idToken = -1;
		
		try {
			idToken = Integer.valueOf(request.getParameter("id"));
		} catch(Exception e) {
			// Tentativa de acessar sem ser pelo TokenAuthHttpInvokerProxyFactoryBean
			e.printStackTrace();
		}

		if (!token.isTokenValid(idToken, key)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		token.invalidateToken(idToken);

		super.handleRequest(request, response);
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
}
