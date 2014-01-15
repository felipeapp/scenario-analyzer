package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.RegistroEntradaDTO;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.dto.biblioteca.DadosTransferenciaMaterialDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoBibliotecaException;
import br.ufrn.integracao.exceptions.NegocioRemotoException;


/**
 * Interface Remota para que o sistema de biblioteca se comunique com o SIPAC atrav�s do Spring HTTP Invoker (Spring Remotting). 
 * @author Itamir de M. Barroca Filho
 * 
 */
@WebService
public interface PatrimonioRemoteService extends Serializable {

	/**
	 * M�todo que realiza a baixa de livros no SIPAC.
	 * @param numTombamento
	 */
	public void realizarBaixaLivro(long numTombamento);

	/**
	 * Desfaz uma baixa de um livro no SIPAC.
	 */
	public void desfazerBaixaLivro(long numTombamento);
	
	/**
	 * 
	 * <p>M�todo que cria um chamado patrimonial no SIPAC sempre que um material da biblioteca � transferido de unidade.  
	 *    J� que os materiais do acervo da biblioteca s�o tombados no patrim�nio, essa informa��o precisa ser sincronizada com
	 *    o sistema financeiro. </p> 
	 *  
	 *  <br/>
	 *  M�todo chamado a partir da classe do Sigaa
	 *   <ul>
	 *    <li>br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.ProcessadorTransfereExemplaresEntreBibliotecas</li>
	 *   </ul>
	 *
	 * @param usuarioRealizouTransferencia o usu�rio que solicitou a transfer�ncia.
	 * @param dadosTransferencia   cont�m um map que relaciona n�mero patrim�nio do bem transferido e a sua unidade origem
	 * @param bibliotecaDestino    a unidade para onde os materiais foram transferidos
	 * @return  O n�mero do chamdo patrim�nial criado.
	 * @throws NegocioRemotoException 
	 * @throws NegocioRemotoBibliotecaException
	 */
	public String geraChamadoPatrimonial(UsuarioDTO usuarioRealizouTransferencia, List<DadosTransferenciaMaterialDTO> dadosTranferencia, RegistroEntradaDTO registro) throws NegocioRemotoException;

}
