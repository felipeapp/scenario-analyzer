package br.ufrn.comum.wireless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class WirelessThread extends Thread {

	Socket	cliente;

	public WirelessThread(Socket socket) {
		cliente = socket;
	}

	@Override
	public void run() {

		try {
			PrintWriter out = new PrintWriter(new OutputStreamWriter(cliente.getOutputStream()), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

			String magic = in.readLine();
			if (magic.equals("WASSerieA")) {

				String comando = in.readLine();
				if (comando.equalsIgnoreCase("LIBERAR")) {

					String ip = in.readLine();
					WASLog.debug("Executando script para " + ip);
					Process process = Runtime.getRuntime().exec(
							"/etc/scripts/wireless-auth/libera-wireless.sh " + ip);
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String macAddress = reader.readLine();
					WASLog.debug("Chegou conexão de: " + ip + " - Mac: " + macAddress);

					out.println(macAddress);
					out.flush();
					TimeOutThread.addIP(ip);

					WASLog.debug("LIBERADO " + ip + " " + new Date());
					
					out.close();
					reader.close();

				}

				if (comando.equalsIgnoreCase("BLOQUEAR")) {
					String ip = in.readLine();
					Process process = Runtime.getRuntime().exec(
							"/etc/scripts/wireless-auth/bloqueia-wireless.sh " + ip);
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String output = reader.readLine();
					WASLog.debug("Bloqueado o IP " + ip + " com o MAC " + output);
					out.println(output);
					//out.println("BLOQUEADO");
					out.flush();

					out.close();
					reader.close();
					process.destroy();
					
					WASLog.debug("BLOQUEADO " + ip + " " + new Date());
				}

			}

			in.close();
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				cliente.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
