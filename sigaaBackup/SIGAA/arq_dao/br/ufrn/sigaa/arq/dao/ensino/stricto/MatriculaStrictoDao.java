/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/06/2008
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * DAO utilizado fazer procedimentos para matrícula stricto 
 * 
 * @author Ricardo Wendell
 *
 */
public class MatriculaStrictoDao extends GenericSigaaDAO{

	/**
	 * Busca as sugestões de matrícula para um discente em um determinado ano/período.
	 * Adicionalmente podem ser verificadas as turmas em que o discente já encontra-se matriculado.
	 *
	 * @param discente
	 * @param turmasMatriculadas
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SugestaoMatricula> findSugestoesMatricula(DiscenteStricto discente,
			Collection<Turma> turmasMatriculadas, Integer ano, Integer periodo) throws DAOException {

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT t.id_turma,t.codigo as cod_turma, t.descricao_horario,t.local, t.id_situacao_turma, dt.id_docente_turma as id_docente, p.nome as nome_docente, "
					+ " c.id_disciplina, c.codigo as cod_componente, cd.nome, ac.denominacao as AREA_CONCENTRACAO, " +
							"cd.co_requisito, cd.pre_requisito, cd.equivalencia, cc.obrigatoria as OBRIGATORIA, t.observacao"
					+ " FROM ensino.turma t join ensino.componente_curricular c on (t.id_disciplina = c.id_disciplina)" +
						" join ensino.componente_curricular_detalhes cd on (c.id_detalhe = cd.id_componente_detalhes)" +
						" join comum.unidade u on (u.id_unidade = c.id_unidade) " +
						" join discente d on (d.id_gestora_academica = u.id_unidade) " +
						" join stricto_sensu.discente_stricto ds on (d.id_discente = ds.id_discente)" +
						" left join graduacao.curriculo_componente cc on (cc.id_componente_curricular = c.id_disciplina and cc.id_curriculo = d.id_curriculo)" +
						" left join stricto_sensu.area_concentracao ac on (cc.id_area_concentracao = ac.id_area_concentracao) " +
						" left join ensino.docente_turma dt on (t.id_turma=dt.id_turma) " +
						" left join rh.servidor s on (dt.id_docente=s.id_servidor) " +
						" left join comum.pessoa p on (s.id_pessoa=p.id_pessoa) " +
					" WHERE t.tipo=" + Turma.REGULAR +
					" AND t.id_situacao_turma in (1,2) " +
					" AND d.id_discente = ? " +
					" AND c.matriculavel = trueValue()  " +
					" AND t.ano = ? AND t.periodo = ? " +
					" AND c.nivel in " + UFRNUtils.gerarStringIn(NivelEnsino.getNiveisStricto()) +
					" AND c.id_disciplina not in " +
						" (select mat.id_componente_curricular from ensino.matricula_componente mat " +
						" where mat.id_situacao_matricula in  " + gerarStringIn(SituacaoMatricula.getSituacoesPagas()) +
						" AND mat.id_discente=d.id_discente )  " +
					" ORDER BY ac.denominacao, cc.obrigatoria, cd.nome, t.codigo");

			st = con.prepareStatement(sql.toString());

			// Setar parâmetros
			int i = 1;
			st.setInt(i++, discente.getId() );
			st.setInt(i++, ano);
			st.setInt(i++, periodo);

			rs = st.executeQuery();

			DiscenteDao ddao = new DiscenteDao();
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

				// Verificar se o resultSet ainda refere-se à turma anterior
				if (sugestao.getTurma().getId() != rs.getInt("ID_TURMA")) {
					sugestao = new SugestaoMatricula();
					adicionar = true;
				}

				// Definir dados da turma
				Turma turma = sugestao.getTurma();
				turma.setId(rs.getInt("ID_TURMA"));
				turma.setSituacaoTurma(new SituacaoTurma(rs.getInt("ID_SITUACAO_TURMA")));
				turma.setCodigo(rs.getString("COD_TURMA"));
				turma.setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				turma.setLocal(rs.getString("LOCAL"));
				turma.setObservacao(rs.getString("OBSERVACAO"));

				ComponenteCurricular componente = new ComponenteCurricular();
				componente.setId(rs.getInt("ID_DISCIPLINA"));
				componente.setCoRequisito(rs.getString("CO_REQUISITO"));
				componente.setPreRequisito(rs.getString("PRE_REQUISITO"));
				componente.setEquivalencia(rs.getString("EQUIVALENCIA"));
				componente.setNome(rs.getString("NOME"));
				componente.setCodigo(rs.getString("COD_COMPONENTE"));

				sugestao.setObrigatoria((Boolean) rs.getObject("OBRIGATORIA"));
				turma.setDisciplina(componente);

				String areaConcentracao = rs.getString("AREA_CONCENTRACAO");
				if  (areaConcentracao == null ) {
					// areaConcentracao = sugestao.isObrigatoria() == null ? "OUTRAS DISCIPLINAS DO PROGRAMA" : "DISCIPLINAS SEM ÁREA DE CONCENTRAÇÃO DEFINIDA" ;
					areaConcentracao = "OUTRAS DISCIPLINAS DO PROGRAMA";
				}
				sugestao.setNivel( areaConcentracao );

				// Verificar pré-requisitos pagos
				if (!isEmpty(componente.getPreRequisito()))  {
					boolean possuiPreRequisito = ExpressaoUtil.evalComTransitividade(componente.getPreRequisito(), discente.getId(), componentesPagos);
					if (!possuiPreRequisito) {
						sugestao.setTipoInvalido(SugestaoMatricula.PRE_REQUISITO);
					}
				}

				// Validar equivalências e componentes já matriculados
				if (sugestao.getTipoInvalido() == null && !isEmpty(componente.getEquivalencia())) {
					if (ExpressaoUtil.eval(componente.getEquivalencia(), componentesPagos) )
						sugestao.setTipoInvalido(SugestaoMatricula.EQUIVALENTE_PAGO);
				}

				if (sugestao.getTipoInvalido() == null && (componentesMatriculados.contains(componente.getId())
						|| ExpressaoUtil.eval(componente.getEquivalencia(), componentesMatriculados)) )
					sugestao.setTipoInvalido(SugestaoMatricula.JA_MATRICULADO);

				sugestao.addDocentesNomes(rs.getInt("ID_DOCENTE"), rs.getString("NOME_DOCENTE"));

				if (adicionar) {
					sugestoes.add(sugestao);
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

	/**
	 * Procura programas em período de matrícula
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Unidade> findProgramasEmPeriodoDeMatricula() throws DAOException {
		return findOutrosProgramasEmPeriodoDeMatricula(null);
	}
	
	/**
	 * Procura programas em período de matrícula com exceção do passado como argumento
	 * 
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Unidade> findOutrosProgramasEmPeriodoDeMatricula(Unidade unidade) throws DAOException {
		
		String projecao = " u.id, u.nome ";
		
		StringBuilder hql = new StringBuilder();
			hql.append("select distinct " + HibernateUtils.removeAliasFromProjecao(projecao) + " " +
						"	from CalendarioAcademico ca " +
						"	join ca.unidade u " +
						"	where  (" +
						"			 (current_date() >= ca.inicioMatriculaOnline and current_date() <= ca.fimMatriculaOnline) " +
						"	   		 or" +
						"			 (current_date() >= ca.inicioReMatricula     and current_date() <= ca.fimReMatricula) " +
						"			)" +
						"		and ca.vigente = true " +
						"       and ca.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto()));
		
		if(unidade != null)
			hql.append("		and u.id != " + unidade.getId()); 
		
		hql.append(" order by u.nome ");
		
		Query query = getSession().createQuery(hql.toString());
		
		return (List<Unidade>) HibernateUtils.parseTo(query.list(), projecao, Unidade.class, "u");
	}	
	
}
