package br.ufrn.sigaa.apedagogica.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.StatusParticipantesAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;


/**
 * Classe de acesso aos dados dos participantes em atividades de atualização pedagógica.
 * @author Mário Rizzi
 *
 */
public class ParticipanteAtividadeAPDAO extends GenericSigaaDAO {
	
	/**
	 * Retorna os participantes em uma atividade
	 * @param idGrupo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> findByAtividade(Integer idAtividade) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT pa FROM ParticipanteAtividadeAtualizacaoPedagogica pa ");
		hql.append(" INNER JOIN pa.atividade a JOIN FETCH pa.docente d JOIN FETCH ");
		hql.append(" d.pessoa p  WHERE a.id = " + idAtividade + "ORDER BY p.nome ");
		Query q = getSession().createQuery(hql.toString());
		return q.list();

	}
	
	/**
	 * Retorna os participantes em uma atividade
	 * @param idGrupo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> 
		findByParticipantes(Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantes) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT pa FROM ParticipanteAtividadeAtualizacaoPedagogica pa ");
		hql.append(" INNER JOIN pa.atividade a JOIN FETCH pa.docente d JOIN FETCH ");
		hql.append(" d.pessoa p  WHERE pa.id IN " + UFRNUtils.gerarStringIn(participantes) + "ORDER BY p.nome ");
		Query q = getSession().createQuery(hql.toString());
		return q.list();

	}
	
	/**
	 * Retorna todas as participações do docente em atividades do pap
	 * @param idGrupo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> 
		findByAnoDocente(Integer ano, Integer idDocente) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT pa FROM ParticipanteAtividadeAtualizacaoPedagogica pa ");
		hql.append(" INNER JOIN pa.docente d INNER JOIN pa.atividade a WHERE d.id = :idDocente AND pa.situacao = :situacao ");
		hql.append(" AND ( YEAR(a.inicio) = :ano OR YEAR(a.fim ) = :ano) ");
		hql.append(" ORDER BY a.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("situacao", StatusParticipantesAtualizacaoPedagogica.CONCLUIDO.getId());
		q.setInteger("idDocente", idDocente);
		q.setInteger("ano", ano);
		
		return q.list();

	}
	
	/**
	 * Retorna todas as participações do docente em atividades específicas
	 * @param idsAtividades
	 * @param idDocente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> findByAtividadesDocente(List<Integer> idsAtividade, Integer idDocente) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT pa FROM ParticipanteAtividadeAtualizacaoPedagogica pa ");
		hql.append(" INNER JOIN pa.docente d ");
		hql.append(" INNER JOIN pa.atividade a ");
		hql.append(" WHERE d.id = :idDocente AND pa.atividade.id in "+UFRNUtils.gerarStringIn(idsAtividade) );
		hql.append(" ORDER BY a.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idDocente", idDocente);
		
		return q.list();
	}
	

}