/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/04/2008
 * 
 */
package br.ufrn.sigaa.arq.dao.avaliacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.TabelaCSV;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoDocenteTurmaInvalida;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.DadosBrutosAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.MediaNotas;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.PercentualSimNao;
import br.ufrn.sigaa.avaliacao.dominio.Pergunta;
import br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacaoDocente;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoResposta;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoTurma;
import br.ufrn.sigaa.avaliacao.dominio.TabelaRespostaResultadoAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.TipoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.TipoPergunta;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para buscas relacionadas à avaliação institucional
 * da docência pelos discentes e auto-avaliação dos docentes.
 *
 * @author David Pereira
 *
 */
public class AvaliacaoInstitucionalDao extends GenericSigaaDAO {

	/**
	 * Busca os vínculos DocenteTurma das turmas que possuem apenas um docente com o discente, ano, período informado
	 *
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> buscarTurmasComApenasUmDocenteNoSemestrePorDiscente(Discente discente, int ano, int periodo) throws DAOException {
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession()
				.createQuery("select dts.id, t.id, di.id, di.codigo, di.excluirAvaliacaoInstitucional, d.id, d.siape, p1.id, p1.nome, de.id, i.sigla, i.nome, p2.id, p2.nome "
						+ "from MatriculaComponente mc left join mc.turma t left join t.docentesTurmas dts left join dts.turma t left join t.disciplina di  "
						+ "left join dts.docente d left join dts.docenteExterno de left join de.instituicao i left join d.pessoa p1 left join de.pessoa p2 "
						+ "where mc.discente.id = ? and t.situacaoTurma.id in " + UFRNUtils.gerarStringIn(SituacaoTurma.getSituacoesValidas())
						+ " and mc.situacaoMatricula.id in "
						+ UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido())
						+ " and size(t.docentesTurmas) = 1 and mc.ano = ? and mc.periodo = ?"
						+ " and (di.excluirAvaliacaoInstitucional is null or di.excluirAvaliacaoInstitucional = false)")
				.setInteger(0, discente.getId())
				.setInteger(1, ano)
				.setInteger(2, periodo)
				.list();
		
		List<DocenteTurma> result = new LinkedList<DocenteTurma>();
		for (Object[] linha : lista) {
			int i = -1;
			DocenteTurma dt = new DocenteTurma();
			dt.setId((Integer) linha[++i]);
			dt.setTurma(new Turma((Integer) linha[++i]));
			dt.getTurma().setDisciplina(new ComponenteCurricular((Integer) linha[++i]));
			dt.getTurma().getDisciplina().setCodigo((String) linha[++i]);
			dt.getTurma().getDisciplina().setExcluirAvaliacaoInstitucional((Boolean) linha[++i]);
			if (linha[++i] != null) dt.setDocente(new Servidor((Integer) linha[i]));
			if (linha[++i] != null) dt.getDocente().setSiape((Integer) linha[i]);
			if (linha[++i] != null) dt.getDocente().setPessoa(new Pessoa((Integer) linha[i]));
			if (linha[++i] != null) dt.getDocente().getPessoa().setNome((String) linha[i]);
			if (linha[++i] != null) dt.setDocenteExterno(new DocenteExterno((Integer) linha[i]));
			if (linha[++i] != null) dt.getDocenteExterno().setInstituicao(new InstituicoesEnsino(0, (String) linha[i], null));
			if (linha[++i] != null) dt.getDocenteExterno().getInstituicao().setSigla((String) linha[i]);
			if (linha[++i] != null) dt.getDocenteExterno().setPessoa(new Pessoa((Integer) linha[i]));
			if (linha[++i] != null) dt.getDocenteExterno().getPessoa().setNome((String) linha[i]);
			result.add(dt);
		}
		return result;
	}

	/**
	 * Busca os vínculos DocenteTurma das turmas que possuem mais de um docente com o discente, ano, período informado
	 *
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> buscarTurmasComMaisDeUmDocenteNoSemestrePorDiscente(Discente discente, int ano, int periodo) throws DAOException {
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession()
				.createQuery("select distinct t.id, di.id, di.codigo, di.excluirAvaliacaoInstitucional, det.nome, dts.id, d.id, d.siape, p1.id, p1.nome, "
						+ "de.id, i.nome, i.sigla, p2.id, p2.nome "
						+ "from MatriculaComponente mc left join mc.turma t left join t.disciplina di left join di.detalhes det left join t.docentesTurmas dts "
						+ "left join dts.docente d left join dts.docenteExterno de left join de.instituicao i left join d.pessoa p1 left join de.pessoa p2 "
						+ "where mc.discente.id = ? and t.situacaoTurma.id in " + UFRNUtils.gerarStringIn(SituacaoTurma.getSituacoesValidas())
						+ " and mc.situacaoMatricula.id in "
						+ UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido())
						+ " and size(t.docentesTurmas) > 1 and mc.ano = ? and mc.periodo = ?"
						+ " and (di.excluirAvaliacaoInstitucional is null or di.excluirAvaliacaoInstitucional = false)")
				.setInteger(0, discente.getId())
				.setInteger(1, ano)
				.setInteger(2, periodo)
				.list();
		
		List<Turma> result = new LinkedList<Turma>();
		for (Object[] linha : lista) {
			int i = -1;
			Turma t = new Turma();
			t.setId((Integer) linha[++i]);
			
			if (result.contains(t)) {
				t = result.get(result.indexOf(t));
				i += 4;
			} else {
				t.setDisciplina(new ComponenteCurricular((Integer) linha[++i]));
				t.getDisciplina().setCodigo((String) linha[++i]);
				t.getDisciplina().setExcluirAvaliacaoInstitucional((Boolean) linha[++i]);
				t.getDisciplina().setDetalhes(new ComponenteDetalhes());
				t.getDisciplina().setNome((String) linha[++i]);
				t.setDocentesTurmas(new HashSet<DocenteTurma>());
				result.add(t);
			}
			
			DocenteTurma dt = new DocenteTurma();
			dt.setId((Integer) linha[++i]);
			if (linha[++i] != null) dt.setDocente(new Servidor((Integer) linha[i]));
			if (linha[++i] != null) dt.getDocente().setSiape((Integer) linha[i]);
			if (linha[++i] != null) dt.getDocente().setPessoa(new Pessoa((Integer) linha[i]));
			if (linha[++i] != null) dt.getDocente().getPessoa().setNome((String) linha[i]);
			if (linha[++i] != null) dt.setDocenteExterno(new DocenteExterno((Integer) linha[i]));
			if (linha[++i] != null) dt.getDocenteExterno().setInstituicao(new InstituicoesEnsino(0, (String) linha[i], null));
			if (linha[++i] != null) dt.getDocenteExterno().getInstituicao().setSigla((String) linha[i]);
			if (linha[++i] != null) dt.getDocenteExterno().setPessoa(new Pessoa((Integer) linha[i]));
			if (linha[++i] != null) dt.getDocenteExterno().getPessoa().setNome((String) linha[i]);
			dt.setTurma(t);
			t.getDocentesTurmas().add(dt);
		}
		return result;
	}
	
	/**
	 * Busca as turmas que o discente informado trancou no ano.período informado
	 *
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findTrancamentosSemestreByDiscente(Discente discente, int ano, int periodo) throws DAOException {
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession()
				.createQuery("select t.id, di.id, di.codigo, det.nome from MatriculaComponente mc left join  mc.turma t left join t.disciplina di left join di.detalhes det "
						+ "where mc.discente.id = ? and mc.situacaoMatricula.id = " + SituacaoMatricula.TRANCADO.getId()
						+ " and mc.ano = ? and mc.periodo = ? and t.id is not null"
						+ " and (di.excluirAvaliacaoInstitucional is null or di.excluirAvaliacaoInstitucional = false)")
				.setInteger(0, discente.getId())
				.setInteger(1, ano)
				.setInteger(2, periodo)
				.list();
		
		List<Turma> result = new LinkedList<Turma>();
		for (Object[] linha : lista) {
			 Turma t = new Turma();
			 t.setId((Integer) linha[0]);
			 t.setDisciplina(new ComponenteCurricular((Integer) linha[1]));
			 t.getDisciplina().setCodigo((String) linha[2]);
			 t.getDisciplina().setDetalhes(new ComponenteDetalhes());
			 t.getDisciplina().getDetalhes().setNome((String) linha[3]);
			 result.add(t);
		}
		
		return result;
	}

	/**
	 * Busca as turmas que o discente informado trancou no ano.período informado com projeção, trazendo
	 * apenas id e código da disciplina.
	 *
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findTrancamentosSemestreByDiscente2(Discente discente, int ano, int periodo) throws DAOException {
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession()
				.createQuery("select t.id, t.disciplina.codigo from MatriculaComponente mc left join  mc.turma t "
						+ "where mc.discente.id = ? and mc.situacaoMatricula.id = " + SituacaoMatricula.TRANCADO.getId()
						+ " and mc.ano = ? and mc.periodo = ? and t is not null"
						+ " and (di.excluirAvaliacaoInstitucional is null or di.excluirAvaliacaoInstitucional = false)")
				.setInteger(0, discente.getId())
				.setInteger(1, ano)
				.setInteger(2, periodo)
				.list();

		List<Turma> turmas = new ArrayList<Turma>();

		for (Object[] obj : list) {
			Turma turma = new Turma();
			turma.setId((Integer) obj[0]);
			turma.setDisciplina(new ComponenteCurricular(0, (String) obj[1], ""));
			turmas.add(turma);
		}
		return turmas;
	}
	

	/** Retorna uma coleção de perguntas de acordo com o tipo especificado.
	 * @param tipoPergunta
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Pergunta> findPerguntasByTipo(TipoPergunta tipoPergunta, int idGrupo, boolean discente) throws HibernateException, DAOException{
		Criteria c = getSession().createCriteria(Pergunta.class).add(Restrictions.eq("tipoPergunta", tipoPergunta))
		.add(Restrictions.eq("ativa", true)).addOrder(Order.asc("id"));
		Criteria cGrupo = c.createCriteria("grupo").add(Restrictions.eq("ativa", true))
			.add(Restrictions.eq("avaliaTurmas", true))
			.add(Restrictions.eq("discente", discente))
			.addOrder(Order.asc("titulo"));
		if (idGrupo > 0) 
			cGrupo.add(Restrictions.eq("id", idGrupo));
		c.addOrder(Order.asc("ordem"));
		@SuppressWarnings("unchecked")
		Collection<Pergunta> lista  = c.list();

		return lista;
	}
	
	/**
	 * Retorna a Avaliação Institucional realizada pelo docente.
	 * 
	 * @param docente
	 * @param ano
	 * @param periodo
	 * @param formulario
	 * @return
	 * @throws DAOException
	 */
	public AvaliacaoInstitucional findByDocente(Servidor docente, int ano, int periodo, FormularioAvaliacaoInstitucional formulario) throws DAOException {
		if (docente == null)
			return null;
		StringBuilder hql = new StringBuilder("select av" +
				" from AvaliacaoInstitucional av" +
				" left join fetch av.respostas r" +
				" left join fetch r.docenteTurma dt" +
				" left join fetch dt.turma t" +
				" where av.servidor.id = ?" +
				" and av.ano = ?" +
				" and av.periodo = ?");
		if (formulario != null)
			hql.append(" and av.formulario.id = :idFormulario");
		else
			hql.append(" and av.formulario is null");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger(0, docente.getId())
		.setInteger(1, ano)
		.setInteger(2, periodo);
		if (formulario != null)
			q.setInteger("idFormulario", formulario.getId());
		return (AvaliacaoInstitucional) q.setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Retorna a Avaliação Institucional realizada pelo docente externo.
	 * 
	 * @param docente
	 * @param ano
	 * @param periodo
	 * @param formulario
	 * @return
	 * @throws DAOException
	 */
	public AvaliacaoInstitucional findByDocenteExterno(DocenteExterno docente, int ano, int periodo, FormularioAvaliacaoInstitucional formulario) throws DAOException {
		if (docente == null)
			return null;
		StringBuilder hql = new StringBuilder("select av" +
				" from AvaliacaoInstitucional av" +
				" left join fetch av.respostas r" +
				" left join fetch r.docenteTurma dt" +
				" left join fetch dt.turma t" +
				" where av.docenteExterno.id = ?" +
				" and av.ano = ?" +
				" and av.periodo = ?");
		if (formulario != null)
			hql.append(" and av.formulario.id = :idFormulario");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger(0, docente.getId())
			.setInteger(1, ano)
			.setInteger(2, periodo);
		if (formulario != null)
			q.setInteger("idFormulario", formulario.getId());
		return (AvaliacaoInstitucional) q.setMaxResults(1).uniqueResult();
	}

	/**
	 * Retorna a Avaliação Institucional realizada por um discente em um ano-período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param formulario
	 * @return
	 * @throws DAOException
	 */
	public AvaliacaoInstitucional findByDiscente(Discente discente, int ano, int periodo, FormularioAvaliacaoInstitucional formulario) throws DAOException {
		Criteria c = getSession().createCriteria(AvaliacaoInstitucional.class);
		c.add(Restrictions.eq("ano", ano));
		c.add(Restrictions.eq("periodo", periodo));
		c.add(Restrictions.eq("discente.id", discente.getId()));
		if (formulario != null)
			c.add(Restrictions.eq("formulario.id",formulario.getId()));
		else 
			c.add(Restrictions.isNull("formulario"));
		
		c.setMaxResults(1);
		
		return (AvaliacaoInstitucional) c.uniqueResult(); 
		
	}
	
	/**
	 * Esse método remove o trancamento no qual tiver o id
	 * igual ao id passado como parâmetro.
	 * 
	 * @param aval
	 */
	public void removerTrancamentos(AvaliacaoInstitucional aval) {
		update("delete from avaliacao.observacoes_trancamento where id_avaliacao = ?", new Object[] { aval.getId() });
	}

	/**
	 * Esse método remove as respostas no qual tiver o id
	 * igual ao id passado como parâmetro.
	 * 
	 * @param aval
	 */
	public void removerRespostas(AvaliacaoInstitucional aval) {
		update("delete from avaliacao.resposta_pergunta where id_avaliacao = ?", new Object[] { aval.getId() });
	}

	/**
	 * Retorna o total de questões respondidas para um professor em um ano/período especificado
	 * @param dt
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public long countAvaliacaoByDocenteTurmaAnoPeriodo(DocenteTurma dt, int ano, int periodo) throws DAOException {
		String sql = "SELECT COUNT(*) FROM avaliacao.resposta_pergunta rp, avaliacao.avaliacao_institucional ai "
			+ " WHERE rp.id_avaliacao = ai.id "
			+ " AND rp.id_docente_turma = :dt "
			+ " AND ai.ano = :ano "
			+ " AND ai.periodo = :periodo";

		Query q = getSession().createSQLQuery(sql);
		q.setInteger("dt", dt.getId());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		return ((BigInteger) q.uniqueResult()).longValue();
	}

	/**
	 * Retorna os dados dos docentes, bem como o horário da turma, assim como o identificador da pergunta, seu tipo
	 * as respostas obtidas e as citações. 
	 * 
	 * @param ano
	 * @param periodo
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<ResultadoAvaliacao> findResultadosDiscentes(int ano, int periodo) throws DAOException {

		String sql = "select ai.id,"
			+ " c.nome as curso,"
			+ " u.nome as centro,"
			+ " d.id_discente,"
			+ " d.matricula,"
			+ " ai.observacoes"
			+ " from avaliacao.avaliacao_institucional ai, discente d, curso c, comum.unidade u "
			+ " where ai.ano = ?" 
			+ " and ai.periodo = ?"
			+ " and ai.id_discente = d.id_discente"
			+ " and d.id_curso = c.id_curso"
			+ " and c.id_unidade = u.id_unidade "
			+ " and c.id_modalidade_educacao = 1"
			+ " and ai.finalizada = trueValue()" 
			+ " and d.nivel = 'G'";

		String sqlRespostas = "select dt.id_docente_turma,"
			+ " coalesce(pe.nome, pe2.nome) as docente,"
			+ " ccd.nome as disc, ccd.codigo,"
			+ " t.codigo as turma,"
			+ " t.descricao_horario,"
			+ " t.local,"
			+ " p.id_grupo,"
			+ " p.id as id_pergunta,"
			+ " p.tipo_pergunta,"
			+ " rp.resposta,"
			+ " rp.citacao,"
			+ " sit_mat.descricao as situacao_matricula,"
			+ " mc.media_final,"
			+ " case when mc.recuperacao is not null then 'SIM' else 'NAO' end as recuperacao,"
			+ " dt.id_turma,"
			+ " mc.numero_faltas"
			+ " from avaliacao.avaliacao_institucional ai"
			+ " left join avaliacao.resposta_pergunta rp on (rp.id_avaliacao = ai.id)"
			+ " left join ensino.docente_turma dt on (rp.id_docente_turma = dt.id_docente_turma)"
			+ " left join rh.servidor s on (dt.id_docente = s.id_servidor)"
			+ " left join comum.pessoa pe on (s.id_pessoa = pe.id_pessoa)"
			+ " left join ensino.turma t on (dt.id_turma = t.id_turma)"
			+ " left join ensino.matricula_componente as mc on (mc.id_turma = dt.id_turma and mc.id_discente = ai.id_discente)"
			+ " left join ensino.situacao_matricula sit_mat on ( sit_mat.id_situacao_matricula = mc.id_situacao_matricula )"
			+ " left join ensino.componente_curricular cc on (t.id_disciplina = cc.id_disciplina)"
			+ " left join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)"
			+ " left join ensino.docente_externo de on (dt.id_docente_externo = de.id_docente_externo)"
			+ " left join comum.pessoa pe2 on (de.id_pessoa = pe2.id_pessoa), avaliacao.pergunta p"
			+ " where rp.id_avaliacao = ? and rp.id_pergunta = p.id"
			+ " and dt.id_docente_turma in"
			+ " (select rp.id_docente_turma from avaliacao.avaliacao_institucional ai"
			+ " left join avaliacao.resposta_pergunta rp on (ai.id = rp.id_avaliacao)"
			+ " left join avaliacao.pergunta p on (p.id = rp.id_pergunta)"
			+ " left join avaliacao.grupo_perguntas gp on (p.id_grupo = gp.id)"
			+ " where gp.id = 2 and p.id = 5 and rp.resposta not in (0,-1) and ai.id = ?)"
			+ " and mc.id_situacao_matricula in (4,6,7)"
			+ " UNION"
			+ " select dt.id_docente_turma,"
			+ " coalesce(pe.nome, pe2.nome) as docente,"
			+ " ccd.nome as disc,"
			+ " ccd.codigo,"
			+ " t.codigo as turma,"
			+ " t.descricao_horario,"
			+ " t.local,"
			+ " p.id_grupo,"
			+ " p.id as id_pergunta,"
			+ " p.tipo_pergunta,"
			+ " rp.resposta,"
			+ " rp.citacao,"
			+ " sit_mat.descricao as situacao_matricula,"
			+ " mc.media_final,"
			+ " case when mc.recuperacao is not null then 'SIM' else 'NAO' end as recuperacao,"
			+ " dt.id_turma,"
			+ " mc.numero_faltas"
			+ " from avaliacao.avaliacao_institucional ai"
			+ " left join avaliacao.resposta_pergunta rp on (rp.id_avaliacao = ai.id)"
			+ " left join ensino.docente_turma dt on (rp.id_docente_turma = dt.id_docente_turma)"
			+ " left join rh.servidor s on (dt.id_docente = s.id_servidor)"
			+ " left join comum.pessoa pe on (s.id_pessoa = pe.id_pessoa)"
			+ " left join ensino.turma t on (dt.id_turma = t.id_turma)"
			+ " left join (select * from ensino.matricula_componente where id_situacao_matricula in (4,6,7)) as mc on (mc.id_turma = dt.id_turma and mc.id_discente = ai.id_discente)"
			+ " left join ensino.situacao_matricula sit_mat on ( sit_mat.id_situacao_matricula = mc.id_situacao_matricula )"
			+ " left join ensino.componente_curricular cc on (t.id_disciplina = cc.id_disciplina)"
			+ " left join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)"
			+ " left join ensino.docente_externo de on (dt.id_docente_externo = de.id_docente_externo)"
			+ " left join comum.pessoa pe2 on (de.id_pessoa = pe2.id_pessoa), avaliacao.pergunta p"
			+ " where rp.id_avaliacao = ? and rp.id_pergunta = p.id and dt.id_docente_turma is null"
			+ " order by id_docente_turma, id_grupo";

		@SuppressWarnings("unchecked")
		List<ResultadoAvaliacao> avaliacoes = getJdbcTemplate().query(sql, new Object[] { ano, periodo }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultadoAvaliacao r = new ResultadoAvaliacao();
				r.setIdAvaliacao(rs.getInt("id"));
				r.setCurso(rs.getString("curso"));
				r.setCentro(rs.getString("centro"));
				r.setDiscente(new Discente(rs.getInt("id_discente")));
				r.getDiscente().setMatricula(rs.getLong("matricula"));
				r.setComentarios(rs.getString("observacoes"));
				return r;
			}
		});

		Map <Integer, Integer> cacheAlternativas = new TreeMap<Integer, Integer>();

		for (ResultadoAvaliacao a : avaliacoes) {
			a.setTurmas(new ArrayList<ResultadoTurma>());
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> respostas = getJdbcTemplate().queryForList(sqlRespostas, new Object[] { a.getIdAvaliacao(), a.getIdAvaliacao(), a.getIdAvaliacao() });

			for (Map<String, Object> linha : respostas) {
				ResultadoTurma r = new ResultadoTurma();
				Integer idDocenteTurma = (Integer) linha.get("id_docente_turma");
				r.setIdDocenteTurma(idDocenteTurma == null ? 0 : idDocenteTurma);

				if (a.getTurmas().contains(r)) {
					r = a.getTurmas().get(a.getTurmas().indexOf(r));
				} else {
					r.setDocente((String) linha.get("docente"));
					r.setDisciplina((String) linha.get("disc"));
					r.setCodigoDisciplina((String) linha.get("codigo"));
					r.setTurma((String) linha.get("turma"));
					r.setHorario((String) linha.get("descricao_horario"));
					r.setLocal((String) linha.get("local"));
					r.setSituacaoMatricula((String) linha.get("situacao_matricula"));
					r.setRecuperacao((String) linha.get("recuperacao"));
					r.setMediaFinal((BigDecimal) linha.get("media_final"));
					r.setIdTurma((Integer) linha.get("id_turma"));
					r.setNumeroFaltas((Integer) linha.get("numero_faltas"));
					r.setDados(new TreeSet<ResultadoResposta>());
					a.addTurma(r);					
				}

				Integer idPergunta = (Integer) linha.get("id_pergunta");
				Integer idGrupo = (Integer) linha.get("id_grupo");
				Integer tipo = (Integer) linha.get("tipo_pergunta");
				Integer resposta = (Integer) linha.get("resposta");
				String citacao = (String) linha.get("citacao");

				ResultadoResposta rr = new ResultadoResposta(idGrupo, idPergunta);
				if (r.getDados().contains(rr)) {
					for (ResultadoResposta tmp : r.getDados()) {
						if (tmp.getIdPergunta() == rr.getIdPergunta()) {
							rr = tmp;
							break;
						}
					}
				} else {
					r.add(rr);
				}

				if (tipo == TipoPergunta.PERGUNTA_SIM_NAO.ordinal()) {
					if (resposta == null) rr.addResposta("");
					else if (1 == resposta) rr.addResposta("Sim");
					else rr.addResposta("Não");
				} else if (tipo == TipoPergunta.PERGUNTA_NOTA.ordinal()){
					if (resposta != null && -1 == resposta) rr.addResposta("N/A");
					else rr.addResposta(resposta);
				} else if (tipo == TipoPergunta.PERGUNTA_MULTIPLA_ESCOLHA.ordinal()){
					int alternativa;
					if (cacheAlternativas.containsKey(resposta)) {
						alternativa = cacheAlternativas.get(resposta);
					} else {
						alternativa = getJdbcTemplate().queryForInt("select numero from avaliacao.alternativa_pergunta where id = ?", resposta);
						cacheAlternativas.put(resposta, alternativa);
					}
					rr.addResposta(alternativa);
				} else {
					int alternativa;
					if (cacheAlternativas.containsKey(resposta)) {
						alternativa = cacheAlternativas.get(resposta);
					} else {
						alternativa = getJdbcTemplate().queryForInt("select numero from avaliacao.alternativa_pergunta where id = ?", resposta);
						cacheAlternativas.put(resposta, alternativa);
					}

					int total = getJdbcTemplate().queryForInt("select count(*) from avaliacao.alternativa_pergunta where id_pergunta = ?", new Object[] { idPergunta });
					for(int j = 1; j <= total; j++) {
						if (j == 6) {
							if (isEmpty(citacao))
								rr.addResposta("");
							else
								rr.addResposta(citacao);
						} else {
							if (alternativa == j)
								rr.addResposta("X");
							else
								rr.addResposta("");
						}
					}
				}

			}

			for (ResultadoTurma r : a.getTurmas()) {				
				if (r.getIdDocenteTurma() != 0) {
					ResultadoTurma rt = a.getTurmas().get(a.getTurmas().indexOf(new ResultadoTurma()));
					r.getDados().addAll(rt.getDados());	
				}

			}

			a.getTurmas().remove(new ResultadoTurma());

			for (ResultadoTurma r : a.getTurmas()) {	
				ResultadoResposta rr = new ResultadoResposta(4, 27);
				for (ResultadoResposta tmp : r.getDados()) {
					if (tmp.getIdPergunta() == rr.getIdPergunta()) {
						rr = tmp;
						break;
					}
				}

				List<Object> resposta = rr.getResposta();
				if (!rr.isProcessamentoMultiplaEscolha()){
					if (resposta != null) {
						rr.setProcessamentoMultiplaEscolha(true);
						rr.setResposta(new ArrayList<Object>());
						int total = getJdbcTemplate().queryForInt("select count(*) from avaliacao.alternativa_pergunta where id_pergunta = ?", new Object[] { rr.getIdPergunta() });

						for (int j = 1; j <= total; j++) {
							if (resposta.contains(j)) {
								if (j == 6) {
									try {
										rr.addResposta(getJdbcTemplate().queryForObject("select citacao from avaliacao.resposta_pergunta where id_avaliacao=? and id_pergunta=27 and citacao is not null " + BDUtils.limit(1), new Object[] { a.getIdAvaliacao() }, String.class));
									} catch(EmptyResultDataAccessException e) {
										rr.addResposta("");
									}
								} else {
									rr.addResposta("X");
								}
							} else {
								rr.addResposta("");
							}
						}
					} else {
						rr.addResposta("");
						rr.addResposta("");
						rr.addResposta("");
						rr.addResposta("");
						rr.addResposta("");
						rr.addResposta("");
						r.getDados().add(rr);
					}
				}
			}

			List<Turma> trancamentos = findTrancamentosSemestreByDiscente2(a.getDiscente(), ano, periodo);
			a.setNumTrancamentos(trancamentos.size());
			for (Turma turma : trancamentos) {
				try {
					a.addMotivoTrancamento(turma.getDisciplina().getCodigo() + " - " + getJdbcTemplate().queryForObject("select observacoes from avaliacao.observacoes_trancamento where id_avaliacao = ? and id_turma = ? " + BDUtils.limit(1), new Object[] { a.getIdAvaliacao(), turma.getId() }, String.class));
				} catch(EmptyResultDataAccessException e) {
					a.addMotivoTrancamento(turma.getDisciplina().getCodigo() + " - Motivo não informado");
				}
			}

		}

		return avaliacoes;
	}

	/**
	 * Retorna os dados dos docentes, bem como o horário da turma, assim como o identificador da pergunta, seu tipo
	 * as respostas obtidas e as citações.
	 *  
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public List<ResultadoAvaliacao> findResultadosDocentes(int ano, int periodo) throws DAOException {

		String sql = "select ai.id," 
			+ " pe.nome," 
			+ " u.nome as centro,"
			+ " d.nome as departamento," 
			+ " c.descricao as categoria,"
			+ " ai.observacoes "
			+ " from avaliacao.avaliacao_institucional ai,"
			+ " rh.servidor s," 
			+ " comum.pessoa pe," 
			+ " comum.unidade d,"
			+ " comum.unidade u," 
			+ " rh.categoria c" 
			+ " where ai.ano = ?"
			+ " and ai.periodo = ?" 
			+ " and ai.id_docente = s.id_servidor"
			+ " and s.id_pessoa = pe.id_pessoa"
			+ " and s.id_unidade = d.id_unidade "
			+ " and d.unidade_responsavel = u.id_unidade"
			+ " and s.id_categoria = c.id_categoria" ;

		String sqlRespostas = "select dt.id_docente_turma,"
			+ " ccd.nome as disc,"
			+ " ccd.codigo,"
			+ " t.codigo as turma,"
			+ " t.descricao_horario,"
			+ " t.local,"
			+ " p.id_grupo,"
			+ " p.id as id_pergunta,"
			+ " p.tipo_pergunta,"
			+ " rp.resposta,"
			+ " rp.citacao "
			+ " from avaliacao.resposta_pergunta rp"
			+ " left join ensino.docente_turma dt on (rp.id_docente_turma = dt.id_docente_turma) "
			+ " left join ensino.turma t on (dt.id_turma = t.id_turma) "
			+ " left join ensino.componente_curricular cc on (t.id_disciplina = cc.id_disciplina)"
			+ " left join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes),"
			+ " avaliacao.pergunta p " 
			+ " where rp.id_avaliacao = ?"
			+ " and rp.id_pergunta = p.id "
			+ " order by rp.id_docente_turma desc, p.id_grupo, p.id";

		@SuppressWarnings("unchecked")
		List<ResultadoAvaliacao> avaliacoes = getJdbcTemplate().query(sql, new Object[] { ano, periodo }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultadoAvaliacao r = new ResultadoAvaliacao();
				r.setIdAvaliacao(rs.getInt("id"));
				r.setNomeProfessor(rs.getString("nome"));
				r.setCentro(rs.getString("centro"));
				r.setDepartamento(rs.getString("departamento"));
				r.setCategoria(rs.getString("categoria"));
				r.setComentarios(rs.getString("observacoes"));
				return r;
			}
		});

		for (ResultadoAvaliacao a : avaliacoes) {
			a.setTurmas(new ArrayList<ResultadoTurma>());
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> respostas = getJdbcTemplate().queryForList(sqlRespostas, new Object[] { a.getIdAvaliacao() });

			for (Map<String, Object> linha : respostas) {
				ResultadoTurma r = new ResultadoTurma();
				Integer idDocenteTurma = (Integer) linha.get("id_docente_turma");
				r.setIdDocenteTurma(idDocenteTurma == null ? 0 : idDocenteTurma);

				if (a.getTurmas().contains(r)) {
					r = a.getTurmas().get(a.getTurmas().indexOf(r));
				} else {
					r.setDisciplina((String) linha.get("disc"));
					r.setCodigoDisciplina((String) linha.get("codigo"));
					r.setTurma((String) linha.get("turma"));
					r.setHorario((String) linha.get("descricao_horario"));
					r.setLocal((String) linha.get("local"));
					r.setDados(new TreeSet<ResultadoResposta>());
					a.addTurma(r);					
				}

				Integer idPergunta = (Integer) linha.get("id_pergunta");
				Integer idGrupo = (Integer) linha.get("id_grupo");
				Integer tipo = (Integer) linha.get("tipo_pergunta");
				Integer resposta = (Integer) linha.get("resposta");
				String citacao = (String) linha.get("citacao");

				ResultadoResposta rr = new ResultadoResposta(idGrupo, idPergunta);
				if (r.getDados().contains(rr)) {
					for (ResultadoResposta tmp : r.getDados()) {
						if (tmp.getIdPergunta() == rr.getIdPergunta()) {
							rr = tmp;
							break;
						}
					}
				} else {
					r.add(rr);
				}

				if (tipo == TipoPergunta.PERGUNTA_SIM_NAO.ordinal()) {
					if (resposta == null) rr.addResposta("");
					else if (1 == resposta) rr.addResposta("Sim");
					else rr.addResposta("Não");
				} else if (tipo == TipoPergunta.PERGUNTA_NOTA.ordinal()){
					if (resposta != null && -1 == resposta) rr.addResposta("N/A");
					else rr.addResposta(resposta);
				} else if (tipo == TipoPergunta.PERGUNTA_MULTIPLA_ESCOLHA.ordinal()){
					rr.addResposta(getJdbcTemplate().queryForInt("select numero from avaliacao.alternativa_pergunta where id = ?", resposta));
				} else {
					int alternativa = getJdbcTemplate().queryForInt("select numero from avaliacao.alternativa_pergunta where id = ?", resposta);
					int total = getJdbcTemplate().queryForInt("select count(*) from avaliacao.alternativa_pergunta where id_pergunta = ?", new Object[] { idPergunta });

					for(int j = 1; j <= total; j++) {
						if (j == total) {
							if (isEmpty(citacao))
								rr.addResposta("");
							else
								rr.addResposta(citacao);
						} else {
							if (alternativa == j)
								rr.addResposta("X");
							else
								rr.addResposta("");
						}
					}
				}

			}

			for (ResultadoTurma r : a.getTurmas()) {				
				if (r.getIdDocenteTurma() != 0) {
					ResultadoTurma rt = a.getTurmas().get(a.getTurmas().indexOf(new ResultadoTurma()));
					r.getDados().addAll(rt.getDados());	
				}

			}

			a.getTurmas().remove(new ResultadoTurma());

			processaMultiplaEscolha(a, 8, 42);
			processaMultiplaEscolha(a, 8, 43);

		}

		return avaliacoes;
	}

	/** Retorna os dados para a geração do arquivo de comentários relativos aos docentes.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findComentariosRelativosDocente(int ano, int periodo) throws DAOException {

		String sql = "select ai.id,"
			+ " di.matricula,"
			+ " u.nome as centro_curso,"
			+ " c.nome as curso,"
			+ " u2.nome as centro_componente,"
			+ " dcc.nome as departamento_componente,"
			+ " ccd.codigo as codigo,"
			+ " ccd.nome as componente,"
			+ " coalesce(doc.nome,'REF. A TODOS DOCENTES DA TURMA') as docente,"
			+ " coalesce(dex.nome,'-') as docente_externo,"
			+ " coalesce(d.nome,'') as departamento_docente,"
			+ " t.codigo as turma,"
			+ " t.descricao_horario as horario,"
			+ " t.local as local,"
			+ " coalesce(odt.observacoes,'') as observacoes,"
			+ " coalesce(odt.observacoes_moderadas,'') as observacoes_moderadas,"
			+ " coalesce(ai.observacoes,'') as observacoes_gerais"
			+ " from avaliacao.avaliacao_institucional ai"
			+ " inner join discente di using (id_discente)"
			+ " left join curso c on (di.id_curso = c.id_curso)"
			+ " left join avaliacao.observacoes_docente_turma odt on (ai.id = odt.id_avaliacao)"
			+ " left join ensino.turma t on (odt.id_turma = t.id_turma)"
			+ " left join ensino.componente_curricular cc using (id_disciplina) "
			+ " left join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)"
			+ " left join ensino.docente_turma dt1 on (odt.id_docente_turma = dt1.id_docente_turma)"
			+ " left join ensino.docente_turma dt2 on (odt.id_docente_turma = dt2.id_docente_turma)"
			+ " left join rh.servidor s1 on (s1.id_servidor = dt1.id_docente)"
			+ " left join ensino.docente_externo de on (dt2.id_docente_externo = de.id_docente_externo)"
			+ " left join rh.servidor s2 on (s2.id_servidor = de.id_servidor)"
			+ " left join comum.pessoa doc on (s1.id_pessoa = doc.id_pessoa)"
			+ " left join comum.pessoa dex on (s2.id_pessoa = dex.id_pessoa)"
			+ " left join comum.unidade d on (s1.id_unidade = d.id_unidade)"
			+ " left join comum.unidade u on (c.id_unidade = u.id_unidade)"
			+ " left join comum.unidade dcc on (cc.id_unidade = dcc.id_unidade)"
			+ " left join comum.unidade u2 on (dcc.unidade_responsavel = u2.id_unidade)"
			+ " where ai.ano = ?"
			+ " and ai.periodo = ?"
			+ " and c.id_modalidade_educacao = ?"
			+ " and ai.finalizada = trueValue()"
			+ " and (odt.observacoes is not null and trim(odt.observacoes) <> '')"
			+ " order by cc.codigo, t.codigo";

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(sql, new Object[]{ano, periodo, ModalidadeEducacao.PRESENCIAL});
		// 	descarta comentários em branco, mas que tenham quebra de linha (não foi possível fazer isso no sql)
		List<Map<String, Object>> resultado = new LinkedList<Map<String,Object>>();
		for (Map<String, Object> linha : lista) {
			if (!isEmpty(linha.get("observacoes"))
					|| !isEmpty(linha.get("observacoes_moderadas"))
					|| !isEmpty(linha.get("observacoes_gerais"))) {
				resultado.add(linha);
			}
		}
		return resultado;
	}

	/**
	 * Tem como objetivo processar as múltiplas escolhas.
	 * Feitas pelo usuário. 
	 * 
	 * @param a
	 * @param grupo
	 * @param pergunta
	 */
	private void processaMultiplaEscolha(ResultadoAvaliacao a, int grupo, int pergunta) {
		for (ResultadoTurma r : a.getTurmas()) {	
			ResultadoResposta rr = new ResultadoResposta(grupo, pergunta);
			for (ResultadoResposta tmp : r.getDados()) {
				if (tmp.getIdPergunta() == rr.getIdPergunta()) {
					rr = tmp;
					break;
				}
			}

			List<Object> resposta = rr.getResposta();
			if (!rr.isProcessamentoMultiplaEscolha()){
				if (resposta != null) {
					int total = getJdbcTemplate().queryForInt("select count(*) from avaliacao.alternativa_pergunta where id_pergunta = ?", new Object[] { pergunta });

					rr.setProcessamentoMultiplaEscolha(true);
					rr.setResposta(new ArrayList<Object>());
					for (int j = 1; j <= total; j++) {
						if (resposta.contains(j)) {
							if (j == total) {
								try {
									rr.addResposta(getJdbcTemplate().queryForObject("select citacao from avaliacao.resposta_pergunta where id_avaliacao=? and id_pergunta="+pergunta+" and citacao is not null " + BDUtils.limit(1), new Object[] { a.getIdAvaliacao() }, String.class));
								} catch(EmptyResultDataAccessException e) {
									rr.addResposta("X");
								}
							} else {
								rr.addResposta("X");
							}
						} else {
							rr.addResposta("");
						}
					}
				} else {
					rr.addResposta("");
					rr.addResposta("");
					rr.addResposta("");
					rr.addResposta("");
					rr.addResposta("");
					rr.addResposta("");
					r.getDados().add(rr);
				}
			}
		}
	}

	/**
	 * O método tem com finalidade deletar a avaliação cujo id foi passado.
	 * 
	 * @param aval
	 */
	public void removerObservacoes(AvaliacaoInstitucional aval) {
		update("delete from avaliacao.observacoes_docente_turma where id_avaliacao = ?", new Object[] { aval.getId() });
		update("delete from avaliacao.observacoes_docencia_assistida where id_avaliacao = ?", new Object[] { aval.getId() });
	}

	/**
	 * Retorna a contagem dos alunos que fizeram parte da avaliação, no ano e período. 
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public int findQtdAlunosAvaliacao(int ano, int periodo, int idFormulario) {
		String sql = "select count(distinct id_discente)"
				+ " from avaliacao.avaliacao_institucional"
				+ " where id_discente is not null "
				+ " and finalizada = trueValue()"
				+ " and ano = ?"
				+ " and periodo = ?"
				+ " and id_formulario_avaliacao = ?";
		return getJdbcTemplate().queryForInt(sql, new Object[] { ano, periodo, idFormulario });
	}

	/**
	 * Retorna a quantidade de disciplinas avaliadas no ano e período informado.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public int findQtdDisciplinasAvaliadas(int ano, int periodo, int idFormulario) {
		String sql = "select count(distinct t.id_disciplina)"
				+ " from avaliacao.avaliacao_institucional ai "
				+ " inner join avaliacao.resposta_pergunta rp on (ai.id = rp.id_avaliacao)"
				+ " inner join ensino.docente_turma dt using (id_docente_turma)"
				+ " inner join ensino.turma t using (id_turma)"
				+ " where ai.finalizada = trueValue()"
				+ " and ai.ano = ?"
				+ " and ai.periodo = ? "
				+ " and id_formulario_avaliacao = ?";
		return getJdbcTemplate().queryForInt(sql, new Object[] { ano, periodo, idFormulario });
	}

	/**
	 * Retorna a quantidade de turmas avaliadas no intervalo informado, ano e período.  
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public int findQtdTurmasAvaliadas(int ano, int periodo, int idFormulario) {
		String sql = "select count(distinct dt.id_turma)" 
				+ " from avaliacao.avaliacao_institucional ai "
				+ " inner join avaliacao.resposta_pergunta rp on (ai.id = rp.id_avaliacao)"
				+ " inner join ensino.docente_turma dt using (id_docente_turma)"
				+ " inner join ensino.turma t using (id_turma)"
				+ " where ai.finalizada = trueValue()"
				+ " and ai.ano = ?"
				+ " and ai.periodo = ? "
				+ " and id_formulario_avaliacao = ?";
		return getJdbcTemplate().queryForInt(sql, new Object[] { ano, periodo, idFormulario });
	}


	/** Retorna uma lista de DocenteTurma que terá o Resultado da Avaliação Institucional processado.
	 * @param idUnidade (Opcional) restringe a consulta a unidade gestora especificada pelo idUnidade. Caso idUnidade seja 0, a busca não restringe as turmas da unidade gestora. 
	 * @param ano
	 * @param periodo
	 * @param idFormulario
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<DocenteTurma> findDocenteTurmaProcessamento (int idUnidade, int ano, int periodo, int idFormulario) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select distinct dt.id_docente_turma, t.id_turma" +
				" from ensino.docente_turma dt" +
				" inner join ensino.turma t using (id_turma)");
		if (idUnidade != 0)
				sql.append(" inner join ensino.componente_curricular cc using (id_disciplina)" +
				" inner join comum.unidade u using (id_unidade)");
		sql.append(" inner join avaliacao.resposta_pergunta rp on (rp.id_docente_turma = dt.id_docente_turma)" +
				" inner join avaliacao.avaliacao_institucional ai on (ai.id = rp.id_avaliacao)" +
				" where t.ano = :ano and t.periodo = :periodo" +
				" and ai.id_formulario_avaliacao = :idFormulario");
		if (idUnidade != 0)
			sql.append(" and (u.id_gestora = :idUnidade or u.id_unidade = :idUnidade or u.unidade_responsavel = :idUnidade)");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idFormulario", idFormulario);
		if (idUnidade != 0)
			q.setInteger("idUnidade", idUnidade);
		@SuppressWarnings("unchecked")
		Collection<Object[]> bulk = q.list();
		List<DocenteTurma> lista = new ArrayList<DocenteTurma>();
		for (Object[] obj : bulk) {
			Turma turma = new Turma((Integer) obj[1]);
			DocenteTurma docenteTurma = new DocenteTurma((Integer) obj[0], turma);
			lista.add(docenteTurma);
		}
		return lista;
	}
	
	/** Retorna uma lista de Turmas de docência assistida que terá o Resultado da Avaliação Institucional processado.
	 * @param idUnidade (Opcional) restringe a consulta a unidade gestora especificada pelo idUnidade. Caso idUnidade seja 0, a busca não restringe as turmas da unidade gestora. 
	 * @param ano
	 * @param periodo
	 * @param idFormulario
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<TurmaDocenciaAssistida> findTurmaDocenciaAssistidaProcessamento (int idUnidade, int ano, int periodo, int idFormulario) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select distinct tda.id_turma_docencia_assistida, tda.id_turma" +
				" from stricto_sensu.turma_docencia_assistida tda" +
				" inner join ensino.turma t using (id_turma)");
		if (idUnidade != 0)
				sql.append(" inner join ensino.componente_curricular cc using (id_disciplina)" +
				" inner join comum.unidade u using (id_unidade)");
		sql.append(" inner join avaliacao.resposta_pergunta rp using (id_turma_docencia_assistida)" +
				" inner join avaliacao.avaliacao_institucional ai on (ai.id = rp.id_avaliacao)" +
				" where ai.ano = :ano and ai.periodo = :periodo" +
				" and ai.id_formulario_avaliacao = :idFormulario");
		if (idUnidade != 0)
			sql.append(" and (u.id_gestora = :idUnidade or u.id_unidade = :idUnidade or u.unidade_responsavel = :idUnidade)");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idFormulario", idFormulario);
		if (idUnidade != 0)
			q.setInteger("idUnidade", idUnidade);
		@SuppressWarnings("unchecked")
		Collection<Object[]> bulk = q.list();
		List<TurmaDocenciaAssistida> lista = new ArrayList<TurmaDocenciaAssistida>();
		for (Object[] obj : bulk) {
			Turma turma = new Turma((Integer) obj[1]);
			TurmaDocenciaAssistida turmaDocenciaAssistida = new TurmaDocenciaAssistida((Integer) obj[0]);
			turmaDocenciaAssistida.setTurma(turma);
			lista.add(turmaDocenciaAssistida);
		}
		return lista;
	}

	/**
	 * Retorna a quantidade de turmas não avaliadas no ano e período informado.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public int findQtdTurmasNaoAvaliadas(int ano, int periodo, int idFormulario) {
		// situações das turmas na consulta
		String abertaConsolidada = "(" + SituacaoTurma.ABERTA + ", " + SituacaoTurma.CONSOLIDADA+")";
		// turma presencial ou a distância ?
		boolean distancia = (Boolean) getJdbcTemplate().queryForObject("select ead" +
				" from avaliacao.formulario_avaliacao" +
				" where id_formulario_avaliacao = ?", new Object[] {idFormulario}, Boolean.class);
		// condições comuns às turmas.
		String where = "where t.ano = " + ano
			+ "  and t.periodo = " + periodo
			+ "  and t.distancia = ?"
			+ "  and cc.nivel = '"+NivelEnsino.GRADUACAO+"'"
			+ "  and t.tipo = " + Turma.REGULAR
			+ "  and t.id_situacao_turma in "+abertaConsolidada;
		// consulta
		String sql = "select count(*)"
			+ " from ( select distinct t.id_turma"
			+ " from ensino.turma t"
			+ "  inner join ensino.componente_curricular cc using (id_disciplina)"
			+ where
			+ "  except "
			+ " select distinct t.id_turma"
			+ " from ensino.docente_turma dt"
			+ "  inner join avaliacao.resposta_pergunta rp using (id_docente_turma)"
			+ "  inner join ensino.turma t using (id_turma)"
			+ "  inner join ensino.componente_curricular cc using (id_disciplina)"
			+ where
			+ ") consulta";
		return getJdbcTemplate().queryForInt(sql, new Object[] {distancia, distancia});
	}

	/**
	 * DAO que gera relatório quantitativo referente aos Discentes, que trancou no ano e semestre.
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public Map<String, Integer> findCountTrancamentoPeriodo(Integer ano, Integer periodo) throws DAOException {
		Map<String, Integer> result = new HashMap<String, Integer>();

		JdbcTemplate template = getJdbcTemplate();

		String[] titulos = {"TOTAL DE ALUNOS",
		"TOTAL DE TRANCAMENTOS"};

		String[] appends = {" select count(distinct d.id_discente)"+
				" from ensino.matricula_componente mc, discente d, ensino.turma t"+
				" where mc.id_discente = d.id_discente and mc.ano = ? and mc.periodo = ? and mc.id_situacao_matricula in (2, 4, 5, 6, 7)"+
				" and t.id_turma = mc.id_turma and t.id_polo is null and d.nivel = 'G' and d.tipo = 1",
				" select count(*)"+
				" from ensino.matricula_componente mc, discente d, ensino.turma t"+
				" where mc.id_discente = d.id_discente and mc.ano = ? and mc.periodo = ? and mc.id_situacao_matricula = 5"+
		" and t.id_turma = mc.id_turma and t.id_polo is null and d.nivel = 'G' and d.tipo = 1"};

		for (int i = 0; i < appends.length; i++) {
			result.put(titulos[i], template.queryForInt(appends[i], new Object[]{ano, periodo}));
		}
		return result;
	}

	/**
	 * Gera um relatório com os Trancamentos por Departamento/Centro, tendo com base o 
	 * ano e período informado.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findTrancamentoDepartamentoCentro(Integer ano, Integer periodo ) throws DAOException {
		String sqlconsulta = "select cen.nome as nome_centro," +
		" u.nome as nome_unidade," +
		" count(*)"+
		" from ensino.matricula_componente mc," +
		" discente d," +
		" ensino.turma t," +
		" ensino.componente_curricular cc," +
		" curso c," +
		" comum.unidade u," +
		" comum.unidade cen"+
		" where mc.id_discente = d.id_discente" +
		" and mc.ano = "+ano+
		" and mc.periodo = "+periodo+
		" and mc.id_situacao_matricula = "+SituacaoMatricula.TRANCADO.getId()+
		" and t.id_turma = mc.id_turma" +
		" and t.id_polo is null" +
		" and d.nivel = '" + NivelEnsino.GRADUACAO+"'"+
		" and d.tipo = " + Discente.REGULAR +
		" and t.id_disciplina = cc.id_disciplina"+
		" and d.id_curso = c.id_curso" +
		" and cc.id_unidade = u.id_unidade" +
		" and u.id_gestora = cen.id_unidade"+
		" group by cen.nome, u.nome order by cen.nome, u.nome";

		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Query query = getSession().createSQLQuery(sqlconsulta);

		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		for (Object [] obj : lista) {
			int i = 0;
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("nome_centro", obj[i++]);
			item.put("nome_unidade", obj[i++]);
			item.put("count", obj[i++]);
			result.add(item);
		}
		return result;
	}

	/**
	 * Gera um relatório quantitativo do alunos reprovados por falta ou por nota. 
	 * no ano e período informado.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findQuantitativoReprovados(Integer ano, Integer periodo) throws DAOException {
		String sqlconsulta = "select curso," +
		" count(*) as total_de_reprovacoes,"+ 
		" sum(case when id_situacao_matricula = 6 then 1 else 0 end) as reprovacao_por_nota,"+ 
		" sum(case when id_situacao_matricula = 7 then 1 else 0 end) as reprovacao_por_falta"+
		" from (select c.nome as curso," +
		" id_situacao_matricula"+
		" from ensino.matricula_componente mc," +
		" discente d," +
		" ensino.turma t," +
		" ensino.componente_curricular cc," +
		" curso c," +
		" comum.unidade u," +
		" comum.unidade cen"+
		" where mc.id_discente = d.id_discente" +
		" and mc.ano = "+ano+
		" and mc.periodo = "+periodo+
		" and mc.id_situacao_matricula in ("+SituacaoMatricula.REPROVADO.getId()+", "+SituacaoMatricula.REPROVADO_FALTA.getId()+", " + SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId() + ")" +
		" and t.id_turma = mc.id_turma" +
		" and t.id_polo is null" +
		" and d.nivel = '" + NivelEnsino.GRADUACAO+"'"+
		" and d.tipo = " + Discente.REGULAR +
		" and t.id_disciplina = cc.id_disciplina"+
		" and d.id_curso = c.id_curso" +
		" and cc.id_unidade = u.id_unidade" +
		" and u.id_gestora = cen.id_unidade) as q"+
		" group by curso order by curso";

		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

		Query query = getSession().createSQLQuery(sqlconsulta);

		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		for (Object [] obj : lista) {
			int i = 0;
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("curso", obj[i++]);
			item.put("total_de_reprovacoes", obj[i++]);
			item.put("reprovacao_por_nota", obj[i++]);
			item.put("reprovacao_por_falta", obj[i++]);
			result.add(item);
		}
		return result;
	}

	/** Retorna uma lista de motivos de trancamentos no ano/período informados.
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public List<Map<String, Object>>  findMotivosTrancamento(int ano, int periodo){
		String sql = "select a.ano,"
			+ " a.periodo,"
			+ " d.matricula,"
			+ " u.nome_ascii as centro, "
			+ " (select array_to_string("
			+ "  array( "
			+ "   select distinct p.nome"
			+ "   from ensino.docente_turma dt," 
			+ "   rh.servidor s,"
			+ "   comum.pessoa p " + "where dt.id_docente = s.id_servidor"
			+ "   and s.id_pessoa = p.id_pessoa"
			+ "   and dt.id_turma = t.id_turma " 
			+ "   union "
			+ "   select distinct p.nome"
			+ "   from ensino.docente_turma dt,"
			+ "   ensino.docente_externo s,"
			+ "   comum.pessoa p "
			+ "   where dt.id_docente_externo = s.id_docente_externo"
			+ "   and s.id_pessoa = p.id_pessoa"
			+ "   and dt.id_turma = t.id_turma"
			+ "   ), ', ')) as professores,"
			+ " to_ascii(ccd.nome, 'LATIN9') as disciplina,"
			+ " ccd.codigo as codigo,"
			+ " t.codigo as turma, "
			+ " t.descricao_horario as horario," 
			+ " t.local,"
			+ " c.nome_ascii as curso,"
			+ " o.observacoes as motivo "
			+ " from avaliacao.avaliacao_institucional a,"
			+ " avaliacao.observacoes_trancamento o,"
			+ " comum.unidade u,"
			+ " discente d, "
			+ " curso c,"
			+ " ensino.turma t,"
			+ " ensino.componente_curricular cc,"
			+ " ensino.componente_curricular_detalhes ccd "
			+ " where a.id = o.id_avaliacao" 
			+ " and a.ano = ?"
			+ " and a.periodo = ?"
			+ " and a.id_discente is not null "
			+ " and a.id_discente = d.id_discente"
			+ " and c.id_unidade = u.id_unidade"
			+ " and d.id_curso = c.id_curso "
			+ " and o.id_turma = t.id_turma"
			+ " and t.id_disciplina = cc.id_disciplina"
			+ " and cc.id_detalhe = ccd.id_componente_detalhes "
			+ " and o.observacoes is not null"
			+ " and o.observacoes != ''"
			+ " order by centro, curso, disciplina, turma, matricula";

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(sql, new Object[] { ano, periodo });
		return lista;
	}

	/**
	 * Verifica se o discente possui uma avaliação institucional cadastrada e finalizada para o ano/período
	 * especificados
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public boolean isAvaliacaoFinalizada(Discente discente, int ano, int periodo, int idFormulario) {
		return getJdbcTemplate().queryForInt("select count(*) from avaliacao.avaliacao_institucional " +
				" where id_discente = ?" +
				" and ano = ?" +
				" and periodo = ?" +
				" and id_formulario_avaliacao = ?" +
				" and finalizada = trueValue()",
				new Object[] { discente.getId(), ano, periodo, idFormulario }) > 0;
	}

	/** Retorna uma coleção de resultados de avaliação de docentes de um centro, ou de um docente específico, para um ano/período específicos.
	 * @param idServidor caso seja = 0, não restringe a busca pelo docente.
	 * @param idCentro caso seja = 0, não restringe a busca pelo centro.
	 * @param idDepartamento caso seja = 0, não restringe a busca pelo departamento.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<ResultadoAvaliacaoDocente> findResultadoByDocenteCentroDepartamentoAnoPeriodo(int idServidor, int idCentro, int idDepartamento, int ano, int periodo) throws HibernateException, DAOException{
		StringBuilder sql = new StringBuilder("select rad.id_resultado_avaliacao_docente," +
				" dt.id_docente_turma," +
				" s.id_servidor as idServidor," +
				" dt.id_docente_externo as idDocenteExterno," +
				" p.nome as nomeDocente," +
				" ccd.nome as nomeComponente," +
				" ccd.codigo as codigoComponente," +
				" t.id_turma as id_turma," +
				" t.codigo as codigoTurma," +
				" t.descricao_horario as descricaoHorario," +
				" rad.num_trancamentos as numTrancamentos," +
				" rad.num_discentes as numDiscentes," +
				" rad.media_geral as mediaGeral," +
				" rad.desvio_padrao_geral as desvioPadraoGeral," +
				" mn.id_media_notas as idMediaNotas," +
				" mn.media," +
				" mn.desvio_padrao as desvioPadrao," +
				" psn.id_percentual_sim_nao," +
				" psn.percentual_sim," +
				" psn.percentual_nao," +
				" pg.id as idPergunta," +
				" pg.descricao as descricaoPergunta," +
				" pg.ordem as ordem," +
				" g.id as idGrupo," +
				" g.titulo as tituloGrupo," +
				" g.descricao as descricaoGrupo" +
				" from avaliacao.pergunta pg" +
				" inner join avaliacao.grupo_perguntas g on (pg.id_grupo = g.id)" +
				" left join avaliacao.media_notas mn on (pg.id = mn.id_pergunta)" +
				" left join avaliacao.percentual_sim_nao psn on (pg.id = psn.id_pergunta)" +
				" inner join avaliacao.resultado_avaliacao_docente rad on (mn.id_resultado_avaliacao_docente = rad.id_resultado_avaliacao_docente or psn.id_resultado_avaliacao_docente = rad.id_resultado_avaliacao_docente)" +
				" inner join ensino.docente_turma dt using (id_docente_turma)" +
				" left join ensino.docente_externo de using (id_docente_externo)" +
				" inner join rh.servidor s on (de.id_servidor = s.id_servidor or dt.id_docente = s.id_servidor)");
		if (idDepartamento > 0) 
			sql.append(" inner join comum.unidade d on (s.id_unidade = d.id_unidade)");
		if (idCentro > 0) 
			sql.append(" inner join comum.unidade c on (d.id_gestora = c.id_unidade)");
		sql.append(" inner join comum.pessoa p on (p.id_pessoa = s.id_pessoa)" +
				" inner join ensino.turma t using (id_turma)" +
				" inner join ensino.componente_curricular cc using (id_disciplina)" +
				" inner join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)" +
				" where (t.distancia = falseValue() or t.id_polo is null)" +
				" and rad.ano = :ano" +
				" and rad.periodo = :periodo");
		if (idServidor > 0) 
			sql.append(" and s.id_servidor = :idServidor");
		if (idCentro > 0) 
			sql.append(" and c.id_unidade = :idCentro");
		if (idDepartamento > 0) 
			sql.append(" and d.id_unidade = :idDepartamento");
		sql.append(" order by p.nome, ccd.nome, t.codigo, g.titulo, pg.ordem");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		if (idServidor > 0)
			q.setInteger("idServidor",idServidor);
		if (idCentro > 0)
			q.setInteger("idCentro",idCentro);
		if (idDepartamento > 0)
			q.setInteger("idDepartamento",idDepartamento);
		@SuppressWarnings("unchecked")
		List<Object[]> bulk = q.list();
		List<ResultadoAvaliacaoDocente> lista = null;
		if (!ValidatorUtil.isEmpty(bulk)) {
			lista = new ArrayList<ResultadoAvaliacaoDocente>();
			TreeMap<Integer, ResultadoAvaliacaoDocente> mapa = new TreeMap<Integer, ResultadoAvaliacaoDocente>();
			for (Object[] obj : bulk){
				int i = 0;
				Integer idResultado = (Integer) obj[i++];// id_resultado_avaliacao_docente; 
				Integer idDocenteTurma = (Integer) obj[i++]; // id_docente_turma;
				Integer idDocente = (Integer) obj[i++];
				Integer idDocenteExterno = (Integer) obj[i++];
				String  nomeDocente  = (String) obj[i++]; // nome as nomeDocente; 
				String  nomeComponente  = (String) obj[i++]; // nome as nomeComponente; 
				String  codigoComponente = (String) obj[i++]; // codigo as codigoComponente;
				Integer idTurma = (Integer) obj[i++];
				String  codigoTurma = (String) obj[i++]; // codigo as codigoTurma;
				String  descricaoHorario = (String) obj[i++];
				Integer numTrancamentos = (Integer) obj[i++]; // num_trancamentos as numTrancamentos; 
				Integer numDiscentes = (Integer) obj[i++]; // num_discente as numDiscentes; 
				BigDecimal mediaGeral = (BigDecimal) obj[i++]; // media_geral as mediaGeral; 
				BigDecimal desvioPadraoGeral = (BigDecimal) obj[i++]; // desvio_padrao_geral as desvioPadraoGeral;
				Integer idMediaNotas = (Integer) obj[i++];// mn.id_media_notas as idMediaNotas," +
				BigDecimal media = (BigDecimal) obj[i++];// mn.media," +
				BigDecimal desvioPadrao = (BigDecimal) obj[i++];// mn.desvio_padrao as desvioPadrao," +
				Integer idPercentualSimNao = (Integer) obj[i++];// psn.id_percentual_sim_nao," +
				BigDecimal percentualSim = (BigDecimal) obj[i++];// psn.percentual_sim," +
				BigDecimal percentualNao = (BigDecimal) obj[i++];// psn.percentual_nao," +
				Integer idPergunta = (Integer) obj[i++];
				String  descricaoPergunta = (String) obj[i++]; // descricao as descricaoPergunta
				Integer ordemPergunta = (Integer) obj[i++];
				Integer idGrupo = (Integer) obj[i++];
				String  titulogrupo = (String) obj[i++]; // titulo as tituloGrupo; 
				String  descricaoGrupo = (String) obj[i++]; // descricao as descricaoGrupo; 
				
				ResultadoAvaliacaoDocente resultado = mapa.get(idResultado);
				if (resultado == null) {
					ComponenteCurricular disciplina = new ComponenteCurricular();
					disciplina.setNome(nomeComponente);
					disciplina.setCodigo(codigoComponente);
					
					Turma turma = new Turma(idTurma);
					turma.setDisciplina(disciplina);
					turma.setCodigo(codigoTurma);
					turma.setDescricaoHorario(descricaoHorario);

					Servidor servidor = new Servidor(idDocente);
					servidor.setPessoa(new Pessoa(0, nomeDocente));
					
					DocenteExterno docenteExterno = null;
					
					if (idDocenteExterno != null){
						docenteExterno = new DocenteExterno(idDocenteExterno);
						docenteExterno.setServidor(servidor);
					}
					
					DocenteTurma docenteTurma = new DocenteTurma(idDocenteTurma);
					docenteTurma.setTurma(turma);
					if (idDocenteExterno != null){
						docenteTurma.setDocenteExterno(docenteExterno);
					} else {
						docenteTurma.setDocente(servidor);
					}
					
					resultado = new ResultadoAvaliacaoDocente(idResultado);
					resultado.setDocenteTurma(docenteTurma);
					resultado.setNumTrancamentos(numTrancamentos);
					resultado.setNumDiscentes(numDiscentes);
					resultado.setMediaGeral(mediaGeral != null ? mediaGeral.doubleValue() : null);
					resultado.setDesvioPadraoGeral(desvioPadraoGeral != null ? desvioPadraoGeral.doubleValue() : null);
					resultado.setMediaNotas(new ArrayList<MediaNotas>());
					resultado.setPercentualRespostasSimNao(new ArrayList<PercentualSimNao>());
				}
				GrupoPerguntas grupo = new GrupoPerguntas(idGrupo);
				grupo.setTitulo(titulogrupo);
				grupo.setDescricao(descricaoGrupo);
				
				Pergunta pergunta = new Pergunta(idPergunta);
				pergunta.setOrdem(ordemPergunta);
				pergunta.setDescricao(descricaoPergunta);
				pergunta.setGrupo(grupo);
				
				if (idMediaNotas != null) {
					MediaNotas mediaNotas = new MediaNotas(idMediaNotas);
					mediaNotas.setMedia(media.doubleValue());
					mediaNotas.setDesvioPadrao(desvioPadrao.doubleValue());
					mediaNotas.setPergunta(pergunta);
					mediaNotas.setResultadoAvaliacaoDocente(resultado);
					
					resultado.getMediaNotas().add(mediaNotas);
				} else if (idPercentualSimNao != null) {
					PercentualSimNao percentual = new PercentualSimNao(idPercentualSimNao);
					percentual.setPercentualNao(percentualNao.doubleValue());
					percentual.setPercentualSim(percentualSim.doubleValue());
					percentual.setPergunta(pergunta);
					percentual.setResultadoAvaliacaoDocente(resultado);
					
					resultado.getPercentualRespostasSimNao().add(percentual);
				}
				mapa.put(idResultado, resultado);
			}
			for (Integer id : mapa.keySet())
				lista.add(mapa.get(id));
		}
		return lista;
	}

	/** Calcula a média das notas de um docenteTurma, ignorando avaliações que possuam respostas nulas às perguntas especificadas.
	 * @param idDocenteTurma ID do DocenteTurma a calcular as médias das notas.
	 * @param idPerguntas lista de perguntas que determinam se a avaliação do discente será ignorada.
	 * @param idFormulario
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<MediaNotas> calculaMediaNotas(int idDocenteTurma, int idTurmaDocenciaAssistida, Collection<Integer> idAvaliacoesInvalidas, int idFormulario) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select id_pergunta,"
			+ " id_grupo,"
			+ " case when qtd_notas > 0 then round(somatorio_notas/cast(qtd_notas as numeric), 2) else null end as media,"
			+ " desvio_padrao from ("
			+ " select pergunta.id as id_pergunta,"
			+ "  grupo.id as id_grupo,"
			+ "  sum(case when resposta = -1 then 0 else resposta end) as somatorio_notas,"
			+ "  sum(case when resposta = -1 then 0 else 1 end) as qtd_notas,"
			+ "  stddev_samp(case when resposta = -1 then null else resposta end) as desvio_padrao"
			+ " from avaliacao.pergunta"
			+ "  inner join avaliacao.grupo_perguntas grupo on (pergunta.id_grupo = grupo.id)"
			+ "  inner join avaliacao.resposta_pergunta on (pergunta.id = id_pergunta)"
			+ "  inner join avaliacao.avaliacao_institucional on (avaliacao_institucional.id = resposta_pergunta.id_avaliacao)"
			+ "  inner join avaliacao.grupo_perguntas on (grupo_perguntas.id = pergunta.id_grupo)"
			+ " where avaliacao_institucional.id_discente is not null"
			+ "  and avaliacao_institucional.finalizada = trueValue()"
			+ "  and avaliacao_institucional.id_formulario_avaliacao = :idFormulario"
			+ "  and tipo_pergunta = :tipoPergunta"
			+ "  and resposta != -1");
		if (idDocenteTurma > 0)
			sql.append("  and resposta_pergunta.id_docente_turma = :idDocenteTurma");
		if (idTurmaDocenciaAssistida > 0)
			sql.append("  and resposta_pergunta.id_turma_docencia_assistida= :idTurmaDocenciaAssistida");
		if (!ValidatorUtil.isEmpty(idAvaliacoesInvalidas)) {
			sql.append(" and avaliacao_institucional.id not in "+ UFRNUtils.gerarStringIn(idAvaliacoesInvalidas));
		}
		sql.append(" group by pergunta.id, grupo.id ) subquery");
		SQLQuery query = getSession().createSQLQuery(sql.toString());
		query.setInteger("tipoPergunta", TipoPergunta.PERGUNTA_NOTA.ordinal());
		query.setInteger("idFormulario", idFormulario);
		if (idDocenteTurma > 0)
			query.setInteger("idDocenteTurma", idDocenteTurma);
		if (idTurmaDocenciaAssistida > 0)
			query.setInteger("idTurmaDocenciaAssistida", idTurmaDocenciaAssistida);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		Collection<MediaNotas> medias = null;
		if (lista != null) {
			medias = new ArrayList<MediaNotas>();
			for (Object[] obj : lista) {
				int i = 0;
				Integer idPergunta = (Integer) obj[i++];
				Integer idGrupo = (Integer) obj[i++];
				BigDecimal mediaCalculada = (BigDecimal) obj[i++];
				BigDecimal desvioPadrao = (BigDecimal) obj[i++];
				MediaNotas media = new MediaNotas();
				media.setPergunta(new Pergunta(idPergunta));
				media.getPergunta().setGrupo(new GrupoPerguntas(idGrupo));
				if (mediaCalculada != null) 
					media.setMedia(mediaCalculada.doubleValue());
				else 
					media.setMedia(0.0);
				if (desvioPadrao != null)
					media.setDesvioPadrao(desvioPadrao.doubleValue());
				else
					media.setDesvioPadrao(0.0);
				medias.add(media);
			}
		}
		return medias;
	}

	/** Retorna as médias das notas de um docenteTurma.
	 * @param idDocenteTurma ID do DocenteTurma a calcular as médias das notas.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<MediaNotas> findMediaNotasByDocenteTurma(int idDocenteTurma) throws HibernateException, DAOException {
		String sql = "select id_media_notas,"
			+ " id_docente_turma,"
			+ " id_pergunta,"
			+ " media,"
			+ " desvio_padrao"
			+ " from avaliacao.media_notas mn"
			+ " inner join avaliacao.resultado_avaliacao_docente rad using (id_resultado_avaliacao_docente)"
			+ " where rad.id_docente_turma = ?"
			+ " order by id_pergunta";
		@SuppressWarnings("unchecked")
		Collection<MediaNotas> medias = getJdbcTemplate().query(sql, new Object[] {idDocenteTurma }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MediaNotas media = new MediaNotas(rs.getInt("id_media_notas"));
				media.setPergunta(new Pergunta(rs.getInt("id_pergunta")));
				media.setMedia(rs.getDouble("media"));
				media.setDesvioPadrao(rs.getDouble("desvio_padrao"));
				return media;
			}
		});
		return medias;
	}
	
	/** Retorna os percentuais de respostas sim/não um docenteTurma.
	 * @param idDocenteTurma ID do DocenteTurma a calcular as médias das notas.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<PercentualSimNao> findPercentuaisByDocenteTurma(int idDocenteTurma) throws HibernateException, DAOException {
		String sql = "select id_percentual_sim_nao,"
			+ " id_pergunta,"
			+ " percentual_sim,"
			+ " percentual_nao,"
			+ " id_resultado_avaliacao_docente"
			+ " from avaliacao.percentual_sim_nao psn"
			+ " inner join avaliacao.resultado_avaliacao_docente rad using (id_resultado_avaliacao_docente)"
			+ " where rad.id_docente_turma = ?"
			+ " order by id_pergunta";
		@SuppressWarnings("unchecked")
		Collection<PercentualSimNao> percentuais = getJdbcTemplate().query(sql, new Object[] {idDocenteTurma }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				PercentualSimNao percentual = new PercentualSimNao(rs.getInt("id_percentual_sim_nao"));
				percentual.setPergunta(new Pergunta(rs.getInt("id_pergunta")));
				percentual.setPercentualSim(rs.getDouble("percentual_sim"));
				percentual.setPercentualNao(rs.getDouble("percentual_nao"));
				percentual.setResultadoAvaliacaoDocente(new ResultadoAvaliacaoDocente(rs.getInt("id_percentual_sim_nao")));
				return percentual;
			}
		});
		return percentuais;
	}


	/** Calcula o percentual de respostas sim/não de um docenteTurma.
	 * @param idDocenteTurma ID do DocenteTurma a calcular as médias das notas.
	 * @param idAvaliacoesInvalidas
	 * @param idFormulario
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<PercentualSimNao> calculaPercentualSimNao(int idDocenteTurma, int idTurmaDocenciaAssistida, Collection<Integer> idAvaliacoesInvalidas, int idFormulario) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select id_pergunta,"
			+ " cast(sum(case when resposta = 1 then 1 else 0 end) as integer) as total_sim,"
			+ " cast(sum(case when resposta = -1 then 1 else 0 end) as integer) as total_nao,"
			+ " cast(count(resposta) as integer) as total_respostas"
			+ " from avaliacao.pergunta"
			+ " inner join avaliacao.resposta_pergunta on (pergunta.id = id_pergunta)"
			+ " inner join avaliacao.avaliacao_institucional on (avaliacao_institucional.id = resposta_pergunta.id_avaliacao)"
			+ " inner join avaliacao.grupo_perguntas on (grupo_perguntas.id = pergunta.id_grupo)"
			+ " where avaliacao_institucional.id_discente is not null"
			+ " and avaliacao_institucional.finalizada = trueValue()"
			+ " and tipo_pergunta = :tipoPergunta"
			+ " and avaliacao_institucional.id_formulario_avaliacao = :idFormulario");
		if (idDocenteTurma >0 )
			sql.append(" and id_docente_turma = :idDocenteTurma");
		if (idTurmaDocenciaAssistida >0 )
			sql.append(" and id_turma_docencia_assistida = :idTurmaDocenciaAssistida");
		if (!ValidatorUtil.isEmpty(idAvaliacoesInvalidas)) {
			sql.append(" and avaliacao_institucional.id not in " + UFRNUtils.gerarStringIn(idAvaliacoesInvalidas));
		}
		sql.append(" group by id_pergunta");
		SQLQuery query = getSession().createSQLQuery(sql.toString());
		query.setInteger("tipoPergunta", TipoPergunta.PERGUNTA_SIM_NAO.ordinal());
		query.setInteger("idFormulario", idFormulario);
		if (idDocenteTurma > 0)
			query.setInteger("idDocenteTurma", idDocenteTurma);
		if (idTurmaDocenciaAssistida > 0)
			query.setInteger("idTurmaDocenciaAssistida", idTurmaDocenciaAssistida);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		Collection<PercentualSimNao> percentuais = null;
		if (!isEmpty(lista)) {
			percentuais = new ArrayList<PercentualSimNao>();
			for (Object[] obj : lista) {
				int i = 0;
				Integer idPergunta = (Integer) obj[i++];
				Integer totalSim = (Integer) obj[i++];
				Integer totalNao = (Integer) obj[i++];
				Integer totalRespostas = (Integer) obj[i++];
				PercentualSimNao percentual = new PercentualSimNao();
				percentual.setPergunta(new Pergunta(idPergunta));
				if (totalRespostas == 0) {
					percentual.setPercentualSim(new Double(0));
					percentual.setPercentualNao(new Double(0));
				} else {
					percentual.setPercentualSim(new Double(100 * ((double) totalSim / totalRespostas)));
					percentual.setPercentualNao(new Double(100 * ((double) totalNao / totalRespostas)));
				}
				percentuais.add(percentual);
			}
		}
		return percentuais;
	}

	/** Calcula o desvio padrão geral das notas dadas a um DocenteTurma
	 * @param idDocenteTurma ID do DocenteTurma a calcular as médias das notas.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Double findDesvioPadraoGeral(int idDocenteTurma, int idTurmaDocenciaAssistida, Collection<Integer> idAvaliacoesInvalidas, int idFormulario) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select stddev_samp(resposta) as desvio_padrao"
			+ " from avaliacao.pergunta"
			+ " inner join avaliacao.resposta_pergunta on (pergunta.id = id_pergunta)"
			+ " inner join avaliacao.avaliacao_institucional on (avaliacao_institucional.id = resposta_pergunta.id_avaliacao)"			
			+ " inner join avaliacao.grupo_perguntas on (grupo_perguntas.id = pergunta.id_grupo)"
			+ " where avaliacao_institucional.id_discente is not null"
			+ " and avaliacao_institucional.finalizada = trueValue()"
			+ " and avaliacao_institucional.id_formulario_avaliacao = :idFormulario"
			+ " and tipo_pergunta = :tipoPergunta"
			+ " and resposta != -1");
		if (idDocenteTurma > 0) 
			sql.append(" and id_docente_turma = :idDocenteTurma");
		if (idTurmaDocenciaAssistida > 0) 
			sql.append(" and id_turma_docencia_assistida = :idTurmaDocenciaAssistida");
		if (!ValidatorUtil.isEmpty(idAvaliacoesInvalidas)) {
			sql.append(" and avaliacao_institucional.id not in " + UFRNUtils.gerarStringIn(idAvaliacoesInvalidas));
		}
		SQLQuery query = getSession().createSQLQuery(sql.toString());
		query.setInteger("tipoPergunta", TipoPergunta.PERGUNTA_NOTA.ordinal());
		query.setInteger("idFormulario", idFormulario);
		if (idDocenteTurma > 0) 
			query.setInteger("idDocenteTurma", idDocenteTurma);
		if (idTurmaDocenciaAssistida > 0) 
			query.setInteger("idTurmaDocenciaAssistida", idTurmaDocenciaAssistida);
		BigDecimal desvioPadrao = (BigDecimal) query.uniqueResult();
		if (desvioPadrao == null)
			return new Double(0);
		else
			return desvioPadrao.doubleValue();
	}


	/** Busca docentes e ano/período da avaliação por nome. 
	 * @param nome
	 * @param idCentro caso o valor seja 0, não será utilizado na busca.
	 * @param idDepartamento caso o valor seja 0, não será utilizado na busca.
	 * @param ano
	 * @param periodo
	 * @return lista com valores mapeados com as chaves {id_servidor, nome, ano, periodo}
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findDocenteAnoPeriodoByNomeCentroDepartamento(String nome, int idCentro, int idDepartamento, int ano, int periodo) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select distinct s.id_servidor," 
				+ " p.nome, t.ano, t.periodo, dep.nome as departamento"
				+ " from rh.servidor s" 
				+ " inner join comum.unidade dep using (id_unidade)"
				+ " inner join comum.unidade centro on (dep.id_gestora = centro.id_unidade)"
				+ " inner join comum.pessoa p using (id_pessoa)"
				+ " left join ensino.docente_turma dt on (dt.id_docente = s.id_servidor)"
				+ " left join ensino.docente_externo using (id_servidor)"
				+ " inner join avaliacao.resultado_avaliacao_docente using (id_docente_turma)"
				+ " inner join avaliacao.media_notas mn using (id_resultado_avaliacao_docente)" 
				+ " inner join ensino.turma t using (id_turma)"
				+ " where (t.distancia = falseValue() or t.id_polo is null)");
		if (nome != null) sql.append(" and p.nome_ascii ilike :nome"); 
		if (idCentro != 0) sql.append(" and centro.id_unidade = :idCentro");
		if (idDepartamento != 0) sql.append(" and dep.id_unidade = :idDepartamento");
		if (ano != 0 && periodo != 0) sql.append(" and t.ano = :ano and t.periodo = :periodo");
		sql.append(" order by nome, ano desc, periodo desc"); 
		Query q = getSession().createSQLQuery(sql.toString());
		if (nome != null) q.setString("nome", nome + "%"); 
		if (idCentro != 0) q.setInteger("idCentro", idCentro);
		if (idDepartamento != 0) q.setInteger("idDepartamento", idDepartamento);
		if (ano != 0 && periodo != 0){
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		for (Object[] rs : lista) {
			Map<String, Object> mapa = new HashMap<String, Object>();
			int i = 0;
			mapa.put("id_servidor", rs[i++]);
			mapa.put("nome", rs[i++]);
			mapa.put("ano", rs[i++]);
			mapa.put("periodo", rs[i++]);
			mapa.put("departamento", rs[i++]);
			resultado.add(mapa);
		}
		return resultado;
	}

	/** Retorna uma lista de resultados processados do docente. 
	 * @param nome lista com valores mapeados com as chaves {id_servidor, nome, ano, periodo}
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> findResultadoByDocente(int idServidor) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select distinct s.id_servidor, p.nome, t.ano, t.periodo"
				+ " from rh.servidor s"
				+ " inner join comum.pessoa p using (id_pessoa)"
				+ " left join ensino.docente_turma dt on (dt.id_docente = s.id_servidor)"
				+ " left join ensino.docente_externo using (id_servidor)"
				+ " inner join avaliacao.resultado_avaliacao_docente using (id_docente_turma)"
				+ " inner join ensino.turma t using (id_turma)"
				+ " where (t.distancia is null or t.distancia = falseValue() or t.id_polo is null)" 
				+ " and s.id_servidor = :idServidor");
		sql.append(" order by nome, ano, periodo"); 
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("idServidor", idServidor);

		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		for (Object[] rs : lista) {
			Map<String, Object> mapa = new HashMap<String, Object>();
			int i = 0;
			mapa.put("id_servidor", rs[i++]);
			mapa.put("nome", rs[i++]);
			mapa.put("ano", rs[i++]);
			mapa.put("periodo", rs[i++]);
			mapa.put("anoPeriodo", mapa.get("ano") + "." + mapa.get("periodo"));
			resultado.add(mapa);
		}
		return resultado;
	}


	/**
	 * Retorna dados para a geração do relatório individual do docente na
	 * Avaliação Institucional. Os dados são agrupados por GrupoPerguntas e
	 * Pergunta.
	 * 
	 * @param idDocenteTurma
	 * @param ano
	 * @param periodo
	 * @return Mapa de dados agrupados no formato GrupoPerguntas -> Pergunta -> ItemRelatorioAvaliacaoInstitucionalDocenteTurma
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao> findRespostasAvaliacaoDocenteTurma(int idDocenteTurma, int ano, int periodo, boolean excluirReprovacoesFalta) throws HibernateException, DAOException {
		
		ParametroProcessamentoAvaliacaoInstitucional parametroProcessamento = findUltimoProcessamento(ano, periodo);
		
		StringBuilder sql = new StringBuilder( 
			"select rp.id as idRespostaPergunta,"
			+ "  p.id as idPergunta,"
			+ "  gp.id as idGrupoPergunta,"
			+ "  ai.id as idAvaliacaoInstitucional,"
			+ "  ai.id_discente as idDiscente,"
			+ "  discente.matricula,"
			+ "  rp.resposta as resposta,"
			+ "  p.descricao as descricaoPergunta,"
			+ "  p.tipo_pergunta as tipoPergunta,"
			+ "  gp.descricao as descricaoGrupoPergunta,"
			+ "  gp.titulo as titulo,"
			+ "  ai.observacoes as observacoes"
			+ " from avaliacao.avaliacao_institucional ai"
			+ "  inner join avaliacao.resposta_pergunta rp on (rp.id_avaliacao=ai.id)"
			+ "  inner join avaliacao.pergunta p on rp.id_pergunta=p.id"
			+ "  inner join avaliacao.grupo_perguntas gp on p.id_grupo=gp.id"
			+ "  inner join ensino.docente_turma using (id_docente_turma)"
			+ "  inner join discente using (id_discente)"
			+ " where ai.finalizada = trueValue()"
			+ "  and ai.ano = :ano"
			+ "  and ai.periodo = :periodo"
			+ "  and rp.id_docente_turma = :idDocenteTurma"
			// docente não avaliacao
			+ "  and rp.id_docente_turma not in (" +
					" select id_docente_turma" +
					" from avaliacao.docente_turma_invalido" +
					" inner join avaliacao.parametro_processamento_avaliacao ppa using (id_parametro_processamento)" +
					" where ppa.id_parametro_processamento = :idParametroProcessamento" +
					")"
			// avaliação não computada
			+ "  and (rp.id_avaliacao, rp.id_docente_turma) not in (" +
					" select id_avaliacao, id_docente_turma" +
					" from avaliacao.avaliacao_docente_turma_invalida" +
					" inner join avaliacao.parametro_processamento_avaliacao ppa using (id_parametro_processamento)" +
					" where ppa.id_parametro_processamento = :idParametroProcessamento" +
					")"
			+ " order by p.id_grupo, p.id, ai.id");
		Query qNotas = getSession().createSQLQuery(sql.toString());
		qNotas.setInteger("idDocenteTurma", idDocenteTurma);
		qNotas.setInteger("ano", ano);
		qNotas.setInteger("periodo", periodo);
		qNotas.setInteger("idParametroProcessamento", parametroProcessamento.getId());

		@SuppressWarnings("unchecked")
		List<Object[]> resultSet = qNotas.list();
		if (resultSet == null || resultSet.isEmpty())
			return null;
		// cria os objetos
		List<RespostaPergunta> respostas = new ArrayList<RespostaPergunta>();
		for (Object[] obj : resultSet) {
			int i = 0;
			Integer idRespostaPergunta = (Integer) obj[i++]; // select rp.id as idRespostaPergunta,"
			Integer idPergunta = (Integer) obj[i++]; //  p.id as idPergunta,"
			Integer idGrupoPergunta = (Integer) obj[i++]; //  gp.id as idGrupoPergunta,"
			Integer idAvaliacaoInstitucional = (Integer) obj[i++]; //  ai.id as idAvaliacaoInstitucional,"
			Integer idDiscente = (Integer) obj[i++]; //  ai.id_discente as idDiscente,"
			BigInteger matricula = (BigInteger) obj[i++]; //  ai.id_discente as idDiscente,"
			Integer resposta = (Integer) obj[i++]; //  rp.resposta as resposta,"
			String descricaoPergunta = (String) obj[i++]; //  p.descricao as descricaoPergunta,"
			Integer tipoPergunta = (Integer) obj[i++]; //  p.tipo_pergunta as tipoPergunta,"
			String descricaoGrupoPergunta = (String) obj[i++]; //  gp.descricao as descricaoGrupoPergunta,"
			String titulo = (String) obj[i++]; //  gp.titulo as título,"
			String observacoes = (String) obj[i++]; //  ai.observacoes as observações,"

			AvaliacaoInstitucional avaliacao = new AvaliacaoInstitucional();
			avaliacao.setId(idAvaliacaoInstitucional);
			avaliacao.setAno(ano);
			avaliacao.setPeriodo(periodo);
			avaliacao.setObservacoes(observacoes);
			avaliacao.setDiscente(new Discente(idDiscente, matricula.longValue(),""));

			DocenteTurma docenteTurma = new DocenteTurma(idDocenteTurma);

			GrupoPerguntas grupo = new GrupoPerguntas(idGrupoPergunta);
			grupo.setDescricao(descricaoGrupoPergunta);
			grupo.setTitulo(titulo);

			Pergunta pergunta = new Pergunta(idPergunta);
			pergunta.setDescricao(descricaoPergunta);
			pergunta.setGrupo(grupo);
			pergunta.setTipoPergunta(TipoPergunta.valueOf(tipoPergunta));

			RespostaPergunta respostaPergunta = new RespostaPergunta(idRespostaPergunta);
			respostaPergunta.setResposta(resposta);
			respostaPergunta.setAvaliacao(avaliacao);
			respostaPergunta.setDocenteTurma(docenteTurma);
			respostaPergunta.setPergunta(pergunta);

			respostas.add(respostaPergunta);
		}
		// respostas agrupadas por GrupoPerguntas, Discente
		Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao> 
		mapaGrupo = new TreeMap<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>();
		// Agrupa as respostas e anexa mais dados para geração do relatório.
		// médias a anexar ao relatório
		Collection<MediaNotas> medias = findMediaNotasByDocenteTurma(idDocenteTurma);
		// percentual sim/não a anexar ao relatório
		Collection<PercentualSimNao> percentuais = findPercentuaisByDocenteTurma(idDocenteTurma);
		for (RespostaPergunta resposta : respostas) {
			// grupo de perguntas (dimensão)
			TabelaRespostaResultadoAvaliacao mapaDiscente = mapaGrupo.get(resposta.getPergunta().getGrupo());
			if (mapaDiscente == null) mapaDiscente = new TabelaRespostaResultadoAvaliacao();
			// grupo de respostas (pergunta)
			Map<Discente, List<RespostaPergunta>> mapaRespostas = mapaDiscente.getMapaRespostas();
			// Pergunta ainda não agrupada
			if (mapaRespostas == null) {
				mapaRespostas = new LinkedHashMap<Discente, List<RespostaPergunta>>();
			}
			List<RespostaPergunta> listaRespostas = mapaRespostas.get(resposta.getAvaliacao().getDiscente());
			if (listaRespostas == null) listaRespostas = new ArrayList<RespostaPergunta>();
			if (!listaRespostas.contains(resposta)) {
				listaRespostas.add(resposta);
			}
			// mapeia de volta
			mapaRespostas.put(resposta.getAvaliacao().getDiscente(), listaRespostas);
			mapaDiscente.setMapaRespostas(mapaRespostas);
			mapaGrupo.put(resposta.getPergunta().getGrupo(), mapaDiscente);
			// adicionando as médias e percentuais
			if (mapaDiscente.getMedias() == null) mapaDiscente.setMedias(new ArrayList<MediaNotas>());
			for (MediaNotas media : medias) {
				if (media.getPergunta().equals(resposta.getPergunta())
						&& !mapaDiscente.getMedias().contains(media)) {
					mapaDiscente.getMedias().add(media);
					break;
				}

			}
			if (mapaDiscente.getPercentuais() == null) mapaDiscente.setPercentuais(new ArrayList<PercentualSimNao>());
			for (PercentualSimNao percentual : percentuais) {
				if (percentual.getPergunta().equals(resposta.getPergunta())
						&& !mapaDiscente.getPercentuais().contains(percentual)) {
					mapaDiscente.getPercentuais().add(percentual);
					break;
				}

			}
		}
		return mapaGrupo;
	}

	/** Retorna as informações utilizadas nos últimos processamentos de uma Avaliação Institucional.
	 * @return
	 * @throws DAOException
	 */
	public Collection<ParametroProcessamentoAvaliacaoInstitucional> findUltimoProcessamentos() throws DAOException {
		try {
			String sql = "select id_parametro_processamento" +
					" from avaliacao.parametro_processamento_avaliacao" +
					" inner join (" +
					"select ano, periodo, id_formulario_avaliacao, max(fim_processamento) as fim_processamento" +
					" from avaliacao.parametro_processamento_avaliacao" +
					" group by ano, periodo, id_formulario_avaliacao) subQuery using (ano, periodo, fim_processamento, id_formulario_avaliacao)";
			Query q = getSession().createSQLQuery(sql);
			@SuppressWarnings("unchecked")
			List<Integer> ids = q.list();
			if (ValidatorUtil.isEmpty(ids)) return null;
			Criteria c = getSession().createCriteria(ParametroProcessamentoAvaliacaoInstitucional.class)
				.add(Restrictions.in("id", ids))
				.addOrder(Order.asc("ano"))
				.addOrder(Order.asc("periodo"));
			@SuppressWarnings("unchecked")
			Collection<ParametroProcessamentoAvaliacaoInstitucional> lista = c.list();
			return lista;
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}
	
	/** Retorna as informações utilizadas no último processamento de uma Avaliação Institucional de um ano-período.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public ParametroProcessamentoAvaliacaoInstitucional findUltimoProcessamento(int ano, int periodo) throws DAOException {
		try {
			String sql = "select id_parametro_processamento" +
					" from avaliacao.parametro_processamento_avaliacao" +
					" inner join (" +
					"select ano, periodo, max(fim_processamento) as fim_processamento" +
					" from avaliacao.parametro_processamento_avaliacao" +
					" group by ano, periodo) subQuery using (ano, periodo, fim_processamento)" +
					" inner join avaliacao.formulario_avaliacao using (id_formulario_avaliacao)" +
					" where tipo_avaliacao = :tipoAvaliacao";
			Query q = getSession().createSQLQuery(sql);
			q.setInteger("tipoAvaliacao", TipoAvaliacaoInstitucional.AVALIACAO_DISCENTE_GRADUACAO);
			@SuppressWarnings("unchecked")
			List<Integer> ids = q.list();
			if (ValidatorUtil.isEmpty(ids)) return null;
			Criteria c = getSession().createCriteria(ParametroProcessamentoAvaliacaoInstitucional.class)
				.add(Restrictions.in("id", ids))
				.add(Restrictions.eq("ano", ano))
				.add(Restrictions.eq("periodo", periodo));
			return (ParametroProcessamentoAvaliacaoInstitucional) c.uniqueResult();
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna uma lista de {@link AvaliacaoDocenteTurmaInvalida
	 * idAvaliacao/idDocenteTurma} que serão consideradas inválidas. Uma
	 * Avaliação é válida para a Comissão Própria de Avaliação se o docente
	 * tiver no mínimo 'n' Avaliações (normalmente 5), e se o discente não
	 * respondeu 0 ou N/A à um grupo determinado de perguntas.
	 * 
	 * @param ano
	 * @param periodo
	 * @param idPerguntas
	 * @param idDocenteTurmas id do DocenteTurma. Caso zero, não será utilizado na consulta.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void determinaAvaliacoesNaoComputadas(ParametroProcessamentoAvaliacaoInstitucional parametroProcessamento) throws HibernateException, DAOException{
		int ano = parametroProcessamento.getAno();
		int periodo = parametroProcessamento.getPeriodo();
		int idFormulario = parametroProcessamento.getFormulario().getId();
		Collection<Integer> idPerguntas = parametroProcessamento.getListaIdPerguntaDeterminanteExclusaoAvaliacao();
		boolean excluiReprovadosFalta = parametroProcessamento.isExcluirRepovacoesFalta();
		int numeroMinimoAvaliacoes = parametroProcessamento.getNumMinAvaliacoes();
		
		List<AvaliacaoDocenteTurmaInvalida> listaAvaliacoeDocenteTurmaInvalidas = new LinkedList<AvaliacaoDocenteTurmaInvalida>();
		List<DocenteTurma> listaDocenteTurmaInvalido = new LinkedList<DocenteTurma>();
		List<TurmaDocenciaAssistida> listaTurmaDocenciaAssistida = new LinkedList<TurmaDocenciaAssistida>();
		String projecao = "idAvaliacao, idDocenteTurma, idTurmaDocenciaAssistida";
		String projecaoSQL = "distinct ai.id, rp.id_docente_turma, rp.id_turma_docencia_assistida";
		String restricaoTipoAvaliacao = "";
		if (parametroProcessamento.getFormulario().isAvaliacaoDocenciaAssistida())
			restricaoTipoAvaliacao = " and rp.id_docente_turma_assistida is not null";
		else 
			restricaoTipoAvaliacao = " and rp.id_docente_turma is not null";
		// consulta de avaliações que serão invalidadas caso o discente tenha respondido com zero, ou resposta inválida a uma das perguntas especificadas
		StringBuilder sqlAvaliacoesInvalidas = new StringBuilder("select ").append(projecaoSQL)
				.append(" from avaliacao.avaliacao_institucional ai"
				+ " inner join avaliacao.resposta_pergunta rp on (rp.id_avaliacao = ai.id)" 
				+ " where ai.finalizada = trueValue()" 
				+ " and ai.id_discente is not null" 
				+ " and ai.id_formulario_avaliacao = :idFormulario"
				+ " and ai.ano = :ano"
				+ " and ai.periodo = :periodo"
				+ " and id_pergunta in " + UFRNUtils.gerarStringIn(idPerguntas)
				+ " and resposta <= 0")
			.append(restricaoTipoAvaliacao)
			.append(" group by ai.id, rp.id_docente_turma, rp.id_turma_docencia_assistida");
		if (!ValidatorUtil.isEmpty(idPerguntas)) {
			SQLQuery query = getSession().createSQLQuery(sqlAvaliacoesInvalidas.toString());
			query.setInteger("idFormulario", idFormulario);
			query.setInteger("ano", ano);
			query.setInteger("periodo", periodo);
			@SuppressWarnings("unchecked")
			Collection<AvaliacaoDocenteTurmaInvalida> listaAvaliacoes = HibernateUtils.parseTo(query.list(), projecao, AvaliacaoDocenteTurmaInvalida.class);
			if (listaAvaliacoes != null){
				listaAvaliacoeDocenteTurmaInvalidas.addAll(listaAvaliacoes);
			} 
		}
		// consulta de avaliações que serão invalidadas caso o discente teve reprovação por falta
		StringBuilder sqlAvaliacoesReprovadosFalta = new StringBuilder("select ").append(projecaoSQL)
				.append(" from avaliacao.avaliacao_institucional ai" 
				+ " inner join avaliacao.resposta_pergunta rp on (rp.id_avaliacao = ai.id)"
				+ " inner join ensino.matricula_componente mc on (ai.id_discente = mc.id_discente and mc.ano = ai.ano and mc.periodo = ai.periodo)"
				+ " where ai.finalizada = trueValue()" 
				+ " and ai.id_discente is not null" 
				+ " and ai.id_formulario_avaliacao = :idFormulario"
				+ " and ai.ano = :ano"
				+ " and ai.periodo = :periodo"
				+ " and mc.id_situacao_matricula in (:idSituacaoMatricula, :idSituacaoRepMediaFalta) ")
			.append(restricaoTipoAvaliacao)
			.append(" group by ai.id, rp.id_docente_turma, rp.id_turma_docencia_assistida");
		if (excluiReprovadosFalta) {
			SQLQuery query = getSession().createSQLQuery(sqlAvaliacoesReprovadosFalta.toString());
			query.setInteger("idFormulario", idFormulario);
			query.setInteger("ano", ano);
			query.setInteger("periodo", periodo);
			query.setInteger("idSituacaoMatricula", SituacaoMatricula.REPROVADO_FALTA.getId());
			query.setInteger("idSituacaoRepMediaFalta", SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
			@SuppressWarnings("unchecked")
			Collection<AvaliacaoDocenteTurmaInvalida> listaAvaliacoes = HibernateUtils.parseTo(query.list(), projecao, AvaliacaoDocenteTurmaInvalida.class);
			if (listaAvaliacoes != null){
				listaAvaliacoeDocenteTurmaInvalidas.addAll(listaAvaliacoes);
			} 
		}
		
		if (parametroProcessamento.getFormulario().isAvaliacaoDocenciaAssistida()) {
			// lista de turmas de docência assistida sem avaliação
			StringBuilder sqlDocentesSemAvaliacao = new StringBuilder(
					"select distinct id_turma_docencia_assistida"
							+ " from stricto_sensu.turma_docencia_assistida tda"
							+ " inner join ensino.turma t using (id_turma)"
							+ " where t.ano = :ano"
							+ " and t.periodo = :periodo"
							+ " except"
							+ " select distinct rp.id_turma_docencia_assistida"
							+ " from avaliacao.resposta_pergunta rp"
							+ " inner join avaliacao.avaliacao_institucional ai on (ai.id = rp.id_avaliacao)"
							+ " where ai.id_formulario_avaliacao = :idFormulario"
							+ " and ai.ano = :ano" + " and ai.periodo = :periodo"
							+ " and ai.id_discente is not null");
			SQLQuery query = getSession().createSQLQuery(sqlDocentesSemAvaliacao.toString());
			query.setInteger("idFormulario", idFormulario);
			query.setInteger("ano", ano);
			query.setInteger("periodo", periodo);
			@SuppressWarnings("unchecked")
			List<Integer> lista = query.list();
			if (lista != null) {
				for (Integer id : lista)
					listaTurmaDocenciaAssistida.add(new TurmaDocenciaAssistida(id));
			}
		} else {
			// lista de docentes sem avaliação
			StringBuilder sqlDocentesSemAvaliacao = new StringBuilder(
					"select distinct id_docente_turma"
							+ " from ensino.docente_turma dt"
							+ " inner join ensino.turma t using (id_turma)"
							+ " where t.ano = :ano"
							+ " and t.periodo = :periodo"
							+ " except"
							+ " select distinct rp.id_docente_turma"
							+ " from avaliacao.resposta_pergunta rp"
							+ " inner join avaliacao.avaliacao_institucional ai on (ai.id = rp.id_avaliacao)"
							+ " where ai.id_formulario_avaliacao = :idFormulario"
							+ " and ai.ano = :ano" + " and ai.periodo = :periodo"
							+ " and ai.id_discente is not null");
			SQLQuery query = getSession().createSQLQuery(sqlDocentesSemAvaliacao.toString());
			query.setInteger("idFormulario", idFormulario);
			query.setInteger("ano", ano);
			query.setInteger("periodo", periodo);
			@SuppressWarnings("unchecked")
			List<Integer> lista = query.list();
			if (lista != null) {
				for (Integer id : lista)
					listaDocenteTurmaInvalido.add(new DocenteTurma(id));
			}
		}
		// lista de idDocenteTurma que não possuem o número mínimo de avaliações
		StringBuilder sqlNumeroAvaliacoes = new StringBuilder(
				"select id_docente_turma, id_turma_docencia_assistida"
				+" from ("
				// quantidade de avaliações preenchidas no ano-período
					+ " select rp.id_docente_turma, rp.id_turma_docencia_assistida, count(distinct ai.id) as total"
					+ " from avaliacao.avaliacao_institucional ai"
					+ " inner join avaliacao.resposta_pergunta rp on (ai.id = rp.id_avaliacao)"
					+ " where ai.finalizada = trueValue()"
					+ " and ai.id_discente is not null"
					+ " and ai.id_formulario_avaliacao = :idFormulario"
					+ " and ai.ano = :ano" + " and ai.periodo = :periodo ")
			.append(restricaoTipoAvaliacao)
			.append(" group by rp.id_docente_turma, rp.id_turma_docencia_assistida");
		// subtrai do total (conta negativo) as avaliações inválidadas
		if (!ValidatorUtil.isEmpty(idPerguntas)) {
			// exceto as que não as perguntas especificadas não possuem resposta válida 
			sqlNumeroAvaliacoes.append(" union ");
			sqlNumeroAvaliacoes.append(sqlAvaliacoesInvalidas.toString().replace(projecaoSQL, "rp.id_docente_turma, rp.id_turma_docencia_assistida, -count(distinct ai.id) as total"));
		}
		if (excluiReprovadosFalta) {
				// exceto as que os discentes foram reprovados por falta
				sqlNumeroAvaliacoes.append(" union ");
				sqlNumeroAvaliacoes.append(sqlAvaliacoesReprovadosFalta.toString().replace(projecaoSQL, "rp.id_docente_turma, rp.id_turma_docencia_assistida, -count(distinct ai.id) as total"));
		}
		sqlNumeroAvaliacoes.append(") sub_consulta"
			+ " group by id_docente_turma, id_turma_docencia_assistida"
			+ " having sum(total) < :numeroMinimoAvaliacoes");
		
		if (numeroMinimoAvaliacoes > 0) {
			SQLQuery query = getSession().createSQLQuery(sqlNumeroAvaliacoes.toString());
			query.setInteger("idFormulario", idFormulario);
			query.setInteger("ano", ano);
			query.setInteger("periodo", periodo);
			query.setInteger("numeroMinimoAvaliacoes", numeroMinimoAvaliacoes);
			if (excluiReprovadosFalta) {
				query.setInteger("idSituacaoMatricula", SituacaoMatricula.REPROVADO_FALTA.getId());
				query.setInteger("idSituacaoRepMediaFalta", SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
			}
			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();
			if (lista != null) {
				for (Object[] id : lista) {
					// avaliação do docente da turma
					if (id[0] != null)
						listaDocenteTurmaInvalido.add(new DocenteTurma((Integer) id[0]));
					if (id[1] != null)
						listaTurmaDocenciaAssistida.add(new TurmaDocenciaAssistida((Integer) id[1]));
				}
			}
		}
		
		parametroProcessamento.setDocentesInvalidos(listaDocenteTurmaInvalido);
		parametroProcessamento.setTurmaDocenciaAssistidaInvalidas(listaTurmaDocenciaAssistida);
		parametroProcessamento.setAvaliacoesDocenteTurmaInvalidas(listaAvaliacoeDocenteTurmaInvalidas);
	}
		
	/**
	 * Exporta os dados brutos da Avaliação Institucional de um ano-período
	 * respondida por discentes. Os dados exportados possuem informações sobre a turma,
	 * componente curricular, centro, departamento, docente e respectivas
	 * respostas ao questionário da Avaliação Institucional.
	 * 
	 * @param ano
	 * @param periodo
	 * @param numeroMinimoAvaliacoes
	 * @param idPerguntas
	 * @param filtrado
	 *            caso true, exportará apenas os dados correspondente ao filtro
	 *            aplicado. Caso false, exportará os dados filtrados (sem os
	 *            dados que correspondem ao filtro).
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String exportarAvaliacaoDiscentes(int ano, int periodo, int idFormularioAvaliacao, boolean somenteDadosFiltrados) throws HibernateException, DAOException {
		ParametroProcessamentoAvaliacaoInstitucional parametro = findUltimoProcessamento(ano, periodo);
		String nomeTabela = "temp_exportacao_" + idFormularioAvaliacao+"_"+ (new Date()).getTime();
		// dados gerais
		String sqlDadosGerais = "create temp table " + nomeTabela+ " as " + 
			" select distinct ai.id as id_avaliacao, " +
			" docente_turma.id_docente_turma, " +
			" turma.ano,  " +
			" turma.periodo, " +
			" discente.matricula, " +
			" status_discente.descricao as status_discente, " +
			" centro.nome as centro, " +
			" departamento.nome as departamento, " +
			" pessoa.nome as docente, " +
			" componente_curricular_detalhes.codigo as codigo, " +
			" componente_curricular_detalhes.nome as disciplina, " +
			" turma.codigo as codigo_turma, " +
			" turma.descricao_horario as horario, " +
			" turma.local as local, " +
			" curso.nome as curso, " +
			" turno.sigla as turno, " +
			" matricula_componente.media_final as media_final, " +
			" situacao_matricula.descricao as situacao_matricula, " +
			" case when matricula_componente.recuperacao is null then 'NÃO' else 'SIM' end as quarta_prova, " +
			" matricula_componente.numero_faltas as faltas, " +
			" observacoes, " +
			" quantidade_trancamentos" +
			" from discente " +
			" left join curso using (id_curso) " +
			" inner join avaliacao.avaliacao_institucional ai using (id_discente)" +
			" inner join ensino.matricula_componente using (id_discente, ano, periodo) " +
			" inner join ensino.turma using (id_turma, ano, periodo) " +
			" left join ensino.docente_turma using (id_turma) " +
			" left join ensino.docente_externo on (docente_externo.id_docente_externo = docente_turma.id_docente_externo) " +
			" left join rh.servidor on (servidor.id_servidor = docente_turma.id_docente or servidor.id_servidor = docente_externo.id_servidor) " +
			" left join comum.pessoa on (servidor.id_pessoa = pessoa.id_pessoa or docente_externo.id_pessoa = pessoa.id_pessoa) " +
			" inner join comum.unidade departamento on (servidor.id_unidade = departamento.id_unidade or docente_externo.id_unidade = departamento.id_unidade) " +
			" inner join comum.unidade centro on (departamento.id_gestora = centro.id_unidade) " +
			" inner join status_discente on (status_discente.status = discente.status)  " +
			" inner join ensino.componente_curricular using (id_disciplina) " +
			" inner join ensino.componente_curricular_detalhes on (componente_curricular.id_detalhe = componente_curricular_detalhes.id_componente_detalhes) " +
			" inner join ensino.situacao_matricula using (id_situacao_matricula) " +
			" left join ensino.turno using (id_turno) " +
			" left join (select id_discente, cast(count(*) as integer) as quantidade_trancamentos " +
			"    from ensino.matricula_componente " +
			"    where ano = :ano " +
			"    and periodo = :periodo " +
			"    and id_situacao_matricula = :idSituacaoTrancamento" +
			"    group by id_discente) trancamentos on (discente.id_discente = trancamentos.id_discente)" +
			" where finalizada=trueValue()" +
			" and ai.ano = :ano " +
			" and ai.periodo = :periodo" +
			" and ai.id_formulario_avaliacao = :idFormularioAvaliacao" +
			" and matricula_componente.id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido());
		
		// cria uma tabela temporária com dos dados a serem exportados
		SQLQuery query = getSession().createSQLQuery(sqlDadosGerais);
		query.setInteger("ano", parametro.getAno());
		query.setInteger("periodo", parametro.getPeriodo());
		query.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		query.setInteger("idSituacaoTrancamento", SituacaoMatricula.TRANCADO.getId());
		query.executeUpdate();
		// agiliza as consultas
		SQLQuery idx = getSession().createSQLQuery("alter table "+nomeTabela+" add primary key (id_avaliacao, id_docente_turma)");
		idx.executeUpdate();
		// carrega as respostas na tabela temporária
		List<String> cabecalhos = carregaRespostasAvaliacao(idFormularioAvaliacao, nomeTabela);
		// consulta a tabela e gera um CSV
		StringBuilder projecao = new StringBuilder("ano, periodo, matricula," +
				" status_discente, centro, departamento, docente, codigo, disciplina," +
				" codigo_turma, horario, local, curso, turno, media_final, situacao_matricula," +
				" quarta_prova, faltas,");
		StringBuilder cabecalho = new StringBuilder(projecao);
		for (String coluna : cabecalhos) {
			projecao.append(coluna).append(", ");
			cabecalho.append(coluna.replace("P_", "").replace("_", ".")).append(",");
		}
		projecao.append(" observacoes, quantidade_trancamentos");
		cabecalho.append(" observacoes, quantidade_trancamentos");
		StringBuilder csv = tabelaToCSV(projecao.toString(), nomeTabela);
		csv.insert(0, cabecalho.toString().replace(',',';')).append("\n");
		return csv.toString();
	}
	
	/** Lê os dados de uma tabela temporária para exportação de um relatório e retorna no formato CSV. 
	 * @param projecao projeção da consulta à tabela, quando não se deseja trazer todas as colunas. 
	 * @param nomeTabela nome da tabela a consultar os dados. A consulta será <code>select projecao from nomeTabela</code>
	 * @return StringBuilder como os dados da tabela no formato CSV
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private StringBuilder tabelaToCSV(String projecao, String nomeTabela) throws HibernateException, DAOException {
		if (isEmpty(projecao)) projecao = "*";
		StringBuilder sqlFinal = new StringBuilder("select ").append(projecao).append(" from " + nomeTabela);
		// o limit serve para não trazer toda a tabela e estourar a memória.
		int limit = 10000;
		int offset = 0;
		boolean temResultado = true;
		StringBuilder str = new StringBuilder();
		while (temResultado) {
			Query qFinal = getSession().createSQLQuery(sqlFinal.toString());
			qFinal.setMaxResults(limit).setFirstResult(offset);
			@SuppressWarnings("unchecked")
			List<Object[]> lista = qFinal.list();
			// monta a tabela CSV
			if (!isEmpty(lista)) {
				for (Object[] linha : lista) {
					for (Object obj : linha) {
						if (obj == null) {
							obj = "";
						} else if (obj instanceof String) {
							obj = "\"" + obj + "\"";
							obj = StringUtils.removeLineBreak((String) obj);
						}
						str.append(obj).append(";");
					}
					if (str.lastIndexOf(String.valueOf(";")) > 0)
						str.delete(str.lastIndexOf(String.valueOf(";")), str.length());
					str.append("\n");
				}
			} else {
				temResultado = false;
			}
			offset += limit;
		}
		if (str.lastIndexOf("\n") > 0)
			str.delete(str.lastIndexOf("\n"), str.length());
		return str;
	}
	
	/**
	 * Exporta os dados brutos da Avaliação Institucional de um ano-período
	 * respondida por docentes. Os dados exportados possuem informações sobre a turma, componente
	 * curricular, centro, departamento, docente e respectivas respostas ao
	 * questionário da Avaliação Institucional.
	 * 
	 * @param ano
	 * @param periodo
	 * @param numeroMinimoAvaliacoes
	 * @param idPerguntas
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String exportarAvaliacaoDocentes(int ano, int periodo, int idFormularioAvaliacao) throws HibernateException, DAOException {
		String nomeTabela = "temp_exportacao_" + idFormularioAvaliacao+"_"+ (new Date()).getTime();
		// dados gerais
		String sqlDadosGerais = "create temp table " + nomeTabela+ " as " +
				"select distinct ai.id as id_avaliacao," +
				" rp.id_docente_turma," +
				" turma.ano, " +
				" turma.periodo," +
				" centro.nome as centro," +
				" departamento.nome as departamento," +
				" pessoa.nome as docente," +
				" componente_curricular_detalhes.codigo as codigo," +
				" componente_curricular_detalhes.nome as disciplina," +
				" turma.codigo as codigo_turma," +
				" turma.descricao_horario as horario," +
				" turma.local as local," +
				" odt.observacoes as observacoes_docente_turma," +
				" ai.observacoes as observacoes_gerais" +
				" from avaliacao.avaliacao_institucional ai" +
				" inner join avaliacao.resposta_pergunta rp on (ai.id = rp.id_avaliacao)" +
				" inner join avaliacao.pergunta p on (rp.id_pergunta = p.id)" +
				" inner join avaliacao.grupo_perguntas grupo on (grupo.id = p.id_grupo)" +
				" inner join ensino.docente_turma using (id_docente_turma)" +
				" left join avaliacao.observacoes_docente_turma odt using (id_avaliacao, id_turma)" +
				" left join ensino.docente_externo on (docente_externo.id_docente_externo = docente_turma.id_docente_externo)" +
				" left join rh.servidor on (servidor.id_servidor = docente_turma.id_docente or servidor.id_servidor = docente_externo.id_servidor)" +
				" left join comum.pessoa on (servidor.id_pessoa = pessoa.id_pessoa or docente_externo.id_pessoa = pessoa.id_pessoa)" +
				" inner join comum.unidade departamento on (servidor.id_unidade = departamento.id_unidade or docente_externo.id_unidade = departamento.id_unidade)" +
				" inner join comum.unidade centro on (departamento.id_gestora = centro.id_unidade)" +
				" inner join ensino.turma using (id_turma)" +
				" inner join ensino.componente_curricular using (id_disciplina)" +
				" inner join ensino.componente_curricular_detalhes on (componente_curricular.id_detalhe = componente_curricular_detalhes.id_componente_detalhes)" +
				" where finalizada=trueValue()" +
				" and ai.ano = :ano" +
				" and ai.periodo = :periodo" + 
				" and ai.id_discente is null" +
				" and ai.id_tutor_orientador is null" +
				" and ai.id_formulario_avaliacao = :idFormularioAvaliacao";
		
		// cria uma tabela temporária com dos dados a serem exportados
		SQLQuery query = getSession().createSQLQuery(sqlDadosGerais);
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
		query.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		query.executeUpdate();
		// cria os índices para otmizar
		getSession().createSQLQuery("create index idx_" + nomeTabela+ " on " + nomeTabela+ "(id_Avaliacao, id_docente_turma)");
		// carrega as respostas na tabela temporária
		List<String> cabecalhos = carregaRespostasAvaliacao(idFormularioAvaliacao, nomeTabela);
		// consulta a tabela e gera um CSV
		StringBuilder projecao = new StringBuilder("ano, " +
				" periodo," +
				" centro," +
				" departamento," +
				" docente," +
				" codigo," +
				" disciplina," +
				" codigo_turma," +
				" horario," +
				" local,");
		StringBuilder cabecalho = new StringBuilder(projecao);
		for (String coluna : cabecalhos) {
			projecao.append(coluna).append(", ");
			cabecalho.append(coluna.replace("P_", "").replace("_", ".")).append(",");
		}
		projecao.append(" observacoes_docente_turma, observacoes_gerais");
		cabecalho.append(" observacoes_docente_turma, observacoes_gerais");
		StringBuilder csv = tabelaToCSV(projecao.toString(), nomeTabela);
		csv.insert(0, cabecalho.toString().replace(',',';')).append("\n");
		return csv.toString();
	}
	
	/**
	 * Exporta os dados brutos da Avaliação Institucional de um ano-período
	 * respondida por docentes. Os dados exportados possuem informações sobre a turma, componente
	 * curricular, centro, departamento, docente e respectivas respostas ao
	 * questionário da Avaliação Institucional.
	 * 
	 * @param ano
	 * @param periodo
	 * @param numeroMinimoAvaliacoes
	 * @param idPerguntas
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String exportarAvaliacaoTutorEad(int ano, int periodo, int idFormularioAvaliacao) throws HibernateException, DAOException {
		String nomeTabela = "temp_exportacao_" + idFormularioAvaliacao+"_"+ (new Date()).getTime();
		// dados gerais
		String sqlDadosGerais = "create temp table " + nomeTabela+ " as " + 
				"select distinct ai.id as id_avaliacao," +
				"  ai.id_tutor_orientador as id_docente_turma," +
				"  ai.ano," +
				"  ai.periodo," +
				"  pessoa.nome as tutor," +
				"  ai.observacoes as observacoes_gerais" +
				" from avaliacao.avaliacao_institucional ai" +
				"  inner join avaliacao.resposta_pergunta rp on (ai.id = rp.id_avaliacao)" +
				"  inner join avaliacao.pergunta p on (rp.id_pergunta = p.id)" +
				"  inner join avaliacao.grupo_perguntas grupo on (grupo.id = p.id_grupo)" +
				"  inner join ead.tutor_orientador using (id_tutor_orientador)" +
				"  inner join comum.pessoa using (id_pessoa)" +
				" where finalizada=trueValue()" +
				" and ai.ano = :ano" +
				" and ai.periodo = :periodo" + 
				" and ai.id_discente is null" +
				" and ai.id_tutor_orientador is not null" +
				" and ai.id_formulario_avaliacao = :idFormularioAvaliacao";
		
		// consulta aos dados gerais
		SQLQuery query = getSession().createSQLQuery(sqlDadosGerais);
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
		query.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		query.executeUpdate();
		// carrega as respostas na tabela temporária
		List<String> cabecalhos = carregaRespostasAvaliacao(idFormularioAvaliacao, nomeTabela);
		// consulta a tabela e gera um CSV
		StringBuilder projecao = new StringBuilder("ano," +
				" periodo," +
				" tutor,");
		StringBuilder cabecalho = new StringBuilder(projecao);
		for (String coluna : cabecalhos) {
			projecao.append(coluna).append(", ");
			cabecalho.append(coluna.replace("P_", "").replace("_", ".")).append(",");
		}
		projecao.append(" observacoes_gerais");
		cabecalho.append(" observacoes_gerais");
		StringBuilder csv = tabelaToCSV(projecao.toString(), nomeTabela);
		csv.insert(0, cabecalho.toString().replace(',',';')).append("\n");
		return csv.toString();
	}
	
	/** Exporta as respostas de uma Avaliação Institucional.
	 * @param dadosAvaliacao
	 * @param listaIdAvaliacoesInvalidas
	 * @param discente
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void exportaRespostasAvaliacao(int idAvaliacao, int idDocenteTurma, TabelaCSV tabelaCSV, int linha, Map<Integer, String> legendasId) throws HibernateException, DAOException {
		// respostas
		StringBuilder sqlRespostas = new StringBuilder("select " +
//			" ai.id," +
//			" resposta_pergunta.id_docente_turma," +
			" pergunta.id as id_pergunta," +
			" pergunta.tipo_pergunta," +
			" alternativa_pergunta.numero," +
			" alternativa_pergunta.permite_citacao," +
			" resposta_pergunta.resposta," +
			" resposta_pergunta.citacao" +
			" from avaliacao.avaliacao_institucional ai" +
			" inner join avaliacao.resposta_pergunta on (id_avaliacao = ai.id)" +
			" inner join avaliacao.pergunta on (resposta_pergunta.id_pergunta = pergunta.id)" +
			" inner join avaliacao.grupo_perguntas on (pergunta.id_grupo = grupo_perguntas.id)" +
			" left join avaliacao.alternativa_pergunta on (pergunta.id = alternativa_pergunta.id_pergunta and resposta_pergunta.resposta = alternativa_pergunta.id)" +
			" where ai.id = :idAvaliacao" +
			" and (resposta_pergunta.id_docente_turma = :idDocenteTurma or resposta_pergunta.id_docente_turma is null)");
		// consulta às respostas
		SQLQuery query = getSession().createSQLQuery(sqlRespostas.toString());
		query.setInteger("idAvaliacao", idAvaliacao);
		query.setInteger("idDocenteTurma", idDocenteTurma);
		@SuppressWarnings("unchecked")
		List<Object[]> respostas = query.list();
		int i;
		for (Object[] obj : respostas) {
			i = 0;
			Integer idPergunta = (Integer) obj[i++];
			Integer tipoPergunta = (Integer) obj[i++];
			Integer numero = (Integer) obj[i++];
			Boolean permiteCitacao = (Boolean) obj[i++];
			Integer resposta = (Integer) obj[i++];
			String citacao = (String) obj[i++];
			// ajuste para os dados de 2009.1 e anteriores, onde há resposta null para avaliações finalizadas.
			if (resposta == null) resposta = -1;
			switch (TipoPergunta.valueOf(tipoPergunta)) {
				case PERGUNTA_SIM_NAO :
					tabelaCSV.set(linha, legendasId.get(idPergunta), (resposta == -1 ? "N/A" : (resposta == 1 ? "SIM" : "NÃO")));
					break;
				case PERGUNTA_MULTIPLA_ESCOLHA :
				case PERGUNTA_UNICA_ESCOLHA :
					if (permiteCitacao != null && permiteCitacao.booleanValue())
						tabelaCSV.set(linha, legendasId.get(idPergunta) + "." + numero, citacao);
					else
						tabelaCSV.set(linha, legendasId.get(idPergunta) + "." + numero, "X");
					break;
				default: 
					tabelaCSV.set(linha, legendasId.get(idPergunta), (resposta == -1 ? "N/A" : resposta));
					break;
			}
		}
	}
	
	private List<String> carregaRespostasAvaliacao(int idFormularioAvaliacao, String nomeTabela) throws HibernateException, DAOException {
		List<String> cabecalhos = new LinkedList<String>();
		// perguntas
		String sqlPerguntas = "select gp.id as id_grupo, " +
				" p.id as id_pergunta, ap.id as id_alternativa" + 
				" from avaliacao.pergunta p" +
				" left join avaliacao.alternativa_pergunta ap on (ap.id_pergunta = p.id)" +
				" inner join avaliacao.grupo_perguntas gp on (p.id_grupo = gp.id)" +
				" inner join avaliacao.grupo_perguntas_formulario_avaliacao gpfa on (gp.id = gpfa.id_grupo_pergunta)" +
				" inner join avaliacao.formulario_avaliacao fa using (id_formulario_avaliacao)" +
				" where fa.id_formulario_avaliacao = :idFormularioAvaliacao" +
				" order by gp.titulo, p.ordem, ap.numero";
			
		Query q = getSession().createSQLQuery(sqlPerguntas);
		q.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		int ordemGrupo = 0, ordemPergunta = 0, ordemAlternativa = 0;
		Integer grupoAnterior = new Integer(0), perguntaAnterior = new Integer(0);
		for (Object[] obj : lista) {
			int i = 0;
			Integer idGrupo = (Integer) obj[i++];
			Integer idPergunta = (Integer) obj[i++];
			Integer idAlternativa = (Integer) obj[i++];
			if (!grupoAnterior.equals(idGrupo)) {
				grupoAnterior = idGrupo;
				ordemGrupo++;
				ordemPergunta = 1;
				ordemAlternativa = 1;
			} else if (!perguntaAnterior.equals(idPergunta)) {
				perguntaAnterior = idPergunta;
				ordemPergunta++;
				ordemAlternativa = 1;
			}
			String legenda = null;
			if (idAlternativa != null)
				legenda = String.format("%d.%d.%d", ordemGrupo, ordemPergunta, ordemAlternativa);
			else
				legenda = String.format("%d.%d", ordemGrupo, ordemPergunta);
			ordemAlternativa++;
			String coluna = "P_" + legenda.replace('.', '_');
			cabecalhos.add(coluna);
			// adiciona a coluna na tabela
			String sql = "alter table " + nomeTabela + " add column " + coluna + " varchar(240)";
			getSession().createSQLQuery(sql).executeUpdate();
			// pergunta a ser inserida
			Pergunta pergunta = findByPrimaryKey(idPergunta, Pergunta.class);
			// respostas
			StringBuilder sqlResposta = new StringBuilder("update " + nomeTabela + " set "+coluna+" = sub_consulta.resposta" +
					" from (select" +
					"   resposta_pergunta.id_avaliacao," +
					"   resposta_pergunta.id_docente_turma," +
					"   pergunta.id as id_pergunta," +
					"   pergunta.tipo_pergunta," +
					"   alternativa_pergunta.numero," +
					"   alternativa_pergunta.permite_citacao, ");
			switch (pergunta.getTipoPergunta()) {
				case PERGUNTA_SIM_NAO :
					sqlResposta.append(" case when resposta = -1 then 'NÃO' when resposta = 1 then 'SIM' else 'N/A' end");
					break;
				case PERGUNTA_MULTIPLA_ESCOLHA :
				case PERGUNTA_UNICA_ESCOLHA :
					sqlResposta.append(" case when permite_citacao then citacao else 'X' end");
					break;
				default: 
					sqlResposta.append(" case when resposta = -1 then 'N/A' else cast(resposta as varchar) end");
					break;
			}
			sqlResposta.append(" as resposta" +
					" from avaliacao.resposta_pergunta" +
					"   inner join avaliacao.pergunta on (resposta_pergunta.id_pergunta = pergunta.id)" +
					"   inner join avaliacao.grupo_perguntas on (pergunta.id_grupo = grupo_perguntas.id) " +
					"   left join avaliacao.alternativa_pergunta on " +
					"     (pergunta.id = alternativa_pergunta.id_pergunta and resposta_pergunta.resposta = alternativa_pergunta.id) " +
					"   where resposta_pergunta.id_pergunta = :idPergunta");
			if (pergunta.getTipoPergunta() == TipoPergunta.PERGUNTA_MULTIPLA_ESCOLHA || pergunta.getTipoPergunta() == TipoPergunta.PERGUNTA_UNICA_ESCOLHA)
				sqlResposta.append(
					"   and alternativa_pergunta.id = :idAlternativa");
			sqlResposta.append(
					" ) sub_consulta" +
					" where " + nomeTabela + ".id_avaliacao = sub_consulta.id_avaliacao" +
					" and (" + nomeTabela + ".id_docente_turma = sub_consulta.id_docente_turma " +
							" or sub_consulta.id_docente_turma is null)");
			// consulta às respostas
			SQLQuery query = getSession().createSQLQuery(sqlResposta.toString());
			if (idAlternativa != null && (
					pergunta.getTipoPergunta() == TipoPergunta.PERGUNTA_UNICA_ESCOLHA || pergunta.getTipoPergunta() == TipoPergunta.PERGUNTA_MULTIPLA_ESCOLHA)) {
				query.setInteger("idPergunta", pergunta.getId());
				query.setInteger("idAlternativa", idAlternativa);
				query.executeUpdate();
			} else {
				query.setInteger("idPergunta", pergunta.getId());
				query.executeUpdate();
			}
		}
		return cabecalhos;
	}

	/** Adiciona respostas aos dados a serem exportados.
	 * @param dadosAvaliacao
	 * @param legendasId
	 * @param chave
	 * @param lista
	 */
	private void adicionaRespostaDadosAvaliacao(
			DadosBrutosAvaliacao dadosAvaliacao,
			Map<Integer, String> legendasId,
			AvaliacaoDocenteTurmaInvalida chave, List<Object[]> lista) {
		for (Object[] obj : lista) {
			int i = 2;
			Integer idPergunta = (Integer) obj[i++];
			Integer tipoPergunta = (Integer) obj[i++];
			Integer numero = (Integer) obj[i++];
			Boolean permiteCitacao = (Boolean) obj[i++];
			Integer resposta = (Integer) obj[i++];
			String citacao = (String) obj[i++];
			// ajuste para os dados de 2009.1 e anteriores, onde há resposta null para avaliações finalizadas.
			if (resposta == null) resposta = -1;
			switch (TipoPergunta.valueOf(tipoPergunta)) {
				case PERGUNTA_SIM_NAO :
					dadosAvaliacao.putDado(chave, legendasId.get(idPergunta), (resposta == -1 ? "N/A" : (resposta == 1 ? "SIM" : "NÃO")));
					break;
				case PERGUNTA_MULTIPLA_ESCOLHA :
				case PERGUNTA_UNICA_ESCOLHA :
					if (permiteCitacao != null && permiteCitacao.booleanValue())
						dadosAvaliacao.putDado(chave, legendasId.get(idPergunta) + "." + numero, citacao);
					else
						dadosAvaliacao.putDado(chave, legendasId.get(idPergunta) + "." + numero, "X");
					break;
				default: 
					dadosAvaliacao.putDado(chave, legendasId.get(idPergunta), (resposta == -1 ? "N/A" : resposta));
					break;
			}
		}
	}
	
	/** Retorna, para as perguntas aplicadas na Avaliação Institucional, um mapa de pares Código de Legenda / Descrição da Pergunta.
	 * @param detalhado caso true, incluirá na legenda os códigos dos grupos de perguntas, e a descrição das perguntas de múltipla escolha. Caso false, retorna apenas os códigos das perguntas e alternativas das perguntas de múltipla escolha.
	 * @param discente caso true, retorna os códigos de legenda para as perguntas respondidas por discentes. Caso false, retorna para as perguntas respondidas por docentes. 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<String, String> legendaDescricaoPerguntas(boolean detalhado, int idFormularioAvaliacao) throws HibernateException, DAOException {
		String sqlPerguntas = "select gp.id as id_grupo," +
			"  gp.titulo as titulo_grupo," +
			"  gp.descricao as desc_grupo," +
			"  p.tipo_pergunta," +
			"  p.descricao as desc_pergunta," +
			"  ap.numero," +
			"  ap.descricao as desc_alternativa" +
			" from avaliacao.grupo_perguntas gp " +
			"  inner join avaliacao.grupo_perguntas_formulario_avaliacao gpfa on (gp.id = gpfa.id_grupo_pergunta)" +
			"  inner join avaliacao.formulario_avaliacao fa using (id_formulario_avaliacao)" +
			"  left join avaliacao.pergunta p on (gp.id = p.id_grupo) " +
			"  left join avaliacao.alternativa_pergunta ap on (p.id = ap.id_pergunta) " +
			" where fa.id_formulario_avaliacao = :idFormularioAvaliacao" +
			" order by gp.id, p.ordem, ap.numero";
		
		Query q = getSession().createSQLQuery(sqlPerguntas);
		q.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		int ordemGrupo = 0, ordemPergunta = 0;
		Integer grupoAnterior = new Integer(0);
		String legenda = null, descricaoPerguntaAnterior = "";
		Map<String, String> legendas = new LinkedHashMap<String, String>();
		for (Object[] obj : lista) {
			int i = 0;
			Integer idGrupo = (Integer) obj[i++];
			String tituloGrupo = (String) obj[i++];
			String descricaoGrupo = (String) obj[i++];
			Integer tipoPergunta = (Integer) obj[i++];
			String descricaoPergunta = (String) obj[i++];
			Integer numero = (Integer) obj[i++];
			String descricaoAlternativa = (String) obj[i++];
			if (descricaoGrupo == null) descricaoGrupo = "";
			if (descricaoPergunta == null) descricaoPergunta = "";
			if (tipoPergunta == null) tipoPergunta = 0;
			
			if (!grupoAnterior.equals(idGrupo)) {
				ordemGrupo++;
				ordemPergunta = 0;
				if (detalhado) {
					legenda = String.format("%d.", ordemGrupo);
					legendas.put(legenda, tituloGrupo +" - " + descricaoGrupo);
				}
			}
			if (!descricaoPergunta.equals(descricaoPerguntaAnterior)) {
				ordemPergunta++;
				if (detalhado) {
					legenda = String.format("%d.%d", ordemGrupo, ordemPergunta);
					legendas.put(legenda, descricaoPergunta);
				}
			}
			if (tipoPergunta.equals(TipoPergunta.PERGUNTA_MULTIPLA_ESCOLHA.ordinal())||tipoPergunta.equals(TipoPergunta.PERGUNTA_UNICA_ESCOLHA.ordinal())) {
				legenda = String.format("%d.%d.%d", ordemGrupo, ordemPergunta, numero);
				legendas.put(legenda, descricaoAlternativa);
			} else {
				legenda = String.format("%d.%d", ordemGrupo, ordemPergunta);
				legendas.put(legenda, descricaoPergunta);
			}
			grupoAnterior = idGrupo;
			descricaoPerguntaAnterior = descricaoPergunta;
		}
		return legendas;
	}
	
	/** Retorna um mapa de pares idPergunta / Código de Legenda das perguntas aplicadas na Avaliação Institucional.
	 * @param discente caso true, retorna os códigos de legenda para as perguntas respondidas por discentes. Caso false, retorna para as perguntas respondidas por docentes.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<Integer, String> legendaIdPerguntas(int idFormularioAvaliacao) throws HibernateException, DAOException {
		String sqlPerguntas = "select gp.id as id_grupo, " +
			" p.id as id_pergunta, ap.id as id_alternativa" + 
			" from avaliacao.pergunta p" +
			" left join avaliacao.alternativa_pergunta ap on (ap.id_pergunta = p.id)" +
			" inner join avaliacao.grupo_perguntas gp on (p.id_grupo = gp.id)" +
			" inner join avaliacao.grupo_perguntas_formulario_avaliacao gpfa on (gp.id = gpfa.id_grupo_pergunta)" +
			" inner join avaliacao.formulario_avaliacao fa using (id_formulario_avaliacao)" +
			" where fa.id_formulario_avaliacao = :idFormularioAvaliacao" +
			" order by gp.titulo, p.ordem, ap.numero";
		
		Query q = getSession().createSQLQuery(sqlPerguntas);
		q.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		int ordemGrupo = 0, ordemPergunta = 0, ordemAlternativa = 0;
		Integer grupoAnterior = new Integer(0), perguntaAnterior = new Integer(0);
		Map<Integer, String> legendas = new LinkedHashMap<Integer, String>();
		for (Object[] obj : lista) {
			int i = 0;
			Integer idGrupo = (Integer) obj[i++];
			Integer idPergunta = (Integer) obj[i++];
			Integer idAlternativa = (Integer) obj[i++];
			if (!grupoAnterior.equals(idGrupo)) {
				grupoAnterior = idGrupo;
				ordemGrupo++;
				ordemPergunta = 1;
				ordemAlternativa = 1;
			} else if (!perguntaAnterior.equals(idPergunta)) {
				perguntaAnterior = idPergunta;
				ordemPergunta++;
				ordemAlternativa = 1;
			}
			String legenda = null;
			if (idAlternativa != null)
				legenda = String.format("%d.%d.%d", ordemGrupo, ordemPergunta, ordemAlternativa);
			else
				legenda = String.format("%d.%d", ordemGrupo, ordemPergunta);
			legendas.put(idPergunta, legenda);
			ordemAlternativa++;
		}
		return legendas;
	}
	
	/** Remove o processamento do resultado da avaliação de um ano/período.
	 * @param ano
	 * @param periodo
	 * @param idFormulario
	 */
	public void removeResultadosAvaliacao(int ano, int periodo, int idFormulario) {
		Object[] parametros = {ano, periodo, idFormulario};
		update("delete from avaliacao.resultado_avaliacao_docente" + 
				" where id_docente_turma in (select distinct id_docente_turma" +
				" from avaliacao.resposta_pergunta rp" +
				" inner join avaliacao.avaliacao_institucional ai on (ai.id = rp.id_avaliacao)" +
				" where ano = ? and periodo = ? and id_formulario_avaliacao = ?)", parametros);
	}

	/**
	 * Retorna uma coleção de médias de docentes que não obtiveram uma média
	 * mínima em pelo menos uma das perguntas.
	 * 
	 * @param idUnidade
	 * @param ano
	 * @param periodo
	 * @param mediaMinima
	 * @param idPerguntas
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<MediaNotas> findDocentesMediaNotasBaixa(int idUnidade, int ano, int periodo, double mediaMinima, Collection<Integer> idPerguntas) throws HibernateException, DAOException {
		if (isEmpty(idPerguntas)) return null;
		StringBuilder hql = new StringBuilder("select notas" +
				" from MediaNotas notas" +
				" inner join fetch notas.resultadoAvaliacaoDocente resultado" +
				" inner join fetch resultado.docenteTurma docenteTurma" +
				" inner join fetch docenteTurma.turma turma" +
				" where resultado.ano = :ano" +
				" and resultado.periodo = :periodo" +
				" and notas.media <= :mediaMinima" +
				" and notas.pergunta.id in ")
				.append(UFRNUtils.gerarStringIn(idPerguntas));
		if (idUnidade > 0) {
			hql.append(" and docenteTurma.id in (" +
					"  select dt.id" +
					"  from DocenteTurma dt" +
					"  left join dt.docente docente" +
					"  left join docente.unidade u1" +
					"  left join dt.docenteExterno de" +
					"  left join de.servidor servidor" +
					"  left join servidor.unidade u2" +
					"  where u1.id =:idUnidade or u2.id = :idUnidade" +
					")");
		}
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setDouble("mediaMinima", mediaMinima);
		if (idUnidade > 0)
			q.setInteger("idUnidade", idUnidade);
		@SuppressWarnings("unchecked")
		Collection<MediaNotas> medias = q.list();
		return medias;
	}
	
	/**
	 * Retorna uma coleção de Resultados de Avaliação de docentes que não obtiveram uma média geral
	 * mínima.
	 * 
	 * @param idUnidade
	 * @param ano
	 * @param periodo
	 * @param mediaMinima
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ResultadoAvaliacaoDocente> findDocentesMediaGeralBaixa(int idUnidade, int ano, int periodo, double mediaMinima) throws HibernateException, DAOException {
		StringBuilder hql = new StringBuilder("select resultado" +
				" from ResultadoAvaliacaoDocente resultado" +
				" inner join fetch resultado.docenteTurma docenteTurma" +
				" inner join fetch docenteTurma.turma turma" +
				" where resultado.ano = :ano" +
				" and resultado.periodo = :periodo" +
				" and resultado.mediaGeral <= :mediaMinima");
		if (idUnidade > 0) {
			hql.append(" and docenteTurma.id in (" +
					"  select dt.id" +
					"  from DocenteTurma dt" +
					"  left join dt.docente docente" +
					"  left join docente.unidade u1" +
					"  left join dt.docenteExterno de" +
					"  left join de.servidor servidor" +
					"  left join servidor.unidade u2" +
					"  where u1.id =:idUnidade or u2.id = :idUnidade" +
					")");
		}
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setDouble("mediaMinima", mediaMinima);
		if (idUnidade > 0)
			q.setInteger("idUnidade", idUnidade);
		@SuppressWarnings("unchecked")
		Collection<ResultadoAvaliacaoDocente> lista = q.list();
		return lista;
	}
	
	/** Gera um mapa de dados para o Relatório Quantitativo de Avaliações Institucionais não Computadas.
	 * @param ano
	 * @param periodo
	 * @param idCentro
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<String, Map<String, Map<String, Integer>>> findDadosRelatorioQuantitativoNaoComputado(int ano, int periodo, int idCentro, int idFormularioAvaliacao) throws HibernateException, DAOException {
		Map<Integer, String> legenda = legendaIdPerguntas(idFormularioAvaliacao);
		ParametroProcessamentoAvaliacaoInstitucional ultimo = findUltimoProcessamento(ano, periodo);
		Map<String, Map<String, Map<String, Integer>>> dadosRelatorio = new HashMap<String, Map<String,Map<String,Integer>>>();
		// docentes não avaliados
		String sql = " select centro.nome as centro," +
			" departamento.nome as departamento," +
			" count(*)" +
			" from (" +
			"    select id_docente_turma " +
			"    from ensino.docente_turma dt " +
			"    inner join ensino.turma t using (id_turma) " +
			"    inner join ensino.componente_curricular using (id_disciplina)" +
			"    where t.ano = :ano" +
			"    and t.periodo = :periodo" +
			"    and componente_curricular.nivel = 'G'" +
			"    except " +
			"    select distinct rp.id_docente_turma" +
			"    from avaliacao.resposta_pergunta rp" +
			"    inner join avaliacao.avaliacao_institucional ai on (ai.id = rp.id_avaliacao)" +
			"    inner join avaliacao.formulario_avaliacao fa using (id_formulario_avaliacao)" +
			"    where ai.finalizada = trueValue()" +
			"    and ai.id_discente is not null" +
			"    and ai.id_formulario_avaliacao = :idFormularioAvaliacao) invalidos" +
			" inner join ensino.docente_turma dt using (id_docente_turma)" +
			" left join ensino.docente_externo on (docente_externo.id_docente_externo = dt.id_docente_externo) " +
			" left join rh.servidor on (servidor.id_servidor = dt.id_docente or servidor.id_servidor = docente_externo.id_servidor) " +
			" inner join comum.unidade departamento on (servidor.id_unidade = departamento.id_unidade or docente_externo.id_unidade = departamento.id_unidade) " +
			" inner join comum.unidade centro on (departamento.id_gestora = centro.id_unidade)" +
			(idCentro > 0 ? " where centro.id_unidade = :idCentro" : "") +
			" group by centro, departamento";
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		if (idCentro > 0) q.setInteger("idCentro", idCentro);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		for (Object[] obj : lista) {
			Map<String, Map<String, Integer>> centro = dadosRelatorio.get(obj[0]);
			if (centro == null) centro = new TreeMap<String, Map<String,Integer>>();
			Map<String, Integer> departamento = centro.get(obj[1]);
			if (departamento == null) departamento = new TreeMap<String, Integer>();
			BigInteger quantidade = (BigInteger) obj[2];
			departamento.put("Docentes Não Avaliados:", quantidade.intValue());
			centro.put((String) obj[1], departamento);
			dadosRelatorio.put((String) obj[0], centro);
		}
		// avaliações não computadas por terem respostas inválidas
		sql = "select centro.nome as centro," +
			" departamento.nome as departamento," +
			" pergunta.id as id_pergunta," +
			" pergunta.descricao as pergunta," +
			" count(rp.id_docente_turma)" +
			" from avaliacao.avaliacao_institucional ai" +
			" inner join avaliacao.resposta_pergunta rp on (rp.id_avaliacao = ai.id)" +
			" inner join avaliacao.pergunta on (pergunta.id = rp.id_pergunta)" +
			" inner join ensino.docente_turma using (id_docente_turma)" +
			" left join ensino.docente_externo on (docente_externo.id_docente_externo = docente_turma.id_docente_externo)  " +
			" left join rh.servidor on (servidor.id_servidor = docente_turma.id_docente or servidor.id_servidor = docente_externo.id_servidor)  " +
			" left join comum.pessoa on (servidor.id_pessoa = pessoa.id_pessoa or docente_externo.id_pessoa = pessoa.id_pessoa)  " +
			" inner join comum.unidade departamento on (servidor.id_unidade = departamento.id_unidade or docente_externo.id_unidade = departamento.id_unidade)  " +
			" inner join comum.unidade centro on (departamento.id_gestora = centro.id_unidade)  " +
			" where ai.finalizada = trueValue() " +
			" and ai.id_discente is not null " +
			" and ai.ano = :ano" +
			" and ai.periodo = :periodo" +
			" and ai.id_formulario_avaliacao = :idFormularioAvaliacao" +
			(ValidatorUtil.isEmpty(ultimo.getListaIdPerguntaDeterminanteExclusaoAvaliacao()) ? "" : " and id_pergunta in " + UFRNUtils.gerarStringIn(ultimo.getListaIdPerguntaDeterminanteExclusaoAvaliacao())) +
			" and resposta <= 0" +
			(idCentro > 0 ? " and centro.id_unidade = :idCentro" : "") +
			" group by centro.nome, departamento.nome, pergunta.id, pergunta.descricao";
		q = getSession().createSQLQuery(sql);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		if (idCentro > 0) q.setInteger("idCentro", idCentro);
		@SuppressWarnings("unchecked")
		List<Object[]>lista2 = q.list();
		for (Object[] obj : lista2) {
			Map<String, Map<String, Integer>> centro = dadosRelatorio.get(obj[0]);
			if (centro == null) centro = new TreeMap<String, Map<String,Integer>>();
			Map<String, Integer> departamento = centro.get(obj[1]);
			if (departamento == null) departamento = new TreeMap<String, Integer>();
			Integer idPergunta = (Integer) obj[2];
			String pergunta = legenda.get(idPergunta) + " - " + obj[3];
			BigInteger quantidade = (BigInteger) obj[4];
			departamento.put("Resposta inválida à Pergunta \"" + pergunta+"\":", quantidade.intValue());
			centro.put((String) obj[1], departamento);
			dadosRelatorio.put((String) obj[0], centro);
		}
		// avaliações não computadas de discentes com reprovação por falta
		sql = " select centro.nome as centro," +
			" departamento.nome as departamento," +
			" count(distinct invalidos.id)" +
			" from (" +
			"    select distinct ai.id, rp.id_docente_turma" +
			"    from ensino.matricula_componente mc" +
			"    inner join ensino.turma t using (id_turma)" +
			"    inner join ensino.docente_turma dt using (id_turma)" +
			"    inner join avaliacao.resposta_pergunta rp using (id_docente_turma)" +
			"    inner join avaliacao.avaliacao_institucional ai on (rp.id_avaliacao = ai.id and ai.id_discente = mc.id_discente)" +
			"    where ai.finalizada = trueValue() " +
			"    and mc.id_situacao_matricula in (:idSituacaoMatricula, :idSituacaoRepMediaFalta) " +
			"    and ai.id_discente is not null " +
			"    and ai.ano = :ano and ai.periodo = :periodo" +
			"    and ai.id_formulario_avaliacao = :idFormularioAvaliacao) invalidos" +
			" inner join ensino.docente_turma dt using (id_docente_turma)" +
			" left join ensino.docente_externo on (docente_externo.id_docente_externo = dt.id_docente_externo)" +
			" left join rh.servidor on (servidor.id_servidor = dt.id_docente or servidor.id_servidor = docente_externo.id_servidor)" +
			" inner join comum.unidade departamento on (servidor.id_unidade = departamento.id_unidade or docente_externo.id_unidade = departamento.id_unidade)" +
			" inner join comum.unidade centro on (departamento.id_gestora = centro.id_unidade)" +
			(idCentro > 0 ? " where centro.id_unidade = :idCentro" : "") +
			" group by centro, departamento";
		q = getSession().createSQLQuery(sql);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		q.setInteger("idSituacaoMatricula", SituacaoMatricula.REPROVADO_FALTA.getId());
		q.setInteger("idSituacaoRepMediaFalta", SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
		if (idCentro > 0) q.setInteger("idCentro", idCentro);
		@SuppressWarnings("unchecked")
		List<Object[]>lista3 = q.list();
		for (Object[] obj : lista3) {
			Map<String, Map<String, Integer>> centro = dadosRelatorio.get(obj[0]);
			if (centro == null) centro = new TreeMap<String, Map<String,Integer>>();
			Map<String, Integer> departamento = centro.get(obj[1]);
			if (departamento == null) departamento = new TreeMap<String, Integer>();
			BigInteger quantidade = (BigInteger) obj[2];
			departamento.put("Avaliações Não Computadas de Discentes com Reprovações por Falta:", quantidade.intValue());
			centro.put((String) obj[1], departamento);
			dadosRelatorio.put((String) obj[0], centro);
		}
		// docentes que não obtiveram um mínimo de 5 avaliações
		sql = " select centro.nome as centro," +
			" departamento.nome as departamento," +
			" count(*)" +
			" from ensino.docente_turma " +
			" left join ensino.docente_externo on (docente_externo.id_docente_externo = docente_turma.id_docente_externo)" +
			" left join rh.servidor on (servidor.id_servidor = docente_turma.id_docente or servidor.id_servidor = docente_externo.id_servidor)" +
			" left join comum.pessoa on (servidor.id_pessoa = pessoa.id_pessoa or docente_externo.id_pessoa = pessoa.id_pessoa)" +
			" inner join comum.unidade departamento on (servidor.id_unidade = departamento.id_unidade or docente_externo.id_unidade = departamento.id_unidade)" +
			" inner join comum.unidade centro on (departamento.id_gestora = centro.id_unidade)" +
			" inner join (" +
			"    select id_docente_turma" +
			"    from (" +
			"        select ai.id, rp.id_docente_turma" +
			"        from avaliacao.avaliacao_institucional ai" +
			"        inner join avaliacao.resposta_pergunta rp on (ai.id = rp.id_avaliacao)" +
			"        where ai.finalizada = trueValue()" +
			"        and ai.id_discente is not null" +
			"        and rp.id_docente_turma is not null" +
			"        and ai.ano = :ano and ai.periodo = :periodo" +
			"        and ai.id_formulario_avaliacao = :idFormularioAvaliacao" +
			"        except" +
			"        select id_avaliacao, id_docente_turma" +
			"        from avaliacao.avaliacao_docente_turma_invalida" +
			"        where id_parametro_processamento = :idParamentroProcessamento" +
			"    ) sub_consulta" +
			"    group by id_docente_turma" +
			"    having count(distinct id) < :numMinimoAvaliacoes ) sub_consulta using (id_docente_turma)" +
			(idCentro > 0 ? " where centro.id_unidade = :idCentro" : "") +
			" group by centro, departamento";
		q = getSession().createSQLQuery(sql);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idFormularioAvaliacao", idFormularioAvaliacao);
		q.setInteger("idParamentroProcessamento", ultimo.getId());
		q.setInteger("numMinimoAvaliacoes", ultimo.getNumMinAvaliacoes());
		if (idCentro > 0) q.setInteger("idCentro", idCentro);
		@SuppressWarnings("unchecked")
		List<Object[]>lista4 = q.list();
		for (Object[] obj : lista4) {
			Map<String, Map<String, Integer>> centro = dadosRelatorio.get(obj[0]);
			if (centro == null) centro = new TreeMap<String, Map<String,Integer>>();
			Map<String, Integer> departamento = centro.get(obj[1]);
			if (departamento == null) departamento = new TreeMap<String, Integer>();
			BigInteger quantidade = (BigInteger) obj[2];
			departamento.put("Docentes que não obtiveram no mínino "+ultimo.getNumMinAvaliacoes()+" avaliações:", quantidade.intValue());
			centro.put((String) obj[1], departamento);
			dadosRelatorio.put((String) obj[0], centro);
		}
		return dadosRelatorio;
	}
	
	/** Retorna um mapa com a evolução da média geral do docente por ano-período na avaliação institucional. 
	 * @param idServidor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<String, Double> findEvolucaoMediaGeralAnoPeriodo(int idServidor, boolean somenteResultadosLiberados) throws HibernateException, DAOException{
		String sql = "select resultado_avaliacao_docente.ano, resultado_avaliacao_docente.periodo," +
				" avg(media_notas.media) as media_geral" +
				" from avaliacao.resultado_avaliacao_docente" +
				" inner join avaliacao.media_notas using (id_resultado_avaliacao_docente)" +
				" inner join avaliacao.pergunta on (pergunta.id = media_notas.id_pergunta)" +
				" left join avaliacao.parametro_processamento_avaliacao using (ano, periodo)" +
				" inner join ensino.docente_turma using (id_docente_turma)" +
				" inner join ensino.turma t using (id_turma)" +
				" left join ensino.docente_externo on (docente_externo.id_docente_externo = docente_turma.id_docente_externo)" +
				" left join rh.servidor on (servidor.id_servidor = docente_turma.id_docente or servidor.id_servidor = docente_externo.id_servidor)" +
				" where (t.distancia is null or t.distancia = falseValue() or t.id_polo is null)" +
				" and servidor.id_servidor = :idServidor" +
				" and pergunta.id_grupo = :idGrupoPergunta" +
				(somenteResultadosLiberados ? " and consulta_docente = trueValue()" :"") +
				" group by resultado_avaliacao_docente.ano, resultado_avaliacao_docente.periodo" +
				" order by resultado_avaliacao_docente.ano, resultado_avaliacao_docente.periodo";
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idServidor", idServidor);
		q.setInteger("idGrupoPergunta", ParametroHelper.getInstance().getParametroInt(ParametrosAvaliacaoInstitucional.ID_GRUPO_PERGUNTAS_MEDIA_GERAL_RESULTADO_AVALIACAO));
		@SuppressWarnings("unchecked")
		List<Object[]> bulk = q.list();
		Map<String, Double> medias = new TreeMap<String, Double>();
		for (Object[] obj : bulk) {
			int i = 0;
			Integer ano = (Integer) obj[i++];
			Integer periodo = (Integer) obj[i++];
			BigDecimal media = (BigDecimal) obj[i++];
			String anoPeriodo = ano+"."+periodo;
			medias.put(anoPeriodo, media.doubleValue());
		}
		return medias;
	}
	

	/**
	 * Indica se o discente preencheu a Avaliação da Docência Assistida para as
	 * turmas matriculadas no ano-período especificados.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public boolean isDocenciaAssistidaFinalizada(Discente discente, int ano, int periodo) throws HibernateException, DAOException {
		Object [] statusDocenciaAssitida = {PlanoDocenciaAssistida.APROVADO,
				PlanoDocenciaAssistida.CONCLUIDO,
				PlanoDocenciaAssistida.ANALISE_RELATORIO,
				PlanoDocenciaAssistida.SOLICITADO_ALTERACAO_RELATORIO};
		// consulta para verificar se há bolsistas de docência assistida para avaliar
		StringBuilder hql = new StringBuilder("select distinct tda.id" +
				" from TurmaDocenciaAssistida tda" +
				" inner join tda.turma turmaDA," +
				" MatriculaComponente mc" +
				" inner join mc.turma turmaMC" +
				" where turmaDA.id = turmaMC.id" +
				" and turmaDA.ano = :ano" +
				" and turmaDA.periodo = :periodo" +
				" and mc.discente.id = :idDiscente" +
				" and mc.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido()) +
				" and tda.planoDocenciaAssistida.status in " + UFRNUtils.gerarStringIn(statusDocenciaAssitida));
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idDiscente", discente.getId());
		@SuppressWarnings("unchecked")
		Collection<Integer> listaIDTurmaDocenciaAssistida = q.list();
		// há bolsistas. verificar se foram avaliados
		if (!isEmpty(listaIDTurmaDocenciaAssistida)) {
			String hqlAval = "select count(distinct rp.turmaDocenciaAssistida.id)" +
					" from RespostaPergunta rp" +
					" where rp.avaliacao.discente.id = :idDiscente" +
					" and rp.avaliacao.finalizada = trueValue()" +
					" and rp.avaliacao.ano = :ano" +
					" and rp.avaliacao.periodo = :periodo" +
					" and rp.turmaDocenciaAssistida is not null";
			q = getSession().createQuery(hqlAval);
			q.setInteger("idDiscente", discente.getId());
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
			long qtdAvaliacoesFinalizadas = (Long) q.uniqueResult();
			if (qtdAvaliacoesFinalizadas > 0)
				return true;
			else
				return false;
		} else {
			return true;
		}
	}

	/** Retorna a Avaliação Institucional realizada pelo tutor de ensino à distância.
	 * @param tutor
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public AvaliacaoInstitucional findByTutor(TutorOrientador tutor, int ano, int periodo, FormularioAvaliacaoInstitucional formulario) throws HibernateException, DAOException {
		if (tutor == null)
			return null;
		StringBuilder hql = new StringBuilder("select av" +
				" from AvaliacaoInstitucional av" +
				" left join fetch av.respostas r" +
				" where av.tutorOrientador.id = ?" +
				" and av.ano = ?" +
				" and av.periodo = ?");
		if (formulario != null)
			hql.append(" and av.formulario.id = :idFormulario");
		else
			hql.append(" and av.formulario is null");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger(0, tutor.getId())
		.setInteger(1, ano)
		.setInteger(2, periodo);
		if (formulario != null)
			q.setInteger("idFormulario", formulario.getId());
		return (AvaliacaoInstitucional) q.setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Esse método remove a avaliação e as respostas de um docente turma.
	 * 
	 * @param aval
	 */
	public void removerAvaliacaoInstitucional(DocenteTurma docenteTurma, int ano, int periodo) {
		
		int id = docenteTurma.getId();
		Object[] params = new Object[] { id, ano, periodo };
		
		String sqlDeleteRespostas = "delete from avaliacao.resposta_pergunta rp " +
				" where rp.id in (" +
				" select rp.id from avaliacao.resposta_pergunta rp" +
				" inner join avaliacao.avaliacao_institucional ai on (rp.id_avaliacao = ai.id)" +
				" where rp.id_docente_turma = ?" +
				" and ai.ano = ?" +
				" and ai.periodo = ?)";
		
		String sqlDeleteObservacoes = "delete from avaliacao.observacoes_docente_turma obs" +
				" where obs.id in (" +
				" select obs.id from avaliacao.observacoes_docente_turma obs" +
				" inner join avaliacao.avaliacao_institucional ai on (obs.id_avaliacao = ai.id)" +
				" where obs.id_docente_turma = ?" +
				" and ai.ano = ?" +
				" and ai.periodo = ?)";
		
		String sqlDeleteAvaliacaoInvalida = "delete from avaliacao.avaliacao_docente_turma_invalida " +
				" where id_docente_turma = " + id;
		
		String sqlDeleteAvaliacaoDocenteInvalida = "delete from avaliacao.docente_turma_invalido " +
				" where id_docente_turma = " + id;
		
		String sqlDeleteResultadoAvaliacao = "delete from avaliacao.resultado_avaliacao_docente where id_docente_turma = " + id;
			
		update(sqlDeleteRespostas, params);
		update(sqlDeleteAvaliacaoInvalida);
		update(sqlDeleteAvaliacaoDocenteInvalida);
		update(sqlDeleteObservacoes, params);
		update(sqlDeleteResultadoAvaliacao);
		//Turma com um único docente, após remover as resposta, a avaliação e as observações deve também ser removidas
		List<Integer> idsAvalicaoesSemRespostas= findAvaliacoesSemRespostas(ano, periodo);
		String sqlDeleteObsAvaliacaoSemRespostas = "delete from avaliacao.observacoes_docente_turma obs" +
				" where obs.id_avaliacao in " + UFRNUtils.gerarStringIn( idsAvalicaoesSemRespostas );
		String sqlDeleteAvaliacoesSemRespostas = "delete from avaliacao.avaliacao_institucional ai" +
				" where ai.id in " + UFRNUtils.gerarStringIn( idsAvalicaoesSemRespostas );
		if( isNotEmpty(idsAvalicaoesSemRespostas) ){
			update(sqlDeleteObsAvaliacaoSemRespostas);
			update(sqlDeleteAvaliacoesSemRespostas);
		}
	}
	
	/**
	 * Retorna todas as avaliações sem resposta para o ano e período selecionado.
	 * Utiliza somente em {@link AvaliacaoInstitucionalDao#removerAvaliacaoInstitucional(DocenteTurma, int, int)}
	 * @param ano
	 * @param periodo
	 * @return
	 */
	private List<Integer> findAvaliacoesSemRespostas(int ano, int periodo){
		
		String sqlAvaliacoesSemRespostas = " select distinct ai.id " +
		" from avaliacao.resposta_pergunta rp" +
		"  right join avaliacao.avaliacao_institucional ai on (ai.id = rp.id_avaliacao)" +
		"  where ai.ano = ?" +
		"  and ai.periodo = ?" +
		"  and rp.id_avaliacao is null";

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList( sqlAvaliacoesSemRespostas,
				new Object[] { ano, periodo });
		List<Integer> ids = new ArrayList<Integer>();
		for (Map<String, Object> map : lista) {
			ids.add( (Integer) map.get("id") );
		}
		
		return ids;
		
	}

	/** Retorna um formulário disponível para preenchimento pelo docente no ano-período.
	 * @param ano
	 * @param periodo
	 * @param ead
	 * @param tipoAvaliacaoInstitucional
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public FormularioAvaliacaoInstitucional findFormularioDocente(int ano, int periodo, boolean ead, int tipoAvaliacaoInstitucional) throws HibernateException, DAOException {
		Criteria c = getSession().createCriteria(CalendarioAvaliacao.class)
				.add(Restrictions.eq("ativo", true))
				.add(Restrictions.eq("ano", ano))
				.add(Restrictions.eq("periodo", periodo))
				.createCriteria("formulario").add(Restrictions.eq("tipoAvaliacao", tipoAvaliacaoInstitucional))
				.add(Restrictions.eq("ead", ead));
		CalendarioAvaliacao cal = (CalendarioAvaliacao) c.setMaxResults(1).uniqueResult();
		if (cal != null)
			return cal.getFormulario();
		else
			return null;
	}
	
	/** Retorna uma coleção de parametros de processamento aplicados a um formulario de Avaliação
	 * @param idFormulario
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ParametroProcessamentoAvaliacaoInstitucional> findParametroProcessamentoByFormulario(int idFormulario) throws HibernateException, DAOException {
		String hql = "select parametro" +
				" from ParametroProcessamentoAvaliacaoInstitucional parametro" +
				" where parametro.formulario.id = :idFormulario" +
				" order by inicioProcessamento desc";
		Query q = getSession().createQuery(hql);
		q.setInteger("idFormulario", idFormulario);
		@SuppressWarnings("unchecked")
		Collection<ParametroProcessamentoAvaliacaoInstitucional> lista = q.list();
		return lista;
	}
	
}
