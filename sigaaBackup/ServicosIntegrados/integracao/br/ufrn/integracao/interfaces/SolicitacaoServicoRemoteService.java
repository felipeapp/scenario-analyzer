package br.ufrn.integracao.interfaces;

import java.util.Date;
import java.util.Map;

/**
 * Interface para comunicação do sistema desktop de atendimento do Departamento de administração de pessoal>br>
 * Realiza as diversas operações necessárias como cadastro e deleção.
 * 
 * @author Rafael Moreira
 *
 */
public interface SolicitacaoServicoRemoteService {

	/**
	 * Deleta uma solicitação de serviço que esteja cadastrada no banco
	 * @param idSolicitacaoServico
	 */
	public void deletarSolicitacaoServico (int idSolicitacaoServico);
	
	/**
	 * Cadastra uma nova solicitação de serviço, cada parâmetro corresponde a uma coluna da tabela
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
	 * Retorna os tipos de solicitação de serviço cadastradas
	 * @return
	 */
	public Map<Integer, String> getTipoSolicitacaoServico () ;
	
}
