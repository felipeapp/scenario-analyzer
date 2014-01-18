package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloDisciplina;

/**
 * 
 * DAO responsável pelas consultas dos Componentes Curriculares do curso técnico do IMD . 
 * 
 * @author Rafael Silva
 *
 */

public class ComponenteCurricularDao extends GenericSigaaDAO{
	
	/**
	 * Lista os Componentes Curriculare vinculados ao módulo informado.
	 * 
	 * @param idModulo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ComponenteCurricular> getByModulo(int idModulo) throws DAOException{
		Criteria c = getSession().createCriteria(ModuloDisciplina.class);
		Criteria cModulo = c.createCriteria("modulo");
		c.createCriteria("disciplina");
		
		cModulo.add(Expression.eq("id", idModulo));
		
		List<ComponenteCurricular> listaComponentes = new ArrayList<ComponenteCurricular>();
		for (ModuloDisciplina md: (List<ModuloDisciplina>) c.list()) {
			listaComponentes.add(md.getDisciplina());
		}
	
		return listaComponentes;
	}
}
