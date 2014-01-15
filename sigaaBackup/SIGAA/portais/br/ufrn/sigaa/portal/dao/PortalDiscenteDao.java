package br.ufrn.sigaa.portal.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO com as consultas utilizadas no Portal do Discente
 * 
 * @author wendell
 *
 */
public class PortalDiscenteDao extends GenericSigaaDAO {

	/**
	 * Retonar as turmas em que um discente está matriculado
	 * 
	 * @param discente
	 * @param calendario
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasMatriculado(DiscenteAdapter discente,	CalendarioAcademico calendario) throws DAOException {
		
		String sql = "select coalesce(t.id_turma_agrupadora, t.id_turma) as id_turma, t.local, t.descricao_horario, t.ano, t.periodo, ccd.nome, cc.codigo, " +
			" dt.id_docente_turma, (select u.login from comum.usuario where id_pessoa = p.id_pessoa order by data_cadastro desc limit 1) as login_docente, (select u.id_usuario from comum.usuario where id_pessoa = p.id_pessoa order by data_cadastro desc limit 1) as id_usuario_docente, " +
			" ht.id_horario_turma, ht.dia, ht.hora_inicio, ht.hora_fim, ht.data_inicio, ht.data_fim, h.id_horario, h.tipo, h.ordem " +
			" from ensino.turma t " +
			" join ensino.matricula_componente mc using(id_turma) " +
			" join ensino.componente_curricular cc using(id_disciplina) " +
			" join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes) " +
			" left join ensino.docente_turma dt on(t.id_turma = dt.id_turma) " +
			" left join rh.servidor s on (s.id_servidor = dt.id_docente) " +
			" left join ensino.docente_externo de on (dt.id_docente_externo = de.id_docente_externo) " +
			" left join comum.pessoa p on (s.id_pessoa = p.id_pessoa or de.id_pessoa = p.id_pessoa) " +
			" left join comum.usuario u on (p.id_pessoa = u.id_pessoa) " +
			" left join ensino.horario_turma ht on(t.id_turma = ht.id_turma) " +
			" left join ensino.horario h on(h.id_horario = ht.id_horario) " +
			" where mc.id_situacao_matricula = " + SituacaoMatricula.MATRICULADO.getId() + 
			" and ((mc.ano =  " + calendario.getAno() + " and mc.periodo = " + calendario.getPeriodo() + " ) " +
			" or (mc.ano =  " + calendario.getAnoFeriasVigente() + " and mc.periodo = " + calendario.getPeriodoFeriasVigente() + " )) " +
			" and mc.id_discente = " + discente.getId() + 
			" and t.id_situacao_turma in " + gerarStringIn(SituacaoTurma.getSituacoesValidas()) +
			" order by t.ano, t.periodo, ccd.nome, ht.dia, h.ordem";
		
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(sql.toString());
		Map<Integer, Turma> turmas = new LinkedHashMap<Integer,Turma>();
		
		for (Map<String, Object> mapa : lista) {
			Integer idTurma = (Integer) mapa.get("id_turma");
			Turma t = turmas.get( idTurma );
			if (t == null) {
				t = new Turma( idTurma );
				turmas.put(idTurma, t);
				
				t.setLocal( (String) mapa.get("local") );
				t.setDescricaoHorario( (String) mapa.get("descricao_horario") );
				t.setAno( (Integer) mapa.get("ano") );
				t.setPeriodo( (Integer) mapa.get("periodo") );
				
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setNome( (String) mapa.get("nome") );
				cc.setCodigo( (String) mapa.get("codigo") );
				t.setDisciplina(cc);
				
				t.setDocentesTurmas( new HashSet<DocenteTurma>() );
				t.setHorarios(new ArrayList<HorarioTurma>());
			}
			
			// Popular usuários dos docentes
			String loginDocente = (String) mapa.get("login_docente");
			if (loginDocente != null) {
				 DocenteTurma dt = new DocenteTurma((Integer) mapa.get("id_docente_turma"));
				 dt.setUsuario(new Usuario((Integer) mapa.get("id_usuario_docente")));
				 dt.getUsuario().setLogin(loginDocente);
				 t.getDocentesTurmas().add(dt);
			}
			
			// Popular horários da turma
			Integer idHorarioTurma = (Integer) mapa.get("id_horario_turma");
			if (idHorarioTurma != null) {
				HorarioTurma ht = new HorarioTurma(idHorarioTurma);
				Horario h = new Horario((Integer) mapa.get("id_horario"));
				h.setTipo( ((Integer) mapa.get("tipo")).shortValue() );
				h.setOrdem( ((Integer) mapa.get("ordem")).shortValue() );
				h.setInicio( (Date) mapa.get("hora_inicio") );
				h.setFim( (Date) mapa.get("hora_fim") );
				
				ht.setDia( ((String) mapa.get("dia")).charAt(0) );
				ht.setHoraInicio( (Date) mapa.get("hora_inicio") );
				ht.setHoraFim( (Date) mapa.get("hora_fim") );
				ht.setDataInicio( (Date) mapa.get("data_inicio") );
				ht.setDataFim( (Date) mapa.get("data_fim") );
				
				ht.setHorario(h);			
				t.getHorarios().add(ht);
			}
			
		}
		
		return turmas.values();
	}

}
