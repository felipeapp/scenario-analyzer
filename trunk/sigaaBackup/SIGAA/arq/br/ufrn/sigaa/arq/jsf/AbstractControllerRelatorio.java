/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Criado em: 06/11/2007
 * 
 */
package br.ufrn.sigaa.arq.jsf;


/**
 * Managed-Bean usado para os relat�rios que tem o formul�rio configurado
 * dinamicamente;
 *
 * @author Gleydson
 *
 */
public class AbstractControllerRelatorio extends SigaaAbstractController {

	private String tituloRelatorio;

	private String tituloFormulario = "Informe os crit�rios de busca";

	public String getTituloFormulario() {
		return tituloFormulario;
	}

	public void setTituloFormulario(String tituloFormulario) {
		this.tituloFormulario = tituloFormulario;
	}

	public String getTituloRelatorio() {
		return this.tituloRelatorio;
	}

	public void setTituloRelatorio(String tituloRelatorio) {
		this.tituloRelatorio = tituloRelatorio;
	}

}
