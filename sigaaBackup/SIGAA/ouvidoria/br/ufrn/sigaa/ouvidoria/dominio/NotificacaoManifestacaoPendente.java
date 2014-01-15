package br.ufrn.sigaa.ouvidoria.dominio;

import java.util.List;

import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe auxiliar que guarda os de uma notifica��o de manifesta��o pedentes para envio de email em lote.
 * 
 * @author Diego J�come
 * 
 */
public class NotificacaoManifestacaoPendente {

	/** Respons�vel da unidade no qual a manifesta��o foi encaminhada. */
	private Pessoa responsavel;
	
	/** Designado pelo respons�vel para responder a manifesta��o */
	private Pessoa designado;
	
	/** Lista de manifesta��es pendentes */
	private List<DelegacaoUsuarioResposta> manifestacoes;

	/** Se a notifica��o � para o designado. */
	private boolean avisoDesignado;
	
	public void setResponsavel(Pessoa responsavel) {
		this.responsavel = responsavel;
	}

	public Pessoa getResponsavel() {
		return responsavel;
	}

	public void setDesignado(Pessoa designado) {
		this.designado = designado;
	}

	public Pessoa getDesignado() {
		return designado;
	}

	public void setAvisoDesignado(boolean avisoDesignado) {
		this.avisoDesignado = avisoDesignado;
	}

	public boolean isAvisoDesignado() {
		return avisoDesignado;
	}

	public void setManifestacoes(List<DelegacaoUsuarioResposta> manifestacoes) {
		this.manifestacoes = manifestacoes;
	}

	public List<DelegacaoUsuarioResposta> getManifestacoes() {
		return manifestacoes;
	}

}
