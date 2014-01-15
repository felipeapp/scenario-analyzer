/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/08/2009
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.vestibular.dominio.JustificativaAusencia;

/** Movimento que encapsula os dados para o cadastro de uma justificativa de ausência de fiscal.
 * @author Édipo Elder F. Melo
 *
 */
public class MovimentoJustificativaFiscal extends AbstractMovimentoAdapter {

	/** Arquivo de justificativa de ausência. */
	private UploadedFile arquivo;
	
	/** Justificativa de ausência do fiscal. */
	private JustificativaAusencia justificativaAusencia;

	/** Retorna o arquivo de justificativa de ausência.
	 * @return Arquivo de justificativa de ausência.
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo de justificativa de ausência. 
	 * @param arquivo Arquivo de justificativa de ausência.
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Retorna a justificativa de ausência do fiscal. 
	 * @return Justificativa de ausência do fiscal. 
	 */
	public JustificativaAusencia getJustificativaAusencia() {
		return justificativaAusencia;
	}

	/** Seta a  justificativa de ausência do fiscal. 
	 * @param justificativaAusencia Justificativa de ausência do fiscal. 
	 */
	public void setJustificativaAusencia(JustificativaAusencia justificativaAusencia) {
		this.justificativaAusencia = justificativaAusencia;
	}

}
