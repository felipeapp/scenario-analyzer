package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;


/**
 * Movimento utilizado para a consolidação das notas do IMD.
 * 
 * @author Rafael Silva
 *
 */
public class MovimentoConsolidacaoNotas extends AbstractMovimentoAdapter{
	/**Turma de entrada do técnico*/
	private TurmaEntradaTecnico turmaEntrada =  new TurmaEntradaTecnico();
	
	/**Notas dos alunos na turma */
	private List<MatriculaTurma> matriculas = new ArrayList<MatriculaTurma>();
	
	/** Módulo ao qual a turma está vinculado*/
	private Modulo modulo = new Modulo();

	//GETTERS AND SETTERS
	public TurmaEntradaTecnico getTurmaEntrada() {
		return turmaEntrada;
	}

	public void setTurmaEntrada(TurmaEntradaTecnico turmaEntrada) {
		this.turmaEntrada = turmaEntrada;
	}

	public List<MatriculaTurma> getMatriculas() {
		return matriculas;
	}
	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public void setMatriculas(List<MatriculaTurma> matriculas) {
		this.matriculas = matriculas;
	}
	
}
