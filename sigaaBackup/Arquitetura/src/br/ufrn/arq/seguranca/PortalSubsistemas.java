/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/03/2009
 */
package br.ufrn.arq.seguranca;

import br.ufrn.comum.dominio.SubSistema;

/**
 * SubSistemas do PortalUFRN
 * 
 * @author Mário Rizzi
 * @author Gleydson Lima
 *
 */
public class PortalSubsistemas {

	public static final SubSistema ADMINISTRACAO = new SubSistema(110500, "Portal dos Sites", "/adminportal/index.jsf", "adminPortal");
	
}
