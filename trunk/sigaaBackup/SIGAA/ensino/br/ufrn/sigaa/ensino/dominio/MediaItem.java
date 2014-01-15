package br.ufrn.sigaa.ensino.dominio;

import java.math.BigDecimal;

import br.ufrn.sigaa.pesquisa.dominio.ItemAvaliacao;

/**
 * Classe que representa uma média de um determinado {@link ItemAvaliacao} em um período de avaliação.
 * 
 * @author bernardo
 *
 */
public class MediaItem {
	
	/** Período a que a média se refere. */
	private int periodoAvaliacao;
	
	/** Item avaliado. */
	private ItemAvaliacao item;
	
	/** Média do item. */
	private BigDecimal media;

	public int getPeriodoAvaliacao() {
		return periodoAvaliacao;
	}

	public void setPeriodoAvaliacao(int periodoAvaliacao) {
		this.periodoAvaliacao = periodoAvaliacao;
	}

	public ItemAvaliacao getItem() {
		return item;
	}

	public void setItem(ItemAvaliacao item) {
		this.item = item;
	}

	public BigDecimal getMedia() {
		return media;
	}

	public void setMedia(BigDecimal media) {
		this.media = media;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MediaItem mediaItem = (MediaItem) obj;
		return item.equals(mediaItem.getItem()) && periodoAvaliacao == mediaItem.getPeriodoAvaliacao();
	}
}
