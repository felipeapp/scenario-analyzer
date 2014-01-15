/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/07/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorioMedia;

/**
 * @author Ricardo Wendell
 *
 */
public class EmissaoRelatorioMediaDao extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public Collection<EmissaoRelatorioMedia> findByClassificacao(ClassificacaoRelatorio classificacaoRelatorio) throws DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append("from EmissaoRelatorioMedia where classificacaoRelatorio.id = " + classificacaoRelatorio.getId());

		hql.append(" order by unidade.sigla");
		return getSession().createQuery(hql.toString()).list();
	}

	/**
	 * Retorna a média da unidade na classificação informada. 
	 * @throws DAOException
	 * @throws HibernateException
	 *
	 */
	public Double findMediaByClassificacaoAndUnidade(ClassificacaoRelatorio classificacaoRelatorio, Unidade unidade) throws HibernateException, DAOException {
		
		String sql = " select e.ipi_medio " +
				" from prodocente.emissao_relatorio_media e";
		
		if(unidade != null){
			sql += 	"  join pesquisa.sigla_unidade_pesquisa s" +
					"    on e.id_unidade = s.id_unidade_classificacao" +
					" where e.id_classificacao_relatorio = " + classificacaoRelatorio.getId();
			if(unidade.getGestora().getId() == UnidadeGeral.UNIDADE_DIREITO_GLOBAL)
				sql += " and s.id_unidade = " + unidade.getId();
			else if(unidade != null)
				sql += " and s.id_unidade = " + unidade.getGestora().getId();
		} else {
			sql +=  " where e.id_classificacao_relatorio = " + classificacaoRelatorio.getId() +
					" and e.id_unidade = " + UnidadeGeral.UNIDADE_DIREITO_GLOBAL;
		}

		return (Double) getSession().createSQLQuery(sql).uniqueResult();
	}

	/**
	 * Remover todas as médias calculadas para uma classificação
	 *
	 * @param classificacao
	 */
	public void removeByClassificacao(ClassificacaoRelatorio classificacao) {
		update("delete from prodocente.emissao_relatorio_media where id_classificacao_relatorio = ? ",
				new Object[] { classificacao.getId() } );
	}

	/**
	 * Calcular a média por centro dos IPIs gerados em uma determinada classificação
	 *
	 * @param classificacao
	 */
	public void calcularMedias(ClassificacaoRelatorio classificacao) {
		update("insert into prodocente.emissao_relatorio_media (id_classificacao_relatorio, id_unidade, ipi_medio, ipi_desvpad)" +
				" select er.id_classificacao_relatorio, u.id_gestora as id_unidade, cast( avg(ipi) as numeric(10,2) ) as ipi_medio, cast( stddev(ipi) as numeric(10,2) ) as ipi_desvpad" +
				" from prodocente.emissao_relatorio er , rh.servidor s, comum.unidade u" +
				" where er.id_servidor = s.id_servidor" +
				" and s.id_unidade = u.id_unidade" +
				" and er.id_classificacao_relatorio = ? " +
				" group by u.id_gestora, er.id_classificacao_relatorio",
				new Object[] { classificacao.getId() } );
	}

}
