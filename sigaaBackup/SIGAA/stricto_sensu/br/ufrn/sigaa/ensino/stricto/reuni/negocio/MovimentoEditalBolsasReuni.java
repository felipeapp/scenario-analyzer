/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.EditalBolsasReuni;

public class MovimentoEditalBolsasReuni extends AbstractMovimentoAdapter {

	EditalBolsasReuni edital;
	Arquivo arquivoEdital;

	public MovimentoEditalBolsasReuni(EditalBolsasReuni edital) {
		this.edital = edital;
	}
	
	public EditalBolsasReuni getEdital() {
		return edital;
	}
	public void setEdital(EditalBolsasReuni edital) {
		this.edital = edital;
	}
	public Arquivo getArquivoEdital() {
		return arquivoEdital;
	}
	public void setArquivoEdital(Arquivo arquivoEdital) {
		this.arquivoEdital = arquivoEdital;
	}
	
}
