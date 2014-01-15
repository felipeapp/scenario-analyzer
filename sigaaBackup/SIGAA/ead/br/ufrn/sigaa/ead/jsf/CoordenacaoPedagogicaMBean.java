
package br.ufrn.sigaa.ead.jsf;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPedagogica;

/**
 * Managed bean para opera��es de cadastro
 * de Coordenadores de P�los de Educa��o a Dist�ncia
 * @author David Pereira
 *
 */
public class CoordenacaoPedagogicaMBean extends SigaaAbstractController<CoordenacaoPedagogica> {

	public CoordenacaoPedagogicaMBean() {
		obj = new CoordenacaoPedagogica();
	}
	
}
