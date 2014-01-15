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
 * Exposta através do Spring HTTP Invoker (Spring Remotting). 
 * @author Cezar Miranda
 * 
 */
@WebService
public interface SolicitacaoInformativoRemoteService extends Serializable {

	/**
	 * Chama o processador do SIPAC e realiza a inclusão de uma bolsa a partir de informações no SIGPRH.
	 * Retorna o id da solicitação cadastrada.
	 * 
	 * @param solicitacao
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public int cadastrarSolicitacaoInformativo(SolicitacaoInformativoDTO solicitacao) throws NegocioRemotoException;
	
	/**
	 * Verifica se já não existe portaria cadastrada com um determinado número para um determinado ano/unidade.
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
	 * Retorna o próximo número disponível de solicitação de portaria para um determinado ano/unidade.
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
	 * Verifica se o informativo ainda está aguardando publicação. 
	 * Caso sim, altera o status para NEGADO, caso contrário cadastra uma nova solicitação de informativo
	 * tornando a anterior sem efeito.
	 * 
	 * @param idSolicitacaoInformativo o informativo a ser verificado
	 * @param solicitacaoCancelamento o texto para o cancelamento caso o informativo verificado já tenha sido publicado.
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws DAOException
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	int cancelarSolicitacaoInformativo(int idSolicitacaoInformativo, SolicitacaoInformativoDTO solicitacaoCancelamento) throws NegocioRemotoException;
	
	/**
	 * Retorna o número/ano do boletim onde a solicitação de informativo foi publicada, se já houver sido publicada, ou nulo caso contrário.
	 * 
	 * @param idSolicitacaoInformativo
	 * @return SolicitacaoInformativoDTO contendo o número, o ano e o id do boletim indexados por "numero" e "ano" e "id"
	 * @throws DAOException 
	 */
	public SolicitacaoInformativoDTO getDadosBoletim(int idSolicitacaoInformativo);

	/**
	 * Permite alterar o status de uma solicitação de informativo para NEGADA, caso a mesma ainda não tenha sido publicada em boletim informativo
	 * 
	 * @param idSolicitacaoInformativo a solicitação que se deseja cancelar
	 * @return <b>true</b> se cancelou com sucesso e <b>false</b> caso contrário (se já estiver publicada). 
	 * @throws DAOException 
	 */
	public boolean negarSolicitacaoInformativo(Integer idSolicitacaoInformativo);
}
