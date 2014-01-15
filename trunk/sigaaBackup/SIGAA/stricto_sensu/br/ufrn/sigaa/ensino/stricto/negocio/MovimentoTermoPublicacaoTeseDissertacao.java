/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 24/05/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.stricto.dominio.TermoAutorizacaoPublicacaoTeseDissertacao;

/**
 * Movimento para registrar o Termo de Publica��o de Tese ou Disserta��o Stricto-Sensu.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class MovimentoTermoPublicacaoTeseDissertacao extends AbstractMovimentoAdapter {
	
	/** Arquivo da Tese ou Disserta��o*/
	private UploadedFile arquivo;

	/** Termo de Autoriza��o de Publica��o de Tese ou Disserta��o */
	private TermoAutorizacaoPublicacaoTeseDissertacao termoAutorizacaoPublicacao;
	
	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public TermoAutorizacaoPublicacaoTeseDissertacao getTermoAutorizacaoPublicacao() {
		return termoAutorizacaoPublicacao;
	}

	public void setTermoAutorizacaoPublicacao(
			TermoAutorizacaoPublicacaoTeseDissertacao termoAutorizacaoPublicacao) {
		this.termoAutorizacaoPublicacao = termoAutorizacaoPublicacao;
	}	
	
}
