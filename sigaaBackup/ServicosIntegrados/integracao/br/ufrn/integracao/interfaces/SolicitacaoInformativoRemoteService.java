package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Map;

import javax.jws.WebService;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.integracao.dto.SolicitacaoInformativoDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;


/**
 * Interface Remota para o SIGPRH se comunicar com o SIPAC. 
 * Exposta atrav�s do Spring HTTP Invoker (Spring Remotting). 
 * @author Cezar Miranda
 * 
 */
@WebService
public interface SolicitacaoInformativoRemoteService extends Serializable {

	/**
	 * Chama o processador do SIPAC e realiza a inclus�o de uma bolsa a partir de informa��es no SIGPRH.
	 * Retorna o id da solicita��o cadastrada.
	 * 
	 * @param solicitacao
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public int cadastrarSolicitacaoInformativo(SolicitacaoInformativoDTO solicitacao) throws NegocioRemotoException;
	
	/**
	 * Verifica se j� n�o existe portaria cadastrada com um determinado n�mero para um determinado ano/unidade.
	 * 
	 * @param numero
	 * @param ano
	 * @param idUnidade
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public Boolean validaNumeroPortaria(int numero, int ano, int idUnidade) throws NegocioRemotoException; 
	
	/**
	 * Retorna o pr�ximo n�mero dispon�vel de solicita��o de portaria para um determinado ano/unidade.
	 * 
	 * @param numero
	 * @param ano
	 * @param idUnidade
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public int getProximoNumeroPortaria(int ano, int idUnidade) throws NegocioRemotoException;
	

	/**
	 * Verifica se o informativo ainda est� aguardando publica��o. 
	 * Caso sim, altera o status para NEGADO, caso contr�rio cadastra uma nova solicita��o de informativo
	 * tornando a anterior sem efeito.
	 * 
	 * @param idSolicitacaoInformativo o informativo a ser verificado
	 * @param solicitacaoCancelamento o texto para o cancelamento caso o informativo verificado j� tenha sido publicado.
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws DAOException
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	int cancelarSolicitacaoInformativo(int idSolicitacaoInformativo, SolicitacaoInformativoDTO solicitacaoCancelamento) throws NegocioRemotoException;
	
	/**
	 * Retorna o n�mero/ano do boletim onde a solicita��o de informativo foi publicada, se j� houver sido publicada, ou nulo caso contr�rio.
	 * 
	 * @param idSolicitacaoInformativo
	 * @return SolicitacaoInformativoDTO contendo o n�mero, o ano e o id do boletim indexados por "numero" e "ano" e "id"
	 * @throws DAOException 
	 */
	public SolicitacaoInformativoDTO getDadosBoletim(int idSolicitacaoInformativo);

	/**
	 * Permite alterar o status de uma solicita��o de informativo para NEGADA, caso a mesma ainda n�o tenha sido publicada em boletim informativo
	 * 
	 * @param idSolicitacaoInformativo a solicita��o que se deseja cancelar
	 * @return <b>true</b> se cancelou com sucesso e <b>false</b> caso contr�rio (se j� estiver publicada). 
	 * @throws DAOException 
	 */
	public boolean negarSolicitacaoInformativo(Integer idSolicitacaoInformativo);
}
