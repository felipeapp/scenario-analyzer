/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.util.Collection;
import java.util.Map;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.ensino.tecnico.relatorios.LinhaAuxiliarTransfTurmaEntrada;

/**
 * Movimento usado para efetuar transferência de alunos entre turmas de 
 * Entrada e turmas Matriculadas.
 *
 * @author Jean Guerethes
 */
public class MovimentoTransferenciaTurmaTurmaEntrada extends AbstractMovimentoAdapter {

	/** Turma de Entrada de Origem */
	private TurmaEntradaTecnico turmaEntradaOrigem;
	/** Turma de Entrada de Destino */
	private TurmaEntradaTecnico turmaEntradaDestino;
	/** Turma do semestre do discente encotradas */
	private Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> turmas;
	/** Turma que o discente está matriculado no semestre */
	private Collection<Turma> turmasMatriculadas;
	
	public TurmaEntradaTecnico getTurmaEntradaDestino() {
		return turmaEntradaDestino;
	}
	
	public void setTurmaEntradaDestino(TurmaEntradaTecnico turmaEntradaDestino) {
		this.turmaEntradaDestino = turmaEntradaDestino;
	}
	
	public TurmaEntradaTecnico getTurmaEntradaOrigem() {
		return turmaEntradaOrigem;
	}
	
	public void setTurmaEntradaOrigem(TurmaEntradaTecnico turmaEntradaOrigem) {
		this.turmaEntradaOrigem = turmaEntradaOrigem;
	}
	
	public Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> getTurmas() {
		return turmas;
	}

	public void setTurmas(
			Map<DiscenteAdapter, Collection<LinhaAuxiliarTransfTurmaEntrada>> turmas) {
		this.turmas = turmas;
	}

	public Collection<Turma> getTurmasMatriculadas() {
		return turmasMatriculadas;
	}

	public void setTurmasMatriculadas(Collection<Turma> turmasMatriculadas) {
		this.turmasMatriculadas = turmasMatriculadas;
	}
	
}