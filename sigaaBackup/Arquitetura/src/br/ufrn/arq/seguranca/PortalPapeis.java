/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/08/2010
 */
package br.ufrn.arq.seguranca;


/**
 * Papéis do Subsistema do PortalUFRN
 * 
 * @author Mário Rizzi
 * @author Gleydson Lima
 *
 */
public class PortalPapeis {

	/** Atribuir permissões aos usuários administradores dos sites relacinados ao gerenciador de conteúdo. */
	public static final int ADMINISTRADOR_SITE = PortalSubsistemas.ADMINISTRACAO.getId() + 1;

	/** Atribuir permissões aos usuários dos sites relacinados ao gerenciador de conteúdo. */
	public static final int GESTOR_SITES =  PortalSubsistemas.ADMINISTRACAO.getId() + 2;
		
	/** Atribuir permissões aos usuários administradores dos sites relacinados ao gerenciador de conteúdo. */
	public static final int GESTOR_AGENCIA_COMUNICACAO = PortalSubsistemas.ADMINISTRACAO.getId() + 3;
	
}
