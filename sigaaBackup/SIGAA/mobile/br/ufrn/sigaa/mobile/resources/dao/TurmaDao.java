package br.ufrn.sigaa.mobile.resources.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

public class TurmaDao extends GenericSigaaDAO {

	/**
	 * Busca as turmas que o discente se encontra matriculado.
	 * 
	 * @param discente
	 * @param calendario
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasMatriculadoByDiscente(DiscenteAdapter discente, CalendarioAcademico calendario) throws DAOException {
		String sql = "select coalesce(t.id_turma_agrupadora, t.id_turma) as id_turma, t.id_curso, t.local, t.descricao_horario, t.ano, t.periodo, t.codigo, ccd.nome, cc.codigo as codigoCC, cc.nivel, ccd.ch_total," +
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
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(sql.toString());
		
		Map<Integer, Turma> turmas = new LinkedHashMap<Integer,Turma>();
		
		for (Map<String, Object> mapa : lista) {
			Integer idTurma = (Integer) mapa.get("id_turma");
			Turma t = turmas.get( idTurma );
			if (t == null) {
				t = new Turma( idTurma );
				turmas.put(idTurma, t);
				
				Curso curso = new Curso();
				if (mapa.get("id_curso") != null)	
					curso.setId( (Integer) mapa.get("id_curso") );
				
				t.setLocal( (String) mapa.get("local") );
				t.setDescricaoHorario( (String) mapa.get("descricao_horario") );
				t.setAno( (Integer) mapa.get("ano") );
				t.setPeriodo( (Integer) mapa.get("periodo") );
				t.setCodigo( (String) mapa.get("codigo") );
				t.setCurso(curso);
				
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setNome( (String) mapa.get("nome") );
				cc.setCodigo( (String) mapa.get("codigoCC") );
				char nivel = ((String) mapa.get("nivel")).charAt(0);
				cc.setNivel(nivel);
				t.setDisciplina(cc);
				
				t.setDocentesTurmas( new HashSet<DocenteTurma>() );
				t.setHorarios(new ArrayList<HorarioTurma>());
				
				if (mapa.get("ch_total") != null)	
					t.getDisciplina().setChTotal((Integer) mapa.get("ch_total"));
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
	
	/**
	 * Busca as turmas de um docente de acordo com os parâmetros informados.
	 * 
	 * @param discente
	 * @param calendario
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasByDocente(Servidor docente, Character nivel, boolean fetchQtdAlunos, Integer ... situacoes) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from DocenteTurma dt" +
					" left join dt.turma t" +
					" left join dt.turma.turmaAgrupadora ta" +
					" left join t.polo p" +
					" left join p.cidade c" +
					" left join c.unidadeFederativa uf" +
					" left join t.turmaAgrupadora ta" +
					" left join ta.situacaoTurma sit" +
					" left join ta.disciplina disc" +
					" left join disc.unidade unidAgr" +
					" left join unidAgr.gestoraAcademica gestAgr" +
					" where 1=1 ");

			if(docente != null)
				hql.append(" and dt.docente.id = "+docente.getId());

			if ( !isEmpty( situacoes ) ) {
				hql.append(" and dt.turma.situacaoTurma.id = " + UFRNUtils.gerarStringIn(situacoes));
			} else {
				hql.append(" and dt.turma.situacaoTurma.id in "
						+ UFRNUtils.gerarStringIn(new int[] {SituacaoTurma.ABERTA, SituacaoTurma.CONSOLIDADA}));
			}

			if (!ValidatorUtil.isEmpty(nivel))
				hql.append(" and dt.turma.disciplina.nivel = "+nivel+" ");
			
			hql.append(" order by dt.turma.ano desc, dt.turma.periodo desc, dt.turma.disciplina.nivel,dt.turma.disciplina.detalhes.nome, dt.turma.codigo asc");
			
			String projecao = "dt.turma.id as turma.id," +
					" dt.turma.curso.id as turma.curso.id," +
					" dt.turma.disciplina.id as turma.disciplina.id," +
					" dt.turma.disciplina.codigo as turma.disciplina.codigo," +
					" dt.turma.disciplina.detalhes.nome as turma.disciplina.detalhes.nome," +
					" dt.turma.disciplina.detalhes.chTotal as turma.disciplina.detalhes.chTotal," +
					" dt.turma.disciplina.detalhes.crAula as turma.disciplina.detalhes.crAula," +
					" dt.turma.disciplina.detalhes.crLaboratorio as turma.disciplina.detalhes.crLaboratorio," +
					" dt.turma.disciplina.detalhes.crEstagio as turma.disciplina.detalhes.crEstagio," +
					" dt.turma.disciplina.nivel as turma.disciplina.nivel," +
					" dt.turma.disciplina.unidade.id as turma.disciplina.unidade.id," +
					" dt.turma.disciplina.unidade.gestoraAcademica.id as turma.disciplina.unidade.gestoraAcademica.id," +
					" dt.turma.ano as turma.ano," +
					" dt.turma.periodo as turma.periodo, " +
					" dt.turma.distancia as turma.distancia, " +
					" dt.turma.descricaoHorario as turma.descricaoHorario," +
					" dt.turma.local as turma.local," +
					" dt.turma.codigo as turma.codigo," +
					" dt.turma.situacaoTurma.id as turma.situacaoTurma.id," +
					" dt.turma.situacaoTurma.descricao as turma.situacaoTurma.descricao," +
					" p.id as dt.turma.polo.id," +
					" c.nome as dt.turma.polo.cidade.nome," +
					" uf.id as dt.turma.polo.cidade.unidadeFederativa.id," +
					" uf.sigla as dt.turma.polo.cidade.unidadeFederativa.sigla," +
					" dt.chDedicadaPeriodo," +
					" t.capacidadeAluno as dt.turma.capacidadeAluno," +
					" ta.id as dt.turma.turmaAgrupadora.id," +
					" ta.codigo as dt.turma.turmaAgrupadora.codigo," +
					" ta.ano as dt.turma.turmaAgrupadora.ano," +
					" ta.periodo as dt.turma.turmaAgrupadora.periodo," +
					" sit.id as dt.turma.turmaAgrupadora.situacaoTurma.id," +
					" sit.descricao as dt.turma.turmaAgrupadora.situacaoTurma.descricao," +
					" disc as dt.turma.turmaAgrupadora.disciplina," +
					" unidAgr.id as dt.turma.turmaAgrupadora.disciplina.unidade.id,"+
					" gestAgr.id as dt.turma.turmaAgrupadora.disciplina.unidade.gestoraAcademica.id";
			
			hql.insert(0, " ").insert(0, HibernateUtils.removeAliasFromProjecao(projecao)).insert(0, "select ");
			
			Query q = getSession().createQuery(hql.toString());

			Collection<Turma> turmas = null;
			
			@SuppressWarnings("unchecked")
			Collection<DocenteTurma> lista = HibernateUtils.parseTo(q.list(), projecao, DocenteTurma.class, "dt");
			if (!isEmpty(lista)) {
				Map<Integer, Turma> mapaTurmas = new HashMap<Integer, Turma>();
				for (DocenteTurma dt : lista) {
					dt.getTurma().addDocenteTurma(dt);
					mapaTurmas.put(dt.getTurma().getId(), dt.getTurma());
				}
				turmas = mapaTurmas.values();
			}
			
			if (fetchQtdAlunos && turmas != null && turmas.size() > 0) {
				Collection<Integer> idTurmas = new ArrayList<Integer>();
				for (Turma turma : turmas) {
					idTurmas.add(turma.getId());
				}
				Query qtd = getSession().createQuery(
						"select turma.id, situacaoMatricula.id, count(*)"
							+ " from MatriculaComponente"
							+ " where turma.id in " + UFRNUtils.gerarStringIn(idTurmas)
							+ " group by turma.id, situacaoMatricula.id");
				@SuppressWarnings("unchecked")
				List<Object[]> resultSet = qtd.list();
				for (Object[] row : resultSet) {
					for (Turma turma : turmas) {
						if (turma.getId() == ((Integer)row[0])) {
							int sit = (Integer) row[1];
							int total = ((Long) row[2]).intValue();
							if (sit == SituacaoMatricula.MATRICULADO.getId())
								turma.setQtdMatriculados(total);
							else if (sit == SituacaoMatricula.APROVADO.getId())
								turma.setQtdAprovados(total);
							else if (sit == SituacaoMatricula.REPROVADO.getId())
								turma.setQtdReprovados(total);
							else if (sit == SituacaoMatricula.REPROVADO_FALTA.getId())
								turma.setQtdReprovadosFalta(total);
							else if (sit == SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId())
								turma.setQtdReprovadosMediaFalta(total); 
							break;
						}
					}
				}
			}
			return turmas;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca as frequências do aluno em uma turma, em uma determinada data
	 *
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public List<FrequenciaAluno> findFrequenciasByTurma(Turma turma, Date data) {
		String sql = "select f.id as id_frequencia, f.faltas, f.data, f.turma.id as id_turma, d.id as id_discente, d.matricula, p.id as id_pessoa, p.nome, f.tipoCaptcaoFrequencia "
				+ "from FrequenciaAluno f left outer join f.discente d left outer join d.pessoa p "
				+ "where f.turma.id = ? and f.data = to_date(?,'dd/MM/yyyy') and f.ativo = trueValue() order by p.nome asc";
		@SuppressWarnings("unchecked")
		List<Object[]> listaConsulta = getHibernateTemplate().find(sql,	new Object[] { turma.getId(), Formatador.getInstance().formatarData(data) });
		List<FrequenciaAluno> lista = new ArrayList<FrequenciaAluno>();
		FrequenciaAluno freq;
		for (Object[] mapa : listaConsulta) {
			freq = new FrequenciaAluno((Integer) mapa[0], 
										 (Short) mapa[1],
										  (Date) mapa[2],
									   (Integer) mapa[3] , 
									      (Long) mapa[5], 
									   (Integer) mapa[6], 
									    (String) mapa[7], 
									    (Character) mapa[8]);
			freq.getDiscente().setId((Integer) mapa[4]);
			lista.add(freq);
		}
		return lista;
	}
}
