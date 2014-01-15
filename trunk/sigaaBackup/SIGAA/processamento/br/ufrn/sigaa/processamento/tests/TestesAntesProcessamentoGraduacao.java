/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 09/01/2009 
 */
package br.ufrn.sigaa.processamento.tests;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;

/**
 * Testes para os DAOs de processamento de matrículas
 * 
 * @author David Pereira
 *
 */
public class TestesAntesProcessamentoGraduacao extends TestCase {

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
	
	public void testExistemSolicitacoesPendentesNaRematricula() {
		if (rematricula) {
			ProcessamentoMatriculaGraduacaoDao dao = new ProcessamentoMatriculaGraduacaoDao();
			List<SolicitacaoMatricula> solicitacoes = dao.findSolicitacoesMatricula(ano, periodo, false);
			assertTrue(isEmpty(solicitacoes));
		}
	}
	
	public void testQtdTurmasAProcessarGraduacao() {
		ProcessamentoMatriculaGraduacaoDao dao = new ProcessamentoMatriculaGraduacaoDao();

		int qtd = dao.findCountTurmasProcessar(ano, periodo, rematricula);
		List<Turma> turmas = dao.findTurmasProcessar(ano, periodo, rematricula);
		
		assertTrue(qtd > 0);
		assertNotNull(turmas);
		assertEquals(qtd, turmas.size());
	}
	
	public void testQtdAlunosTurmaGraduacao() {
		
		ProcessamentoMatriculaGraduacaoDao dao = new ProcessamentoMatriculaGraduacaoDao();
		dao.addJdbcTemplate(ds, template);
		List<Turma> turmas = dao.findTurmasProcessar(ano, periodo, rematricula);
		int qtd = turmas.size();
		int atual = 1;
		for (Turma turma : turmas) {
			String sql = dao.getSqlMatriculasTurma(turma, rematricula);
			List<?> lista = template.queryForList(sql);
			int count = template.queryForInt("select count(*) from ensino.matricula_componente mc, discente d where mc.id_discente = d.id_discente and mc.id_turma = ? and mc.id_situacao_matricula = 1 and d.status in (1,2,8)", turma.getId());
			System.out.println("Verificando turma " + turma .getId()+ ": " + count + "/" + lista.size() + " ["+(atual++) + "/" + qtd + "]");
			assertEquals(count, lista.size());
		}
		
	}
	
}
