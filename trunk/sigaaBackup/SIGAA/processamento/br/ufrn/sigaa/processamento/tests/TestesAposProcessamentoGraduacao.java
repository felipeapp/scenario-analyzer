/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 09/01/2009 
 */
package br.ufrn.sigaa.processamento.tests;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;

/**
 * Testes para os DAOs de processamento de matrículas
 * 
 * @author David Pereira
 *
 */
public class TestesAposProcessamentoGraduacao extends TestCase {

	Logger log = Logger.getLogger(TestesAposProcessamentoGraduacao.class);
	
	private int ano;
	
	private int periodo;
	
	private boolean rematricula;
	
	private BasicDataSource ds;
	
	private JdbcTemplate template;
	
	@Override
	protected void setUp() throws Exception {
		ano = 2010;
		periodo = 1;
		rematricula = false;
		ds = new BasicDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		ds.setUrl("jdbc:postgresql://desenvolvimento.info.ufrn.br:5432/sigaa");
		ds.setUsername("sigaa");
		ds.setPassword("sigaa");
		ds.setInitialSize(10);
		template = new JdbcTemplate(ds);
		
		Database.setDirectMode();
	}
	
	public void testNaoExistemTurmasSemProcessar() {
		ProcessamentoMatriculaGraduacaoDao dao = new ProcessamentoMatriculaGraduacaoDao();
		dao.addJdbcTemplate(ds, template);
		List<Turma> turmasProcessar = dao.findTurmasProcessar(ano, periodo, rematricula);
		assertTrue(turmasProcessar.isEmpty());
	}
	
	public void testNaoExistemMatriculasEspera() {
		int count = template.queryForInt("select count(*) from ensino.matricula_componente mc, discente d, ensino.turma t "
				+ "where mc.id_discente = d.id_discente and mc.id_situacao_matricula = 1 and mc.ano = ? and mc.periodo = ? "
				+ "and d.nivel = 'G' and d.status in (1,2,8) and mc.id_turma = t.id_turma and t.id_situacao_turma in (1,2) and t.tipo = 1", new Object[] { ano, periodo });
		assertTrue(count == 0);
	}
	
	public void testMatriculasNaoPassaramCapacidadeTurma() {
		String sql = "select count(*) from ensino.matricula_componente mc, ensino.turma t where mc.id_turma = t.id_turma and t.id_polo is null and mc.id_situacao_matricula = 2 and mc.id_turma = ?";
		List<Turma> turmas = getTurmas();
		int qtd = turmas.size();
		int atual = 1;
		for (Turma t : turmas) {
			int capacidade = t.getCapacidadeAluno();
			int total = template.queryForInt(sql, t.getId());
			log.info("Verificando turma " + t.getId()+ ": " + total + "/" + capacidade + " ["+(atual++) + "/" + qtd + "]");
			assertTrue(capacidade >= total);
		}
	}
	
	private List<Turma> getTurmas() {
		ProcessamentoMatriculaGraduacaoDao dao = new ProcessamentoMatriculaGraduacaoDao();
		dao.addJdbcTemplate(ds, template);
		return dao.findTurmasProcessadas(ano, periodo, rematricula);
	}
	
}
