/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Criado em: 06/11/2007
 * 
 */
package br.ufrn.sigaa.arq.jsf;


/**
 * Managed-Bean usado para os relatórios que tem o formulário configurado
 * dinamicamente;
 *
 * @author Gleydson
 *
 */
public class AbstractControllerRelatorio extends SigaaAbstractController {

	private String tituloRelatorio;

	private String tituloFormulario = "Informe os critérios de busca";

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
