package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.integracao.dto.MembroFilaDto;
import br.ufrn.integracao.dto.SolicitacaoServicoDTO;
import br.ufrn.integracao.dto.TipoServicoDTO;
import br.ufrn.integracao.dto.UnidadeDTO;

/**
 * Interface responsï¿½vel por concentrar os mï¿½todos de acesso referente aos membros de filas de atendimento
 * @author Rafael Moreira
 *
 */
@WebService
public interface MembroFilaRemoteService extends Serializable{
	
	/**
	 * Retorna um membro de acordo com o ID passado como parâmetro
	 * 
	 * @param idMembro
	 * @return
	 */
	public MembroFilaDto getMembroById (int idMembro);
	
	/**
	 * Retorna um membro através de uma sequência e identificação da fila
	 * 
	 * @param sequencia
	 * @param idFila
	 * @return
	 */
	public MembroFilaDto getMembroBySequencia (int sequencia, int idFila);
	
	/**
	 * realiza o registro de um atendimento de um membro da fila.
	 * 
	 * @param membroDto
	 * @param solicitacaoDto
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public int registraAtendimento (MembroFilaDto membroDto, SolicitacaoServicoDTO solicitacaoDto) throws Exception;
	
	/**
	 * Retorna todos os tipos de serviços cadastrados no sistema (Usado para alimentar um Combobox)
	 * 
	 * @return
	 */
	public List<TipoServicoDTO> getAllTipoServico ();
	
	/**
	 * Método responsável por alterar a situação de um membro em uma determinada fila
	 * 
	 * @param novaSituacao
	 * @param idMembro
	 */
	public MembroFilaDto alteraSituacaoMembro(int novaSituacao, MembroFilaDto membroDTO, int guiche) throws Exception;
	
	
	/** Retorna as unidades organizacionais para solicitações de serviço */
	public List<UnidadeDTO> getUnidadesSolicitacoes();
}
