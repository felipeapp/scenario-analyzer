/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 06/11/2007
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
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaMusicaDao;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;
import br.ufrn.sigaa.processamento.dominio.ProcessamentoMatriculasTurma;
import br.ufrn.sigaa.processamento.dominio.TurmaProcessada;

/**
 * Processador para realizar o processamento da matr�cula em disciplinas de m�sica.
 * Pega o conjunto de matr�culas realizadas, ordena de acordo com as ordens
 * de prioridade do regulamento, e matr�cula os n primeiros, onde n � a capacidade
 * da turma. Os demais s�o marcados como indeferidos.
 *
 * @author David Pereira
 *
 */
public class ProcessadorProcessamentoMatriculaMusica extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		ProcessamentoMatriculaDao dao = getDAO(ProcessamentoMatriculaMusicaDao.class, mov);

		try {
			boolean rematricula = ((TurmaMov) mov).isRematricula();
			Turma turma = ((TurmaMov) mov).getTurma();
			
			int matriculados = dao.findTotalMatriculadosTurma(turma.getId());
			turma.setQtdMatriculados(matriculados);
			
			// Para cada turma busca as matr�culas e ordena
			Map<MatrizCurricular, ReservaCurso> vagasReservadas = dao.findInformacoesVagasTurma(turma.getId());
			Map<Integer, List<MatriculaEmProcessamento>> matriculas = dao.findMatriculasEmOrdemParaProcessamento(turma, vagasReservadas, rematricula);
			matriculas.entrySet().size();
			// Efetua o processamento, marcando as matr�culas como MATRICULADAS ou INDEFERIDAS
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
