package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.MembroFilaDto;
import br.ufrn.integracao.dto.UsuarioDTO;


/**
 * Interface que define os métodos a serem implementados <br> 
 * para disponibilizar o acesso remoto a dados do sistema de auto atendimento.
 * 
 * @author Rafael Moreira
 *
 */
@WebService
public interface FilaAtendimentoRemoteService extends Serializable{
	
	/**
	 * Método para realizar login no sistema.
	 * @param nome
	 * @param senha
	 * @param ip
	 * @return
	 */
	public boolean logar(String login, String senha, String hostName, String hostAddress);
	
	/**
	 * Método para validar se um usuário tem permissão para acesso ao sistema de atendimento.
	 * @param login
	 * @return
	 */
	public boolean usuarioTemPermissaoAtendimento ();
	
	/**
	 * Método para resgatar os dados de um usuário para integração entre sistemas.
	 * @param login
	 * @return
	 */
	public UsuarioDTO getUsuario();
	
	/**
	 * Método para inserção de novo membro na fila.<br>
	 * O parâmetro preferencial define se o membro será inserido em uma fila preferencial ou normal
	 * @param preferencial
	 * @param idMembro
	 */
	public void insereNaFila (boolean preferencial, MembroFilaDto membroFila) throws Exception ;
	
	/**
	 * Método responsável por listar os membros de uma determinada fila.
	 * @param idFila (int)
	 * @return
	 */
	public List<MembroFilaDto> listarFila (int idFila);

	
	/**
	 * Método responsável por retornar o próximo número de ficha de uma determinada fila.
	 * @param idFila
	 * @return
	 */
	public int getProximaSequenciaAtendimento (boolean preferencial);
	
	/**
	 * Método que retorna o id de uma fila de atendimento recebendo como parâmetros se é preferencial e qual o dia da fila
	 * @param preferencial
	 * @return
	 */
	public int getFilaId (boolean preferencial, Date data);
	
	/**
	 * Retorna um determinado número de guichê cadastrado no banco de dados, que corresponda a um endereço mac. 
	 * @param enderecoMac
	 * @return
	 */
	public int getNumeroGuiche (String enderecoMac);
	
	/**
	 * Cadastra uma nova máquina no sistema com o endereço mac e número de guichê passados como parâmetros
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
