/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criada em 25/02/2008
 * 
 * @author eric
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioProdutividade;

/**
 * Dao responsável pelas consultas específicas relacionadas a Classificação Relatório. 
 * 
 * @author Ricardo Wendell
 */
public class ClassificacaoRelatorioDao extends GenericSigaaDAO {

	/**
	 * Esse método realiza o cálculo médio do IPI por unidade.
	 * 
	 * @param unidade
	 * @param classificacaoRelatorio
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Double findIpiMedioUnidade(Unidade unidade,ClassificacaoRelatorio classificacaoRelatorio) throws DAOException{

		Criteria emissaoRelatorioCriteria = getSession().createCriteria(EmissaoRelatorio.class);
		emissaoRelatorioCriteria.add(Expression.eq("classificacaoRelatorio", classificacaoRelatorio));
		Criteria servidorCriteria = emissaoRelatorioCriteria.createCriteria("servidor");
		Criteria unidadeCriteria = servidorCriteria.createCriteria("unidade");
		unidadeCriteria.add(Expression.eq("gestora", unidade));

		Collection<EmissaoRelatorio> emissaoCollection = emissaoRelatorioCriteria.list();

		Double ipi =0.0;
		int quantDocentes = emissaoCollection.size();
		for (EmissaoRelatorio relatorio : emissaoCollection) {
			ipi+=relatorio.getIpi();
		}

		ipi/=quantDocentes;

		return ipi;
	}

	/**
	 * Buscar as classificações geradas para o relatório para distribuição de cotas de pesquisa
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ClassificacaoRelatorio> findClassificacoesRelatorioPesquisa() throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ClassificacaoRelatorio.class);
			c.add(Expression.eq("relatorioProdutividade.id", RelatorioProdutividade.RELATORIO_DISTRIBUICAO_COTAS));
			c.addOrder(  Order.desc("dataClassificacao"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Esse método tem como função realizar a busca de um relatório pelo ano de vigência.
	 * 
	 * @param classificacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ClassificacaoRelatorio> findByRelatorioAndAnoVigencia(ClassificacaoRelatorio classificacao) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ClassificacaoRelatorio.class);
			
			c.add(Expression.eq("ativo", true));

			if (classificacao.getRelatorioProdutividade() != null) {
				c.add(Expression.eq("relatorioProdutividade.id", classificacao.getRelatorioProdutividade().getId()));
			}
			if (classificacao.getAnoVigencia() != null) {
				c.add(Expression.eq("anoVigencia", classificacao.getAnoVigencia()));
			}
			c.addOrder(  Order.desc("dataClassificacao"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Esse método é responsável pela realização da consulta que servirá para a geração de um relatório em excel 
	 * para análise dos responsável no módulo de pesquisa.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> relatorioAvaliacaoPesquisa(int relatorio) throws HibernateException, DAOException {
		
		String sqlConsulta = "select cen.nome as centro, dep.nome as depto, p.nome as docente, e.ipi" +
				" from prodocente.emissao_relatorio e" +
				" inner join rh.servidor s using (id_servidor)" +
				" inner join comum.pessoa p using (id_pessoa)" +
				" inner join comum.unidade dep on (s.id_unidade=dep.id_unidade)" +
				" inner join comum.unidade cen on (dep.id_gestora=cen.id_unidade)" +
				" where id_classificacao_relatorio = " + relatorio +
				" order by cen.nome, e.ipi desc";
		
			SQLQuery q = getSession().createSQLQuery(sqlConsulta);
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
			return q.list();
	}

}