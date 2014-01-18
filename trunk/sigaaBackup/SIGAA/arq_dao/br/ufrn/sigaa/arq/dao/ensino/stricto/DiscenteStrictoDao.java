/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/10/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.Transformers;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.GrupoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DAO para consultas referentes a discentes de Stricto Sensu.
 * 
 * @author Leonardo
 * 
 */
public class DiscenteStrictoDao extends GenericSigaaDAO {

	/**
	 * Busca os discentes de pós-graduação ativos da pessoa passada por
	 * parâmetro
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteStricto> findAtivoByPessoa(int idPessoa) throws DAOException {
		try {

			Criteria c = getSession().createCriteria(DiscenteStricto.class);
			c.createAlias("discente", "d");
			c.add(Expression.eq("d.pessoa.id", idPessoa));
			
			c.add(Expression.ne("d.status", StatusDiscente.CANCELADO));
			c.add(Expression.ne("d.status", StatusDiscente.CONCLUIDO));
			c.add(Expression.ne("d.status", StatusDiscente.JUBILADO));
			c.add(Expression.ne("d.status", StatusDiscente.EXCLUIDO));
			c.add(Expression.ne("d.status", StatusDiscente.DEFENDIDO));
			c.add(Expression.ne("d.status", StatusDiscente.EM_HOMOLOGACAO));

			return c.list();

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca as informações da homologação de trabalho final de um determinado discente
	 * 
	 * @param discente
	 * @return
	 */
	public HomologacaoTrabalhoFinal findHomologacaoTrabalhoFinal(Discente discente) {
		try {
			Query q = getSession()
					.createQuery(
							"select h from HomologacaoTrabalhoFinal h left join h.banca b left join b.dadosDefesa d where d.discente.id = ?");
			q.setInteger(0, discente.getId());
			q.setMaxResults(1);
			return (HomologacaoTrabalhoFinal) q.uniqueResult();
		} catch (DAOException e) {
			throw new HibernateException(e.getCause());
		}
	}

	/**
	 * Atualiza os totais de integralização de um determinado discente 
	 * ATENÇÃO: Deve ser chamado somente a partir de um processador!
	 * 
	 * @param d
	 */
	public void atualizaTotaisIntegralizados(DiscenteStricto d) {
		update("update stricto_sensu.discente_stricto set cr_total_obrigatorio_integralizado=?, cr_total_integralizado=?, ch_total_integralizada=?, ch_obrigatoria_integralizada=?,ch_optativa_integralizada=? where id_discente=?",
				new Object[] {
					d.getCrTotaisObrigatoriosIntegralizado(),
					d.getCrTotaisIntegralizados(), d.getChTotalIntegralizada(), d.getChObrigatoriaIntegralizada(), d.getChOptativaIntegralizada(), d.getId() } );
	}

	/**
	 * Calcula os totais de créditos integralizados no histórico de
	 * matrículas de um discente 
	 * 
	 * @param d
	 * @return
	 */
	public int calculaCrTotaisIntegralizados(DiscenteStricto d) {
		ParametrosGestoraAcademica parametros = new ParametrosGestoraAcademica();
		try {
			parametros = ParametrosGestoraAcademicaHelper.getParametros(d);
		} catch (DAOException e) {
			e.printStackTrace();
		}
		String sql = "select coalesce(sum(ccd.ch_total), 0) from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
				+ "where mc.id_discente=? and mc.id_componente_curricular = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes "
				+ "and mc.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesPagas());
		int total = getJdbcTemplate().queryForInt(sql,
				new Object[] { d.getId() }) / parametros.getHorasCreditosAula();

		int aproveitados = getJdbcTemplate()
				.queryForInt(
						"select coalesce(sum(creditos), 0) from stricto_sensu.aproveitamento_credito where id_discente=? and ativo = trueValue()",
						new Object[] { d.getId() });
		return total + aproveitados;
	}

	/**
	 * Calcula o total de créditos exigidos pelo currículo
	 * de um discente
	 * 
	 * @param d
	 * @return
	 */
	public int calculaCrTotalCurriculo(DiscenteStricto d) {
		try {
			String sql = "select coalesce(cr_total_minimo, 0) from graduacao.curriculo where id_curriculo = (select id_curriculo from discente where id_discente=?)";
			return getJdbcTemplate().queryForInt(sql,
					new Object[] { d.getId() });
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	/**
	 * Retorna todos os discentes com os parâmetros informados e que não
	 * pertencem ao programa informado
	 * 
	 * @param cpf
	 * @param matricula
	 * @param nome
	 * @param nomeCurso
	 * @param cursos
	 * @param statusValidos
	 * @param tiposValidos
	 * @param programa
	 *            Não retorna discentes deste programa
	 * @param nivel
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteStricto> findDiscenteOutroPrograma(
			Long matricula, String nome, Collection<Integer> status,
			int programa) throws DAOException {

		StringBuffer hql = new StringBuffer(
				"SELECT d FROM DiscenteStricto d, Pessoa p ");
		hql.append(" WHERE d.pessoa.id= p.id ");
		hql.append(" AND d.gestoraAcademica != :programa ");
		hql.append(" AND d.status in " + gerarStringIn(status));
		if (matricula != null)
			hql.append(" AND d.matricula = :matricula ");
		if (nome != null)
			hql.append(" AND upper(p.nomeAscii) like :nome");

		hql.append(" ORDER BY d.curso.id, p.nome");

		Query q = getSession().createQuery(hql.toString());
		if (matricula != null)
			q.setLong("matricula", matricula);
		q.setInteger("programa", programa);
		if (nome != null)
			q.setString("nome", "%" + StringUtils.toAscii(nome.toUpperCase().trim()) + "%");
		return q.list();
	}

	
	/**
	 * 
	 * Retorna todos os discentes por linha de pesquisa
	 * 
	 * @param linha
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteStricto> findDiscentesPorLinhaPesquisaByPrograma(int programa)throws DAOException {
		String sql = "select d.matricula, p.nome, p.email, lp.denominacao, lp.id_linha_pesquisa, a.nivel, a.id_area_concentracao "
			+ "from discente d, comum.pessoa p, stricto_sensu.area_concentracao a, stricto_sensu.discente_stricto ds "
			+ "left outer join stricto_sensu.linha_pesquisa_stricto lp on (ds.id_linha_pesquisa = lp.id_linha_pesquisa) "
			+ "where d.id_discente = ds.id_discente and d.id_gestora_academica = ? "
			+ "and d.status in (?, ?, ?) and d.id_pessoa = p.id_pessoa and a.id_area_concentracao = lp.id_area and ds.id_linha_pesquisa isnull = falseValue() "
			+ "order by lp.denominacao, a.nivel, ds.id_linha_pesquisa, p.nome ";
	
		return getJdbcTemplate().query(sql, new Object[] { programa, StatusDiscente.ATIVO, StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.DEFENDIDO }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				DiscenteStricto ds = new DiscenteStricto();
					
					ds.setLinha(new LinhaPesquisaStricto());
					ds.getLinha().setDenominacao (rs.getString("denominacao"));
					ds.getLinha().setId(rs.getInt("id_linha_pesquisa"));
					ds.setMatricula(rs.getLong("matricula"));
					ds.getPessoa().setNome(rs.getString("nome"));
					ds.getPessoa().setEmail(rs.getString("email"));
					ds.setArea(new AreaConcentracao());
					ds.getArea().setNivel(rs.getString("nivel").charAt(0));
						
				return ds;	
				}
			}
		);
		
	}
	
	/**
	 * 
	 * Retorna todos os discentes por programa
	 * 
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteStricto> findDiscentesByPrograma(int programa)throws DAOException {
		String sql = "select distinct d.matricula , p.nome, p.email " +
		"from discente d, comum.pessoa p, stricto_sensu.discente_stricto ds " + 
		"where d.id_discente = ds.id_discente and d.id_gestora_academica = ? " + 
		"and d.status in (?, ?, ?) and d.id_pessoa = p.id_pessoa " +
		"order by p.nome";
			
		return getJdbcTemplate().query(sql, new Object[] { programa, StatusDiscente.ATIVO, StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.DEFENDIDO }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				DiscenteStricto ds = new DiscenteStricto();
				ds.setDiscente(new Discente());
				ds.setMatricula(rs.getLong("matricula"));
				ds.getPessoa().setNome(rs.getString("nome"));
				ds.getPessoa().setEmail(rs.getString("email"));
						
				return ds;	
				}
			}
		);
		
	}
	
	/**
	 * 
	 * Retorna todos os discentes por programa para geração do relatório de discentes por linha de pesquisa.
	 * 
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteStricto> findDiscentesByProgramaParaRelatorio(int programa)throws DAOException {
		String sql = "select distinct d.id_discente, d.matricula, p.nome, p.email, lp.denominacao, lp.id_linha_pesquisa, a.nivel " +
		"from discente d " +
			"inner join comum.pessoa p using (id_pessoa) " +
			"inner join stricto_sensu.discente_stricto ds on (ds.id_discente = d.id_discente) " +
			"left outer join stricto_sensu.linha_pesquisa_stricto lp on (ds.id_linha_pesquisa = lp.id_linha_pesquisa) " +
			"left outer join stricto_sensu.area_concentracao a on (a.id_area_concentracao = lp.id_area) " +
		"where d.id_gestora_academica = ? " + 
		"and d.status in (?, ?, ?) and ds.id_linha_pesquisa isnull = falseValue() " +
		"order by lp.denominacao, a.nivel, lp.id_linha_pesquisa, p.nome";
			
		return getJdbcTemplate().query(sql, new Object[] { programa, StatusDiscente.ATIVO, StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.DEFENDIDO }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				DiscenteStricto ds = new DiscenteStricto();
				ds.setDiscente(new Discente());
				ds.setLinha(new LinhaPesquisaStricto());
				ds.getLinha().setDenominacao (rs.getString("denominacao"));
				ds.getLinha().setId(rs.getInt("id_linha_pesquisa"));
				ds.setArea(new AreaConcentracao());
				String nivel = rs.getString("nivel");
				if(nivel == null || nivel.trim().equals(""))
					nivel = " ";
				ds.getArea().setNivel(nivel.charAt(0));
				ds.setMatricula(rs.getLong("matricula"));
				ds.getPessoa().setNome(rs.getString("nome"));
				ds.getPessoa().setEmail(rs.getString("email"));
						
				return ds;	
				}
			}
		);
		
	}
	
	/**
	 * 
	 * Retorna todos os ativos discentes por programa
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteStricto> findAtivosByPrograma(int programa, int tipoDiscente)throws DAOException {
		String sql = "select distinct d.id_discente, d.matricula , p.nome, p.email, d.status, d.ano_ingresso, ds.mes_entrada, d.nivel, "+
		" d.periodo_ingresso, curr.id_curriculo, curr.meses_conclusao_ideal, c.id_curso, tc.id_tipo_curso_stricto, ds.prazo_maximo_conclusao,  ds.mes_atual, ds.variacao_prazo " +		
		" from discente d  " + 
		" inner join comum.pessoa p using (id_pessoa) "+
		" inner join stricto_sensu.discente_stricto ds using (id_discente) "+
		" left join graduacao.curriculo curr using (id_curriculo) "+
		" left join public.curso c on c.id_curso = curr.id_curso  "+
		" left join stricto_sensu.tipo_curso_stricto tc on tc.id_tipo_curso_stricto = c.id_tipo_curso_stricto ";
		if (programa > 0)
			sql += " where d.id_gestora_academica = " + programa;
		
		sql += " and d.status = ? " +
		(tipoDiscente > 0 ? "and d.tipo = "+tipoDiscente : "")+
		" order by p.nome ";
			
		return getJdbcTemplate().query(sql, new Object[] { StatusDiscente.ATIVO }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				DiscenteStricto ds = new DiscenteStricto();
				ds.setDiscente(new Discente());
				ds.setId(rs.getInt("id_discente"));
				ds.setMatricula(rs.getLong("matricula"));
				ds.getPessoa().setNome(rs.getString("nome"));
				ds.getPessoa().setEmail(rs.getString("email"));
				ds.setStatus(rs.getInt("status"));
				ds.setAnoIngresso(rs.getInt("ano_ingresso"));
				ds.setMesEntrada(rs.getInt("mes_entrada"));
				ds.setPeriodoIngresso(rs.getInt("periodo_ingresso"));
				ds.setNivel(rs.getString("nivel").toCharArray()[0]);
				if (rs.getObject("id_curriculo") != null){
					ds.setCurriculo(new Curriculo(rs.getInt("id_curriculo")));
					ds.getCurriculo().setMesesConclusaoIdeal(rs.getInt("meses_conclusao_ideal"));
					ds.getCurriculo().setCurso(new Curso(rs.getInt("id_curso")));
					ds.getCurriculo().getCurso().setTipoCursoStricto(new TipoCursoStricto(rs.getInt("id_tipo_curso_stricto")));
				}
				ds.setPrazoMaximoConclusao(rs.getDate("prazo_maximo_conclusao"));
				ds.setMesAtual(rs.getInt("mes_atual"));
				ds.setVariacaoPrazo(rs.getInt("variacao_prazo"));						
				return ds;	
				}
			}
		);		
	}	
	
	/**
	 * Consulta usada para trazer todos os discente com o prazo máximo de conclusão de um terminado programa.
	 * Usado no Relatório de Discentes Ativos e Prazo máximo de conclusão.
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findPrazoMaximoConclusaoByPrograma(int programa, int tipo)throws DAOException {
		String sql = 
		"	select u.id_unidade, u.nome as programa, d.nivel, c.id_curso, "+
		"       c.nome || ' - ' || (case when c.nivel ='"+NivelEnsino.MESTRADO+"' then '"+NivelEnsino.getDescricao(NivelEnsino.MESTRADO)+"'"+
		"       when c.nivel = '"+NivelEnsino.DOUTORADO+"' then '"+NivelEnsino.getDescricao(NivelEnsino.DOUTORADO)+"' end) as curso_nome, "+
		"       d.id_discente, d.tipo as tipo_discente, cast(d.matricula as varchar), p.nome as nome_discente,  "+
		"       d.ano_ingresso, coalesce(ds.mes_entrada, case when d.periodo_ingresso = 1 then 1 else 7 end, 1) as mes_ingresso, "+
	    " case when d.tipo = "+Discente.ESPECIAL+" then null else ds.prazo_maximo_conclusao end as data_prazo_maximo, ds.mes_atual, "+
		"       case  "+
		"         when d.tipo = "+Discente.ESPECIAL+" then 0 "+ 
		"       else  "+
		"         coalesce(curr.meses_conclusao_ideal, case when d.nivel = '"+NivelEnsino.DOUTORADO+"' then 12 * 5 when d.nivel = '"+NivelEnsino.MESTRADO+"' then 12 * 3 end, 12 * 3) end as prazo_maximo_curriculo, "+
		"       case  "+
		"         when d.tipo = "+Discente.ESPECIAL+" then null "+ 
		"       else  "+
		"         ds.variacao_prazo + coalesce(curr.meses_conclusao_ideal, case when d.nivel = '"+NivelEnsino.DOUTORADO+"' then 12 * 5 when d.nivel = '"+NivelEnsino.MESTRADO+"' then 12 * 3 end, 12 * 3) end as prazo_maximo, "+		
		"       cast(sum(case when ma.id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.TRANCAMENTO+" then valor_movimentacao else 0 end) as integer) as trancamentos, "+
		"       cast(sum(case when ma.id_tipo_movimentacao_aluno in "+
									UFRNUtils.gerarStringIn(new int[]{TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA, 
											TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL, 
											TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA})
											+" then valor_movimentacao else 0 end) as integer) as prorrogacoes "+
		" from discente d "+ 
		"    left join curso c using (id_curso) "+
		"    left join comum.unidade u on (u.id_unidade = d.id_gestora_academica) "+
		"    inner join comum.pessoa p using (id_pessoa) "+
		"    left join stricto_sensu.discente_stricto ds using (id_discente) "+
		"    left join lato_sensu.discente_lato dl using (id_discente) "+
		"    left join graduacao.curriculo curr using (id_curriculo) "+
		"    left join ensino.movimentacao_aluno ma on (d.id_discente = ma.id_discente and ma.id_tipo_movimentacao_aluno in "+
									UFRNUtils.gerarStringIn(new int[]{TipoMovimentacaoAluno.TRANCAMENTO,
											TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA, 
											TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL, 
											TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA})+" and ma.ativo = true) "+
		" where d.nivel in ('"+NivelEnsino.MESTRADO+"','"+NivelEnsino.DOUTORADO+"','"+NivelEnsino.STRICTO+"') "+
		" and d.status = "+StatusDiscente.ATIVO+
		(programa > 0 ? " and u.id_unidade= "+programa : "")+
		(tipo > 0 ? " and d.tipo = "+tipo : "")+
		" group by u.id_unidade, u.nome, d.nivel, c.id_curso, curso_nome, d.id_discente, d.tipo, d.matricula, p.nome, "+ 
		" d.ano_ingresso, mes_ingresso, ds.prazo_maximo_conclusao, ds.mes_atual, ds.variacao_prazo, prazo_maximo_curriculo, curr.meses_conclusao_ideal "+
		" order by u.nome, curso_nome, nome_discente ";			
		
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();

		try {
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.list();
			resultado = lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return resultado;					
	}	
	
	
	/**
	 * Retorna o relatório de quantitativos de alunos da pós-graduação, ativos e matriculados por mês.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoAlunosMatriculadosMes(int ano) throws DAOException{
		String sql = 		
		" select count(id_discente) as total, mes from (select d.id_discente, "
		+"		CASE min(mc.mes) "
		+"			WHEN 0 THEN  min(cast( extract(month from mc.data_cadastro) as int)) "
		+"			ELSE coalesce(min(mc.mes), min(cast( extract(month from mc.data_cadastro) as int)) )  "
		+"		END as mes 	"	
		+"	from discente d  "
		+"	inner join ensino.matricula_componente mc using(id_discente) "
		+"	inner join ensino.situacao_matricula sit using(id_situacao_matricula) "		
		+"	where mc.ano = "+ano     
		+"	and d.nivel in "+ gerarStringIn(NivelEnsino.getNiveisStricto())   
		+"	and d.status <> "+ StatusDiscente.EXCLUIDO   
		+"	and mc.id_situacao_matricula <> "+ SituacaoMatricula.EXCLUIDA.getId() 
		+"	group by d.id_discente 	) as total group by mes order by mes ";
		
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();

		try {
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.list();
			resultado = lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return resultado;
	}	
	
	/**
	 * Retorna os alunos da pós-graduação com vinculo acadêmico(matricula ou renovação de matrícula) por ano.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findAlunosVinculadosByAno(int ano) throws DAOException{
		String sql = 		
	
		" select d.id_discente as idDiscente, d.status, tmov.grupo, mov.data_retorno as dataRetorno, mov.id_movimentacao_aluno as idMovimentacaoAluno	"
		+", d.ano_ingresso||''||ds.mes_entrada as dataIngresso	"
		+", extract(year from max(mov.data_ocorrencia)) ||''|| extract(month from max(mov.data_ocorrencia)) as dataOcorrenciaMovimentacao	"
		+", CASE "
		+"		WHEN every(tmov.grupo IN ('"+GrupoMovimentacaoAluno.AFASTAMENTO_TEMPORARIO+"', '"+GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE+"') AND mov.data_retorno is null  )	" 
		+"		THEN extract(year from mov.data_ocorrencia) ||''|| extract(month from mov.data_ocorrencia) ELSE null	"
		+"	END as dataSaida	"
		+" 	from discente d	"
		+" 	inner join stricto_sensu.discente_stricto ds using(id_discente)	"
		+"	inner join ensino.matricula_componente mc using(id_discente)	"
		+"	left join stricto_sensu.renovacao_atividade_pos renov using(id_matricula_componente)	"
		+"	inner join ensino.situacao_matricula sit using(id_situacao_matricula)	"
		+"	left join ensino.movimentacao_aluno mov on (mov.id_discente = d.id_discente) "
		+"	left join ensino.tipo_movimentacao_aluno tmov using(id_tipo_movimentacao_aluno)	"
		+"	where d.nivel in "+ gerarStringIn(NivelEnsino.getNiveisStricto())     
		+"	and (mc.ano = "+ano+" or renov.ano = "+ano+")	"
		+"	group by d.id_discente, d.status, d.ano_ingresso, ds.mes_entrada, tmov.grupo, mov.data_retorno	" 
		+"		, mov.data_ocorrencia, mov.id_movimentacao_aluno	"
		+"	order by d.id_discente	";
		
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();

		try {
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.list();
			resultado = lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return resultado;
	}	

	/**
	 * Retorna a listagem de todos os alunos com o prazo máximo de conclusão espirado ou faltando menos de 1 semestre para espirar.
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findDiscentesPrazoMaximoEstourado() throws DAOException{
		String sql = 	
		"	select id_unidade, programa, id_discente, "
		+"       matricula, nome, email,"
		+"      case when tipo_discente = " + Discente.ESPECIAL + " then null else prazo_maximo_curriculo end as prazo_maximo_curriculo, mes_atual,"
		+"     case when tipo_discente = " + Discente.ESPECIAL + " then null"
		+"     else to_date('01/'||mes_ingresso||'/'||ano_ingresso,'DD/MM/YYYY')" 
		+"          + cast(prazo_maximo_curriculo"
		+"          + sum(case when tipo_movimento = "+TipoMovimentacaoAluno.TRANCAMENTO+" then valor_movimentacao else 0 end)" 
		+"          + sum(case when tipo_movimento in ("+TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA+", "+
			                            TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL+", "+
			                            TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA+") then valor_movimentacao else 0 end)||' month' as interval) end as data_prazo_maximo"
		+" from ("
		+"    select u.id_unidade, u.nome as programa, d.id_discente,"
		+"   d.tipo as tipo_discente, d.matricula, p.nome, p.email,"
		+"   ma.id_tipo_movimentacao_aluno as tipo_movimento, ma.id_movimentacao_aluno,"
		+"   ma.valor_movimentacao, d.ano_ingresso,"
		+"   coalesce(ds.mes_entrada, case when d.periodo_ingresso = 1 then 1 else 7 end, 1) as mes_ingresso,"
		+"   case when d.tipo = " + Discente.ESPECIAL + " then 0" 
		+"        else coalesce(curr.meses_conclusao_ideal," 
		+"             case when c.nivel = '"+NivelEnsino.DOUTORADO+"' then 12 * 5" 
		+"                  when c.nivel = '"+NivelEnsino.MESTRADO+"' then 12 * 3" 
		+"             end, 12 * 3) end as prazo_maximo_curriculo,"
		+"  cast( ((extract('year' from current_date) - d.ano_ingresso) * 12 + (extract('month' from current_date) -" 
		+"         coalesce(ds.mes_entrada, case when d.periodo_ingresso = 1 then 1 else 7 end, 1))"        
		+"         - case when ma.id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.TRANCAMENTO+" then ma.id_tipo_movimentacao_aluno else 0 end"           
		+"         - case when ma.id_tipo_movimentacao_aluno in ("+TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA+", "+
			                            TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL+", "+
			                            TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA+") then ma.id_tipo_movimentacao_aluno else 0 end) + 1 as int) as mes_atual"         
		+"   from discente d" 
		+"   left join curso c using (id_curso)"
		+"   left join comum.unidade u on (u.id_unidade = d.id_gestora_academica)"
		+"   inner join comum.pessoa p using (id_pessoa)"
		+"   left join stricto_sensu.discente_stricto ds using (id_discente)"
		+"   left join lato_sensu.discente_lato dl using (id_discente)"
		+"   left join graduacao.curriculo curr using (id_curriculo)"
		+"   left join ensino.movimentacao_aluno ma on (d.id_discente = ma.id_discente and ma.id_tipo_movimentacao_aluno in ("+TipoMovimentacaoAluno.TRANCAMENTO+", "+
			                            TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA+", "+
			                            TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL+", "+
			                            TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA+") and ma.ativo = true)"
		+"   where d.nivel in ('"+NivelEnsino.DOUTORADO+"','"+NivelEnsino.MESTRADO+"', '"+NivelEnsino.STRICTO+"')"
		+"     and d.status="+StatusDiscente.ATIVO
		+") as sub_consulta"
		+" where (prazo_maximo_curriculo <> 0) and ((prazo_maximo_curriculo - mes_atual <= 0) or (prazo_maximo_curriculo - mes_atual between 1 and 6))"
		+" group by id_unidade,programa, id_discente,tipo_discente,"
		+"         matricula,nome,email,ano_ingresso,mes_ingresso,prazo_maximo_curriculo, mes_atual"
		+" order by programa, data_prazo_maximo, nome";
			
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();

		try {
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.list();
			resultado = lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return resultado;
	}

	/**
	 * Retorna um conjunto com os ids dos componentes do currículo
	 * @param discente
	 * @param obrigatorios Se os componentes são obrigatórios ou não no currículo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TreeSet<Integer> findComponentesDoCurriculoByDiscente(DiscenteStricto discente, boolean obrigatorios) {
		String sql = "select cc.id_disciplina from graduacao.curriculo_componente cu, ensino.componente_curricular cc, discente d "
			+ "where cu.id_curriculo = d.id_curriculo and d.id_discente = ? and "
			+ "cu.obrigatoria = ? and cu.id_componente_curricular = cc.id_disciplina ";
		return (TreeSet<Integer>) getJdbcTemplate().query(sql, new Object[] { discente.getId(), obrigatorios }, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				TreeSet<Integer> componentes = new TreeSet<Integer>();
				while (rs.next())
					componentes.add(rs.getInt("id_disciplina"));
				return componentes;
			}
		});
	}	
	
	/**
	 * Retorna a quantidade de discentes regulares, oriundos de um determinado processo seletivo.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public long findQuantidateDiscentesByProcessoSeletivo(int idProcessoSeletivo, int tipo) throws DAOException{
		String sql = 		
		" select count(d.id_discente) "
		+"	from ensino.processo_seletivo ps "
		+"		inner join ensino.inscricao_selecao i using (id_processo_seletivo) "           
		+"      inner join ensino.pessoa_inscricao pi on pi.id = i.id_pessoa_inscricao "
		+"      inner join ensino.edital_processo_seletivo eps on eps.id_edital_processo_seletivo = ps.id_edital_processo_seletivo "
		+"      inner join comum.pessoa p on p.cpf_cnpj = pi.cpf "
		+"		inner join discente d on d.id_pessoa = p.id_pessoa "
		+"		inner join stricto_sensu.discente_stricto ds on ds.id_discente = d.id_discente "
		+"	where ds.id_processo_seletivo = ps.id_processo_seletivo " 
		+"   and ds.id_processo_seletivo = ps.id_processo_seletivo "	
		+"   and d.tipo = :tipo "
		+"   and ps.id_processo_seletivo = :idProcessoSeletivo";
		
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo",idProcessoSeletivo);
		q.setInteger("tipo", tipo);
		return ((BigInteger) q.uniqueResult()).longValue();
	}
}