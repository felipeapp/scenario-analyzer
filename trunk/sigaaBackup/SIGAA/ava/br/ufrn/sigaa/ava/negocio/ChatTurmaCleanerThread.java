package br.ufrn.sigaa.ava.negocio;

import java.io.File;
import java.util.Random;

import br.ufrn.arq.chat.ChatEngine;
import br.ufrn.arq.usuarios.UserOnlineMonitor;
import br.ufrn.comum.dominio.Sistema;

/**
 * Chat que serve para limpar mensagens antigas e usu�rios off-line.
 * 
 * @author Diego J�come
 * 
 */
public class ChatTurmaCleanerThread extends Thread {

	/** �nica inst�ncia da thread */
	private static ChatTurmaCleanerThread thread;

	/** Observa os usu�rios deslogados e os remove do chat */
	@Override
	public void run() {

		while (true) {

			Random r = new Random();
			int tempoDormida = r.nextInt(30 * 1000); // 30s
			try {
				Thread.sleep(tempoDormida);
				removeUsuariosDeslogados();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Limpa o chat da turma
	 */
	public static void clean() {
		if (thread == null) {
			thread = new ChatTurmaCleanerThread();
			thread.start();
		}		
	}
	
	/**
	 * Remove os usu�rios deslogados do sistema do chat 
	 */
	public void removeUsuariosDeslogados() {

		File[] chatsAtivos = new File(ChatEngine.getDirBaseUserOnline()).listFiles();

		if (chatsAtivos != null) {
			for (int i = 0; i < chatsAtivos.length; i++) {
	
				File chatDir = chatsAtivos[i];
				// testa se � diret�rio por garantia
				if (chatDir != null && chatDir.isDirectory()) {
	
					File[] usuarios = chatDir.listFiles();
					
					if (usuarios != null) {
						
						if (usuarios.length == 0)
							chatDir.delete();
						else 				
							for (int j = 0; j < usuarios.length; j++) 
								if ( usuarios[j] != null && !UserOnlineMonitor.isUserOnline(usuarios[j].getName(), Sistema.SIGAA) ) 
									usuarios[j].delete();
					}	
				}
			}	
		}
	}
	
}
