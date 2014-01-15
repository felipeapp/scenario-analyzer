/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 07/01/2008 
 *
 */
package br.ufrn.sigaa.processamento.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;


/**
 * Classe para identificar as matrículas como MATRICULADAS
 * ou INDEFERIDAS de acordo com o número de vagas da turma
 * e considerando a reserva de vagas por curso.
 * 
 * Se um aluno de um curso não entrar nas vagas da reserva,
 * ele passa a concorrer com os demais alunos de acordo com
 * os outros critérios de processamento.
 *  
 * @author David Pereira
 *
 */
public class ProcessamentoMatriculasTurma {

	private int capacidade;
	
	private Map<MatrizCurricular, ReservaCurso> vagasReservadasPorMatriz;
	
	private Map<Integer, List<MatriculaEmProcessamento>> matriculas;

	private ProcessamentoMatriculasTurma() { }
	
	public List<MatriculaEmProcessamento> processar() {
		
		List<MatriculaEmProcessamento> matriculasAposProcessamento = new ArrayList<MatriculaEmProcessamento>(); // Lista geral de matrículas da turma após o processamento.
		List<MatriculaEmProcessamento> sobras = matriculas.get(0); // Matrículas que não caem nas reservas
		matriculas.remove(0);
		
		for (Entry<Integer, List<MatriculaEmProcessamento>> entry : matriculas.entrySet()) {
			int ordem = 0;
			
			for (MatriculaEmProcessamento matricula : entry.getValue()) { // Verificar todas as matrículas cuja matriz tem reserva
				
				ReservaCurso reserva = vagasReservadasPorMatriz.get(new MatrizCurricular(entry.getKey()));
				if (reserva.getTotalVagasReservadas() > 0 && capacidade > 0) {
					if (reserva.getVagasReservadasIngressantes() > 0 && matricula.getTipo() == MatriculaEmProcessamento.VESTIBULAR) {
						reserva.setVagasReservadasIngressantes((short) (reserva.getVagasReservadasIngressantes() - 1));
					} else if (reserva.getVagasReservadas() > 0)
						reserva.setVagasReservadas((short) (reserva.getVagasReservadas() - 1));
					else {
						sobras.add(matricula); 
						continue;
					}
					capacidade--;
					matricula.setSituacao(SituacaoMatricula.MATRICULADO);
					matricula.setOrdem(++ordem);
					matricula.setIdMatrizReserva(matricula.getIdMatrizCurricular());
					matriculasAposProcessamento.add(matricula);
				} else {
					// Se não existirem mais vagas para essa reserva, o discente passa a concorrer com os outros das sobras
					sobras.add(matricula); 
				}
			}
			
			int vagasIngressantes = calcularVagasIngressantes();
			if (vagasIngressantes > 0)
				capacidade -= vagasIngressantes;
		}
		
		if (!isEmpty(sobras)) {
			Collections.sort(sobras, new Comparator<MatriculaEmProcessamento>() {
	
				public int compare(MatriculaEmProcessamento o1, MatriculaEmProcessamento o2) {
					int tipo = Integer.valueOf(o2.getTipo()).compareTo(o1.getTipo());
					
					if (tipo == 0) {
						return Double.valueOf(o2.getIndice()).compareTo(o1.getIndice());
					}
					
					return tipo;
				}
				
			});
		}
		
		// Após processar todas as reservas, processas as sobras
		if (!isEmpty(sobras)) {
			int ordem = 0;
			for (MatriculaEmProcessamento matricula : sobras) {
				if (capacidade > 0) {
					capacidade--;
					matricula.setSituacao(SituacaoMatricula.MATRICULADO);
				} else {
					matricula.setSituacao(SituacaoMatricula.INDEFERIDA);
				}
				
				matricula.setOrdem(++ordem);
				matricula.setIdMatrizReserva(0);
				matriculasAposProcessamento.add(matricula);
			}
		}
		
		Collections.sort(matriculasAposProcessamento, PROCESSAMENTO_COMPARATOR);
		return matriculasAposProcessamento;
	}
	
	private int calcularVagasIngressantes() {
		int vagasIngressantes = 0;
		for (ReservaCurso reserva : vagasReservadasPorMatriz.values()) {
			vagasIngressantes += reserva.getVagasReservadasIngressantes();
		}
		return vagasIngressantes;
	}

	public static ProcessamentoMatriculasTurma criar(Turma turma, Map<MatrizCurricular, ReservaCurso> vagasReservadasPorMatriz, Map<Integer, List<MatriculaEmProcessamento>> matriculas) {
		ProcessamentoMatriculasTurma processador = new ProcessamentoMatriculasTurma();
		processador.capacidade = turma.getCapacidadeAluno() - turma.getTotalMatriculados();
		processador.vagasReservadasPorMatriz = vagasReservadasPorMatriz;
		processador.matriculas = matriculas;
		
		return processador;
	}
	
	public static final Comparator<MatriculaEmProcessamento> PROCESSAMENTO_COMPARATOR = new Comparator<MatriculaEmProcessamento>() {
		public int compare(MatriculaEmProcessamento o1, MatriculaEmProcessamento o2) {
			return o2.compareTo(o1);
		}
	};
	
}
