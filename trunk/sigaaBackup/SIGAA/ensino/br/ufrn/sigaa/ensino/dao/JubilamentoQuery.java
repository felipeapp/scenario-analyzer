/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Interface com método a ser implementado para buscar os alunos passíveis de jubilamento. 
 * 
 * @author Igor Linnik 
 * 
 ******************************************************************************/

public interface JubilamentoQuery {	
	/*
	 * Método que deverá ser implementado para buscar todos os alunos de um tipo, passíveis de jubilamento
	 * em um ou mais semestres. Pode ou não retornar alunos matriculados e isto é especificado em um parâmetro.
	 */
	public Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Boolean ead, Boolean filtroMatriculados, Unidade unidade, Boolean reprovacoesComp) throws DAOException;	
}
