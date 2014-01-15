/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 07/01/2008
 */
package br.ufrn.sigaa.processamento.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;
import br.ufrn.sigaa.processamento.dominio.ProcessamentoMatriculasTurma;

/**
 * Testes unitários para a classe ProcessamentoMatriculasTurma
 * 
 * @author David Pereira
 *
 */
public class ProcessamentoMatriculasTurmaTest extends TestCase {

	public void testSemMatriculas() {
		ProcessamentoMatriculasTurma processamento = ProcessamentoMatriculasTurma.criar(getTurma(), getVagasSemReserva(), getMatriculasVazias());
		List<MatriculaEmProcessamento> resultado = processamento.processar();
		
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());
	}
	
	public void testSemReservas() {
		ProcessamentoMatriculasTurma processamento = ProcessamentoMatriculasTurma.criar(getTurma(), getVagasSemReserva(), getMatriculasSemReserva());
		MatriculaEmProcessamento[] resultado = processamento.processar().toArray(new MatriculaEmProcessamento[10]);
		
		assertNotNull(resultado);
		assertFalse(resultado.length == 0);
		
		assertEquals(SituacaoMatricula.MATRICULADO, resultado[0].getSituacao());
		assertEquals(MatriculaEmProcessamento.VESTIBULAR, resultado[0].getTipo());
		assertEquals(10.0, resultado[0].getIndice());
		assertEquals(1, resultado[0].getOrdem());
		
		assertEquals(SituacaoMatricula.MATRICULADO, resultado[1].getSituacao());
		assertEquals(MatriculaEmProcessamento.NIVELADO, resultado[1].getTipo());
		assertEquals(9.0, resultado[1].getIndice());
		assertEquals(2, resultado[1].getOrdem());
		
		assertEquals(SituacaoMatricula.MATRICULADO, resultado[2].getSituacao());
		assertEquals(MatriculaEmProcessamento.NIVELADO, resultado[2].getTipo());
		assertEquals(8.0, resultado[2].getIndice());
		assertEquals(3, resultado[2].getOrdem());
		
		assertEquals(SituacaoMatricula.MATRICULADO, resultado[3].getSituacao());
		assertEquals(MatriculaEmProcessamento.FORMANDO, resultado[3].getTipo());
		assertEquals(9.0, resultado[3].getIndice());
		assertEquals(4, resultado[3].getOrdem());

		assertEquals(SituacaoMatricula.MATRICULADO, resultado[4].getSituacao());
		assertEquals(MatriculaEmProcessamento.FORMANDO, resultado[4].getTipo());
		assertEquals(7.0, resultado[4].getIndice());
		assertEquals(5, resultado[4].getOrdem());

		assertEquals(SituacaoMatricula.MATRICULADO, resultado[5].getSituacao());
		assertEquals(MatriculaEmProcessamento.RECUPERACAO, resultado[5].getTipo());
		assertEquals(8.0, resultado[5].getIndice());
		assertEquals(6, resultado[5].getOrdem());

		assertEquals(SituacaoMatricula.MATRICULADO, resultado[6].getSituacao());
		assertEquals(MatriculaEmProcessamento.RECUPERACAO, resultado[6].getTipo());
		assertEquals(7.0, resultado[6].getIndice());
		assertEquals(7, resultado[6].getOrdem());

		assertEquals(SituacaoMatricula.MATRICULADO, resultado[7].getSituacao());
		assertEquals(MatriculaEmProcessamento.ADIANTADO, resultado[7].getTipo());
		assertEquals(8.0, resultado[7].getIndice());
		assertEquals(8, resultado[7].getOrdem());

		assertEquals(SituacaoMatricula.INDEFERIDA, resultado[8].getSituacao());
		assertEquals(MatriculaEmProcessamento.ELETIVO, resultado[8].getTipo());
		assertEquals(8.0, resultado[8].getIndice());
		assertEquals(9, resultado[8].getOrdem());

		assertEquals(SituacaoMatricula.INDEFERIDA, resultado[9].getSituacao());
		assertEquals(MatriculaEmProcessamento.OUTROS, resultado[9].getTipo());
		assertEquals(8.0, resultado[9].getIndice());
		assertEquals(10, resultado[9].getOrdem());

	}
	
	public void testComReservas() {
		ProcessamentoMatriculasTurma processamento = ProcessamentoMatriculasTurma.criar(getTurma(), getVagasComReserva(), getMatriculasComReserva());
		List<MatriculaEmProcessamento> resultadoSet = processamento.processar();
		MatriculaEmProcessamento[] resultado = resultadoSet.toArray(new MatriculaEmProcessamento[10]);
		
		assertNotNull(resultado);
		assertFalse(resultado.length == 0);
		
		assertEquals(SituacaoMatricula.MATRICULADO, resultado[0].getSituacao());
		assertEquals(MatriculaEmProcessamento.VESTIBULAR, resultado[0].getTipo());
		assertEquals(10.0, resultado[0].getIndice());
		assertEquals(1, resultado[0].getOrdem());
		
		assertEquals(SituacaoMatricula.MATRICULADO, resultado[1].getSituacao());
		assertEquals(MatriculaEmProcessamento.NIVELADO, resultado[1].getTipo());
		assertEquals(9.0, resultado[1].getIndice());
		assertEquals(1, resultado[1].getOrdem());
		
		assertEquals(SituacaoMatricula.MATRICULADO, resultado[2].getSituacao());
		assertEquals(MatriculaEmProcessamento.NIVELADO, resultado[2].getTipo());
		assertEquals(8.0, resultado[2].getIndice());
		assertEquals(1, resultado[2].getOrdem());
		
		assertEquals(SituacaoMatricula.MATRICULADO, resultado[3].getSituacao());
		assertEquals(MatriculaEmProcessamento.FORMANDO, resultado[3].getTipo());
		assertEquals(9.0, resultado[3].getIndice());
		assertEquals(2, resultado[3].getOrdem());

		assertEquals(SituacaoMatricula.MATRICULADO, resultado[4].getSituacao());
		assertEquals(MatriculaEmProcessamento.FORMANDO, resultado[4].getTipo());
		assertEquals(7.0, resultado[4].getIndice());
		assertEquals(2, resultado[4].getOrdem());

		assertEquals(SituacaoMatricula.MATRICULADO, resultado[5].getSituacao());
		assertEquals(MatriculaEmProcessamento.RECUPERACAO, resultado[5].getTipo());
		assertEquals(8.0, resultado[5].getIndice());
		assertEquals(2, resultado[5].getOrdem());

		assertEquals(SituacaoMatricula.MATRICULADO, resultado[6].getSituacao());
		assertEquals(MatriculaEmProcessamento.RECUPERACAO, resultado[6].getTipo());
		assertEquals(7.0, resultado[6].getIndice());
		assertEquals(3, resultado[6].getOrdem());

		assertEquals(SituacaoMatricula.INDEFERIDA, resultado[7].getSituacao());
		assertEquals(MatriculaEmProcessamento.ADIANTADO, resultado[7].getTipo());
		assertEquals(8.0, resultado[7].getIndice());
		assertEquals(4, resultado[7].getOrdem());

		assertEquals(SituacaoMatricula.MATRICULADO, resultado[8].getSituacao());
		assertEquals(MatriculaEmProcessamento.ELETIVO, resultado[8].getTipo());
		assertEquals(8.0, resultado[8].getIndice());
		assertEquals(3, resultado[8].getOrdem());

		assertEquals(SituacaoMatricula.INDEFERIDA, resultado[9].getSituacao());
		assertEquals(MatriculaEmProcessamento.OUTROS, resultado[9].getTipo());
		assertEquals(8.0, resultado[9].getIndice());
		assertEquals(5, resultado[9].getOrdem());
		
	}

	private Map<MatrizCurricular, ReservaCurso> getVagasSemReserva() {
		return new HashMap<MatrizCurricular, ReservaCurso>();
	}

	private Map<Integer, List<MatriculaEmProcessamento>> getMatriculasVazias() {
		return new HashMap<Integer, List<MatriculaEmProcessamento>>();
	}
	
	private Map<Integer, List<MatriculaEmProcessamento>> getMatriculasSemReserva() {
		Map<Integer, List<MatriculaEmProcessamento>> resultado = new HashMap<Integer, List<MatriculaEmProcessamento>>();
		resultado.put(0, new ArrayList<MatriculaEmProcessamento>());
		
		MatriculaEmProcessamento m1 = new MatriculaEmProcessamento(MatriculaEmProcessamento.NIVELADO, 8.0);
		MatriculaEmProcessamento m2 = new MatriculaEmProcessamento(MatriculaEmProcessamento.NIVELADO, 9.0);
		MatriculaEmProcessamento m3 = new MatriculaEmProcessamento(MatriculaEmProcessamento.VESTIBULAR, 10.0);
		MatriculaEmProcessamento m4 = new MatriculaEmProcessamento(MatriculaEmProcessamento.FORMANDO, 7.0);
		MatriculaEmProcessamento m5 = new MatriculaEmProcessamento(MatriculaEmProcessamento.FORMANDO, 9.0);
		MatriculaEmProcessamento m6 = new MatriculaEmProcessamento(MatriculaEmProcessamento.ADIANTADO, 8.0);
		MatriculaEmProcessamento m7 = new MatriculaEmProcessamento(MatriculaEmProcessamento.ELETIVO, 8.0);
		MatriculaEmProcessamento m8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.RECUPERACAO, 8.0);
		MatriculaEmProcessamento m9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.RECUPERACAO, 7.0);
		MatriculaEmProcessamento m10 = new MatriculaEmProcessamento(MatriculaEmProcessamento.OUTROS, 8.0);

		resultado.get(0).add(m1);
		resultado.get(0).add(m2);
		resultado.get(0).add(m3);
		resultado.get(0).add(m4);
		resultado.get(0).add(m5);
		resultado.get(0).add(m6);
		resultado.get(0).add(m7);
		resultado.get(0).add(m8);
		resultado.get(0).add(m9);
		resultado.get(0).add(m10);
		
		Collections.sort(resultado.get(0), ProcessamentoMatriculasTurma.PROCESSAMENTO_COMPARATOR);
		
		return resultado;
	}
	
	private Map<Integer, List<MatriculaEmProcessamento>> getMatriculasComReserva() {
		Map<Integer, List<MatriculaEmProcessamento>> resultado = new HashMap<Integer, List<MatriculaEmProcessamento>>();
		resultado.put(0, new ArrayList<MatriculaEmProcessamento>());
		resultado.put(1, new ArrayList<MatriculaEmProcessamento>());
		resultado.put(2, new ArrayList<MatriculaEmProcessamento>());
		
		MatriculaEmProcessamento m1 = new MatriculaEmProcessamento(MatriculaEmProcessamento.NIVELADO, 8.0, 1);
		MatriculaEmProcessamento m2 = new MatriculaEmProcessamento(MatriculaEmProcessamento.FORMANDO, 7.0, 1);
		MatriculaEmProcessamento m3 = new MatriculaEmProcessamento(MatriculaEmProcessamento.ELETIVO, 8.0, 1);
		MatriculaEmProcessamento m4 = new MatriculaEmProcessamento(MatriculaEmProcessamento.OUTROS, 8.0, 1);

		MatriculaEmProcessamento m5 = new MatriculaEmProcessamento(MatriculaEmProcessamento.NIVELADO, 9.0, 2);
		MatriculaEmProcessamento m6 = new MatriculaEmProcessamento(MatriculaEmProcessamento.FORMANDO, 9.0, 2);
		MatriculaEmProcessamento m7 = new MatriculaEmProcessamento(MatriculaEmProcessamento.RECUPERACAO, 8.0, 2);

		MatriculaEmProcessamento m8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.VESTIBULAR, 10.0, 3);
		MatriculaEmProcessamento m9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.RECUPERACAO, 7.0, 3);
		MatriculaEmProcessamento m10 = new MatriculaEmProcessamento(MatriculaEmProcessamento.ADIANTADO, 8.0, 3);

		resultado.get(1).add(m1);
		resultado.get(1).add(m2);
		resultado.get(1).add(m3);
		resultado.get(1).add(m4);
		resultado.get(2).add(m5);
		resultado.get(2).add(m6);
		resultado.get(2).add(m7);
		resultado.get(0).add(m8);
		resultado.get(0).add(m9);
		resultado.get(0).add(m10);
		
		Collections.sort(resultado.get(0), ProcessamentoMatriculasTurma.PROCESSAMENTO_COMPARATOR);
		Collections.sort(resultado.get(1), ProcessamentoMatriculasTurma.PROCESSAMENTO_COMPARATOR);
		Collections.sort(resultado.get(2), ProcessamentoMatriculasTurma.PROCESSAMENTO_COMPARATOR);
		
		return resultado;
	}
	
	private Map<MatrizCurricular, ReservaCurso> getVagasComReserva() {
		Map<MatrizCurricular, ReservaCurso> vagas = new HashMap<MatrizCurricular, ReservaCurso>();
		ReservaCurso r = new ReservaCurso();
		r.setVagasReservadas((short) 3);
		vagas.put(new MatrizCurricular(1), r);
		r = new ReservaCurso();
		r.setVagasReservadas((short) 2);
		vagas.put(new MatrizCurricular(2), r);
		return vagas;
	}
	
	private Turma getTurma() {
		Turma t = new Turma(1);
		t.setCapacidadeAluno(8);
		return t;
	}

}



