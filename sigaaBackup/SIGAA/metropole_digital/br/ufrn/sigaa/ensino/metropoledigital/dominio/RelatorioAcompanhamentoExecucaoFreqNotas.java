package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Entidade utilizada no relat�rio "Execu��o da Frequ�ncia e Notas Semanais" do Coordenador de Tutores do IMD
 * 
 * ATEN��O! ESSA CLASSE N�O SER� PERSISTIDA NO BANCO DE DADOS
 * 
 * @author Rafael Barros
 *
 */
public class RelatorioAcompanhamentoExecucaoFreqNotas {

	/** Objeto correspondente a turma de entrada vinculada ao acompanhamento de execu��o da frequ�ncia e notas semanais **/
	private TurmaEntradaTecnico turmaEntrada = new TurmaEntradaTecnico();
	
	/** Objeto correspondente ao periodo avalia��o vinculado ao acompanhamento de execu��o de frequ�ncia e notas semanais**/
	private PeriodoAvaliacao periodo = new PeriodoAvaliacao();
	
	/** Vari�vel de controle que indica se a PV foi executada ou n�o para uma determinada turma e periodo de avalia��o**/
	private int pvExecutada;
	
	/** Vari�vel de controle que indica se a PP foi executada ou n�o para uma determinada turma e periodo de avalia��o**/
	private int ppExecutada;
	
	/** Vari�vel de controle que indica se a frequ�ncia foi executada ou n�o para uma determinada turma e periodo de avalia��o**/
	private int frequenciaExecutada;

	public RelatorioAcompanhamentoExecucaoFreqNotas(){
	}
	
	public RelatorioAcompanhamentoExecucaoFreqNotas(TurmaEntradaTecnico turmaEntrada, PeriodoAvaliacao periodo,
			int pvExecutada, int ppExecutada, int frequenciaExecutada) {
		super();
		this.turmaEntrada = turmaEntrada;
		this.periodo = periodo;
		this.pvExecutada = pvExecutada;
		this.ppExecutada = ppExecutada;
		this.frequenciaExecutada = frequenciaExecutada;
	}

	public TurmaEntradaTecnico getTurmaEntrada() {
		return turmaEntrada;
	}

	public void setTurmaEntrada(TurmaEntradaTecnico turmaEntrada) {
		this.turmaEntrada = turmaEntrada;
	}

	public PeriodoAvaliacao getPeriodo() {
		return periodo;
	}

	public void setPeriodo(PeriodoAvaliacao periodo) {
		this.periodo = periodo;
	}

	public int getPvExecutada() {
		return pvExecutada;
	}

	public void setPvExecutada(int pvExecutada) {
		this.pvExecutada = pvExecutada;
	}

	public int getPpExecutada() {
		return ppExecutada;
	}

	public void setPpExecutada(int ppExecutada) {
		this.ppExecutada = ppExecutada;
	}

	public int getFrequenciaExecutada() {
		return frequenciaExecutada;
	}

	public void setFrequenciaExecutada(int frequenciaExecutada) {
		this.frequenciaExecutada = frequenciaExecutada;
	}

	
	
	
	
}
