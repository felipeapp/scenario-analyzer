/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.interfaces;

import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.ServicoRemotoDTO;

/**
 * Servi�o remoto para listagem de todos os servi�os remotos 
 * importados e exportados dos sistemas.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface BuscaServicosRemotosRemoteService {

	public List<ServicoRemotoDTO>[] listarServicosRemotos();
	
}
