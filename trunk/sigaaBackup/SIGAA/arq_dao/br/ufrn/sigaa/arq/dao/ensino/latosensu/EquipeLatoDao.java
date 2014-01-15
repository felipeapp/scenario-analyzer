/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/11/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteDisciplinaLato;

/**
 * Consultas de Equipe Lato. 
 *
 * @author Leonardo
 *
 */
public class EquipeLatoDao extends GenericSigaaDAO {

	public EquipeLatoDao(){
		daoName = "equipeLatoDao";
	}

	/**
	 * Busca todos os registros de equipe lato de uma dada disciplina e de uma mesma proposta.
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CorpoDocenteDisciplinaLato> findByDisciplina( int idDisciplina, int idProposta ) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CorpoDocenteDisciplinaLato.class);
			c.add( Expression.eq("disciplina.id", idDisciplina));
			c.add( Expression.eq("proposta.id", idProposta));

			return c.list();
		} catch( Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca todos os registros de equipe lato para o curso informado.
	 * @param idCurso
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CorpoDocenteDisciplinaLato> findByCurso(int idCurso) throws ArqException {
		
		String sql = "SELECT el.id_equipe_lato AS id, el.id_disciplina AS componente, el.carga_horaria AS ch," +
			" (SELECT nome FROM comum.pessoa WHERE (s.id_pessoa IS NOT NULL AND id_pessoa=s.id_pessoa)" +
			" OR (de.id_pessoa IS NOT NULL AND id_pessoa=de.id_pessoa)) AS nome FROM lato_sensu.curso_lato cl," +
			" lato_sensu.proposta_curso_lato pcl, lato_sensu.equipe_lato el LEFT JOIN ensino.docente_externo de ON" +
			" el.id_docente_externo=de.id_docente_externo LEFT JOIN rh.servidor s ON el.id_servidor=s.id_servidor WHERE" +
			" el.id_proposta=pcl.id_proposta AND pcl.id_proposta=cl.id_proposta AND cl.id_curso="+idCurso+" ORDER BY el.id_disciplina";
		
		return getSession().createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(CorpoDocenteDisciplinaLato.class)).list();
	}

}
