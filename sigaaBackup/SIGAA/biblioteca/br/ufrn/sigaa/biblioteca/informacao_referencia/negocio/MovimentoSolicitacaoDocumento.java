/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/11/2010
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 * Movimento para acesso ao processador de a��es relacionadas aos servi�os que necessitam apresenta��o de documento.
 *
 * @author Felipe Rivas
 */
public class MovimentoSolicitacaoDocumento extends MovimentoCadastro {

	/**
	 * Arquivo que representa o trabalho digitalizado. 
	 */
	private UploadedFile arquivoTrabalho;

	public UploadedFile getArquivoTrabalho() {
		return arquivoTrabalho;
	}

	public void setArquivoTrabalho(UploadedFile arquivoTrabalho) {
		this.arquivoTrabalho = arquivoTrabalho;
	}

}
