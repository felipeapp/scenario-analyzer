/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/09/2009
 *
 */		
package br.ufrn.sigaa.arq.dao.ensino;


import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;

/**
 * DAO responsável pela consulta das datas de agendamento do processo seletivo.
 *
 * @author Mário Rizzi
 *
 */
public class EditalProcessoSeletivoDao extends GenericSigaaDAO {

	
	
	/**
	 * Retorna todos os editais de processo seletivo de acordo com o nível de ensino.
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalProcessoSeletivo> findByNivel(char nivel) 
		throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT DISTINCT e FROM ProcessoSeletivo ps INNER JOIN ps.editalProcessoSeletivo e ");
		hql.append(" LEFT JOIN ps.curso c LEFT JOIN  ps.matrizCurricular m ");
		hql.append(" WHERE c.nivel = :nivel OR m.curso.nivel= :nivel");
		hql.append(" ORDER BY e.nome  ");

		Query q = getSession().createQuery(hql.toString());
		q.setCharacter("nivel", nivel);
		
		return  q.list();
	}
	

	/** Retorna a quantidade de candidatos inscritos em um Edital.
	 * @param idEdital
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countTotalInscricoesByEdital(int idEdital) throws HibernateException, DAOException{
		String hql = " select count(*)" +
				" from InscricaoSelecao inscricao" +
				" where inscricao.processoSeletivo.editalProcessoSeletivo.id = :idEdital";
		Query q = getSession().createQuery(hql);
		q.setInteger("idEdital", idEdital);
		return ((Long) q.uniqueResult()).intValue();
	}
}
