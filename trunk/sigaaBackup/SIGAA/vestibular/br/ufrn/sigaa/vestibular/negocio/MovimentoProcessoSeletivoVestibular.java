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
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Classe responsável pelo encapsulamento dos objetos para o cadastro de
 * Processos Seletivos Vestibulares.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class MovimentoProcessoSeletivoVestibular extends AbstractMovimentoAdapter {

	/** Arquivo do Edital do Processo Seletivo. */
	private UploadedFile edital;

	/** Arquivo do Manual do Candidato. */
	private UploadedFile manualCandidato;
	
	/** Processo Seletivo. */
	private ProcessoSeletivoVestibular processoSeletivo;
	
	/** Retorna o arquivo do Edital do Processo Seletivo. 
	 * @return 
	 */
	public UploadedFile getEdital() {
		return edital;
	}

	/** Seta o arquivo do Edital do Processo Seletivo.
	 * @param edital 
	 */
	public void setEdital(UploadedFile edital) {
		this.edital = edital;
	}

	/** Retorna o arquivo do Manual do Candidato. 
	 * @return
	 */
	public UploadedFile getManualCandidato() {
		return manualCandidato;
	}

	/** Seta o arquivo do Manual do Candidato.
	 * @param manualCandidato
	 */
	public void setManualCandidato(UploadedFile manualCandidato) {
		this.manualCandidato = manualCandidato;
	}

	/** Retorna o Processo Seletivo. 
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	/** Seta o Processo Seletivo.
	 * @param processoSeletivo
	 */
	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

}
