/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Apr 4, 2007
 *
 */
package br.ufrn.sigaa.eleicao.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 * 
 * @author Victor Hugo
 *
 */
public class CandidatoMov extends MovimentoCadastro {

	private UploadedFile foto;

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}
	
	
	
}
