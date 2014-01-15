/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 31/05/2007
 * 
 */
package br.ufrn.sigaa.arq.dao.graduacao;


import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatriculaEquivalentes;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.LinhaRelatorioAlunosAtivosCurso;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.LinhaRelatorioAlunosPendenteMatricula;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.LinhaRelatorioMatriculasOnlineNaoAtendidas;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.LinhaRelatorioReprovacoesDisciplinas;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.LinhaRelatorioTrancamentos;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.LinhaRelatorioTurmasConsolidadas;

/**
 * Dao utilizado para efetuar as consultas que geram os relatórios do 
 * coordenador de curso de graduação.
 * @author leonardo
 *
 */
public class RelatoriosCoordenadorDao extends GenericSigaaDAO {

	/**
	 * Consulta que constrói o relatório de trancamentos realizados por alunos de um determinado curso
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Map<ComponenteCurricular, LinhaRelatorioTrancamentos> findTrancamentosByCurso(Curso curso, int ano, int periodo) throws DAOException {

		Map<ComponenteCurricular, LinhaRelatorioTrancamentos> relatorio = new LinkedHashMap<ComponenteCurricular, LinhaRelatorioTrancamentos>();

		StringBuilder hql = new StringBuilder();
		hql.append(" select mc.turma.disciplina.id, mc.turma.disciplina.detalhes.codigo, mc.turma.disciplina.detalhes.nome, mc.discente.id, mc.discente.matricula, mc.discente.pessoa.nome, dg.matrizCurricular.id");
		hql.append(" from MatriculaComponente mc, DiscenteGraduacao dg");
		hql.append(" where mc.discente.curso.id = :idCurso");
		hql.append(" and dg.discente = mc.discente ");
		hql.append(" and mc.situacaoMatricula.id = :idTrancado");
		hql.append(" and mc.ano = :ano");
		hql.append(" and mc.periodo = :periodo");
		hql.append(" order by mc.turma.disciplina.detalhes.nome asc");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCurso", curso.getId());
		q.setInteger("idTrancado", SituacaoMatricula.TRANCADO.getId());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);

		@SuppressWarnings("unchecked")
		List<Object[]> trancamentos = q.list();
		
		Iterator<Object[]> it = trancamentos.iterator();

		while(it.hasNext()){
			int col = 0;
			Object[] colunas = it.next();

			int id = (Integer) colunas[col++];
			String codigo = (String) colunas[col++];
			String nome = (String) colunas[col++];
			ComponenteCurricular disciplina = new ComponenteCurricular(id, codigo, nome);

			LinhaRelatorioTrancamentos linha = relatorio.get(disciplina);
			if(linha == null){
				linha = new LinhaRelatorioTrancamentos();
			}

			linha.setDisciplina(disciplina);
			DiscenteGraduacao discente = new DiscenteGraduacao((Integer) colunas[col++], (Long) colunas[col++], (String) colunas[col++]);
			MatrizCurricular matrizCurricular = new MatrizCurricular((Integer) colunas[col++]);
			discente.setMatrizCurricular(refresh(matrizCurricular));
			linha.getDiscentes().put(discente.getNome(), discente);

			relatorio.put(disciplina, linha);
		}
		return relatorio;
	}

	/**
	 * Consulta que constrói o relatório de alunos ativos de um curso passado como argumento
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, LinhaRelatorioAlunosAtivosCurso> findAlunosAtivosCurso(Curso curso) throws DAOException {

		Map<Integer, LinhaRelatorioAlunosAtivosCurso> relatorio = new TreeMap<Integer, LinhaRelatorioAlunosAtivosCurso>();

		int[] statusAtivos = new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO};

		StringBuilder hql = new StringBuilder();
		hql.append(" select d.anoIngresso, d.pessoa.nome, d.matricula");
		hql.append(" from Discente d");
		hql.append(" where d.curso.id = :idCurso");
		hql.append(" and d.status in " + gerarStringIn(statusAtivos));
		hql.append(" group by d.anoIngresso, d.pessoa.nome, d.matricula");
		hql.append(" order by d.pessoa.nome asc");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCurso", curso.getId());

		@SuppressWarnings("unchecked")
		List<Object[]> discentes = q.list();
		Iterator<Object[]> it = discentes.iterator();

		while(it.hasNext()){
			int col = 0;
			Object[] colunas = it.next();

			int ano = (Integer) colunas[col++];

			LinhaRelatorioAlunosAtivosCurso linha = relatorio.get(ano);
			if(linha == null){
				linha = new LinhaRelatorioAlunosAtivosCurso();
			}

			linha.setAno(ano);
			linha.getDiscentes().put((String) colunas[col++], (Long) colunas[col++]);
			linha.setTotal(linha.getDiscentes().size());

			relatorio.put(ano, linha);
		}
		return relatorio;
	}

	/**
	 * Consulta que constrói o relatório de alunos de um curso que ainda não efetuaram matrícula ou pré-matrícula,
	 * exceto alunos que tenham entrada posterior ao ano/período atual (evitando assim, discentes cadastrados 
	 * ainda não matriculados para o segundo período).
	 * @param curso
	 * @param periodoIngresso 
	 * @param anoIngresso 
	 * @return
	 * @throws DAOException
	 */
	public Map<String, LinhaRelatorioAlunosPendenteMatricula> findAlunosPendenteMatricula(Curso curso, int ano, int periodo, Integer anoIngresso, Integer periodoIngresso) throws DAOException {

		Map<String, LinhaRelatorioAlunosPendenteMatricula> relatorio = new TreeMap<String, LinhaRelatorioAlunosPendenteMatricula>();

		StringBuilder hql = new StringBuilder();
		hql.append(" select d.pessoa.nome, d.pessoa.email, d.matricula, d.anoIngresso, d.periodoIngresso, count(d.id)");
		hql.append(" from Discente d");
		hql.append(" where d.curso.id = :idCurso");
		if (!isEmpty(anoIngresso) && !isEmpty(periodoIngresso))
			hql.append(" and d.anoIngresso = :anoIngresso and d.periodoIngresso = :periodoIngresso ");
		else
			hql.append(" and (d.anoIngresso*10 + d.periodoIngresso) <= (:ano * 10 + :periodo)");
		hql.append(" and d.status in (:ativo, :cadastrado)");
		// exceto os matriculados no período
		hql.append(" and d.id not in (");
		hql.append(" select mc.discente.id from MatriculaComponente mc");
		hql.append(" where mc.ano = :ano");
		hql.append(" and mc.periodo = :periodo");
		hql.append(" and mc.situacaoMatricula not in (:situacaoMatricula)");
		hql.append(" and mc.discente.curso.id = :idCurso )");
		// exceto os que solicitaram matrícula no período
		hql.append(" and d.id not in (");
		hql.append(" select sm.discente.id from SolicitacaoMatricula sm");
		hql.append(" where sm.ano = :ano");
		hql.append(" and sm.periodo = :periodo");
		hql.append(" and sm.discente.curso.id = :idCurso ");
		hql.append(" and sm.status <> :idSolicitacaoExcluida )");
		// exceto os trancados no período
		hql.append(" and d.id not in (");
		hql.append(" select mov.discente.id from MovimentacaoAluno mov");
		hql.append(" where mov.anoReferencia = :ano");
		hql.append(" and mov.periodoReferencia = :periodo");
		hql.append(" and mov.discente.curso.id = :idCurso");
		hql.append(" and mov.tipoMovimentacaoAluno.id = :idMovimentacaoAluno )");
		hql.append(" group by d.pessoa.nome,d.pessoa.email, d.matricula, d.anoIngresso, d.periodoIngresso");
		hql.append(" order by d.pessoa.nome asc");

		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idCurso", curso.getId());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("ativo", StatusDiscente.ATIVO);
		q.setInteger("cadastrado", StatusDiscente.CADASTRADO);
		q.setInteger("idMovimentacaoAluno", TipoMovimentacaoAluno.TRANCAMENTO);
		q.setParameterList("situacaoMatricula", SituacaoMatricula.getSituacoesAproveitadas());
		q.setInteger("idSolicitacaoExcluida", SolicitacaoMatricula.EXCLUIDA);
		if (!isEmpty(anoIngresso) && !isEmpty(periodoIngresso)) {
			q.setInteger("anoIngresso", anoIngresso);
			q.setInteger("periodoIngresso", periodoIngresso);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> discentes = q.list();
		Iterator<Object[]> it = discentes.iterator();

		while(it.hasNext()){
			int col = 0;
			Object[] colunas = it.next();

			String nome = (String) colunas[col++];
			String email = (String) colunas[col++];
			Long matricula = (Long) colunas[col++];
			Integer anoDeIngresso = (Integer) colunas[col++];
			Integer periodoDeIngresso = (Integer) colunas[col++];

			LinhaRelatorioAlunosPendenteMatricula linha = relatorio.get(nome);
			if(linha == null){
				linha = new LinhaRelatorioAlunosPendenteMatricula();
			}

			linha.setNome(nome);
			linha.setEmail(email);
			linha.setMatricula(matricula);
			linha.setAno(anoDeIngresso);
			linha.setPeriodo(periodoDeIngresso);


			relatorio.put(nome, linha);
		}
		return relatorio;
	}

	/**
	 * Consulta que constrói o relatório de pré-matrículas ainda não atendidas pelo coordenador de um curso.
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, LinhaRelatorioMatriculasOnlineNaoAtendidas> findMatriculasOnlineNaoAtendidas(Curso curso) throws DAOException {

		Map<Integer, LinhaRelatorioMatriculasOnlineNaoAtendidas> relatorio = new TreeMap<Integer, LinhaRelatorioMatriculasOnlineNaoAtendidas>();

		StringBuilder hql = new StringBuilder();
		hql.append(" select sm.discente.anoIngresso, sm.discente.pessoa.nome, sm.discente.matricula, count(sm.id)");
		hql.append(" from SolicitacaoMatricula sm");
		hql.append(" where sm.discente.curso.id = :idCurso");
		hql.append(" and sm.anulado=falseValue() and sm.status in " + gerarStringIn(SolicitacaoMatricula.getStatusSolicitacoesPendentes()));
		hql.append(" group by sm.discente.anoIngresso, sm.discente.pessoa.nome, sm.discente.matricula");
		hql.append(" order by sm.discente.anoIngresso, sm.discente.pessoa.nome asc");
		
		StringBuilder hqlCount = new StringBuilder();
		hqlCount.append(" select sm.discente.anoIngresso, count(*)");
		hqlCount.append(" from SolicitacaoMatricula sm");
		hqlCount.append(" where sm.discente.curso.id = :idCurso");
		hqlCount.append(" and sm.anulado=falseValue() and sm.status in " + gerarStringIn(SolicitacaoMatricula.getStatusSolicitacoesPendentes()));
		hqlCount.append(" group by sm.discente.anoIngresso");
		hqlCount.append(" order by sm.discente.anoIngresso asc");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCurso", curso.getId());
		
		Query qCount = getSession().createQuery(hqlCount.toString());
		qCount.setInteger("idCurso", curso.getId());

		@SuppressWarnings("unchecked")
		List<Object[]> discentes = q.list();
		Iterator<Object[]> it = discentes.iterator();
		
		@SuppressWarnings("unchecked")
		List<Object[]> count = qCount.list();
		Iterator<Object[]> itCount = count.iterator();
		Object[] colunasCount = null;

		while(it.hasNext()){
			int col = 0;
			int colCount = 0;
			Object[] colunas = it.next();

			Integer ano = (Integer) colunas[col++];

			LinhaRelatorioMatriculasOnlineNaoAtendidas linha = relatorio.get(ano);
			if(linha == null){
			    	colunasCount = itCount.next();
				linha = new LinhaRelatorioMatriculasOnlineNaoAtendidas();
			}

			linha.setAno(ano);
			linha.getDiscentes().put((String) colunas[col++], (Long) colunas[col++]);
			linha.setTotal((Long) colunasCount[++colCount]);

			relatorio.put(ano, linha);
		}
		return relatorio;
	}


	/**
	 * Consulta que constrói o relatório de turmas consolidadas.
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Map<String, LinhaRelatorioTurmasConsolidadas> findTurmasConsolidadas(int ano, int periodo, Curso curso, Unidade departamento) throws DAOException {

		Map<String, LinhaRelatorioTurmasConsolidadas> relatorio = new TreeMap<String, LinhaRelatorioTurmasConsolidadas>();

		StringBuilder hql = new StringBuilder();

		hql.append(" select " + /*t.ano, t.periodo,*/ "t.disciplina.codigo, t.disciplina.detalhes.nome, t.situacaoTurma.descricao, count(distinct t.id)");
		if(curso != null && departamento == null)
			hql.append(" from Turma t, CurriculoComponente cc");
		else
			hql.append(" from Turma t, CurriculoComponente cc");
		hql.append(" where t.situacaoTurma.id = :idConsolidada");
		hql.append(" and t.ano = :ano");
		hql.append(" and t.periodo = :periodo");
		if(curso != null && departamento == null){
			hql.append(" and cc.curriculo.curso.id = :idCurso");
			hql.append(" and cc.componente.id = t.disciplina.id");
		}else{
			hql.append(" and t.disciplina.unidade.id = :idUnidade");
			hql.append(" and cc.componente.id = t.disciplina.id");
		}
		hql.append(" group by t.disciplina.codigo, t.disciplina.detalhes.nome, t.situacaoTurma.descricao");
		hql.append(" order by t.disciplina.detalhes.nome asc");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idConsolidada", SituacaoTurma.CONSOLIDADA);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		if(curso != null && departamento == null)
			q.setInteger("idCurso", curso.getId());
		else
			q.setInteger("idUnidade", departamento.getId());

		@SuppressWarnings("unchecked")
		List<Object[]> turmas = q.list();
		Iterator<Object[]> it = turmas.iterator();

		while(it.hasNext()){
			int col = 0;
			Object[] colunas = it.next();

			String codigo = (String) colunas[col++];
			String nome = (String) colunas[col++];

			LinhaRelatorioTurmasConsolidadas linha = relatorio.get(nome);
			if(linha == null){
				linha = new LinhaRelatorioTurmasConsolidadas();
			}

			linha.setNome(nome);
			linha.setCodigo(codigo);
			linha.setSituacao((String) colunas[col++]);
			linha.setTotal((Long) colunas[col++]);

			relatorio.put(nome, linha);
		}
		return relatorio;
	}


	/**
	 * Consulta que constrói o relatório de disciplinas com mais reprovações.
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Map<String, LinhaRelatorioReprovacoesDisciplinas> findReprovacoesDisciplinas(int ano, int periodo, Curso curso, ComponenteCurricular componente, Unidade departamento, boolean incluirEAD) throws DAOException {

		Map<String, LinhaRelatorioReprovacoesDisciplinas> relatorio = new TreeMap<String, LinhaRelatorioReprovacoesDisciplinas>();

		StringBuilder hql = new StringBuilder(
			"select mc.turma.disciplina.codigo," +
			" mc.turma.disciplina.detalhes.nome," +
			" mc.discente.pessoa.nome," +
			" mc.discente.matricula," +
			" mc.discente.curso.modalidadeEducacao.id," +
			" count(mc.id)" +
			" from MatriculaComponente mc" +
			" where mc.ano = :ano" +
			" and mc.periodo = :periodo" +
			" and (mc.situacaoMatricula.id = :idReprovado" +
			" or mc.situacaoMatricula.id = :idReprovadoFalta or mc.situacaoMatricula.id = :idReprovadoMediaFalta)");
		if(curso != null)
			hql.append(" and mc.discente.curso.id = :idCurso");
		if( componente != null )
			hql.append(" and mc.componente.id = :idComponente");
		if(departamento != null)
			hql.append(" and mc.turma.disciplina.unidade.id = :idDepartamento");
		if (!incluirEAD)
			hql.append(" and mc.turma.polo is null");
		hql.append(" group by mc.turma.disciplina.detalhes.nome," +
				" mc.turma.disciplina.codigo," +
				" mc.discente.pessoa.nome," +
				" mc.discente.matricula," +
				" mc.discente.curso.modalidadeEducacao.id" +
				" order by mc.turma.disciplina.detalhes.nome asc");

		Query q = getSession().createQuery(hql.toString());
		if(curso != null)
			q.setInteger("idCurso", curso.getId());
		if( componente != null )
			q.setInteger("idComponente", componente.getId());
		if(departamento != null)
			q.setInteger("idDepartamento", departamento.getId());
		q.setInteger("idReprovado", SituacaoMatricula.REPROVADO.getId());
		q.setInteger("idReprovadoFalta", SituacaoMatricula.REPROVADO_FALTA.getId());
		q.setInteger("idReprovadoMediaFalta", SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		
		@SuppressWarnings("unchecked")
		List<Object[]> reprovacoes = q.list();
		Iterator<Object[]> it = reprovacoes.iterator();

		while(it.hasNext()){
			int col = 0;
			Object[] colunas = it.next();

			String codigo = (String) colunas[col++];
			String nome = (String) colunas[col++];

			LinhaRelatorioReprovacoesDisciplinas linha = relatorio.get(nome);
			if(linha == null){
				linha = new LinhaRelatorioReprovacoesDisciplinas();
			}

			linha.setCodigo(codigo);
			linha.setNome(nome);
			linha.getDiscentes().put((String) colunas[col++], (Long) colunas[col++]);
			
			Integer idModalidade = (Integer) colunas[col++];
			if (idModalidade != null && idModalidade == ModalidadeEducacao.A_DISTANCIA)
				linha.setTotalEad( linha.getTotalEad() + 1 );
			else
				linha.setTotalPresencial( linha.getTotalPresencial() + 1 );
			
			linha.setTotal((Long) colunas[col++]);

			relatorio.put(nome, linha);
		}
		return relatorio;
	}
	
	/**
	 * Retorna os alunos do curso (<code>idCurso</code>) que ainda não cumpriram um
	 * componente curricular (<code>idComponenteCurricular</code>). Opcionalmente, a
	 * busca pode se restringir a alunos que entraram num certo período
	 * (<code>anoDeIngresso</code> e <code>periodoDeIngresso</code>).
	 *
	 * @param idCurso
	 * @param idComponenteCurricular
	 * @param anoDeIngresso passe 0 se não quiser filtrar por ano e período
	 * @param idPeriodoDeIngresso passe 0 se não quiser filtrar por ano e período
	 * 
	 * @return uma lista com os alunos que não cumpriram o componente 
	 *
	 * @author Bráulio Bezerra
	 * @since 20/11/2009
	 * @version 1.0 Criação
	 */
	public Map<Integer, List<Integer>> findAlunosPendentesDeComponente(
			int idCurso, ComponenteCurricular componenteCurricular, int anoDeIngresso, int periodoDeIngresso, 
			boolean matriculados , boolean aptos, boolean possiveisAptos, int idMatrizCurricular)	throws DAOException {
		
		List<Integer> idsSituacoesPagasOuMatriculadas = new ArrayList<Integer>();
		
		for ( SituacaoMatricula sit : SituacaoMatricula.getSituacoesPagas() )
			idsSituacoesPagasOuMatriculadas.add( sit.getId() );

		// Se o filtro LISTAR ALUNOS MATRICULADOS NO PERÍODO ATUAL não é selecionado a situação
		// MATRICULADO é considerado na subseleção de discentes que já cumpriram o componente 
		// curricular, ou seja, na consulta final a situação MATRICULADO não é considerado
		// (se filtro selecionado, MATRICULADO é considerado na consulta final).
		if(!matriculados) 
			idsSituacoesPagasOuMatriculadas.add(SituacaoMatricula.MATRICULADO.getId());
		
		String sql =
				"( \n" +
				"	--Seleciona todos [com entrada no período] para em seguida serem mantidos \n" +
				"	-- apenas os que ainda não possuem matricula no componente \n" +
				"	SELECT d.id_discente, mc.id_componente_curricular \n" +
				"	FROM \n" +
				"		public.discente d \n" +
				"	INNER JOIN graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente \n"+
				"	LEFT JOIN ensino.matricula_componente mc ON mc.id_discente = d.id_discente \n"+
				"	WHERE \n" ;
		
		if ( idCurso > 0 )
			sql+="		d.id_curso = " + idCurso + " AND \n";
		if ( idMatrizCurricular > 0 )
			sql+="		dg.id_matriz_curricular = " + idMatrizCurricular + " AND \n";
		if ( anoDeIngresso > 0 )
			sql+="		d.ano_ingresso = " + anoDeIngresso + " AND \n";
		if ( periodoDeIngresso > 0 )
			sql+="		d.periodo_ingresso = " + periodoDeIngresso + " AND \n";
		if ( !possiveisAptos && ValidatorUtil.isNotEmpty(componenteCurricular.getPreRequisito()) ) // Considera os discentes que estejam com situações de conclusão em pré-requisitos do componente
			sql+="		mc.id_situacao_matricula NOT IN " + gerarStringIn(new int[] {SituacaoMatricula.MATRICULADO.getId()}) + " AND \n";
		
		sql +=	"		d.status IN " + gerarStringIn(StatusDiscente.getAtivos()) + " \n" + // alunos ainda na UFRN
				"		AND mc.id_situacao_matricula IN " + gerarStringIn(idsSituacoesPagasOuMatriculadas) + " \n" +		
				"	--Seleciona todos que já cumpriram o componente curricular \n" +
				"		AND NOT EXISTS ( \n" +
				"			SELECT d2.id_discente \n" +
				"			FROM \n" +
				"				public.discente d2 \n" +
				"				INNER JOIN ensino.matricula_componente mc2 ON mc2.id_discente = d2.id_discente  \n" + 
				"			WHERE \n" +
				"				d2.id_discente = d.id_discente \n" +
				"				AND mc2.id_componente_curricular = " + componenteCurricular.getId() + " \n" +
				"				AND mc2.id_situacao_matricula IN " + gerarStringIn(idsSituacoesPagasOuMatriculadas) + " \n" +			
				"				) \n" +
				" 	GROUP BY d.id_discente , mc.id_componente_curricular \n" +
				" 	ORDER BY d.id_discente, mc.id_componente_curricular \n" +
				")	";
				
		Query q = getSession().createSQLQuery( sql );
		
		@SuppressWarnings("unchecked")
		List< Object[] > res = q.list();
		
		List<Integer> listComponentesCursados = new ArrayList<Integer>();
		Map<Integer, List<Integer>> resultados = new HashMap<Integer, List<Integer>>();
		
		Integer idDiscenteAnterior = 0;
		Integer idDiscente;
		int cont = 0;
		for ( Object obj[] : res ) {
			idDiscente = (Integer) obj[0];
			cont++;
			if (!(idDiscente.compareTo(idDiscenteAnterior) == 0)){
				if(idDiscenteAnterior != 0){
					resultados.put(idDiscenteAnterior, listComponentesCursados);
				}	
					idDiscenteAnterior = idDiscente;
					listComponentesCursados =  new ArrayList<Integer>();
					listComponentesCursados.add((Integer) obj[1]);
				
			}else{
				listComponentesCursados.add((Integer) obj[1]);
			}
			
			//Inserindo no map o último discente e seus componentes da lista de objetos da query.
			if(cont == (res.size() ))
				resultados.put(idDiscenteAnterior, listComponentesCursados);
		}
	
		return resultados;
	}

	/**
	 * Retorna um conjunto de {@link SugestaoMatriculaEquivalentes} de acordo com os dados passados.
	 * 
	 * @param curso
	 * @param matriz
	 * @param curriculo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SugestaoMatriculaEquivalentes> findEquivalentesByCurriculo(int curso, int matriz, int curriculo) throws DAOException {
		List<SugestaoMatriculaEquivalentes> sugestoes = new ArrayList<SugestaoMatriculaEquivalentes>();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try{
			con = Database.getInstance().getSigaaConnection();
			
			String sql = "SELECT cc.semestre_oferta, c.id_disciplina as id_componente, c.codigo as codigo_componente, cd.nome as nome_componente, cd.equivalencia as equivalencia,  c2.id_disciplina as id_equivalente, c2.codigo as codigo_equivalente, cd2.nome as nome_equivalente, cc.obrigatoria as obrigatoria " +
							"FROM curso crs " +
								"JOIN graduacao.matriz_curricular mc ON crs.id_curso = mc.id_curso " +
								"JOIN graduacao.curriculo curr ON mc.id_matriz_curricular = curr.id_matriz " +
								"JOIN graduacao.curriculo_componente cc ON cc.id_curriculo = ? " +
								"JOIN ensino.componente_curricular c ON c.id_disciplina = cc.id_componente_curricular " +
								"JOIN ensino.componente_curricular_detalhes cd ON (c.id_detalhe = cd.id_componente_detalhes) " +
								"JOIN ensino.componente_curricular c2 ON (cd.equivalencia like  '% '||c2.id_disciplina||' %' ) " +
								"JOIN ensino.componente_curricular_detalhes cd2 on (c2.id_detalhe = cd2.id_componente_detalhes) " +
							"WHERE crs.id_curso = ? " +
								"AND mc.id_matriz_curricular = ? " +
								"AND curr.id_curriculo = ? " +
							"ORDER BY cc.semestre_oferta, cc.obrigatoria DESC, c.codigo, c2.codigo";
			
			st = con.prepareStatement(sql);
			
			int i = 1;
			st.setInt(i++, curriculo);
			st.setInt(i++, curso);
			st.setInt(i++, matriz);
			st.setInt(i++, curriculo);
			
			rs = st.executeQuery();
			
			while (rs.next()) {
				ComponenteCurricular componenteCurricular = new ComponenteCurricular();
				componenteCurricular.setId(rs.getInt("id_componente"));
				componenteCurricular.setNome(rs.getString("nome_componente"));
				componenteCurricular.setCodigo(rs.getString("codigo_componente"));
				componenteCurricular.setEquivalencia(rs.getString("equivalencia"));
				
				CurriculoComponente curriculoComponente = new CurriculoComponente(componenteCurricular);
				curriculoComponente.setSemestreOferta(rs.getInt("semestre_oferta"));
				curriculoComponente.setObrigatoria(rs.getBoolean("obrigatoria"));
				
				SugestaoMatriculaEquivalentes sugestaoEquivalente = new SugestaoMatriculaEquivalentes(curriculoComponente);
				
				ComponenteCurricular equivalente = new ComponenteCurricular();
				equivalente.setId(rs.getInt("id_equivalente"));
				equivalente.setNome(rs.getString("nome_equivalente"));
				equivalente.setCodigo(rs.getString("codigo_equivalente"));
				
				SugestaoMatricula sugestao = new SugestaoMatricula();
				sugestao.setAtividade(equivalente);
				
				if(sugestoes.contains(sugestaoEquivalente)) {
					if(!sugestoes.get(sugestoes.indexOf(sugestaoEquivalente)).hasSugestao(sugestao))
						sugestoes.get(sugestoes.indexOf(sugestaoEquivalente)).adicionaSugestao(sugestao);
				} else {
					sugestaoEquivalente.adicionaSugestao(sugestao);
					sugestoes.add(sugestaoEquivalente);
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}  finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
		
		return sugestoes;
	}
	
	/**
	 * Busca os discentes que possuem os id's especificados.
	 * 
	 * @param ids
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findByIds(Collection<Integer> ids) throws DAOException {
		
		String projecao = 
						"dg.id, " +
						"dg.discente.matricula, " +
						"dg.discente.pessoa.nome, " +
						"dg.discente.status, " +
						"dg.discente.nivel, " +
						"dg.discente.curso.id, " +
						"dg.discente.curso.nome, " +
						"dg.discente.curso.municipio.nome, " +
						"matriz.id as dg.matrizCurricular.id, " +
						"ga.id as dg.matrizCurricular.grauAcademico.id, " +
						"ga.descricao as dg.matrizCurricular.grauAcademico.descricao, " +
						"h.id as dg.matrizCurricular.habilitacao.id, " +
						"h.nome as dg.matrizCurricular.habilitacao.nome, " +
						"t.id as dg.matrizCurricular.turno.id, " +
						"t.sigla as dg.matrizCurricular.turno.sigla, " +
						"c.id as dg.matrizCurricular.curso.id, " +
						"c.nome as dg.matrizCurricular.curso.nome, " +
						"m.nome as dg.matrizCurricular.curso.municipio.nome ";
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT " + HibernateUtils.removeAliasFromProjecao(projecao) + 
					" FROM DiscenteGraduacao dg " +
					" INNER JOIN dg.matrizCurricular matriz " +
					" LEFT JOIN matriz.curso c " +
					" LEFT JOIN c.municipio m " +
					" LEFT JOIN matriz.habilitacao h " +
					" LEFT JOIN matriz.turno t " +
					" LEFT JOIN matriz.grauAcademico ga " +
					"WHERE dg.id IN " + gerarStringIn(ids));

			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			Collection<DiscenteGraduacao> lista = HibernateUtils.parseTo(q.list(), projecao, DiscenteGraduacao.class, "dg" );
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
}
