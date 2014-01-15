/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/04/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import br.ufrn.comum.dominio.notificacoes.Destinatario;

/**
 * Classe que representa um destinat�rio de mensagens de orienta��o acad�mica.
 * 
 * @author bernardo
 *
 */
public class DestinatarioMensagemOrientacao extends Destinatario {
	
	public DestinatarioMensagemOrientacao(String nome, String email) {
		super(nome, email);
	}

	/** Orienta��o acad�mica que armazena os dados do docente e do discente. */
	private OrientacaoAcademica orientacaoAcademica;

	public OrientacaoAcademica getOrientacaoAcademica() {
		return orientacaoAcademica;
	}

	public void setOrientacaoAcademica(OrientacaoAcademica orientacaoAcademica) {
		this.orientacaoAcademica = orientacaoAcademica;
	}
}
