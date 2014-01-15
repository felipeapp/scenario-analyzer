/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 30/03/2005
 *
 */
package br.ufrn.arq.caixa_postal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe que encapsula a lógica de JMS para acesso ao Gravador de Mensagens
 *
 * @author Gleydson Lima
 *
 */
public class ASyncMsgDelegate {

	Queue<ComandoMensagem> mensagens;

	private static ASyncMsgDelegate singleton;

	public static ASyncMsgDelegate getInstance() {

		try {
			if (singleton == null) {
				singleton = new ASyncMsgDelegate();
			}

			return singleton;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	private ASyncMsgDelegate() {
		mensagens = new LinkedList<ComandoMensagem>();
		new MessageProcessorThread(mensagens).start();
	}

	public void enviaMensagemUsuarios(Mensagem mensagem, UsuarioGeral user, Collection<UsuarioGeral> users) {

		ComandoMensagem cmdMensagem = new ComandoMensagem();
		cmdMensagem.setMensagem(mensagem);
		cmdMensagem.setSolicitante(user);
		cmdMensagem.setDestinatarios(users);

		synchronized (mensagens) {
			mensagens.add(cmdMensagem);
			mensagens.notifyAll();
		}

	}
}