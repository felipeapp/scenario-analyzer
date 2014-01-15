/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.ExtrapolarCredito;

public class MovimentoExtrapolarCredito extends AbstractMovimentoAdapter  {

	public static final int CADASTRAR = 1;
	public static final int EXCLUIR = 2;	
	
	private ExtrapolarCredito extrapolarCredito;
	private int acao;


	public int getAcao() {
		return acao;
	}

	public void setAcao(int acao) {
		this.acao = acao;
	}

	public ExtrapolarCredito getExtrapolarCredito() {
		return extrapolarCredito;
	}

	public void setExtrapolarCredito(ExtrapolarCredito extrapolarCredito) {
		this.extrapolarCredito = extrapolarCredito;
	}

}
