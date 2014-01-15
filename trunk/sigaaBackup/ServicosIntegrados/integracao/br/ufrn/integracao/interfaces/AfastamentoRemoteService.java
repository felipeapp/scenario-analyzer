/**
 * 
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Date;

import javax.jws.WebService;

import org.springframework.remoting.RemoteAccessException;

import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Interface Remota para comunica��o entre SIPAC e SIGRH para consultar se existe afastamento homologados para um terminado servidor.
 * <br/>
 * @author Mychell Teixeira (UFRN)
 *
 */
@WebService
public interface AfastamentoRemoteService extends Serializable {
	/**
	 * M�todo utilizado para verificar se existe afastamento homologados para o servidor.
	 * <br/>
	 * @param inicio In�cio do afastamento.
	 * @param fim Fim do afastamento.
	 * @param idServidor Identificador do servido.
	 * @return Booleano indicando se exite ou n�o afastamento para o servidor no per�odo informado.
	 * @throws RemoteAccessException
	 */
	public void validaSolicitacaoAfastamento( Date inicio, Date fim, int idSevidor, int [] idsCategoria, int [] idsOcorrencia, 
			String paramTipoAfastamento, String paramAfastamentoHomologado, String paramAfastamentoNegado, String paramAfastamentoDispensaHomologacao  ) throws RemoteAccessException, NegocioRemotoException;
}
