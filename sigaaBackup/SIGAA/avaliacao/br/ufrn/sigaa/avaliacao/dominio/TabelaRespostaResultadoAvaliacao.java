package br.ufrn.sigaa.avaliacao.dominio;

import java.util.List;
import java.util.Map;

import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Tabela com as respostas dos discentes na Avalia��o Institucional para um
 * docente/turma. Esta classe auxilia a gera��o do relat�rio anal�tico do
 * resultado da Avalia��o Institucional do docente.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class TabelaRespostaResultadoAvaliacao {
	/** Mapa de respostas por discente.*/
	private Map<Discente, List<RespostaPergunta>> mapaRespostas;
	
	/** Lista com as m�dias das notas por pergunta. */
	private List<MediaNotas> medias;
	
	/** Lista com os percentuais de respostas sim/n�o por pergunta. */
	private List<PercentualSimNao> percentuais;
	
	/** Retorna o mapa de respostas por discente.
	 * @return Mapa de respostas por discente.
	 */
	public Map<Discente, List<RespostaPergunta>> getMapaRespostas() {
		return mapaRespostas;
	}
	
	/** Seta o mapa de respostas por discente.
	 * @param mapaRespostas Mapa de respostas por discente.
	 */
	public void setMapaRespostas(Map<Discente, List<RespostaPergunta>> mapaRespostas) {
		this.mapaRespostas = mapaRespostas;
	}
	
	/** Retorna a lista com as m�dias das notas por pergunta.
	 * @return Lista com as m�dias das notas por pergunta. 
	 */
	public List<MediaNotas> getMedias() {
		return medias;
	}
	
	/** Seta a lista com as m�dias das notas por pergunta. 
	 * @param medias Lista com as m�dias das notas por pergunta. 
	 */
	public void setMedias(List<MediaNotas> medias) {
		this.medias = medias;
	}
	
	/** Retorna a lista com os percentuais de respostas sim/n�o por pergunta.
	 * @return Lista com os percentuais de respostas sim/n�o por pergunta. 
	 */
	public List<PercentualSimNao> getPercentuais() {
		return percentuais;
	}
	
	/** Seta a lista com os percentuais de respostas sim/n�o por pergunta. 
	 * @param percentuais Lista com os percentuais de respostas sim/n�o por pergunta. 
	 */
	public void setPercentuais(List<PercentualSimNao> percentuais) {
		this.percentuais = percentuais;
	}
}
