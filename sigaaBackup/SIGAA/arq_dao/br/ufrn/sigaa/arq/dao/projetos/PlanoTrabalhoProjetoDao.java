/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '29/12/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/**
 * Dao responsável por consultas relacionadas aos planos de trabalhos de projetos
 * @author geyson
 *
 */
public class PlanoTrabalhoProjetoDao extends GenericSigaaDAO {
	
	
	/**
	 * Planos de trabalho de ações ativas onde o servidor informado é
	 * coordenador ativo
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalhoProjeto> findByServidorCoordenadorAtivo(
			Servidor servidor, Integer[] idSituacaoAcao) throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();

			hql.append("select plano.id, plano.dataFim, plano.dataInicio, disProjeto.id, pessoa.nome, " +
						"projeto.ano, projeto.titulo, tipoVinculo.descricao, situacao.descricao, " +
						"projeto.dataInicio, projeto.dataFim, projeto.ativo, dis.id " + 
						"from PlanoTrabalhoProjeto plano ");
			hql.append("join plano.projeto projeto ");
			hql.append("left join plano.discenteProjeto disProjeto ");
			hql.append("left join disProjeto.discente dis ");
			hql.append("left join dis.pessoa pessoa ");
			hql.append("left join disProjeto.tipoVinculo tipoVinculo ");
			hql.append("left join disProjeto.situacaoDiscenteProjeto situacao ");
//			hql.append("join atividade.projeto projeto ");
			hql.append("join projeto.coordenador coord ");
			hql.append("join coord.servidor servidor ");
			hql.append("where plano.ativo = trueValue() and (servidor.pessoa.id = :idPessoa) "
					+ "and (coord.ativo = trueValue()) "
					+ "and (projeto.situacaoProjeto.id in (:idSituacao)) ");
			hql.append("order by projeto.titulo, pessoa.nome ");
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idPessoa", servidor.getPessoa().getId());
			query.setParameterList("idSituacao", idSituacaoAcao);
			
			ArrayList<PlanoTrabalhoProjeto> result = new ArrayList<PlanoTrabalhoProjeto>();
			List<Object[]> lista = query.list();
			for (Object[ ]obj : lista) {

				int col = 0;
				PlanoTrabalhoProjeto pl = new PlanoTrabalhoProjeto();
				pl.setProjeto(new Projeto());
				pl.setId((Integer) obj[col++]);
				pl.setDataFim((Date) obj[col++]);
				pl.setDataInicio((Date) obj[col++]);
				pl.setDiscenteProjeto(new DiscenteProjeto((Integer) obj[col++]));
				pl.getDiscenteProjeto().getDiscente().getPessoa().setNome((String) obj[col++]);
				pl.getProjeto().setAno((Integer) obj[col++]);
				pl.getProjeto().setTitulo((String) obj[col++]);
				pl.getDiscenteProjeto().setTipoVinculo(new TipoVinculoDiscente());
				pl.getDiscenteProjeto().getTipoVinculo().setDescricao((String) obj[col++]);
				pl.getDiscenteProjeto().getSituacaoDiscenteProjeto().setDescricao(((String) obj[col++]));
				pl.getProjeto().setDataInicio((Date) obj[col++]);
				pl.getProjeto().setDataFim((Date) obj[col++]);
				pl.getProjeto().setAtivo((Boolean) obj[col++]);
				pl.getDiscenteProjeto().getDiscente().setId((Integer) obj[col++]);
				result.add(pl);
			}

			return result;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/** Retorna os planos de trabalhos dos projetos Integrados. */
	@SuppressWarnings("unchecked")
	public Collection<PlanoTrabalhoProjeto> findByPlanosAssociados(
			Servidor servidor, Integer anoInicio, Integer anoFim, Integer periodoInicio, Integer periodoFim ) throws DAOException {

		String hql = "SELECT plano.dataInicio, plano.dataFim, projeto.ensino, projeto.pesquisa, projeto.extensao, pessoa.nome" +
				     " FROM PlanoTrabalhoProjeto plano" +
				     " JOIN plano.projeto projeto " +
				     " INNER JOIN projeto.coordenador coord " +
					 " LEFT JOIN plano.discenteProjeto disProjeto " +
					 " LEFT JOIN disProjeto.discente dis " +
					 " LEFT JOIN dis.pessoa pessoa" +
					 " LEFT JOIN disProjeto.tipoVinculo tipoVinculo " +
					 " LEFT JOIN disProjeto.situacaoDiscenteProjeto situacao " +
					 " WHERE coord.servidor.id = :idServidor" +
					 " and projeto.ativo = trueValue() " +
					 " and plano.ativo = trueValue() " +
			         " and projeto.tipoProjeto.id = " + TipoProjeto.ASSOCIADO +
					 " and " + HibernateUtils.generateDateIntersection("plano.dataInicio", "plano.dataFim", ":inicial" , ":final");

		ArrayList<PlanoTrabalhoProjeto> result = new ArrayList<PlanoTrabalhoProjeto>();
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idServidor", servidor.getId());
		query.setDate("inicial", CalendarUtils.createDate(01,  periodoInicio == 1 ? 0 : 5 , anoInicio) );
		query.setDate("final", CalendarUtils.createDate(01,  periodoInicio == 1 ? 0 : 5 , anoFim) );
		List<Object[]> lista = query.list();
		for (Object[] obj : lista) {

			int col = 0;
			PlanoTrabalhoProjeto pl = new PlanoTrabalhoProjeto();
			pl.setProjeto(new Projeto());
			pl.setDataInicio((Date) obj[col++]);
			pl.setDataFim((Date) obj[col++]);
			pl.getProjeto().setEnsino((Boolean) obj[col++]);
			pl.getProjeto().setPesquisa((Boolean) obj[col++]);
			pl.getProjeto().setExtensao((Boolean) obj[col++]);
			pl.getDiscenteProjeto().getDiscente().getPessoa().setNome((String) obj[col++]);
			result.add(pl);
		}

		return result;
	}
	
}
