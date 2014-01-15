/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 16/06/2011
 * Autor: Arlindo Rodrigues
 */

package br.ufrn.sigaa.ensino.medio.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;

/**
 * Dao para consulta sobre a entidade DiscenteMedio
 * 
 * @author Arlindo
 *
 */
public class DiscenteMedioDao  extends GenericSigaaDAO {
	
	/**
	 * Busca os discentes de ensino médio ativos da pessoa passada por parâmetro
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public DiscenteMedio findAtivoByPessoa(int idPessoa) throws DAOException {
		Criteria c = getSession().createCriteria(DiscenteMedio.class);
		Criteria sc = c.createCriteria("discente");
		
		sc.add(Restrictions.eq("pessoa.id", idPessoa));
		sc.add(Restrictions.eq("nivel", NivelEnsino.MEDIO));
		sc.add(Restrictions.eq("status", StatusDiscente.ATIVO));
		sc.add(Restrictions.eq("status", StatusDiscente.ATIVO_DEPENDENCIA));

		return (DiscenteMedio) c.uniqueResult();
	}	
	

	/**
	 * Busca todas as disciplinas de um discente com matrículas com status
	 * MATRICULADA e EM_ESPERA. (( OTIMIZAÇÃO PARAMETRIZADA ))
	 *
	 * @param idDiscente
	 * @param otimizado
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findDisciplinasMatriculadas(int idDiscente, boolean otimizado) throws DAOException {
		
		try {
			StringBuffer hql = new StringBuffer();
			if (otimizado) {
				hql.append("select distinct m.turma.id, m.componente.id, m.componente.detalhes.nome, m.componente.codigo ");
			} else {
				hql.append("select distinct m.turma ");
			}
			hql.append("from MatriculaComponente m ");
			if (!otimizado) {
				hql.append(" left join fetch m.turma.horarios ");
			}
			hql.append("where m.discente.id = :idDiscente and "
					+ "m.turma.id > 0 and m.situacaoMatricula.id "
					+ "in (:matriculado, :espera)");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idDiscente", idDiscente);
			q.setInteger("matriculado", SituacaoMatricula.MATRICULADO.getId());
			q.setInteger("espera", SituacaoMatricula.EM_ESPERA.getId());
			if (!otimizado) {
				@SuppressWarnings("unchecked")
				Collection<Turma> lista = q.list();
				return lista;
			} else {
				ArrayList<Turma> turmas = new ArrayList<Turma>(0);
				@SuppressWarnings("unchecked")
				List<Object[]> res = q.list();
				if (res != null) {
					for (Object[] id : res) {
						Turma t = new Turma((Integer) id[0]);
						t.setDisciplina(new ComponenteCurricular(
								(Integer) id[1]));
						t.getDisciplina().setDetalhes(new ComponenteDetalhes());
						t.getDisciplina().getDetalhes().setNome( (String) id[2]);
						t.getDisciplina().setCodigo( (String) id[3]);
						turmas.add(t);
					}
				}
				return turmas;
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

}
