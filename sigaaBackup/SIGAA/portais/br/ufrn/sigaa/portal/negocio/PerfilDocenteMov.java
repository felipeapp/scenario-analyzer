/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/12/2006 
 *
 */
package br.ufrn.sigaa.portal.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.portal.dominio.PerfilServidor;

/**
 * Classe utilizada para atualizações do perfil do docente
 * 
 * @author ricardo
 * 
 */
public class PerfilDocenteMov extends AbstractMovimentoAdapter {

	/** Perfil a ser atualizado */
	PerfilServidor perfilDocente;

	/** Foto a ser armazenada */
	UploadedFile foto;

	public PerfilDocenteMov() {

	}

	public PerfilServidor getPerfilDocente() {
		return perfilDocente;
	}

	public void setPerfilDocente(PerfilServidor perfilDocente) {
		this.perfilDocente = perfilDocente;
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

}
