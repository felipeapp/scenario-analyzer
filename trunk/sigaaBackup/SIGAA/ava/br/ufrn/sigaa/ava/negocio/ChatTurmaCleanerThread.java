package br.ufrn.sigaa.ava.negocio;

import java.io.File;
import java.util.Random;

import br.ufrn.arq.chat.ChatEngine;
import br.ufrn.arq.usuarios.UserOnlineMonitor;
import br.ufrn.comum.dominio.Sistema;

/**
 * Chat que serve para limpar mensagens antigas e usuários off-line.
 * 
 * @author Diego Jácome
 * 
 */
public class ChatTurmaCleanerThread extends Thread {

	/** Única instãncia da thread */
	private static ChatTurmaCleanerThread thread;

	/** Observa os usuários deslogados e os remove do chat */
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
	 * Remove os usuários deslogados do sistema do chat 
	 */
	public void removeUsuariosDeslogados() {

		File[] chatsAtivos = new File(ChatEngine.getDirBaseUserOnline()).listFiles();

		if (chatsAtivos != null) {
			for (int i = 0; i < chatsAtivos.length; i++) {
	
				File chatDir = chatsAtivos[i];
				// testa se é diretório por garantia
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
