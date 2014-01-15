/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 10/08/2013 
 */
package br.ufrn.sigaa.ensino_rede.resources;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * 
 * Conjunto de resources RESTful utilizados para a integração
 * do SIGAA com aplicações externas consumidoras desses recursos.
 * 
 * @author Leonardo Campos
 *
 */
public class DadosEnsinoRedeApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		 Set<Class<?>> s = new HashSet<Class<?>>();
		 
		 s.add(DadosEnsinoRedeResource.class);
		 
		 return s;
	}
	
}
