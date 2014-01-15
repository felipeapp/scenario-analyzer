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
 * Interface Remota para que o sistema de biblioteca se comunique com o SIPAC através do Spring HTTP Invoker (Spring Remotting). 
 * @author Itamir de M. Barroca Filho
 * 
 */
@WebService
public interface PatrimonioRemoteService extends Serializable {

	/**
	 * Método que realiza a baixa de livros no SIPAC.
	 * @param numTombamento
	 */
	public void realizarBaixaLivro(long numTombamento);

	/**
	 * Desfaz uma baixa de um livro no SIPAC.
	 */
	public void desfazerBaixaLivro(long numTombamento);
	
	/**
	 * 
	 * <p>Método que cria um chamado patrimonial no SIPAC sempre que um material da biblioteca é transferido de unidade.  
	 *    Já que os materiais do acervo da biblioteca são tombados no patrimônio, essa informação precisa ser sincronizada com
	 *    o sistema financeiro. </p> 
	 *  
	 *  <br/>
	 *  Método chamado a partir da classe do Sigaa
	 *   <ul>
	 *    <li>br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.ProcessadorTransfereExemplaresEntreBibliotecas</li>
	 *   </ul>
	 *
	 * @param usuarioRealizouTransferencia o usuário que solicitou a transferência.
	 * @param dadosTransferencia   contém um map que relaciona número patrimônio do bem transferido e a sua unidade origem
	 * @param bibliotecaDestino    a unidade para onde os materiais foram transferidos
	 * @return  O número do chamdo patrimônial criado.
	 * @throws NegocioRemotoException 
	 * @throws NegocioRemotoBibliotecaException
	 */
	public String geraChamadoPatrimonial(UsuarioDTO usuarioRealizouTransferencia, List<DadosTransferenciaMaterialDTO> dadosTranferencia, RegistroEntradaDTO registro) throws NegocioRemotoException;

}
