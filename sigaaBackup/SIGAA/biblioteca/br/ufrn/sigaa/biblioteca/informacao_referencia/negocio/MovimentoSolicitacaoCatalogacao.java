/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/11/2010
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.FichaCatalografica;

/**
 * Movimento para acesso ao processador de a��es relacionadas � Cataloga��o.
 *
 * @author Felipe Rivas
 */
public class MovimentoSolicitacaoCatalogacao extends MovimentoSolicitacaoDocumento {

	/**
	 * Arquivo que representa a ficha catalogr�fica digitalizada, caso ela tenha sido gerada por arquivo. 
	 */
	private UploadedFile arquivo;
	/**
	 * Ficha catalogr�fica gerada anteriormente, caso tenha havido uma.
	 */
	private FichaCatalografica fichaCatalograficaAnterior;
	/**
	 * Id da ficha digitalizada anteriormente, caso tenha havido uma.
	 */
	private Integer idFichaDigitalizadaAnterior;

	public UploadedFile getArquivoFichaDigitalizada() {
		return arquivo;
	}

	public void setArquivoFichaDigitalizada(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public FichaCatalografica getFichaCatalograficaAnterior() {
		return fichaCatalograficaAnterior;
	}

	public void setFichaCatalograficaAnterior(FichaCatalografica fichaCatalograficaAnterior) {
		this.fichaCatalograficaAnterior = fichaCatalograficaAnterior;
	}

	public Integer getIdFichaDigitalizadaAnterior() {
		return idFichaDigitalizadaAnterior;
	}

	public void setIdFichaDigitalizadaAnterior(Integer idFichaDigitalizadaAnterior) {
		this.idFichaDigitalizadaAnterior = idFichaDigitalizadaAnterior;
	}

}
