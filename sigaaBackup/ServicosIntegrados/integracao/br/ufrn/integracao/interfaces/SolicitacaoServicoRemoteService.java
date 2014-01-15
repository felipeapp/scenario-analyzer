package br.ufrn.integracao.interfaces;

import java.util.Date;
import java.util.Map;

/**
 * Interface para comunica��o do sistema desktop de atendimento do Departamento de administra��o de pessoal>br>
 * Realiza as diversas opera��es necess�rias como cadastro e dele��o.
 * 
 * @author Rafael Moreira
 *
 */
public interface SolicitacaoServicoRemoteService {

	/**
	 * Deleta uma solicita��o de servi�o que esteja cadastrada no banco
	 * @param idSolicitacaoServico
	 */
	public void deletarSolicitacaoServico (int idSolicitacaoServico);
	
	/**
	 * Cadastra uma nova solicita��o de servi�o, cada par�metro corresponde a uma coluna da tabela
	 * 
	 * @param idSolicitacaoServico
	 * @param idUsuarioSolicitante
	 * @param idTipoServico
	 * @param dataCadastro
	 * @param mensagem
	 * @param idStatusServico
	 * @param idUnidade
	 * @param numeroSolicitacao
	 * @param idServidorInteressado
	 */
	public void cadastraSolicitacaoServico (int idSolicitacaoServico, 
											int idUsuarioSolicitante, 
											int idTipoServico, 
											Date dataCadastro, 
											String mensagem, 
											int idStatusServico, 
											int idUnidade, 
											int numeroSolicitacao, 
											int idServidorInteressado);
	
	/**
	 * Retorna os tipos de solicita��o de servi�o cadastradas
	 * @return
	 */
	public Map<Integer, String> getTipoSolicitacaoServico () ;
	
}
