/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 04/03/2008
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.axis.types.IDRef;
import org.apache.jasper.tagplugins.jstl.core.If;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.RelatorioMonitor;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;
import br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Dao responsável pelas consultas sobre os relatórios parciais e finais do
 * projeto e do monitor
 * 
 * @author ilueny santos
 * 
 */
public class RelatorioProjetoMonitorDao extends GenericSigaaDAO {

	private static final long LIMITE_RESULTADOS = 1000;

	/**
	 * Retorna todos os relatórios do discente informado
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioMonitor> findByDiscenteMonitoria(
			DiscenteMonitoria discente) throws DAOException {

		Criteria c = getSession().createCriteria(RelatorioMonitor.class);
		c.add(Expression.eq("discenteMonitoria.id", discente.getId()));
		c.add(Expression.eq("ativo", true));

		return c.list();
	}

	/**
	 * Retorna o relatório do discente com o tipo informado
	 * 
	 * @param discente
	 * @param idTipoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public RelatorioMonitor findByDiscenteMonitoriaTipoRelatorio(DiscenteMonitoria discente, int idTipoRelatorio)
	throws DAOException {

		Criteria c = getSession().createCriteria(RelatorioMonitor.class);
		c.add(Expression.eq("discenteMonitoria.id", discente.getId()));
		c.add(Expression.eq("tipoRelatorio.id", idTipoRelatorio));
		c.add(Expression.eq("ativo", true));

		c.setMaxResults(1);

		return (RelatorioMonitor) c.uniqueResult();
	}


	/**
	 * Retorna todos os relatórios do projetoMonitoria informado
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioProjetoMonitoria> findByProjetoMonitoria(
			ProjetoEnsino projeto) throws DAOException {

		Criteria c = getSession().createCriteria(
				RelatorioProjetoMonitoria.class);
		c.add(Expression.eq("projetoEnsino.id", projeto.getId()));

		return c.list();
	}

	/**
	 * Retorna o relatório do projetoMonitoria com o tipo informado
	 * 
	 * @param projeto
	 * @param idTipoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public RelatorioProjetoMonitoria findByProjetoMonitoriaTipoRelatorio(
			ProjetoEnsino projeto, int idTipoRelatorio) throws DAOException {

		Criteria c = getSession().createCriteria(
				RelatorioProjetoMonitoria.class);
		c.add(Expression.eq("tipoRelatorio.id", idTipoRelatorio));

		c.add(Expression.eq("projetoEnsino.id", projeto.getId()));
		c.add(Expression.eq("ativo", true));

		c.setMaxResults(1);

		return (RelatorioProjetoMonitoria) c.uniqueResult();
	}

	/**
	 * Método para buscar os relatórios de projetos de acordo com uma série de
	 * filtros opcionais
	 * 
	 * @param titulo
	 * @param anoProjeto
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioProjetoMonitoria> filter(String tituloProjeto, Integer anoProjeto, Integer idServidorCoordenador, 
			Integer idTipoRelatorio, Integer[] idSituacoes) throws DAOException {

			String sql = "select rp.id_relatorio_projeto, rp.data_envio, rp.id_tipo_relatorio_monitoria, trm.descricao, " +
					" pm.id_projeto_monitoria, pm.id_tipo_situacao_projeto, p.id_projeto, p.ano, p.titulo, pes.nome, pm.id_projeto_monitoria" +
					" from projetos.projeto p" +
					" join monitoria.projeto_monitoria pm using ( id_projeto )" +
					" join projetos.membro_projeto mp on ( p.id_coordenador = mp.id_membro_projeto )" +
					" join comum.pessoa pes on ( mp.id_pessoa = pes.id_pessoa )";
			
			String tipoJoin = ((idTipoRelatorio == null || idTipoRelatorio < 0) && idSituacoes == null)  ? "left" : "inner";
			sql +=  tipoJoin + " join monitoria.relatorio_projeto rp on ( pm.id_projeto_monitoria = rp.id_projeto_monitoria and rp.ativo = trueValue()";
				if ( idSituacoes != null ) {
					sql += " and rp.id_status_relatorio = " + StatusRelatorio.AGUARDANDO_DISTRIBUICAO;
				}
				  
			sql += " )";
			sql += tipoJoin + " join monitoria.tipo_relatorio_monitoria trm on ( trm.id_tipo_relatorio_monitoria = rp.id_tipo_relatorio_monitoria )";
			sql += " where 1=1 ";
			
			if ( tituloProjeto != null && !tituloProjeto.equals("") )
				sql += " and p.titulo ilike '%" + tituloProjeto + "%'";
			
			if ( anoProjeto != null && anoProjeto > 0 )
				sql += " and p.ano = " + anoProjeto;
			
			if ( idServidorCoordenador != null && idServidorCoordenador > 0 )
				sql += " and mp.id_servidor = " +  idServidorCoordenador;

			if ( idTipoRelatorio != null && idTipoRelatorio > 0 )
				sql += " and rp.id_tipo_relatorio_monitoria = " +  idTipoRelatorio;

			if ( idSituacoes != null && idSituacoes.length > 0 )
				sql += " and pm.id_tipo_situacao_projeto in " +  gerarStringIn(idSituacoes);
			
			if ( idTipoRelatorio != null && idTipoRelatorio < 0 )
				sql += " and pm.id_projeto_monitoria not in (" +
						" select id_projeto_monitoria " +
						" from monitoria.relatorio_projeto " +
						" where ativo = trueValue() " +
						" and id_tipo_relatorio_monitoria = " + (idTipoRelatorio*-1) +" )";
			
			sql += " and p.ativo = trueValue()" +
				   " and mp.ativo = trueValue()" +
				   " order by p.titulo, pes.nome";
			
			List<Object[]> rs = getSession().createSQLQuery(sql).list();
			List<RelatorioProjetoMonitoria> lista = new ArrayList<RelatorioProjetoMonitoria>();
			int i = 0;
			for (Object[] rel : rs) {
				i = 0;
				RelatorioProjetoMonitoria rpm = new RelatorioProjetoMonitoria();
				rpm.setId( ((Integer) rel[i]) != null ? ((Integer) rel[i]) : 0 );
				rpm.setDataEnvio( (Date) rel[++i] );
				rpm.setTipoRelatorio(new TipoRelatorioMonitoria( ((Integer) rel[++i]) != null ? ((Integer) rel[i]) : 0 ));
				rpm.getTipoRelatorio().setDescricao( (String) rel[++i] );
				rpm.setProjetoEnsino(new ProjetoEnsino((Integer) rel[++i]));
				rpm.getProjetoEnsino().setSituacaoProjeto(new TipoSituacaoProjeto((Integer) rel[++i]));
				rpm.getProjetoEnsino().setProjeto(new Projeto((Integer) rel[++i]));
				rpm.getProjetoEnsino().getProjeto().setAno( ((Short) rel[++i]).intValue() );
				rpm.getProjetoEnsino().getProjeto().setTitulo( (String) rel[++i] );
				rpm.getProjetoEnsino().getProjeto().setCoordenador(new MembroProjeto());
				rpm.getProjetoEnsino().getProjeto().getCoordenador().setPessoa(new Pessoa());
				rpm.getProjetoEnsino().getProjeto().getCoordenador().getPessoa().setNome( (String) rel[++i] );

				lista.add(rpm);
				}

		return lista;
	}

	/**
	 * Método para buscar os relatórios de monitor de acordo com uma série de
	 * filtros opcionais
	 * 
	 * @param titulo
	 * @param anoProjeto
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<RelatorioMonitor> filterRelatorioMonitor(
			String tituloProjeto, Integer anoProjeto, Integer idDiscente,
			Integer idTipoRelatorio) throws DAOException {

		try {

			StringBuilder hqlCount = new StringBuilder();
			hqlCount
			.append(" SELECT  count(distinct rm.id) FROM RelatorioMonitor as rm "
					+ "LEFT JOIN rm.discenteMonitoria as dm "
					+ "LEFT JOIN dm.projetoEnsino p "
					+ "INNER JOIN p.projeto pj "
					+ "LEFT JOIN dm.situacaoDiscenteMonitoria sdm ");
			hqlCount.append(" WHERE rm.ativo = trueValue() ");

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
			.append(" SELECT rm.id, rm.dataEnvio, rm.dataCadastro, rm.tipoRelatorio.id, rm.tipoRelatorio.descricao, "
					+ "rm.discenteMonitoria.id, rm.discenteMonitoria.ativo, tipoVinculo.id, tipoVinculo.descricao, "
					+ "rm.discenteMonitoria.discente.discente.matricula, rm.discenteMonitoria.discente.discente.pessoa.nome, "
					+ "rm.status.id, rm.status.descricao, sdm, "
					+ "rm.coordenacaoValidouDesligamento, rm.progradValidouDesligamento, pj.ano, pj.titulo "
					+ "FROM RelatorioMonitor as rm "
					+ "LEFT JOIN rm.discenteMonitoria as dm "
					+ "JOIN dm.tipoVinculo tipoVinculo "
					+ "LEFT JOIN dm.projetoEnsino p "
					+ "INNER JOIN p.projeto pj "
					+ "LEFT JOIN dm.situacaoDiscenteMonitoria sdm ");
			hqlConsulta.append(" WHERE rm.ativo = trueValue() ");

			StringBuilder hqlFiltros = new StringBuilder();

			// Filtros para a busca
			if (tituloProjeto != null) {
				hqlFiltros.append(" AND "
						+ UFRNUtils.toAsciiUpperUTF8("pj.titulo")+ " like "
						+ UFRNUtils.toAsciiUTF8(":tituloProjeto"));
			}

			if (anoProjeto != null) {
				hqlFiltros.append(" AND pj.ano = :anoProjeto");
			}

			if (idTipoRelatorio != null) {
				hqlFiltros.append(" AND rm.tipoRelatorio = :idTipoRelatorio");
			}

			if (idDiscente != null) {
				hqlFiltros.append(" AND (dm.discente.id = :idDiscente)");
			}

			hqlCount.append(hqlFiltros.toString());
			hqlConsulta.append(hqlFiltros.toString());

			hqlConsulta.append(" ORDER BY rm.dataEnvio desc ");

			// Criando consulta
			Query queryCount = getSession().createQuery(hqlCount.toString());
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

			// Populando os valores dos filtros
			if (tituloProjeto != null) {
				queryCount.setString("tituloProjeto", tituloProjeto.toUpperCase());
				queryConsulta.setString("tituloProjeto", "%"+tituloProjeto.toUpperCase()+"%");
			}

			if (anoProjeto != null) {
				queryCount.setInteger("anoProjeto", anoProjeto);
				queryConsulta.setInteger("anoProjeto", anoProjeto);
			}

			if (idTipoRelatorio != null) {
				queryCount.setInteger("idTipoRelatorio", idTipoRelatorio);
				queryConsulta.setInteger("idTipoRelatorio", idTipoRelatorio);
			}

			if (idDiscente != null) {
				queryCount.setInteger("idDiscente", idDiscente);
				queryConsulta.setInteger("idDiscente", idDiscente);
			}

			Long total = (Long) queryCount.uniqueResult();
			if (total > LIMITE_RESULTADOS) {
				throw new LimiteResultadosException("A consulta retornou "
						+ total
						+ "	resultados. Por favor, restrinja mais a busca.");
			}

			@SuppressWarnings("unchecked")
			List<Object[]> lista = queryConsulta.list();

			ArrayList<RelatorioMonitor> result = new ArrayList<RelatorioMonitor>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				RelatorioMonitor rm = new RelatorioMonitor();
				rm.setId((Integer) colunas[col++]);
				rm.setDataEnvio((Date) colunas[col++]);
				rm.setDataCadastro((Date) colunas[col++]);

				TipoRelatorioMonitoria tipoRelatorio = new TipoRelatorioMonitoria();
				tipoRelatorio.setId((Integer) colunas[col++]);
				tipoRelatorio.setDescricao((String) colunas[col++]);
				rm.setTipoRelatorio(tipoRelatorio);

				DiscenteMonitoria discenteMonitoria = new DiscenteMonitoria();
				discenteMonitoria.setId((Integer) colunas[col++]);
				discenteMonitoria.setAtivo((Boolean) colunas[col++]);
				discenteMonitoria.getTipoVinculo().setId((Integer) colunas[col++]);
				discenteMonitoria.getTipoVinculo().setDescricao((String) colunas[col++]);

				DiscenteGraduacao d = new DiscenteGraduacao();
				d.setMatricula((Long) colunas[col++]);

				Pessoa p = new Pessoa();
				p.setNome((String) colunas[col++]);

				d.setPessoa(p);
				discenteMonitoria.setDiscente(d);
				rm.setDiscenteMonitoria(discenteMonitoria);

				StatusRelatorio s = new StatusRelatorio();
				s.setId((Integer) colunas[col++]);
				s.setDescricao((String) colunas[col++]);
				rm.setStatus(s);

				rm.getDiscenteMonitoria().setSituacaoDiscenteMonitoria(
						(SituacaoDiscenteMonitoria) colunas[col++]);
				rm.setCoordenacaoValidouDesligamento((Boolean) colunas[col++]);
				rm.setProgradValidouDesligamento((Boolean) colunas[col++]);

				ProjetoEnsino pj = new ProjetoEnsino();
				pj.setAno((Integer) colunas[col++]);
				pj.setTitulo((String) colunas[col++]);
				rm.getDiscenteMonitoria().setProjetoEnsino(pj);

				result.add(rm);

			}

			return result;

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}


	/***
	 * Retorna somente os relatórios de desligamento de monitores pendentes
	 * de validação pela Pró-Reitoria de Graduação.
	 * 
	 * @param anoProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioMonitor> findRelatorioDesligamentoParaValidacao(Integer anoProjeto) throws DAOException {
		try {
			String projecao = " rm.id, rm.dataEnvio, rm.dataCadastro, rm.coordenacaoValidouDesligamento, rm.progradValidouDesligamento, " +
			" rm.tipoRelatorio.id, rm.tipoRelatorio.descricao, " +
			" rm.discenteMonitoria.id, rm.discenteMonitoria.ativo, rm.discenteMonitoria.tipoVinculo.id, " +
			" rm.discenteMonitoria.tipoVinculo.descricao, rm.discenteMonitoria.discente.discente.matricula, " +
			" rm.discenteMonitoria.discente.discente.pessoa.nome, rm.status.id, rm.status.descricao, " +
			" rm.discenteMonitoria.projetoEnsino.projeto.ano ";

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
			.append(" SELECT " + projecao + " FROM RelatorioMonitor as rm "
					+ " JOIN rm.discenteMonitoria dm "
					+ " JOIN dm.projetoEnsino pe "
					+ " JOIN pe.projeto pj "
					+ " WHERE rm.ativo = trueValue() "
					+ " AND rm.tipoRelatorio.id = :tipoRelatorio "
					+ " AND rm.registroValidacaoCoordenacaoDesligamento is not null " //Coordenação do projeto analisou
					+ " AND rm.registroValidacaoProgradDesligamento is null "		  //pendente de análise pela Pró-Reitoria
					+ " AND rm.status.id = :statusRelatorio ");

			StringBuilder hqlFiltros = new StringBuilder();

			if (anoProjeto != null) {
				hqlFiltros.append(" AND pj.ano = :anoProjeto");
			}

			hqlConsulta.append(hqlFiltros.toString());
			hqlConsulta.append(" ORDER BY rm.dataEnvio desc ");

			// Criando consulta
			Query q = getSession().createQuery(hqlConsulta.toString());

			// Populando os valores dos filtros
			q.setInteger("tipoRelatorio", TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR);
			q.setInteger("statusRelatorio", StatusRelatorio.AGUARDANDO_AVALIACAO);
			if (anoProjeto != null) {
				q.setInteger("anoProjeto", anoProjeto);
			}
			return HibernateUtils.parseTo(q.list(), projecao, RelatorioMonitor.class, "rm");

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	/***************************************************************************
	 * Método para buscar os relatórios de projetos do servidor usado para testar
	 * se o docente pode ser inserido no projeto.
	 * 
	 * 
	 * Se o idSituacaoProjeto for nulo retorna todos os relatórios de projetos
	 * do servidor
	 * 
	 * @param titulo
	 * @param anoProjeto
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioProjetoMonitoria> findByRelatorioProjetosDocente(
			Integer idServidor, Integer[] idSituacoesProjeto,
			Integer anoProjetoInicio, Integer anoProjetoFim, Integer idTipoRelatorio, Boolean coordenador)
			throws DAOException {

		try {

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
			.append(" SELECT distinct rpm FROM RelatorioProjetoMonitoria as rpm LEFT JOIN rpm.projetoEnsino pm "
					+ "LEFT OUTER JOIN pm.equipeDocentes as equipe ");

			hqlConsulta.append(" WHERE 1 = 1 ");

			StringBuilder hqlFiltros = new StringBuilder();

			if (idServidor != null) {
				hqlFiltros.append(" AND (equipe.servidor.id = :idServidor)");
			}

			if (coordenador != null) {
				hqlFiltros.append(" AND (equipe.coordenador = :coordenador)");
			}

			if (idSituacoesProjeto != null) {
				hqlFiltros.append(" AND (pm.projeto.situacaoProjeto.id IN (:idSituacoesProjeto))");
			}

			if (anoProjetoInicio != null) {
				hqlFiltros.append(" AND (pm.projeto.ano >= :anoProjetoInicio)");
			}

			if (anoProjetoFim != null) {
				hqlFiltros.append(" AND (pm.projeto.ano <= :anoProjetoFim)");
			}

			if (idTipoRelatorio != null) {
				hqlFiltros.append(" AND (rpm.tipoRelatorio.id = :idTipoRelatorio)");
			}


			// Criando consulta
			hqlConsulta.append(hqlFiltros.toString());
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

			// Populando os valores
			if (idServidor != null) {
				queryConsulta.setInteger("idServidor", idServidor);
			}

			if (coordenador != null) {
				queryConsulta.setBoolean("coordenador", coordenador);
			}

			if (idSituacoesProjeto != null) {
				queryConsulta.setParameterList("idSituacoesProjeto", idSituacoesProjeto);
			}

			if (anoProjetoInicio != null) {
				queryConsulta.setInteger("anoProjetoInicio", anoProjetoInicio);
			}

			if (anoProjetoFim != null) {
				queryConsulta.setInteger("anoProjetoFim", anoProjetoFim);
			}

			if (idTipoRelatorio != null) {
				queryConsulta.setInteger("idTipoRelatorio", idTipoRelatorio);
			}

			return queryConsulta.list();

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	/**
	 * Método para buscar os relatórios de monitor de acordo com uma série de
	 * filtros opcionais
	 * 
	 * @param titulo
	 * @param anoProjeto
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<RelatorioMonitor> findByCoordenador(Integer idServidor,
			Integer idTipoRelatorio) throws DAOException {

		try {

			StringBuilder hqlCount = new StringBuilder();
			hqlCount
			.append(" SELECT  count(distinct rm.id) FROM RelatorioMonitor as rm "
					+ "LEFT JOIN rm.discenteMonitoria as dm "
					+ "LEFT JOIN dm.projetoEnsino p "
					+ "LEFT JOIN p.projeto pj "
					+ "LEFT JOIN p.equipeDocentes equipe "
					+ "LEFT JOIN equipe.servidor serv "
					+ "LEFT JOIN dm.situacaoDiscenteMonitoria sdm ");
			hqlCount.append(" WHERE 1 = 1 ");

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
			.append(" SELECT rm.id, rm.dataEnvio, rm.dataCadastro, rm.tipoRelatorio.id, rm.tipoRelatorio.descricao, "
					+ "rm.discenteMonitoria.id, rm.discenteMonitoria.ativo, tipoVinculo.id, tipoVinculo.descricao, "
					+ "p.id, pj.ano, pj.titulo, "
					+ "rm.discenteMonitoria.discente.discente.matricula, rm.discenteMonitoria.discente.discente.pessoa.nome, "
					+ "rm.status.id, rm.status.descricao, sdm, "
					+ "rm.coordenacaoValidouDesligamento, rm.progradValidouDesligamento, rm.status "
					+ "FROM RelatorioMonitor as rm "
					+ "LEFT JOIN rm.discenteMonitoria as dm "
					+ "INNER JOIN dm.tipoVinculo tipoVinculo "
					+ "LEFT JOIN dm.projetoEnsino p "
					+ "INNER JOIN p.projeto pj "
					+ "LEFT JOIN p.equipeDocentes equipe "
					+ "LEFT JOIN equipe.servidor serv "
					+ "LEFT JOIN dm.situacaoDiscenteMonitoria sdm ");
			hqlConsulta.append(" WHERE rm.ativo = trueValue() ");

			StringBuilder hqlFiltros = new StringBuilder();

			hqlFiltros.append(" AND (serv.id = :idServidor)");
			hqlFiltros
			.append(" AND equipe.ativo = trueValue()");

			if (idTipoRelatorio != null) {
				hqlFiltros.append(" AND rm.tipoRelatorio = :idTipoRelatorio");
			}

			hqlCount.append(hqlFiltros.toString());
			hqlConsulta.append(hqlFiltros.toString());

			hqlConsulta.append(" ORDER BY rm.dataCadastro ");

			// Criando consulta
			Query queryCount = getSession().createQuery(hqlCount.toString());
			Query queryConsulta = getSession().createQuery(
					hqlConsulta.toString());

			// Populando os valores dos filtros
			if (idServidor != null) {
				queryCount.setInteger("idServidor", idServidor);
				queryConsulta.setInteger("idServidor", idServidor);
			}

			if (idTipoRelatorio != null) {
				queryCount.setInteger("idTipoRelatorio", idTipoRelatorio);
				queryConsulta.setInteger("idTipoRelatorio", idTipoRelatorio);
			}

			Long total = (Long) queryCount.uniqueResult();
			if (total > LIMITE_RESULTADOS) {
				throw new LimiteResultadosException("A consulta retornou "
						+ total
						+ "	resultados. Por favor, restrinja mais a busca.");
			}

			@SuppressWarnings("unchecked")
			List<Object[]> lista = queryConsulta.list();

			ArrayList<RelatorioMonitor> result = new ArrayList<RelatorioMonitor>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				RelatorioMonitor rm = new RelatorioMonitor();
				rm.setId((Integer) colunas[col++]);
				rm.setDataEnvio((Date) colunas[col++]);
				rm.setDataCadastro((Date) colunas[col++]);

				TipoRelatorioMonitoria tipoRelatorio = new TipoRelatorioMonitoria();
				tipoRelatorio.setId((Integer) colunas[col++]);
				tipoRelatorio.setDescricao((String) colunas[col++]);
				rm.setTipoRelatorio(tipoRelatorio);

				DiscenteMonitoria discenteMonitoria = new DiscenteMonitoria();
				discenteMonitoria.setId((Integer) colunas[col++]);
				discenteMonitoria.setAtivo((Boolean) colunas[col++]);
				discenteMonitoria.getTipoVinculo().setId((Integer) colunas[col++]);
				discenteMonitoria.getTipoVinculo().setDescricao((String) colunas[col++]);
				ProjetoEnsino pj = new ProjetoEnsino((Integer) colunas[col++]);
				pj.setAno((Integer) colunas[col++]);
				pj.setTitulo((String) colunas[col++]);
				discenteMonitoria.setProjetoEnsino(pj);

				DiscenteGraduacao d = new DiscenteGraduacao();
				d.setMatricula((Long) colunas[col++]);

				Pessoa p = new Pessoa();
				p.setNome((String) colunas[col++]);

				d.setPessoa(p);
				discenteMonitoria.setDiscente(d);
				rm.setDiscenteMonitoria(discenteMonitoria);

				StatusRelatorio s = new StatusRelatorio();
				s.setId((Integer) colunas[col++]);
				s.setDescricao((String) colunas[col++]);
				rm.setStatus(s);

				rm.getDiscenteMonitoria().setSituacaoDiscenteMonitoria(
						(SituacaoDiscenteMonitoria) colunas[col++]);
				rm.setCoordenacaoValidouDesligamento((Boolean) colunas[col++]);
				rm.setProgradValidouDesligamento((Boolean) colunas[col++]);

				rm.setStatus((StatusRelatorio) colunas[col++]);

				result.add(rm);

			}

			return result;

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	/**
	 * Retorna o total de relatórios de desligamento de monitores
	 * pendentes de validação da PROGRAD.
	 * 
	 * @return
	 */
	public int totalRelatoriosDesligamentoPendentesValidacaoPrograd() {

		String sql = "SELECT COUNT(id_relatorio_monitor) " +
		" FROM monitoria.relatorio_monitor r " +
		" WHERE r.ativo = trueValue() " +
		" AND r.id_registro_entrada_prograd is null " +
		" AND r.id_registro_entrada_coordenacao is not null " +
		" AND r.id_status_relatorio = " + StatusRelatorio.AGUARDANDO_AVALIACAO +
		" AND r.id_tipo_relatorio_monitoria = " + TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR;

		return getJdbcTemplate().queryForInt(sql);

	}

}