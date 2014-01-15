package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.UnidadeDTO;

/**
 * Interface que define os métodos a serem implementados <br> 
 * para disponibilizar o acesso remoto a dados das unidades de responsabilidade de avaliação.
 * 
 * @author Rafael Moreira
 *
 */
@WebService
public interface UnidadeRespAvaliacaoRemoteService extends Serializable{

	/**
	 * Retorna uma lista de unidades onde o servidor passado como parâmetro é responsável pela avaliação da unidade.
	 * @param idServidor
	 * @return
	 * @throws Exception 
	 */
	public List<UnidadeDTO> getUnidadesRespAvaliacao (int idServidor) throws Exception;
	
}
