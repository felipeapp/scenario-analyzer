/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/07/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável pelas consultas a cotas de docentes
 *
 * @author Ricardo Wendell
 *
 */
public class CotaDocenteDao extends GenericSigaaDAO {

	/**
	 * Busca as distribuições de cotas para um determinado edital
	 *
	 * @param edital
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CotaDocente> findByEditalPesquisa(EditalPesquisa edital, Integer idUnidade) throws DAOException {
        try {
        	Criteria c = getSession().createCriteria(CotaDocente.class)
        		.add( Restrictions.eq("edital.id", edital.getId()) );

        	if (idUnidade != null && idUnidade > 0) {
        		c.createAlias("docente.unidade", "unidade");
        		c.add( Restrictions.or(Restrictions.eq("unidade.id", idUnidade),  Restrictions.eq("unidade.gestora.id", idUnidade)) );
        	}

        	return 	c.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca uma cota específica recebida por um docente em
	 * um determinado período de cota de bolsas
	 * 
	 * @param docente
	 * @param cota
	 * @return
	 * @throws DAOException
	 */
	public CotaDocente findByDocenteAndCota(Servidor docente, CotaBolsas cota) throws DAOException {
        try {
        	Criteria c = getSession().createCriteria(CotaDocente.class)
        		.createAlias("edital.cota", "cota")
        		.add( Expression.eq("cota.id", cota.getId()) )
        		.add( Expression.eq("docente.id", docente.getId()) );

        	return 	(CotaDocente) c.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca uma cota específica recebida por um docente através de
	 * um determinado edital de pesquisa
	 * 
	 * @param docente
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	public CotaDocente findByDocenteAndEdital(Servidor docente, EditalPesquisa edital) throws DAOException {
        try {
        	Criteria c = getSession().createCriteria(CotaDocente.class)
        		.add( Expression.eq("edital.id", edital.getId()) )
        		.add( Expression.eq("docente.id", docente.getId()) );

        	return 	(CotaDocente) c.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	/**
	 * Busca todas as cotas de bolsa recebidas por um docente num determinado período de cotas.
	 * 
	 * @param docente
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CotaDocente> findByDocentePeriodoCota(Servidor docente, CotaBolsas periodoCota) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(CotaDocente.class)
			.createAlias("edital.cota", "cota")
			.add( Restrictions.eq("cota.id", periodoCota.getId()) )
			.add( Restrictions.eq("docente.id", docente.getId()) );
			
			return 	c.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Buscar as distribuições de cotas vinculadas a um ano.
	 * Traz somente as cotas cujo início é no ano passado como parâmetro.
	 *
	 * @param docente
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CotaDocente> findByDocenteAndAno(Servidor docente, int ano) throws DAOException {
        try {
        	Criteria c = getSession().createCriteria(CotaDocente.class).createAlias("edital.cota", "cota")
        	.add(Expression.ge("cota.fim", new Date()))
        	.add( Expression.eq("docente.id", docente.getId()))
        	.addOrder( Order.desc("cota.descricao"));

        	return 	c.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca os dados utilizados no relatório de acompanhamento de distribuição de cotas
	 * de acordo com o período de cotas de bolsas informado.
	 * 
	 * @param cota
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection findResumoDistribuicaoCota( CotaBolsas cota ) throws DAOException {
		try {
			List<Object[]> tiposBolsa = getSession().createSQLQuery(
					" select distinct(c.id_tipo_bolsa_pesquisa) as id, t.descricao || ' (' || case when t.categoria = 1 then 'IC' else 'IT' end || ')' as descricao" +
					" from pesquisa.cotas c" +
					" inner join pesquisa.edital_pesquisa e on c.id_edital_pesquisa=e.id_edital_pesquisa" +
					" inner join pesquisa.tipo_bolsa_pesquisa t on c.id_tipo_bolsa_pesquisa=t.id_tipo_bolsa" +
					" where e.id_cota = "+cota.getId()).list();
			
			StringBuilder hql = new StringBuilder();
			hql.append(" select pt.orientador.pessoa.nome as docente, " +
					" pt.orientador.unidade.gestora.sigla as centro, " +
					" count(pt.id) as solicitacoes ");
			List<Integer> tipos = new ArrayList<Integer>();
			tipos.add(TipoBolsaPesquisa.A_DEFINIR);
			int i = 0;
			for(Object[] tipo: tiposBolsa){
				tipos.add((Integer) tipo[0]);
				hql.append(", '"+ tipo[1] +"' as tipo"+i);
				hql.append(", (select sum(c.quantidade) from CotaDocente cd join cd.cotas c where cd.docente.id = pt.orientador.id and cd.edital.cota.id = " + cota.getId() + " and c.tipoBolsa.id = "+ tipo[0] +") as cota"+i);
				hql.append(", sum(case when pt.membroProjetoDiscente is not null and pt.tipoBolsa.id = " + tipo[0] + " then 1 else 0 end) as indicacao"+i);
				i++;
			}
			hql.append(", '"+i+"' as tamanho");
			hql.append(" from PlanoTrabalho pt");
			hql.append(" where pt.cota.id = " + cota.getId());
			hql.append(" and pt.tipoBolsa.id in "+ UFRNUtils.gerarStringIn(tipos));
			hql.append(" group by pt.orientador.id, pt.orientador.pessoa.nome, pt.orientador.unidade.gestora.sigla");
			hql.append(" order by pt.orientador.unidade.gestora.sigla, pt.orientador.pessoa.nome");

			Query query = getSession().createQuery(hql.toString());

			return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca os dados utilizados no relatório de acompanhamento de distribuição de cotas
	 * de acordo com o edital de pesquisa informado.
	 * 
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection findResumoDistribuicaoEdital( EditalPesquisa edital ) throws DAOException {
		try {
			List<Object[]> tiposBolsa = getSession().createSQLQuery(
					" select distinct(c.id_tipo_bolsa_pesquisa) as id, t.descricao || ' (' || case when t.categoria = 1 then 'IC' else 'IT' end || ')' as descricao" +
					" from pesquisa.cotas c" +
					" inner join pesquisa.tipo_bolsa_pesquisa t on c.id_tipo_bolsa_pesquisa=t.id_tipo_bolsa" +
					" where c.id_edital_pesquisa = "+edital.getId()).list();
			
			StringBuilder hql = new StringBuilder();
			hql.append(" select pt.orientador.pessoa.nome as docente, " +
					" pt.orientador.unidade.gestora.sigla as centro, " +
					" count(pt.id) as solicitacoes ");
			List<Integer> tipos = new ArrayList<Integer>();
			tipos.add(TipoBolsaPesquisa.A_DEFINIR);
			int i = 0;
			for(Object[] tipo: tiposBolsa){
				tipos.add((Integer) tipo[0]);
				hql.append(", '"+ tipo[1] +"' as tipo"+i);
				hql.append(", (select sum(c.quantidade) from CotaDocente cd join cd.cotas c where cd.docente.id = pt.orientador.id and cd.edital.id = " + edital.getId() + " and c.tipoBolsa.id = "+ tipo[0] +") as cota"+i);
				hql.append(", sum(case when pt.membroProjetoDiscente is not null and pt.tipoBolsa.id = " + tipo[0] + " then 1 else 0 end) as indicacao"+i);
				i++;
			}
			hql.append(", '"+i+"' as tamanho");
			hql.append(" from PlanoTrabalho pt");
			hql.append(" where pt.edital.id = " + edital.getId());
			hql.append(" and pt.tipoBolsa.id in "+ UFRNUtils.gerarStringIn(tipos));
			hql.append(" group by pt.orientador.id, pt.orientador.pessoa.nome, pt.orientador.unidade.gestora.sigla");
			hql.append(" order by pt.orientador.unidade.gestora.sigla, pt.orientador.pessoa.nome");

			Query query = getSession().createQuery(hql.toString());

			return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Método que verifica se já existe uma distribuição de cotas para o EDITAL informado.
	 * 
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	public boolean existsDistribuicao(EditalPesquisa edital) throws DAOException {
		try {
			return (Long) getSession()
					.createQuery("select count(*) from CotaDocente cd where cd.edital.id = " + edital.getId())
					.uniqueResult() > 0;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

}
