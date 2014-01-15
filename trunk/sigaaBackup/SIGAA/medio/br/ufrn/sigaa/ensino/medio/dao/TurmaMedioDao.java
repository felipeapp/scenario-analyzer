/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 25/07/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaComponenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.OrdemBuscaDisciplina;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoUtil;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.nee.dominio.StatusAtendimento;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe de Dao com consultas sobre a entidade Turma do ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class TurmaMedioDao extends GenericSigaaDAO{

	/**
	 * Retorna todas as matrículas da turma informada que possuem as situações também informadas
	 *
	 * @param idTurma
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponenteMedio> findMatriculasByTurma(int idTurma, SituacaoMatricula... situacoes) throws DAOException {

		// projeção da consulta por discentes da turma
		String projecao = " mc.id, "
				+ " d.id, " 
				+ " d.matricula, " 
				+ " p.nome, "
				+ " cc.id, " 
				+ " cc.codigo,"
				+ " det.nome, " 
				+ " cc.numUnidades, " 
				+ " mc.mediaFinal, " 
				+ " mc.numeroFaltas, " 
				+ " mc.situacaoMatricula.id, " 
				+ " mc.situacaoMatricula.descricao, " 
				+ " d.nivel, " 
				+ " s.id, " 
				+ " s.numero, " 
				+ " s.descricao, " 
				+ " mds.id, " 
				+ " sm.id, " 
				+ " sm.descricao, " 
				+ " mds.dependencia "; 
		
		StringBuilder hql = new StringBuilder("select "+projecao
				  + " from MatriculaComponente mc " 
				  + " inner join mc.discente d "
				  + " inner join d.pessoa p "
				  + " inner join mc.turma t "
				  + " inner join t.disciplina cc "
				  + " inner join cc.detalhes det "
				  + " left join mc.serie s "
				  + " , TurmaSerieAno tsa "
				  + " inner join tsa.turmaSerie ts "
				  + " , MatriculaDiscenteSerie mds "
				  + " inner join mds.situacaoMatriculaSerie sm "
				  + " where t.id = ? ");
		hql.append( " and tsa.turma.id = t.id " 
				  + " and mds.discenteMedio.id = d.id "
				  + " and mds.turmaSerie.id = ts.id " 
				  + " and sm.id IN "+ UFRNUtils.gerarStringIn(SituacaoMatriculaSerie.getSituacoesAtivas())); 
		
		if (situacoes != null && situacoes.length > 0) {
			hql.append(" and mc.situacaoMatricula.id in ");
			hql.append(UFRNUtils.gerarStringIn(situacoes));
		}

		hql.append(" order by d.pessoa.nome ");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger(0, idTurma);

		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		List<MatriculaComponenteMedio> matriculas = new ArrayList<MatriculaComponenteMedio>();

		if (result != null && !result.isEmpty()) {

			for (Object[] linha : result) {
				
				
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId((Integer) linha[0]);

				mc.setDiscente(new Discente());
				mc.getDiscente().setId((Integer) linha[1]);
				mc.getDiscente().setMatricula((Long) linha[2]);
				mc.getDiscente().setPessoa(new Pessoa());
				mc.getDiscente().getPessoa().setNome((String) linha[3]);

				mc.setTurma(new Turma());
				mc.getTurma().setDisciplina(new ComponenteCurricular());
				mc.getTurma().getDisciplina().setId((Integer) linha[4]);
				mc.getTurma().getDisciplina().setCodigo((String) linha[5]);
				mc.getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes());
				mc.getTurma().getDisciplina().getDetalhes().setNome((String) linha[6]);
				mc.getTurma().getDisciplina().setNumUnidades((Integer) linha[7]);
				mc.setMediaFinal((Double) linha[8]);
				mc.setNumeroFaltas((Integer) linha[9]);
				mc.setSituacaoMatricula(new SituacaoMatricula());
				mc.getSituacaoMatricula().setId((Integer) linha[10]);
				mc.getSituacaoMatricula().setDescricao((String) linha[11]);
				mc.getDiscente().setNivel((Character) linha[12]);

				if ( (Integer) linha[13] != null ) {
					mc.setSerie(new Serie());
					mc.getSerie().setId((Integer) linha[13]);
					mc.getSerie().setNumero((Integer) linha[14]);
					mc.getSerie().setDescricao((String) linha[15]);
				}	

				MatriculaDiscenteSerie mds = new MatriculaDiscenteSerie();
				mds.setId((Integer) linha[16]);
				mds.setSituacaoMatriculaSerie(new SituacaoMatriculaSerie((Integer) linha[17], (String) linha[18]));
				mds.setDependencia((Boolean) linha[19]);
				
				matriculas.add(new MatriculaComponenteMedio(mds, mc));
			}
		}
		return matriculas;
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
					"select mc.id_matricula_componente, d.id_discente, d.matricula, d.nivel, p.id_pessoa, p.nome, p.email as emailPessoa, p.cpf_cnpj, u.id_usuario, u.login, u.email as emailUsuario, t.id_turma, t.codigo as codigo_turma, t.ano, t.periodo, " +
					"cc.id_disciplina, cc.codigo, cc.num_unidades, cd.id_componente_detalhes, cd.nome as nome_disciplina, cd.ch_total, mc.media_final, mc.numero_faltas, mc.recuperacao, " +
					"mc.apto, sm.id_situacao_matricula, sm.descricao as descricao_sm, un.id_gestora_academica, c.id_curso, c.nome as nome_curso, d.id_foto, po.id_polo, ma.metodo_avaliacao, " +
					"ma.id, ma.porcentagem_tutor, ma.porcentagem_professor, ma.numero_aulas, cc.id_bloco_subunidade, nu.id_nota_unidade, nu.unidade, nu.nota, nu.faltas, " +
					"nee.id_solicitacao_apoio_nee, nee.justificativa_solicitacao, nee.data_cadastro, nee.parecer_comissao, nee.ativo, nee.id_status_atendimento, id_tipo_atividade," +
					"mod.id_modalidade_educacao, unid.sigla, s.id_serie, s.numero as serie_numero, s.descricao as serie_descricao, c.id_curso as id_curso_serie, c.nome as nome_curso_serie " +
				
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
					mc.setDiscente(new Discente());
					mc.getDiscente().setId(rs.getInt("id_discente"));
					
					if (rs.getObject("matricula") != null)
						mc.getDiscente().setMatricula(rs.getLong("matricula") );
						
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
	
						if (metodologiaAvaliacao == MetodoAvaliacao.UMA_PROVA)
							mc.getTurma().getDisciplina().setNumUnidades(1);
						else
							mc.getTurma().getDisciplina().setNumUnidades(2);
					}
	
					mc.setComponente(mc.getTurma().getDisciplina());
						
					if (rs.getObject("id_bloco_subunidade") != null)
						mc.getComponente().setBlocoSubUnidade(new ComponenteCurricular(rs.getInt("id_bloco_subunidade")));
						
					mc.setNotas(new ArrayList<NotaUnidade>());
	
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
						
					if (rs.getObject("nota") != null){
						String notaValue = rs.getObject("nota").toString() ;
						nota.setNota(Double.valueOf(notaValue));
					}	
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
				
				if (rs.getObject("id_serie") != null && mesmoUsuario ){
					Serie serie = new Serie(); 
					if (rs.getObject("id_serie") != null)
						serie.setId(rs.getInt("id_serie"));
					if (rs.getObject("serie_numero") != null)
						serie.setNumero(rs.getInt("serie_numero"));
					if (rs.getObject("serie_descricao") != null)
						serie.setDescricao(rs.getString("serie_descricao"));
					if (rs.getObject("id_curso_serie") != null){
						serie.setCursoMedio(new CursoMedio(rs.getInt("id_curso_serie")));
						serie.getCursoMedio().setNome( rs.getString("nome_curso_serie"));
					}	
					
					mc.setSerie(serie);
				}
					
			}
		} finally {
			closeResultSet(rs);			
		}
		
		return matriculas;
	}
	
	/**
	 * Consulta geral de turmas filtrando por diversos parâmetros.
	 * @param publico - busca somente as turmas públicas para o discente.
	 * @param palavraChave - busca em nome do componente e em topicos de aula.
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<Turma> findGeral(Character nivel, Unidade unidade, String codigoComp, String codigoTurma,
			String nome, String nomeDocente, Integer[] situacao, Integer ano, Integer periodo, Polo polo, Collection<Curso> cursos,
			ModalidadeEducacao modalidade, Curso curso, Serie serie, Integer idComponente, boolean externos, String local, Integer tipoTurma,
			boolean publico,String palavraChave, String horario, Integer ordenarPor) throws DAOException, LimiteResultadosException {
		try {
			Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
			StringBuilder hql = new StringBuilder();
			StringBuilder projecao = new StringBuilder(
					" t.id, d.id, p.nome, dt.chDedicadaPeriodo, t.ano, t.periodo, t.distancia, " +
					" t.codigo, t.local, t.descricaoHorario, t.capacidadeAluno, t.disciplina.id, " +
					" t.disciplina.codigo, t.disciplina.nivel, t.disciplina.detalhes.nome,t.disciplina.detalhes.chTotal," +
					" t.disciplina.unidade.id, t.disciplina.unidade.nome, t.disciplina.unidade.sigla, t.situacaoTurma.id, " +
					" t.situacaoTurma.descricao, t.idPolo, e.descricao, t.tipo, t.curso.id, t.processada, t.processadaRematricula ");

			// Contabilização de matrículas ativas
			String countMatriculas = "select count(mc.id) from MatriculaComponente as mc where mc.turma.id = t.id " +
				"and mc.situacaoMatricula.id in " + gerarStringIn(situacoes);
			projecao.append(", ("+countMatriculas+") as total_matriculados ");

			// Contabilização de solicitações pendentes
			String countEspera = "select count(sol.id) from SolicitacaoMatricula as sol where sol.turma.id = t.id " +
				" and sol.anulado = falseValue() " +
				" and sol.idMatriculaGerada is null " +
				" and sol.status in " + gerarStringIn(SolicitacaoMatricula.getStatusSolicitacoesPendentes());
			projecao.append(", ("+countEspera+") as total_espera ");
			
			projecao.append(", ts.id, ts.nome, s.id, s.numero, s.descricao ");
			
			String count = " count(distinct t.id) ";

			if ( !externos )
				hql.append(" from Turma t left join t.docentesTurmas as dt left join dt.docente as d left join d.pessoa as p " +
						" left join t.especializacao as e inner join t.disciplina disciplina inner join disciplina.detalhes detalhes " +
						" , TurmaSerieAno tsa inner join tsa.turmaSerie ts inner join ts.serie s inner join s.cursoMedio cm");
			else
			    hql.append(" from Turma t join t.docentesTurmas as dt join dt.docenteExterno as d left join d.pessoa as p " +
			    		" left join t.especializacao as e inner join t.disciplina disciplina inner join disciplina.detalhes detalhes " +
			    		" , TurmaSerieAno tsa inner join tsa.turmaSerie ts inner join ts.serie s inner join s.cursoMedio cm" );

			// Somente as turmas que tem a permissão para visualização para aluno (pública)
			if( publico )
				hql.append(", ConfiguracoesAva ca ");


			hql.append(" WHERE ");
			hql.append(" tsa.turma.id = t.id " );
			hql.append(" AND t.agrupadora = falseValue() " );
			hql.append(" AND t.situacaoTurma.id not in "+ gerarStringIn(SituacaoTurma.getSituacoesInvalidas()));
			if (cursos != null && !cursos.isEmpty()) hql.append(" and t.curso in " + gerarStringIn(cursos));
			if (situacao != null) hql.append(" AND t.situacaoTurma.id in " + UFRNUtils.gerarStringIn(situacao));
			if (!isEmpty(unidade) && unidade.getId()>0)	hql.append(" AND t.disciplina.unidade.id = '" + unidade.getId() + "' ");
			if (codigoTurma != null) hql.append(" AND t.codigo = :codigoTurma ");
			if (codigoComp != null) hql.append(" AND t.disciplina.codigo = :codigoComp ");
			if (idComponente != null) hql.append(" AND t.disciplina.id = :idComponente ");

			// Somente as turmas que tem a permissão para visualização para aluno (pública)
			if(publico)
				hql.append(" AND ca.turma.id = t.id AND ca.permiteVisualizacaoExterna = trueValue() ");

			// Somente quando o valor da palavra chave é passada
			if(palavraChave != null){
				hql.append(" AND (t.disciplina.detalhes.nome_ascii LIKE :palavraChave");
				hql.append(" OR t.id = ANY(SELECT DISTINCT(ta.turma.id) FROM TopicoAula ta WHERE ");
				hql.append(" ta.visivel = trueValue() AND ta.ativo = trueValue() AND ");
				hql.append(" (upper(ta.descricao) LIKE :palavraChave ");
				hql.append(" OR upper(ta.conteudo) LIKE :palavraChave)))");
			}else if (nome != null) hql.append(" AND t.disciplina.detalhes.nome_ascii LIKE :nomeComponente" );

			if (ano != null) hql.append(" AND t.ano = :ano ");

			if (nomeDocente != null){
				hql.append(" AND (" + UFRNUtils.toAsciiUpperUTF8("p.nomeAscii") + " like :nomeDocente)");
			}
			if (nivel != null && NivelEnsino.isValido(nivel)) hql.append(" AND t.disciplina.nivel = :nivel ");
			if (horario != null) hql.append(" AND t.descricaoHorario like :horario ");
			if (polo != null) hql.append(" AND t.polo.id = " + polo.getId()+" ");
			if (curso != null) hql.append(" AND cm.id = " + curso.getId()+" ");
			if (serie != null) hql.append(" AND s.id = " + serie.getId()+" ");

			if (local != null) hql.append(" AND "+ UFRNUtils.toAsciiUpperUTF8("t.local")+ " like :local");
			if (tipoTurma != null) hql.append(" AND t.tipo = :tipoTurma ");
		
			String groupBy = " group by t.ano, s.numero, t.codigo, ts.nome, t.distancia, t.id, t.local, t.descricaoHorario, t.situacaoTurma.id, t.situacaoTurma.descricao, " +
			" t.disciplina.id, t.disciplina.codigo, t.disciplina.nivel, t.disciplina.detalhes.nome,t.disciplina.detalhes.chTotal, t.disciplina.unidade.id, " +
			" t.disciplina.unidade.nome, t.disciplina.unidade.sigla, d.id, p.nome, dt.chDedicadaPeriodo, t.periodo, t.capacidadeAluno, t.idPolo, " +
			" t.especializacao.descricao, t.tipo, t.curso.id, t.processada, t.processadaRematricula, ts.id, s.id, s.descricao ";
			
			String orderBy = new String();			
			
			if(ordenarPor == null || ordenarPor == OrdemBuscaDisciplina.ORDENAR_POR_COMPONENTE_CURRICULAR.ordinal()) {
				orderBy = " order by t.ano desc, t.periodo desc, t.disciplina.detalhes.nome asc, t.codigo asc";
			}
			else if(ordenarPor == OrdemBuscaDisciplina.ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS.ordinal()) {
				orderBy = " order by t.descricaoHorario";				
			}
			else if(ordenarPor == OrdemBuscaDisciplina.ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS.ordinal()) {
				orderBy = " order by p.nome, t.disciplina.detalhes.nome, t.descricaoHorario ";	
			}
			else if(ordenarPor == OrdemBuscaDisciplina.ORDENAR_POR_LOCAL.ordinal()) {
				orderBy = " order by t.local";	
			}
			else if(ordenarPor == OrdemBuscaDisciplina.ORDENAR_POR_SERIE_TURMA.ordinal()) {
				orderBy = " order by t.disciplina.detalhes.nome, t.ano desc, s.numero, ts.nome";	
			}
			
			
			
			
			HashMap<Integer, Integer> idTurmaPosicaoLista = new HashMap<Integer, Integer>();

			Query q = getSession().createQuery("select " + count + hql);
			if( nome != null )
				q.setString("nomeComponente", "%" + StringUtils.toAscii(nome.toUpperCase()) + "%");
			if (codigoComp != null)
				q.setString("codigoComp", codigoComp);

			if (codigoTurma != null) q.setString("codigoTurma", StringUtils.toAscii(codigoTurma.toUpperCase())); 
			if (idComponente != null) q.setInteger("idComponente", idComponente);
			if (palavraChave != null) q.setString("palavraChave", "%" + StringUtils.toAscii(palavraChave.toUpperCase()) + "%");
			if (ano != null) q.setInteger("ano", ano);
			if (nomeDocente != null) q.setString("nomeDocente", "%" + StringUtils.toAscii(nomeDocente.toUpperCase()) + "%");
			if (local != null) q.setString("local", "%" + StringUtils.toAscii(local.toUpperCase()) + "%");
			if (tipoTurma != null) q.setInteger("tipoTurma", tipoTurma);
			if (nivel != null && NivelEnsino.isValido(nivel)) q.setCharacter("nivel", nivel);
			if (horario != null) q.setString("horario", "%" + StringUtils.toAscii(horario.toUpperCase()) + "%");
			
			Long qtd = (Long) q.uniqueResult();
			if (qtd > 300)
				throw new LimiteResultadosException();

			q = getSession().createQuery("select " + projecao.toString() + hql + groupBy + orderBy);
			if( nome != null )
				q.setString("nomeComponente", "%" + StringUtils.toAscii(nome.toUpperCase()) + "%");
			if (codigoComp != null)
				q.setString("codigoComp", codigoComp);
			if (codigoTurma != null) q.setString("codigoTurma", StringUtils.toAscii(codigoTurma.toUpperCase())); 
			if (idComponente != null) q.setInteger("idComponente", idComponente);
			if (palavraChave != null) q.setString("palavraChave", "%" + StringUtils.toAscii(palavraChave.toUpperCase()) + "%");
			if (ano != null) q.setInteger("ano", ano);
			if (nomeDocente != null) q.setString("nomeDocente", "%" + StringUtils.toAscii(nomeDocente.toUpperCase()) + "%");
			if (local != null) q.setString("local", "%" + StringUtils.toAscii(local.toUpperCase()) + "%");
			if (tipoTurma != null) q.setInteger("tipoTurma", tipoTurma);
			if (nivel != null && NivelEnsino.isValido(nivel)) q.setCharacter("nivel", nivel);
			if (horario != null) q.setString("horario", "%" + StringUtils.toAscii(horario.toUpperCase()) + "%");

			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			ArrayList<Turma> result = new ArrayList<Turma>();
			List<Integer> idTurmas = new ArrayList<Integer>();
			//  Instancia as turmas do resultado da busca.
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);

				Turma t = new Turma();
				t.setId((Integer) colunas[col++]);	
				idTurmas.add(t.getId());
				if( !idTurmaPosicaoLista.isEmpty() && idTurmaPosicaoLista.get(t.getId()) != null)				
					t = result.get(idTurmaPosicaoLista.get(t.getId()));
				DocenteTurma dt = new DocenteTurma();
				if ( !externos  ) {
					Integer idDocente = (Integer) colunas[col++];
					String nDocente = (String) colunas[col++];
					Integer ch = (Integer) colunas[col++];
					if(idDocente != null){
						dt.getDocente().setId(idDocente);
						dt.setChDedicadaPeriodo(ch);
						if (dt.getDocente().getPessoa() != null)
							dt.getDocente().getPessoa().setNome(nDocente);
						t.getDocentesTurmas().add(dt);
					}
				} else {
					Integer idDocenteExt = (Integer) colunas[col++];
					String nDocenteExt = (String) colunas[col++];
					Integer ch = (Integer) colunas[col++];
					if(idDocenteExt != null){
						dt.setDocenteExterno(new DocenteExterno(idDocenteExt));
						dt.setChDedicadaPeriodo(ch);
						if(dt.getDocenteExterno().getPessoa() != null)
							dt.getDocenteExterno().getPessoa().setNome(nDocenteExt);
						t.getDocentesTurmas().add(dt);
					}
				}
				if (idTurmaPosicaoLista.containsKey(t.getId()))
					continue;
				t.setAno((Integer) colunas[col++]);
				t.setPeriodo((Integer) colunas[col++]);
				t.setDistancia((Boolean) colunas[col++]);
				t.setCodigo((String) colunas[col++]);
				t.setLocal((String) colunas[col++]);
				t.setDescricaoHorario((String) colunas[col++]);
				t.setCapacidadeAluno((Integer) colunas[col++]);
				t.setDisciplina(new ComponenteCurricular((Integer) colunas[col++]));
				t.getDisciplina().setCodigo((String) colunas[col++]);
				t.getDisciplina().setNivel((Character) colunas[col++]);
				t.getDisciplina().setNome((String) colunas[col++]);
				t.getDisciplina().setChTotal((Integer) colunas[col++]);
				Integer idUnidade = (Integer) colunas[col++];
				if (idUnidade != null){
					t.getDisciplina().getUnidade().setId(idUnidade);
					t.getDisciplina().getUnidade().setNome((String)colunas[col++]);
					t.getDisciplina().getUnidade().setSigla((String)colunas[col++]);
				}
				t.setSituacaoTurma(new SituacaoTurma());
				t.getSituacaoTurma().setId((Integer) colunas[col++]);
				t.getSituacaoTurma().setDescricao((String) colunas[col++]);


				Integer idPolo = (Integer) colunas[col++];
				if (idPolo != null) {
					t.setPolo(new Polo());
					t.getPolo().setId(idPolo);
				}
				String descricaoEspecializacao = (String) colunas[col++];
				if(descricaoEspecializacao != null){
					t.setEspecializacao(new EspecializacaoTurmaEntrada());
					t.getEspecializacao().setDescricao(descricaoEspecializacao);
				}

				t.setTipo( (Integer) colunas[ col++ ] );

				Integer idCurso = (Integer) colunas[ col++ ];
				if( idCurso != null ){
					t.setCurso( new Curso() );
					t.getCurso().setId(idCurso);
				}

				t.setProcessada((Boolean) colunas[col++]);
				t.setProcessadaRematricula((Boolean) colunas[col++]);
				
				Long totalMatriculados = (Long) colunas[col++];
				t.setQtdMatriculados(totalMatriculados);

				t.setQtdEspera( (Long) colunas[col++] );
				
				Integer idTurmaSerie = (Integer) colunas[ col++ ];
				if ( idTurmaSerie != null ){
					t.setTurmaSerie(new TurmaSerie());
					t.getTurmaSerie().setId(idTurmaSerie);
					t.getTurmaSerie().setNome((String) colunas[ col++ ]);
					t.getTurmaSerie().setSerie(new Serie());
					t.getTurmaSerie().getSerie().setId((Integer) colunas[ col++ ]);
					t.getTurmaSerie().getSerie().setNumero((Integer) colunas[ col++ ]);
					t.getTurmaSerie().getSerie().setDescricao((String) colunas[ col++ ]);
				}
				
				
				result.add(t);
				idTurmaPosicaoLista.put(t.getId(), result.size()-1);				
			}
			
			return result;
		} catch (LimiteResultadosException lre) {
			throw new LimiteResultadosException(lre);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca as turmas de acordo com os parâmetros informados
	 *
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<Turma> findGeral(Character nivel, Unidade unidade, String codigoComp, String codigoTurma,
			String nome, String nomeDocente, Integer[] situacao, Integer ano, Integer periodo, Polo polo, Collection<Curso> cursos, 
			ModalidadeEducacao modalidade, Curso curso, Serie serie, Integer idComponente, String local, Integer tipoTurma, String horario, Integer ordenarPor) throws DAOException, LimiteResultadosException {


		ArrayList<Turma> result = new ArrayList<Turma>();
		result.addAll(findGeral(nivel, unidade, codigoComp, codigoTurma, nome, nomeDocente, situacao, ano, periodo, polo, cursos, modalidade, curso, serie, idComponente, false, local, tipoTurma, false, null,horario,ordenarPor));

		// exclui as repetidas pois as turmas com docente externo vem na primeira consulta também com a coleção de docentes vazia
		Collection<Turma> comDocentesExternos = findGeral(nivel, unidade, codigoComp, codigoTurma, nome, nomeDocente, situacao, ano, periodo, polo, cursos, modalidade, curso, serie, idComponente, true, local, tipoTurma, false, null, horario,ordenarPor);
		for ( Turma t : comDocentesExternos ) {
			int existe = result.indexOf(t);
			if ( existe != -1 ) {
				Turma outra = result.get( result.indexOf(t) );
				outra.getDocentesTurmas().addAll(t.getDocentesTurmas());
			} else {
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Retorna todas as disciplinas que possuem o discente, situações de matrícula e situações da disciplina informados
	 *
	 * @param discente
	 * @param situacoesMatricula
	 * @param situacoesTurma
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findAllByDiscente(Discente discente, SituacaoMatricula[] situacoesMatricula, SituacaoTurma[] situacoesTurma) throws DAOException {
		try {
			String projecao = "md.turma.id as turma.id," +
					" md.turma.disciplina.detalhes.nome as turma.disciplina.detalhes.nome," +
					" md.turma.disciplina.codigo as turma.disciplina.codigo," +
					" md.turma.disciplina.detalhes.chTotal as turma.disciplina.detalhes.chTotal," +
					" md.turma.disciplina.detalhes.crAula as turma.disciplina.detalhes.crAula," +
					" md.turma.disciplina.detalhes.crLaboratorio as turma.disciplina.detalhes.crLaboratorio," +
					" md.turma.disciplina.detalhes.crEstagio as turma.disciplina.detalhes.crEstagio," +
					" md.turma.disciplina.nivel as turma.disciplina.nivel," +
					" md.turma.disciplina.unidade.id as turma.disciplina.unidade.id," +
					" md.turma.descricaoHorario as turma.descricaoHorario," +
					" md.turma.distancia as turma.distancia," +
					" md.turma.ano as turma.ano," +
					" md.turma.periodo as turma.periodo," +
					" md.turma.codigo as turma.codigo," +
					" md.turma.local as turma.local," +
					" md.turma.turmaAgrupadora.id as turma.turmaAgrupadora.id," +
					" md.turma.situacaoTurma.descricao as turma.situacaoTurma.descricao," +
					" ts.nome as turma.turmaSerie.nome, " +
					" s.numero as turma.turmaSerie.serie.numero, " +
					" s.descricao as turma.turmaSerie.serie.descricao";
			StringBuffer hql = new StringBuffer();
			hql.append("select ");
			hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
			hql.append(" from MatriculaComponente md inner join md.turma t inner join t.situacaoTurma st, " +
					" TurmaSerieAno tsa inner join tsa.turmaSerie ts inner join ts.serie s where ");
			if (situacoesMatricula != null)
				hql.append("md.situacaoMatricula.id in " + gerarStringIn(situacoesMatricula) + " and ");
			hql.append("t.id = tsa.turma.id and md.discente.id = :discenteId  and md.turma.situacaoTurma.id in " + gerarStringIn(situacoesTurma)
					+ " order by md.turma.ano desc, s.numero desc, md.turma.codigo, md.turma.disciplina.detalhes.nome asc");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("discenteId", discente.getId());

			@SuppressWarnings("unchecked")
			List<Turma> turmas = (List<Turma>) HibernateUtils.parseTo(q.list(), projecao, Turma.class, "turma");
			String idsTurmas = "";

			for (Turma t : turmas) {
				idsTurmas += (idsTurmas.equals("") ? "" : ",") + t.getId();
			}
			
			if (!isEmpty(turmas)) {
				// Descobre a quantidade de alunos matriculados nas turmas do discente.
				Query qtd = getSession().createSQLQuery(
						"select id_turma, count(id_matricula_componente) from ensino.matricula_componente " +
						"where id_turma in ("+idsTurmas+") and " +
						"id_situacao_matricula in " + gerarStringIn(situacoesMatricula) +
				" group by id_turma");
				
				@SuppressWarnings("unchecked")
				List <Object []> linhas = qtd.list();
				
				Map <Integer, Integer> mapaQuantidades = new HashMap <Integer, Integer> ();
				
				for (Object [] linha : linhas)
					mapaQuantidades.put(Integer.parseInt(""+linha[0]), Integer.parseInt(""+linha[1]));
				
				for (Turma turma : turmas){
					Integer mats = mapaQuantidades.get(turma.getId());
					turma.setQtdMatriculados(mats == null ? 0 : mats);
				}
			}

			return turmas;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
}
