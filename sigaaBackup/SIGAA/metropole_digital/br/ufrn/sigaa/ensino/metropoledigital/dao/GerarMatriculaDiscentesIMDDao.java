package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

/***
 * 
 * DAO responsável pelas consultas relacionadas a geração de matrícula dos discentes do IMD que não possuem matrícula
 * 
 * @author Rafael Barros
 *
 */

public class GerarMatriculaDiscentesIMDDao extends GenericSigaaDAO {

	/**
	 * Lista os discentes do IMD que não possuem matrícula de acordo com os filtros informados
	 * 
	 * @param idProcessoSeletivo, idOpcao
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteTecnico> findDiscentesSemMatricula(Integer idProcessoSeletivo, Integer idOpcao) throws DAOException{
		try {
			
			Criteria cDiscTec = getSession().createCriteria(DiscenteTecnico.class);
			Criteria cDiscente = cDiscTec.createCriteria("discente");
			Criteria cPessoa = cDiscente.createCriteria("pessoa");
			
			cDiscTec.add(Expression.eq("processoSeletivo.id", idProcessoSeletivo));
			
			if(idOpcao != null && idOpcao > 0) {
				cDiscTec.add(Expression.eq("opcaoPoloGrupo.id", idOpcao));
			}
			
			cDiscente.add(Expression.isNull("matricula"));
			cPessoa.addOrder(org.hibernate.criterion.Order.asc("nome"));
			
			return cDiscTec.list();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
}
