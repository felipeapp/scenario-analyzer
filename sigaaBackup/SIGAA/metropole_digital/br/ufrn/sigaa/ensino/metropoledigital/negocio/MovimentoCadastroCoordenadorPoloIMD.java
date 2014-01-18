package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CoordenadorPoloIMD;

/**
 * 
 * Movimento respons�vel pela manipula��o do cadastro de Coordenador de P�lo do IMD
 * 
 * @author Rafael Barros
 *
 */

public class MovimentoCadastroCoordenadorPoloIMD extends AbstractMovimentoAdapter {

	/**Objeto que ser� persistido no processador**/
	private CoordenadorPoloIMD coordenadorPoloIMD;

	public CoordenadorPoloIMD getCoordenadorPoloIMD() {
		return coordenadorPoloIMD;
	}

	public void setCoordenadorPoloIMD(CoordenadorPoloIMD coordenadorPoloIMD) {
		this.coordenadorPoloIMD = coordenadorPoloIMD;
	}
	
}
