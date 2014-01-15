package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/** Movimento utilizado para encapsular dados no cálculo do histórico do discente.
 * @author Édipo Elder F. Melo
 *
 */
public class MovimentoCalculoHistorico extends AbstractMovimentoAdapter {

	/** Indica se deve recalcular os totais do currículo do discente. */
	private boolean recalculaCurriculo;
	
	/** Discente que terá o histórico calculado. */
	private DiscenteAdapter discente;

	/** Indica se deve recalcular os totais do currículo do discente. 
	 * @return
	 */
	public boolean isRecalculaCurriculo() {
		return recalculaCurriculo;
	}

	/** Seta se deve recalcular os totais do currículo do discente. 
	 * @param recalculaCurriculo
	 */
	public void setRecalculaCurriculo(boolean recalculaCurriculo) {
		this.recalculaCurriculo = recalculaCurriculo;
	}

	/** Retorna o discente que terá o histórico calculado.
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/** Seta o discente que terá o histórico calculado.
	 * @param discente
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}
	

}
