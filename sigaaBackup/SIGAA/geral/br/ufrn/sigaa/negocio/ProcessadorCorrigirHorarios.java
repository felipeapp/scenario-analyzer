package br.ufrn.sigaa.negocio;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;

public class ProcessadorCorrigirHorarios extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		final HorarioDao dao = getDAO(HorarioDao.class, mov);
		try {
			
			
			String sql = 	" select sol.horario as sol_horario, t.id_turma, cc.permite_horario_flexivel from graduacao.turma_solicitacao_turma tsol " +
			" join graduacao.solicitacao_turma sol on (tsol.id_solicitacao = sol.id_solicitacao_turma) " + 
			" join ensino.turma t on (t.id_turma = tsol.id_turma) " +
			" join ensino.componente_curricular cc on (cc.id_disciplina = t.id_disciplina) " +
			" where t.ano = 2010 and t.periodo = 2 and t.tipo = 1 and sol.id_curso = 10320810 and cc.permite_horario_flexivel = true " +
			" and t.id_turma not in (1155311,1155313)";
			
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List query = dao.getJdbcTemplate().query(sql, new RowMapper(){
				
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					Turma turma = null;
					try {
						
						String solHorario = rs.getString("sol_horario");
						int idTurma = rs.getInt("id_turma");
						boolean permiteHorarioFlexivel = rs.getBoolean("permite_horario_flexivel");
						
						removerHorarioAntigo(dao, idTurma);
						
						List<HorarioTurma> novoHorario = HorarioTurmaUtil.parseCodigoHorarios(solHorario, Unidade.UNIDADE_DIREITO_GLOBAL, NivelEnsino.GRADUACAO, dao);
						
						turma = criarPseudoTurma(idTurma, permiteHorarioFlexivel, novoHorario);
						criarNovoHorario(dao, novoHorario, turma);
						
						atualizarTurma(dao, turma);
						
					} catch (DAOException e) {
						e.printStackTrace();
					}
					return turma;
				}
				
				private void atualizarTurma(final HorarioDao dao, Turma turma)
				throws DAOException {
					String novoHorarioFormatado = HorarioTurmaUtil.formatarCodigoHorarios(turma);
					dao.updateField(Turma.class, turma.getId(), "descricaoHorario", novoHorarioFormatado);
				}
				
				private Turma criarPseudoTurma(int idTurma, boolean permiteHorarioFlexivel, List<HorarioTurma> novoHorario) {
					Turma turma = new Turma(idTurma);
					turma.setHorarios(novoHorario);
					turma.setDisciplina(new ComponenteCurricular());
					return turma;
				}
				
				private void criarNovoHorario(final HorarioDao dao, List<HorarioTurma> novoHorario, Turma turma) throws DAOException {
					for (HorarioTurma ht : novoHorario) {
						ht.setTurma(turma);
						dao.create(ht);
					}
				}
				
				private void removerHorarioAntigo(final HorarioDao dao, int idTurma)
				throws DAOException {
					Collection<HorarioTurma> antigoHorario = dao.findByExactField(HorarioTurma.class, "turma.id", idTurma);
					
					for (HorarioTurma ht : antigoHorario) {
						dao.remove(ht);
					}
				}
				
			});
			
			return query.size();
		} finally {
			dao.close();
		}
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
