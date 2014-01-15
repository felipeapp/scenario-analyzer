/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '29/09/206'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Dao para consultas relativas aos consultores do CNPq
 * @author ilueny santos
 * @author Leonardo Campos
 *
 */
public class ConsultorDao extends GenericSigaaDAO {
	
	/**
	 * Buscar um consultor a partir do seu código de acesso
	 *
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public Consultor findByServidor(Servidor servidor) throws DAOException{
		try {
			return (Consultor) getSession().createCriteria(Consultor.class)
			.add( Restrictions.eq("servidor", servidor))
			.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Buscar um consultor a partir do seu código de acesso
	 *
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public Consultor findByCodigoAcesso(Integer codigo) throws DAOException{
        try {
        	return (Consultor) getSession().createCriteria(Consultor.class)
        		.add( Expression.eq("codigo", codigo))
        		.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Verificar se o código e senha de acesso pertencem a um consultor cadastrado
	 *
	 * @param codigo
	 * @param senha
	 * @return
	 * @throws DAOException
	 */
	public Consultor validateAcesso(Integer codigo, String senha) throws DAOException {
        try {
        	return (Consultor) getSession().createCriteria(Consultor.class)
        		.add( Expression.eq("codigo", codigo))
        		.add( Expression.eq("senha", senha.trim()))
        		.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Buscar consultores por nome e tipo
	 *
	 * @param nome
	 * @param internos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Consultor> findByNomeAndTipo(String nome, Boolean internos) throws DAOException {
        try {
        	Criteria c = getSession().createCriteria(Consultor.class)
    			.add( Expression.like("nome", nome.trim().toUpperCase() + "%"));

        	if (internos != null) {
        		c.add( Expression.eq("interno", internos) );
        	}
        	c.addOrder( Order.asc( "nome" ) );

        	return c.list() ;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Retorna uma coleção com um número máximo de consultores que
	 * atuam em determinada área de conhecimento
	 *
	 * @param areaConhecimentoCnpq
	 * @param maxResultados número máximo de consultores
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Consultor> findByAreaConhecimentoCnpq(AreaConhecimentoCnpq areaConhecimentoCnpq, Integer maxResultados) throws DAOException{
        try {

        	StringBuffer hql = new StringBuffer();
        	hql.append( " SELECT new Consultor(c.id, c.qtdAvaliacoes) FROM Consultor c " );
        	hql.append( " WHERE c.areaConhecimentoCnpq.id = :idArea AND c.interno = falseValue() " );
        	hql.append( " AND c.id not in ( SELECT a.consultor.id from AvaliacaoProjeto a WHERE year(a.dataAvaliacao) = " + CalendarUtils.getAnoAtual() +
        									" AND a.situacao = " + AvaliacaoProjeto.DESISTENTE +" ) " );

        	hql.append( " ORDER BY c.qtdAvaliacoes ASC, random()" );

        	Query query = getSession().createQuery( hql.toString() );
            query.setInteger("idArea", areaConhecimentoCnpq.getId());

            query.setMaxResults(maxResultados);

            return query.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Consultor> findByAreaConhecimentoCnpqEspeciais(AreaConhecimentoCnpq areaConhecimentoCnpq, Integer maxResultados) throws DAOException{
        try {

        	StringBuffer hql = new StringBuffer();
        	hql.append( " SELECT new Consultor(c.id, c.qtdAvaliacoes) FROM Consultor c, ConsultoriaEspecial ce " );
        	hql.append( " WHERE c.areaConhecimentoCnpq.id = :idArea " );
        	hql.append( " AND c.id = ce.consultor.id " );
        	hql.append( " AND (:hoje BETWEEN ce.dataInicio AND  ce.dataFim)" );

        	hql.append( " ORDER BY c.qtdAvaliacoes ASC, random()" );

        	Query query = getSession().createQuery( hql.toString() );
            query.setInteger("idArea", areaConhecimentoCnpq.getId());
            query.setDate("hoje", new Date());

            query.setMaxResults(maxResultados);

            return query.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Buscar o total de consultores que ainda possuem avaliações pendentes
	 *
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalPendentesNotificacao() throws DAOException {
		try {
        	String hql = " SELECT count(distinct c.id) FROM Consultor c join c.avaliacoesProjeto AS a" +
        			" WHERE a.dataAvaliacao is null and a.situacao = " + AvaliacaoProjeto.AGUARDANDO_AVALIACAO;

        	return (Long) getSession().createQuery( hql ).uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Retorna a lista de consultores que possuem projetos pendentes de avaliacao
	 * juntamente com a lista destes projetos
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<Consultor, Collection<ProjetoPesquisa>> findPendentesNotificacao() throws DAOException {
		Map<Consultor, Collection<ProjetoPesquisa>> resultado = new HashMap<Consultor, Collection<ProjetoPesquisa>>();

		try {
			int[] statusDistribuicao = new int[] { TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE, TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE };

        	String hql = " SELECT c.id, c.nome, c.codigo, c.email, c.senha, c.interno, a.projetoPesquisa.codigo, a.projetoPesquisa.projeto.titulo " +
        			" FROM Consultor c join c.avaliacoesProjeto AS a " +
        			" WHERE a.dataAvaliacao is null " +
        			" AND a.situacao = " + AvaliacaoProjeto.AGUARDANDO_AVALIACAO +
        			" AND a.projetoPesquisa.projeto.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(statusDistribuicao);

        	List lista = getSession().createQuery(hql).list();

			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				Consultor consultor = new Consultor();
				consultor.setId( (Integer) colunas[col++] );
				consultor.setNome( (String) colunas[col++] );
				consultor.setCodigo( (Integer) colunas[col++] );
				consultor.setEmail( (String) colunas[col++]  );
				consultor.setSenha( (String) colunas[col++]  );
				consultor.setInterno( (Boolean) colunas[col++] );

				// Buscar ou criar lista de projetos pendentes do consultor
				Collection<ProjetoPesquisa> projetos = resultado.get(consultor);
				if (projetos == null ) {
					projetos = new ArrayList<ProjetoPesquisa>();
					resultado.put(consultor, projetos );
				}

				ProjetoPesquisa projeto = new ProjetoPesquisa();
				projeto.setCodigo( (CodigoProjetoPesquisa) colunas[col++] );
				projeto.setTitulo( (String) colunas[col++] );

				projetos.add(projeto);
			}

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
		return resultado;
	}

	/**
	 * Busca os consultores especiais externos cujo período de consultoria está vigente.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<Consultor, Collection<ProjetoPesquisa>> findEspeciaisPendentesNotificacao() throws DAOException {
		Map<Consultor, Collection<ProjetoPesquisa>> resultado = new HashMap<Consultor, Collection<ProjetoPesquisa>>();

		try {
        	String hql = " SELECT ce.consultor.id, ce.consultor.nome, ce.consultor.codigo, ce.consultor.email, ce.consultor.senha, " +
        			" ce.consultor.areaConhecimentoCnpq.grandeArea.nome " +
        			" FROM ConsultoriaEspecial ce " +
        			" WHERE ce.dataFim >= :hoje " +
        			" AND ce.consultor.interno = falseValue() ";

        	Query q = getSession().createQuery(hql);
        	q.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
        	List lista = q.list();

			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				Consultor consultor = new Consultor();
				consultor.setId( (Integer) colunas[col++] );
				consultor.setNome( (String) colunas[col++] );
				consultor.setCodigo( (Integer) colunas[col++] );
				consultor.setEmail( (String) colunas[col++]  );
				consultor.setSenha( (String) colunas[col++]  );

				consultor.setAreaConhecimentoCnpq( new AreaConhecimentoCnpq() );
				consultor.getAreaConhecimentoCnpq().setNome( (String) colunas[col++] );

				resultado.put(consultor, null);
			}

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
		return resultado;
	}

	public List<Long> findResumoAvaliacoes(int idConsultor) throws DAOException {
		List<Long> resumo = new ArrayList<Long>();
		try {
			Object[] somas = null;

			Query query = getSession()
				.createQuery(
					"select count(ap.id), " +
					" sum(case when ap.dataAvaliacao is not null then 1 else 0 end), " +
					" sum(case when ap.dataAvaliacao is null then 1 else 0 end) " +
					" from AvaliacaoProjeto ap" +
					" where ap.consultor.id = :idConsultor");

			query.setInteger("idConsultor", idConsultor);
			somas = (Object[]) query.uniqueResult();

			for (int i = 0; i < somas.length; i++) {
				resumo.add( (Long) somas[i] );
			}

			query = getSession()
				.createQuery(
					"select count(pt.id), " +
					" sum(case when pt.dataAvaliacao is not null then 1 else 0 end), " +
					" sum(case when pt.dataAvaliacao is null then 1 else 0 end) " +
					" from PlanoTrabalho pt" +
					" where pt.consultor.id = :idConsultor");

			query.setInteger("idConsultor", idConsultor);
			somas = (Object[]) query.uniqueResult();

			for (int i = 0; i < somas.length; i++) {
				resumo.add( (Long) somas[i] );
			}

			query = getSession()
				.createQuery(
					"select count(rp.id), " +
					" sum(case when rp.dataAvaliacao is not null then 1 else 0 end), " +
					" sum(case when rp.dataAvaliacao is null then 1 else 0 end) " +
					" from RelatorioProjeto rp" +
					" where rp.consultor.id = :idConsultor");

			query.setInteger("idConsultor", idConsultor);
			somas = (Object[]) query.uniqueResult();

			for (int i = 0; i < somas.length; i++) {
				resumo.add( (Long) somas[i] );
			}

			return resumo;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

}