/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 21/03/2005
 *
 */
package br.ufrn.arq.caixa_postal;

import java.util.Queue;

/**
 * Processador de mensagens assíncrono
 *
 * @author Gleydson Lima
 *
 */
public class MessageProcessorThread extends Thread {

	Queue<ComandoMensagem> mensagens = null;

	public MessageProcessorThread(Queue<ComandoMensagem> q) {
		mensagens = q;
	}

	public void run() {

		while (true) {
			try {

				ComandoMensagem cmdMsg = null;
				synchronized (mensagens) {

					if (!mensagens.isEmpty()) {
						cmdMsg = mensagens.poll();
					} else {
						mensagens.wait();
					}
				}
				
				if (cmdMsg != null) {					
					cmdMsg.enviaMensagemUsuarios();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}