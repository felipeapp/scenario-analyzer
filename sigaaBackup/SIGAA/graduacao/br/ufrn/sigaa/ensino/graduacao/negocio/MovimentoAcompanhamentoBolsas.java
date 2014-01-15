/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;

/** Movimento que encapsula os dados para o processador que ir� gerar os relat�rios.
 * @author �dipo Elder F. Melo
 *
 */
public class MovimentoAcompanhamentoBolsas extends AbstractMovimentoAdapter {
	
	/** Constantes para indicar qual consulta ser� realizada. */
	public static final int CONSULTA_CARGA_HORARIA_SEMESTRE_ATUAL = 1;
	public static final int CONSULTA_DESEMPENHO_ACADEMICO = 2;
	public static final int CONSULTA_BOLSISTA_DUPLO_VINCULO = 3;
	
	/** Indica qual consulta ser� realizada. */
	private int consulta = 0;
	
	/** Ano de ingresso. */
	private int ano;
	
	/** Per�odo de ingresso. */
	private int periodo;

	/** Ano a ser processado. */
	private int anoIngresso;
	
	/** Per�odo a ser processado. */
	private int periodoIngresso;

	/** Informa o tipo de bolsa que se deseja que retorne */
	private int idTipoBolsa;

	/** Informa a situa��o da bolsa que se deseja que retorne */
	private int idSituacaoBolsa;

	/** Indica qual consulta ser� realizada. 
	 * @return 
	 */
	public int getConsulta() {
		return consulta;
	}
	
	/** Seta qual consulta ser� realizada.
	 * @param consulta
	 */
	public void setConsulta(int consulta) {
		this.consulta = consulta;
	}

	/** Retorna o ano a ser processado. 
	 * @return Ano a ser processado. 
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano a ser processado. 
	 * @param ano Ano a ser processado. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo a ser processado. 
	 * @return Per�odo a ser processado. 
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo a ser processado. 
	 * @param periodo Per�odo a ser processado. 
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getIdTipoBolsa() {
		return idTipoBolsa;
	}

	public void setIdTipoBolsa(int idTipoBolsa) {
		this.idTipoBolsa = idTipoBolsa;
	}

	public int getAnoIngresso() {
		return anoIngresso;
	}

	public void setAnoIngresso(int anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public int getPeriodoIngresso() {
		return periodoIngresso;
	}

	public void setPeriodoIngresso(int periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	public int getIdSituacaoBolsa() {
		return idSituacaoBolsa;
	}

	public void setIdSituacaoBolsa(int idSituacaoBolsa) {
		this.idSituacaoBolsa = idSituacaoBolsa;
	}
	
}