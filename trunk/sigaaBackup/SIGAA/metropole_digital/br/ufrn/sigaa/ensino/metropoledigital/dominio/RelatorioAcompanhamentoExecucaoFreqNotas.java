package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Entidade utilizada no relatório "Execução da Frequência e Notas Semanais" do Coordenador de Tutores do IMD
 * 
 * ATENÇÃO! ESSA CLASSE NÃO SERÁ PERSISTIDA NO BANCO DE DADOS
 * 
 * @author Rafael Barros
 *
 */
public class RelatorioAcompanhamentoExecucaoFreqNotas {

	/** Objeto correspondente a turma de entrada vinculada ao acompanhamento de execução da frequência e notas semanais **/
	private TurmaEntradaTecnico turmaEntrada = new TurmaEntradaTecnico();
	
	/** Objeto correspondente ao periodo avaliação vinculado ao acompanhamento de execução de frequência e notas semanais**/
	private PeriodoAvaliacao periodo = new PeriodoAvaliacao();
	
	/** Variável de controle que indica se a PV foi executada ou não para uma determinada turma e periodo de avaliação**/
	private int pvExecutada;
	
	/** Variável de controle que indica se a PP foi executada ou não para uma determinada turma e periodo de avaliação**/
	private int ppExecutada;
	
	/** Variável de controle que indica se a frequência foi executada ou não para uma determinada turma e periodo de avaliação**/
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
