/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.bolsas.dominio.Bolsista;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.HistoricoSituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.RelatorioMonitor;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;
import br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * Classe responsável por consultas específicas a discentes de monitoria.
 * 
 * @author Victor Hugo
 * 
 */
public class DiscenteMonitoriaDao extends GenericSigaaDAO {

	/** Limite de resultados da consulta. */
	private static final long LIMITE_RESULTADOS = 1000;
	
	/** Informa qual o índice acadêmico usado na*/
	private static final int INDICE_ACADEMICO_SELECAO_MONITORIA = 
		ParametroHelper.getInstance().getParametroInt(ParametrosMonitoria.INDICE_ACADEMICO_SELECAO_MONITORIA);
	
	/**
	 * Retorna todos os monitores do projeto de monitoria informado
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	public Collection<DiscenteMonitoria> findByProjetoMonitoria(ProjetoEnsino projeto) throws DAOException {
		if (projeto != null && projeto.getId() != 0) {
			Criteria c = getSession().createCriteria(DiscenteMonitoria.class);
			c.add(Restrictions.eq("ativo", Boolean.TRUE));
			c.add(Restrictions.eq("projetoEnsino.id", projeto.getId()));
			return c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} else {
			return null;
		}
	}

	/**
	 * Retorna todos os monitores do projeto de monitoria onde o servidor é
	 * coordenador
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	public Collection<DiscenteMonitoria> findByCoordenador(int idServidor)
	throws DAOException {

		if (idServidor != 0) {

			StringBuilder hqlConsulta = new StringBuilder();

			hqlConsulta
			.append(" SELECT DISTINCT "
					+ " dm.id, dm.discente.id,  dm.discente.discente.matricula, dm.discente.discente.pessoa.nome, "
					+ " tipoVinculo.id, tipoVinculo.descricao, dm.classificacao, dm.ativo, "
					+ " dm.situacaoDiscenteMonitoria.id, dm.situacaoDiscenteMonitoria.descricao, "
					+ " p.id, p.projeto.ano, p.projeto.titulo "
					+ " FROM DiscenteMonitoria dm "
					+ " INNER JOIN dm.tipoVinculo tipoVinculo "
					+ " INNER JOIN dm.projetoEnsino p "
					+ " INNER JOIN p.equipeDocentes ed "
					+ " INNER JOIN ed.servidor serv "
					+ " WHERE dm.ativo = trueValue() and serv.id = :idServidor and ed.coordenador = trueValue()"
					+ " ORDER BY p.id, dm.classificacao ");

			Query query = getSession().createQuery(hqlConsulta.toString());
			query.setInteger("idServidor", idServidor);
			List lista = query.list();

			ArrayList<DiscenteMonitoria> result = new ArrayList<DiscenteMonitoria>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				DiscenteMonitoria dm = new DiscenteMonitoria();
				dm.setId((Integer) colunas[col++]);

				DiscenteGraduacao discente = new DiscenteGraduacao();
				discente.setId((Integer) colunas[col++]);
				discente.setMatricula((Long) colunas[col++]);
				Pessoa p = new Pessoa();
				p.setNome((String) colunas[col++]);
				discente.setPessoa(p);
				dm.setDiscente(discente);

				dm.getTipoVinculo().setId((Integer) colunas[col++]);
				dm.getTipoVinculo().setDescricao((String) colunas[col++]);
				dm.setClassificacao((Integer) colunas[col++]);
				dm.setAtivo((Boolean) colunas[col++]);

				SituacaoDiscenteMonitoria situacao = new SituacaoDiscenteMonitoria();
				situacao.setId((Integer) colunas[col++]);
				situacao.setDescricao((String) colunas[col++]);
				dm.setSituacaoDiscenteMonitoria(situacao);

				ProjetoEnsino projeto = new ProjetoEnsino();
				projeto.setId((Integer) colunas[col++]);
				projeto.setAno((Integer) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);

				dm.setProjetoEnsino(projeto);
				result.add(dm);

			}

			return result;

		} else {
			return null;
		}
	}

	/**
	 * Retorna o DiscenteMonitoria ativo para o discente passado por parâmetro.
	 * Um discente pode possuir uma bolsa remunerada e uma não remunerada.
	 * 
	 * @param discente
	 *            o discente a ser pesquisado
	 * @return em caso de mais de um discenteMonitoria ativo retorna null;
	 * @throws DAOException
	 */
	public Collection<DiscenteMonitoria> findDiscenteMonitoriaAtivoByDiscente(DiscenteAdapter discente) throws DAOException {

		if (discente != null) {

			String hql = "select disc from DiscenteMonitoria disc where disc.discente.id = :idDiscente "
				+ "and disc.ativo = :ativo "
				+ "and disc.projetoEnsino.projeto.ativo = :projetoAtivo "
				+ " and  disc.situacaoDiscenteMonitoria.id = :ASSUMIU_MONITORIA ";

			Query q = getSession().createQuery(hql);
			q.setInteger("idDiscente", discente.getId());
			q.setBoolean("ativo", true);
			q.setBoolean("projetoAtivo", true);
			q.setInteger("ASSUMIU_MONITORIA", SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);

			return q.list();

		} else {
			return null;
		}

	}

	/**
	 * Retorna a quantidade de disciplinas pertinentes ao projeto de monitoria
	 * que foram cursadas pelo discente, com média de aprovação superior ou
	 * igual a 7,0.
	 * 
	 * @param projeto
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public int findQtdDisciplinasAprovadasByDiscenteProjeto(
			ProjetoEnsino projeto, Discente discente) throws DAOException {

		String hql = " SELECT COUNT(distinct matricula.id) " 
			+ " FROM MatriculaComponente matricula " 
			+ " JOIN matricula.componente componente " 
			+ " WHERE (matricula.discente.id = :idDiscente) " 
			+ " AND ( " 
			+	 " (matricula.situacaoMatricula IN (:aproveitadas)) OR "
			+	 " (matricula.situacaoMatricula IN (:pagas) AND matricula.mediaFinal >= :mediaMinima)" 
			+	")"			
			+ " AND componente.id IN (SELECT ccm.disciplina.id FROM ComponenteCurricularMonitoria ccm " 
									+ " WHERE ccm.projetoEnsino.id = :idProjeto)";

		Query query = getSession().createQuery(hql);
		query.setInteger("idDiscente", discente.getId());
		query.setParameterList("aproveitadas", SituacaoMatricula.getSituacoesAproveitadas());
		query.setParameterList("pagas", SituacaoMatricula.getSituacoesPagas());
		query.setInteger("idProjeto", projeto.getId());
		query.setDouble("mediaMinima", projeto.getEditalMonitoria().getNotaMinimaAprovacaoSelecaoMonitora());

		return ((Number) query.uniqueResult()).intValue();
	}

	/**
	 * Retorna a quantidade de disciplinas pertinentes ao projeto de monitoria e a uma prova de seleção
	 * que foram cursadas pelo discente, com média de aprovação superior ou
	 * igual a 7,0.
	 * 
	 * @param prova
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public int findQtdDisciplinasAprovadasByDiscenteProvaSelecao(
			ProvaSelecao prova, DiscenteAdapter discente) throws DAOException {

		String hql = " SELECT COUNT(DISTINCT matricula.id) " 
			+ " FROM MatriculaComponente matricula " 
			+ " JOIN matricula.componente componente "
			+ " WHERE (matricula.discente.id = :idDiscente) " 
			+ " AND ( " 
			+ 		" (matricula.situacaoMatricula IN (:aproveitadas)) OR "
			+ 		" (matricula.situacaoMatricula IN (:pagas) AND matricula.mediaFinal >= :mediaMinima)" 
			+		") "			
			+ " AND componente.id IN (SELECT ccm.disciplina.id FROM ProvaSelecaoComponenteCurricular pcc " 
									+ " JOIN pcc.provaSelecao prova " 
									+ " JOIN pcc.componenteCurricularMonitoria ccm " 
									+ " WHERE prova.id = :idProva)";

		Query query = getSession().createQuery(hql);
		query.setInteger("idDiscente", discente.getId());
		query.setParameterList("aproveitadas", SituacaoMatricula.getSituacoesAproveitadas());
		query.setParameterList("pagas", SituacaoMatricula.getSituacoesPagas());
		query.setInteger("idProva", prova.getId());
		query.setDouble("mediaMinima", prova.getProjetoEnsino().getEditalMonitoria().getNotaMinimaAprovacaoSelecaoMonitora());

		return ((Number) query.uniqueResult()).intValue();
	}

	/**
	 * Retorna todas as componentes curriculares que o aluno reprovou e que
	 * fazem parte do projeto de monitoria
	 * 
	 * @param projeto
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findDisciplinasReprovadasProjeto(
			ProjetoEnsino projeto, Discente discente) throws DAOException {

		String hql = " SELECT new ComponenteCurricular(comp.id, det.codigo, det.nome) "
			+ " FROM ComponenteCurricular comp, ComponenteCurricularMonitoria compMon, ComponenteDetalhes det "
			+ " WHERE compMon.disciplina.id = comp.id "
			+ " AND comp.detalhes.id = det.id "
			+ " AND compMon.projetoEnsino.id = :idProjeto "
			+ " AND comp.id IN ( SELECT disciplina.id "
								+ " FROM MatriculaComponente matricula "
									+ " JOIN matricula.componente disciplina "
									+ " WHERE (matricula.discente.id = :idDiscente) "
										+ " AND (" 
											+	"(matricula.situacaoMatricula.id = :idAprovado AND matricula.mediaFinal < :mediaFinal) "
											+	" OR (matricula.situacaoMatricula = :idsReprovados)" +
												")" 
							+") ";

		Query query = getSession().createQuery(hql);

		query.setInteger("idProjeto", projeto.getId());
		query.setInteger("idDiscente", discente.getId());
		query.setInteger("idAprovado", SituacaoMatricula.APROVADO.getId());
		query.setParameterList("idsReprovados", SituacaoMatricula.getSituacoesReprovadas());
		query.setDouble("mediaFinal", projeto.getEditalMonitoria().getNotaMinimaAprovacaoSelecaoMonitora());

		return query.list();

	}

	/**
	 * Retorna a média ponderada das disciplinas que o discente cursou e que
	 * fazem parte do projeto de monitoria passado por parâmetro.
	 * 
	 * @param projeto
	 * @param monitor
	 * @return
	 * @throws DAOException
	 */
	public float calcularMediaPonderadaDisciplinasDiscenteProjeto(
			ProjetoEnsino projeto, DiscenteAdapter discente) throws DAOException {

		String hql = " SELECT mat.mediaFinal, detalhes.crAula + detalhes.crLaboratorio + detalhes.crEstagio "
			+ " FROM MatriculaComponente mat, ComponenteCurricular comp, ComponenteDetalhes detalhes, ComponenteCurricularMonitoria compMon "
			+ " WHERE mat.componente.id = comp.id "
			+ " AND comp.detalhes.id = detalhes.id "
			+ " AND compMon.disciplina.id = comp.id "
			+ " AND compMon.projetoEnsino.id = :idProjeto "
			+ " AND mat.discente.id = :idDiscente ";

		Query query;
		try {
			query = getSession().createQuery(hql);
		} catch (DAOException e) {
			throw new DAOException(e.getMessage(), e);
		}

		query.setInteger("idProjeto", projeto.getId());
		query.setInteger("idDiscente", discente.getId());

		ArrayList resultados = (ArrayList) query.list();
		Double media = 0.0;
		Integer creditos = 0;
		Double soma = 0.0;
		Integer somaCreditos = 0;
		for (int i = 0; i < resultados.size(); i++) {

			Object[] linha = (Object[]) resultados.get(i);
			Double m = (Double) linha[0];
			media = m == null ? 0.0 : m;
			creditos = (Integer) linha[1];

			soma += media * creditos;
			somaCreditos += creditos;

		}

		return (float) (soma / somaCreditos);

	}

	/**
	 * Retorna a média ponderada das disciplinas que o discente cursou e que
	 * fazem parte da prova de monitoria passado por parâmetro.
	 * 
	 * @param prova
	 * @param monitor
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Object[]> calcularMediaPonderadaDisciplinasDiscenteProva(
			ProvaSelecao prova,  Collection<Integer> discentes) throws DAOException {

		String hql = " select sum(mat.mediaFinal), sum(detalhes.crAula + detalhes.crLaboratorio + detalhes.crEstagio), discGra.id, " +

		"(select indiceDiscente from IndiceAcademicoDiscente indiceDiscente " +
		" inner join indiceDiscente.discente dis where " +
		" dis.id = discGra.id and indiceDiscente.indice.id = :tipoIndiceDiscente ) " +

		" from ProvaSelecaoComponenteCurricular provaComp ,ComponenteCurricularMonitoria compMon, MatriculaComponente mat" +
		" inner join mat.componente comp " +
		" inner join comp.detalhes detalhes " +
		" inner join mat.discente discGra " +
		" where " +
		" compMon.disciplina.id = comp.id " +
		" and provaComp.provaSelecao.id = :idProva " +
		" and provaComp.componenteCurricularMonitoria.id = compMon.id " +
		" and discGra.id in "+ UFRNUtils.gerarStringIn(discentes)+"" +
		" group by discGra.id ";

		Query query;
		try {
			query = getSession().createQuery(hql);
		} catch (DAOException e) {
			throw new DAOException(e.getMessage(), e);
		}

		query.setInteger("idProva", prova.getId());
		query.setInteger("tipoIndiceDiscente", ParametroHelper.getInstance().getParametroInt(ParametrosMonitoria.INDICE_ACADEMICO_SELECAO_MONITORIA));
		ArrayList resultados = (ArrayList) query.list();
		Double media = 0.0;
		Long creditos;
		Double soma = 0.0;
		Long somaCreditos;
		IndiceAcademicoDiscente indiceAcaDiscente = new IndiceAcademicoDiscente();
		Map<Integer, Object[]> resultadoMap = new HashMap<Integer, Object[]>();
		for (int i = 0; i < resultados.size(); i++) {

			Object[] colunas = (Object[]) resultados.get(i);
			//média ponderada e indiceAcademico(IRA) do discente
			Object[] mediaDiscente = {0.0 , new Object()};
			//recupera média
			Double m = (Double) colunas[0];
			media = m == null ? 0.0 : m;
			//recupera créditos
			creditos = (Long) colunas[1];

			soma = media * creditos;
			somaCreditos = creditos;
			//média ponderada é a razão das médias pelo somatório de créditos
			//dos componentes envolvidos na prova de seleção.
			mediaDiscente[0] = (float) (soma / somaCreditos);
			//Popula indiceAcademicoDiscente
			indiceAcaDiscente = (IndiceAcademicoDiscente) colunas[3];
			mediaDiscente[1] = indiceAcaDiscente;
			//adiciona ao Map com os resultados.
			resultadoMap.put((Integer) colunas[2], mediaDiscente);
			//zera dados
			soma = 0.0;
			somaCreditos = null;

		}

		return resultadoMap;

	}

	/**
	 * Busca os discentes de monitoria de acordo com uma série de
	 * filtros opcionais
	 * 
	 * @param idProjeto
	 *            id do projeto
	 * @param idOrientador
	 *            id do servidor
	 * @param idSituacaoMonitor
	 *            id da situacao do DiscenteMonitoria
	 * @param tipoMonitoria
	 *            tipo de vinculo com a monitoria
	 * @param anoProjeto -
	 *            usado para o Prodocente
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteMonitoria> filter(String tituloProjeto,
			Integer idOrientador, Integer idDiscente,
			Integer idSituacaoMonitor, Integer tipoMonitoria,
			Integer anoProjeto, Boolean monitorAtivo, Integer idCurso, Date dataInicioEntrada, Date dataFimEntrada)
			throws DAOException {

		StringBuilder hqlCount = new StringBuilder();
		hqlCount
		.append(" SELECT  count(distinct dm.id) FROM DiscenteMonitoria dm "
				+ "INNER JOIN dm.tipoVinculo as tipoVinculo "
				+ "LEFT OUTER JOIN dm.orientacoes as orientacoes "
				+ "LEFT JOIN dm.relatoriosMonitor rel ");
		hqlCount.append(" WHERE 1 = 1 ");

		StringBuilder hqlConsulta = new StringBuilder();

		hqlConsulta
		.append(" SELECT DISTINCT "
				+ " dm.id, dm.discente.id,  dm.discente.discente.matricula, dm.discente.discente.pessoa.nome, "
				+ " tipoVinculo.id, tipoVinculo.descricao, dm.classificacao, dm.ativo, "
				+ " banco.codigo, banco.denominacao, dm.agencia, dm.conta, "
				+ " dm.situacaoDiscenteMonitoria.id, dm.situacaoDiscenteMonitoria.descricao, dm.dataInicio, dm.dataFim, "
				+ " dm.projetoEnsino.id, dm.projetoEnsino.projeto.ano, dm.projetoEnsino.projeto.titulo, "	
				+ " rel.id, status, rel.dataCadastro, rel.dataEnvio, tipoRel, rel.ativo,"
				+ " dm.discente.discente.curso.nome, dm.discente.discente.curso.modalidadeEducacao.id, "
				+ " dm.discente.discente.curso.municipio.nome"
				
				+ ", dm.operacao "

				// código para resgatar coordenador
				+ ", dm.projetoEnsino.projeto.coordenador.id, dm.projetoEnsino.projeto.coordenador.servidor.id "
				
				+ " FROM DiscenteMonitoria dm "
				+ " LEFT JOIN dm.orientacoes as orientacoes "
				+ " LEFT JOIN dm.tipoVinculo tipoVinculo "
				+ " LEFT JOIN dm.banco as banco "
				+ " LEFT JOIN dm.relatoriosMonitor rel "
				+ " LEFT JOIN rel.tipoRelatorio tipoRel "
				+ " LEFT JOIN rel.status status ");
		hqlConsulta.append(" WHERE 1 = 1 ");

		StringBuilder hqlFiltros = new StringBuilder();
		// Filtros para a busca
		if (tituloProjeto!= null) {
			hqlFiltros.append(" AND "
					+ UFRNUtils.toAsciiUpperUTF8("dm.projetoEnsino.projeto.titulo") + " like "
					+ UFRNUtils.toAsciiUTF8(":tituloProjeto"));
		}

		if (idOrientador != null) {
			hqlFiltros
			.append(" AND orientacoes.equipeDocente.servidor.id = :idOrientador ");
		}
		if (idSituacaoMonitor != null) {
			hqlFiltros
			.append(" AND dm.situacaoDiscenteMonitoria.id = :idSituacaoMonitor ");
		}

		if (tipoMonitoria != null) {
			hqlFiltros.append(" AND tipoVinculo.id = :tipoMonitoria ");
		}

		if (anoProjeto != null) {
			hqlFiltros.append(" AND dm.projetoEnsino.projeto.ano = :anoProjeto ");
		}

		if (idDiscente != null) {
			hqlFiltros.append(" AND dm.discente.id = :idDiscente ");
		}

		if (idCurso != null) {
			hqlFiltros.append(" AND dm.discente.discente.curso.id = :idCurso ");
		}

		//Busca pelo período de entrada do discente
		if ((dataInicioEntrada != null) && (dataFimEntrada != null)) {
			hqlFiltros.append( " AND cast(dm.dataInicio as date) >= :dataInicioEntrada AND cast(dm.dataInicio as date) <= :dataFimEntrada " );
		}

		if (monitorAtivo != null) {
			hqlFiltros.append(" AND dm.ativo = :monitorAtivo ");
		}

		// não mostra os excluídos ...
		hqlFiltros.append(" AND dm.situacaoDiscenteMonitoria.id != :idExcluido ");

		hqlCount.append(hqlFiltros.toString());
		hqlConsulta.append(hqlFiltros.toString());
		hqlConsulta.append(" ORDER BY dm.projetoEnsino.id , dm.classificacao ");

		// Criando consulta
		Query queryCount = getSession().createQuery(hqlCount.toString());
		Query queryConsulta = getSession().createQuery(
				hqlConsulta.toString());

		// não mostrar nem contar com os excluídos
		queryCount.setInteger("idExcluido", SituacaoDiscenteMonitoria.EXCLUIDO);
		queryConsulta.setInteger("idExcluido", SituacaoDiscenteMonitoria.EXCLUIDO);

		// Populando os valores dos filtros
		if (tituloProjeto != null) {
			queryCount.setString("tituloProjeto", tituloProjeto.toUpperCase());
			queryConsulta.setString("tituloProjeto", "%"+tituloProjeto.toUpperCase()+"%");
		}

		if (idOrientador != null) {
			queryCount.setInteger("idOrientador", idOrientador);
			queryConsulta.setInteger("idOrientador", idOrientador);
		}

		if (idDiscente != null) {
			queryCount.setInteger("idDiscente", idDiscente);
			queryConsulta.setInteger("idDiscente", idDiscente);
		}

		if (idCurso != null) {
			queryCount.setInteger("idCurso", idCurso);
			queryConsulta.setInteger("idCurso", idCurso);
		}

		if (idSituacaoMonitor != null) {
			queryCount.setInteger("idSituacaoMonitor", idSituacaoMonitor);
			queryConsulta
			.setInteger("idSituacaoMonitor", idSituacaoMonitor);
		}

		if (tipoMonitoria != null) {
			queryCount.setInteger("tipoMonitoria", tipoMonitoria);
			queryConsulta.setInteger("tipoMonitoria", tipoMonitoria);
		}

		if (anoProjeto != null) {
			queryCount.setInteger("anoProjeto", anoProjeto);
			queryConsulta.setInteger("anoProjeto", anoProjeto);
		}

		if ((dataInicioEntrada != null) && (dataFimEntrada != null)) {
			queryCount.setDate("dataInicioEntrada", dataInicioEntrada);
			queryCount.setDate("dataFimEntrada", dataFimEntrada);
			queryConsulta.setDate("dataInicioEntrada", dataInicioEntrada);
			queryConsulta.setDate("dataFimEntrada", dataFimEntrada);
		}

		if (monitorAtivo != null) {
			queryCount.setBoolean("monitorAtivo", monitorAtivo);
			queryConsulta.setBoolean("monitorAtivo", monitorAtivo);
		}

		Long total = (Long) queryCount.uniqueResult();
		if (total > LIMITE_RESULTADOS) {
			throw new LimiteResultadosException("A consulta retornou "
					+ total
					+ " resultados. Por favor, restrinja mais a busca.");
		}

		@SuppressWarnings("unchecked")
		List<Object[]> lista = queryConsulta.list();

		ArrayList<DiscenteMonitoria> result = new ArrayList<DiscenteMonitoria>();
		int idOld = 0;
		for (int a = 0; a < lista.size(); a++) {

			int col = 0;
			Object[] colunas = lista.get(a);

			int idNew = (Integer) colunas[col++];
			DiscenteMonitoria dm = new DiscenteMonitoria();
			dm.setId(idNew);

			DiscenteGraduacao discente = new DiscenteGraduacao();
			discente.setId((Integer) colunas[col++]);
			discente.setMatricula((Long) colunas[col++]);
			Pessoa p = new Pessoa();
			p.setNome((String) colunas[col++]);
			discente.setPessoa(p);
			dm.setDiscente(discente);

			if (idOld != idNew) {

				idOld = idNew;

				dm.getTipoVinculo().setId((Integer) colunas[col++]);
				dm.getTipoVinculo().setDescricao((String) colunas[col++]);
				dm.setClassificacao((Integer) colunas[col++]);
				dm.setAtivo((Boolean) colunas[col++]);

				Banco banco = new Banco();

				Integer codigo = (Integer) colunas[col++];
				if (codigo != null) {
					banco.setCodigo(codigo);
				}

				String nome = (String) colunas[col++];
				if (nome != null) {
					banco.setDenominacao(nome);
				}

				dm.setBanco(banco);
				dm.setAgencia((String) colunas[col++]);
				dm.setConta((String) colunas[col++]);

				SituacaoDiscenteMonitoria situacao = new SituacaoDiscenteMonitoria();
				situacao.setId((Integer) colunas[col++]);
				situacao.setDescricao((String) colunas[col++]);
				dm.setSituacaoDiscenteMonitoria(situacao);
				dm.setDataInicio((Date) colunas[col++]);
				dm.setDataFim((Date) colunas[col++]);

				ProjetoEnsino projeto = new ProjetoEnsino();
				projeto.setId((Integer) colunas[col++]);
				projeto.setAno((Integer) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);
				
				dm.setOperacao((String) colunas[colunas.length-3]);

				// Código adicionado para retornar coordenador do projeto. Obs.: 2 ultimos campos!
				projeto.getProjeto().setCoordenador(new MembroProjeto((Integer) colunas[colunas.length-2]));
				projeto.getProjeto().getCoordenador().setServidor(new Servidor((Integer) colunas[colunas.length-1]));
				
				dm.setProjetoEnsino(projeto);
				
				Curso curso = new Curso();
				curso.setNome((String) colunas[25]);
				curso.setModalidadeEducacao(new ModalidadeEducacao((Integer) colunas[26]));
				Municipio municipio = new Municipio();
				municipio.setNome((String) colunas[27]);
				curso.setMunicipio(municipio);
				dm.getDiscente().setCurso(curso);

				
				result.add(dm);

			}
			//relatório ativo
			if ((((Integer) colunas[19]) != null) && (((Boolean) colunas[24]) == true)) {
				RelatorioMonitor r = new RelatorioMonitor();
				r.setId((Integer) colunas[19]);
				r.setStatus((StatusRelatorio) colunas[20]);
				r.setDataCadastro((Date) colunas[21]);
				r.setDataEnvio((Date) colunas[22]);
				r.setTipoRelatorio((TipoRelatorioMonitoria) colunas[23]);
				r.setAtivo((Boolean) colunas[24]);

				result.get(result.indexOf(dm)).getRelatoriosMonitor().add(r);
			}

		}

		return result;
	}

	/**
	 * Retorna a quantidade de monitores com a situação e/ou unidade
	 * informada(s)
	 * 
	 * @param idSituacao
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 * @throws ArqException
	 */
	public long findQtdDiscentesMonitoria(Integer idSituacao,
			Integer idUnidade, Integer idCurso, Integer ano, Boolean ativo,
			Integer idTipoMonitor) throws HibernateException, DAOException {

		String hql = " SELECT COUNT(*) FROM DiscenteMonitoria dm  WHERE 1=1 ";

		if (idSituacao != null) {
			hql += "and dm.situacaoDiscenteMonitoria.id = :idSituacao ";
		}

		if (idUnidade != null) {
			hql += "and dm.projetoEnsino.projeto.unidade.id = :idUnidade ";
		}

		if (idCurso != null) {
			hql += " and dm.discente.discente.curso.id = :idCurso ";
		}

		if (ano != null) {
			hql += " and dm.projetoEnsino.projeto.ano = :ano ";
		}

		if (ativo != null) {
			hql += " and dm.ativo = :ativo ";
		}

		if (idTipoMonitor != null) {
			hql += " and dm.tipoVinculo.id = :idTipoMonitor ";
		}

		Query query = getSession().createQuery(hql);

		if (idSituacao != null) {
			query.setInteger("idSituacao", idSituacao);
		}

		if (idUnidade != null) {
			query.setInteger("idUnidade", idUnidade);
		}

		if (idCurso != null) {
			query.setInteger("idCurso", idCurso);
		}

		if (ano != null) {
			query.setInteger("ano", ano);
		}

		if (ativo != null) {
			query.setBoolean("ativo", ativo);
		}

		if (idTipoMonitor != null) {
			query.setInteger("idTipoMonitor", idTipoMonitor);
		}

		return (Long) query.uniqueResult();

	}

	/**
	 * Retorna a quantidade de bolsas ATIVAS que o discente tem.
	 * 
	 * @see Bolsista;
	 * 
	 * @param discente
	 * @return
	 * @throws ArqException
	 */
	public int findQtdBolsasByDiscente(Discente discente) throws ArqException {
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Criteria c = getCriteria(Bolsista.class);
		c.createCriteria("discente").add(Restrictions.eq("id", discente.getId()));
		c.add(Restrictions.ge("fim", hoje)); // bolsas não finalizadas
		return c.list().size();
	}

	/**
	 * Retorna todos monitores ativos (que assumiram monitoria ou que foram convocados)
	 * do projeto e, opcionalmente, da prova seletiva informada.
	 * 
	 * @param idProjeto
	 * @param tipoMonitoria
	 *            0 - TODOS OS TIPOS, 1 - NAO_REMUNERADO, 2 - BOLSISTA, 3 -
	 *            NAO_CLASSIFICADO
	 * @param naoRetornarExcluidos
	 *            retira os excluidos da lista
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteMonitoria> findAtivosByProjeto(Integer idProjeto,
			Integer tipoMonitoria, Integer idProvaSeletiva) throws DAOException {

		StringBuilder hqlConculta = new StringBuilder();
		hqlConculta.append( " select distinct dm.id, dm.dataInicio, dm.dataFim, vinculo.id, sit.id, pessoa.nome, pessoa.email " +
				" from DiscenteMonitoria dm " +
				" inner join dm.discente discenteGraduacao " +
				" inner join discenteGraduacao.discente discente" +
				" inner join discente.pessoa pessoa" +
				" inner join dm.tipoVinculo vinculo " +
				" inner join dm.situacaoDiscenteMonitoria sit " +
				" inner join dm.projetoEnsino pm " +
				" left  join dm.provaSelecao ps " +
				" where pm.id = :idProjeto and dm.ativo = trueValue() " +
		" and (sit.id in (:ASSUMIU_MONITORIA,:CONVOCADO_MONITORIA) or (dm.dataInicio <= :hoje and dm.dataFim >= :hoje)) ");

		if(idProvaSeletiva != null) {
			hqlConculta.append(" and ps.id = :idProvaSeletiva ");
		}

		if (tipoMonitoria != null) {
			hqlConculta.append(" and vinculo.id = :tipoVinculo ");
		}

		Query q = getSession().createQuery(hqlConculta.toString());
		q.setInteger("idProjeto", idProjeto);
		q.setInteger("ASSUMIU_MONITORIA", SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);
		q.setInteger("CONVOCADO_MONITORIA", SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA);
		q.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));

		if (tipoMonitoria != null) {
			q.setInteger("tipoVinculo", tipoMonitoria);
		}

		if(idProvaSeletiva != null) {
			q.setInteger("idProvaSeletiva", idProvaSeletiva);
		}

		List lista = q.list();
		ArrayList<DiscenteMonitoria> result = new ArrayList<DiscenteMonitoria>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			DiscenteMonitoria dm = new DiscenteMonitoria();
			dm.setId((Integer) colunas[col++]);
			dm.setDataInicio((Date) colunas[col++]);
			dm.setDataFim((Date) colunas[col++]);
			dm.setTipoVinculo(new TipoVinculoDiscenteMonitoria((Integer) colunas[col++]));
			dm.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria((Integer) colunas[col++]));
			dm.getDiscente().getPessoa().setNome((String) colunas[col++]);
			dm.getDiscente().getPessoa().setEmail((String) colunas[col++]);
			result.add(dm);
		}

		return result;

	}

	/**
	 * Retorna todos monitores ativos (que assumiram monitoria ou que foram convocados)
	 * do projeto informado.
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteMonitoria> findAtivosByProjeto(Integer idProjeto) throws DAOException {
		return findAtivosByProjeto(idProjeto, null, null);
	}

	/**
	 * Retorna a quantidade de monitores ativos (que assumiram monitoria ou que foram convocados) no projeto informado com os tipos de vínculo especificados.
	 */ 
	public int countMonitoresByProjeto(Integer idProjeto, Integer... tipos) throws HibernateException, DAOException {
		return countMonitoresByProjetoOuProvaOuEdital(idProjeto, null, null, tipos);
	}

	/**
	 * Retorna a quantidade de monitores ativos (que assumiram monitoria ou que foram convocados) na prova informada com os tipos de vínculo especificados.
	 */ 
	public int countMonitoresByProva(Integer idProva, Integer... tipos) throws HibernateException, DAOException {
		return countMonitoresByProjetoOuProvaOuEdital(null, idProva, null, tipos);
	}

	/**
	 * Retorna a quantidade de monitores ativos (que assumiram monitoria ou que foram convocados) no edital informado com os tipos de vínculo especificados.
	 */ 
	public int countMonitoresByEdital(Integer idEdital, Integer... tipos) throws HibernateException, DAOException {
		return countMonitoresByProjetoOuProvaOuEdital(null, null, idEdital, tipos);
	}

	
	/**
	 * Retorna a quantidade de monitores no projeto ou prova informado com os tipos de
	 * vínculo especificados.
	 * 
	 * @param idProjeto
	 * @param tipos
	 * @return total de monitores
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countMonitoresByProjetoOuProvaOuEdital(Integer idProjeto, Integer idProva, Integer idEdital,  Integer[] tipos) throws HibernateException, DAOException {

		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT COUNT( DISTINCT dm.id ) FROM DiscenteMonitoria dm ");
		hql.append(" WHERE dm.ativo = trueValue() " +
				"AND (" +
						"dm.situacaoDiscenteMonitoria.id in (:idsSituacoesAtivas) " +
						"OR (dm.dataInicio <= :hoje AND dm.dataFim >= :hoje)" +
					") ");

		if (idProjeto != null) {
			hql.append(" AND dm.projetoEnsino.id = :idProjeto ");
		}
		
		if (idProva != null) {
			hql.append(" AND dm.provaSelecao.id = :idProva ");
		}

		if (idEdital != null) {
			hql.append(" AND dm.projetoEnsino.editalMonitoria.id = :idEdital ");
		}
		
		if (tipos != null) {
			hql.append(" AND dm.tipoVinculo.id in ( :tipos ) ");
		}

		Query q = getSession().createQuery(hql.toString());
		q.setParameterList("idsSituacoesAtivas", new Integer[] {SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA});
		q.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));

		// só conta com os discentes do projeto informado
		if (idProjeto != null) {
			q.setInteger("idProjeto", idProjeto);
		}
		
		// só conta com os discentes da prova informada
		if (idProva != null) {
			q.setInteger("idProva", idProva);
		}

		// só conta com os discentes do edital
		if (idEdital != null) {
			q.setInteger("idEdital", idEdital);
		}

		
		if(tipos != null) { 
			q.setParameterList("tipos",tipos);
		}

		Long l = (Long) q.uniqueResult();
		return l.intValue();
	}

	/**
	 * Retorna a quantidade de monitores no projeto informado com os tipos de
	 * vínculo especificados. Se o id da prova for informado, todos os discentes
	 * desta prova não são contabilizados.
	 * 
	 * @param idProjeto
	 * @param tipos
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countMonitoresByProjetoExcetoDaProva(Integer idProjeto, Integer idProva, Integer... tipos) throws HibernateException, DAOException {

		StringBuffer hql = new StringBuffer();
		hql
		.append(" SELECT COUNT( DISTINCT dm.id ) FROM DiscenteMonitoria dm ");
		hql.append(" WHERE dm.projetoEnsino.id = :idProjeto ");
		hql.append(" AND dm.ativo = trueValue() " +
				"AND (" +
						"dm.situacaoDiscenteMonitoria.id in (:idsSituacoesAtivas) " +
						"OR (dm.dataInicio <= :hoje AND dm.dataFim >= :hoje)" +
					") " +
				"AND dm.provaSelecao.id != :idProva");

		if (tipos != null) {
			hql.append(" AND dm.tipoVinculo.id in ( :tipos ) ");
		}

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idProjeto", idProjeto);
		q.setParameterList("idsSituacoesAtivas", new Integer[] {SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA});
		q.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		// não conta com os discentes a prova informada
		q.setInteger("idProva", idProva);
		
		if(tipos != null) { 
			q.setParameterList("tipos",tipos);
		}

		Long l = (Long) q.uniqueResult();
		return l.intValue();
	}

	
	/**
	 * Retorna a quantidade de monitores de todos os projetos de um edital
	 * especificado
	 * 
	 * @param idEdital
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countMonitoresByEdital(int idEdital, int tipoVinculo,
			int idSituacaoDiscente, boolean monitoresAtivos)
	throws HibernateException, DAOException {

		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT COUNT(DISTINCT dm.id) FROM DiscenteMonitoria dm ");
		hql.append(" INNER JOIN dm.projetoEnsino pm ");
		hql
		.append(" INNER JOIN dm.situacaoDiscenteMonitoria situacaoDiscente ");
		hql.append(" WHERE (pm.editalMonitoria.id = :idEdital) ");

		hql.append(" AND dm.ativo = :monitoresAtivos ");
		hql.append(" AND dm.tipoVinculo.id = :tipoVinculo ");
		hql.append(" AND situacaoDiscente.id = :idSituacaoDiscente");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEdital", idEdital);
		q.setBoolean("monitoresAtivos", monitoresAtivos);
		q.setInteger("tipoVinculo", tipoVinculo);
		q.setInteger("idSituacaoDiscente", idSituacaoDiscente);

		Long l = (Long) q.uniqueResult();
		return l.intValue();

	}

	
	/**
	 * Retorna a quantidade de bolsas concedidas para todos os projetos de um edital
	 * especificado.
	 * 
	 * @param id de um Edital de Monitoria
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countBolsasConcedidasByEdital(int idEditalMonitoria) throws HibernateException, DAOException {

		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT SUM(pm.bolsasConcedidas) FROM ProjetoEnsino pm ");
		hql.append(" WHERE pm.editalMonitoria.id = :idEdital ");
		hql.append(" AND pm.ativo = trueValue() ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEdital", idEditalMonitoria);
		Long l = (Long) q.uniqueResult();
		return l.intValue();

	}
	
	
	/**
	 * Retorna todos monitores inscritos para uma prova seletiva
	 * 
	 * @param idProva
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteMonitoria> findByProvaSeletiva(int idProva)
	throws DAOException {

		if (idProva != 0) {

			StringBuilder hqlConsulta = new StringBuilder();

			hqlConsulta
			.append(" SELECT DISTINCT "
					+ " dm.id, dm.nota, dm.notaProva, dm.dataInicio, dm.dataFim, dm.ativo, pj.id, ps.id, discenteGrad.id, discente.id, discente.matricula, discente.nivel, discente.status, "
					+ " p.id, p.nome, "
					+ " tipoVinculo.id, tipoVinculo.descricao, dm.classificacao, dm.ativo, "
					+ " sdm.id, sdm.descricao, indice "
					+ " FROM DiscenteMonitoria dm "
					+ " INNER JOIN dm.provaSelecao as ps "
					+ " INNER JOIN dm.tipoVinculo as tipoVinculo "
					+ " INNER JOIN dm.projetoEnsino as pj "
					+ " INNER JOIN dm.situacaoDiscenteMonitoria as sdm "
					+ " INNER JOIN dm.discente as discenteGrad "
					+ " INNER JOIN discenteGrad.discente as discente "
					+ " INNER JOIN discente.pessoa as p "
					+ " LEFT JOIN discente.indices as indice " 
					+ " LEFT JOIN indice.indice idx "					
					+ " WHERE ps.id = :idProva and idx.id = :idxIRA "
					+ " ORDER BY dm.classificacao, dm.id ");

			Query query = getSession().createQuery(hqlConsulta.toString());
			query.setInteger("idProva", idProva);
			query.setInteger("idxIRA", INDICE_ACADEMICO_SELECAO_MONITORIA);
			List lista = query.list();

			ArrayList<DiscenteMonitoria> result = new ArrayList<DiscenteMonitoria>();
			int idOld = 0;
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				
				DiscenteMonitoria dm = new DiscenteMonitoria();
				int idNew = (Integer) colunas[col++];
				dm.setId(idNew);

				if (idOld != idNew) {
					idOld = idNew;
					dm.setNota((Double) colunas[col++]);
					dm.setNotaProva((Double) colunas[col++]);
					dm.setDataInicio((Date) colunas[col++]);					
					dm.setDataFim((Date) colunas[col++]);
					dm.setAtivo((Boolean) colunas[col++]);
					dm.setProjetoEnsino(new ProjetoEnsino((Integer)colunas[col++]));
					dm.setProvaSelecao(new ProvaSelecao((Integer)colunas[col++]));
	
					DiscenteGraduacao discenteGrad = new DiscenteGraduacao();
					discenteGrad.setId((Integer) colunas[col++]);
					
					Discente discente = new Discente();
					discente.setId((Integer) colunas[col++]);
					discente.setMatricula((Long) colunas[col++]);
					discente.setNivel((Character) colunas[col++]);
					discente.setStatus((Integer) colunas[col++]);
					Pessoa p = new Pessoa((Integer) colunas[col++]);
					p.setNome((String) colunas[col++]);
					discente.setPessoa(p);
					discenteGrad.setDiscente(discente);
					
					dm.setDiscente(discenteGrad);
	
					TipoVinculoDiscenteMonitoria vinculo = new TipoVinculoDiscenteMonitoria();
					vinculo.setId((Integer) colunas[col++]);
					vinculo.setDescricao((String) colunas[col++]);
					dm.setTipoVinculo(vinculo);
					dm.setClassificacao((Integer) colunas[col++]);
					dm.setAtivo((Boolean) colunas[col++]);
	
					SituacaoDiscenteMonitoria situacao = new SituacaoDiscenteMonitoria();
					situacao.setId((Integer) colunas[col++]);
					situacao.setDescricao((String) colunas[col++]);
					dm.setSituacaoDiscenteMonitoria(situacao);
					dm.getDiscente().getDiscente().setIndices(new ArrayList<IndiceAcademicoDiscente>());
	
					result.add(dm);
				}
				
				//Preenchendo os índices do discente. Utilizado para classificação na prova seletiva. 
				if (colunas[21] != null) {
					(result.get(result.indexOf(dm))).getDiscente().getDiscente().getIndices().add((IndiceAcademicoDiscente)colunas[21]);
				}
			}

			return result;

		} else {
			return null;
		}


	}

	/**
	 * Retorna todos os discentes que ingressaram e/ou os que saírão, em um determinado mês.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<HistoricoSituacaoDiscenteMonitoria> findByHistoricoSituacaoDiscenteMes(
			Integer mes, Integer ano, Integer idSituacao) throws DAOException {

		if (mes != null) {
			String hql = " SELECT "
				+ "h.id, "
				+ "h.data, "
				+ "sdm, "
				+ "dm.id, tipoVinculo.id, tipoVinculo.descricao, d.id, d.matricula, p.id, p.nome, "
				+ "pe.id, "
				+ "pj.ano, pj.titulo "
				+ " FROM HistoricoSituacaoDiscenteMonitoria h "
				+ " inner join  h.situacaoDiscenteMonitoria as sdm  "
				+ " inner join h.discenteMonitoria dm "
				+ " inner join dm.tipoVinculo tipoVinculo "
				+ " inner join dm.projetoEnsino pe "
				+ " inner join dm.discente grad "
				+ " inner join grad.discente d "
				+ " inner join pe.projeto pj "
				+ " inner join d.pessoa p "
				+ " WHERE (MONTH(h.data) = :mes) and (YEAR(h.data) = :ano) and (sdm.id = :idSituacao)"
				+ " ORDER BY pe";

			Query query = getSession().createQuery(hql);

			query.setInteger("idSituacao", idSituacao);
			query.setInteger("mes", mes);
			query.setInteger("ano", ano);

			List lista = query.list();

			ArrayList<HistoricoSituacaoDiscenteMonitoria> result = new ArrayList<HistoricoSituacaoDiscenteMonitoria>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				HistoricoSituacaoDiscenteMonitoria h = new HistoricoSituacaoDiscenteMonitoria();
				h.setId((Integer) colunas[col++]);
				h.setData((Date) colunas[col++]);
				h
				.setSituacaoDiscenteMonitoria((SituacaoDiscenteMonitoria) colunas[col++]);

				DiscenteMonitoria dm = new DiscenteMonitoria();
				dm.setId((Integer) colunas[col++]);
				dm.getTipoVinculo().setId((Integer) colunas[col++]);
				dm.getTipoVinculo().setDescricao((String) colunas[col++]);
				DiscenteGraduacao d = new DiscenteGraduacao();
				d.setId((Integer) colunas[col++]);
				d.setMatricula((Long) colunas[col++]);
				Pessoa p = new Pessoa();
				p.setId((Integer) colunas[col++]);
				p.setNome((String) colunas[col++]);
				d.setPessoa(p);
				dm.setDiscente(d);
				h.setDiscenteMonitoria(dm);

				ProjetoEnsino projeto = new ProjetoEnsino();
				projeto.setId((Integer) colunas[col++]);
				projeto.setAno((Integer) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);

				dm.setProjetoEnsino(projeto);
				h.setDiscenteMonitoria(dm);

				result.add(h);

			}

			return result;

		} else {
			return null;
		}

	}

	/**
	 * Retorna Lista de DiscenteMonitoria onde a pessoa informada participa ou
	 * participou de algum projeto válido (aprovado pela comissão de extensão).
	 * 
	 * @param servidor
	 * @param funcao
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteMonitoria> findByPessoa(int idPessoa)
	throws DAOException {

		try {

			String hql = "select dm, rel from DiscenteMonitoria dm "
				+ " inner join dm.projetoEnsino p "
				+ " left join dm.relatoriosMonitor rel "
				+ " inner join dm.discente grad "
				+ " inner join grad.discente d "
				+ " inner join d.pessoa pe "
				+ " inner join p.projeto pj "
				+ " inner join pj.situacaoProjeto s "
				+ " where pe.id = :idPessoa "
				+ " and pj.ativo = trueValue() and dm.dataInicio != null and dm.dataFim != null "
				+ " and dm.situacaoDiscenteMonitoria.id in (:SITUACAO_DISCENTE_VALIDA) "
				+ " order by p.id";

			Query query = getSession().createQuery(hql);
			query.setInteger("idPessoa", idPessoa);
			query.setParameterList("SITUACAO_DISCENTE_VALIDA", SituacaoDiscenteMonitoria.SITUACAO_DISCENTE_VALIDA);


			List lista = query.list();
			Integer discenteInserido = 0;
			DiscenteMonitoria dm = new DiscenteMonitoria();
			ArrayList<DiscenteMonitoria> result = new ArrayList<DiscenteMonitoria>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);


				if(discenteInserido != ((DiscenteMonitoria) colunas[col]).getId()) {
					dm = new DiscenteMonitoria();
					dm = ((DiscenteMonitoria) colunas[col++]);
					result.add(dm);
					discenteInserido = dm.getId();
				} else {
					col++;
				}

				//Não adicionar na coleção caso venha nulo da consulta.
				if(colunas[col]!=null) {
					dm.getRelatoriosMonitor().add((RelatorioMonitor) colunas[col++]);
				}



			}
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna os discentes de monitoria de acordo os parâmetros informados.
	 * 
	 * @param idEdital
	 * @param tipoMonitoria
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<DiscenteMonitoria> findByEdital(Integer idEdital, Integer tipoMonitoria, Integer idSituacaoDiscenteMonitoria) throws HibernateException, DAOException {

		String projecao = "dm.id, dm.dataCadastro, dm.dataInicio, dm.dataFim, dm.agencia, dm.conta, dm.operacao, dm.banco.codigo, " +
				" dm.discente.id, dm.discente.discente.nivel, dm.discente.discente.matricula, dm.discente.discente.pessoa.id, dm.discente.discente.pessoa.nome, " +
				" dm.discente.discente.curso.id, dm.discente.discente.curso.nome," +
				" dm.discente.discente.curso.modalidadeEducacao.id, dm.discente.discente.curso.modalidadeEducacao.descricao," +
				" dm.discente.discente.curso.municipio.nome," +
				" dm.projetoEnsino.projeto.dataFim, dm.projetoEnsino.projeto.unidade.id," +
				" dm.discente.discente.curso.modalidadeEducacao.id, dm.discente.discente.pessoa.nomeAscii";
		
		StringBuffer hql = new StringBuffer();

		hql.append(  " select " + projecao +
				" from   DiscenteMonitoria dm " +
		" where  dm.ativo = trueValue() " );

		if( idEdital != null ) {
			hql.append(" and dm.projetoEnsino.editalMonitoria.id = :idEdital ");
		}

		if( tipoMonitoria != null ) {
			hql.append(" and  dm.tipoVinculo.id = :tipoMonitoria ");
		}

		if( idSituacaoDiscenteMonitoria != null ) {
			hql.append(" and dm.situacaoDiscenteMonitoria.id = :idSituacaoDiscenteMonitoria ");
		}

		hql.append(" order by dm.discente.discente.pessoa.nomeAscii");
		Query query = getSession().createQuery(hql.toString());

		if( idEdital != null ) {
			query.setInteger("idEdital", idEdital);
		}

		if( tipoMonitoria != null ) {
			query.setInteger("tipoMonitoria", tipoMonitoria);
		}

		if( idSituacaoDiscenteMonitoria != null ) {
			query.setInteger("idSituacaoDiscenteMonitoria", idSituacaoDiscenteMonitoria);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();

		Collection<DiscenteMonitoria> result =  HibernateUtils.parseTo(lista, projecao, DiscenteMonitoria.class, "dm");
		return result;
	}


	/**
	 * Lista de todos os ids de discentes de monitoria que já solicitaram cadastro de bolsas no Sipac via Sigaa.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Integer> findDiscentesBolsasSolicitadasSipac() throws DAOException, LimiteResultadosException {
		int idTipoBolsaMonitoria = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_MONITORIA);
		String consulta = "select id_discente_projeto from projetos.sincronizacao_bolsa_sipac where id_tipo_bolsa = " + idTipoBolsaMonitoria;
		return getJdbcTemplate().query(consulta, new RowMapper() {
			public Object mapRow(ResultSet rs, int pos) throws SQLException {
				return new Integer(rs.getInt(1));
			}
		});
	}

	/***
	 * Retorna lista de discentes que foram efetivados no projeto.
	 * Todos os atualmente ativos e os já finalizados.
	 * 
	 * @return Lista de discentes de monitoria.
	 * @param idProjeto id do projeto de monitoria onde os monitores estão vinculados.
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public List<DiscenteMonitoria> findByDiscentesEfetivados(int idProjeto) throws HibernateException, DAOException{
		String projecao = " dm.id, dm.dataInicio, dm.dataFim, dm.ativo, dm.tipoVinculo.id, dm.tipoVinculo.descricao," +
		"dm.situacaoDiscenteMonitoria.id, dm.situacaoDiscenteMonitoria.descricao, dm.discente.id, dm.projetoEnsino.id," +
		"dm.discente.discente.id, dm.discente.discente.matricula, dm.discente.discente.pessoa.id, dm.discente.discente.pessoa.nome ";

		StringBuilder hql = new StringBuilder();
		hql.append("SELECT ");
		hql.append(projecao);
		hql.append(" FROM DiscenteMonitoria dm JOIN dm.projetoEnsino pm ");
		hql.append(" WHERE pm.id = :idProjeto AND dm.tipoVinculo.id in (:tiposVinculo) ");

		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idProjeto", idProjeto);
		query.setParameterList("tiposVinculo", new Integer[] {TipoVinculoDiscenteMonitoria.BOLSISTA, TipoVinculoDiscenteMonitoria.NAO_REMUNERADO});
		return (List<DiscenteMonitoria>) HibernateUtils.parseTo(query.list(), projecao, DiscenteMonitoria.class, "dm");
	}

	/**
	 * Retorna lista de discentes de monitoria ativos, no período informado mesInicio até
	 * mesFim do ano informado. A Lista de discentes contempla todos os
	 * discentes que entraram antes do período informado.
	 * 
	 * Este relatório pode ser utilizado para o cancelamento de bolsas dos
	 * monitores.
	 * 
	 * @param mesInicio
	 * @param mesFim
	 * @param ano
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */	
	public Collection<DiscenteMonitoria> findByDiscentesACancelar(int ano, Integer tipoVinculo)
			throws HibernateException, DAOException { 

		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT dm.projetoEnsino.projeto.ano, "
				+ " dm.projetoEnsino.projeto.titulo, dm.id, dm.dataInicio, dm.dataFim, "
				+ " dm.discente.discente.matricula, dm.discente.discente.pessoa.nome, "
				+ " dm.ativo, dm.discente.id, tipoVinculo.id, tipoVinculo.descricao,"
				+ " dm.discente.discente.pessoa.email, dm.situacaoDiscenteMonitoria.descricao "
				+ " FROM DiscenteMonitoria dm " 
				+ " inner join dm.tipoVinculo tipoVinculo " 
				+ " inner join dm.situacaoDiscenteMonitoria sit ");		
		hql.append(" WHERE dm.ativo = trueValue() AND sit.id = :assumiuMonitoria AND year(dm.dataInicio) = :ano ");
				
		if( tipoVinculo != null && tipoVinculo!=0 ) {
		    hql.append(" AND dm.tipoVinculo.id = :tipoVinculo ");
		}
		hql.append(" ORDER BY dm.discente.discente.pessoa.nome ");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("assumiuMonitoria", SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);
		
		if (tipoVinculo != null && tipoVinculo !=0) {
			q.setInteger("tipoVinculo", tipoVinculo);
		}
		
		@SuppressWarnings("unchecked")
		List<Object> result = q.list();
		ArrayList<DiscenteMonitoria> discentes = new ArrayList<DiscenteMonitoria>(); 
		
		if (result != null){
			for (int i = 0; i < result.size(); i++) {
				int col = 0;
				
				Object[] obj = (Object[]) result.get(i);
				DiscenteMonitoria dm = new DiscenteMonitoria();
				dm.setProjetoEnsino(new ProjetoEnsino());
				dm.getProjetoEnsino().setAno( (Integer) obj[col++] );
				dm.getProjetoEnsino().setTitulo( (String) obj[col++] );					
				dm.setId( (Integer)obj[col++] );					
				dm.setDataInicio((Date)obj[col++] );
				dm.setDataFim((Date)obj[col++] );					
				dm.setDiscente(new DiscenteGraduacao());
				dm.getDiscente().setMatricula( (Long)obj[col++] );
				dm.getDiscente().setPessoa(new Pessoa());
				dm.getDiscente().getPessoa().setNome( (String) obj[col++] );
				dm.setAtivo( (Boolean) obj[col++] );
				dm.getDiscente().setId( (Integer)obj[col++] );
				dm.getTipoVinculo().setId( (Integer)obj[col++] );
				dm.getTipoVinculo().setDescricao( (String)obj[col++] );
				dm.getDiscente().getPessoa().setEmail( (String) obj[col++] );
				dm.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria());
				dm.getSituacaoDiscenteMonitoria().setDescricao((String)obj[col++]);
				discentes.add(dm); 
			}
		}
		return discentes;

	}

}
