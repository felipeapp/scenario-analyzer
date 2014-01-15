package br.ufrn.arq.chat;

import java.io.File;
import java.util.Random;

import br.ufrn.arq.usuarios.UserOnlineMonitor;
import br.ufrn.comum.dominio.Sistema;

/**
 * Chat que serve para limpar mensagens antigas e usuários off-line.
 * 
 * @author Gleydson
 * 
 */
public class ChatCleanerThread extends Thread {

	private static int MAX_MESSSAGE_TIME = 10 * 60 * 1000; // 10 minutos

	@Override
	public void run() {

		while (true) {

			Random r = new Random();
			int tempoDormida = r.nextInt(30 * 1000); // 30s
			try {
				Thread.sleep(tempoDormida);
				limpaMensagens();
				removeUsuariosDeslogados();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * limpa as mensagens que ultrapassaram o tempo máximo
	 */
	public void limpaMensagens() {

		File[] chatsAtivos = new File(ChatEngine.getRootDir() + ChatEngine.getDirBaseConversas())
				.listFiles();

		for (int i = 0; i < chatsAtivos.length; i++) {

			File chatDir = chatsAtivos[i];
			// testa se é diretório por garantia
			if (chatDir.isDirectory()) {

				File[] mensagens = chatDir.listFiles();
				for (int j = 0; j < mensagens.length; j++) {
					// se a mensagens já passou do tempo máximo apaga
					if (System.currentTimeMillis()
							- mensagens[j].lastModified() > MAX_MESSSAGE_TIME) {
						mensagens[j].delete();
					}
				}

			}
		}
	}

	/**
	 * Remove os usuários não ativos no chat
	 */
	public void removeUsuariosDeslogados() {

		File[] chatsAtivos = new File(ChatEngine.getRootDir()
				+ ChatEngine.getDirBaseUserOnline()).listFiles();

		for (int i = 0; i < chatsAtivos.length; i++) {

			File chatDir = chatsAtivos[i];
			// testa se é diretório por garantia
			if (chatDir.isDirectory()) {

				File[] usuarios = chatDir.listFiles();
				for (int j = 0; j < usuarios.length; j++) {
					if ( ! UserOnlineMonitor.isUserOnline(usuarios[j].getName(), Sistema.SIGAA) ) {
						usuarios[j].delete();
					}
				}
			}
		}

	}
	
	public static void main(String[] args) {
		new ChatCleanerThread().start();
	}

}
