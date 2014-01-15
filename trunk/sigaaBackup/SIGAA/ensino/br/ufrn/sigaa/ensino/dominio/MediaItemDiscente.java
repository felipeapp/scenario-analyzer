/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 23/05/2008
 */
package br.ufrn.sigaa.ensino.dominio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe responsável pela exibição das notas lançadas pelo tutor(a) na semana.
 * 
 * @author Bernardo
 */
public class MediaItemDiscente {

	/** Discente avaliado. */
	private Discente discente;
	
	/** Médias do discente em cada item de avaliação. */
	private List<MediaItem> medias;
	
	/** Médias gerais dos períodos de avaliação. */
	private List<BigDecimal> mediasGerais;
	
	/** Semana máxima que possui avaliação. Utilizada para contolar o aparecimento das notas no relatório. */
	private int semanaMaximaAvaliacao;
	
	/** metodologia de avaliação utilizada para calcular a média. */ 
	private MetodologiaAvaliacao metodologia;
	
	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public List<MediaItem> getMedias() {
		return medias;
	}

	public void setMedias(List<MediaItem> medias) {
		this.medias = medias;
	}

	public List<BigDecimal> getMediasGerais() {
		return mediasGerais;
	}

	public int getSemanaMaximaAvaliacao() {
		return semanaMaximaAvaliacao;
	}

	public void setSemanaMaximaAvaliacao(int semanaMaximaAvaliacao) {
		this.semanaMaximaAvaliacao = semanaMaximaAvaliacao;
	}

	public MetodologiaAvaliacao getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(MetodologiaAvaliacao metodologia) {
		this.metodologia = metodologia;
	}

	/**
	 * Adiciona uma média geral na listagem.
	 * 
	 * @param mediaGeral
	 * @throws NegocioException
	 */
	public void addMediaGeral(BigDecimal mediaGeral) throws NegocioException {
		if(mediasGerais == null)
			mediasGerais = new ArrayList<BigDecimal>();
		
		if(mediasGerais.size() == 2)
			throw new NegocioException("Não é possível adicionar mais de duas médias.");
		
		mediasGerais.add(mediaGeral);
	}

	/**
	 * Adiciona uma {@link MediaItem} na listagem.
	 * 
	 * @param mediaItem
	 */
	public void addMediaItem(MediaItem mediaItem) {
		if(medias == null)
			medias = new ArrayList<MediaItem>();
		
		if(!medias.contains(mediaItem))
			medias.add(mediaItem);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MediaItemDiscente mediaItemDiscente = (MediaItemDiscente) obj;
		return discente.equals(mediaItemDiscente.getDiscente());
	}
	
}
