/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/01/2012
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.RestricaoDiscenteMatricula;
import br.ufrn.sigaa.ensino.dominio.SequenciaMatriculaTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;

/**
 * DAO com consultas para a matrícula de formação complementar. 
 * 
 * @author Leonardo Campos
 *
 */
public class MatriculaFormacaoComplementarDao extends GenericSigaaDAO {

	/**
	 * Retorna a sequência que será utilizada para a matrícula de formação complementar.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public SequenciaMatriculaTurma getSequenciaMatriculaTurma(Turma turma) throws DAOException {
		Criteria c = getSession().createCriteria(SequenciaMatriculaTurma.class);
		c.add(Restrictions.eq("turma.id", turma.getId()));
		SequenciaMatriculaTurma sequencia = (SequenciaMatriculaTurma) c.setLockMode(LockMode.UPGRADE).uniqueResult();
		if (sequencia == null) {
			sequencia = new SequenciaMatriculaTurma();
			sequencia.setTurma(turma);
		}
		return sequencia;
	}
	
	/**
	 * Verifica se o aluno é veterano, checando se o mesmo cursou pelo menos um componente curricular,
	 * seja com aprovação, reprovação ou aproveitamento.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public boolean isAlunoVeterano(DiscenteAdapter discente) throws DAOException {
		String hql = "select count(*) from MatriculaComponente m " +
				" where m.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesConcluidas()) +
				" and m.discente.id = " + discente.getId();
		
		Long count = ((Long) getSession().createQuery(hql).uniqueResult());
		return count != null && count > 0;
	}
	
	/**
	 * Verifica se o aluno é bolsista, checando se há uma associação entre o discente e um tipo de bolsa
	 * de formação complementar.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public boolean isAlunoBolsista(DiscenteAdapter discente) throws DAOException {
		String hql = "select count(*) from BolsaDiscenteFormacaoComplementar b " +
				" where b.ativo = trueValue()" +
				" and b.discente.id = " + discente.getId();
				
		Long count = ((Long) getSession().createQuery(hql).uniqueResult());
		return count != null && count > 0;
	}
	
	/**
	 * Busca as sugestões de matrícula para o discente de formação complementar para o ano-período informado.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SugestaoMatricula> findSugestoesMatricula(DiscenteAdapter discente, Integer ano, Integer periodo) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		DiscenteDao ddao = new DiscenteDao();
		try {
			con = Database.getInstance().getSigaaConnection();

			String turmasDeModulo = "SELECT distinct  t.id_turma,mod.descricao as MODULO_DESCRICAO  ,t.codigo as cod_turma, " +
					" t.descricao_horario,t.local, t.observacao,  t.id_situacao_turma, dt.id_docente_turma as id_docente, " +
					" p.nome as nome_docente, p2.nome as nome_docente_externo, c.id_disciplina, c.codigo as cod_componente, " +
					" cd.nome, cd.co_requisito, cd.pre_requisito, cd.equivalencia, esp.descricao, t.id_especializacao_turma_entrada, " +
					" c.matriculavel, c.qtd_max_matriculas" +
					"  FROM tecnico.discente_tecnico disc join tecnico.turma_entrada_tecnico te on (disc.id_turma_entrada = te.id_turma_entrada)   " +
					"  join tecnico.estrutura_curricular_tecnica ec on (te.id_estrutura_curricular = ec.id_estrutura_curricular)   " +
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

			ddao.setSession(getSession());
			Collection<ComponenteCurricular> componentesPagos = ddao.findComponentesCurricularesConcluidos(discente.getDiscente());
			Collection<ComponenteCurricular> componentesMatriculados = ddao.findComponentesCurriculares(discente.getDiscente(), SituacaoMatricula.getSituacoesMatriculadas(), null);

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

				}

				if (sugestao.getTipoInvalido() == null
						&& componente.getQtdMaximaMatriculas() == 1
						&& (componentesMatriculados.contains(componente) 
								|| (!isEmpty(componente.getEquivalencia()) 
										&& ExpressaoUtil.eval(
												componente.getEquivalencia(),
												componentesMatriculados))))
					sugestao.setTipoInvalido(SugestaoMatricula.JA_MATRICULADO);
				
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
			ddao.close();
			Database.getInstance().close(con);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Collection<RestricaoDiscenteMatricula> findRestricoesDiscenteMatricula(Unidade unidade, char nivel) throws DAOException {
		return getSession().createCriteria(RestricaoDiscenteMatricula.class)
				.add(Restrictions.eq("unidade.id", unidade.getId()))
				.add(Restrictions.eq("nivel", nivel)).list();
	}
	
	/**
	 * Verifica se o discente possui bolsa auxílio.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public boolean possuiBolsaAuxilio(DiscenteAdapter discente, Integer ano, Integer periodo) throws DAOException{
		
		String sql = " SELECT CAST(COUNT(DISTINCT p.id_pessoa) AS INTEGER) FROM discente d "
					+ " INNER JOIN comum.pessoa p ON(d.id_pessoa = p.id_pessoa) "
					+ " INNER JOIN sae.bolsa_auxilio ba ON(ba.id_discente = d.id_discente) "
					+ " LEFT JOIN sae.bolsa_auxilio_periodo bap ON(bap.id_bolsa_auxilio = ba.id_bolsa_auxilio) "
					+ " WHERE d.status IN " + gerarStringIn( StatusDiscente.getAtivos() )  
					+ " AND ( "
					+ " 		( "
					+ "				( "
					+ "					( ba.id_tipo_bolsa_auxilio IN " + gerarStringIn( TipoBolsaAuxilio.getTiposResidencia() ) 
										+ " AND ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA + " ) "
					+ "					OR "
					+ "					( ba.id_tipo_bolsa_auxilio = " + TipoBolsaAuxilio.ALIMENTACAO 
										+ " AND ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA +" ) "
					+ "				)"
					+ "				AND bap.ano = " + ano + " AND bap.periodo = " + periodo
					+ "			)"
					+ "			OR ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA 
					+ " 	) "
					+ " AND p.id_pessoa = " + discente.getPessoa().getId();
		
	    Query q = getSession().createSQLQuery(sql);
	    Integer qtd = (Integer) q.uniqueResult();
	    return qtd > 0;
	}
}
