package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.ensino.tecnico.relatorios.LinhaAuxiliarTransfTurmaEntrada;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;

/**
 * Dao responsável pelas consultas realizadas na transferência de turmas e turma de entrada.
 * 
 * @author Jean Guerethes
 */
public class TransferenciaTurmaIMDDao extends GenericSigaaDAO {

	/**
	 * Responsável por montar os dados a serem exibidos para o usuário, tais como as turmas cadastradas do discente e as possíveis turmas de origem. 
	 */
	public Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> findTurmasMatriculadasByDiscente(Collection<DiscenteAdapter> discentes, int ano, int periodo, TurmaEntradaTecnico turmaEntrada) throws DAOException {
		Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> result = new HashMap<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>>();
		Collection<LinhaAuxiliarTransfTurmaEntrada> linhas;
		
		Collection<MatriculaComponente> col = new ArrayList<MatriculaComponente>();
		for (DiscenteAdapter  discenteAtual : discentes) {
			col = findMatriculadasByDiscenteAnoPeriodo(discenteAtual, ano, periodo);
			
			linhas = new ArrayList<LinhaAuxiliarTransfTurmaEntrada>();
			if ( !col.isEmpty() ) {
				for( MatriculaComponente tmd: col ){
		        		ArrayList<Turma> turmas = (ArrayList<Turma>) findByDisciplinaAnoPeriodo(tmd.getComponente(), ano, periodo, NivelEnsino.TECNICO, turmaEntrada.getEspecializacao().getId()); 
		        		turmas.iterator();
		        		linhas.add( LinhaAuxiliarTransfTurmaEntrada.montarLinhaAuxiliarTransfTurmaEntrada(tmd.getTurma(), turmas, (Discente) tmd.getDiscente()) );
		        }
				result.put(discenteAtual, linhas);
			} else {
				result.put(discenteAtual, new ArrayList<LinhaAuxiliarTransfTurmaEntrada>());
			}
		} 
        return result;
	}
	
	/**
	 * Responsável por carregar as turmas de uma determinada turma de entrada de acordo com ano e período.
	 * @param disciplina
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param especializacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDisciplinaAnoPeriodo(ComponenteCurricular disciplina, int ano, int periodo, char nivel, int especializacao) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("FROM Turma t WHERE ");
			hql.append("t.disciplina.nivel = '" + nivel + "' and ");
			hql.append("((t.situacaoTurma.id = " + SituacaoTurma.ABERTA + ") or ");
			hql.append("(t.situacaoTurma.id = " + SituacaoTurma.A_DEFINIR_DOCENTE +")) and ");
			hql.append("t.disciplina.id = " + disciplina.getId() + " and ");
			hql.append("t.ano = " + ano + " and ");
			hql.append("t.periodo = " + periodo + " and ");
			hql.append("t.especializacao.id = " + especializacao);
			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			Collection<Turma> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Carrega todas as turma que o discente está matriculado no ano e semestre.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculadasByDiscenteAnoPeriodo(DiscenteAdapter discente, Integer ano, Integer periodo) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "select mc.id_matricula_componente,mc.id_turma, sit.id_situacao_matricula as ID_SITUACAO, sit.descricao as SITUACAO, ccd.nome as COMPONENTE_NOME , " +
			"cc.codigo as COMPONENTE_CODIGO, cc.id_disciplina as id_componente, t.codigo as COD_TURMA, t.descricao_horario as DESCRICAO_HORARIO, t.local as LOCAL, " +
			"p1.nome as DOCENTE_NOME, dt.id_docente as ID_DOCENTE, ccd.ch_total as COMPONENTE_CH_TOTAL, " +
			"mc.ano as ANO, mc.periodo as PERIODO, t.id_turma_bloco, cc.id_tipo_componente, " +
			"dt.id_docente_externo as ID_DOCENTE_EXTERNO, p2.nome as DOCENTE_NOME_EXTERNO, t.data_inicio, t.data_fim, tcc.descricao as tipoComponenteCurricular, " +
			"serie.id_serie as ID_SERIE, serie.descricao as SERIE_DESCRICAO, serie.numero as SERIE_NUMERO, d.id_discente, p3.nome as discente " +
			"FROM discente d " +
			" join ensino.matricula_componente mc on (mc.id_discente=d.id_discente)" +
			" join ensino.componente_curricular cc on (mc.id_componente_curricular=cc.id_disciplina)" +
			" join ensino.tipo_componente_curricular tcc on ( cc.id_tipo_componente = tcc.id_tipo_disciplina )" + 
			" join ensino.situacao_matricula sit on (mc.id_situacao_matricula = sit.id_situacao_matricula)" +
			" join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe=ccd.id_componente_detalhes)" +
			" left join ensino.turma t on (mc.id_turma = t.id_turma)" +
			" left join ensino.docente_turma dt on (t.id_turma = dt.id_turma)" +
			" left join rh.servidor s on (dt.id_docente=s.id_servidor)" +
			" left join comum.pessoa p1 on (s.id_pessoa = p1.id_pessoa) " +
			" left join ensino.docente_externo de on (dt.id_docente_externo = de.id_docente_externo) " +
			" left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) " +
			" left join medio.serie serie on (serie.id_serie = mc.id_serie) " +
			" join comum.pessoa p3 on ( d.id_pessoa = p3.id_pessoa)" +  
			" WHERE d.id_discente = ? and (mc.id_situacao_matricula=? or mc.id_situacao_matricula=?) " +
			" and tcc.id_tipo_disciplina in " + UFRNUtils.gerarStringIn(new int[] {TipoComponenteCurricular.DISCIPLINA, 
					TipoComponenteCurricular.MODULO}) ;
			if( ano != null )
				sql += "AND mc.ano =? ";
			if( periodo != null )
				sql += "AND mc.periodo =? ";
			
			sql += "ORDER BY ccd.nome asc, mc.data_cadastro asc";

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);

			st.setInt(1, discente.getId());
			st.setInt(2, SituacaoMatricula.EM_ESPERA.getId());
			st.setInt(3, SituacaoMatricula.MATRICULADO.getId());
			if( ano != null )
				st.setInt(4, ano);
			if( periodo != null )
				st.setInt(5, periodo);

			rs = st.executeQuery();

			List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
			int idAtual = 0;
			while(rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				if (idAtual == mc.getId() && idAtual > 0)
					mc = matriculas.get(matriculas.size()-1);
				else
					mc.setTurma(new Turma());
				DocenteTurma dt = new DocenteTurma();
				if (rs.getInt("ID_DOCENTE") > 0) {
					dt.getDocente().setId(rs.getInt("ID_DOCENTE"));
					dt.getDocente().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				} else {
					dt.setDocenteExterno(new DocenteExterno(rs.getInt("ID_DOCENTE_EXTERNO")));
					dt.getDocenteExterno().getPessoa().setNome(rs.getString("DOCENTE_NOME_EXTERNO"));
				}
				mc.getTurma().getDocentesTurmas().add(dt);
				if (idAtual == mc.getId() && idAtual > 0)
					continue;

				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("ID_SITUACAO")));
				mc.getSituacaoMatricula().setDescricao(rs.getString("SITUACAO"));
				mc.setAno((short)rs.getInt("ANO"));
				mc.setPeriodo((byte)rs.getInt("PERIODO"));

				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setNome(rs.getString("COMPONENTE_NOME"));
				cc.getTipoComponente().setId(rs.getInt("id_tipo_componente"));
				cc.getTipoComponente().setDescricao(rs.getString("tipoComponenteCurricular"));
				cc.setCodigo(rs.getString("COMPONENTE_CODIGO"));
				cc.setChTotal(rs.getInt("COMPONENTE_CH_TOTAL"));
				cc.getDetalhes().setCodigo(rs.getString("COMPONENTE_CODIGO"));
				cc.setId(rs.getInt("ID_COMPONENTE"));
				mc.getTurma().setDisciplina(cc);
				mc.setComponente(cc);
				mc.setDetalhesComponente(cc.getDetalhes());

				mc.getTurma().setId(rs.getInt("ID_TURMA"));
				mc.getTurma().setCodigo(rs.getString("COD_TURMA"));
				mc.getTurma().setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				mc.getTurma().setLocal(rs.getString("LOCAL"));
				mc.getTurma().setDataInicio(rs.getDate("data_inicio"));
				mc.getTurma().setDataFim(rs.getDate("data_fim"));
				mc.getTurma().setDisciplina(cc);
				
				mc.setDiscente(new Discente());
				mc.getDiscente().setId(rs.getInt("ID_DISCENTE"));
				mc.getDiscente().getPessoa().setNome(rs.getString("DISCENTE"));
				
				if ( rs.getInt("ID_SERIE") > 0 ){
					mc.setSerie(new Serie());
					mc.getSerie().setId(rs.getInt("ID_SERIE"));
					mc.getSerie().setDescricao(rs.getString("SERIE_DESCRICAO"));
					mc.getSerie().setNumero(rs.getInt("SERIE_NUMERO"));
				}

				idAtual = mc.getId();
				matriculas.add(mc);
			}

			return matriculas;
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

}