/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 06/03/2008
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.tecnico.relatorios.LinhaRelatorioAprovadosReprovados;

/** Classe responsável por consultas específicas para a geração de relatórios Técnicos.
 * @author leonardo
 */
public class RelatoriosTecnicoDao extends AbstractRelatorioSqlDao {

	/**
	 * Busca a lista de alunos ingressantes de uma escola do ensino técnico.
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaAlunosCadastrados(int gestora, char nivel) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select d.ano_ingresso, d.periodo_ingresso, d.matricula, d.status, p.nome as aluno_nome, " +
				"  st.descricao as descricao_status " +
				" from discente d " +
				"	join comum.pessoa p using (id_pessoa)" +
				"	join status_discente st using (status)" +
				" where d.id_pessoa = p.id_pessoa " +
				" and d.status in " + UFRNUtils.gerarStringIn( StatusDiscente.getStatusComVinculo() ) +
				" and d.nivel = '" + nivel + "'" +
				" and d.id_gestora_academica = " + gestora);
		sqlconsulta.append(" order by d.ano_ingresso, d.periodo_ingresso, p.nome");
		
		List<Map<String,Object>> result;
		
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Busca a lista de alunos ingressantes de uma escola do ensino técnico.
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaAlunosIngressantes(int gestora, Integer idCurso, Integer ano, Integer periodo, char nivel) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select d.ano_ingresso, d.periodo_ingresso, d.matricula, d.status, p.nome as aluno_nome, " +
				" c.id_curso, c.nome as curso_nome, ete.id_especializacao_turma_entrada, ete.descricao as especializacao_nome " +
				" from discente d, comum.pessoa p, curso c, tecnico.discente_tecnico dt, " +
				" tecnico.turma_entrada_tecnico tt " +
				" left outer join tecnico.especializacao_turma_entrada ete using (id_especializacao_turma_entrada), " +
				" tecnico.estrutura_curricular_tecnica et " +
				" where d.id_pessoa = p.id_pessoa " +
				" and d.status <> " + StatusDiscente.EXCLUIDO +
				" and d.nivel = '" + nivel + "'" +
				" and d.id_gestora_academica = " + gestora +
				" and d.ano_ingresso = " + ano +
				" and d.periodo_ingresso = " + periodo +
				" and d.id_discente = dt.id_discente " +
				" and dt.id_turma_entrada = tt.id_turma_entrada " +
				" and tt.id_estrutura_curricular = et.id_estrutura_curricular " +
				" and et.id_curso = c.id_curso ");
		if(idCurso!=null && idCurso > 0)
			sqlconsulta.append(" and c.id_curso = "+ idCurso);
		sqlconsulta.append(" order by c.nome, p.nome");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Busca a lista de alunos concluintes de uma escola do ensino técnico.
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaAlunosConcluintes(int gestora, Integer idCurso) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select 	d.ano_ingresso, d.periodo_ingresso, d.matricula, p.nome as aluno_nome," +
				"	c.id_curso, c.nome as curso_nome, ete.id_especializacao_turma_entrada, ete.descricao as especializacao_nome" +
				" from 	ensino.matricula_componente m, ensino.turma t, discente d, comum.pessoa p, curso c,	" +
				"	ensino.componente_curricular cc, ensino.componente_curricular_detalhes cd, 	" +
				"	tecnico.discente_tecnico dt," +
				"	tecnico.turma_entrada_tecnico tt left outer join tecnico.especializacao_turma_entrada ete using (id_especializacao_turma_entrada),	" +
				"	tecnico.estrutura_curricular_tecnica et  " +
				" where m.id_turma = t.id_turma  " +
				" and m.id_discente = d.id_discente  " +
				" and d.id_pessoa = p.id_pessoa  " +
				" and t.id_disciplina = cc.id_disciplina  " +
				" and cc.id_detalhe = cd.id_componente_detalhes  " +
				" and m.id_situacao_matricula in (1,2,4,21,22,23)  " +
				" and d.status = 1  " +
				" and d.nivel = 'T'  " +
				" and d.id_gestora_academica = " + gestora +
				" and d.id_discente = dt.id_discente  " +
				" and dt.id_turma_entrada = tt.id_turma_entrada  " +
				" and tt.id_estrutura_curricular = et.id_estrutura_curricular  " +
				" and et.id_curso = c.id_curso  ");
		if(idCurso!=null && idCurso > 0)
			sqlconsulta.append(" and c.id_curso = "+ idCurso);
		sqlconsulta.append(" group by d.ano_ingresso, d.periodo_ingresso, d.matricula, p.nome, et.carga_horaria, c.id_curso, c.nome, ete.id_especializacao_turma_entrada, ete.descricao  having sum(cd.ch_total) >= et.carga_horaria  " +
				" order by c.nome, ete.descricao, p.nome");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		} 
		return result;
	}

	/**
	 * Busca a lista de alunos que concluíram o programa de uma escola do ensino técnico.
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaAlunosConcluidos(int gestora, Integer idCurso, int ano, int periodo) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select 	d.ano_ingresso, d.periodo_ingresso, d.matricula, p.nome as aluno_nome, st.descricao, " +
				" c.id_curso, c.nome as curso_nome, ete.id_especializacao_turma_entrada, ete.descricao as especializacao_nome, m.observacao " +
				" from 	ensino.movimentacao_aluno m, discente d, comum.pessoa p, curso c,	status_discente st, " +
				" tecnico.discente_tecnico dt, " +
				" tecnico.turma_entrada_tecnico tt left outer join tecnico.especializacao_turma_entrada ete using (id_especializacao_turma_entrada), " +
				" tecnico.estrutura_curricular_tecnica et " +
				" where m.id_discente = d.id_discente " +
				" and d.id_pessoa = p.id_pessoa " +
				" and d.status = st.status " +
				" and d.status = " + StatusDiscente.CONCLUIDO +
				" and d.nivel = 'T' " +
				" and d.id_gestora_academica = " + gestora +
				" and m.ano_referencia = " + ano +
				" and m.periodo_referencia = " + periodo +
				" and m.id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.CONCLUSAO +
				" and d.id_discente = dt.id_discente " +
				" and dt.id_turma_entrada = tt.id_turma_entrada " +
				" and tt.id_estrutura_curricular = et.id_estrutura_curricular " +
				" and et.id_curso = c.id_curso ");
		if(idCurso!=null && idCurso > 0)
			sqlconsulta.append(" and c.id_curso = "+ idCurso);
		sqlconsulta.append(" group by d.ano_ingresso, d.periodo_ingresso, d.matricula, p.nome, et.carga_horaria, c.id_curso, " +
				" c.nome, ete.id_especializacao_turma_entrada, ete.descricao, m.observacao, st.descricao " +
				" order by c.nome, ete.descricao, p.nome");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}
	

	/**
	 * Gera a lista de alunos matriculados numa escola do ensino técnico num dado ano-período.
	 *
	 * @param ano
	 * @param periodo
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunosMatriculados(int ano, int periodo, int gestora) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select c.id_curso, c.nome as nome_curso, et.id_especializacao_turma_entrada," +
				" et.descricao as nome_especializacao, d.matricula, p.nome as nome_discente" +
				" from comum.pessoa p, discente d, curso c," +
				" tecnico.discente_tecnico dt" +
				" left outer join tecnico.turma_entrada_tecnico te on (dt.id_turma_entrada = te.id_turma_entrada)" +
				" left outer join tecnico.especializacao_turma_entrada et on (te.id_especializacao_turma_entrada = et.id_especializacao_turma_entrada)" +
				" where d.id_pessoa = p.id_pessoa" +
				" and d.nivel = 'T'" +
				" and d.id_gestora_academica = " + gestora +
				" and d.id_discente = dt.id_discente" +
				" and d.id_curso = c.id_curso" +
				" and exists (select * from ensino.matricula_componente m" +
				"		where m.id_discente = d.id_discente" +
				"		and m.ano = " + ano +
				"		and m.periodo = " + periodo +
				"		and m.id_situacao_matricula in (1,2,4,5,6,7)" +
				"		)" +
				" group by c.id_curso, nome_curso, et.id_especializacao_turma_entrada, nome_especializacao," +
				" d.matricula, nome_discente" +
				" order by nome_curso, nome_especializacao, nome_discente");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}

	/**
	 * Gera o quantitativo de alunos por ano de ingresso
	 *
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findQuantitativoAlunosByAnoIngresso(int gestora) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select d.ano_ingresso, count(*) as total " +
				" from discente d, comum.pessoa p, tecnico.discente_tecnico dt " +
				" where d.id_pessoa = p.id_pessoa " +
				" and d.id_discente = dt.id_discente " +
				" and d.id_gestora_academica = " + gestora +
				" group by d.ano_ingresso " +
				" order by d.ano_ingresso");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}

	/**
	 * Busca a lista de trancamentos por disciplina num dado ano período
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaTrancamentosByDisciplina(int ano, int periodo, int gestora) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(" select c.id_disciplina, c.codigo, det.nome as nome_disciplina, d.matricula, p.nome as nome_discente" +
				" from ensino.matricula_componente m, ensino.componente_curricular c," +
				" ensino.componente_curricular_detalhes det, discente d, comum.pessoa p" +
				" where m.id_componente_curricular = c.id_disciplina" +
				" and c.id_disciplina = det.id_componente" +
				" and m.id_discente = d.id_discente" +
				" and d.id_pessoa = p.id_pessoa" +
				" and d.id_gestora_academica = " + gestora +
				" and c.nivel = 'T'" +
				" and m.id_situacao_matricula = 5" +
				" and m.ano = " + ano +
				" and m.periodo = " + periodo +
				" group by c.id_disciplina, c.codigo, det.nome, d.matricula, p.nome" +
				" order by det.nome, p.nome");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}

	/** Retorna uma lista de trancamentos por ano e semestre de uma unidade gestora.
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaTrancamentosByAnoSemestre(int gestora) throws DAOException {
		String sql ="select mat.ano, mat.periodo, count(mat.ano) as total " +
				" from ensino.matricula_componente mat join discente d on d.id_discente=mat.id_discente" +
				" where d.nivel='T' and d.id_gestora_academica= " + gestora +
				" and mat.id_situacao_matricula=" + SituacaoMatricula.TRANCADO.getId()+
				" group by mat.ano, mat.periodo" +
				" order by mat.ano, mat.periodo";
		StringBuilder sqlconsulta = new StringBuilder(sql);
		List<Map<String,Object>> result;
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}

	/** Retorna os dados para o relatório de alunos sem matrícula em um ano, período e unidade especificados.
	 * @param ano
	 * @param periodo
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaAlunosSemMatriculaComponente(int ano, int periodo, int gestora) throws DAOException {
		String sql ="select c.id_curso, c.nome as curso_nome, d.ano_ingresso, d.periodo_ingresso, d.matricula, p.nome as aluno_nome " +
				" from discente d join curso c on d.id_curso=c.id_curso join comum.pessoa p on d.id_pessoa=p.id_pessoa" +
				" where d.nivel='T' and d.id_gestora_academica="+gestora+
				"  and d.ano_ingresso<"+ano+" and d.periodo_ingresso< " +periodo+
				"and d.status in (1,8) and " +
				"not exists (select m.id_matricula_componente from ensino.matricula_componente m " +
				"where m.id_discente=d.id_discente and m.ano="+ano+" and m.periodo=" +periodo+") order by c.nome, p.nome";
		StringBuilder sqlconsulta = new StringBuilder(sql);
		List<Map<String,Object>> result;
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}

	/** Retorna os dados para o relatório de alunos aprovados e reprovados por disciplina, em um ano, período e unidade especificados.
	 * @param ano
	 * @param periodo
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public Map<String, LinhaRelatorioAprovadosReprovados> findAprovadosReprovadosDisciplina(int ano, int periodo, int gestora) throws DAOException {

		Map<String, LinhaRelatorioAprovadosReprovados> relatorio =  new TreeMap<String, LinhaRelatorioAprovadosReprovados>();

		StringBuilder hql = new StringBuilder(" select c.id_disciplina, c.codigo, det.nome as nome_disciplina, m.id_situacao_matricula, m.id_turma, m.id_discente " +
				" from ensino.matricula_componente m, ensino.componente_curricular c," +
				" ensino.componente_curricular_detalhes det" +
				" where m.id_componente_curricular = c.id_disciplina" +
				" and c.id_disciplina = det.id_componente" +
				" and c.id_unidade = " + gestora +
				" and c.nivel = 'T'" +
				" and m.id_situacao_matricula in (2,4,5,6,7,21,22,23)" +
				" and m.ano = :ano" +
				" and m.periodo = :periodo" +
				" group by m.id_discente, c.id_disciplina, c.codigo, det.nome, m.id_situacao_matricula, m.id_turma " +
				" order by det.nome, m.id_turma");

		Query q = getSession().createSQLQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);

		@SuppressWarnings("unchecked")
		List<Object[]> matriculas = q.list();
		Iterator<Object[]> it = matriculas.iterator();

		while(it.hasNext()){
			int col = 0;
			Object[] colunas = it.next();

			Integer id = (Integer) colunas[col++];
			String codigo = (String) colunas[col++];
			String nome = (String) colunas[col++];
			Integer situacao = (Integer) colunas[col++];
			Integer turma = (Integer) colunas[col++];

			LinhaRelatorioAprovadosReprovados linha = relatorio.get(nome+" "+codigo);
			if(linha == null)
				linha = new LinhaRelatorioAprovadosReprovados();

			linha.setId(id);
			linha.setCodigo(codigo);
			linha.setNome(nome);

			if(situacao == SituacaoMatricula.MATRICULADO.getId())
				linha.setMatriculados( linha.getMatriculados() + 1 );
			else if(situacao == SituacaoMatricula.APROVADO.getId() || situacao == SituacaoMatricula.APROVEITADO_CUMPRIU.getId()
					|| situacao == SituacaoMatricula.APROVEITADO_DISPENSADO.getId() || situacao == SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId())
				linha.setAprovados( linha.getAprovados() + 1 );
			else if(situacao == SituacaoMatricula.REPROVADO.getId() || situacao == SituacaoMatricula.REPROVADO_FALTA.getId() || situacao == SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId())
				linha.setReprovados( linha.getReprovados() + 1 );
			else if(situacao == SituacaoMatricula.TRANCADO.getId())
				linha.setTrancados( linha.getTrancados() + 1 );
			
			if(turma != null && turma != linha.getTurmaAtual()){
				linha.setTurmas( linha.getTurmas() + 1 );
				linha.setTurmaAtual(turma);
			}

			relatorio.put(nome+" "+codigo, linha);
		}

		return relatorio;
	}
	
	/**
	 * Retorna uma lista de dados mapeados, de turmas por departamento. Os dados
	 * mapeados são: docente_nome, siape, componente_codigo, componente_nome,
	 * nivel, ch_total, ch_dedicada_periodo, tipo, codigo_turma, id_turma, ano,
	 * periodo, qtd_alunos, id_polo, nome_municipio.
	 * 
	 * @param unidade
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaTurmasDocenteByDepartamento(int unidade, int ano, int periodo) throws DAOException {
		String sql = "select p.nome as docente_nome,"
			+ "  cast(coalesce(s.siape, p.cpf_cnpj) as text) as siape,"
			+ "  s.dedicacao_exclusiva as dedicacao_exclusiva,"
			+ "  coalesce(s.regime_trabalho, 0) as regime_trabalho,"
			+ "  cc.codigo as componente_codigo,"
			+ "  ccd.nome as componente_nome,"
			+ "  cc.nivel,"
			+ "  ccd.ch_total,"
			+ "  case when (t.distancia = true OR t.id_polo is not null)" +
					" then ch_ead.ch_dedicada" +
					" else dt.ch_dedicada_periodo end as ch_dedicada_periodo,"
			+ "  t.tipo,"
			+ "  t.codigo as codigo_turma,"
			+ "  t.id_turma,"
			+ "  t.ano,"
			+ "  t.periodo,"
			+ "  (select count (mc.id_matricula_componente) from ensino.matricula_componente mc where mc.id_turma = t.id_turma and mc.id_situacao_matricula in  " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + "  ) as qtd_alunos,"
			+ "  t.id_polo,"
			+ "  municipio.nome as nome_municipio," 
			+ "  upper(cf.denominacao) as denominacao_classe_funcional" 
			+ " from ensino.turma t"
			+ "  inner join ensino.docente_turma dt using (id_turma)"
			+ "  left join ensino.docente_externo dex using (id_docente_externo)"
			+ "  inner join rh.servidor s on (dt.id_docente = s.id_servidor or dex.id_servidor = s.id_servidor)"
			+ "  left join stricto_sensu.equipe_programa eqp on (dt.id_docente = eqp.id_servidor)"
			+ "  left join comum.pessoa p on (s.id_pessoa = p.id_pessoa)"
			+ "  left join rh.classe_funcional cf on (cf.id_classe_funcional = s.id_classe_funcional)"
			+ "  inner join ensino.componente_curricular cc on (t.id_disciplina = cc.id_disciplina)"
			+ "  inner join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)"
			+ "  left join ead.polo polo on (t.id_polo = polo.id_polo) "
			+ "  left join comum.municipio on (polo.id_cidade = municipio.id_municipio) "
			+ "  left join ead.carga_horaria_ead ch_ead on (s.id_servidor = ch_ead.id_servidor and t.ano = ch_ead.ano and t.periodo = ch_ead.periodo)"
			+ " where (s.id_unidade = " + unidade + " or dex.id_unidade = " + unidade + " or eqp.id_programa = " + unidade + ")"
			+ "  and t.ano = " + ano
			+ "  and t.periodo = " + periodo
			+ "  and t.id_situacao_turma in " + gerarStringIn(SituacaoTurma.getSituacoesValidas())
			+ "  and cc.nivel <> '" + NivelEnsino.FORMACAO_COMPLEMENTAR + "'"
			+ " group by t.distancia, t.id_polo, ch_ead.ch_dedicada, t.id_turma, p.nome, s.siape, p.cpf_cnpj, s.dedicacao_exclusiva,"
			+ "  s.regime_trabalho, cc.codigo, ccd.nome, cc.nivel, ccd.ch_total, dt.ch_dedicada_periodo,"
			+ "  t.tipo, t.codigo, t.ano, t.periodo, t.id_polo, nome_municipio, cf.denominacao"
			+ " order by docente_nome, siape, nivel, periodo, componente_nome";
		StringBuilder sqlconsulta = new StringBuilder(sql);
		List<Map<String,Object>> result;
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	/** Retorna uma lista contendo dados para o relatório de alunos com movimentação do tipo CANCELAMENTO.
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findListaAlunosComMovimentacaoCancelamento(int gestora) throws DAOException {
		String sql = "select c.id_curso, c.nome as nome_curso, et.id_especializacao_turma_entrada, " +
				" et.descricao as nome_especializacao, d.id_discente, d.matricula, d.status, " +
				" p.nome as nome_discente, sum(cd.ch_total) as ch_cumprida" +
				" from ensino.matricula_componente m " +
				"	inner join ensino.componente_curricular_detalhes cd on (m.id_componente_detalhes=cd.id_componente_detalhes) " +
				"	inner join discente d on (m.id_discente=d.id_discente) " +
				"	inner join comum.pessoa p on (d.id_pessoa=p.id_pessoa) " +
				"	inner join curso c on (d.id_curso=c.id_curso) " +
				"	inner join tecnico.discente_tecnico dt on (d.id_discente=dt.id_discente) " +
				" 	left outer join tecnico.turma_entrada_tecnico te on (dt.id_turma_entrada = te.id_turma_entrada)" +
				" 	left outer join tecnico.especializacao_turma_entrada et on (te.id_especializacao_turma_entrada = et.id_especializacao_turma_entrada)" +
				" where d.nivel = 'T' " +
				" and d.id_discente in ( " +
				" select id_discente from ensino.movimentacao_aluno where id_tipo_movimentacao_aluno in ( " +
				" select id_tipo_movimentacao_aluno from ensino.tipo_movimentacao_aluno where statusdiscente = 6) and ativo = trueValue()) " +
				" and d.id_gestora_academica = " + gestora +
				" group by c.id_curso, c.nome, et.id_especializacao_turma_entrada, " + 
				" et.descricao, d.id_discente, d.matricula, d.status, p.nome " +
				" order by nome_curso, nome_especializacao, nome_discente";
		StringBuilder sqlconsulta = new StringBuilder(sql);
		List<Map<String,Object>> result;
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Gera um relatório com todos os alunos do técnico. Não é necessário passar nenhum parâmetro
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findAlunosTecnico(int idUnidade) throws DAOException {
		String sqlconsulta = "select p.cpf_cnpj as cpf, p.nome, d.matricula, c.nome as turma"+
 							 " from discente d"+
							 " join comum.pessoa p using(id_pessoa)"+
							 " join curso c using(id_curso)"+
							 " where d.nivel = '"+NivelEnsino.TECNICO+"'"+
							 " and id_gestora_academica = " + idUnidade +
							 " and d.status in " + UFRNUtils.gerarStringIn(
									 new int[]{StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.FORMANDO}) +  
							 " order by p.nome"; 

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
}