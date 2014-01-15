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
import br.ufrn.sigaa.sites.dominio.DetalhesSite;

/**
 * A classe que define o movimento dos dados envolvidos no caso de uso de alterar
 * os detalhes de um programa de pós-graduação na parte pública
 * @author Victor Hugo
 */
public class MovimentoDetalhesSite extends AbstractMovimentoAdapter {

	private DetalhesSite detalhesSite;

	/** Foto do detalhes do site a ser armazenada */
	private UploadedFile foto;

	/** Logo do detalhes do site a ser armazenada */
	private UploadedFile logo;
	
	/** Indica se deve excluir a foto */
	private boolean excluirFoto;
	
	/** Indica se deve excluir a logo */
	private boolean excluirLogo;

	public DetalhesSite getDetalhesSite() {
		return detalhesSite;
	}

	public void setDetalhesSite(DetalhesSite detalhesSite) {
		this.detalhesSite = detalhesSite;
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	public UploadedFile getLogo() {
		return logo;
	}

	public void setLogo(UploadedFile logo) {
		this.logo = logo;
	}

	public boolean isExcluirFoto() {
		return excluirFoto;
	}

	public void setExcluirFoto(boolean excluirFoto) {
		this.excluirFoto = excluirFoto;
	}

	public boolean isExcluirLogo() {
		return excluirLogo;
	}

	public void setExcluirLogo(boolean excluirLogo) {
		this.excluirLogo = excluirLogo;
	}

}
