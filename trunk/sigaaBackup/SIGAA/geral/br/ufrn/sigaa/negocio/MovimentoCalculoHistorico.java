package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/** Movimento utilizado para encapsular dados no c�lculo do hist�rico do discente.
 * @author �dipo Elder F. Melo
 *
 */
public class MovimentoCalculoHistorico extends AbstractMovimentoAdapter {

	/** Indica se deve recalcular os totais do curr�culo do discente. */
	private boolean recalculaCurriculo;
	
	/** Discente que ter� o hist�rico calculado. */
	private DiscenteAdapter discente;

	/** Indica se deve recalcular os totais do curr�culo do discente. 
	 * @return
	 */
	public boolean isRecalculaCurriculo() {
		return recalculaCurriculo;
	}

	/** Seta se deve recalcular os totais do curr�culo do discente. 
	 * @param recalculaCurriculo
	 */
	public void setRecalculaCurriculo(boolean recalculaCurriculo) {
		this.recalculaCurriculo = recalculaCurriculo;
	}

	/** Retorna o discente que ter� o hist�rico calculado.
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/** Seta o discente que ter� o hist�rico calculado.
	 * @param discente
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}
	

}
