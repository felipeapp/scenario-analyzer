/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/05/2012
 */
package br.ufrn.sigaa.ead.resources;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Conjunto de resources RESTful utilizados para a integração
 * do SIGAA com o aplicativo da Sedis.
 * 
 * @author David Pereira
 *
 */
public class DadosEadApplication extends Application {
	
	@Override
	public Set<Class<?>> getClasses() {
		 Set<Class<?>> s = new HashSet<Class<?>>();
		 
		 s.add(DadosEadResource.class);
		 
		 return s;
	}
	
}
