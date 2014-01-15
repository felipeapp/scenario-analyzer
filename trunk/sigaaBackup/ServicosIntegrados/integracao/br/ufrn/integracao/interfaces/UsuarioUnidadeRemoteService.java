/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.interfaces;

/**
 * Servi�o remoto de cadastro/remo��o de vincula��o entre usu�rios
 * e unidades nos sistemas administrativos.
 * 
 * @author David Pereira
 *
 */
public interface UsuarioUnidadeRemoteService {

	/**
	 * Cadastra o v�nculo entre o usu�rio e a unidade associando-o � responsabilidade de unidade. 
	 * @param idUsuarioOperacao
	 * @param idUsuarioResponsavel
	 * @param idUnidade
	 * @param idResponsabilidadeUnidade
	 */
	public void cadastrarVinculo(int idUsuarioOperacao, int idUsuarioResponsavel, int idUnidade, int idResponsabilidadeUnidade);
	
	/**
	 * Remove o v�nculo entre usu�rio e unidade que estiver associado � responsabilidade passada como par�metro.
	 * @param idUsuarioOperacao
	 * @param idUsuarioResponsavel
	 * @param idUnidade
	 * @param idResponsabilidadeUnidade
	 */
	public void removerVinculo(int idUsuarioOperacao, int idUsuarioResponsavel, int idUnidade, int idResponsabilidadeUnidade);
	
}
