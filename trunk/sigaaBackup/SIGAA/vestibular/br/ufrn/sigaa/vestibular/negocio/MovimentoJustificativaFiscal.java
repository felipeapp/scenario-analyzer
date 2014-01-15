/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/08/2009
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.vestibular.dominio.JustificativaAusencia;

/** Movimento que encapsula os dados para o cadastro de uma justificativa de aus�ncia de fiscal.
 * @author �dipo Elder F. Melo
 *
 */
public class MovimentoJustificativaFiscal extends AbstractMovimentoAdapter {

	/** Arquivo de justificativa de aus�ncia. */
	private UploadedFile arquivo;
	
	/** Justificativa de aus�ncia do fiscal. */
	private JustificativaAusencia justificativaAusencia;

	/** Retorna o arquivo de justificativa de aus�ncia.
	 * @return Arquivo de justificativa de aus�ncia.
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo de justificativa de aus�ncia. 
	 * @param arquivo Arquivo de justificativa de aus�ncia.
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Retorna a justificativa de aus�ncia do fiscal. 
	 * @return Justificativa de aus�ncia do fiscal. 
	 */
	public JustificativaAusencia getJustificativaAusencia() {
		return justificativaAusencia;
	}

	/** Seta a  justificativa de aus�ncia do fiscal. 
	 * @param justificativaAusencia Justificativa de aus�ncia do fiscal. 
	 */
	public void setJustificativaAusencia(JustificativaAusencia justificativaAusencia) {
		this.justificativaAusencia = justificativaAusencia;
	}

}
