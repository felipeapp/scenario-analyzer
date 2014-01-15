package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.MembroFilaDto;
import br.ufrn.integracao.dto.UsuarioDTO;


/**
 * Interface que define os m�todos a serem implementados <br> 
 * para disponibilizar o acesso remoto a dados do sistema de auto atendimento.
 * 
 * @author Rafael Moreira
 *
 */
@WebService
public interface FilaAtendimentoRemoteService extends Serializable{
	
	/**
	 * M�todo para realizar login no sistema.
	 * @param nome
	 * @param senha
	 * @param ip
	 * @return
	 */
	public boolean logar(String login, String senha, String hostName, String hostAddress);
	
	/**
	 * M�todo para validar se um usu�rio tem permiss�o para acesso ao sistema de atendimento.
	 * @param login
	 * @return
	 */
	public boolean usuarioTemPermissaoAtendimento ();
	
	/**
	 * M�todo para resgatar os dados de um usu�rio para integra��o entre sistemas.
	 * @param login
	 * @return
	 */
	public UsuarioDTO getUsuario();
	
	/**
	 * M�todo para inser��o de novo membro na fila.<br>
	 * O par�metro preferencial define se o membro ser� inserido em uma fila preferencial ou normal
	 * @param preferencial
	 * @param idMembro
	 */
	public void insereNaFila (boolean preferencial, MembroFilaDto membroFila) throws Exception ;
	
	/**
	 * M�todo respons�vel por listar os membros de uma determinada fila.
	 * @param idFila (int)
	 * @return
	 */
	public List<MembroFilaDto> listarFila (int idFila);

	
	/**
	 * M�todo respons�vel por retornar o pr�ximo n�mero de ficha de uma determinada fila.
	 * @param idFila
	 * @return
	 */
	public int getProximaSequenciaAtendimento (boolean preferencial);
	
	/**
	 * M�todo que retorna o id de uma fila de atendimento recebendo como par�metros se � preferencial e qual o dia da fila
	 * @param preferencial
	 * @return
	 */
	public int getFilaId (boolean preferencial, Date data);
	
	/**
	 * Retorna um determinado n�mero de guich� cadastrado no banco de dados, que corresponda a um endere�o mac. 
	 * @param enderecoMac
	 * @return
	 */
	public int getNumeroGuiche (String enderecoMac);
	
	/**
	 * Cadastra uma nova m�quina no sistema com o endere�o mac e n�mero de guich� passados como par�metros
	 * @param enderecoMac
	 */
	public void cadastraMaquina (int numGuiche, int idRegistroEntrada) throws Exception;
	
	/**
	 * Verifica se um determinado guiche pode chamar um novo membro no painel.
	 * @param guiche
	 * @return
	 */
	public boolean guichePodeChamarNovo (int guiche);
	
}
