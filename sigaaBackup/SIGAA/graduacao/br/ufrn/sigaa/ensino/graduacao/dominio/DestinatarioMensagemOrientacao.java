/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/04/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import br.ufrn.comum.dominio.notificacoes.Destinatario;

/**
 * Classe que representa um destinatário de mensagens de orientação acadêmica.
 * 
 * @author bernardo
 *
 */
public class DestinatarioMensagemOrientacao extends Destinatario {
	
	public DestinatarioMensagemOrientacao(String nome, String email) {
		super(nome, email);
	}

	/** Orientação acadêmica que armazena os dados do docente e do discente. */
	private OrientacaoAcademica orientacaoAcademica;

	public OrientacaoAcademica getOrientacaoAcademica() {
		return orientacaoAcademica;
	}

	public void setOrientacaoAcademica(OrientacaoAcademica orientacaoAcademica) {
		this.orientacaoAcademica = orientacaoAcademica;
	}
}
