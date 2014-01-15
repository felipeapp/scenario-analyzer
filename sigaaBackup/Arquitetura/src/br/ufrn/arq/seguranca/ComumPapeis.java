/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/03/2008
 */
package br.ufrn.arq.seguranca;

/**
 * Constantes com todos os papéis do sistema comum
 * 
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
public class ComumPapeis {

	private static int COMUM_SUBSISTEMA = 1000;
	
	/** Permissão de envio de notificações aos usuários */
	public static final int GESTOR_COMUNICACAO = COMUM_SUBSISTEMA + 1;
	
	/** Permissão de acesso à área de administração de redes */
	public static final int ADMINISTRADOR_REDE = COMUM_SUBSISTEMA + 2;
	
	/** Permissão para atribuir permissões a outros usuários */
	public static final int GESTOR_PERMISSOES = COMUM_SUBSISTEMA + 3;
	
}
