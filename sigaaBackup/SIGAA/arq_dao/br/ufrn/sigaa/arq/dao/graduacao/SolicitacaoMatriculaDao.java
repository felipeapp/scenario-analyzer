/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 30/01/2007
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Class responsável por consultas relativas a solicitações de matrícula.
 * @author ricardo
 *
 */
public class SolicitacaoMatriculaDao extends GenericSigaaDAO {

	/**
	 * Busca todas as turmas do discente de acordo com o ano, periodo e situação da matrícula informada
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param situacaoMatricula
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasMatriculadasByDiscente(DiscenteAdapter discente, int ano, int periodo, Collection<SituacaoMatricula> situacaoMatricula) throws DAOException {
		String hql = "select turma from MatriculaComponente mc " +
				" inner join mc.turma turma " +
				" inner join mc.situacaoMatricula sit " +
				" inner join mc.discente d " +
				" where sit.id in " + gerarStringIn(situacaoMatricula) +
				" and mc.ano = :ano " +
				" and mc.periodo = :periodo " +
				" and d.id = :idDiscente";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
		query.setInteger("idDiscente", discente.getId());
		
		return query.list();
	}
	
	/**
	 * Busca as turmas para as quais o usuário possui solicitações de matrícula em aberto
	 *
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasSolicitacoesByDiscente(int idDiscente, Integer  ano, Integer  periodo, Integer... status) throws DAOException {
		return findTurmasSolicitacoesByDiscente(false, idDiscente, ano, periodo, status);
	}

	/**
	 * Busca as turmas para as quais o usuário possui solicitações de matrícula em aberto
	 *
	 * @param otimiz
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private Collection<Turma> findTurmasSolicitacoesByDiscente(boolean otimiz, Integer idDiscente, Integer ano, Integer  periodo, Integer... status) throws DAOException {
		
		String hql = "SELECT sol.turma " +
			" FROM SolicitacaoMatricula sol " +
			"left join fetch sol.turma.horarios h  left join fetch sol.turma.docentesTurmas " +
			" WHERE sol.anulado = falseValue() " +
			" and sol.discente.id="+idDiscente +
			" and sol.status in " + gerarStringIn(status) +
			" and sol.ano = " + ano + " and sol.periodo = " + periodo+
			" ORDER BY sol.turma.disciplina.codigo";
		return getSession().createQuery(hql).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	

	/** Retorna uma coleção de solicitações de matrícula de uma turma especificada.
	 * @param turma
	 * @param somentePendentesProcessamento
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findByTurma(int turma, boolean somentePendentesProcessamento, Integer... status) throws DAOException {
		try {
			String projecao = "s.id, s.rematricula,s.discente.id, s.discente.matricula, s.discente.pessoa.nome, s.status, " +
					"matricula.id, matricula.situacaoMatricula.id, m, s.discente.nivel";
			String hql = "SELECT "+projecao +
				" FROM SolicitacaoMatricula s left join s.matriculaGerada as matricula, Discente d left join d.curriculo c left join c.matriz m " +
				" WHERE s.discente.id=d.id and s.anulado=falseValue() and s.turma.id="+turma+" and s.status in " + gerarStringIn(status);
			if ( somentePendentesProcessamento ) {
				hql += " and s.idMatriculaGerada is null";
			}
			hql += " ORDER BY s.discente.pessoa.nome";

			List<?> lista = getSession().createQuery(hql).list();
			Collection<SolicitacaoMatricula> result = new ArrayList<SolicitacaoMatricula>();

			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				SolicitacaoMatricula solicitacao = new SolicitacaoMatricula();

				solicitacao.setId((Integer) colunas[col++]);
				solicitacao.setRematricula((Boolean)colunas[col++]);

				Discente discente = new Discente();
				discente.setId((Integer) colunas[col++]);
				discente.setMatricula((Long) colunas[col++]);
				discente.getPessoa().setNome((String) colunas[col++]);
				MatrizCurricular matriz = (MatrizCurricular) colunas[8];
				if (matriz != null) {
					discente.setCurriculo(new Curriculo());
					discente.getCurriculo().setMatriz(matriz);
				}
				Character nivel = (Character) colunas[9];
				discente.setNivel(nivel);
				solicitacao.setDiscente(discente);

				solicitacao.setStatus((Integer) colunas[col++]);

				Integer idMatricula = (Integer) colunas[col++];
				if (idMatricula != null) {
					MatriculaComponente matricula = new MatriculaComponente(idMatricula);
					matricula.setSituacaoMatricula(new SituacaoMatricula((Integer) colunas[col++]));
					solicitacao.setMatriculaGerada(matricula);
				}

				result.add(solicitacao);
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna uma coleção de solicitações de matrícula da turma especificada de acordo com 
	 * seu status, desconsiderando as situações que possuam matrículas geradas com situação 
	 * dentro das informadas.
	 * 
	 * @param turma
	 * @param status
	 * @param situacoesMatricula
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findByTurma(int turma, List<Integer> status, List<SituacaoMatricula> situacoesMatricula) throws DAOException {
		try {
			String projecao = "s.id, s.discente.id, s.discente.matricula, s.discente.pessoa.nome, s.status, " +
					"matricula.id, matricula.situacaoMatricula.id, s.discente.nivel";
			String hql = 
				" SELECT " + projecao +
				"  FROM SolicitacaoMatricula s left join s.matriculaGerada as matricula " +
				" WHERE " +
				"  s.anulado = falseValue() and s.turma.id = " + turma + 
				"  and s.status in " + gerarStringIn(status) +
				"  and matricula.situacaoMatricula.id not in " + gerarStringIn(situacoesMatricula);
			
			hql += " ORDER BY s.discente.pessoa.nome";

			List<?> lista = getSession().createQuery(hql).list();
			Collection<SolicitacaoMatricula> result = new ArrayList<SolicitacaoMatricula>();

			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				SolicitacaoMatricula solicitacao = new SolicitacaoMatricula();

				solicitacao.setId((Integer) colunas[col++]);

				Discente discente = new Discente();
				discente.setId((Integer) colunas[col++]);
				discente.setMatricula((Long) colunas[col++]);
				discente.getPessoa().setNome((String) colunas[col++]);
				
				solicitacao.setStatus((Integer) colunas[col++]);
				
				Integer idMatricula = (Integer) colunas[col++];
				if (idMatricula != null) {
					MatriculaComponente matricula = new MatriculaComponente(idMatricula);
					matricula.setSituacaoMatricula(new SituacaoMatricula((Integer) colunas[col++]));
					solicitacao.setMatriculaGerada(matricula);
				}
				
				Character nivel = (Character) colunas[col++];
				discente.setNivel(nivel);
				solicitacao.setDiscente(discente);


				result.add(solicitacao);
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	
	/**
	 * Retorna o número de solicitações cadastradas para uma determinada turma
	 *
	 * @param turma
	 * @param status
	 * @param pendentes
	 * @return
	 * @throws DAOException
	 */
	public Long countByTurma(Turma turma, Collection<Integer> status, boolean pendentes) throws DAOException {
		try {
			String hql = "SELECT count(id) "+
			" FROM SolicitacaoMatricula WHERE anulado = falseValue() and turma.id=" + turma.getId() +
			" AND status IN " + gerarStringIn(status);

			if (pendentes) {
				hql += " AND idMatriculaGerada is null";
			}

			Query q = getSession().createQuery( hql.toString() );
			return (Long) q.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna a quantidade de solicitações de matrícula de um discente em um ano-período
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public int countByDiscenteAnoPeriodo(Discente discente, Integer ano, Integer periodo, Integer... status)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT count(id) FROM SolicitacaoMatricula ");
			hql.append( " WHERE anulado=falseValue() and discente.id = :idDiscente " );
			hql.append( " AND ano = :ano " );
			hql.append( " AND periodo = :periodo " );
			hql.append( " AND status in " + gerarStringIn(status) );
			Query q = getSession().createQuery( hql.toString() );
			q.setInteger("idDiscente", discente.getId());
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
			return ((Long) q.uniqueResult()).intValue();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/** Retorna uma coleção de solicitações de matrícula de um discente.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findByDiscenteAnoPeriodo(DiscenteAdapter discente, Integer ano, Integer periodo, Integer idUnidade, Integer... status) throws DAOException{
		return findByDiscenteTurmaAnoPeriodo(discente, null, ano, periodo, idUnidade, status);
	}

	/** Retorna uma coleção de solicitações de matrícula de um discente.
	 * @param discente
	 * @param turma
	 * @param ano
	 * @param periodo
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findByDiscenteTurmaAnoPeriodo(DiscenteAdapter discente, Turma turma, Integer ano, Integer periodo, Integer idUnidade , Integer... status) throws DAOException{

		StringBuilder sql = new StringBuilder();
		sql.append("select t.codigo, t.id_turma, t.id_disciplina, t.local, t.capacidade_aluno, t.descricao_horario, ccd.id_componente_detalhes, ccd.nome, ccd.codigo as cod_componente, c.id_unidade, " +
				" sol.id_solicitacao_matricula, sol.numerosolicitacao, sol.id_matricula_gerada, sol.data_cadastro as data_cadastro_solicitacao, sol.data_alteracao, sol.status, " +
				" sol.observacao, sol.ano, sol.periodo, sol.data_solicitacao, sol.data_analise, sol.id_registro_entrada, sol.id_registro_cadastro, " +
				" sol.id_registro_alteracao, p.nome as nome_analisador," +
				" sol.anulado, sol.rematricula, " +
				
				" mc.id_matricula_componente,  mc.id_situacao_matricula, mc.data_cadastro as data_cadastro_matricula, mc.id_turma as matricula_turma, sm.descricao as descricao_situacao_matricula, " +
				" d.id_discente, d.nivel, d.status as status_discente, " +
				" ccd.cr_aula, ccd.cr_laboratorio, ccd.cr_estagio, ccd.ch_total, " +
				
				" sol.id_atividade as id_atividade, atividade.id_unidade as id_unid_atividade, " +
				" atividade.id_tipo_componente, atividade.id_tipo_atividade, " +
				" atividade_detalhes.id_componente_detalhes as id_detalhe_atividade, atividade_detalhes.nome as nome_atividade, atividade_detalhes.codigo as cod_atividade, " +
				" atividade_detalhes.cr_aula as cr_aula_atividade, atividade_detalhes.cr_laboratorio as cr_lab_atividade, " +
				" atividade_detalhes.cr_estagio as cr_estagio_atividade, atividade_detalhes.ch_total as ch_total_atividade" +
				
				" from graduacao.solicitacao_matricula sol " +
				
				" left outer join ensino.turma t using(id_turma) " +
				" join discente d using(id_discente) " +
				
				" left outer join ensino.componente_curricular c using(id_disciplina) " +
				" left outer join ensino.componente_curricular_detalhes ccd on( c.id_detalhe = ccd.id_componente_detalhes ) " +
				
				" left outer join ensino.componente_curricular atividade on(atividade.id_disciplina = sol.id_atividade) " +
				" left outer join ensino.componente_curricular_detalhes atividade_detalhes on( atividade.id_detalhe = atividade_detalhes.id_componente_detalhes ) " +
				
				" left outer join ensino.matricula_componente mc on (sol.id_matricula_gerada = mc.id_matricula_componente) " +
				" left outer join ensino.situacao_matricula sm on (sm.id_situacao_matricula = mc.id_situacao_matricula)" +
				" left outer join comum.registro_entrada re on(re.id_entrada = sol.id_registro_alteracao) " +
				" left outer join comum.usuario u using(id_usuario) " +
				" left outer join comum.pessoa p on (p.id_pessoa = u.id_pessoa) " +
				" where sol.anulado = falseValue() and sol.id_discente = ? " +
				" and ( mc.id_situacao_matricula <> " + SituacaoMatricula.EXCLUIDA.getId() + " or mc.id_situacao_matricula is null ) ");
		
		List<Object> params = new ArrayList<Object>();
		params.add(discente.getId());
		
		if (turma != null) {
			sql.append(" and sol.id_turma = ? ");
			params.add(turma.getId());
		}
		if (ano != null) {
			sql.append(" and sol.ano = ? ");
			params.add(ano);
		}
		if (periodo != null) {
			sql.append(" and sol.periodo = ? ");
			params.add(periodo);
		}
		if (!isEmpty(status))
			sql.append(" and sol.status in " + gerarStringIn(status));
		
		if (!isEmpty(idUnidade)){ 
			sql.append(" and (atividade.id_unidade = ? or c.id_unidade = ?)");
			params.add(idUnidade);
			params.add(idUnidade);
		}
		
		sql.append(" order by sol.data_solicitacao");
			
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoMatricula> lista = getJdbcTemplate().query(sql.toString(), params.toArray(), new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				SolicitacaoMatricula sm = new SolicitacaoMatricula(rs.getInt("id_solicitacao_matricula"));
				sm.setNumeroSolicitacao(rs.getInt("numerosolicitacao"));
				sm.setDataCadastro( rs.getTimestamp("data_cadastro_solicitacao") );
				sm.setDataAlteracao( rs.getTimestamp("data_alteracao") );
				sm.setStatus( rs.getInt("status") );
				sm.setObservacao( rs.getString("observacao") );
				sm.setAno(rs.getInt("ano"));
				sm.setPeriodo(rs.getInt("periodo"));
				sm.setDataSolicitacao(rs.getTimestamp("data_solicitacao"));
				sm.setDataAnalise(rs.getTimestamp("data_analise"));
				sm.setRegistroEntrada(new RegistroEntrada());
				sm.getRegistroEntrada().setId(rs.getInt("id_registro_entrada"));
				sm.setRegistroCadastro(new RegistroEntrada());
				sm.getRegistroCadastro().setId(rs.getInt("id_registro_cadastro"));
				
				if( rs.getInt("id_registro_alteracao") > 0 && rs.getString("nome_analisador") != null){
					sm.setRegistroAlteracao(new RegistroEntrada());
					sm.getRegistroAlteracao().setId(rs.getInt("id_registro_alteracao"));
					sm.getRegistroAlteracao().setUsuario(new  UsuarioGeral());
					sm.getRegistroAlteracao().getUsuario().setPessoa(new Pessoa());
					sm.getRegistroAlteracao().getUsuario().getPessoa().setNome(rs.getString("nome_analisador"));
				}
				
				sm.setAnulado(rs.getBoolean("anulado"));
				sm.setRematricula(rs.getBoolean("rematricula"));
				
				if( rs.getInt("id_turma") > 0 ){
					sm.setTurma(new Turma( rs.getInt("id_turma") ));
					sm.getTurma().setCodigo( rs.getString("codigo") );
					sm.getTurma().setLocal( rs.getString("local") );
					sm.getTurma().setCapacidadeAluno( rs.getInt("capacidade_aluno") );
					sm.getTurma().setDescricaoHorario( rs.getString("descricao_horario") );
				}

				// Discente
				Discente discente = new Discente( rs.getInt("id_discente") );
				discente.setNivel( rs.getString("nivel").charAt(0) );
				discente.setStatus( rs.getInt("status_discente") );
				sm.setDiscente(discente);
				
				// Componente
				ComponenteCurricular componente = null;
				if( rs.getInt("id_disciplina") > 0 ){
					componente = new ComponenteCurricular(rs.getInt("id_disciplina"));
					componente.setNome(rs.getString("nome"));
					componente.setCodigo(rs.getString("cod_componente"));
					componente.setUnidade(new Unidade(rs.getInt("id_unidade")));
					sm.getTurma().setDisciplina(componente);
				}

				Integer idMatricula = (Integer) rs.getObject("id_matricula_componente");
				if (idMatricula != null) {
					MatriculaComponente matricula = new MatriculaComponente(idMatricula);
					matricula.setSituacaoMatricula(  new SituacaoMatricula( rs.getInt("id_situacao_matricula")) );
					matricula.getSituacaoMatricula().setDescricao( rs.getString("descricao_situacao_matricula") );
					matricula.setDataCadastro( rs.getDate("data_cadastro_matricula") );
					if( componente != null) 
						matricula.setComponente(componente);
					matricula.setDiscente(discente);
					matricula.setTurma(new Turma());
					matricula.getTurma().setId( (rs.getInt("matricula_turma") ) );
					sm.setMatriculaGerada(matricula);
				}
				
				// Detalhes do componente
				if( componente != null){
					ComponenteDetalhes detalhes = componente.getDetalhes();
					detalhes.setId(rs.getInt("id_componente_detalhes"));
					detalhes.setCrAula( rs.getInt("cr_aula") );
					detalhes.setCrLaboratorio( rs.getInt("cr_laboratorio") );
					detalhes.setCrEstagio( rs.getInt("cr_estagio") );
					detalhes.setChTotal( rs.getInt("ch_total") );
				}

				if( rs.getInt("id_atividade") > 0 ){
					ComponenteCurricular atividade = new ComponenteCurricular(rs.getInt("id_atividade"));
					atividade.setCodigo( rs.getString("cod_atividade") );
					atividade.setTipoComponente(new TipoComponenteCurricular(rs.getInt("id_tipo_componente")));
					atividade.setTipoAtividade(new  TipoAtividade(rs.getInt("id_tipo_atividade")));
					atividade.getDetalhes().setId(rs.getInt("id_detalhe_atividade"));
					atividade.getDetalhes().setNome( rs.getString("nome_atividade") );
					atividade.getDetalhes().setCrAula( rs.getInt("cr_aula_atividade") );
					atividade.getDetalhes().setCrLaboratorio( rs.getInt("cr_lab_atividade") );
					atividade.getDetalhes().setCrEstagio( rs.getInt("cr_estagio_atividade") );
					atividade.getDetalhes().setChTotal( rs.getInt("ch_total_atividade") );
					atividade.setUnidade(new Unidade(rs.getInt("id_unid_atividade")));
					sm.setAtividade(atividade);
				}
				//" atividade_detalhes.nome as nome_atividade, atividade_detalhes.codigo as cod_atividade, " +
				//" atividade_detalhes.cr_aula, atividade_detalhes.cr_laboratorio, atividade_detalhes.cr_estagio, atividade_detalhes.ch_total " +
				
				return sm;
			}
		});
		
		return lista;
	}

	/**
	 * Buscar todas as solicitações de matrícula realizadas por um discente, ordenadas por ano/período
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findByDiscente(DiscenteAdapter discente) throws DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append( " SELECT sm FROM SolicitacaoMatricula sm left join fetch sm.matriculaGerada m left join fetch sm.turma t left join fetch t.docentesTurmas" );
		hql.append( " WHERE sm.anulado=falseValue() and  sm.discente.id = :idDiscente " );
		hql.append( " AND sm.status != " + SolicitacaoMatricula.EXCLUIDA);
		hql.append( " ORDER BY sm.ano, sm.periodo " );

		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("idDiscente", discente.getId());

		@SuppressWarnings("unchecked")
		Collection<SolicitacaoMatricula> lista = q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		return lista;
	}
	
	/** Retorna uma coleção de solicitações de matrículas.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findTodasByDiscenteAnoPeriodo(DiscenteAdapter discente, Integer ano, Integer periodo, Integer... status) throws DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append( " FROM SolicitacaoMatricula  WHERE discente.id = :idDiscente " );
		if( ano != null )
			hql.append( " AND ano = :ano " );
		if( periodo != null )
			hql.append( " AND periodo = :periodo " );
		if( !isEmpty(status) )
			hql.append( " AND status in " + gerarStringIn(status) );
		hql.append( " ORDER BY dataCadastro" );

		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("idDiscente", discente.getId());

		if( ano != null )
			q.setInteger("ano", ano);
		if( periodo != null )
			q.setInteger("periodo", periodo);

		@SuppressWarnings("unchecked")
		Collection<SolicitacaoMatricula> lista = q.list();
		return lista;
	}

	/** Retorna uma coleção de solicitações de matrícula validadas
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findValidasByDiscenteAnoPeriodo(DiscenteAdapter discente, Integer ano, Integer periodo, Integer idUnidade, boolean analiseOutroPrograma) throws DAOException{
		if (analiseOutroPrograma) {
			return findByDiscenteAnoPeriodo(discente, ano, periodo, idUnidade,
					SolicitacaoMatricula.ATENDIDA,
					SolicitacaoMatricula.NEGADA,
					SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA, 
					SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA);
		}else{
			return findByDiscenteAnoPeriodo(discente, ano, periodo, idUnidade,
					SolicitacaoMatricula.SUBMETIDA,
					SolicitacaoMatricula.ORIENTADO,
					SolicitacaoMatricula.ATENDIDA,
					SolicitacaoMatricula.NEGADA,
					SolicitacaoMatricula.VISTA,
					SolicitacaoMatricula.EXCLUSAO_SOLICITADA,
					SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA, 
					SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA);
		}
	}

	/** Retorna uma coleção de discentes por curso, ano e período.
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @param portal
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByCursoAnoPeriodo(Curso curso, Integer ano, Integer periodo, boolean portal) throws DAOException {
		return findByCursoAnoPeriodo(curso, ano, periodo, portal, true, true);
	}

	/** Retorna uma coleção de discentes por curso, ano e período.
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @param portal
	 * @param somenteSubmetidas
	 * @param orderByNome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByCursoAnoPeriodo(Curso curso, Integer ano, Integer periodo, boolean portal, boolean somenteSubmetidas, boolean orderByNome)	throws DAOException {

		Collection<Integer> status = new ArrayList<Integer>();
		status.add(SolicitacaoMatricula.SUBMETIDA);
		if (!somenteSubmetidas) {
			status.add(SolicitacaoMatricula.VISTA);
		}

		try {
			String hql = "select distinct new Discente(s.discente.id,s.discente.matricula," +
					" s.discente.pessoa.nome, s.discente.status)" +
					" from SolicitacaoMatricula s" +
					" where s.anulado = falseValue() " +
					" and s.ano = :ano and s.periodo = :periodo " +
					" and s.status in " + UFRNUtils.gerarStringIn(status) +
					" and s.discente.curso.id = :curso " +
					" and s.matriculaGerada is null";
			
			if (orderByNome)
				hql += " order by s.discente.pessoa.nome";
			else
				hql += " order by s.discente.matricula";
			
			
			Query q = getSession().createQuery(hql);
			q.setInteger("periodo", periodo);
			q.setInteger("ano", ano);
			q.setInteger("curso", curso.getId());
			// Se a consulta é destinada ao portal do coordenador, restringe o número de resultados
			if(portal) q.setMaxResults(5);

			@SuppressWarnings("unchecked")
			Collection<Discente> lista = q.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException( e);
		}
	}

	/**
	 * Retorna os discentes da unidade que possuem solicitação de matrícula
	 * @param unidade
	 * @param ano
	 * @param periodo
	 * @param apenasSubmetidas TRUE caso deva retornar apenas aqueles que possuem solicitação de matrícula submetidas,
	 * FALSE caso deva retornar apenas aqueles que não possuem solicitação de matrícula submetidas
	 * NULL caso deva retornar todos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByUnidadeAnoPeriodo(Integer unidade, Integer ano, Integer periodo, Boolean apenasSubmetidas, boolean orderByNome) throws DAOException {
		return findByUnidadeAnoPeriodo(unidade, ano, periodo, null, apenasSubmetidas, orderByNome);
	}

	/** Retorna os discentes da unidade que possuem solicitação de matrícula.
	 * @param unidade
	 * @param ano
	 * @param periodo
	 * @param limiteResultados
	 * @param apenasSubmetidas
	 * @param orderByNome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByUnidadeAnoPeriodo(Integer unidade, Integer ano, Integer periodo, Integer limiteResultados, Boolean apenasSubmetidas, boolean orderByNome) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer("SELECT DISTINCT new Discente(s.discente.id,s.discente.matricula," + " s.discente.pessoa.nome, s.discente.status)"
					+ " FROM SolicitacaoMatricula s"
					+ " WHERE s.ano = :ano AND s.periodo=:periodo ");
					if( apenasSubmetidas == null ){
					} else if( apenasSubmetidas ){
						hql.append( " AND s.status = " + SolicitacaoMatricula.SUBMETIDA );
					} else {
						hql.append( " AND s.status in " + gerarStringIn( new int[] {SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA} ) );
					}
					hql.append( " AND s.anulado=falseValue() AND s.discente.gestoraAcademica.id=:gestoraAcademica" );
					
					if (orderByNome)
						hql.append(" ORDER BY s.discente.pessoa.nome");
					else
						hql.append(" ORDER BY s.discente.matricula");
					
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("periodo", periodo);
			q.setInteger("ano", ano);
			q.setInteger("gestoraAcademica", unidade);

			if (limiteResultados != null) {
				q.setMaxResults(limiteResultados);
			}

			@SuppressWarnings("unchecked")
			Collection<Discente> lista = q.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os discentes que são orientandos do servidor informado, com o tipo de solicitação de matrícula informado
	 * @param idServidor id do servidor que é orientador acadêmico
	 * @param ano
	 * @param periodo
	 * @param status
	 * @param nivel nível dos discentes que deve trazer
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByOrientadorAnoPeriodoStatus(int idServidor, int ano, int periodo, Character[] niveis, int... status) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select distinct new Discente(s.discente.id,s.discente.matricula,s.discente.pessoa.nome,s.discente.status)");
			hql.append(" from SolicitacaoMatricula s, OrientacaoAcademica orientacao");
			hql.append(" where s.anulado=falseValue() and s.ano = :ano and s.periodo=:periodo and s.status in " + UFRNUtils.gerarStringIn(status));
			hql.append(" and orientacao.discente.id = s.discente.id and orientacao.fim is null and orientacao.servidor.id = :idServidor");
			hql.append(" and s.discente.nivel in " + gerarStringIn(niveis));
			hql.append(" order by s.discente.pessoa.nome");

			Query q = getSession().createQuery(hql.toString());

			q.setInteger("periodo", periodo);
			q.setInteger("ano", ano);
			q.setInteger("idServidor", idServidor);

			@SuppressWarnings("unchecked")
			Collection<Discente> lista = q.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}



	/** Retorna as solicitações de matrícula cadastradas em um ano-período.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findCadastradasByAnoPeriodo(Integer ano, Integer periodo)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(SolicitacaoMatricula.class);
			c.add(Expression.eq("periodo", periodo));
			c.add(Expression.eq("ano", ano));
			c.add(Expression.eq("anulado", false));
			c.add(Expression.eq("status", SolicitacaoMatricula.CADASTRADA));
			@SuppressWarnings("unchecked")
			Collection<SolicitacaoMatricula> lista = c.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna quantidade de solicitações de matrícula ainda não aprovadas pelo
	 * coordenador do curso.
	 * @return
	 * @throws DAOException
	 */
	public Long getNumeroSolicitacoesMatricula(Curso curso, Integer ano, Integer periodo, boolean somenteSubmetidas) throws DAOException {
		Collection<Integer> status = new ArrayList<Integer>();
		status.add(SolicitacaoMatricula.SUBMETIDA);
		if (!somenteSubmetidas) {
			status.add(SolicitacaoMatricula.VISTA);
		}

		try {
			String hql = "select count(distinct s.discente) from SolicitacaoMatricula s" +
					" where s.anulado=falseValue() " +
					" and s.ano = :ano and s.periodo=:periodo " +
					" and s.status in " + UFRNUtils.gerarStringIn(status) +
					" and s.matriculaGerada is null" +
					" and s.discente.curso.id=:curso ";
			Query q = getSession().createQuery(hql);
			q.setInteger("periodo", periodo);
			q.setInteger("ano", ano);
			q.setInteger("curso", curso.getId());
			return (Long) q.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException( e);
		}
	}


	/** Retorna o número sequencial de solicitação de matrícula (utilizado no comprovante de solicitação).
	 * @return
	 * @throws DAOException
	 */
	public Integer getSequenciaSolicitacoes() throws DAOException {
		try {
			return ((BigInteger) getSession().createSQLQuery(
					"select nextval('graduacao.solicitacao_matricula_seq')").uniqueResult()).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/** Altera o status das solicitações de matrícula.
	 * @param solicitacoes
	 * @param status
	 * @throws DAOException
	 */
	public void alterarStatusSolicitacoes(ArrayList<SolicitacaoMatricula> solicitacoes, Integer status) throws DAOException {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.createStatement();
			java.util.Date now = new java.util.Date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			for (SolicitacaoMatricula sol : solicitacoes) {
				String update = "UPDATE graduacao.solicitacao_matricula SET  " +
						" status = " + status + ", id_registro_alteracao = " + sol.getRegistroAlteracao().getId()
						+ ", data_alteracao = '" + df.format(now) + "' " +
						" WHERE id_solicitacao_matricula = "+sol.getId();
				st.addBatch(update);
				// se for exclusão, decrementa o total na turma
				if (status == SolicitacaoMatricula.EXCLUIDA) {
					String sel = "SELECT total_solicitacoes FROM ensino.turma WHERE id_turma=" + sol.getTurma().getId();
					rs = st.executeQuery(sel);
					int total = 0;
					if (rs.next())
						total = rs.getInt("total_solicitacoes");
					total = total > 0 ? total - 1 : 0;
					update  = "UPDATE ensino.turma SET total_solicitacoes ="+total+" WHERE id_turma = " + sol.getTurma().getId();
					st.addBatch(update);
				}
			}
			st.executeBatch();
        } catch (Exception e) {
        	e.printStackTrace();
            throw new DAOException(e);
        } finally {
    		closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);

		}
	}

	/**
	 * Buscas as solicitações em espera de um discente
	 *
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasSolicitadasEmEspera(DiscenteAdapter discente, int ano, int periodo, boolean otimizado) throws DAOException {
		int[] pendentes = new int[] {SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA};

		// m.turma.id, m.componente.id, m.detalhes.nome
		
		StringBuilder hql = new StringBuilder();
		
		if (otimizado)
			hql.append("select sol.turma.id, sol.turma.disciplina.id, sol.turma.disciplina.detalhes.nome ");
		else
			hql.append("select sol.turma ");
		
		hql.append(" FROM SolicitacaoMatricula sol " +
			" WHERE sol.anulado = falseValue() " +
			" and sol.discente.id="+discente.getId() +
			" and sol.status in " + gerarStringIn(pendentes) +
			" and sol.ano = " + ano + " and sol.periodo = " + periodo+
			" and sol.idMatriculaGerada is null " +
			" ORDER BY sol.turma.disciplina.codigo");
		
		
		Collection<Turma> turmas = null;
		if (!otimizado) {
			@SuppressWarnings("unchecked")
			Collection<Turma> lista  = getSession().createQuery(hql.toString()).list();		
			turmas = lista;
		} else {
			@SuppressWarnings("unchecked")
			List<Object[]> res = getSession().createQuery(hql.toString()).list();
			if (res != null) {
				turmas = new ArrayList<Turma>();
				for (Object[] id : res) {
					Turma turma = new Turma();
					turma.setId( (Integer) id[0]);
					turma.setDisciplina(new ComponenteCurricular());
					turma.getDisciplina().setId( (Integer) id[1]);
					turma.getDisciplina().setDetalhes(new ComponenteDetalhes());
					turma.getDisciplina().getDetalhes().setNome( (String) id[2]);
					
					turmas.add(turma);
				}
			}
		}
		
		return turmas;
	}

	/**
	 * Buscas as solicitações em espera de um discente (NÃO UTILIZA OTIMIZAÇÃO)
	 *
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasSolicitadasEmEspera(DiscenteAdapter discente, int ano, int periodo) throws DAOException {
		return findTurmasSolicitadasEmEspera(discente, ano, periodo, false);
	}	
	
	/** Retorna a solicitação de matrícula excluída de um discente
	 * @param turma
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public SolicitacaoMatricula findDesistente(int turma, int discente) throws DAOException {
		try {
			String hql = "SELECT id, matriculaGerada.id, matriculaGerada.discente.id, matriculaGerada.turma.id, matriculaGerada.situacaoMatricula.id "+
			" FROM SolicitacaoMatricula WHERE anulado=falseValue() and turma.id="+turma+" and discente.id="+discente+" and status ="
			+ SolicitacaoMatricula.EXCLUIDA +" and matriculaGerada.situacaoMatricula.id = "+SituacaoMatricula.DESISTENCIA.getId() ;
			Query q = getSession().createQuery(hql);
			q.setMaxResults(1);
			Object[] result = (Object[]) q.uniqueResult();
			if(result != null){
				SolicitacaoMatricula sol = new SolicitacaoMatricula((Integer) result[0]);
				sol.setMatriculaGerada(new MatriculaComponente((Integer) result[1]));
				sol.getMatriculaGerada().setDiscente(new Discente((Integer) result[2]));
				sol.getMatriculaGerada().setTurma(new Turma((Integer) result[3]));
				sol.getMatriculaGerada().setSituacaoMatricula(new SituacaoMatricula((Integer) result[4]));
				return sol;
			}
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/** Retorna uma coleção de matrículas em espera, ordenadas aleatoriamente. 
	 * @param turma
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findEmEsperaAleatoriasByTurma(Turma turma, Integer quantidade) throws DAOException {
		Query q = getSession().createQuery("from SolicitacaoMatricula sm " +
				" where sm.turma.id = ? " +
				" and sm.matriculaGerada is null" +
				" and sm.anulado is falseValue()" +
				" and sm.status in " + UFRNUtils.gerarStringIn( SolicitacaoMatricula.getStatusSolicitacoesPendentes()) +
				" order by random()");
		q.setInteger(0, turma.getId());

		if (quantidade != null) {
			q.setMaxResults(quantidade);
		}
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoMatricula> lista = q.list();
		return lista;
	}

	/**
	 * Busca os orientandos de um Programa de pós de acordo com o orientador informado
	 * 
	 * @param ano
	 * @param periodo
	 * @param servidor
	 * @param docenteExterno
	 * @param programa
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByOrientadorAndProgramaStricto(int ano, int periodo, Servidor servidor, DocenteExterno docenteExterno, Unidade programa, List<Integer> situacoes) throws DAOException {
		return findByOrientadorStricto(ano, periodo, servidor, docenteExterno, programa, null, situacoes);
	}
	
	/**
	 * Busca os orientandos de um Curso de pós de acordo com o orientador informado
	 * 
	 * @param ano
	 * @param periodo
	 * @param servidor
	 * @param docenteExterno
	 * @param curso
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByOrientadorAndCursoStricto(Integer ano, Integer periodo, Servidor servidor, DocenteExterno docenteExterno, Curso curso, List<Integer> situacoes) throws DAOException {
		return findByOrientadorStricto(ano, periodo, servidor, docenteExterno, null, curso, situacoes);
	}	
	
	/**
	 * Busca os discentes que possuem solicitações de matrícula pendentes de processamento
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	private Collection<Discente> findByOrientadorStricto(Integer ano, Integer periodo, Servidor servidor, DocenteExterno docenteExterno, Unidade programa, Curso curso, List<Integer> situacoes) throws DAOException {
		try {
			String projecao = " d.id, d.matricula, d.pessoa.nome, d.status, d.nivel ";
			
			StringBuilder hql = new StringBuilder();
			hql.append("select distinct " + projecao +  " from SolicitacaoMatricula s " +
					"		join s.discente d " +
					"		left join d.gestoraAcademica gestoraAcademica " +
					"		left join d.curso curso, " +
					"    OrientacaoAcademica orientacao " +
					"		join orientacao.discente discenteOrientado " +
					"		left join orientacao.servidor servidor " +
					"		left join orientacao.docenteExterno de ");
			hql.append(" where s.anulado = falseValue() ");
			
			hql.append(" and s.ano = " + ano);
			hql.append(" and s.periodo = " + periodo);
			hql.append(" and discenteOrientado.id = d.id and orientacao.fim is null ");
			hql.append(" and d.nivel in " + UFRNUtils.gerarStringIn(NivelEnsino.getNiveisStricto()) );
			hql.append(" and orientacao.tipoOrientacao = '"+OrientacaoAcademica.ORIENTADOR +"'");
			hql.append(" and orientacao.cancelado = falseValue() ");
			
			if (isNotEmpty(programa))
				hql.append(" and gestoraAcademica.id = " + programa.getId());
			
			if (isNotEmpty(curso))
				hql.append(" and curso.id = " + curso.getId());
			
			if (isNotEmpty(situacoes))
				hql.append(" AND s.status in " + gerarStringIn(situacoes));
			
			if (isNotEmpty(servidor)) 
				hql.append(" and servidor.id = :idServidor ");
			
			if (isNotEmpty(docenteExterno)) 
				hql.append(" and de.id = :idDocenteExterno ");
			
			hql.append(" order by d.nivel desc, d.pessoa.nome");

			Query q = getSession().createQuery(hql.toString());
			
			if (isNotEmpty(servidor)) 
				q.setInteger("idServidor", servidor.getId());

			if (isNotEmpty(docenteExterno)) 
				q.setInteger("idDocenteExterno", docenteExterno.getId());

			return HibernateUtils.parseTo(q.list(), projecao, Discente.class, "d");
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todas as solicitações de matrícula para atividades realizadas por um discente
	 * para um determinado ano/período
	 *
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findSolicitacoesAtividadeByDiscente(DiscenteStricto discente, int ano, int periodo) throws DAOException {
		int[] statusIgnorados = new int[] { SolicitacaoMatricula.EXCLUIDA };

		String hql = " FROM SolicitacaoMatricula sol LEFT JOIN FETCH sol.matriculaGerada mat LEFT JOIN FETCH mat.situacaoMatricula" +
			" WHERE sol.anulado = falseValue() " +
			" AND sol.atividade is not null " +
			// " AND sol.matriculaGerada is null" +
			" AND sol.discente.id = " +discente.getId() +
			" AND sol.ano = " + ano +
			" AND sol.periodo = " + periodo +
			" AND sol.status not in " + UFRNUtils.gerarStringIn(statusIgnorados);

		@SuppressWarnings("unchecked")
		Collection<SolicitacaoMatricula> lista = getSession().createQuery(hql).list();
		return lista;
	}

	/**
	 * Conta o número de solicitações de matrícula feitas por alunos de um determinado
	 * programa/centro
	 *
	 * @param programaStricto
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Long getNumeroSolicitacoesMatricula(Unidade programa, int ano, int periodo) throws DAOException {
		Collection<Integer> status = new ArrayList<Integer>();
		status.add(SolicitacaoMatricula.ATENDIDA);
		status.add(SolicitacaoMatricula.NEGADA);

		try {
			// Contabilizar apenas as solicitações analisadas.
			String hql = "select count(distinct s.discente) from SolicitacaoMatricula s" +
					" where s.anulado=falseValue() " +
					" and s.ano = :ano and s.periodo=:periodo " +
					" and s.status in " + UFRNUtils.gerarStringIn(status) +
					" and s.discente.gestoraAcademica.id=:programa ";
			Query q = getSession().createQuery(hql);
			q.setInteger("periodo", periodo);
			q.setInteger("ano", ano);
			q.setInteger("programa", programa.getId());
			Long qtdSolicitacoes = (Long) q.uniqueResult();
			
			// Contabilizar apenas as solicitações submetidas.
			status.clear();
			status.add(SolicitacaoMatricula.SUBMETIDA);
			hql = "select count(distinct s.discente) from SolicitacaoMatricula s" +
					" where s.anulado=falseValue() " +
					" and s.ano = :ano and s.periodo=:periodo " +
					" and s.status in " + UFRNUtils.gerarStringIn(status) +
					" and s.discente.gestoraAcademica.id=:programa ";
			Query q2 = getSession().createQuery(hql);
			q2.setInteger("periodo", periodo);
			q2.setInteger("ano", ano);
			q2.setInteger("programa", programa.getId());
			qtdSolicitacoes += (Long) q2.uniqueResult();
			return qtdSolicitacoes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException( e);
		}
	}


	/**
	 * Buscas as solicitações em espera de um discente
	 * 
	 * @param discente
	 * @param componente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findEmEspera( DiscenteAdapter discente, ComponenteCurricular componente, Integer ano, Integer periodo  ) throws DAOException {
		int[] statusIgnorados = new int[] { SolicitacaoMatricula.EXCLUIDA };

		String hql = " FROM SolicitacaoMatricula sol " +
			" WHERE sol.anulado = falseValue() " +
			" AND (sol.matriculaGerada is null or sol.matriculaGerada.situacaoMatricula.id = " + SituacaoMatricula.EM_ESPERA.getId() + ")" +
			" AND sol.discente.id = " + discente.getId() +
			" AND sol.turma.disciplina.id = " + componente.getId() +
			" AND sol.status not in " + UFRNUtils.gerarStringIn(statusIgnorados);
		if (ano != null)
			hql += " AND sol.ano = " + ano;
		if (periodo != null)
			hql += " AND sol.periodo = " + periodo;
		
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoMatricula> lista = getSession().createQuery(hql).list();
		return lista;
	}

	/**
	 * Retorna as solicitações de matrícula de discentes que NÃO PERTENCEM ao programa informado 
	 * e que possuem a situação informada.
	 * @param idPrograma
	 * @param situacoes
	 * @return
	 * @throws DAOException 
	 */
	public Collection<Discente> findDiscentesOutrosProgramasByProgramaSituacao( int idPrograma, CalendarioAcademico calendario, int... situacoes) throws DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append( " SELECT DISTINCT new Discente(s.discente.id,s.discente.matricula,s.discente.pessoa.nome,s.discente.status, s.discente.nivel)" );
		hql.append( " FROM SolicitacaoMatricula s " );
		hql.append( " WHERE s.anulado = falseValue() " );
		hql.append( " AND s.discente.gestoraAcademica.id <> :idPrograma" );
		hql.append( " AND s.turma.disciplina.unidade.id = :idPrograma" );
		hql.append( " AND s.discente.nivel in " + UFRNUtils.gerarStringIn(NivelEnsino.getNiveisStricto())); 
		
		
		//hql.append( " AND ( SELECT count(*) FROM SolicitacaoMatricula sm WHERE sm.turma.disciplina.unidade.id AND sm.discente.id = s.discente.id AND sm. ) > 1 " );
		
		if( !isEmpty(situacoes) ){
			hql.append(" AND s.status in " + gerarStringIn(situacoes));
		}
		
		if (calendario != null) {
			hql.append( " AND s.ano = "+calendario.getAno()+" AND s.periodo = "+calendario.getPeriodo());
		}
		
		hql.append(" ORDER BY s.discente.nivel desc, s.discente.pessoa.nome" );

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPrograma", idPrograma );
		@SuppressWarnings("unchecked")
		Collection<Discente> lista = q.list();
		return lista;
	}
	
	
	
	/** Retorna uma coleção de discentes por curso, ano e período.
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @param portal
	 * @param somenteSubmetidas
	 * @param orderByNome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByTutorAnoPeriodo(Pessoa tutor, Integer ano, Integer periodo, boolean portal, boolean somenteSubmetidas, boolean orderByNome)	throws DAOException {

		Collection<Integer> status = new ArrayList<Integer>();
		status.add(SolicitacaoMatricula.SUBMETIDA);
		if (!somenteSubmetidas) {
			status.add(SolicitacaoMatricula.VISTA);
		}

		String hql = " SELECT DISTINCT new Discente(s.discente.id,s.discente.matricula," +
				" s.discente.pessoa.nome, s.discente.status)" +
				" FROM SolicitacaoMatricula s, TutoriaAluno ta" +
				" WHERE s.anulado = falseValue() " +
				
				" AND ta.aluno.id = s.discente.id " +
				" AND ta.tutor.pessoa.id = :idPessoa " + 
				
				" AND s.ano = :ano AND s.periodo = :periodo " +
				" AND s.status in " + UFRNUtils.gerarStringIn(status) +
				" AND s.matriculaGerada is null";
		
		if (orderByNome)
			hql += " ORDER BY s.discente.pessoa.nome";
		else
			hql += " ORDER BY s.discente.matricula";
		
		
		Query q = getSession().createQuery(hql);
		q.setInteger("periodo", periodo);
		q.setInteger("ano", ano);
		q.setInteger("idPessoa", tutor.getId());
		// Se a consulta é destinada ao portal do coordenador, restringe o número de resultados
		if(portal) q.setMaxResults(5);

		@SuppressWarnings("unchecked")
		Collection<Discente> lista = q.list();
		return lista;
	}

	
	
	/**
	 * Retorna quantidade de solicitações de matrícula ainda não aprovadas pelo
	 * coordenador do curso.
	 * @return
	 * @throws DAOException
	 */
	public Long getNumeroSolicitacoesMatricula(Pessoa tutor, Integer ano, Integer periodo, boolean somenteSubmetidas) throws DAOException {
		Collection<Integer> status = new ArrayList<Integer>();
		status.add(SolicitacaoMatricula.SUBMETIDA);
		if (!somenteSubmetidas) {
			status.add(SolicitacaoMatricula.VISTA);
		}

		String hql = "SELECT count(distinct s.discente) FROM SolicitacaoMatricula s, TutoriaAluno ta " +
				" WHERE s.anulado=falseValue() " +
				
				" AND ta.aluno.id = s.discente.id " +
				" AND ta.tutor.pessoa.id = :idPessoa " +
				
				" AND s.ano = :ano AND s.periodo=:periodo " +
				" AND s.status in " + UFRNUtils.gerarStringIn(status) +
				" AND s.matriculaGerada is null";
				//" AND s.discente.curso.id=:curso ";
		Query q = getSession().createQuery(hql);
		q.setInteger("periodo", periodo);
		q.setInteger("ano", ano);
		q.setInteger("idPessoa", tutor.getId());
		return (Long) q.uniqueResult();
	}
	
	/**
	 * Busca os alunos especiais que solicitaram matrícula para um determinada
	 * unidade num determinado ano-período.
	 * 
	 * @param unidade
	 * @param ano
	 * @param periodo
	 * @param orderByNome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByAlunoEspecialAnoPeriodo(Unidade unidade, Integer ano, Integer periodo, boolean orderByNome, Collection<Character> niveis, Integer tipoEspecial, boolean isDepartamento)	throws DAOException {

		Collection<Integer> status = new ArrayList<Integer>();
		status.add(SolicitacaoMatricula.SUBMETIDA);
		status.add(SolicitacaoMatricula.VISTA);

		try {
			String hql = "select distinct new Discente(s.discente.id,s.discente.matricula," +
					" s.discente.pessoa.nome, s.discente.status)" +
					" from SolicitacaoMatricula s" +
					" where s.anulado = falseValue() " +
					" and s.ano = :ano and s.periodo = :periodo " +
					" and s.status in " + UFRNUtils.gerarStringIn(status) +
					" and s.matriculaGerada is null" +
					" and s.discente.tipo = :especial" + 
					" and s.discente.nivel in " + gerarStringIn(niveis);
			
			if (isNotEmpty(tipoEspecial)) {
				hql += " and s.discente.formaIngresso.categoriaDiscenteEspecial.id = " + tipoEspecial;
			}
			
			if(unidade != null)
				hql += " and s.turma.disciplina.unidade.id = :unidade";

			if (isDepartamento)
				hql += " and s.discente.formaIngresso.categoriaDiscenteEspecial.chefeAnalisaSolicitacao = trueValue() ";
				
			if (orderByNome)
				hql += " order by s.discente.pessoa.nome";
			else
				hql += " order by s.discente.matricula";
			
			
			Query q = getSession().createQuery(hql);
			q.setInteger("periodo", periodo);
			q.setInteger("ano", ano);
			q.setInteger("especial", Discente.ESPECIAL);
			if(unidade != null)
				q.setInteger("unidade", unidade.getId());

			@SuppressWarnings("unchecked")
			Collection<Discente> lista = q.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException( e);
		}
	}
	
	
	/**
	 * Busca as solicitações de matrícula dos discentes no ano e período determinados.
	 * 
	 * @param idsDiscentes
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoMatricula> findByDiscentesAnoPeriodo(List<Integer> idsDiscentes, Integer ano, Integer periodo)	throws DAOException {

		Collection<Integer> status = new ArrayList<Integer>();
		status.add(SolicitacaoMatricula.CADASTRADA);
		
		String hql = " select s from SolicitacaoMatricula s " +
						" where s.ano = " +ano+ " and s.periodo = " +periodo+ 
						" and s.discente.id in " +UFRNUtils.gerarStringIn(idsDiscentes)+
						" and s.status in " +UFRNUtils.gerarStringIn(status);
		
		Query q = getSession().createQuery(hql);

		@SuppressWarnings("unchecked")
		Collection<SolicitacaoMatricula> lista = q.list();
		return lista;
	}
}
