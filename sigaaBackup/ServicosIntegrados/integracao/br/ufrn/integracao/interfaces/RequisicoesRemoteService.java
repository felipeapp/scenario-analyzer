/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on '12/03/2012'
 * 
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.RequisicaoDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Interface Remota para o SIGRH se comunicar com o SIPAC. 
 * Exposta através do Spring HTTP Invoker (Spring Remotting). 
 *
 * @author Tiago Hiroshi
 */
@WebService
public interface RequisicoesRemoteService extends Serializable {
	
	public List<RequisicaoDTO> findRequisicaoByNumeroAno(int numero, int ano) throws NegocioRemotoException;
}
