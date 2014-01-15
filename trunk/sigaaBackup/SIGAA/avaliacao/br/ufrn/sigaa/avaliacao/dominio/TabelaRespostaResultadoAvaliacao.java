package br.ufrn.sigaa.avaliacao.dominio;

import java.util.List;
import java.util.Map;

import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Tabela com as respostas dos discentes na Avaliação Institucional para um
 * docente/turma. Esta classe auxilia a geração do relatório analítico do
 * resultado da Avaliação Institucional do docente.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class TabelaRespostaResultadoAvaliacao {
	/** Mapa de respostas por discente.*/
	private Map<Discente, List<RespostaPergunta>> mapaRespostas;
	
	/** Lista com as médias das notas por pergunta. */
	private List<MediaNotas> medias;
	
	/** Lista com os percentuais de respostas sim/não por pergunta. */
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
	
	/** Retorna a lista com as médias das notas por pergunta.
	 * @return Lista com as médias das notas por pergunta. 
	 */
	public List<MediaNotas> getMedias() {
		return medias;
	}
	
	/** Seta a lista com as médias das notas por pergunta. 
	 * @param medias Lista com as médias das notas por pergunta. 
	 */
	public void setMedias(List<MediaNotas> medias) {
		this.medias = medias;
	}
	
	/** Retorna a lista com os percentuais de respostas sim/não por pergunta.
	 * @return Lista com os percentuais de respostas sim/não por pergunta. 
	 */
	public List<PercentualSimNao> getPercentuais() {
		return percentuais;
	}
	
	/** Seta a lista com os percentuais de respostas sim/não por pergunta. 
	 * @param percentuais Lista com os percentuais de respostas sim/não por pergunta. 
	 */
	public void setPercentuais(List<PercentualSimNao> percentuais) {
		this.percentuais = percentuais;
	}
}
