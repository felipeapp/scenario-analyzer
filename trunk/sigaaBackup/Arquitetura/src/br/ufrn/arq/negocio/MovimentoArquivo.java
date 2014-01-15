/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 24/09/2009
 */
package br.ufrn.arq.negocio;

import java.io.InputStream;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 * Classe utilizada para representar um movimento que deve passar um arquivo para o processador.
 * 
 * @author Mário Melo
 * @author Gleydson Lima
 *
 */
public class MovimentoArquivo extends MovimentoCadastro{
	
	private InputStream arquivo;

	private InputStream arquivo2;
	
	public InputStream getArquivo() {
		return arquivo;
	}

	public void setArquivo(InputStream arquivo) {
		this.arquivo = arquivo;
	}

	public InputStream getArquivo2() {
		return arquivo2;
	}

	public void setArquivo2(InputStream arquivo2) {
		this.arquivo2 = arquivo2;
	}

}
