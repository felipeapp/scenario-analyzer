/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.interfaces;

import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.ServicoRemotoDTO;

/**
 * Serviço remoto para listagem de todos os serviços remotos 
 * importados e exportados dos sistemas.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface BuscaServicosRemotosRemoteService {

	public List<ServicoRemotoDTO>[] listarServicosRemotos();
	
}
