/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/03/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ead;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ead.dominio.HorarioTutoria;

/**
 * Dao que executa consultas sobre a entidade HorarioTutoria
 * @author Andre M Dantas
 * @author Victor Hugo
 *
 */
public class HorarioTutoriaDao extends GenericSigaaDAO {

	/**
	 * Retorna os horários das tutorias dado uma tutoria,ano,periodo
	 * @param tutoria
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HorarioTutoria> findByTutoria(int tutoria, int ano, int periodo) throws DAOException {
		StringBuilder hql = new StringBuilder(" SELECT ht FROM HorarioTutoria ht ");
		hql.append( " WHERE ht.ano = :ano " );
		hql.append( " AND ht.periodo = :periodo " );
		hql.append( " AND ht.tutoria.id = :idTutoria" );
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idTutoria", tutoria);
		
		return q.list();
	}

	/**
	 * Retorna os horários das tutorias dado uma discente ,ano,periodo
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HorarioTutoria> findByDiscente(int idDiscente, int ano, int periodo) throws DAOException {
		StringBuilder hql = new StringBuilder(" SELECT ht FROM HorarioTutoria ht ");
		hql.append( " WHERE ht.ano = :ano " );
		hql.append( " AND ht.periodo = :periodo " );
		hql.append( " AND ht.tutoria.aluno.id = :idDiscente " );
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idDiscente", idDiscente);
		
		return q.list();
	}
	
	/**
	 * Retorna os dias da semana que o discente informado possui tutoria no ano/período indicados
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findDiasSemanaTutoriaByDiscente(int idDiscente, int ano, int periodo) throws DAOException {
		StringBuilder hql = new StringBuilder(" SELECT ht.diaSemana FROM HorarioTutoria ht ");
		hql.append( " WHERE ht.ano = :ano " );
		hql.append( " AND ht.periodo = :periodo " );
		hql.append( " AND ht.tutoria.aluno.id = :idDiscente " );
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idDiscente", idDiscente);
		
		return q.list();
	}
	
	/**
	 * Remove o HorarioTutoria dado o discente, dia da semana, ano e período
	 * @param idDiscente
	 * @param dia
	 * @param ano
	 * @param periodo
	 * @throws DAOException
	 */
	public void removerHorarioTutoriaByDiscenteDiaAnoPeriodo( int idDiscente, int dia, int ano, int periodo ) throws DAOException{
		
		StringBuilder hql = new StringBuilder(" SELECT ht FROM HorarioTutoria ht ");
		hql.append( " WHERE ht.ano = :ano " );
		hql.append( " AND ht.periodo = :periodo " );
		hql.append( " AND ht.diaSemana = :dia " );
		hql.append( " AND ht.tutoria.aluno.id = :idDiscente " );
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("dia", dia);
		q.setInteger("idDiscente", idDiscente);
		
		HorarioTutoria ht = (HorarioTutoria) q.uniqueResult();
		
		if( ht != null )
			remove(ht);
	}
	
}
