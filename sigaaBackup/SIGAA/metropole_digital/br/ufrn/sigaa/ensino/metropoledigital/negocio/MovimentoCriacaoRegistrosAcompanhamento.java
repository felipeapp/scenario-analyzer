package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Movimento para chamada da cria��o dos registros dos acompanhamentos de frequ�ncia e notas semanais dos discentes do IMD
 * 
 * @author Rafael Barros
 * 
 */
public class MovimentoCriacaoRegistrosAcompanhamento extends AbstractMovimentoAdapter {

	/**
	 * Entidade que ir� armazenar a listagem dos discentes do curso t�cnico que
	 * comp�em a turma de entrada selecionada para se efetuar a frequ�ncia
	 **/
	private Collection<AcompanhamentoSemanalDiscente> listaGeralAcompanhamentos;

	/**
	 * Entidade que ir� armazenar a listagem dos discentes do curso t�cnico que
	 * comp�em a turma de entrada selecionada para se efetuar a frequ�ncia
	 **/
	private Collection<DiscenteTecnico> listaDiscentesTurma;

	/**
	 * Entidade que ir� armazenar a turma de entrada selecionada para se efetuar
	 * a frequ�ncia semanal do IMD
	 **/
	private TurmaEntradaTecnico turmaSelecionada;

	/**
	 * Entidade que ir� armazenar a listagem dos per�odos que comp�em a turma de
	 * entrada selecionada para se efetuar a frequ�ncia
	 **/
	private Collection<PeriodoAvaliacao> listaPeriodosTurma;

	public Collection<AcompanhamentoSemanalDiscente> getListaGeralAcompanhamentos() {
		return listaGeralAcompanhamentos;
	}

	public void setListaGeralAcompanhamentos(Collection<AcompanhamentoSemanalDiscente> listaGeralAcompanhamentos) {
		this.listaGeralAcompanhamentos = listaGeralAcompanhamentos;
	}

	public Collection<DiscenteTecnico> getListaDiscentesTurma() {
		return listaDiscentesTurma;
	}

	public void setListaDiscentesTurma(Collection<DiscenteTecnico> listaDiscentesTurma) {
		this.listaDiscentesTurma = listaDiscentesTurma;
	}

	public TurmaEntradaTecnico getTurmaSelecionada() {
		return turmaSelecionada;
	}

	public void setTurmaSelecionada(TurmaEntradaTecnico turmaSelecionada) {
		this.turmaSelecionada = turmaSelecionada;
	}

	public Collection<PeriodoAvaliacao> getListaPeriodosTurma() {
		return listaPeriodosTurma;
	}

	public void setListaPeriodosTurma(Collection<PeriodoAvaliacao> listaPeriodosTurma) {
		this.listaPeriodosTurma = listaPeriodosTurma;
	}

}
