/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/04/2009
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.vestibular.dominio.AvisoProcessoSeletivoVestibular;

/** Classe responsável pelo encapsulamento dos objetos para o 
 * cadastro de avisos nos Processos Seletivos Vestibulares.
 * @author Édipo Elder F. Melo
 *
 */
public class MovimentoAvisoProcessoSeletivoVestibular extends
		AbstractMovimentoAdapter {
	/** Arquivo do Edital do Processo Seletivo. */
	private UploadedFile arquivo;

	/** Aviso. */
	private AvisoProcessoSeletivoVestibular aviso;

	/** Retorna o arquivo do Edital do Processo Seletivo. 
	 * @return
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo do Edital do Processo Seletivo. 
	 * @param arquivo
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Retorna o aviso.
	 * @return
	 */
	public AvisoProcessoSeletivoVestibular getAviso() {
		return aviso;
	}

	/** Seta o aviso.
	 * @param aviso
	 */
	public void setAviso(AvisoProcessoSeletivoVestibular aviso) {
		this.aviso = aviso;
	}
}
