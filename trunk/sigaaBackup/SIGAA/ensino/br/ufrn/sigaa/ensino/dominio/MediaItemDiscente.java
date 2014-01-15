/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe respons�vel pela exibi��o das notas lan�adas pelo tutor(a) na semana.
 * 
 * @author Bernardo
 */
public class MediaItemDiscente {

	/** Discente avaliado. */
	private Discente discente;
	
	/** M�dias do discente em cada item de avalia��o. */
	private List<MediaItem> medias;
	
	/** M�dias gerais dos per�odos de avalia��o. */
	private List<BigDecimal> mediasGerais;
	
	/** Semana m�xima que possui avalia��o. Utilizada para contolar o aparecimento das notas no relat�rio. */
	private int semanaMaximaAvaliacao;
	
	/** metodologia de avalia��o utilizada para calcular a m�dia. */ 
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
	 * Adiciona uma m�dia geral na listagem.
	 * 
	 * @param mediaGeral
	 * @throws NegocioException
	 */
	public void addMediaGeral(BigDecimal mediaGeral) throws NegocioException {
		if(mediasGerais == null)
			mediasGerais = new ArrayList<BigDecimal>();
		
		if(mediasGerais.size() == 2)
			throw new NegocioException("N�o � poss�vel adicionar mais de duas m�dias.");
		
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
