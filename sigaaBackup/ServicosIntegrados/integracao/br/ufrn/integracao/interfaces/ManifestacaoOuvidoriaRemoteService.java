package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.ManifestacaoDTO;

/**
 * Interface de acesso �s funcionalidades disponibilizadas para manifesta��es da ouvidoria.
 * 
 * @author Bernardo
 *
 */
@WebService
public interface ManifestacaoOuvidoriaRemoteService extends Serializable {

	/**
	 * Retorna uma cole��o contendo todas as manifesta��es atrasadas dada uma unidade.
	 * 
	 * @param idUnidade
	 * @return
	 */
	public List<ManifestacaoDTO> getAllSemRespostaByUnidade(int idUnidade);
	
}
