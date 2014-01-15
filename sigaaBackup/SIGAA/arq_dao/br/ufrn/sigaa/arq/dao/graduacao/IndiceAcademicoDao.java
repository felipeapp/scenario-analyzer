/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/03/2010
 * Autor:     David Pereira
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.AtualizacaoIndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DAO para buscas relacionadas aos índices
 * acadêmicos e suas associações com discentes
 * de graduação.
 * 
 * @author David Pereira
 *
 */
public class IndiceAcademicoDao extends GenericSigaaDAO {

	/**
	 * Busca a associação entre índice acadêmico e discente
	 * para o índice e o discente passados como parâmetro.
	 * @param discente
	 * @param indice
	 * @return
	 */
	public IndiceAcademicoDiscente findIndiceAcademicoDiscente(final Discente discente, final IndiceAcademico indice) throws DAOException {
		try {
			return (IndiceAcademicoDiscente) getJdbcTemplate().queryForObject("select * from ensino.indice_academico_discente where id_discente = ? and id_indice_academico = ?", new Object[] { discente.getId(), indice.getId() }, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					IndiceAcademicoDiscente iad = new IndiceAcademicoDiscente();
					iad.setId(rs.getInt("id"));
					iad.setDiscente(discente);
					iad.setIndice(indice);
					iad.setValor(rs.getDouble("valor"));
					return iad;
				}
			});
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Busca a lista de todos os índices acadêmicos que estão ativos.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<IndiceAcademico> findIndicesAtivos(char nivel) throws DAOException {
		return getSession().createCriteria(IndiceAcademico.class).add(eq("ativo", true)).add(eq("nivel", nivel)).addOrder(asc("ordem")).list();
	}

	/**
	 * Retorna a média final de um discente.
	 *
	 * Considera todas as disciplinas e todas as atividades do tipo estágio e
	 * trabalho de conclusão de curso, exceto aquelas que tem carga horária
	 * zero. São excluídas todas as atividades do tipo "atividade complementar".
	 *
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public double calculaIraDiscente(int idDiscente) throws DAOException {
		return getJdbcTemplate().queryForFloat("select sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total) "
				+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
				+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina "
				+ "and mc.media_final is not null and cc.id_bloco_subunidade is null "
				+ "and mc.id_situacao_matricula in (?, ?, ?, ?, ?, ?) and mc.id_componente_detalhes = ccd.id_componente_detalhes "
				+ "and (cc.id_tipo_componente != ? or (cc.id_tipo_componente = ? and ccd.ch_total > 0 and cc.id_tipo_atividade in (?, ?) "
				+ "and cc.necessitamediafinal = true))", idDiscente, SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.REPROVADO.getId(),
				SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), 
				SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(), TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.ATIVIDADE,
				TipoAtividade.ESTAGIO, TipoAtividade.TRABALHO_CONCLUSAO_CURSO);
	}
	
	/**
	 * O cálculo do IRA de stricto existe uma particulariedade em relação aos demais. 
	 * Caso o discente reprove na disciplina X, mas no outro semestre paga ela denovo e passa, o cálculo do IRA deve desconsiderar a reprovação.
	 * 
	 * @param idDiscente
	 * @return
	 */
	public double calculaIraDiscenteStricto(int idDiscente) {
		return getJdbcTemplate().queryForFloat(
				" select sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total) " +
				" from ensino.matricula_componente mc, " +
				"	ensino.componente_curricular cc, " +
				"	ensino.componente_curricular_detalhes ccd, " +
				"	(select m2.id_componente_curricular, count(case when id_situacao_matricula not in (?, ?, ?) then media_final end) as reprovacoes, count(case when id_situacao_matricula in (?, ?, ?) then media_final end) as aprovacoes from ensino.matricula_componente m2 " +
				" where id_discente = ? and media_final is not null group by m2.id_componente_curricular) as r " +
				" where mc.id_discente = ? " +
				"	and mc.id_componente_curricular = cc.id_disciplina " +
				"	and cc.id_disciplina = r.id_componente_curricular " +
				"	and mc.media_final is not null " +
				"	and ((mc.id_situacao_matricula in (?, ?, ?) and r.aprovacoes != 0) or (r.aprovacoes = 0 and mc.id_situacao_matricula in (?, ?, ?) )) " +
				"	and mc.id_componente_detalhes = ccd.id_componente_detalhes " +
				"	and (cc.id_tipo_componente != ? or (cc.id_tipo_componente = ? and ccd.ch_total > 0 and cc.id_tipo_atividade in (?, ?) and cc.necessitamediafinal = true)) ", 
				SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(),
				SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(),
				idDiscente, 
				idDiscente,
				SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(),
				SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId(),
				TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.ATIVIDADE, TipoAtividade.ESTAGIO, TipoAtividade.TRABALHO_CONCLUSAO_CURSO);
	}
	
	/**
	 * Retorna a Média de Conclusão (MC) de um discente.
	 * 
	 * A MC é a média ponderada do rendimento escolar final nos componentes curriculares 
	 * em que conseguiu êxito ao longo do curso. Considera apenas os componentes em que se obteve 
	 * êxito, excluindo as atividades complementares e os componentes curriculares cujo 
	 * rendimento escolar não é expresso de forma numérica
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public double calculaMcDiscente(int idDiscente) {
		return getJdbcTemplate().queryForFloat("select coalesce((sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total)), 0) "
				+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
				+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina "
				+ "and mc.media_final is not null and cc.id_bloco_subunidade is null "
				+ "and mc.id_situacao_matricula in (?) and mc.id_componente_detalhes = ccd.id_componente_detalhes "
				+ "and (cc.id_tipo_componente != ? or (cc.id_tipo_componente = ? and ccd.ch_total > 0 and cc.id_tipo_atividade in (?, ?) "
				+ "and cc.necessitamediafinal = true))", idDiscente, SituacaoMatricula.APROVADO.getId(), TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.ATIVIDADE,
				TipoAtividade.ESTAGIO, TipoAtividade.TRABALHO_CONCLUSAO_CURSO);
	}

	/**
	 * Calcula a Média de Conclusão Normalizada (MCN) de um discente.
	 * 
	 * A Média de Conclusão Normalizada é a MC do aluno normalizada em relação à média 
	 * e desvio padrão amostral das MCs dos concluintes da mesma modalidade do curso.
	 * 
	 * @param anoAtual
	 * @param idDiscente
	 * @return
	 */
	public double calculaMcnDiscente(int anoAtual, int idDiscente) {
		boolean temConcluidos = temConcluidos(anoAtual, idDiscente);
		double media = calculaMediaMc(temConcluidos, anoAtual, idDiscente);
		double desvio = calculaDesvioMc(temConcluidos, anoAtual, idDiscente, media);
		
		double mc = 0.0;
		
		try {
			mc = getJdbcTemplate().queryForFloat("select ia.valor from graduacao.discente_graduacao dg, ensino.indice_academico_discente ia "
					+ "where dg.id_discente_graduacao = ia.id_discente and ia.id_indice_academico = 1 and dg.id_discente_graduacao = ?", idDiscente);
		} catch(EmptyResultDataAccessException e) {
			
		}
				
				
		
		if (Math.abs(desvio) <= 0.0000000000001) {
			desvio = 1;
		}

		return 500 + 100 * ((mc - media) / desvio);
	}
	
	/**
	 * Calcula a média de todos os MCs dos últimos concluíntes da matriz
	 * curricular do aluno passado como parâmetro.
	 * @param anoAtual
	 * @param idDiscente
	 * @return
	 */
	public double calculaMediaMc(boolean temConcluidos, int anoAtual, int idDiscente) {
		
		if (temConcluidos) {
			return getJdbcTemplate().queryForFloat("select sum(mc)/count(id_discente) from ("
				+ "select id_discente, sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total) as mc " 
				+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd " 
				+ "where mc.id_discente in (select d.id_discente from discente d, graduacao.discente_graduacao dg, graduacao.matriz_curricular mc " 
				+ "where d.id_discente = dg.id_discente_graduacao and dg.id_matriz_curricular = mc.id_matriz_curricular " 
				+ "and d.id_curso = (select id_curso from discente where id_discente = ?) and mc.id_grau_academico = " 
				+ "(select mc.id_grau_academico from graduacao.matriz_curricular mc " 
				+ "where mc.id_matriz_curricular = (select id_matriz_curricular from graduacao.discente_graduacao where id_discente_graduacao = ?)) " 
				+ "and d.status = ? and d.id_forma_ingresso not in (select id_forma_ingresso from ensino.forma_ingresso where contagem_taxa_conclusao is false) and " 
				+ "(? - (select ano_referencia from ensino.movimentacao_aluno where id_discente = d.id_discente and id_tipo_movimentacao_aluno = ? and (ativo or ativo is null) order by ano_referencia limit 1)) <= 5 ) " 
				+ "and mc.id_componente_curricular = cc.id_disciplina and cc.id_bloco_subunidade is null  " 
				+ "and mc.id_situacao_matricula in (?, ?, ?) and mc.id_componente_detalhes = ccd.id_componente_detalhes " 
				+ "and (cc.id_tipo_componente != ? or (cc.id_tipo_componente = ? and ccd.ch_total > 0 and cc.id_tipo_atividade in (?, ?) " 
				+ "and cc.necessitamediafinal = true)) group by id_discente) as q", 
				idDiscente, idDiscente, StatusDiscente.CONCLUIDO, anoAtual, TipoMovimentacaoAluno.CONCLUSAO, 
				SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(),
				TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.ATIVIDADE, TipoAtividade.TRABALHO_CONCLUSAO_CURSO, TipoAtividade.ESTAGIO);
		} else {
			return getJdbcTemplate().queryForFloat("select sum(mc)/count(id_discente) from ( "
					+ "select id_discente, sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total) as mc " 
					+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd " 
					+ "where mc.id_discente in ( "
					+ "select id_discente from discente d, curso c " 
					+ "where d.id_curso = c.id_curso and "
					+ "c.id_unidade = (select id_unidade_referencia_mcn from curso, discente where curso.id_curso = discente.id_curso and discente.id_discente = ?) and " 
					+ "c.nivel = 'G' and " 
					+ " d.status = 3 and c.id_modalidade_educacao = 1 and " 
					+ "d.id_forma_ingresso not in (select id_forma_ingresso from ensino.forma_ingresso where contagem_taxa_conclusao is false) and "
					+ "((? - (select ano_referencia from ensino.movimentacao_aluno where id_discente = d.id_discente and id_tipo_movimentacao_aluno = 1 " 
					+ "and (ativo or ativo is null) order by ano_referencia limit 1)) <= 5)) "
					+ "and mc.id_componente_curricular = cc.id_disciplina and cc.id_bloco_subunidade is null  " 
					+ "and mc.id_situacao_matricula in (4, 22, 23) and mc.id_componente_detalhes = ccd.id_componente_detalhes " 
					+ "and (cc.id_tipo_componente != 1 or (cc.id_tipo_componente = 1 and ccd.ch_total > 0 and cc.id_tipo_atividade in (1, 3) " 
					+ "and cc.necessitamediafinal = true)) group by id_discente) as q", new Object[] { idDiscente, anoAtual });
		}
	}
	
	/**
	 * Calcula o desvio padrão de todos os MCs dos últimos concluíntes da matriz
	 * curricular do aluno passado como parâmetro.
	 * @param anoAtual
	 * @param idDiscente
	 * @return
	 */
	public double calculaDesvioMc(boolean temConcluidos, int anoAtual, int idDiscente, double media) {
		if (temConcluidos) {
			return getJdbcTemplate().queryForFloat("select (CASE WHEN count(id_discente) - 1 > 0 THEN (sum((mc - ?)^2)/( count(id_discente) - 1 ))^0.5 ELSE 0 END) from ("
				+ "select id_discente, sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total) as mc " 
				+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd " 
				+ "where mc.id_discente in (select d.id_discente from discente d, graduacao.discente_graduacao dg, graduacao.matriz_curricular mc " 
				+ "where d.id_discente = dg.id_discente_graduacao and dg.id_matriz_curricular = mc.id_matriz_curricular " 
				+ "and d.id_curso = (select id_curso from discente where id_discente = ?) and mc.id_grau_academico = " 
				+ "(select mc.id_grau_academico from graduacao.matriz_curricular mc " 
				+ "where mc.id_matriz_curricular = (select id_matriz_curricular from graduacao.discente_graduacao where id_discente_graduacao = ?)) " 
				+ "and d.status = ? and d.id_forma_ingresso not in (select id_forma_ingresso from ensino.forma_ingresso where contagem_taxa_conclusao is false) and " 
				+ "(? - (select ano_referencia from ensino.movimentacao_aluno where id_discente = d.id_discente and id_tipo_movimentacao_aluno = ? and (ativo or ativo is null) order by ano_referencia limit 1)) <= 5 ) " 
				+ "and mc.id_componente_curricular = cc.id_disciplina and cc.id_bloco_subunidade is null  " 
				+ "and mc.id_situacao_matricula in (?, ?, ?) and mc.id_componente_detalhes = ccd.id_componente_detalhes " 
				+ "and (cc.id_tipo_componente != ? or (cc.id_tipo_componente = ? and ccd.ch_total > 0 and cc.id_tipo_atividade in (?, ?) " 
				+ "and cc.necessitamediafinal = true)) group by id_discente) as q", 
				media, idDiscente, idDiscente, StatusDiscente.CONCLUIDO, anoAtual, TipoMovimentacaoAluno.CONCLUSAO, 
				SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(),
				TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.ATIVIDADE, TipoAtividade.TRABALHO_CONCLUSAO_CURSO, TipoAtividade.ESTAGIO);
		} else {
			return getJdbcTemplate().queryForFloat("select (CASE WHEN count(id_discente) - 1 > 0 THEN (sum((mc - ?)^2)/( count(id_discente) - 1 ))^0.5 ELSE 0 END) from ( "
					+ "select id_discente, sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total) as mc " 
					+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd " 
					+ "where mc.id_discente in ( "
					+ "select id_discente from discente d, curso c " 
					+ "where d.id_curso = c.id_curso and "
					+ "c.id_unidade = (select id_unidade_referencia_mcn from curso, discente where curso.id_curso = discente.id_curso and discente.id_discente = ?) and " 
					+ "c.nivel = 'G' and " 
					+ " d.status = 3 and c.id_modalidade_educacao = 1 and " 
					+ "d.id_forma_ingresso not in (select id_forma_ingresso from ensino.forma_ingresso where contagem_taxa_conclusao is false) and "
					+ "((? - (select ano_referencia from ensino.movimentacao_aluno where id_discente = d.id_discente and id_tipo_movimentacao_aluno = 1 " 
					+ "and (ativo or ativo is null) order by ano_referencia limit 1)) <= 5)) "
					+ "and mc.id_componente_curricular = cc.id_disciplina and cc.id_bloco_subunidade is null  " 
					+ "and mc.id_situacao_matricula in (4, 22, 23) and mc.id_componente_detalhes = ccd.id_componente_detalhes " 
					+ "and (cc.id_tipo_componente != 1 or (cc.id_tipo_componente = 1 and ccd.ch_total > 0 and cc.id_tipo_atividade in (1, 3) " 
					+ "and cc.necessitamediafinal = true)) group by id_discente) as q", new Object[] { media, idDiscente, anoAtual });
		}
	}
	
	/**
	 * Identifica se existem concluintes suficientes na matriz do aluno para saber se é possível calcular a média
	 * dos MCs desses alunos. 
	 * @param anoAtual
	 * @param idDiscente
	 * @return
	 */
	public boolean temConcluidos(int anoAtual, int idDiscente) {
		return getJdbcTemplate().queryForInt("select count(id_discente) from graduacao.discente_graduacao dg, discente d where d.id_discente = dg.id_discente_graduacao and dg.id_matriz_curricular = "
				+ "(select id_matriz_curricular from graduacao.discente_graduacao where id_discente_graduacao = ?) and "
				+ "d.status = 3 and d.id_forma_ingresso not in (select id_forma_ingresso from ensino.forma_ingresso where contagem_taxa_conclusao is false) and "
				+ "((? - (select ano_referencia from ensino.movimentacao_aluno where id_discente = d.id_discente and id_tipo_movimentacao_aluno = 1 and "
				+ "(ativo or ativo is null) order by ano_referencia limit 1)) <= 5)", new Object[] { idDiscente, anoAtual }) > ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.NUMERO_MINIMO_CONCLUIDOS_CALCULO_MCN);
	}

	/**
	 * Calcula o maior MC dos alunos ativos da mesma matriz curricular 
	 * curricular do aluno passado como parâmetro.
	 * @param temConcluidos 
	 * @param idDiscente
	 * @return
	 */
	public double calculaMaiorMc(boolean temConcluidos, int idDiscente) {
		return calculaMaiorMenorMC(temConcluidos, idDiscente, "max");
	}

	/**
	 * Calcula o maior ou menor MC
	 * 
	 * @param temConcluidos
	 * @param idDiscente
	 * @param operacao
	 * @return
	 */
	private double calculaMaiorMenorMC(boolean temConcluidos, int idDiscente, String operacao) {
		if (temConcluidos) {
			return  getJdbcTemplate().queryForFloat("select "+ operacao + "(iad.valor) from discente d, graduacao.discente_graduacao dg, "
					+ "graduacao.matriz_curricular mc, ensino.indice_academico_discente iad " 
					+ "where d.id_discente = dg.id_discente_graduacao and dg.id_matriz_curricular = mc.id_matriz_curricular and d.id_discente = iad.id_discente "
					+ "and iad.id_indice_academico = ? "
					+ "and d.id_curso = (select id_curso from discente where id_discente = ?) "
					+ "and mc.id_grau_academico = (select mc.id_grau_academico from graduacao.matriz_curricular mc " 
					+ "where mc.id_matriz_curricular = (select id_matriz_curricular from graduacao.discente_graduacao where id_discente_graduacao = ?)) "
					+ "and (? - (select ano_referencia from ensino.movimentacao_aluno where id_discente = d.id_discente and id_tipo_movimentacao_aluno = ? and (ativo or ativo is null) order by ano_referencia limit 1)) <= 5 "
					+ "and d.status in (?)", 1, idDiscente, idDiscente, CalendarUtils.getAnoAtual(), TipoMovimentacaoAluno.CONCLUSAO, StatusDiscente.CONCLUIDO);
		} else {
			return  getJdbcTemplate().queryForFloat("select "+ operacao + "(iad.valor) from discente d " +
			"	join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao ) " +
			"	join graduacao.matriz_curricular mc on (dg.id_matriz_curricular = mc.id_matriz_curricular) " +
			"	join ensino.indice_academico_discente iad on (d.id_discente = iad.id_discente) " +
			"	join curso c on (c.id_curso = d.id_curso) " +
			" where  iad.id_indice_academico = ? " +
			"	and c.id_unidade = (select id_unidade_referencia_mcn from curso, discente where curso.id_curso = discente.id_curso and discente.id_discente = ?) " +
			"	and (? - (select ano_referencia from ensino.movimentacao_aluno where id_discente = d.id_discente and id_tipo_movimentacao_aluno = ? and (ativo or ativo is null) order by ano_referencia limit 1)) <= 5 " +
			"	and d.status in (?)", 1, idDiscente, CalendarUtils.getAnoAtual(), TipoMovimentacaoAluno.CONCLUSAO, StatusDiscente.CONCLUIDO);
		}
	}

	/**
	 * Calcula o menor MC dos alunos ativos da mesma matriz curricular 
	 * curricular do aluno passado como parâmetro.
	 * @param idDiscente
	 * @return
	 */
	public double calculaMenorMc(boolean temConcluidos, int idDiscente) {
		return calculaMaiorMenorMC(temConcluidos, idDiscente, "min");
	}

	/**
	 * Retorna Índice de Eficiência em Carga Horária (IECH)
	 * 
	 * O IECH é o percentual da carga horária utilizada pelo aluno que se converteu em aprovação.
	 * São contabilizados no numerador todos os componentes curriculares em que o aluno obteve aprovação, 
	 * excluindo-se os componentes curriculares trancados, cancelados, reprovados, aproveitados e dispensados, 
	 * as atividades complementares, as atividades individuais e as atividades de orientação individual. 
	 * 
	 * São contabilizados no denominador todos os componentes curriculares em que o aluno se matriculou, 
	 * incluindo os trancamentos, reprovações e cancelamentos de matrícula e excluindo-se os componentes 
	 * curriculares aproveitados e dispensados, as atividades complementares, as atividades individuais e as 
	 * atividades de orientação individual.
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public double calculaIechDiscente(int idDiscente) throws DAOException {
		return getJdbcTemplate().queryForFloat("select ( "
				+ "select sum(ccd.ch_total) "
				+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
				+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina and cc.id_bloco_subunidade is null "
				+ "and mc.id_situacao_matricula not in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) and mc.id_componente_detalhes = ccd.id_componente_detalhes "
				+ "and (cc.id_tipo_componente != ? or (cc.id_tipo_componente = ? and ccd.ch_total > 0 and cc.id_tipo_atividade in (?, ?) "
				+ "and cc.necessitamediafinal = true)) "
				+ ") * 1.0 / ( "
				+ "select sum(ccd.ch_total) "
				+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
				+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina and cc.id_bloco_subunidade is null "
				+ "and mc.id_situacao_matricula not in (?, ?, ?, ?, ?, ?, ?, ?) and mc.id_componente_detalhes = ccd.id_componente_detalhes "
				+ "and (cc.id_tipo_componente != ? or (cc.id_tipo_componente = ? and ccd.ch_total > 0 and cc.id_tipo_atividade in (?, ?) "
				+ "and cc.necessitamediafinal = true)) "
				+ ") as iech", idDiscente, SituacaoMatricula.EM_ESPERA.getId(), SituacaoMatricula.MATRICULADO.getId(), SituacaoMatricula.CANCELADO.getId(),
				SituacaoMatricula.TRANCADO.getId(), SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId(),
				SituacaoMatricula.EXCLUIDA.getId(), SituacaoMatricula.INDEFERIDA.getId(), SituacaoMatricula.DESISTENCIA.getId(), SituacaoMatricula.APROVEITADO_DISPENSADO.getId(),
				SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(), TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.ATIVIDADE,
				TipoAtividade.ESTAGIO, TipoAtividade.TRABALHO_CONCLUSAO_CURSO, idDiscente, SituacaoMatricula.EM_ESPERA.getId(), SituacaoMatricula.MATRICULADO.getId(),
				SituacaoMatricula.EXCLUIDA.getId(), SituacaoMatricula.INDEFERIDA.getId(), SituacaoMatricula.DESISTENCIA.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getId(),
				SituacaoMatricula.APROVEITADO_DISPENSADO.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(), TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.ATIVIDADE,
				TipoAtividade.ESTAGIO, TipoAtividade.TRABALHO_CONCLUSAO_CURSO);
	}

	/**
	 * Retorna o Índice de Eficiência em Períodos Letivos (IEPL).
	 * 
	 * O IEPL é divisão da carga horária acumulada pela carga horária esperada.
	 * 
	 * @param idDiscente
	 * @return
	 */
	public double calculaIeplDiscente(int idDiscente) {
		return getJdbcTemplate().queryForFloat("select ( "
				+ "select greatest((coalesce(dg.ch_total_integralizada, 0) - coalesce(dg.ch_integralizada_aproveitamentos, 0)), 0) " 
				+ "from graduacao.discente_graduacao dg "
				+ "where dg.id_discente_graduacao = ?) * 1.0 / ( "
				+ "(select greatest(count(*), 1) as periodos_cursados "
				+ "from (select distinct mc.ano, mc.periodo from ensino.matricula_componente mc, (select distinct id_discente, ano, periodo from ensino.matricula_componente where id_discente = ? "
				+ "and periodo in (1,2) and id_situacao_matricula in (4, 5, 6, 7, 9, 21)) as p "
				+ "where mc.id_discente = p.id_discente and mc.ano = p.ano and mc.periodo = p.periodo and not exists (select * from ensino.matricula_componente where id_discente = p.id_discente and ano = p.ano and periodo = p.periodo and " 
				+ "id_situacao_matricula in (1, 2))) as q) * " 
				+ "cast(((select ch_total_minima from graduacao.curriculo g where id_curriculo = (select id_curriculo from discente where id_discente = ?)) / " 
				+ "(select semestre_conclusao_ideal from graduacao.curriculo g where id_curriculo = (select id_curriculo from discente where id_discente = ?))) "
				+ "as int)) as iepl", idDiscente, idDiscente, idDiscente, idDiscente);
	}

	/**
	 * Retorna a lista de índices acadêmicos para o discente
	 * passado como parâmetro.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<IndiceAcademicoDiscente> findIndicesAcademicoDiscente(DiscenteAdapter discente) throws DAOException {
		List<Object[]> lista = getSession().createQuery("select i.id, i.indice, i.valor from IndiceAcademicoDiscente i where i.discente.id = ? order by i.indice.ordem").setInteger(0, discente.getId()).list();
		List<IndiceAcademicoDiscente> result = new LinkedList<IndiceAcademicoDiscente>();
		for (Object[] linha : lista) {
			IndiceAcademicoDiscente iad = new IndiceAcademicoDiscente();
			iad.setId((Integer) linha[0]);
			iad.setIndice((IndiceAcademico) linha[1]);
			iad.setValor((Double) linha[2]);
			iad.setDiscente(discente.getDiscente());
			result.add(iad);
		}
		
		return result;
	}
	
	/**
	 * Retorna a lista de índices acadêmicos para o discente, que podem ser exibidos no histórico.
	 * passado como parâmetro.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<IndiceAcademicoDiscente> findIndicesAcademicoDiscenteExibiveis(Discente discente) throws DAOException {
		return getSession().createCriteria(IndiceAcademicoDiscente.class).add(eq("discente.id", discente.getId()))
			.createCriteria("indice").add(eq("ativo", true)).add(eq("exibidoHistorico", true)).addOrder(asc("ordem")).list();
	}
	
	/**
	 * Retorna o registro da última atualização de índice acadêmico do aluno.
	 * @param indice
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AtualizacaoIndiceAcademicoDiscente> findUltimasAtualizacaoIndices(Discente discente) {
		  StringBuilder sql = new StringBuilder(); 
			  sql.append(" select iad.id_discente, iad.id_indice_academico, iad.id as id_indice_academico_discente,  iad.valor, max(atualizacao.data) as max_data, max(atualizacao.id) as id_atualizacao_indice_academico_discente " +
			  		"  from ensino.indice_academico_discente iad " +
			  		"  inner join ensino.atualizacao_indice_academico_discente atualizacao ON atualizacao.id_indice_Academico_discente = iad.id" +
			  		" where iad.id_discente = ? " +
			  		" group by iad.id, iad.id_discente,iad.id_indice_academico, iad.valor order by iad.id "); 	
		 
		  return getJdbcTemplate().query(sql.toString(), new Object[]{discente.getId()}, new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						
						AtualizacaoIndiceAcademicoDiscente at = new AtualizacaoIndiceAcademicoDiscente();
						
						try {
							at.setId(rs.getInt("id_atualizacao_indice_academico_discente"));
							at.setIndice(findByPrimaryKey((rs.getInt("id_indice_academico_discente")),IndiceAcademicoDiscente.class));
							at.setData(rs.getDate("max_data"));
							at.setValor(rs.getFloat("valor"));
						} catch (DAOException e) {
							e.printStackTrace();
						}
						
						return at;
					}
				});
		  
	}

	/**
	 * Retorna a média geral de um discente.
	 *
	 * Considera todas os componentes curriculares que possuem carga horária maior que zero e necessitam de média final.
	 *
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public double calculaMediaGeralDiscente(int idDiscente) throws DAOException {
		return getJdbcTemplate().queryForFloat("select sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total) "
				+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
				+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina "
				+ "and mc.media_final is not null and cc.id_bloco_subunidade is null "
				+ "and mc.id_situacao_matricula in (?, ?, ?, ?, ?, ?) and mc.id_componente_detalhes = ccd.id_componente_detalhes "
				+ "and ccd.ch_total > 0 "
				+ "and cc.necessitamediafinal = true", idDiscente, SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.REPROVADO.getId(),
				SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), 
				SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId());
	}

	/**
	 * Calcula o maior IEA dos alunos ativos da mesma matriz curricular 
	 * curricular do aluno passado como parâmetro.
	 * @param idDiscente
	 * @return
	 */
	public double calculaMaiorIea(int idDiscente) {
		return  getJdbcTemplate().queryForFloat("select max(iad.valor) from discente d, graduacao.discente_graduacao dg, "
				+ "graduacao.matriz_curricular mc, ensino.indice_academico_discente iad " 
				+ "where d.id_discente = dg.id_discente_graduacao and dg.id_matriz_curricular = mc.id_matriz_curricular and d.id_discente = iad.id_discente "
				+ "and iad.id_indice_academico = ? "
				+ "and d.id_curso = (select id_curso from discente where id_discente = ?) "
				+ "and mc.id_grau_academico = (select mc.id_grau_academico from graduacao.matriz_curricular mc " 
				+ "where mc.id_matriz_curricular = (select id_matriz_curricular from graduacao.discente_graduacao "
				+ "where id_discente_graduacao = ?)) "
				+ "and d.status in (?,?,?) and exists (select matricula_componente from ensino.matricula_componente where id_discente = d.id_discente and id_situacao_matricula in (4, 5, 6, 7, 9, 21, 22, 23))", 
				6, idDiscente, idDiscente, StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO);
	}

	/**
	 * Calcula o menor IEA dos alunos ativos da mesma matriz curricular 
	 * curricular do aluno passado como parâmetro.
	 * @param idDiscente
	 * @return
	 */
	public double calculaMenorIea(int idDiscente) {
		return  getJdbcTemplate().queryForFloat("select min(iad.valor) from discente d, graduacao.discente_graduacao dg, "
				+ "graduacao.matriz_curricular mc, ensino.indice_academico_discente iad " 
				+ "where d.id_discente = dg.id_discente_graduacao and dg.id_matriz_curricular = mc.id_matriz_curricular and d.id_discente = iad.id_discente "
				+ "and iad.id_indice_academico = ? "
				+ "and d.id_curso = (select id_curso from discente where id_discente = ?) "
				+ "and mc.id_grau_academico = (select mc.id_grau_academico from graduacao.matriz_curricular mc " 
				+ "where mc.id_matriz_curricular = (select id_matriz_curricular from graduacao.discente_graduacao "
				+ "where id_discente_graduacao = ?)) "
				+ "and d.status in (?,?,?) and exists (select matricula_componente from ensino.matricula_componente where id_discente = d.id_discente and id_situacao_matricula in (4, 5, 6, 7, 9, 21, 22, 23))", 
				6, idDiscente, idDiscente, StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO);
	}

	/**
	 * Calcula a média de todos os IEAs dos últimos concluíntes da matriz
	 * curricular do aluno passado como parâmetro.
	 * @param anoAtual
	 * @param idDiscente
	 * @return
	 */
	public double calculaMediaIea(int anoAtual, int idDiscente) {
		return  getJdbcTemplate().queryForFloat("select avg(iad.valor) from discente d, graduacao.discente_graduacao dg, "
				+ "graduacao.matriz_curricular mc, ensino.indice_academico_discente iad " 
				+ "where d.id_discente = dg.id_discente_graduacao and dg.id_matriz_curricular = mc.id_matriz_curricular and d.id_discente = iad.id_discente "
				+ "and iad.id_indice_academico = ? "
				+ "and d.id_curso = (select id_curso from discente where id_discente = ?) "
				+ "and mc.id_grau_academico = (select mc.id_grau_academico from graduacao.matriz_curricular mc " 
				+ "where mc.id_matriz_curricular = (select id_matriz_curricular from graduacao.discente_graduacao "
				+ "where id_discente_graduacao = ?)) "
				+ "and d.status in (?,?,?) and exists (select matricula_componente from ensino.matricula_componente where id_discente = d.id_discente and id_situacao_matricula in (4, 5, 6, 7, 9, 21, 22, 23))", 
				6, idDiscente, idDiscente, StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO);
	}
	
	/**
	 * Calcula o desvio padrão de todos os IEAs dos últimos concluíntes da matriz
	 * curricular do aluno passado como parâmetro.
	 * @param anoAtual
	 * @param idDiscente
	 * @return
	 */
	public double calculaDesvioIea(int anoAtual, int idDiscente, double media) {
		return  getJdbcTemplate().queryForFloat("select stddev(iad.valor) from discente d, graduacao.discente_graduacao dg, "
				+ "graduacao.matriz_curricular mc, ensino.indice_academico_discente iad " 
				+ "where d.id_discente = dg.id_discente_graduacao and dg.id_matriz_curricular = mc.id_matriz_curricular and d.id_discente = iad.id_discente "
				+ "and iad.id_indice_academico = ? "
				+ "and d.id_curso = (select id_curso from discente where id_discente = ?) "
				+ "and mc.id_grau_academico = (select mc.id_grau_academico from graduacao.matriz_curricular mc " 
				+ "where mc.id_matriz_curricular = (select id_matriz_curricular from graduacao.discente_graduacao "
				+ "where id_discente_graduacao = ?)) "
				+ "and d.status in (?,?,?) and exists (select matricula_componente from ensino.matricula_componente where id_discente = d.id_discente and id_situacao_matricula in (4, 5, 6, 7, 9, 21, 22, 23))", 
				6, idDiscente, idDiscente, StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO);
	}

	/**
	 * Busca o valor do IEA do discente passado como parâmetro.
	 * @param id
	 * @return
	 */
	public double buscaIeaDiscente(int idDiscente) {
		return getJdbcTemplate().queryForFloat("select coalesce(valor, 0.0) from ensino.indice_academico_discente where id_discente = ? and id_indice_academico = 6", new Object[] { idDiscente });
	}

	/**
	 * Busca o valor do MC do discente passado como parâmetro.
	 * @param id
	 * @return
	 */
	public double buscaMcDiscente(int idDiscente) {
		return getJdbcTemplate().queryForFloat("select coalesce(valor, 0.0) from ensino.indice_academico_discente where id_discente = ? and id_indice_academico = 1", new Object[] { idDiscente });
	}

	/**
	 * Busca o valor do IECH do discente passado como parâmetro.
	 * @param id
	 * @return
	 */
	public double buscaIechDiscente(int idDiscente) {
		return getJdbcTemplate().queryForFloat("select coalesce(valor, 0.0) from ensino.indice_academico_discente where id_discente = ? and id_indice_academico = 4", new Object[] { idDiscente });
	}

	/**
	 * Busca o valor do IEPL do discente passado como parâmetro.
	 * @param id
	 * @return
	 */
	public double buscaIeplDiscente(int idDiscente) {
		return getJdbcTemplate().queryForFloat("select coalesce(valor, 0.0) from ensino.indice_academico_discente where id_discente = ? and id_indice_academico = 5", new Object[] { idDiscente });
	}

	/**
	 * Busca o valor do MCN do discente passado como parâmetro.
	 * @param id
	 * @return
	 */
	public double buscaMcnDiscente(int idDiscente) {
		return getJdbcTemplate().queryForFloat("select coalesce(valor, 0.0) from ensino.indice_academico_discente where id_discente = ? and id_indice_academico = 3", new Object[] { idDiscente });
	}
	
	/**
	 * Busca o valor do MCN do discente passado como parâmetro.
	 * @param id
	 * @return
	 */
	public boolean isExisteDiscenteConcluiuCurso(int idCurso) {		
		Float total = getJdbcTemplate().queryForFloat("select count(id_discente) from discente  where id_curso = ? and status = ?", new Object[] { idCurso, StatusDiscente.CONCLUIDO } );		
		if(total!= null && total > 0)
			return true;		
		return false;	
	}
	
	
	
}
