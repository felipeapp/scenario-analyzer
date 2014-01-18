package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CoordenadorTutorIMD;

/**
 * 
 * Movimento respons�vel pela manipula��o do cadastro de Coordenador de Tutor do IMD
 * 
 * @author Rafael Barros
 *
 */

public class MovimentoCadastroCoordenadorTutorIMD extends AbstractMovimentoAdapter {

	/**Objeto que ser� persistido no processador**/
	private CoordenadorTutorIMD coordenadorTutorIMD;

	public CoordenadorTutorIMD getCoordenadorTutorIMD() {
		return coordenadorTutorIMD;
	}

	public void setCoordenadorTutorIMD(CoordenadorTutorIMD coordenadorTutorIMD) {
		this.coordenadorTutorIMD = coordenadorTutorIMD;
	}
	
}
