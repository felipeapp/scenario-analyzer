package br.ufrn.sigaa.ouvidoria.dominio;

import java.util.List;

import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe auxiliar que guarda os de uma notificação de manifestação pedentes para envio de email em lote.
 * 
 * @author Diego Jácome
 * 
 */
public class NotificacaoManifestacaoPendente {

	/** Responsável da unidade no qual a manifestação foi encaminhada. */
	private Pessoa responsavel;
	
	/** Designado pelo responsável para responder a manifestação */
	private Pessoa designado;
	
	/** Lista de manifestações pendentes */
	private List<DelegacaoUsuarioResposta> manifestacoes;

	/** Se a notificação é para o designado. */
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
