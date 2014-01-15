package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.UnidadeDTO;

/**
 * Interface que define os m�todos a serem implementados <br> 
 * para disponibilizar o acesso remoto a dados das unidades de responsabilidade de avalia��o.
 * 
 * @author Rafael Moreira
 *
 */
@WebService
public interface UnidadeRespAvaliacaoRemoteService extends Serializable{

	/**
	 * Retorna uma lista de unidades onde o servidor passado como par�metro � respons�vel pela avalia��o da unidade.
	 * @param idServidor
	 * @return
	 * @throws Exception 
	 */
	public List<UnidadeDTO> getUnidadesRespAvaliacao (int idServidor) throws Exception;
	
}
