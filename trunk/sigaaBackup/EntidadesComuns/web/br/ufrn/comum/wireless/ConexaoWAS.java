package br.ufrn.comum.wireless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Conexão com o WAS (Wireless Autentication Server)
 * 
 * @author Gleydson
 * 
 */
public class ConexaoWAS {

	public static final String	server	= "10.4.236.1";

	private static Connection	con		= null;

	public static Connection getConnection() throws ClassNotFoundException, SQLException {

		Class.forName("org.postgresql.Driver");

		if (con == null) {
			con = DriverManager.getConnection("jdbc:postgresql://bdsistemas.info.ufrn.br:5432/sistemas_comum",
					"wireless", "WASSerie@");
		}

		return con;

	}

	public static boolean autenticar(String ip, Integer idUsuario) throws IOException, SQLException {

		WASLog.debug("[Conexao] Autenticar: " + ip);
		Socket conexao = new Socket(server, 3636);
		BufferedReader in = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(conexao.getOutputStream()), true);

		// imprime magic number
		out.println("WASSerieA");
		out.flush();
		out.println("LIBERAR");
		out.flush();
		out.println(ip);
		out.flush();

		String status = in.readLine();
		WASLog.debug("[Conexao] Status: " + status);

		boolean autenticado = false;
		if (status != null && !status.equals("-1")) {
			insertLogon(ip, idUsuario, status);
			autenticado = true;
		} else {
			autenticado = false;
		}

		in.close();
		out.close();
		conexao.close();

		return autenticado;

	}

	public static boolean autenticarVisitanteExterno(String ip, Integer idUsuario) throws IOException, SQLException {
		return autenticar(ip, 0);
		//		return true;
	}

	public static boolean bloquear(String ip) throws IOException, SQLException {

		Socket conexao = new Socket(server, 3636);
		BufferedReader in = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(conexao.getOutputStream()), true);

		// imprime magic number
		out.println("WASSerieA");
		out.flush();
		out.println("BLOQUEAR");
		out.flush();
		out.println(ip);
		out.flush();

		String status = in.readLine();
		boolean autenticado = false;
		if (!status.equals("-1")) {
			TimeOutThread.removeIP(ip);
			try {
				insertBloqueio(ip, status);
			} catch (Exception e) {
				WASLog.error(e.getMessage());
				e.printStackTrace();
			}
			autenticado = true;
		} else {
			autenticado = false;
		}

		in.close();
		out.close();
		conexao.close();

		return autenticado;

	}

	public static void insertLogon(String ip, Integer idUsuario, String macAddress) throws SQLException {

		PreparedStatement pSt = null;
		try {
			pSt = getConnection()
					.prepareStatement(
							"INSERT INTO COMUM.LOGON_WIRELESS (ID, ID_USUARIO, IP, MAC, DATA) VALUES ((select nextval('comum.wireless_seq')),?,?,?,?)");
			pSt.setInt(1, idUsuario);
			pSt.setString(2, ip);
			pSt.setString(3, macAddress);
			pSt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
			pSt.executeUpdate();

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (pSt != null) pSt.close();
		}

	}

	public static void insertBloqueio(String ip, String returnFromScript) throws SQLException {

		PreparedStatement pSt = null;
		try {
			pSt = getConnection()
					.prepareStatement(
							"INSERT INTO COMUM.DESLOGON_WIRELESS (ID, IP, MAC, DATA) VALUES ((select nextval('comum.wireless_seq')),?,?,?)");
			pSt.setString(1, ip);
			pSt.setString(2, returnFromScript);
			pSt.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
			pSt.executeUpdate();

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (pSt != null) pSt.close();
		}

	}

}
