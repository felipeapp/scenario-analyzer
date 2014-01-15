package br.ufrn.sigaa.apedagogica.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;


/**
 * Classe de acesso aos dados das atividades de atualização pedagógica.
 * @author Mário Rizzi
 *
 */
public class AtividadeAtualizacaoPedagogicaDAO extends GenericSigaaDAO {
	
	/**
	 * Retorna as atividades e a quantidade de participantes de acordo commo grupo passado por parâmetro.
	 * @param idGrupo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeAtualizacaoPedagogica> findAtividadesByGrupo(Integer idGrupo) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		StringBuilder projecao =   new StringBuilder("id, nome, ch, numVagas, inicio, fim, grupoAtividade.id, grupoAtividade.denominacao, ");
		projecao.append(" (SELECT SUM(1) FROM ParticipanteAtividadeAtualizacaoPedagogica WHERE atividade.id = a.id) AS numVagasAtual ");
		
		hql.append(" SELECT DISTINCT ");
		hql.append(projecao);
		hql.append(" FROM AtividadeAtualizacaoPedagogica a ");
		hql.append(" WHERE a.grupoAtividade.id = " + idGrupo );
		hql.append(" AND a.ativo = " + Boolean.TRUE );
		hql.append(" GROUP BY ");
		hql.append(" id, nome, ch, numVagas, inicio, fim, grupoAtividade.id, grupoAtividade.denominacao ");
		hql.append(" ORDER BY a.id ");
		
		Query q = getSession().createQuery(hql.toString());
		
		List<Object[]> lista = q.list();

		Collection<AtividadeAtualizacaoPedagogica> result = new LinkedHashSet<AtividadeAtualizacaoPedagogica>();
		
		for (int a = 0; a < lista.size(); a++) {
			
			int col = 0;
			Object[] colunas = lista.get(a);
			
			AtividadeAtualizacaoPedagogica atividade = new AtividadeAtualizacaoPedagogica();
			atividade.setId((Integer) colunas[col++]);
			atividade.setNome((String) colunas[col++]);
			atividade.setCh((Integer) colunas[col++]);
			atividade.setNumVagas((Integer) colunas[col++]);
			atividade.setInicio((Date) colunas[col++]);
			atividade.setFim((Date) colunas[col++]);
			atividade.setGrupoAtividade(new GrupoAtividadesAtualizacaoPedagogica());
			atividade.getGrupoAtividade().setId((Integer) colunas[col++]);
			atividade.getGrupoAtividade().setDenominacao((String) colunas[col++]);
			
			Long numVagasAtual = (Long) colunas[col++];
			if(numVagasAtual!=null)
				atividade.setNumVagasAtual(Integer.valueOf(numVagasAtual.toString()));
			
			result.add(atividade);
			
		}
		
		return result;

	}
	
	/**
	 * Retorna as atividades que iniciam com o nome passado como parâmetro
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeAtualizacaoPedagogica> findByGrupoAtividade(Integer idGrupo, String atividade) throws DAOException {
		
		Criteria c = getSession().createCriteria(AtividadeAtualizacaoPedagogica.class);
		
		if( !isEmpty(idGrupo) )
			c.add(Expression.eq("grupoAtividade.id", idGrupo));
		if( !isEmpty(atividade) )
			c.add(Expression.ilike("nome", StringUtils.toAscii(atividade) + "%"));
		c.add(Expression.eq("ativo", Boolean.TRUE));
		
		return c.list();
	}
	
	/**
	 * Retorna as atividades dentro de um determinado ano e período.
	 * @param ano
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeAtualizacaoPedagogica> findByAnoPeriodo(Integer ano, Integer periodo) throws HibernateException, DAOException{
		
		String hql = " select atividade from AtividadeAtualizacaoPedagogica atividade" +
						" where atividade.ativo = trueValue() " +
				        " and " + HibernateUtils.generateDateIntersection("atividade.inicio", "atividade.fim", ":inicial" , ":final");
		
		Query query = getSession().createQuery(hql);
		query.setDate("inicial", CalendarUtils.createDate(01,  periodo == 1 ? 0 : 6 , ano) );
		query.setDate("final", CalendarUtils.createDate(01,  periodo == 1 ? 6 : 0 , periodo == 1 ? ano : ano+1) );
		return query.list();

	}
	
	/**
	 * Retorna as atividades de determinado grupo e participante
	 * @param idGrupo
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeAtualizacaoPedagogica> findByGrupoParticipante(Integer idGrupo, Integer idServidor) throws HibernateException, DAOException{
		
		String hql = " select p.atividade from ParticipanteAtividadeAtualizacaoPedagogica p" +
						" where p.atividade.ativo = trueValue() " +
				        " and p.atividade.grupoAtividade.id = "+idGrupo+" "+// OBS
				        " and p.docente.id = "+idServidor+" ";
		
		Query query = getSession().createQuery(hql);
		return query.list();

	}
	
}