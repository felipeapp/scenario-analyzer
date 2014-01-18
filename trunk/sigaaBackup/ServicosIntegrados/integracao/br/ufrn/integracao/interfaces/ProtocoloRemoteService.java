package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.InteressadoDTO;
import br.ufrn.integracao.dto.MovimentoDTO;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.integracao.dto.TipoProcessoDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;


/**
 * Interface Remota para que os sistemas se comunique com o SIPAC para utilização das informações do protocolo através do Spring HTTP Invoker (Spring Remotting). 
 * @author Itamir de M. Barroca Filho
 * 
 */
@WebService
public interface ProtocoloRemoteService  extends Serializable {
	
	/**
	 * Cadastra um processo. O movimento recebido como parâmetro refere-se a   
	 * movimentação inicial do processo.
	 * @param processo
	 * @param movimento
	 * @return
	 * @throws DAOException
	 */
	public ProcessoDTO cadastrarProcesso(ProcessoDTO processo, MovimentoDTO movimento) throws NegocioRemotoException;
	
	
	/**
	 * Realiza o envio de um processo. Basicamente cria o movimento recebido 
	 * como parâmetro e atualiza o movimento atual do processo. 
	 * @param movimento
	 * @return
	 */
	public MovimentoDTO enviarProcesso(MovimentoDTO movimento);
	
	
	/**
	 * Realiza o recebimento de um processo. O movimento recebido como parâmetro
	 * deve ser o movimento atual do processo, onde deve ser setado o id do 
	 * usuário que recebeu o processo.
	 * @param movimento
	 * @return
	 */
	public MovimentoDTO receberProcesso(MovimentoDTO movimento) ;
	
	
	/**
	 * 
	 * Busca o processo de avaliação
	 * 
	 * @param idServidor
	 * @param idPessoa
	 * @param cpfCnpj
	 * @param idTipoProcesso
	 * 
	 * @return
	 * @throws NegocioRemotoException
	 */
	public List<ProcessoDTO> buscaProcesso(Integer idServidor, Integer idPessoa, Long cpfCnpj) throws NegocioRemotoException;


	/**
	 * Busca processos de acordo com a lista de identificadores fornecida
	 * 
	 * @param idsProcessos
	 * 
	 * @return
	 * @throws NegocioRemotoException
	 */
	public List<ProcessoDTO> buscaProcessos(Integer[] idsProcessos) throws NegocioRemotoException;
	
	/**
	 * Busca todos os processos com o mesmo número e ano ou com apenas o número ou ano informado.
	 * 
	 * @param numero
	 * @param ano
	 * 
	 * @return
	 * @throws NegocioRemotoException
	 */
	public List<ProcessoDTO> buscaProcessoPorNumeroAno(Integer numero, Integer ano) throws NegocioRemotoException;
	
	/**
	 * Busca todos os tipos de processos no protocolo ou apenas os tipos ativos.
	 * 
	 * @param apenasAtivos
	 * 
	 * @return
	 * @throws NegocioRemotoException
	 */
	public List<TipoProcessoDTO> buscaTiposProcesso(boolean apenasAtivos) throws NegocioRemotoException;
	
	/**
	 * Busca todos os interessados de um determinado processo.
	 * 
	 * @param idProcesso
	 * @return
	 * @throws NegocioRemotoException
	 */
	public List<InteressadoDTO> buscaInteressadosPorProcesso(int idProcesso) throws NegocioRemotoException;
}
