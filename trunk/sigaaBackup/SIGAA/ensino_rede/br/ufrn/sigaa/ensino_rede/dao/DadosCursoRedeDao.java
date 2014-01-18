package br.ufrn.sigaa.ensino_rede.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;

public class DadosCursoRedeDao extends GenericSigaaDAO{
	
	/**
	 * Método responsável por retornar as instituições de ensino vinculadas a um curso, 
	 * que pertença a uma rede de ensino entre outras instituições.
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
