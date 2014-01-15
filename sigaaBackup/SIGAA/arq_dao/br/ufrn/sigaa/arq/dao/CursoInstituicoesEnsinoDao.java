/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *  DAO para consulta espec�fica do relacionamento entre cursos, que perten�a a uma rede de institui��es de ensino
 *  e as institui��es vinculadas ao curso.
 * @author Rafael Gomes
 *
 */
public class CursoInstituicoesEnsinoDao extends GenericSigaaDAO{
	
	/**
	 * M�todo respons�vel por retornar as institui��es de ensino vinculadas a um curso, 
	 * que perten�a a uma rede de ensino entre outras institui��es.
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
