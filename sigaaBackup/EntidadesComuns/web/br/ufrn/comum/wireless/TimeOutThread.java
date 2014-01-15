package br.ufrn.comum.wireless;

import java.util.Hashtable;

public class TimeOutThread extends Thread {

	private static Hashtable<String, Long> alowedIPs = new Hashtable<String, Long>();

	private static final int REFRESH_PERIOD = 60 * 1000;

	private static final int TIMEOUT_PERIOD = 6 * 60 * 60 * 1000; // 1 min

	public TimeOutThread() {

	}

	public void run() {

		while (true) {
			try {
				Thread.sleep(REFRESH_PERIOD);
				WASLog.debug("Limpando IPs (" + alowedIPs.size() + " IPs ativos)");
				for (String ip : alowedIPs.keySet()) {

					Long tempo = alowedIPs.get(ip);
					WASLog.debug("Testando IP " + ip + " - Tempo: " + tempo + " / Timeout: " + TIMEOUT_PERIOD);
					if (System.currentTimeMillis() - tempo > TIMEOUT_PERIOD) {
						WASLog.debug("Bloqueando IP " + ip);
						ConexaoWAS.bloquear(ip);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public static void addIP(String ip) {
		WASLog.debug("[TimeOutThread] Adicionando IP: " + ip);
		alowedIPs.put(ip, System.currentTimeMillis());
	}

	public static void removeIP(String ip) {
		alowedIPs.remove(ip);
	}

}
