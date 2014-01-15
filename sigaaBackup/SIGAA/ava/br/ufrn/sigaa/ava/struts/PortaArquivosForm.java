/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.struts;

import br.ufrn.arq.web.struts.AbstractForm;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;

/**
 * Form que auxilia no cadastro de um arquivo em uma pasta do porta-arquivos.
 * 
 * @author davidpereira
 *
 */
public class PortaArquivosForm extends AbstractForm {

	private ArquivoUsuario arquivo = new ArquivoUsuario();
	
	private PastaArquivos pasta = new PastaArquivos();
	
	public PortaArquivosForm() {
		pasta.setPai(new PastaArquivos());
	}
	
	/**
	 * @return the arquivo
	 */
	public ArquivoUsuario getArquivo() {
		return arquivo;
	}

	/**
	 * @param arquivo the arquivo to set
	 */
	public void setArquivo(ArquivoUsuario arquivo) {
		this.arquivo = arquivo;
	}

	/**
	 * @return the pasta
	 */
	public PastaArquivos getPasta() {
		return pasta;
	}

	/**
	 * @param pasta the pasta to set
	 */
	public void setPasta(PastaArquivos pasta) {
		this.pasta = pasta;
	}

}
