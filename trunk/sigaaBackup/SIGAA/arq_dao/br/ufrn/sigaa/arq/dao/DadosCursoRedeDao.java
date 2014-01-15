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
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;

/**
 *  DAO para consulta espec�fica do relacionamento entre cursos, que perten�a a uma rede de institui��es de ensino
 *  e as institui��es vinculadas ao curso.
 * @author Rafael Gomes
 *
 */
public class DadosCursoRedeDao extends GenericSigaaDAO{
	
	/**
	 * M�todo respons�vel por retornar as institui��es de ensino vinculadas a um curso, 
	 * que perten�a a uma rede de ensino entre outras institui��es.
	 * @param id_curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<DadosCursoRede> findByCurso(int id_curso) throws DAOException {
		Criteria c = getSession().createCriteria(DadosCursoRede.class);
		
		c.add(Restrictions.eq("curso.id", id_curso));

		@SuppressWarnings("unchecked")
		List<DadosCursoRede> lista = c.list();
		
		return lista;
	}
	
	public DadosCursoRede findByCampusPrograma(CampusIes campus, ProgramaRede programa) throws DAOException {
		Criteria c = getSession().createCriteria(DadosCursoRede.class);
		
		c.add(Restrictions.eq("campus.id", campus.getId()));
		c.add(Restrictions.eq("programaRede.id", programa.getId()));

		return (DadosCursoRede) c.uniqueResult();
	}
}
