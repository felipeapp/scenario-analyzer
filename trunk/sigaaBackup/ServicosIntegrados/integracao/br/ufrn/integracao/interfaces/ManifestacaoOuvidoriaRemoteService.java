package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.ManifestacaoDTO;

/**
 * Interface de acesso às funcionalidades disponibilizadas para manifestações da ouvidoria.
 * 
 * @author Bernardo
 *
 */
@WebService
public interface ManifestacaoOuvidoriaRemoteService extends Serializable {

	/**
	 * Retorna uma coleção contendo todas as manifestações atrasadas dada uma unidade.
	 * 
	 * @param idUnidade
	 * @return
	 */
	public List<ManifestacaoDTO> getAllSemRespostaByUnidade(int idUnidade);
	
}
