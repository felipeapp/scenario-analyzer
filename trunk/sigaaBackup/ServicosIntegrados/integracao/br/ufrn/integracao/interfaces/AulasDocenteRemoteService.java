/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/01/2010'
 *
 */

package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

import br.ufrn.integracao.dto.AulasDocenteDto;

/**
 * Interface que define os serviços a serem expostos referentes aos dados de aulas dos docentes.
 * 
 * @author Rômulo Augusto
 */
@WebService
public interface AulasDocenteRemoteService {

	/**
	 * Verifica se o afastamento do servidor docente no período coincide com aulas que necessitem de reposição 
	 * e retorna uma mensagem com as informações das turmas afetadas em forma de lista não-ordenada (&lt;ul&gt; &lt;li&gt;).
	 * 
	 * @param aulasDocenteDto - DTO com os dados para consulta (identificador do servidor, início do afastamento, fim do afastamento).
	 * @return String com as informações das turmas afetadas em forma de lista não-ordenada (&lt;ul&gt; &lt;li&gt;)
	 * @throws DAOException
	 */
	public String getTurmasDocenteAsString(AulasDocenteDto aulasDocenteDto);
}
