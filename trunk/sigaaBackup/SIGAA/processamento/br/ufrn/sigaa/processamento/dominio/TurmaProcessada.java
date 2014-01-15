/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/01/2008 
 *
 */
package br.ufrn.sigaa.processamento.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;

/**
 * Classe responsável pela Turma do Processamento.
 * 
 * @author David Pereira
 *
 */
public class TurmaProcessada {
	
	private Turma turma;
	
	private Map<MatrizCurricular, ReservaCurso> reservas;
	
	private Map<MatrizCurricular, List<MatriculaEmProcessamento>> matriculas;

	private String docentes;

	private List<MatriculaEmProcessamento> desistencias;

	public TurmaProcessada(Turma turma, Collection<MatriculaEmProcessamento> matriculas, Map<MatrizCurricular, ReservaCurso> reservas, List<MatriculaEmProcessamento> desistencias) {
		this.turma = turma;
		this.reservas = reservas;
		this.docentes = turma.getDocentesNomes();
		this.desistencias = desistencias;
		
		Map<MatrizCurricular, List<MatriculaEmProcessamento>> mapa = new TreeMap<MatrizCurricular, List<MatriculaEmProcessamento>>(new Comparator<MatrizCurricular> () {
			public int compare(MatrizCurricular o1, MatrizCurricular o2) {
				return o2.getId() - o1.getId();
			}
		});
		
		for (MatrizCurricular m : reservas.keySet()) {
			mapa.put(m, new ArrayList<MatriculaEmProcessamento>());
		}
		
		MatrizCurricular outros = new MatrizCurricular(-1);
		outros.setCurso(new Curso());
		outros.getCurso().setNome("Outros");
		mapa.put(outros, new ArrayList<MatriculaEmProcessamento>());
		
		for (MatriculaEmProcessamento matricula : matriculas) {
			MatrizCurricular matriz = new MatrizCurricular(matricula.getIdMatrizReserva() == 0 ? -1 : matricula.getIdMatrizReserva());
			if (mapa.get(matriz) == null) {
				mapa.get(outros).add(matricula);
			} else {
				mapa.get(matriz).add(matricula);
			}
		}

		this.matriculas = mapa;
	}

	public int getAno() {
		return turma.getAno();
	}

	public int getPeriodo() {
		return turma.getPeriodo();
	}

	public int getId() {
		return turma.getId();
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Map<MatrizCurricular, List<MatriculaEmProcessamento>> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Map<MatrizCurricular, List<MatriculaEmProcessamento>> matriculas) {
		this.matriculas = matriculas;
	}

	public Map<MatrizCurricular, ReservaCurso> getReservas() {
		return reservas;
	}

	public void setReservas(Map<MatrizCurricular, ReservaCurso> reservas) {
		this.reservas = reservas;
	}

	public String getDocentes() {
		return docentes;
	}
	
	public void setDocentes(String docentes) {
		this.docentes = docentes;
	}

	public List<MatriculaEmProcessamento> getDesistencias() {
		return desistencias;
	}

	public void setDesistencias(List<MatriculaEmProcessamento> desistencias) {
		this.desistencias = desistencias;
	}

}
