/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;


/**
 * Classe que movimenta objetos da classe ProcessoSeletivo
 * 
 * @author Ricardo Wendell
 *
 */
public class MovimentoProcessoSeletivo extends AbstractMovimentoAdapter {

	private ProcessoSeletivo processoSeletivo;

	// Arquivos opcionais que podem ser anexados
	private UploadedFile edital;
	
	private UploadedFile manualCandidato;
	
	public UploadedFile getEdital() {
		return this.edital;
	}

	public void setEdital(UploadedFile edital) {
		this.edital = edital;
	}

	public UploadedFile getManualCandidato() {
		return this.manualCandidato;
	}

	public void setManualCandidato(UploadedFile manualCandidato) {
		this.manualCandidato = manualCandidato;
	}

	public ProcessoSeletivo getProcessoSeletivo() {
		return this.processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}
	
}
