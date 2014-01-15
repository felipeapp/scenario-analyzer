package br.ufrn.sigaa.avaliacao.dominio;

import java.util.List;

import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * Classe para auxiliar a gera��o do relat�rio do resultado individual do
 * docente na Avalia��o Institucional.
 * 
 * Classe n�o mais utilizada.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Deprecated
public class ItemRelatorioAvaliacaoInstitucionalDocenteTurma {
	/** Ano da Avalia��o Institucional. */
	private int ano;
	/** Per�odo da Avalia��o Institucional. */
	private int periodo;
	/** DocenteTurma ao qual este dados pertencem. */
	private DocenteTurma docenteTurma;
	/** Lista de respostas dadas pelos discentes ao DocenteTurma.*/
	private List<RespostaPergunta> respostas;
	/** M�dia calculada das notas dadas pelos discentes. */
	private MediaNotas mediaNotas;
	/** Percentual de notas Sim/N�o dadas pelos discentes*/
	private PercentualSimNao percentualSimNao;
	
	/** Retorna ano da Avalia��o Institucional. 
	 * @return Ano da Avalia��o Institucional. 
	 */
	public int getAno() {
		return ano;
	}
	/** Seta o ano da Avalia��o Institucional. 
	 * @param ano Ano da Avalia��o Institucional. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
	/** Retorna o per�odo da Avalia��o Institucional.
	 * @return Per�odo da Avalia��o Institucional. 
	 */
	public int getPeriodo() {
		return periodo;
	}
	/** Seta o per�odo da Avalia��o Institucional. 
	 * @param periodo Per�odo da Avalia��o Institucional. 
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
	/** Retorna a m�dia calculada das notas dadas pelos discentes. 
	 * @return M�dia calculada das notas dadas pelos discentes. 
	 */
	public MediaNotas getMediaNotas() {
		return mediaNotas;
	}
	/** Seta a m�dia calculada das notas dadas pelos discentes.
	 * @param mediaNotas M�dia calculada das notas dadas pelos discentes. 
	 */
	public void setMediaNotas(MediaNotas mediaNotas) {
		this.mediaNotas = mediaNotas;
	}
	/** Retorna o percentual de notas Sim/N�o dadas pelos discentes
	 * @return Percentual de notas Sim/N�o dadas pelos discentes
	 */
	public PercentualSimNao getPercentualSimNao() {
		return percentualSimNao;
	}
	/** Seta o percentual de notas Sim/N�o dadas pelos discentes
	 * @param percentualSimNao Percentual de notas Sim/N�o dadas pelos discentes
	 */
	public void setPercentualSimNao(PercentualSimNao percentualSimNao) {
		this.percentualSimNao = percentualSimNao;
	}
	
}
