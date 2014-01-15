/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Aug 26, 2008
 *
 */
package br.ufrn.sigaa.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.sites.dominio.NoticiaSite;

/**
 * Classe que define o movimento dos dados envolvidos no caso de uso de cadastrar
 * ou alterar notícias.
 * @author Victor Hugo
 */
public class MovimentoNoticiaSite extends AbstractMovimentoAdapter {

	private NoticiaSite noticiaSite;

	/** Foto da notícia a ser armazenada */
	private UploadedFile foto;
	
	/** Foto do notícia a ser armazenada */
	private UploadedFile arquivo;

	public NoticiaSite getNoticiaSite() {
		return noticiaSite;
	}

	public void setNoticiaSite(NoticiaSite noticiaSite) {
		this.noticiaSite = noticiaSite;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

}
