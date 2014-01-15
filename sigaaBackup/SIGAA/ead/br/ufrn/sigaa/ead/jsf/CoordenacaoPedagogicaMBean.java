
package br.ufrn.sigaa.ead.jsf;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPedagogica;

/**
 * Managed bean para operações de cadastro
 * de Coordenadores de Pólos de Educação a Distância
 * @author David Pereira
 *
 */
public class CoordenacaoPedagogicaMBean extends SigaaAbstractController<CoordenacaoPedagogica> {

	public CoordenacaoPedagogicaMBean() {
		obj = new CoordenacaoPedagogica();
	}
	
}
