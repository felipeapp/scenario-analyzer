package br.ufrn.sigaa.complexohospitalar.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Dao para realizar consultas específicas do processo de matrícula da residência
 * 
 * @author bernardo
 *
 */
public class MatriculaResidenciaDao extends GenericSigaaDAO {
	
	/** Define o limite de resultados da busca por turmas. */
	private static final int LIMITE_RESULTADOS_BUSCA_TURMA = 500;

	/**
	 * Busca por turmas abertas. Filtra a partir de diversos atributos dos
	 * componentes dessas turmas.
	 * Só procurar por turmas de componentes matriculáveis.
	 * Sobrecarga que utiliza o discente de graduação para retornar também as reservas
	 * de vagas nas turmas para para a matriz curricular do aluno. 
	 * BUSCA USADA PARA MATRÍCULA DE GRADUAÇÃO.
	 *
	 * @param nomeComponente
	 * @param codigo
	 * @param depto
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findAbertasByComponenteCurricular(DiscenteGraduacao discenteGrad, Collection<ComponenteCurricular> componentes, String nomeComponente, String codigo, Integer depto,
			Integer tipo, String nomeDocente, String horario, Curso curso, Polo polo,
			Integer ano, Integer periodo, Boolean matriculaveis, char nivel, boolean aDistancia, Integer... tiposTurma) throws DAOException, LimiteResultadosException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			StringBuffer restoSQL = new StringBuffer();

			restoSQL.append(" FROM ensino.componente_curricular cc " +
				" join ensino.componente_curricular_detalhes ccd on ( cc.id_detalhe = ccd.id_componente_detalhes ) " +
				" join comum.unidade as und on cc.id_unidade = und.id_unidade " +				
				" join ensino.turma t on ( cc.id_disciplina = t.id_disciplina ) " +
				" left join ensino.docente_turma dt on ( t.id_turma = dt.id_turma ) " +
				" left join rh.servidor s on (dt.id_docente = s.id_servidor) " +
				" left join comum.pessoa p on ( p.id_pessoa = s.id_pessoa ) " +
				" left join ead.polo polo on ( polo.id_polo = t.id_polo ) " +
				" left join comum.municipio municipio on (municipio.id_municipio = polo.id_cidade)");
			if(discenteGrad != null){
				restoSQL.append(" left join graduacao.reserva_curso rc on (rc.id_turma=t.id_turma and rc.id_matriz_curricular = "+ discenteGrad.getMatrizCurricular().getId() +")");
			}
			if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
				restoSQL.append( " join comum.unidade unidade on ( cc.id_unidade = unidade.id_unidade ) " );
			}

			restoSQL.append(" WHERE t.id_turma_bloco is null AND t.agrupadora = falseValue()" + 
					" AND t.id_situacao_turma in " + gerarStringIn(
							new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA}));

			if (!isEmpty(componentes))
				restoSQL.append(" and t.id_disciplina in " + gerarStringIn(componentes));
			if (NivelEnsino.isValido(nivel)){
				if( NivelEnsino.isAlgumNivelStricto(nivel) ){
					restoSQL.append(" and cc.nivel in " + gerarStringIn( NivelEnsino.getNiveisStricto() ));
				}else
					restoSQL.append(" and cc.nivel= '" + nivel+"'");
			}
			if (matriculaveis != null)
				restoSQL.append(" and cc.matriculavel= " + matriculaveis);

			// Turmas probásica
			if (curso != null && curso.getId() > 0)
				restoSQL.append(" and t.id_curso = ?");
			else {
				restoSQL.append(" and t.id_curso is null");
			}
			if (polo != null && polo.getId() > 0)
				restoSQL.append(" and t.id_polo = ?");
			if (depto!= null && depto > 0)
				restoSQL.append(" and cc.id_unidade = ?");
			if (tipo != null && tipo > 0)
				restoSQL.append(" and cc.id_tipo_componente = ?");
			if (!isEmpty(horario))
				restoSQL.append(" and t.descricao_horario like '"+horario+"%' ");
			if (codigo != null && codigo.length() > 0)
				restoSQL.append(" and ccd.codigo = ?");
			if (nomeDocente != null && nomeDocente.length() > 0)
				restoSQL.append(" AND p.nome_ascii ilike"+
						" translate('%"+UFRNUtils.trataAspasSimples(nomeDocente)+"%','áàâãäéèêëíìïóòôõöúùûüÁÀÂÃÄÉÈÊËÍÌÏÓÒÔÕÖÚÙÛÜçÇñÑ','aaaaaeeeeiiiooooouuuuAAAAAEEEEIIIOOOOOUUUUcCnN')");
			if (nomeComponente != null && nomeComponente.length() > 0)
				restoSQL.append(" AND ccd.nome_ascii ilike"+
						" translate('%"+UFRNUtils.trataAspasSimples(nomeComponente)+"%','áàâãäéèêëíìïóòôõöúùûüÁÀÂÃÄÉÈÊËÍÌÏÓÒÔÕÖÚÙÛÜçÇñÑ','aaaaaeeeeiiiooooouuuuAAAAAEEEEIIIOOOOOUUUUcCnN')");

			// Turmas de ensino a distância
			if (aDistancia) {
				restoSQL.append(" AND t.distancia = trueValue()");
			} else {
				restoSQL.append(" AND (t.distancia is null or t.distancia = falseValue())");
			}

			if( ano != null )
				restoSQL.append(" AND t.ano = " + ano);
			if( periodo != null )
				restoSQL.append(" AND t.periodo = " + periodo);
			if( tiposTurma != null )
				restoSQL.append(" AND t.tipo in " + UFRNUtils.gerarStringIn(tiposTurma) );

			st = con.prepareStatement("SELECT count(t.id_turma) as total " + restoSQL);
			setParametrosTurma(nomeComponente, codigo, tipo, depto, curso, polo, st, ano, periodo);
			rs = st.executeQuery();
			long total;
			if (rs.next()) {
				total = rs.getLong("TOTAL");
				if (total > LIMITE_RESULTADOS_BUSCA_TURMA)
					throw new LimiteResultadosException();
			}

			String countMatriculas = " , (select count(mc.id_matricula_componente) from ensino.matricula_componente as mc where mc.id_turma = t.id_turma " +
			"and mc.id_situacao_matricula in " + gerarStringIn( SituacaoMatricula.getSituacoesAtivas() ) + " ) as TOTAL_MATRICULADOS ";

			String projecao = "SELECT t.ano as ANO, t.periodo as PERIODO, t.distancia, t.id_turma, t.codigo as TURMA_CODIGO, t.local, t.id_situacao_turma, t.descricao_horario, cc.id_disciplina,  " +
			"cc.codigo as CC_CODIGO, cc.id_unidade as CC_ID_UNIDADE, und.id_gestora_academica as GESTORA_ACADEMICA, ccd.id_componente_detalhes as CC_ID_DETALHE, ccd.nome as CC_NOME, ccd.pre_requisito, ccd.co_requisito, ccd.equivalencia, " +
			"dt.id_docente, p.nome as DOCENTE_NOME, t.capacidade_aluno AS CAPACIDADE, t.tipo AS TIPO, polo.id_polo, polo.id_cidade, municipio.nome as nomeCidade";
			if(discenteGrad != null)
				projecao += ", rc.vagas_atendidas";

			String orderBy = " ORDER BY ccd.nome, t.id_disciplina, t.codigo asc";

			if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
				projecao += " , unidade.nome as UNIDADE_NOME ";
				orderBy = " ORDER BY cc.id_unidade, ccd.nome, t.id_disciplina, t.codigo asc";
			}

			System.out.println( projecao +
					countMatriculas +
					restoSQL +
					orderBy );

			st = con.prepareStatement( projecao +
					countMatriculas +
					restoSQL +
					orderBy);

			setParametrosTurma(nomeComponente, codigo, tipo, depto, curso, polo, st, ano, periodo);
			rs = st.executeQuery();

			ArrayList<Turma> turmas = new ArrayList<Turma>(0);
			int idAtual = 0;
			while (rs.next()) {
				Turma t = new Turma();
				t.setId(rs.getInt("ID_TURMA"));
				if (idAtual == t.getId() && idAtual > 0)
					t = turmas.get(turmas.size()-1);
				DocenteTurma dt = new DocenteTurma();
				dt.getDocente().setId(rs.getInt("ID_DOCENTE"));
				dt.getDocente().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				t.getDocentesTurmas().add(dt);
				if (idAtual == t.getId() && idAtual > 0)
					continue;
				t.setCodigo(rs.getString("TURMA_CODIGO"));
				t.setLocal(rs.getString("LOCAL"));
				if (discenteGrad != null && rs.getInt("vagas_atendidas") > 0) {
					t.setReservas(new ArrayList<ReservaCurso>());
					ReservaCurso rc = new ReservaCurso();
					rc.setVagasReservadas((short) rs.getInt("vagas_atendidas"));
					t.getReservas().add(rc);
				}
				t.setSituacaoTurma(new SituacaoTurma(rs.getInt("ID_SITUACAO_TURMA")));
				t.setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				t.setDisciplina(new ComponenteCurricular(rs.getInt("ID_DISCIPLINA")));
				t.getDisciplina().setCodigo(rs.getString("CC_CODIGO"));
				t.getDisciplina().setDetalhes(new ComponenteDetalhes(rs.getInt("CC_ID_DETALHE")));
				t.getDisciplina().getDetalhes().setPreRequisito(rs.getString("pre_requisito"));
				t.getDisciplina().getDetalhes().setCoRequisito(rs.getString("co_requisito"));
				t.getDisciplina().getDetalhes().setEquivalencia(rs.getString("equivalencia"));
				t.getDisciplina().setUnidade(new Unidade());
				t.getDisciplina().getUnidade().setId( rs.getInt("CC_ID_UNIDADE") );

				if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
					t.getDisciplina().getUnidade().setNome( rs.getString("UNIDADE_NOME") );
				}
				t.getDisciplina().getUnidade().setGestoraAcademica(new Unidade());
				t.getDisciplina().getUnidade().getGestoraAcademica().setId(rs.getInt("GESTORA_ACADEMICA"));

				t.getDisciplina().setNome(rs.getString("CC_NOME"));

				t.setCapacidadeAluno( rs.getInt("CAPACIDADE") );
				t.setTipo( rs.getInt("TIPO") );
				t.setQtdMatriculados( rs.getInt("TOTAL_MATRICULADOS") );
				t.setAno( Integer.parseInt( rs.getString("ANO") ) );
				t.setPeriodo( Integer.parseInt( rs.getString("PERIODO") ) );
				t.setDistancia(rs.getBoolean("distancia"));
				Integer idPolo = (Integer) rs.getObject("id_polo");

				if (idPolo != null) {
					t.setPolo(new Polo());
					t.getPolo().setId( idPolo );
					t.getPolo().setCidade(new Municipio());
					t.getPolo().getCidade().setId( Integer.parseInt( rs.getString("id_cidade") ));
					t.getPolo().getCidade().setNome( rs.getString("nomeCidade") );
				}

				turmas.add(t);
				idAtual = t.getId();
			}
			return turmas;
		} catch (LimiteResultadosException e) {
			throw new LimiteResultadosException (e);
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
	 * Seta os parâmetros informados no PreparedStatement passado
	 *
	 * @param nome
	 * @param codigo
	 * @param tipo
	 * @param depto
	 * @param curso
	 * @param polo
	 * @param st
	 * @param ano
	 * @param periodo
	 * @throws SQLException
	 */
	private void setParametrosTurma(String nome, String codigo, Integer tipo, Integer depto, Curso curso, Polo polo, PreparedStatement st, Integer ano, Integer periodo) throws SQLException {
		int i = 1;
		if (curso != null && curso.getId() > 0)
			st.setInt(i++, curso.getId());
		if (polo != null && polo.getId() > 0)
			st.setInt(i++, polo.getId());
		if (depto!= null && depto > 0)
			st.setInt(i++, depto);
		if (tipo!= null && tipo > 0)
			st.setInt(i++, tipo);
		if (codigo != null && codigo.length() > 0)
			st.setString(i++, codigo);
	}
}
