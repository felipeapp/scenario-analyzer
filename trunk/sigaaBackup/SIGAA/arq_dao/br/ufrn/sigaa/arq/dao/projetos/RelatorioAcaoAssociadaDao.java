/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/01/2011
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.RelatorioAcaoAssociada;
import br.ufrn.sigaa.projetos.dominio.TipoParecerAvaliacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoRelatorioProjeto;

/**
 * Dao responsável por consultas relacionadas a relatórios de ações associadas.
 * @author geyson
 *
 */
public class RelatorioAcaoAssociadaDao extends GenericSigaaDAO {

	/**
	 * Lista os relatórios ativos do tipo informado da atividade informada
	 * Se o tipo não for informado, retorna todos os tipos de relatório da atividade
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioAcaoAssociada> findByProjetoTipoRelatorio(
			Integer idProjeto, Integer idTipoRelatorio) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer(
					"select r from RelatorioAcaoAssociada r  "
					+ "where r.projeto.id = :idProjeto "
					+ "and r.ativo = trueValue() ");

			if (idTipoRelatorio != null)
				hql.append(" and r.tipoRelatorio.id = :idTipoRelatorio ");

			hql.append(" order by r.projeto.id ");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idProjeto", idProjeto);

			if (idTipoRelatorio != null)
				query.setInteger("idTipoRelatorio", idTipoRelatorio);

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}



	/**
	 * Lista os relatórios pendentes das unidades informadas.
	 * Utilizado para listar todos os relatórios pendentes de autorização do chefe do 
	 * departamento.
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public List<RelatorioAcaoAssociada> findByUnidades(Collection<UnidadeGeral> unidades)	throws DAOException {

		if (ValidatorUtil.isEmpty(unidades)) {
			return null;
		}

		try {
			String hql = "select " 
				+ " r.id, "
				+ " r.ativo, "
				+ " r.dataCadastro, "
				+ " r.dataEnvio, "
				+ " r.tipoRelatorio, "
				+ " r.dataValidacaoDepartamento, "
				+ " tipoParecerDepartamento, "
				+ " r.dataValidacaoComite, "
				+ " tipoParecerComite, "
				+ " pj.id, pj.ano, pj.titulo "
				+ " from RelatorioAcaoAssociada r " 
				+ " left  join r.tipoParecerDepartamento tipoParecerDepartamento "
				+ " left  join r.tipoParecerComite tipoParecerComite "
				+ " inner join r.projeto pj "
				+ " inner join pj.unidade unidade "
				+ " where unidade.id IN (:idsUnidades) and r.ativo = trueValue() " +
				"and r.dataEnvio is not null and pj.ativo=trueValue()" 
				+ " order by pj.ano desc ";
			Query query = getSession().createQuery(hql);

			ArrayList<Integer> idsUnidades = new ArrayList<Integer>();
			for (UnidadeGeral u: unidades) {
				idsUnidades.add(u.getId());
			}

			query.setParameterList("idsUnidades", idsUnidades);	
			@SuppressWarnings("rawtypes")
			List lista = query.list();

			ArrayList<RelatorioAcaoAssociada> result = new ArrayList<RelatorioAcaoAssociada>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				RelatorioAcaoAssociada rel = new RelatorioAcaoAssociada();
				rel.setId((Integer) colunas[col++]);
				rel.setAtivo((Boolean) colunas[col++]);
				rel.setDataCadastro((Date) colunas[col++]);
				rel.setDataEnvio((Date) colunas[col++]);
				rel.setTipoRelatorio((TipoRelatorioProjeto) colunas[col++]);
				rel.setDataValidacaoDepartamento((Date) colunas[col++]);
				rel.setTipoParecerDepartamento((TipoParecerAvaliacaoProjeto) colunas[col++]);
				rel.setDataValidacaoComite((Date) colunas[col++]);
				rel.setTipoParecerComite((TipoParecerAvaliacaoProjeto) colunas[col++]);

				Projeto pj = new Projeto();
				pj.setId((Integer) colunas[col++]);
				pj.setAno((Integer) colunas[col++]);
				pj.setTitulo((String) colunas[col++]);
				rel.setProjeto(pj);

				result.add(rel);	
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	
	/**
	 * Lista os relatórios pendentes de análise pelo Comitê Integrado de Ações Associadas. 
	 * Utilizado para listar todos os relatórios que os membros do comitê podem autorizar.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<RelatorioAcaoAssociada> findAllRelatoriosPendentesComite(
			Integer[] idsTipoAtividade) throws DAOException {

		try {
			StringBuilder hql = new StringBuilder(
					"select "
					+ " r.id, "
					+ " r.ativo, "
					+ " r.dataCadastro, "
					+ " r.dataEnvio, "
					+ " r.tipoRelatorio, "
					+ " r.dataValidacaoComite, "
					+ " tipoParecerComite, "
					+ " r.dataValidacaoComite, "
					+ " tipoParecerComite, "
					+ " pj.id, pj.ano, pj.titulo "
					+ " from RelatorioAcaoAssociada r "
					+ " left  join r.tipoParecerDepartamento tipoParecerDepartamento "
					+ " left  join r.tipoParecerComite tipoParecerComite "
					+ " inner join r.projeto pj "
					+ " where (r.ativo = trueValue())  ");
			hql.append(" order by pj.ano desc ");
			Query query = getSession().createQuery(hql.toString());

			@SuppressWarnings("rawtypes")
			List lista = query.list();

			ArrayList<RelatorioAcaoAssociada> result = new ArrayList<RelatorioAcaoAssociada>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				RelatorioAcaoAssociada rel = new RelatorioAcaoAssociada();
				rel.setId((Integer) colunas[col++]);
				rel.setAtivo((Boolean) colunas[col++]);
				rel.setDataCadastro((Date) colunas[col++]);
				rel.setDataEnvio((Date) colunas[col++]);
				rel.setTipoRelatorio((TipoRelatorioProjeto) colunas[col++]);
				rel.setDataValidacaoDepartamento((Date) colunas[col++]);
				rel.setTipoParecerDepartamento((TipoParecerAvaliacaoProjeto) colunas[col++]);
				rel.setDataValidacaoComite((Date) colunas[col++]);
				rel.setTipoParecerComite((TipoParecerAvaliacaoProjeto) colunas[col++]);

				Projeto pj = new Projeto();
				pj.setId((Integer) colunas[col++]);
				pj.setAno((Integer) colunas[col++]);
				pj.setTitulo((String) colunas[col++]);
				rel.setProjeto(pj);

				result.add(rel);
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	
	/**
	 * Utilizado na busca de relatórios.
	 * 
	 * @param titulo
	 * @param idTipoRelatorio
	 * @param ano
	 * @param inicio
	 * @param fim
	 * @param inicioConclusao
	 * @param fimConclusao
	 * @param idEdital
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public List<RelatorioAcaoAssociada> buscaRelatorios(String titulo,
			Integer idTipoRelatorio,  Integer ano, Date inicio, Date fim, 
			Date inicioConclusao, Date fimConclusao,  Integer idEdital, 
			Integer idServidor) throws DAOException {

		try {

			StringBuffer hql = new StringBuffer();

			hql.append("SELECT " 
							+ " DISTINCT r.id, "
							+ " r.ativo, "
							+ " r.dataCadastro, "
							+ " r.dataEnvio, "
							+ " r.tipoRelatorio, "
							+ " r.dataValidacaoDepartamento, "
							+ " tipoParecerDepartamento, "
							+ " r.dataValidacaoComite, "
							+ " tipoParecerComite,"
							+ " pj.id, pj.ano, " 
							+ " pj.titulo "
							+ " FROM RelatorioAcaoAssociada r "
							+ " LEFT JOIN r.tipoParecerDepartamento tipoParecerDepartamento "
							+ " LEFT JOIN r.tipoParecerComite tipoParecerComite "
							+ " JOIN r.projeto pj "
							+ " JOIN pj.equipe equipe "
							+ " JOIN equipe.servidor servidor "
							+ " LEFT JOIN pj.edital edital "							
							+ " where r.ativo = trueValue() and r.dataEnvio is not null ");
			
			if (idServidor != null) {
				hql.append(" AND servidor.id = :idServidor ");
			}
			
			if (idEdital != null) {
				hql.append(" AND edital.id = :idEdital ");
			}			

			if (titulo != null) {
				hql.append("and upper(pj.titulo) like :titulo ");
			}

			if (idTipoRelatorio != null) {
				hql.append(" and r.tipoRelatorio.id = :idTipoRelatorio ");
			}

			if (ano != null) {
				hql.append("and pj.ano = :ano ");
			}

			if ((inicio != null) && (fim != null)) {
				hql.append("and ((r.dataEnvio >= :inicio) and (r.dataEnvio <= :fim))");
			}
			
			if ((inicioConclusao != null) && (fimConclusao != null)) {
				hql.append(" and pj.dataFim >= :inicioConclusao AND pj.dataFim <= :fimConclusao  ");
			}

			hql.append("order by  pj.ano DESC ");
			Query query = getSession().createQuery(hql.toString());

			if (titulo != null) {
				query.setString("titulo", "%" + titulo.toUpperCase() + "%");
			}

			if (idTipoRelatorio != null) {
				query.setInteger("idTipoRelatorio", idTipoRelatorio);
			}

			if (ano != null) {
				query.setInteger("ano", ano);
			}
			
			if (idServidor != null) {
				query.setInteger("idServidor", idServidor);
			}

			if ((inicio != null) && (fim != null)) {
				query.setDate("inicio", inicio);
				query.setDate("fim", fim);
			}
			
			if ((inicioConclusao != null) && (fimConclusao != null)) {
				query.setDate("inicioConclusao", inicioConclusao);
				query.setDate("fimConclusao", fimConclusao);
			}
			
			if (idEdital != null) {				
				query.setInteger("idEdital", idEdital);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

			ArrayList<RelatorioAcaoAssociada> result = new ArrayList<RelatorioAcaoAssociada>();
			for (int a = 0; a < lista.size(); a++) {
				
				int col = 0;
				Object[] colunas = lista.get(a);

				RelatorioAcaoAssociada rel = new RelatorioAcaoAssociada();
				rel.setId((Integer) colunas[col++]);
				rel.setAtivo((Boolean) colunas[col++]);
				rel.setDataCadastro((Date) colunas[col++]);
				rel.setDataEnvio((Date) colunas[col++]);
				rel.setTipoRelatorio((TipoRelatorioProjeto) colunas[col++]);
				rel.setDataValidacaoDepartamento((Date) colunas[col++]);
				rel.setTipoParecerDepartamento((TipoParecerAvaliacaoProjeto) colunas[col++]);
				rel.setDataValidacaoComite((Date) colunas[col++]);
				rel.setTipoParecerComite((TipoParecerAvaliacaoProjeto) colunas[col++]);

				Projeto projeto = new Projeto();
				projeto.setId((Integer) colunas[col++]);
				projeto.setAno((Integer) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);
				rel.setProjeto(projeto);

				result.add(rel);
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}


}
