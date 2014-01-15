package br.ufrn.sigaa.ensino.tecnico.relatorios;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Linha auxiliar repons�vel por armazenar as informa��es necess�rias para a 
 * transfer�ncia dos discentes entre turmas de entrada. 
 *  
 * @author Jean Guerethes
 */
public class LinhaAuxiliarTransfTurmaEntrada {

	/** Turma na qual o discente est� matriculado */
	private Turma turma = new Turma();
	/** Turma Destino a qual o discente ir� est� matriculado */
	private Turma turmaDestino = new Turma();
	/** Turmas encontradas onde discente pode se matricular */
	private Collection<Turma> turmas = new ArrayList<Turma>();
	/** Discente que est� realizado a tranfer�ncia de turma de entrada */
	private Discente discente;
	/** Componente curricular da turma matriculada */
	private MatriculaComponente matriculaComp = new MatriculaComponente();
	/** Retorna todas as turma que o discente est� matriculado */
	private Collection<Turma> turmasMatriculadas = new ArrayList<Turma>();
	
	/**
	 * Realiza a constru��o da linha auxiliar utilizada para realizar a transfer�ncia entre turmas de entrada.
	 * @param turma
	 * @param turmas
	 * @param discente
	 * @return
	 */
	public static LinhaAuxiliarTransfTurmaEntrada montarLinhaAuxiliarTransfTurmaEntrada(Turma turma, ArrayList<Turma> turmas, Discente discente) {
		LinhaAuxiliarTransfTurmaEntrada linha = new LinhaAuxiliarTransfTurmaEntrada();
		linha.setTurma(turma);
		linha.setTurmas(turmas);
		if ( turmas.size() == 1 )
			linha.setTurmaDestino(turmas.get(0));
		linha.setDiscente(discente);
		return linha;
	}
	
	public Turma getTurma() {
		return turma;
	}
	
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Turma getTurmaDestino() {
		return turmaDestino;
	}

	public void setTurmaDestino(Turma turmaDestino) {
		this.turmaDestino = turmaDestino;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/**
	 * Cria uma combo com as poss�veis turmas na qual o 
	 * discente pode ser transferido.
	 */
	public Collection<SelectItem> getTurmaCombo(){
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		for (Turma t : turmas)
			itens.add(new SelectItem(t.getId(), t.getNome()));
		return itens;
	}

	public MatriculaComponente getMatriculaComp() {
		return matriculaComp;
	}

	public void setMatriculaComp(MatriculaComponente matriculaComp) {
		this.matriculaComp = matriculaComp;
	}

	public Collection<Turma> getTurmasMatriculadas() {
		return turmasMatriculadas;
	}

	public void setTurmasMatriculadas(Collection<Turma> turmasMatriculadas) {
		this.turmasMatriculadas = turmasMatriculadas;
	}
	
	public boolean isExibeCombo(){
		return getTurmas().size() != 1;
	}
	
}