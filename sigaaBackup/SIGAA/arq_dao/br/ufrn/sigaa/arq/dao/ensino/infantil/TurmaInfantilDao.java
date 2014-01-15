/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/11/2009
 *
 */

package br.ufrn.sigaa.arq.dao.ensino.infantil;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.infantil.dominio.DiscenteInfantil;
import br.ufrn.sigaa.ensino.infantil.dominio.ResponsavelDiscenteInfantil;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoUtil;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.nee.dominio.StatusAtendimento;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao com consultas sobre as turmas do ensino infantil.
 * 
 * @author Leonardo Campos
 *
 */
public class TurmaInfantilDao extends GenericSigaaDAO {

	/**
	 * Retorna a quantidade de turmas cadastradas para o ano, nível e turno informados.
	 * 
	 * @param ano
	 * @param nivel
	 * @param idTurno
	 * @return
	 * @throws DAOException
	 */
	public int countTurmasByAnoNivelTurno(int ano, int nivel, String turno) throws DAOException{
		try {
			StringBuilder hql = new StringBuilder();
			hql.append( " SELECT COUNT(*) FROM Turma t " );
			hql.append( " WHERE t.descricaoHorario = :turno " );
			hql.append( " AND  t.situacaoTurma.id <> :excluida " );
			hql.append( " AND t.ano = :ano " );
			hql.append( " AND t.disciplina.id = :nivel " );

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("ano", ano );
			q.setInteger("excluida", SituacaoTurma.EXCLUIDA );
			q.setInteger("nivel", nivel );
			q.setString("turno", turno );

			return ((Long) q.uniqueResult()).intValue();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Verifica se o professor informado já foi atribuído a uma turma no ano informado.
	 * @param idProfessor
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public boolean existsTurmaProfessor(int idProfessor, Turma turma) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append( " FROM Turma t inner join t.docentesTurmas dt left join dt.docente s left join dt.docenteExterno de " );
			hql.append( " WHERE t.ano = :ano " );
			hql.append( " AND  t.situacaoTurma.id <> :excluida " );
			hql.append( " AND  t.disciplina.nivel = :infantil " );
			hql.append( " AND  ((s.id = :idProfessor) OR (de.id = :idProfessor))" );
			if(turma.getId() > 0)
				hql.append( " AND t.id <> " + turma.getId() );
				

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("ano", turma.getAno() );
			q.setInteger("excluida", SituacaoTurma.EXCLUIDA );
			q.setCharacter("infantil", NivelEnsino.INFANTIL );
			q.setInteger("idProfessor", idProfessor );

			return q.list().size() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	/**
	 * Serve para realizar a consolidação da turma Infantil
	 */
	public void consolidarTurmaInfantil( Collection<MatriculaComponente> matriculaComp, Turma turma) throws DAOException{
		Collection<Integer> discentesAprovados = new ArrayList<Integer>();
		Collection<Integer> discentesReprovados = new ArrayList<Integer>();
		Collection<Integer> matriculas = new ArrayList<Integer>();
		Integer novoAtual = null;
		
		for (MatriculaComponente mc : matriculaComp) {
			if (mc.getDiscente().isSelecionado()) {
				discentesAprovados.add(mc.getId());
				matriculas.add(mc.getDiscente().getId());
				if (mc.getDiscente().getPeriodoAtual() != 6) 
					novoAtual = mc.getDiscente().getPeriodoAtual() + 1;
				else 
					novoAtual = mc.getDiscente().getPeriodoAtual();
			}
			else 
				discentesReprovados.add(mc.getId());
		}

		if (!discentesAprovados.isEmpty()) {
			String mcAprovados = "UPDATE ensino.matricula_componente SET id_situacao_matricula = " + SituacaoMatricula.APROVADO.getId()
			+ " WHERE id_matricula_componente in "+ UFRNUtils.gerarStringIn(discentesAprovados);
			update(mcAprovados);
		}

		if (!discentesReprovados.isEmpty()) {
			String mcReprovados = "UPDATE ensino.matricula_componente SET id_situacao_matricula = " + SituacaoMatricula.REPROVADO.getId()
			+ " WHERE id_matricula_componente in "+ UFRNUtils.gerarStringIn(discentesReprovados);
			update(mcReprovados);
		}

		if (!matriculas.isEmpty()) {
			String periodoAtual = "UPDATE discente SET periodo_atual = " + novoAtual 
			+ " WHERE id_discente in "+ UFRNUtils.gerarStringIn(matriculas);
			update(periodoAtual);
		}

		String consolidacao= "UPDATE ensino.turma SET id_situacao_turma = " + SituacaoTurma.CONSOLIDADA 
		+ " WHERE id_turma = "+ turma.getId();
		update(consolidacao);
	}
	
	/** Busca pelas matriculas componentes dos discentes informados */
	public List<MatriculaComponente> findMatriculaDiscente(Collection<DiscenteInfantil> discentes) throws DAOException {
		Criteria c = getSession().createCriteria(MatriculaComponente.class);

		List <Integer> ids = new ArrayList <Integer> ();
		for (DiscenteInfantil d : discentes)
			ids.add(d.getId());

		c.add(Restrictions.in("discente.id", ids));
		c.add(Restrictions.eq("situacaoMatricula.id", SituacaoMatricula.MATRICULADO.getId()));
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = c.list();
		return rs;
	}
	
	/**
	 * Retorna todas as matrículas da turma informada .
	 *
	 * @param idTurma
	 * @return
	 */
	public Collection<MatriculaComponente> findMatriculasAConsolidar(Turma turma) {
		return findParticipantesTurma(turma.getId(), null, false, turma.isAgrupadora(), ConsolidacaoUtil.getSituacaoesAConsolidar());
	}
	
	/**
	 * Retorna todos os discentes que participam ou participaram da turma, discente e situações informadas
	 *
	 * @param idTurma
	 * @param idDiscente
	 * @param pegarUsuario diz se é pra trazer o usuário dos alunos retornados
	 * @param situacoes
	 * @return
	 */
	public Collection<MatriculaComponente> findParticipantesTurma(int idTurma, Integer idDiscente, boolean pegarUsuario, boolean buscarNasSubturmas, List<SituacaoMatricula> situacoes) {
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet result = null;
		
		try {
			String idTurmas = "" + idTurma;
			
			// Se for para buscar nas subturmas, procura as ids das subturmas.
			if (buscarNasSubturmas){
				@SuppressWarnings("unchecked")
				List <Integer> ids = getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora = " + idTurma).list();
				
				for (Integer id : ids)
					idTurmas += "," + id;
			}
			
			idTurmas = "("+idTurmas+")";
				
	
			int[] statusPossiveis = new int[] { StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA, StatusDiscente.CADASTRADO, StatusDiscente.CONCLUIDO,
					StatusDiscente.CANCELADO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.TRANCADO, StatusDiscente.DEFENDIDO};
	
			String sql =
					"select mc.id_matricula_componente, d.id_discente, d.matricula, d.status, d.nivel, p.id_pessoa, p.nome, p.email as emailPessoa, p.cpf_cnpj, u.id_usuario, u.login, u.email as emailUsuario, t.id_turma, t.codigo as codigo_turma, t.ano, t.periodo, " +
					"cc.id_disciplina, cc.codigo, cc.num_unidades, cd.id_componente_detalhes, cd.nome as nome_disciplina, cd.ch_total, mc.media_final, mc.numero_faltas, mc.recuperacao, " +
					"mc.apto, sm.id_situacao_matricula, sm.descricao as descricao_sm, un.id_gestora_academica, c.id_curso, c.nome as nome_curso, d.id_foto, po.id_polo, ma.metodo_avaliacao, " +
					"ma.id, ma.porcentagem_tutor, ma.porcentagem_professor, ma.numero_aulas, cc.id_bloco_subunidade, nu.id_nota_unidade, nu.unidade, nu.nota, nu.faltas, " +
					"nee.id_solicitacao_apoio_nee, nee.justificativa_solicitacao, nee.data_cadastro, nee.parecer_comissao, nee.ativo, nee.id_status_atendimento, id_tipo_atividade, " +
					"mod.id_modalidade_educacao, unid.sigla, s.id_serie, s.id_curso as id_curso_medio, prd.nome as rd_nome, por.nome as ord_nome, p.data_nascimento, p.nome_pai, p.nome_mae, p.telefone_fixo, p.codigo_area_nacional_telefone_fixo " +
			
					"from ensino.matricula_componente mc left join ensino.nota_unidade nu using (id_matricula_componente) join ensino.turma t on mc.id_turma = t.id_turma "+
					"join ensino.componente_curricular cc on t.id_disciplina = cc.id_disciplina left join ensino.componente_curricular_detalhes cd using (id_componente_detalhes) "+
					"left join comum.unidade un on cc.id_unidade = un.id_unidade join discente d using (id_discente) join comum.pessoa p using (id_pessoa) "+
					"join ensino.situacao_matricula sm using (id_situacao_matricula)" +
					"left join (" +
						"curso c left join ead.metodologia_avaliacao ma using (id_curso) " +
						"inner join comum.modalidade_educacao mod on (c.id_modalidade_educacao = mod.id_modalidade_educacao) " +
						"inner join comum.unidade unid on (c.id_unidade = unid.id_unidade)" +
					") on d.id_curso = c.id_curso " +
					"left join (graduacao.discente_graduacao dg join ead.polo po using (id_polo)) on dg.id_discente_graduacao = d.id_discente " +
					"left join comum.usuario u on u.id_pessoa = p.id_pessoa "+
					"left join nee.solicitacao_apoio_nee nee on nee.id_discente = d.id_discente and nee.ativo = trueValue() and nee.id_status_atendimento = "+StatusAtendimento.EM_ATENDIMENTO + " " +
					"left join medio.serie s on s.id_serie = mc.id_serie " +
					"left join curso cSerie on cSerie.id_curso = s.id_curso " +
					"left join infantil.discente_infantil di on di.id_discente = mc.id_discente " +
					"left join infantil.responsavel_discente rd on rd.id_responsavel_discente = di.id_responsavel_ufrn " +
					"left join comum.pessoa prd on prd.id_pessoa = rd.id_pessoa " +
					"left join infantil.responsavel_discente ord on ord.id_responsavel_discente = di.id_outro_responsavel " +
					"left join comum.pessoa por on por.id_pessoa = ord.id_pessoa " +
					"where mc.id_turma in " + idTurmas + " and d.status in " + gerarStringIn(statusPossiveis) + " " +
					(idDiscente  != null ?  "and mc.id_discente = " + idDiscente + " " : "" ) +
					"and mc.id_situacao_matricula in " + gerarStringIn(situacoes) + " order by translate(p.nome_ascii, ' ', '0') asc, nu.unidade asc";
			
			
			
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql.toString());
			result = st.executeQuery();

			if (result != null) 
				return populaMatriculasComponentes(pegarUsuario, result);
				
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} finally {
			closeResultSet(result);
			closeStatement(st);
			Database.getInstance().close(con);
		} 

		return null;		
	}
	
	/**
	 * Converte o vetor de objetos passado em uma lista de MatriculaComponente.
	 * 
	 * @param pegarUsuario
	 * @param result
	 * @return
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	private List<MatriculaComponente> populaMatriculasComponentes(boolean pegarUsuario, ResultSet rs) throws DataAccessException, SQLException {
		List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
		
		try {
			while (rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				if (matriculas.contains(mc)) {
					mc = matriculas.get(matriculas.indexOf(mc));
				} else {
					
					mc.setDiscente(new DiscenteInfantil());
					DiscenteInfantil d = (DiscenteInfantil) mc.getDiscente();
					d.setDiscente(new Discente());
										
					mc.getDiscente().setId(rs.getInt("id_discente"));
					mc.getDiscente().getDiscente().setId(rs.getInt("id_discente"));
					
					if (rs.getObject("matricula") != null)
						mc.getDiscente().setMatricula(rs.getLong("matricula") );
					
					if (rs.getObject("status") != null)
						mc.getDiscente().setStatus(rs.getInt("status") );
					
					mc.getDiscente().setNivel(rs.getString("nivel").charAt(0));
						
					mc.getDiscente().setPessoa(new Pessoa());
					mc.getDiscente().getPessoa().setId(rs.getInt("id_pessoa"));
					mc.getDiscente().getPessoa().setNome(rs.getString("nome"));
					mc.getDiscente().getPessoa().setEmail(rs.getString("emailPessoa"));
						
					if (rs.getObject("cpf_cnpj") != null)
						mc.getDiscente().getPessoa().setCpf_cnpj(rs.getLong("cpf_cnpj"));
	
					if (pegarUsuario) {
						try {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = getJdbcTemplate().queryForMap("select id_usuario, login, email, id_foto from comum.usuario where id_pessoa=? and inativo=falseValue() " + BDUtils.limit(1), new Object[] { mc.getDiscente().getPessoa().getId() });
							if (map != null) {
								mc.getDiscente().setUsuario(new Usuario());
								mc.getDiscente().getUsuario().setId((Integer) map.get("id_usuario"));
								mc.getDiscente().getUsuario().setLogin((String) map.get("login"));
								mc.getDiscente().getUsuario().setEmail((String) map.get("email"));
								mc.getDiscente().getUsuario().setIdFoto((Integer) map.get("id_foto"));
							}
						} catch(EmptyResultDataAccessException e) {
						// Não existe usuário, não fazer nada.
						}
					} else {					
						if (rs.getObject("id_usuario") != null) {
							mc.getDiscente().setUsuario(new Usuario());
							mc.getDiscente().getUsuario().setId(rs.getInt("id_usuario"));
							mc.getDiscente().getUsuario().setLogin(rs.getString("login"));
							mc.getDiscente().getUsuario().setEmail(rs.getString("emailUsuario"));
						}
					}
	
					mc.setTurma(new Turma(rs.getInt("id_turma")));
					mc.getTurma().setCodigo(rs.getString("codigo_turma"));
					mc.getTurma().setAno(rs.getInt("ano"));
					mc.getTurma().setPeriodo(rs.getInt("periodo"));
						
					mc.getTurma().setDisciplina(new ComponenteCurricular(rs.getInt("id_disciplina")));
					mc.getTurma().getDisciplina().setCodigo(rs.getString("codigo"));
					if( rs.getObject("id_tipo_atividade") != null ){
						mc.getTurma().getDisciplina().setTipoAtividade(new TipoAtividade(rs.getInt("id_tipo_atividade")));
					}
						
						
					if (rs.getObject("num_unidades") != null)
						mc.getTurma().getDisciplina().setNumUnidades(rs.getInt("num_unidades"));
	
					mc.getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes(rs.getInt("id_componente_detalhes")));
					mc.getTurma().getDisciplina().getDetalhes().setNome(rs.getString("nome_disciplina"));
										
					if (rs.getObject("ch_total") != null)
						mc.getTurma().getDisciplina().getDetalhes().setChTotal(rs.getInt("ch_total"));
						
					if (rs.getObject("media_final") != null)
						mc.setMediaFinal(rs.getDouble("media_final"));
						
					mc.setNumeroFaltas(rs.getInt("numero_faltas"));
						
					if (rs.getObject("recuperacao") != null)
						mc.setRecuperacao(rs.getDouble("recuperacao"));
						
					mc.setApto(rs.getBoolean("apto"));
						
					mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("id_situacao_matricula")));
					mc.getSituacaoMatricula().setDescricao(rs.getString("descricao_sm"));
						
					if(rs.getObject("id_gestora_academica") != null){
						mc.getTurma().getDisciplina().setUnidade(new Unidade());
						mc.getTurma().getDisciplina().getUnidade().setGestoraAcademica(new Unidade(rs.getInt("id_gestora_academica")));
					}
						
					mc.getTurma().setId(rs.getInt("id_turma"));
					mc.getTurma().getDisciplina().setId(rs.getInt("id_disciplina"));
						
					mc.getDiscente().setCurso(new Curso());
					mc.getDiscente().getCurso().setModalidadeEducacao(new ModalidadeEducacao(rs.getInt("id_modalidade_educacao")));
					mc.getDiscente().getCurso().setUnidade(new Unidade());
					mc.getDiscente().getCurso().getUnidade().setSigla(rs.getString("sigla"));
						
					if (rs.getObject("id_curso") != null)
						mc.getDiscente().getCurso().setId(rs.getInt("id_curso"));
					mc.getDiscente().getCurso().setNome(rs.getString("nome_curso"));
	
					if (mc.getDiscente().getUsuario() != null)
						mc.getDiscente().setIdFoto(mc.getDiscente().getUsuario().getIdFoto());
					else
						mc.getDiscente().setIdFoto(rs.getInt("id_foto"));
	
					// Turmas de Ensino a Distância tem alguns parâmetros diferentes...
						
					if (rs.getObject("id_polo") != null && !mc.getTurma().isLato() && !mc.getTurma().getDisciplina().isEstagio()) {
						Integer metodologiaAvaliacao = rs.getInt("metodo_avaliacao");
						if (metodologiaAvaliacao == null)
							throw new TurmaVirtualException("O curso " + mc.getDiscente().getCurso().getDescricao() + " não tem metodologia de avaliação definida. Por favor, entre em contato com a SEDIS.");
	
						mc.getTurma().setPolo(new Polo(rs.getInt("id_polo")));
					}
	
					mc.setComponente(mc.getTurma().getDisciplina());
						
					if (rs.getObject("id_bloco_subunidade") != null)
						mc.getComponente().setBlocoSubUnidade(new ComponenteCurricular(rs.getInt("id_bloco_subunidade")));
						
					mc.setNotas(new ArrayList<NotaUnidade>());
					
					if (rs.getObject("id_serie") != null){
						Serie s = new Serie(rs.getInt("id_serie"));
						s.setCursoMedio(new CursoMedio(rs.getInt("id_curso_medio")));
						mc.setSerie(s);
					}
	
					if (rs.getObject("rd_nome") != null){
						ResponsavelDiscenteInfantil rd = new ResponsavelDiscenteInfantil();
						rd.setPessoa(new Pessoa());
						rd.getPessoa().setNome(rs.getString("rd_nome"));
						DiscenteInfantil di = (DiscenteInfantil) mc.getDiscente();
						di.setResponsavel(rd);
					}
					
					if (rs.getObject("ord_nome") != null){
						ResponsavelDiscenteInfantil ord = new ResponsavelDiscenteInfantil();
						ord.setPessoa(new Pessoa());
						ord.getPessoa().setNome(rs.getString("ord_nome"));
						DiscenteInfantil di = (DiscenteInfantil) mc.getDiscente();
						di.setOutroResponsavel(ord);
					}
					
					if (rs.getObject("data_nascimento") != null)
						mc.getDiscente().getPessoa().setDataNascimento(rs.getDate("data_nascimento"));
					
					if (rs.getObject("nome_pai") != null)
						mc.getDiscente().getPessoa().setNomePai(rs.getString("nome_pai"));
					
					if (rs.getObject("nome_mae") != null)
						mc.getDiscente().getPessoa().setNomeMae(rs.getString("nome_mae"));
					
					if (rs.getObject("telefone_fixo") != null)
						mc.getDiscente().getPessoa().setTelefone(rs.getString("telefone_fixo"));
					
					if (rs.getObject("codigo_area_nacional_telefone_fixo") != null)
						mc.getDiscente().getPessoa().setCodigoAreaNacionalTelefoneFixo(rs.getShort("codigo_area_nacional_telefone_fixo"));
					
					matriculas.add(mc);
				}
	
				// Se tiver nota_unidade e for do mesmo usuário
				boolean mesmoUsuario = true;
					
				if (rs.getObject("id_usuario") != null && mc.getDiscente().getUsuario() != null)
					mesmoUsuario = (   rs.getInt("id_usuario")  ==   mc.getDiscente().getUsuario().getId()  );
					
				if (rs.getObject("id_nota_unidade") != null && mesmoUsuario){
						
					NotaUnidade nota = new NotaUnidade ();
						
					if (rs.getObject("id_nota_unidade") != null)
						nota.setId(rs.getInt("id_nota_unidade"));
						
					if (rs.getObject("unidade") != null)
						nota.setUnidade(rs.getByte("unidade"));
						
					if (rs.getObject("nota") != null)
						nota.setNota(rs.getDouble("nota"));
						
					if (rs.getObject("faltas") != null)
						nota.setFaltas(rs.getShort("faltas"));
						
					nota.setMatricula(mc);
					
					mc.getNotas().add(nota);
				}
					
				if (rs.getObject("id_solicitacao_apoio_nee") != null && mesmoUsuario ){
						
					SolicitacaoApoioNee solNee = new SolicitacaoApoioNee();
						
					if (rs.getObject("id_solicitacao_apoio_nee") != null)
						solNee.setId(rs.getInt("id_solicitacao_apoio_nee"));
						
					if (rs.getObject("justificativa_solicitacao") != null)
						solNee.setJustificativaSolicitacao(rs.getString("justificativa_solicitacao"));
						
					if (rs.getObject("data_cadastro") != null)
						solNee.setDataCadastro(rs.getDate("data_cadastro"));
						
					if (rs.getObject("parecer_comissao") != null)
						solNee.setParecerComissao(rs.getString("parecer_comissao"));
						
					if (rs.getObject("id_status_atendimento") != null)
						solNee.setStatusAtendimento(new StatusAtendimento( rs.getInt("id_status_atendimento") ) );
						
					boolean addSolicitacaoNee = true;
					for (SolicitacaoApoioNee sol : mc.getDiscente().getSolicitacoesApoioNee()) {
						if ( sol.getId() == solNee.getId() )
							addSolicitacaoNee = false;
					}
					if (addSolicitacaoNee) mc.getDiscente().getSolicitacoesApoioNee().add(solNee);
						
				}
			}
		} finally {
			closeResultSet(rs);			
		}
		
		return matriculas;
	}

}