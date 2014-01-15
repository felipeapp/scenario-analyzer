
package br.ufrn.sigaa.ead.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ead.dominio.AvaliacaoDiscenteEad;
import br.ufrn.sigaa.ead.dominio.FichaAvaliacaoEad;

/**
 * Movimento para realização da avaliação de discentes no EAD
 * 
 * @author David Pereira
 *
 */
public class AvaliacaoEadMov extends AbstractMovimentoAdapter {

	private FichaAvaliacaoEad ficha;
	
	private AvaliacaoDiscenteEad avaliacao;

	/**
	 * @return the avaliacao
	 */
	public AvaliacaoDiscenteEad getAvaliacao() {
		return avaliacao;
	}

	/**
	 * @param avaliacao the avaliacao to set
	 */
	public void setAvaliacao(AvaliacaoDiscenteEad avaliacao) {
		this.avaliacao = avaliacao;
	}

	/**
	 * @return the ficha
	 */
	public FichaAvaliacaoEad getFicha() {
		return ficha;
	}

	/**
	 * @param ficha the ficha to set
	 */
	public void setFicha(FichaAvaliacaoEad ficha) {
		this.ficha = ficha;
	}
	
}
