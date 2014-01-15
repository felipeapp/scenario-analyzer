/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/04/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.arq.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.CursoInstituicoesEnsino;

/**
 *  DAO para consulta específica do relacionamento entre cursos, que pertença a uma rede de instituições de ensino
 *  e as instituições vinculadas ao curso.
 * @author Rafael Gomes
 *
 */
public class CursoInstituicoesEnsinoDao extends GenericSigaaDAO{
	
	/**
	 * Método responsável por retornar as instituições de ensino vinculadas a um curso, 
	 * que pertença a uma rede de ensino entre outras instituições.
	 * @param id_curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<CursoInstituicoesEnsino> findByCurso(int id_curso) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(CursoInstituicoesEnsino.class);
			
			c.add(Restrictions.eq("curso.id", id_curso));

			@SuppressWarnings("unchecked")
			List<CursoInstituicoesEnsino> lista = c.list();
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
}
