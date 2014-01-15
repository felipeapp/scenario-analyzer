/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 04/12/2012
 * Autor: Rafael Gomes
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import fundacao.integracao.academico.CursoDTO;

/**
 * Interface Remota de cursos para que os sistemas se comuniquem com o SIGAA através do Spring HTTP Invoker (Spring Remotting).
 * 
 * @author Rafael Gomes
 *
 */
@WebService
public interface CursoFundacaoRemoteService extends Serializable{
	
	/**
	 * Retorna a lista de todos os cursos para serviço remoto com o sistema da Fundação. 
	 * @return
	 */
	public List<CursoDTO> findAllCursoFundacao();
}
