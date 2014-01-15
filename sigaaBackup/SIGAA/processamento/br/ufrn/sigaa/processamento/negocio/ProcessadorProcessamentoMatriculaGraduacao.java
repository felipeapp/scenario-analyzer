/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 06/11/2007 
 *
 */
package br.ufrn.sigaa.processamento.negocio;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.processamento.batch.ListaTurmasProcessadas;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;
import br.ufrn.sigaa.processamento.dominio.ProcessamentoMatriculasTurma;
import br.ufrn.sigaa.processamento.dominio.TurmaProcessada;

/**
 * Processador para realizar o processamento da matrícula em disciplinas.
 * Pega o conjunto de matrículas realizadas, ordena de acordo com as ordens
 * de prioridade do regulamento, e matrícula os n primeiros, onde n é a capacidade
 * da turma. Os demais são marcados como indeferidos.
 *
 * @author David Pereira
 *
 */
public class ProcessadorProcessamentoMatriculaGraduacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		ProcessamentoMatriculaDao dao = getDAO(ProcessamentoMatriculaGraduacaoDao.class, mov);

		try {
			boolean rematricula = ((TurmaMov) mov).isRematricula();
			Turma turma = ((TurmaMov) mov).getTurma();

			// Para cada turma busca as matrículas e ordena
			Map<MatrizCurricular, ReservaCurso> vagasReservadas = dao.findInformacoesVagasTurma(turma.getId());
			int matriculados = dao.findTotalMatriculadosTurma(turma.getId());
			turma.setQtdMatriculados(matriculados);
			
			Map<Integer, List<MatriculaEmProcessamento>> matriculas = dao.findMatriculasEmOrdemParaProcessamento(turma, vagasReservadas, rematricula);
			matriculas.entrySet().size();
			
			// Efetua o processamento, marcando as matrículas como MATRICULADAS ou INDEFERIDAS
			ProcessamentoMatriculasTurma processamento = ProcessamentoMatriculasTurma.criar(turma, vagasReservadas, matriculas);
			List<MatriculaEmProcessamento> resultadoProcessamento = processamento.processar();
			
			// Inserir dados no banco (executar batch update)
			System.out.println(turma.getId());
			dao.registrarProcessamentoTurma(turma.getAno(), turma.getPeriodo(), rematricula, turma, resultadoProcessamento);
			
			ListaTurmasProcessadas.addTurma(new TurmaProcessada(turma, resultadoProcessamento, vagasReservadas, null));
			
			System.out.println("Processada " + turma.getId());

		} finally {
			dao.close();
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
