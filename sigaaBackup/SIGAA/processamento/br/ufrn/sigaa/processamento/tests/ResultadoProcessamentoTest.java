/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 12/01/2008
 */
package br.ufrn.sigaa.processamento.tests;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;
import br.ufrn.sigaa.processamento.dominio.ResultadoProcessamento;
import br.ufrn.sigaa.processamento.dominio.TurmaProcessada;

/**
 * @author David Pereira
 *
 */
public class ResultadoProcessamentoTest extends TestCase {

	public void testCriar() throws Exception {
		
		ResultadoProcessamento.criar(getTurma(), false, "");
		
		File f = new File("/Users/david/Documents/workspace/SIGAA/app/sigaa.ear/sigaa.war/graduacao/matricula/processamento/resultado/20081/1234.html");
		assertTrue(f.exists());
	}
	
	
	private TurmaProcessada getTurma() {
		Turma turma = new Turma(1234);
		turma.setDisciplina(new ComponenteCurricular());
		turma.getDisciplina().setNome("Comp. Curricular de Teste");
		turma.setCodigo("01");
		turma.setDescricaoHorario("24M56");
		turma.setCapacidadeAluno(10);
		
		Set<MatriculaEmProcessamento> matriculas = new HashSet<MatriculaEmProcessamento>();
		MatriculaEmProcessamento m1 = new MatriculaEmProcessamento();
		m1.setCurso("Eng. Computacao");
		m1.setSituacao(SituacaoMatricula.MATRICULADO);
		m1.setIdMatrizCurricular(1);
		m1.setOrdem(1);
		m1.setNome("Fulano de Tal");
		m1.setMatricula(1234578);
		matriculas.add(m1);
		
		MatriculaEmProcessamento m2 = new MatriculaEmProcessamento();
		m2.setIdMatrizCurricular(3);
		m2.setCurso("Fisica");
		m2.setSituacao(SituacaoMatricula.MATRICULADO);
		m2.setOrdem(2);
		m2.setNome("Albert Einstein");
		m2.setMatricula(1234579);
		matriculas.add(m2);
		
		MatriculaEmProcessamento m3 = new MatriculaEmProcessamento();
		m3.setIdMatrizCurricular(2);
		m3.setCurso("Ciencia da Computacao");
		m3.setSituacao(SituacaoMatricula.INDEFERIDA);
		m3.setOrdem(2);
		m3.setNome("Joao da Silva");
		m3.setMatricula(1234580);
		matriculas.add(m3);
		
		MatriculaEmProcessamento m4 = new MatriculaEmProcessamento();
		m4.setIdMatrizCurricular(3);
		m4.setCurso("Fisica");
		m4.setSituacao(SituacaoMatricula.INDEFERIDA);
		m4.setOrdem(2);
		m4.setNome("Jose de Almeida");
		m4.setMatricula(1234581);
		matriculas.add(m4);
		
		Map<MatrizCurricular, ReservaCurso> mapa = new HashMap<MatrizCurricular, ReservaCurso>();
		ReservaCurso reserva1 = new ReservaCurso();
		reserva1.setMatrizCurricular(new MatrizCurricular(1));
		reserva1.getMatrizCurricular().setCurso(new Curso());
		reserva1.getMatrizCurricular().getCurso().setNome("Engenharia de Computação");
		reserva1.setVagasReservadas((short) 5);
		mapa.put(reserva1.getMatrizCurricular(), reserva1);
		
		ReservaCurso reserva2 = new ReservaCurso();
		reserva2.setMatrizCurricular(new MatrizCurricular(2));
		reserva2.getMatrizCurricular().setCurso(new Curso());
		reserva2.getMatrizCurricular().getCurso().setNome("Ciencia da Computação");
		reserva2.setVagasReservadas((short) 3);
		mapa.put(reserva2.getMatrizCurricular(), reserva2);
		
		TurmaProcessada turmaProcessada = new TurmaProcessada(turma, matriculas, mapa, null);
		return turmaProcessada;
	}
	
}
