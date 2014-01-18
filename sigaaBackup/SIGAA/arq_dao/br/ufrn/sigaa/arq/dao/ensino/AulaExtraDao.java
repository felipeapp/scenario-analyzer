/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/11/2010
 *
 */


package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Dao responsável por consultas específicas às aulas extras das turmas.
 * 
 * @author Fred_Castro
 */
public class AulaExtraDao extends GenericSigaaDAO {

	/** Construtor padrão. */
	public AulaExtraDao() {
	
	}

	/**
	 * Retorna todas as aulas extras existentes na turma passada.
	 * 
	 * @param idsTurmas
	 * @return
	 * @throws DAOException
	 */
	public List <AulaExtra> findByIdsTurmas (List <Integer> idsTurmas) throws DAOException {
		@SuppressWarnings("unchecked")
		List <AulaExtra> rs = getSession().createQuery("select a from AulaExtra a where a.ativo = trueValue() and a.turma.id in " + UFRNUtils.gerarStringIn(idsTurmas)).list();
		
		return rs;
	}
	
	/**
	 * Retorna a {@link AulaExtra} associada ao {@link TopicoAula} cujo id foi passado como parâmetro,
	 * ou <code>null</code> caso não exista.
	 * 
	 * @param topicoAula
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public AulaExtra findByTopicoAula (int topicoAula) throws HibernateException, DAOException {
		String projecao = "a.id";
		
		String hql = "select " + projecao + " from TopicoAula t JOIN t.aulaExtra a WHERE t.id = :idTopico";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("idTopico", topicoAula);
		
		@SuppressWarnings("unchecked")
		Collection<AulaExtra> aula = HibernateUtils.parseTo(q.list(), projecao, AulaExtra.class, "a");
		
		return (isEmpty(aula) ? null : aula.iterator().next());
	}
	
	/**
	 * Retorna as aulas de ensino individual de determinada turma
	 * @param turma
	 * @return
	 */
	public List<AulaExtra> buscarAulasEnsinoIndividual(Turma turma) {
		DetachedCriteria c = DetachedCriteria.forClass(AulaExtra.class);
		c.add(eq("turma.id", turma.getId()));
		c.add(Restrictions.eq("tipo", AulaExtra.ENSINO_INDIVIDUAL)).addOrder(asc("dataAula"));
		
		@SuppressWarnings("unchecked")
		List<AulaExtra> lista = getHibernateTemplate().findByCriteria(c);
		return lista;
	}
	
	/**
	 * Retorna a lista de aulas de ensino de individual das sub-turmas de uma turma.
	 * @param turma
	 * @return
	 */
	public List<AulaExtra> buscarAulasEnsinoIndividualSubTurmas(Turma turma) {
		@SuppressWarnings("unchecked")
		List<AulaExtra> lista = getHibernateTemplate().find("from AulaExtra a where a.turma.id in " + UFRNUtils.gerarStringIn(turma.getSubturmas()) + " and a.tipo = "+AulaExtra.ENSINO_INDIVIDUAL+" order by dataAula asc");
		return lista;
	}
}