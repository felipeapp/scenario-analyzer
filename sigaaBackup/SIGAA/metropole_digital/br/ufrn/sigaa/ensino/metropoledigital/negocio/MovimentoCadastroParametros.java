package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ParametrosAcademicosIMD;

/**
 * Movimento utilizado no cadastro dos parâmetros acadêmicos do IMD.
 * 
 * @author Rafael Silva
 *
 */
public class MovimentoCadastroParametros extends AbstractMovimentoAdapter{
	/**Parâmetros Antigos*/
	private ParametrosAcademicosIMD parametrosAntigos;
	/**Parâmetros Novos*/
	private ParametrosAcademicosIMD parametrosNovos;

	//GETTERS AND SETTERS
	public ParametrosAcademicosIMD getParametrosAntigos() {
		return parametrosAntigos;
	}
	public void setParametrosAntigos(ParametrosAcademicosIMD parametrosAntigos) {
		this.parametrosAntigos = parametrosAntigos;
	}
	public ParametrosAcademicosIMD getParametrosNovos() {
		return parametrosNovos;
	}
	public void setParametrosNovos(ParametrosAcademicosIMD parametrosNovos) {
		this.parametrosNovos = parametrosNovos;
	}
}
