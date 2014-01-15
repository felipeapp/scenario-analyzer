package br.ufrn.comum.wireless;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerWireless {

	public static void main(String[] args) throws IOException {

		TimeOutThread timeout = new TimeOutThread();
		timeout.start();
		
		ServerSocket server = new ServerSocket(3636);
		WASLog.debug("Wireless Autentication Server");
		WASLog.debug("WAS Iniciado");
		while ( true ) {
			Socket socket = server.accept();
			WASLog.debug("Conexão de " + socket);
			new WirelessThread(socket).start();
		}


	}
}
