package br.ufrn.integracao.interfaces;

import java.io.Serializable;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.MovimentoDTO;
import br.ufrn.integracao.dto.ProcessoDTO;
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
	
}
