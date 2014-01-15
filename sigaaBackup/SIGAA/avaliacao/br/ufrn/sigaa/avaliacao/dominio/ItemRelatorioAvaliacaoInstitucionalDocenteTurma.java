package br.ufrn.sigaa.avaliacao.dominio;

import java.util.List;

import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * Classe para auxiliar a geração do relatório do resultado individual do
 * docente na Avaliação Institucional.
 * 
 * Classe não mais utilizada.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Deprecated
public class ItemRelatorioAvaliacaoInstitucionalDocenteTurma {
	/** Ano da Avaliação Institucional. */
	private int ano;
	/** Período da Avaliação Institucional. */
	private int periodo;
	/** DocenteTurma ao qual este dados pertencem. */
	private DocenteTurma docenteTurma;
	/** Lista de respostas dadas pelos discentes ao DocenteTurma.*/
	private List<RespostaPergunta> respostas;
	/** Média calculada das notas dadas pelos discentes. */
	private MediaNotas mediaNotas;
	/** Percentual de notas Sim/Não dadas pelos discentes*/
	private PercentualSimNao percentualSimNao;
	
	/** Retorna ano da Avaliação Institucional. 
	 * @return Ano da Avaliação Institucional. 
	 */
	public int getAno() {
		return ano;
	}
	/** Seta o ano da Avaliação Institucional. 
	 * @param ano Ano da Avaliação Institucional. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
	/** Retorna o período da Avaliação Institucional.
	 * @return Período da Avaliação Institucional. 
	 */
	public int getPeriodo() {
		return periodo;
	}
	/** Seta o período da Avaliação Institucional. 
	 * @param periodo Período da Avaliação Institucional. 
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	/** Retorna o DocenteTurma ao qual este dados pertencem. 
	 * @return DocenteTurma ao qual este dados pertencem. 
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}
	/** Seta o DocenteTurma ao qual este dados pertencem. 
	 * @param docenteTurma DocenteTurma ao qual este dados pertencem. 
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}
	/** Retorna a lista de respostas dadas pelos discentes ao DocenteTurma. 
	 * @return Lista de respostas dadas pelos discentes ao DocenteTurma.
	 */
	public List<RespostaPergunta> getRespostas() {
		return respostas;
	}
	/** Seta a lista de respostas dadas pelos discentes ao DocenteTurma.
	 * @param respostas Lista de respostas dadas pelos discentes ao DocenteTurma.
	 */
	public void setRespostas(List<RespostaPergunta> respostas) {
		this.respostas = respostas;
	}
	/** Retorna a média calculada das notas dadas pelos discentes. 
	 * @return Média calculada das notas dadas pelos discentes. 
	 */
	public MediaNotas getMediaNotas() {
		return mediaNotas;
	}
	/** Seta a média calculada das notas dadas pelos discentes.
	 * @param mediaNotas Média calculada das notas dadas pelos discentes. 
	 */
	public void setMediaNotas(MediaNotas mediaNotas) {
		this.mediaNotas = mediaNotas;
	}
	/** Retorna o percentual de notas Sim/Não dadas pelos discentes
	 * @return Percentual de notas Sim/Não dadas pelos discentes
	 */
	public PercentualSimNao getPercentualSimNao() {
		return percentualSimNao;
	}
	/** Seta o percentual de notas Sim/Não dadas pelos discentes
	 * @param percentualSimNao Percentual de notas Sim/Não dadas pelos discentes
	 */
	public void setPercentualSimNao(PercentualSimNao percentualSimNao) {
		this.percentualSimNao = percentualSimNao;
	}
	
}
