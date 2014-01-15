/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;

/**
 * Movimento usado na indicação/substituição de coordenadores
 * 
 * @author leonardo
 *
 */
public class CoordenacaoCursoMov extends AbstractMovimentoAdapter {

	private CoordenacaoCurso coordenador;
	
	private CoordenacaoCurso coordenadorAntigo;
	
	public CoordenacaoCursoMov(){
		
	}

	public CoordenacaoCurso getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(CoordenacaoCurso coordenador) {
		this.coordenador = coordenador;
	}

	public CoordenacaoCurso getCoordenadorAntigo() {
		return coordenadorAntigo;
	}

	public void setCoordenadorAntigo(CoordenacaoCurso coordenadorAntigo) {
		this.coordenadorAntigo = coordenadorAntigo;
	}
	
	
}
