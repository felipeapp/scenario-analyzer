/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 * Movimento utilizado para manipular editais de pesquisa
 * 
 * @author ricardo
 */
public class MovimentoEditalPesquisa extends MovimentoCadastro {

	/** Informações do arquivo com o edital */
	private UploadedFile arquivoEdital;
	
	public MovimentoEditalPesquisa() {
		
	}

	public UploadedFile getArquivoEdital() {
		return arquivoEdital;
	}

	public void setArquivoEdital(UploadedFile arquivoEdital) {
		this.arquivoEdital = arquivoEdital;
	}
	
}
