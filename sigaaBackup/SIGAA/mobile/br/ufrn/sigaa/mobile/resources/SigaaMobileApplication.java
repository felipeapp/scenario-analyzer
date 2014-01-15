/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/10/2011
 */
package br.ufrn.sigaa.mobile.resources;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;

/**
 * Conjunto de resources RESTful utilizados para a integração do SIGAA com
 * dispositivos móveis como smartphones e tablets.
 * 
 * @author David Pereira
 * 
 */
public class SigaaMobileApplication extends Application {

	@Context
	ServletContext context;
	
	private boolean initialized;

	public SigaaMobileApplication() {
		
		super();
		initialized = false;


	}

	@Override
	public Set<Class<?>> getClasses() {

		configMobileApplication();

		Set<Class<?>> s = new HashSet<Class<?>>();

		s.add(LogonSigaaResource.class);
		s.add(VinculoResource.class);
		s.add(TurmaResource.class);
		s.add(NoticiaResource.class);
		s.add(TopicoAulaResource.class);
		s.add(ListaFrequenciaDiaDiscenteResource.class);
		s.add(FrequenciaDiaDocenteResource.class);
		s.add(ParticipantesTurmaResource.class);
		s.add(MatriculaComponenteResource.class);
		s.add(ErrorLoggerResource.class);
		s.add(DocumentosDiscenteResource.class);

		return s;
	}

	private void configMobileApplication() {

		if (!initialized) {
			ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
			TokenGenerator tokenGenerator = (TokenGenerator) ac.getBean("tokenGenerator");
			context.setAttribute("token", tokenGenerator);
		}
	}

}
