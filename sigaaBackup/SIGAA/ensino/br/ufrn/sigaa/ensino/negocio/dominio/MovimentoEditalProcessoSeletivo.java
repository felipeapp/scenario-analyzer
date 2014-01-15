/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;


/**
 * Classe que movimenta objetos da classe EditalProcessoSeletivo
 * 
 * @author M�rio Rizzi
 *
 */
public class MovimentoEditalProcessoSeletivo extends AbstractMovimentoAdapter {

	/** Objeto principal a ser movimentado */
	private EditalProcessoSeletivo editalProcessoSeletivo;

	/** Arquivo contendo o edital do processo seletivo. */
	private UploadedFile edital;
	
	/** Arquivo contendo o manual do candidato. */
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

	public EditalProcessoSeletivo getEditalProcessoSeletivo() {
		return editalProcessoSeletivo;
	}

	public void setEditalProcessoSeletivo(
			EditalProcessoSeletivo editalProcessoSeletivo) {
		this.editalProcessoSeletivo = editalProcessoSeletivo;
	}
	
}
