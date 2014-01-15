/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Oct 22, 2008
 *
 */
package br.ufrn.sigaa.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.sites.dominio.DocumentoSite;

/**
 *
 * @author Victor Hugo
 */
public class MovimentoDocumentoSite extends AbstractMovimentoAdapter {

	private DocumentoSite documentoSite;
	
	/**
	 * documento a ser cadastrado/alterado
	 */
	private UploadedFile arquivo;

	public DocumentoSite getDocumentoSite() {
		return documentoSite;
	}

	public void setDocumentoSite(DocumentoSite documentoSite) {
		this.documentoSite = documentoSite;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	
}
