/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 12/02/2007 
 *
 */
package br.ufrn.sigaa.portal.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.portal.dominio.PerfilDiscente;

/**
 * Classe utilizada para atualiza��es do perfil do docente
 *
 * @author ricardo
 *
 */
public class PerfilDiscenteMov extends AbstractMovimentoAdapter {

	/** Perfil a ser atualizado */
	PerfilDiscente perfilDiscente;

	/** Foto a ser armazenada */
	UploadedFile foto;

	public PerfilDiscenteMov() {

	}

	public PerfilDiscente getPerfilDiscente() {
		return perfilDiscente;
	}

	public void setPerfilDiscente(PerfilDiscente perfilDiscente) {
		this.perfilDiscente = perfilDiscente;
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

}
