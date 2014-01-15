/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/10/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaAcompanhamentoCursoLato;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoAlunosCurso;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoCursosCentro;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoEntradas;

/**
 * DAO para realizar consultas a fim de gerar relatórios (entradas de alunos no ano, alunos por curso, curso por centro,
 * acompanhamento das disciplinas para o coordenador, alunos concluintes, ranking de alunos,
 * alunos matriculados e concluintes por centro num dado ano-período) no módulo lato sensu
 * 
 * @author Leonardo
 *
 */
public class RelatoriosLatoDao extends AbstractRelatorioSqlDao {

	/**
	 * Quantitativo de alunos ingressantes por ano.
	 * 
	 * @param anoInicio
	 * @param anoFim
	 * @return
	 * @throws DAOException
	 */
	
	public Map<Integer, LinhaRelatorioSinteticoEntradas> findEntradasAno(int anoInicio, int anoFim) throws DAOException{
		
		Map<Integer, LinhaRelatorioSinteticoEntradas> relatorio = new TreeMap<Integer, LinhaRelatorioSinteticoEntradas>();
		
		StringBuilder hql = new StringBuilder();
		hql.append(" select d.anoIngresso,count(d.id) ");
		hql.append(" from DiscenteLato dl, Discente d ");
		hql.append(" where d.anoIngresso between :anoInicio and :anoFim ");
		hql.append(" and d.id = dl.id ");
		hql.append(" group by d.anoIngresso");
		hql.append(" order by d.anoIngresso desc ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("anoInicio", anoInicio);
		q.setInteger("anoFim", anoFim);
		
		@SuppressWarnings("unchecked")
		List<Object[]> discentes = q.list();
		Iterator<Object[]> it = discentes.iterator();
		while (it.hasNext()) {
			int col = 0;
			Object[] colunas = it.next();

			int ano = (Integer) colunas[col++];
			LinhaRelatorioSinteticoEntradas linha = relatorio.get( ano );
			if ( linha == null ) {
				linha = new LinhaRelatorioSinteticoEntradas();
			}

			linha.setAno( ano );
			linha.setEntradas((Long)colunas[col++]);
			relatorio.put(ano, linha);
		}
		
		return relatorio;
	}

	/**
	 * Quantitativo de alunos por curso. 
	 * 
	 * @return
	 * @throws DAOException
	 */
	
	public Map<CursoLato, LinhaRelatorioSinteticoAlunosCurso> findAlunosCurso(Integer anoInicial, Integer anoFinal, Unidade centro) throws DAOException{
		
		Map<CursoLato, LinhaRelatorioSinteticoAlunosCurso> relatorio = new LinkedHashMap<CursoLato, LinhaRelatorioSinteticoAlunosCurso>();
	
		
		
		
		StringBuilder hql = new StringBuilder();
		
		
		hql.append("select cl.id, cl.unidade.gestora.nome, cl.nome,count(dl.id) from CursoLato cl " +
				"	left join cl.turmasEntrada te " +
				"	left join te.discentesLato dl " +
				"	join cl.propostaCurso prop " +
				"	join prop.situacaoProposta sit " +
				" where sit.id in " + gerarStringIn(new int[] {SituacaoProposta.ACEITA, SituacaoProposta.SUBMETIDA}));
		
		
		
		if (anoInicial != null || anoFinal != null) {
			if (anoInicial != null) 
				hql.append(" and cl.dataInicio >= '" + CalendarUtils.createDate(1, 0, anoInicial) + "'");
			if (anoFinal != null) 
				hql.append(" and cl.dataFim <= '" + CalendarUtils.createDate(31, 11, anoFinal) + "'");
		}
		
		if (isNotEmpty(centro)) {
			hql.append(" and cl.unidade.gestora.id = " + centro.getId());
		}
		
		hql.append(" group by cl.id, cl.unidade.gestora.nome, cl.nome ");
		hql.append(" order by cl.unidade.gestora.nome ");
		
//		hql.append(" select dl.turmaEntrada.cursoLato.id, dl.turmaEntrada.cursoLato.unidade.gestora.nome , dl.turmaEntrada.cursoLato.nome, count(dl.id) ");
//		hql.append(" from DiscenteLato dl ");
//		
//		if (anoInicial != null || anoFinal != null) {
//			hql.append(" where 1=1 ");
//			if (anoInicial != null) 
//				hql.append(" and turmaEntrada.cursoLato.dataInicio >= '" + CalendarUtils.createDate(1, 0, anoInicial) + "'");
//			if (anoFinal != null) 
//				hql.append(" and turmaEntrada.cursoLato.dataFim <= '" + CalendarUtils.createDate(31, 11, anoFinal) + "'");
//		}
//		
//		if (isNotEmpty(centro)) {
//			hql.append(" and dl.turmaEntrada.cursoLato.unidade.gestora.id = " + centro.getId());
//		}
		
		//hql.append(" and dl.discente.status in" + gerarStringIn(new int[] { StatusDiscente.ATIVO, StatusDiscente.CONCLUIDO }));
		
//		hql.append(" group by dl.turmaEntrada.cursoLato.id, dl.turmaEntrada.cursoLato.unidade.gestora.nome , dl.turmaEntrada.cursoLato.nome ");
//		hql.append(" order by dl.turmaEntrada.cursoLato.unidade.gestora.nome ");
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> discentes = q.list();
		Iterator<Object[]> it = discentes.iterator();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		try {
			while (it.hasNext()) {
				Object[] colunas = it.next();
				
				int idCurso = (Integer) colunas[0];
				String nomeCentro = (String) colunas[1];
				String nomeCurso = (String) colunas[2];
				CursoLato curso =  new CursoLato();
				curso.setUnidade(new Unidade());
				curso.getUnidade().setGestora(new Unidade());
				
				curso.setId(idCurso);
				curso.setNome(nomeCurso.toUpperCase());
				curso.getUnidade().getGestora().setNome(nomeCentro);
				LinhaRelatorioSinteticoAlunosCurso linha = relatorio.get( curso );
				if ( linha == null ) {
					linha = new LinhaRelatorioSinteticoAlunosCurso();
				}
				
				linha.setCurso(curso);
				linha.setNumeroAlunos((Long)colunas[3]);
				
				if (relatorio.get(curso) == null)
					relatorio.put(curso, linha);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return relatorio;
		
	}
	
	/**
	 * Quantitativo de Cursos por Centro.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Map<Unidade, LinhaRelatorioSinteticoCursosCentro> findCursosCentro(Integer anoInicial, Integer anoFinal) throws DAOException{
		
		Map<Unidade, LinhaRelatorioSinteticoCursosCentro> relatorio = new TreeMap<Unidade, LinhaRelatorioSinteticoCursosCentro>();
	
		StringBuilder hql = new StringBuilder();
		hql.append(" select cl.unidade.gestora.id, count(cl.id) ");
		hql.append(" from CursoLato cl ");
		hql.append("	join cl.propostaCurso prop ");
		hql.append("	join prop.situacaoProposta sit ");
		hql.append(" where sit.id in " + gerarStringIn(new int[] {SituacaoProposta.ACEITA, SituacaoProposta.SUBMETIDA}));
		if (anoInicial != null || anoFinal != null) {
			
			if (anoInicial != null) 
				hql.append(" and cl.dataInicio >= '" + CalendarUtils.createDate(1, 0, anoInicial) + "'");
			if (anoFinal != null) 
				hql.append(" and cl.dataFim <= '" + CalendarUtils.createDate(31, 11, anoFinal) + "'");
		}
		hql.append(" group by cl.unidade.gestora.id ");
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> cursos = q.list();
		Iterator<Object[]> it = cursos.iterator();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		try {
			while (it.hasNext()) {
				int col = 0;
				Object[] colunas = it.next();
				
				int idUnidade = (Integer) colunas[col++];
				Unidade unidade = dao.findByPrimaryKey(idUnidade, Unidade.class);
				LinhaRelatorioSinteticoCursosCentro linha = relatorio.get( unidade );
				if ( linha == null ) {
					linha = new LinhaRelatorioSinteticoCursosCentro();
				}
				
				linha.setUnidade(unidade);
				linha.setNumeroCursos((Long)colunas[col++]);
				relatorio.put(unidade, linha);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return relatorio;
		
	}
	
	
	/**
	 * Lista para acompanhamento pelo coordenador das disciplinas que possuem ou não turmas criadas para elas.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaAcompanhamentoCursoLato> findAcompanhamentoCursoLato(Curso curso) throws DAOException {
		Collection<LinhaAcompanhamentoCursoLato> acompanhamento = new ArrayList<LinhaAcompanhamentoCursoLato>();
		
		StringBuilder hqlDisc = new StringBuilder();
		hqlDisc.append(" select distinct t.id_turma, t.ano, t.periodo, t.data_inicio, t.data_fim, t.local, t.codigo as codTurma, c.id_disciplina, c.codigo, det.nome, s.descricao" +
				" from ensino.componente_curricular c " +
				" 	left outer join ensino.turma t on (c.id_disciplina = t.id_disciplina)" +
				"	left outer join ensino.situacao_turma s on (t.id_situacao_turma = s.id_situacao_turma)," +
				" lato_sensu.curso_lato cl, ensino.componente_curricular_detalhes det"+
				" where c.id_disciplina = det.id_componente" +
				" and s.id_situacao_turma not in (4,5)" +
				" and cl.id_curso = :idCursoLato " + 
				" and t.data_inicio >= cl.data_inicio "+ 
				" and t.data_fim <= cl.data_fim " +
				" and c.id_disciplina in (" +
				" select ccl.id_componente_curricular" +
				" from lato_sensu.componente_curso_lato ccl" +
				" where ccl.id_curso_lato = :idCursoLato" +
				" )" +
				" order by c.codigo");
		Query q1 = getSession().createSQLQuery( hqlDisc.toString() );
		q1.setInteger("idCursoLato", curso.getId());
		q1.setInteger("idCursoLato", curso.getId());

		@SuppressWarnings("unchecked")
		List<Object[]> lista = q1.list();
		Iterator<Object[]> it = lista.iterator();
		
		while(it.hasNext()){
			int col = 0;
			Object[] colunas = it.next();
			
			Integer idTurma = (Integer) colunas[col++];
			Integer ano = (Integer) colunas[col++];
			Integer periodo = (Integer) colunas[col++];
			Date dataInicio = (Date) colunas[col++];
			Date dataFim = (Date) colunas[col++];
			String local = (String) colunas[col++];
			String codTurma = (String) colunas[col++];
			Integer idDisciplina = (Integer) colunas[col++];
			String codigo = (String) colunas[col++];
			String nome = (String) colunas[col++];
			String situacao = (String) colunas[col++];
			
			ComponenteCurricular disciplina = new ComponenteCurricular(idDisciplina);
			disciplina.setDetalhes(new ComponenteDetalhes());
			disciplina.setCodigo(codigo);
			disciplina.getDetalhes().setNome(nome);
			
			Turma turma = null; 
			if(idTurma != null){
				turma = new Turma();
				turma.setSituacaoTurma(new SituacaoTurma());
				turma.setId(idTurma);
				turma.getSituacaoTurma().setDescricao(situacao);
				turma.setAno(ano);
				turma.setPeriodo(periodo);
				turma.setDataInicio(dataInicio);
				turma.setDataFim(dataFim);
				turma.setLocal(local);
				turma.setCodigo(codTurma);
			}
						
			LinhaAcompanhamentoCursoLato linha = new LinhaAcompanhamentoCursoLato();
			linha.setDisciplina(disciplina);
			linha.setTurma(turma);
						
			acompanhamento.add(linha);
		}
				
		return acompanhamento;
	}
	
	/**
	 * Lista de alunos concluintes do lato sensu
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findListaAlunosConcluintes() throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("SELECT * FROM " +
				" ( " +
				" SELECT CL.ID_CURSO," +
				" C.NOME||COALESCE(' - '||MOD.descricao,'') AS CURSO,D.ID_PESSOA,D.ID_DISCENTE,D.MATRICULA,P.NOME, " +
				" ( SELECT COUNT(*) " +
				" FROM ( SELECT CASE " +
				" WHEN " +
				" (SELECT COUNT(*) " +
				" FROM ENSINO.MATRICULA_COMPONENTE MC " +
				" WHERE MC.ID_COMPONENTE_CURRICULAR=CCL.ID_COMPONENTE_CURRICULAR " +
				" AND MC.ID_DISCENTE=D.ID_DISCENTE " +
				" AND MC.ID_SITUACAO_MATRICULA IN (1,2,4,8,21,22) " +
				" ) > 0 THEN 1 " +
				" ELSE 0 " +
				" END AS OK " +
				" FROM LATO_SENSU.COMPONENTE_CURSO_LATO CCL " +
				" WHERE CCL.ID_CURSO_LATO=CL.ID_CURSO " +
				" ) A WHERE OK=0 " +
				" ) AS PENDENTES " +
				" FROM LATO_SENSU.CURSO_LATO CL " +
				" INNER JOIN DISCENTE D ON D.ID_CURSO=CL.ID_CURSO " +
				" INNER JOIN COMUM.PESSOA P ON P.ID_PESSOA=D.ID_PESSOA " +
				" INNER JOIN CURSO C ON C.ID_CURSO=D.ID_CURSO " +
				" INNER JOIN COMUM.MODALIDADE_EDUCACAO MOD ON (C.ID_MODALIDADE_EDUCACAO = MOD.ID_MODALIDADE_EDUCACAO)" +
				" WHERE D.STATUS=1 " +
				" ) B WHERE PENDENTES=0 " +
				" ORDER BY CURSO,NOME ");
		
		List result = new ArrayList<HashMap<String,Object>>();

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Ranking de alunos de um curso lato sensu
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findRankingCurso(Integer idCurso) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select c.id_curso," +
				" c.nome||coalesce(' - '||mod.descricao,'') as nome_curso, d.id_discente, " +
				" d.matricula, d.status, p.nome as nome_discente, avg(m.media_final) as media " +
				" from ensino.matricula_componente m " +
				"	inner join discente d on (m.id_discente=d.id_discente)" +
				"	inner join lato_sensu.discente_lato dl on (d.id_discente=dl.id_discente)" +
				"	inner join comum.pessoa p on (d.id_pessoa=p.id_pessoa)" +
				"	inner join curso c on (d.id_curso=c.id_curso)" +
				"   inner join comum.modalidade_educacao mod on (c.id_modalidade_educacao = mod.id_modalidade_educacao)" +
				" where d.status in " + UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO, StatusDiscente.CONCLUIDO}) +
				" and d.nivel = '" + NivelEnsino.LATO + "'" +
				" and m.id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesConcluidas()));
		if(idCurso != null && idCurso > 0)
			sqlconsulta.append(" and d.id_curso = " + idCurso);
		sqlconsulta.append(" group by c.id_curso, nome_curso, d.id_discente, d.matricula, d.status, p.nome ");
		sqlconsulta.append(" order by nome_curso, media desc ");
		
		List result = new ArrayList<HashMap<String,Object>>();
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Quantitativo de alunos matriculados e concluintes por centro num determinado ano-período.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findMatriculadosEConcluintes(Integer ano, Integer periodo) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select ur.id_unidade, ur.nome, " +
				"( " +
				"	select count(*) " +
				"	from discente d " +
				"		inner join curso c2 on c2.id_curso = d.id_curso " +
				"		inner join comum.unidade uc2 on uc2.id_unidade = c2.id_unidade " +
				"	where uc2.unidade_responsavel = ur.id_unidade " +
				"	and d.nivel = 'L' " +
				"	and d.id_discente in ( " +
				"		select id_discente " +
				"		from ensino.matricula_componente mc " +
				"		where mc.ano = " + ano +
				" 		and mc.periodo =  " + periodo +
				" 		and mc.id_situacao_matricula in " + UFRNUtils.gerarStringIn(new int[]{SituacaoMatricula.MATRICULADO.getId(), 
							SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.TRANCADO.getId(), SituacaoMatricula.REPROVADO.getId(),
							SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId()})+
				"		) " +
				") as alunos_matriculados, " +
				"( " +
					"select count(*) " +
				"	from ensino.movimentacao_aluno ma" +
				"		inner join discente d on d.id_discente = ma.id_discente " +
				"		inner join curso c2 on c2.id_curso = d.id_curso " +
				"		inner join comum.unidade uc2 on uc2.id_unidade = c2.id_unidade " +
				"	where ma.ano_referencia = "+ano+" and ma.periodo_referencia = "+periodo+"" +
				"	and ma.id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.CONCLUSAO +
				"	and uc2.unidade_responsavel = ur.id_unidade " +
				"	and d.nivel = 'L' " +
				"	and d.status = " + StatusDiscente.CONCLUIDO +
				") as alunos_concluintes " +
				"from curso c " +
				"inner join comum.unidade uc on uc.id_unidade = c.id_unidade " +
				"inner join comum.unidade ur on ur.id_unidade = uc.unidade_responsavel " +
				"where c.nivel = 'L' " +
				"group by ur.id_unidade, ur.nome ");
		
		List result = new ArrayList<HashMap<String,Object>>();
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Retorna os trabalhos de fim de curso a partir de um curso
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findTrabalhoFimCursoByCurso(Collection<Curso> curso) throws DAOException {
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String,Object>>();
		String sql = "select d.id_discente, p2.nome as orientador_externo, p.nome as orientador, p1.nome as orientando, titulo, " +
					 " c.nome as curso, u.nome as unidade_servidor, u2.nome as unidade_docente_externo" +
					 " from prodocente.trabalho_fim_curso tcc" +
					 " inner join discente d on ( d.id_discente = tcc.id_orientando ) " +
					 " inner join comum.pessoa p1 on ( d.id_pessoa = p1.id_pessoa ) " +
					 " inner join curso c on ( c.id_curso = d.id_curso )" +
					 " left join rh.servidor s on ( s.id_servidor = tcc.id_servidor )" +
					 " left join comum.unidade u on ( s.id_unidade = u.id_unidade )" +
					 " left join comum.pessoa p on ( p.id_pessoa = s.id_pessoa )" +
					 " left join ensino.docente_externo de on ( de.id_docente_externo = tcc.id_docente_externo )" +
					 " left join comum.unidade u2 on ( u2.id_unidade = de.id_unidade )"+
					 " left join comum.pessoa p2 on ( p2.id_pessoa = de.id_pessoa )" +
					 " where c.id_curso in " + UFRNUtils.gerarStringIn(curso) +
					 " order by p.nome, orientando";

		Query q1 = getSession().createSQLQuery( sql );
		
		List<Object[]> lista = q1.list();
		Iterator<Object[]> it = lista.iterator();
		
		while(it.hasNext()){
			HashMap<String, Object> linha = new HashMap<String, Object>();
			int col = 0;
			Object[] colunas = it.next();
			
			linha.put("id_discente", colunas[col]);
			
			if (colunas[++col] != null){
				linha.put("docente", colunas[col]);
				col++;
			}
			else
				linha.put("docente", colunas[++col]);
			
			linha.put("orientando", colunas[++col]);
			linha.put("titulo", colunas[++col]);
			linha.put("curso", colunas[++col]);
			
			if (colunas[++col] != null)
				linha.put("unidade", colunas[col]);
			else
				linha.put("unidade", colunas[++col]);

			result.add(linha);
		}
		
		return result;
	}

	public LinhaRelatorioSinteticoAlunosCurso detalharAlunosCursoSintetico(CursoLato cl) throws DAOException {
		String hqlconcluidos =
				" select count(*) " +
				" from Discente d " +
				"   join d.curso c " +
				" where 1=1 " +
				"	and d.status in " + gerarStringIn(new int[] {StatusDiscente.CONCLUIDO}) + 
				" 	and c.id = " + cl.getId() + 
				" group by d.status " +
				" order by d.status ";
		
		Long countConcluidos = (Long) getSession().createQuery(hqlconcluidos).uniqueResult();
		
		String hqlMatriculados =
				" select count(distinct d.id) " +
				" from MatriculaComponente mc " +
				"	join mc.discente d " +
				"	join mc.situacaoMatricula sit " +
				"   join d.curso c " +
				" where 1=1 " +
				"   and sit.id not in " + gerarStringIn(SituacaoMatricula.getSituacoesInativas()) +
				"	and d.status in " + gerarStringIn(new int[] {StatusDiscente.ATIVO}) + 
				" 	and c.id = " + cl.getId() + 
				"";
		
		Long countMatriculados = (Long) getSession().createQuery(hqlMatriculados).uniqueResult();
		
		LinhaRelatorioSinteticoAlunosCurso linha = new LinhaRelatorioSinteticoAlunosCurso();
		linha.setCurso(cl);
		linha.setNumeroAlunosConcluido(countConcluidos == null ? 0 : countConcluidos);
		linha.setNumeroAlunosMatriculados(countMatriculados == null ? 0 : countMatriculados);
		
		return linha; 
	}
	
	
}
