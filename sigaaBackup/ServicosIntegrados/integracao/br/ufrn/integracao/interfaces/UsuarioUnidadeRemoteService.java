/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.interfaces;

/**
 * Serviço remoto de cadastro/remoção de vinculação entre usuários
 * e unidades nos sistemas administrativos.
 * 
 * @author David Pereira
 *
 */
public interface UsuarioUnidadeRemoteService {

	/**
	 * Cadastra o vínculo entre o usuário e a unidade associando-o à responsabilidade de unidade. 
	 * @param idUsuarioOperacao
	 * @param idUsuarioResponsavel
	 * @param idUnidade
	 * @param idResponsabilidadeUnidade
	 */
	public void cadastrarVinculo(int idUsuarioOperacao, int idUsuarioResponsavel, int idUnidade, int idResponsabilidadeUnidade);
	
	/**
	 * Remove o vínculo entre usuário e unidade que estiver associado à responsabilidade passada como parâmetro.
	 * @param idUsuarioOperacao
	 * @param idUsuarioResponsavel
	 * @param idUnidade
	 * @param idResponsabilidadeUnidade
	 */
	public void removerVinculo(int idUsuarioOperacao, int idUsuarioResponsavel, int idUnidade, int idResponsabilidadeUnidade);
	
}
