/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/01/2007'
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DadosCalculosDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatriculaEquivalentes;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.IntegralizacoesHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.util.RepositorioInformacoesCalculoDiscente;

/**
 * DAO com consultas utilizadas durante a operação de matrícula
 *
 * @author ricardo
 *
 */
public class MatriculaGraduacaoDao extends GenericSigaaDAO {

	/**
	 * Busca de sugestões para matrícula de discentes em turmas.
	 * A consulta específica depende do nível do discente.
	 *
	 * @param discente
	 * @param turmasMatriculadas
	 * @param ano
	 * @param periodo
	 * @param discenteLogado 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SugestaoMatricula> findSugestoesMatricula(DiscenteAdapter discente,
			Collection<Turma> turmasMatriculadas, Integer ano, Integer periodo, boolean discenteLogado) throws DAOException {

		if (discente.isGraduacao()) {
			Curso curso = (discente.getCurso() != null && (discente.getCurso().isProbasica() || discente.getCurso().isADistancia()) ) ? discente.getCurso() : null;
			Polo polo = discente.isGraduacao() ? ((DiscenteGraduacao)discente).getPolo() : null ;
			if (polo != null)
				polo = ((DiscenteGraduacao)discente).getPolo();

			return findSugestoesMatriculaGraduacao(discente, curso, polo, turmasMatriculadas, ano, periodo, discenteLogado);
		} else if (discente.isTecnico()) {
			return findSugestoesMatriculaTecnico(discente, turmasMatriculadas, ano, periodo);
		}
		return null;
	}

	/** Busca de sugestões para matrícula de discentes em turmas de nível técnico.
	 * @param discente
	 * @param turmasMatriculadas
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	private Collection<SugestaoMatricula> findSugestoesMatriculaTecnico(DiscenteAdapter discente, Collection<Turma> turmasMatriculadas,
			Integer ano, Integer periodo) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			String turmasDeModulo = "SELECT distinct  t.id_turma,mod.descricao as MODULO_DESCRICAO  ,t.codigo as cod_turma, " +
					" t.descricao_horario,t.local, t.observacao,  t.id_situacao_turma, dt.id_docente_turma as id_docente, " +
					" p.nome as nome_docente, p2.nome as nome_docente_externo, c.id_disciplina, c.codigo as cod_componente, " +
					" cd.nome, cd.co_requisito, cd.pre_requisito, cd.equivalencia, esp.descricao, t.id_especializacao_turma_entrada, " +
					" c.matriculavel, c.qtd_max_matriculas" +
					"  FROM tecnico.discente_tecnico disc join tecnico.turma_entrada_tecnico te on (disc.id_turma_entrada = te.id_turma_entrada)   " +
					"  join tecnico.estrutura_curricular_tecnica ec on (disc.id_estrutura_curricular = ec.id_estrutura_curricular)   " +
					"  left join tecnico.modulo_curricular mc on (ec.id_estrutura_curricular=mc.id_estrutura_curricular) " +
					"  join tecnico.modulo mod on (mod.id_modulo = mc.id_modulo)  " +
					"  join tecnico.modulo_disciplina md on (md.id_modulo = mod.id_modulo)  " +
					"  join ensino.componente_curricular c on (c.id_disciplina = md.id_disciplina)   " +
					"  join ensino.turma t on (t.id_disciplina = c.id_disciplina )" +
					"  join ensino.componente_curricular_detalhes cd on (c.id_detalhe = cd.id_componente_detalhes) " +
					"  left join tecnico.especializacao_turma_entrada esp on (esp.id_especializacao_turma_entrada=t.id_especializacao_turma_entrada)" +
					"  left join ensino.docente_turma dt on (t.id_turma=dt.id_turma)   " +
					"  left join rh.servidor s on (dt.id_docente=s.id_servidor)   " +
					"  left join comum.pessoa p on (s.id_pessoa=p.id_pessoa)   " +
					"  left join ensino.docente_externo de on (de.id_docente_externo = dt.id_docente_externo) "+ 
					"  left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) "+
					"  WHERE t.id_situacao_turma in (1,2) AND disc.id_discente =  " + discente.getId() +
					" AND t.ano = "+ano+" AND t.periodo = "+periodo+" AND c.id_disciplina not in " +
					"(select mat.id_componente_curricular from ensino.matricula_componente mat " +
					" join ensino.componente_curricular cc on (mat.id_componente_curricular = cc.id_disciplina)" +
					" where mat.id_situacao_matricula in "+gerarStringIn(SituacaoMatricula.getSituacoesPagas())+
					"  AND mat.id_discente=disc.id_discente " +
					" group by mat.id_componente_curricular, qtd_max_matriculas"+
					" having cc.qtd_max_matriculas <= count(mat.id_componente_curricular)"+
					" ) " +
					" and t.id_turma_bloco is null ORDER BY mod.descricao, c.codigo, t.codigo";

			String complementares = "SELECT distinct  t.id_turma, null, t.codigo as cod_turma, " +
					" t.descricao_horario,t.local, t.observacao, t.id_situacao_turma, dt.id_docente_turma as id_docente, " +
					" p.nome as nome_docente, p2.nome as nome_docente_externo, c.id_disciplina, c.codigo as cod_componente, " +
					" cd.nome, cd.co_requisito, cd.pre_requisito, cd.equivalencia, esp.descricao, t.id_especializacao_turma_entrada, " +
					" c.matriculavel, c.qtd_max_matriculas" +
					"  FROM tecnico.discente_tecnico disc join tecnico.turma_entrada_tecnico te on (disc.id_turma_entrada = te.id_turma_entrada)   " +
					"  join tecnico.estrutura_curricular_tecnica ec on (te.id_estrutura_curricular = ec.id_estrutura_curricular)   " +
					"  left join tecnico.disciplina_complementar dc on (ec.id_estrutura_curricular=dc.id_estrutura_curricular)" +
					"  join ensino.componente_curricular c on (c.id_disciplina = dc.id_disciplina)   " +
					"  join ensino.turma t on (t.id_disciplina = c.id_disciplina )" +
					"  left join tecnico.especializacao_turma_entrada esp on (esp.id_especializacao_turma_entrada=t.id_especializacao_turma_entrada)" +
					"  join ensino.componente_curricular_detalhes cd on (c.id_detalhe = cd.id_componente_detalhes)  " +
					"  left join ensino.docente_turma dt on (t.id_turma=dt.id_turma)   " +
					"  left join rh.servidor s on (dt.id_docente=s.id_servidor)   " +
					"  left join comum.pessoa p on (s.id_pessoa=p.id_pessoa)   " +
					"  left join ensino.docente_externo de on (de.id_docente_externo = dt.id_docente_externo) "+ 
					"  left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) "+
					"  WHERE t.id_situacao_turma in (1,2) AND disc.id_discente =  " + discente.getId() +
					"  AND t.agrupadora = falseValue() AND t.ano = "+ano+" AND t.periodo = "+periodo+" AND " +
					"c.id_disciplina not in (select mat.id_componente_curricular from ensino.matricula_componente mat " +
					"where mat.id_situacao_matricula in "+gerarStringIn(SituacaoMatricula.getSituacoesPagas()) +
					"AND mat.id_discente=disc.id_discente )  " +
					"and t.id_turma_bloco is null ORDER BY c.codigo, t.codigo";

			st = con.prepareStatement("("+turmasDeModulo  +") union (" + complementares+") order by MODULO_DESCRICAO, cod_componente");
			rs = st.executeQuery();

			DiscenteDao ddao = new DiscenteDao();
			ddao.setSession(getSession());
			Collection<ComponenteCurricular> componentesPagos =  ddao.findComponentesCurricularesConcluidos(discente.getDiscente());

			TreeSet<Integer> componentesMatriculados =  new TreeSet<Integer>();
			for (Turma t : turmasMatriculadas) {
				componentesMatriculados.add(t.getDisciplina().getId());
			}

			// Percorrer ResultSet e criar objetos de domínio
			ArrayList<SugestaoMatricula> sugestoes = new ArrayList<SugestaoMatricula>();
			SugestaoMatricula sugestao = new SugestaoMatricula();
			while (rs.next()) {
				boolean adicionar = false;
				if (sugestao.getTurma().getId() != rs.getInt("ID_TURMA")) {
					sugestao = new SugestaoMatricula();
					adicionar = true;
				}
				sugestao.getTurma().setId(rs.getInt("ID_TURMA"));
				sugestao.getTurma().setSituacaoTurma(new SituacaoTurma(rs.getInt("ID_SITUACAO_TURMA")));
				sugestao.getTurma().setCodigo(rs.getString("COD_TURMA"));
				sugestao.getTurma().setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				sugestao.getTurma().setLocal(rs.getString("LOCAL"));
				sugestao.getTurma().setObservacao(rs.getString("OBSERVACAO"));
				
				sugestao.getTurma().setEspecializacao(new EspecializacaoTurmaEntrada());
				sugestao.getTurma().getEspecializacao().setDescricao(rs.getString("DESCRICAO"));
				sugestao.getTurma().getEspecializacao().setId(rs.getInt("id_especializacao_turma_entrada"));

				ComponenteCurricular componente = new ComponenteCurricular();
				componente.setId(rs.getInt("ID_DISCIPLINA"));
				componente.setCoRequisito(rs.getString("CO_REQUISITO"));
				componente.setPreRequisito(rs.getString("PRE_REQUISITO"));
				componente.setEquivalencia(rs.getString("EQUIVALENCIA"));
				componente.setNome(rs.getString("NOME"));
				componente.setCodigo(rs.getString("COD_COMPONENTE"));
				componente.setMatriculavel(rs.getBoolean("MATRICULAVEL"));
				componente.setQtdMaximaMatriculas(rs.getInt("QTD_MAX_MATRICULAS"));

				sugestao.getTurma().setDisciplina(componente);

				sugestao.setNivel(rs.getString("MODULO_DESCRICAO"));
				if (isEmpty(sugestao.getNivel())) {
					sugestao.setNivel("ELETIVAS");
				}

				if (!isEmpty(componente.getPreRequisito()))  {
					boolean possuiPreRequisito = ExpressaoUtil.evalComTransitividade(componente.getPreRequisito(), discente.getId(), componentesPagos);
					if (!possuiPreRequisito)
						sugestao.setTipoInvalido(SugestaoMatricula.PRE_REQUISITO);
				}

				if (sugestao.getTipoInvalido() == null && !isEmpty(componente.getEquivalencia())) {
					if (ExpressaoUtil.eval(componente.getEquivalencia(), componentesPagos) )
						sugestao.setTipoInvalido(SugestaoMatricula.EQUIVALENTE_PAGO);

					if (sugestao.getTipoInvalido() == null && componente.getQtdMaximaMatriculas() == 1 && componentesMatriculados.contains(componente.getId()) || ExpressaoUtil.eval(componente.getEquivalencia(), componentesMatriculados))
						sugestao.setTipoInvalido(SugestaoMatricula.JA_MATRICULADO);
				}

				String nomeDocente = rs.getString("NOME_DOCENTE");
				if (nomeDocente == null) {
					nomeDocente = rs.getString("NOME_DOCENTE_EXTERNO");
				}
				
				sugestao.addDocentesNomes(rs.getInt("ID_DOCENTE"), nomeDocente );
				if (adicionar)
					sugestoes.add(sugestao);
			}
			ArrayList<Integer> praRemover = new ArrayList<Integer>(0);
			for (int i = 0 ; i < sugestoes.size(); i++) {
				SugestaoMatricula sug = sugestoes.get(i);
				EspecializacaoTurmaEntrada esp = sug.getTurma().getEspecializacao();
				DiscenteTecnico dt = (DiscenteTecnico) discente;
				if (esp != null && dt.getTurmaEntradaTecnico().getEspecializacao() != null && esp.getId() != dt.getTurmaEntradaTecnico().getEspecializacao().getId())
					praRemover.add(i);
				if (!sug.getTurma().getDisciplina().isMatriculavel()) {
					sug.setTipoInvalido(SugestaoMatricula.NAO_MATRICULAVEL);
				}
			}
			for (Integer id : praRemover)
				sugestoes.remove(new SugestaoMatricula(id));

			return sugestoes;
		} catch (Exception e) {
			throw new DAOException(e);
		}  finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}
	/**
	 * Busca pelas turmas de componentes do currículo de um discente que ele ainda
	 * não concluiu.
	 * os componentes devem ser 'matriculáveis'
	 * @param discenteLogado 
	 */
	private Collection<SugestaoMatricula> findSugestoesMatriculaGraduacao(DiscenteAdapter discente, Curso curso, Polo polo,
			Collection<Turma> turmasMatriculadas, Integer ano, Integer periodo, boolean discenteLogado) throws DAOException {

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		DiscenteDao ddao = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT t.id_turma,t.codigo as cod_turma, t.descricao_horario,t.local, t.id_situacao_turma, dt.id_docente_turma, " +
					"p1.nome as nome_docente, p2.nome as nome_docente_externo,  "
					+ " c.id_disciplina, c.codigo as cod_componente, cd.nome, cc.semestre_oferta, " +
							"cd.co_requisito, cd.pre_requisito, cc.obrigatoria as OBRIGATORIA," +
							"rc.vagas_atendidas, rc.id_matriz_curricular as matriz_reserva "
					+ " FROM ensino.turma t join ensino.componente_curricular c on (t.id_disciplina = c.id_disciplina)" +
							" join graduacao.curriculo_componente cc on (cc.id_componente_curricular = c.id_disciplina or cc.id_componente_curricular = c.id_bloco_subunidade)" +
							" join discente d on (cc.id_curriculo = d.id_curriculo)" +
							" join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao)" +
							" join ensino.componente_curricular_detalhes cd on (c.id_detalhe = cd.id_componente_detalhes)" +
							" left join graduacao.reserva_curso rc on (rc.id_turma=t.id_turma and rc.id_matriz_curricular = dg.id_matriz_curricular)" +
							" left join ensino.docente_turma dt on (t.id_turma=dt.id_turma) " +
							" left join rh.servidor s on (s.id_servidor = dt.id_docente) "+ 
							" left join comum.pessoa p1 on (s.id_pessoa = p1.id_pessoa) " +
							" left join ensino.docente_externo de on (de.id_docente_externo = dt.id_docente_externo) "+ 
							" left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) "							
					+ " WHERE 1 = 1 ");
			if (curso != null && isEmpty(polo)) // caso precise filtrar por curso de convênio
				sql.append(" AND t.id_curso="+curso.getId());
			if (polo != null) { // caso precise filtrar por pólos de EAD
				if (curso != null)
					sql.append(" AND (t.id_polo="+polo.getId() +" or t.id_curso = "+curso.getId()+")");
				else
					sql.append(" AND t.id_polo="+polo.getId());
			} else {
				sql.append(" AND (t.distancia is null or t.distancia = falseValue()) " );
			}
			if (discenteLogado) {
				sql.append(" AND c.matriculavel = trueValue() ");
			}
			sql.append(" AND t.tipo="+Turma.REGULAR+" AND t.id_situacao_turma in (1,2) AND t.agrupadora = falseValue() AND d.id_discente = ? " +
					" AND t.ano = ? AND t.periodo = ? " +
					" AND cc.id_componente_curricular not in " +
					"(select mat.id_componente_curricular from ensino.matricula_componente mat " +
					"where mat.id_situacao_matricula in  " + gerarStringIn(SituacaoMatricula.getSituacoesPagas()) +
					"AND mat.id_discente= ? )  " +
					"AND t.id_turma_bloco is null " +
					"ORDER BY cc.semestre_oferta, cc.obrigatoria desc, c.codigo, t.codigo");

			st = con.prepareStatement(sql.toString());

			// Setar parâmetros
			int i = 1;
			st.setInt(i++, discente.getId());
			st.setInt(i++, ano);
			st.setInt(i++, periodo);
			st.setInt(i++, discente.getId());

			rs = st.executeQuery();

			ddao = new DiscenteDao();
			ddao.setSession(getSession());
			Collection<ComponenteCurricular> componentesPagos =  ddao.findComponentesCurricularesConcluidos(discente.getDiscente());

			TreeSet<Integer> componentesMatriculados =  new TreeSet<Integer>();
			for (Turma t : turmasMatriculadas) {
				componentesMatriculados.add(t.getDisciplina().getId());
			}

			// Percorrer ResultSet e criar objetos de domínio
			Collection<SugestaoMatricula> sugestoes = new ArrayList<SugestaoMatricula>();
			SugestaoMatricula sugestao = new SugestaoMatricula();
			while (rs.next()) {
				boolean adicionar = false;
				if (sugestao.getTurma().getId() != rs.getInt("ID_TURMA")) {
					sugestao = new SugestaoMatricula();
					adicionar = true;
				}
				sugestao.getTurma().setId(rs.getInt("ID_TURMA"));
				sugestao.getTurma().setSituacaoTurma(new SituacaoTurma(rs.getInt("ID_SITUACAO_TURMA")));
				sugestao.getTurma().setCodigo(rs.getString("COD_TURMA"));
				sugestao.getTurma().setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				sugestao.getTurma().setLocal(rs.getString("LOCAL"));
				if (rs.getInt("vagas_atendidas") > 0) {
					sugestao.getTurma().setReservas(new ArrayList<ReservaCurso>());
					ReservaCurso rc = new ReservaCurso();
					rc.setVagasReservadas((short) rs.getInt("vagas_atendidas"));
					sugestao.getTurma().getReservas().add(rc);
				}

				ComponenteCurricular componente = new ComponenteCurricular();
				componente.setId(rs.getInt("ID_DISCIPLINA"));
				componente.setCoRequisito(rs.getString("CO_REQUISITO"));
				componente.setPreRequisito(rs.getString("PRE_REQUISITO"));
				componente.setNome(rs.getString("NOME"));
				componente.setCodigo(rs.getString("COD_COMPONENTE"));

				sugestao.setObrigatoria(rs.getBoolean("OBRIGATORIA"));
				sugestao.getTurma().setDisciplina(componente);

				sugestao.setNivel(rs.getInt("SEMESTRE_OFERTA") + "º Nível");

				String nomeDocente = rs.getString("NOME_DOCENTE");
				if (nomeDocente == null) {
					nomeDocente = rs.getString("NOME_DOCENTE_EXTERNO");
				}
				
				sugestao.addDocentesNomes(rs.getInt("ID_DOCENTE_TURMA"), nomeDocente );
				if (adicionar)
					sugestoes.add(sugestao);
			}
			
			
			analiarEquivalenciaAndPreRequisitos(discente, ddao, componentesPagos, sugestoes);
			
			return sugestoes;
		} catch (Exception e) {
			throw new DAOException(e);
		}  finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
			if (ddao !=null) ddao.close();
		}
	}

	/**
	 * Verifica Equivalências e Pré-Requisitos das suegstões de matrícula
	 * 
	 * @param discente
	 * @param ddao
	 * @param componentesPagos
	 * @param sugestoes
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void analiarEquivalenciaAndPreRequisitos(DiscenteAdapter discente, DiscenteDao ddao, Collection<ComponenteCurricular> componentesPagos, Collection<SugestaoMatricula> sugestoes) throws DAOException,	ArqException {
		if (!isEmpty(sugestoes)) {
			DadosCalculosDiscente dados = RepositorioInformacoesCalculoDiscente.INSTANCE.buscarInformacoes(discente.getId());
			Date primeiraData = dados.getDataInicio();
			Date ultimaData = dados.getDataFim();
			
			ComponenteCurricularDao ccdao = new ComponenteCurricularDao();
			ddao.setSession(getSession());
			
			MatriculaComponenteDao mcdao = new MatriculaComponenteDao();
			mcdao.setSession(getSession());
			
			List<ComponenteCurricular> ccs = new ArrayList<ComponenteCurricular>();
			for (SugestaoMatricula s : sugestoes) {
				ccs.add(s.getTurma().getDisciplina());
			}
			
			
			Map<Integer, List<Object[]>> mapaEquivalencias = ccdao.findEquivalenciasComponentesByIntervaloDatas(ccs, dados.getCurriculo().getId(), primeiraData, ultimaData);
			Collection<ComponenteCurricular> componentesPagosEMatriculados = ddao.findComponentesCurriculares(discente,SituacaoMatricula.getSituacoesPagasEMatriculadas(),null);
			Collection<MatriculaComponente> matriculas = mcdao.findPagaseMatriculadasByDiscente(discente);
			
			for (ComponenteCurricular ccPendente : ccs) {
				List<Object[]> equivalencias = mapaEquivalencias.get(ccPendente.getId());

				criaStringEquivalencia(ccPendente, equivalencias);
				
				analisaEquivalencias(ddao, sugestoes, componentesPagosEMatriculados, matriculas, ccPendente, equivalencias);
			}				
			
			analisaPreRequistos(discente, componentesPagos, sugestoes);
		}
	}

	/**
	 * Verifica Pré-Requisitos das suegstões de matrícula
	 * @param discente
	 * @param componentesPagos
	 * @param sugestoes
	 * @throws ArqException
	 */
	private void analisaPreRequistos(DiscenteAdapter discente, 	Collection<ComponenteCurricular> componentesPagos, Collection<SugestaoMatricula> sugestoes) throws ArqException {
		for (SugestaoMatricula s : sugestoes) {
			ComponenteCurricular componente = s.getTurma().getDisciplina();
			if (!isEmpty(componente.getPreRequisito()))  {
				boolean possuiPreRequisito = ExpressaoUtil.evalComTransitividade(componente.getPreRequisito(), discente.getId(), componentesPagos);
				if (!possuiPreRequisito)
					s.setTipoInvalido(SugestaoMatricula.PRE_REQUISITO);
			}
		}
	}

	/**
	 * Analisa Equivalências das sugestoes
	 * 
	 * @param ddao
	 * @param sugestoes
	 * @param componentesPagosEMatriculados
	 * @param matriculas
	 * @param ccPendente
	 * @param equivalencias
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void analisaEquivalencias(DiscenteDao ddao, Collection<SugestaoMatricula> sugestoes, Collection<ComponenteCurricular> componentesPagosEMatriculados, Collection<MatriculaComponente> matriculas, ComponenteCurricular ccPendente, List<Object[]> equivalencias) throws DAOException, ArqException {
		if (!isEmpty(equivalencias)) {
			for (Object[] infoEquivalencia : equivalencias) {
				String equivalencia = (String) infoEquivalencia[0];
				Integer idEspecifica = (Integer) infoEquivalencia[1];
				Date fimVigenciaEquivalencia = (Date) infoEquivalencia[3];
				EquivalenciaEspecifica especifica = null;
				if (!isEmpty(idEspecifica))
					especifica = ddao.findByPrimaryKey(idEspecifica, EquivalenciaEspecifica.class);
				
				// codigo para evitar que uma obrigatória seja equivalente a outra obrigatória
				Collection<ComponenteCurricular> componentesPagosEMatriculadosNaoObrigatorios = IntegralizacoesHelper.filtrarNaoObrigatorias(matriculas, componentesPagosEMatriculados);
				
				if (equivalencia != null && ExpressaoUtil.eval(equivalencia, ccPendente, componentesPagosEMatriculadosNaoObrigatorios)) {
					Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, ccPendente, componentesPagosEMatriculadosNaoObrigatorios);
					if (equivalentes != null) {
						int qtdeMatEquivalenciasCadastradas = 0;
						for (ComponenteCurricular componente : equivalentes) {
							MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
							boolean componenteEquivalente = !isEmpty(mat);
							// Existem casos onde matricula pode ser null. 
							// Por exemplo, se estiver validando apenas um conjunto de matrículas (matriculas do semestre por exemplo)
							// o componente equivalente pago pode não estar nesse pequeno conjunto de matrículas. 
							// Mas se estiver analisando todas as matrículas do discente, ai sim não deveria ser null.
							if (mat != null) {
								// Verifica se a equivalência que foi encontrada é do tipo específica e estava valendo na data da matrícula

								if (((componenteEquivalente && IntegralizacoesHelper.isEquivalenciaValendoNaDataMatricula(mat, null, fimVigenciaEquivalencia)) && especifica == null) 
										|| (especifica != null && especifica.isEquivalenciaValendoNaData(mat.getDataCadastro()))) {
									
									++qtdeMatEquivalenciasCadastradas;
								}
							}
						}
						if(equivalentes.size() == qtdeMatEquivalenciasCadastradas) {
							for (SugestaoMatricula s : sugestoes) {
								if (s.getTurma().getDisciplina().getId() == ccPendente.getId()) {
									s.setTipoInvalido(SugestaoMatricula.EQUIVALENTE_PAGO);
								}
							}
						}
					}									
				}
			}
		}
	}

	/**
	 * Retorna a string de equivalencia que vale para o componente
	 * 
	 * @param ccPendente
	 * @param equivalencias
	 */
	private void criaStringEquivalencia(ComponenteCurricular ccPendente, List<Object[]> equivalencias) {
		if (!isEmpty(equivalencias)) {
			StringBuilder sbEq = new StringBuilder();
			for (Object[] infoEquivalencia : equivalencias) {
				if (infoEquivalencia[0] != null) {
					if (sbEq.length() == 0)
						sbEq.append((String) infoEquivalencia[0]);
					else {
						sbEq.append(" OU ( ");
						sbEq.append((String) infoEquivalencia[0]);
						sbEq.append(" )  ");
					}
				}
			}
			
			if (sbEq.length() > 0) {
				ccPendente.setEquivalencia(sbEq.toString().trim());
			}
			
		}
	}

	/**
	 * Consulta que retorna as sugestões de matrículas em componentes
	 * pertencentes ao currículo do discente 
	 * 
	 * @param discente
	 * @param turmasMatriculadas
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SugestaoMatriculaEquivalentes> findSugestoesEquivalentesMatricula(DiscenteAdapter discente, 
			Collection<Turma> turmasMatriculadas, Integer ano, Integer periodo, boolean discenteLogado) throws DAOException {

		if (discente == null) {
			return null;
		}
		
		Curso curso = (discente.getCurso() != null && discente.getCurso().isProbasica() || discente.getCurso().isADistancia()) ? discente.getCurso() : null;
		Polo polo = discente.isGraduacao() ? ((DiscenteGraduacao)discente).getPolo() : null ;
		if (polo != null) {
			polo = ((DiscenteGraduacao)discente).getPolo();
		}
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
	try {
			con = Database.getInstance().getSigaaConnection();
			StringBuffer sql = new StringBuffer();
			
			sql.append("select  cc.semestre_oferta, c.id_disciplina as id_curricular,c.codigo as codigo_curricular, cd.nome as nome_curricular, cd.equivalencia as equivalencia_curricular, " +
					" c2.id_disciplina, c2.codigo as cod_componente, cd2.nome, cc.semestre_oferta, cc.obrigatoria as obrigatoria," + 
					" cd2.co_requisito, cd2.pre_requisito, cd2.equivalencia, rc.vagas_atendidas, " +
					" t.id_turma,t.codigo as cod_turma, t.descricao_horario, t.local, t.id_situacao_turma, " +
					" dt.id_docente_turma, p.nome as nome_docente, p2.nome as nome_docente_externo " +
					" FROM ensino.componente_curricular c " +
					" join ensino.componente_curricular_detalhes cd on (c.id_detalhe = cd.id_componente_detalhes) " +
					" join graduacao.curriculo_componente cc on (cc.id_componente_curricular = c.id_disciplina or cc.id_componente_curricular = c.id_bloco_subunidade) " +
					" join discente d on (cc.id_curriculo = d.id_curriculo) " +
					" join ensino.componente_curricular c2 on (cd.equivalencia like  '% '||c2.id_disciplina||' %' ) " +
					" join ensino.componente_curricular_detalhes cd2 on (c2.id_detalhe = cd2.id_componente_detalhes) " +
					" join ensino.turma t on (t.id_disciplina = c2.id_disciplina) " +
					" join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao) " +
					" left join graduacao.reserva_curso rc on (rc.id_turma=t.id_turma and rc.id_matriz_curricular = dg.id_matriz_curricular) " +
					" left join ensino.docente_turma dt on (t.id_turma=dt.id_turma) " +
					" left join rh.servidor s on (dt.id_docente=s.id_servidor) " +
					" left join comum.pessoa p on (s.id_pessoa=p.id_pessoa) " +
					" left join ensino.docente_externo de on (de.id_docente_externo = dt.id_docente_externo) "+ 
					" left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) ");
			sql.append(" WHERE d.id_discente = ?");
			if (curso != null && discente.getCurso().isProbasica()) // caso precise filtrar por curso de convênio
				sql.append(" AND t.id_curso =" + curso.getId());
			if (polo != null) { // caso precise filtrar por pólos de EAD
				if (curso != null)
					sql.append(" AND (t.id_polo="+polo.getId() +" or t.id_curso = "+curso.getId()+")");
				else
					sql.append(" AND t.id_polo="+polo.getId());
			}	
			sql.append(" AND cd.equivalencia is not null");
			if (discenteLogado) {
				sql.append(" AND c2.matriculavel = trueValue()");
			}
			sql.append(" AND t.tipo = " + Turma.REGULAR);
			sql.append(" AND t.id_situacao_turma in " + gerarStringIn(SituacaoTurma.getSituacoesAbertas()) );
			sql.append(" AND t.ano = " + ano + " AND t.periodo = " + periodo);
			sql.append(" AND t.id_turma_bloco is null AND t.agrupadora = falseValue()");
			sql.append(" AND cc.id_componente_curricular not in " +
					" (select mat.id_componente_curricular from ensino.matricula_componente mat" +
					" where mat.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesPagasEMatriculadas()) +
					" AND mat.id_discente = ? ) ");
			sql.append(" AND c2.id_disciplina not in " +
					" (select mat.id_componente_curricular from ensino.matricula_componente mat" +
					" where mat.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesPagasEMatriculadas()) +
					" AND mat.id_discente = ? ) ");				
			sql.append(" ORDER BY cc.semestre_oferta, cc.obrigatoria desc, c.codigo, c2.codigo, t.codigo");
			
			st = con.prepareStatement(sql.toString());
			int i = 1;
			st.setInt(i++, discente.getId());
			st.setInt(i++, discente.getId());
			st.setInt(i++, discente.getId());
			
			rs = st.executeQuery();

			// Buscar componentes pagos
			DiscenteDao ddao = new DiscenteDao();
			ddao.setSession(getSession());
			Collection<ComponenteCurricular> componentesPagos =  ddao.findComponentesCurricularesConcluidos(discente.getDiscente());
			
			// Criar turmas vazias a partir dos componentes
			TreeSet<Integer> componentesMatriculados =  new TreeSet<Integer>();
			for (Turma t : turmasMatriculadas) {
				componentesMatriculados.add(t.getDisciplina().getId());
			}
			
			// Percorrer ResultSet e criar objetos de domínio
			List<SugestaoMatriculaEquivalentes> sugestoes = new ArrayList<SugestaoMatriculaEquivalentes>();
			
			SugestaoMatricula sugestao = new SugestaoMatricula();
			while (rs.next()) {
				
				ComponenteCurricular componenteCurricular = new ComponenteCurricular();
				componenteCurricular.setId(rs.getInt("ID_CURRICULAR"));
				componenteCurricular.setNome(rs.getString("NOME_CURRICULAR"));
				componenteCurricular.setCodigo(rs.getString("CODIGO_CURRICULAR"));
				componenteCurricular.setEquivalencia(rs.getString("EQUIVALENCIA_CURRICULAR"));
				CurriculoComponente curriculoComponente = new CurriculoComponente(componenteCurricular);
				curriculoComponente.setSemestreOferta(rs.getInt("SEMESTRE_OFERTA"));
				curriculoComponente.setObrigatoria(rs.getBoolean("OBRIGATORIA"));
				
				SugestaoMatriculaEquivalentes sugestaoEquivalente = new SugestaoMatriculaEquivalentes(curriculoComponente);
				if ( sugestoes.contains(sugestaoEquivalente) ) {
					sugestaoEquivalente = sugestoes.get( sugestoes.indexOf(sugestaoEquivalente) );
				} else {
					// Verificar se este componente curricular foi cumprido através de alguma equivalente
					boolean cumpriuEquivalente = ExpressaoUtil.eval(sugestaoEquivalente.getEquivalencia(), componentesPagos);
					sugestaoEquivalente.setCumpriuEquivalente(cumpriuEquivalente);
					
					// Verificar se o discente está matriculado em equivalente
					boolean matriculadoEquivalente = ExpressaoUtil.eval(sugestaoEquivalente.getEquivalencia(), componentesMatriculados);
					sugestaoEquivalente.setMatriculadoEquivalente(matriculadoEquivalente);
					
					if (!cumpriuEquivalente) {
						sugestoes.add(sugestaoEquivalente);
					}
				}
				
				boolean adicionar = false;
				if (sugestao.getTurma().getId() != rs.getInt("ID_TURMA")) {
					sugestao = new SugestaoMatricula();
					adicionar = true;
				}
				
				sugestao.getTurma().setId(rs.getInt("ID_TURMA"));
				sugestao.getTurma().setSituacaoTurma(new SituacaoTurma(rs.getInt("ID_SITUACAO_TURMA")));
				sugestao.getTurma().setCodigo(rs.getString("COD_TURMA"));
				sugestao.getTurma().setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				sugestao.getTurma().setLocal(rs.getString("LOCAL"));
				if (rs.getInt("vagas_atendidas") > 0) {
					sugestao.getTurma().setReservas(new ArrayList<ReservaCurso>());
					ReservaCurso rc = new ReservaCurso();
					rc.setVagasReservadas((short) rs.getInt("vagas_atendidas"));
					sugestao.getTurma().getReservas().add(rc);
				}

				ComponenteCurricular componente = new ComponenteCurricular();
				componente.setId(rs.getInt("ID_DISCIPLINA"));
				componente.setCoRequisito(rs.getString("CO_REQUISITO"));
				componente.setPreRequisito(rs.getString("PRE_REQUISITO"));
				componente.setEquivalencia(rs.getString("EQUIVALENCIA"));
				componente.setNome(rs.getString("NOME"));
				componente.setCodigo(rs.getString("COD_COMPONENTE"));
				
				sugestao.getTurma().setDisciplina(componente);

				if (!isEmpty(componente.getPreRequisito()))  {
					boolean possuiPreRequisito = ExpressaoUtil.evalComTransitividade(componente.getPreRequisito(), discente.getId(), componentesPagos);
					if (!possuiPreRequisito)
						sugestao.setTipoInvalido(SugestaoMatricula.PRE_REQUISITO);
				}

				if (sugestao.getTipoInvalido() == null && !isEmpty(componente.getEquivalencia())) {
					if (ExpressaoUtil.eval(componente.getEquivalencia(), componentesPagos) )
						sugestao.setTipoInvalido(SugestaoMatricula.EQUIVALENTE_PAGO);

					if (sugestao.getTipoInvalido() == null && componentesMatriculados.contains(componente.getId()) || ExpressaoUtil.eval(componente.getEquivalencia(), componentesMatriculados))
						sugestao.setTipoInvalido(SugestaoMatricula.JA_MATRICULADO);
				}

				String nomeDocente = rs.getString("NOME_DOCENTE");
				if (nomeDocente == null) {
					nomeDocente = rs.getString("NOME_DOCENTE_EXTERNO");
				}
				
				sugestao.addDocentesNomes(rs.getInt("ID_DOCENTE_TURMA"), nomeDocente );
				
				if (adicionar) {
					sugestaoEquivalente.adicionaSugestao(sugestao);
				}
			}
			
			return sugestoes;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}  finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

}