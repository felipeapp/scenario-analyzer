/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2011
 *
 */

package br.ufrn.sigaa.ensino.dao;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;



/*******************************************************************************
 * Interface com m�todo a ser implementado para buscar os alunos pass�veis de jubilamento. 
 * 
 * @author Igor Linnik 
 * 
 ******************************************************************************/

public interface JubilamentoQuery {	
	/*
	 * M�todo que dever� ser implementado para buscar todos os alunos de um tipo, pass�veis de jubilamento
	 * em um ou mais semestres. Pode ou n�o retornar alunos matriculados e isto � especificado em um par�metro.
	 */
	public Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Boolean ead, Boolean filtroMatriculados, Unidade unidade, Boolean reprovacoesComp) throws DAOException;	
}
