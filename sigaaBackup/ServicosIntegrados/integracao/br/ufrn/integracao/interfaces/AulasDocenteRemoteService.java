/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '27/01/2010'
 *
 */

package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

import br.ufrn.integracao.dto.AulasDocenteDto;

/**
 * Interface que define os servi�os a serem expostos referentes aos dados de aulas dos docentes.
 * 
 * @author R�mulo Augusto
 */
@WebService
public interface AulasDocenteRemoteService {

	/**
	 * Verifica se o afastamento do servidor docente no per�odo coincide com aulas que necessitem de reposi��o 
	 * e retorna uma mensagem com as informa��es das turmas afetadas em forma de lista n�o-ordenada (&lt;ul&gt; &lt;li&gt;).
	 * 
	 * @param aulasDocenteDto - DTO com os dados para consulta (identificador do servidor, in�cio do afastamento, fim do afastamento).
	 * @return String com as informa��es das turmas afetadas em forma de lista n�o-ordenada (&lt;ul&gt; &lt;li&gt;)
	 * @throws DAOException
	 */
	public String getTurmasDocenteAsString(AulasDocenteDto aulasDocenteDto);
}
