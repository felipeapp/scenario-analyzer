/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/02/2009
 */
package br.ufrn.arq.util;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;

/**
 * Classe utilitária para mostrar avisos
 * 
 * @author Gleydson Lima
 *
 */
public class AvisoHelper {

	/**
	 * Insere uma mensagem de aviso em vários sistemas
	 */
	public static void insereMensagemAviso(Integer[] sistemas, String mensagem) {
		for (Integer sistema : sistemas) {
			insereMensagemAviso(sistema, mensagem);
		}
	}
	
	/**
	 * Insere uma mensagem de aviso em um sistema
	 */
	public static void insereMensagemAviso(int sistema, String mensagem) {
		try {
			if (mensagem != null) {
				FileOutputStream out = new FileOutputStream(getDirShared() + sistema);
				BufferedWriter bufOut = new BufferedWriter(new OutputStreamWriter(out));
				bufOut.write(mensagem);

				bufOut.close();
				out.close();
			} else {
				File file = new File(getDirShared() + sistema);
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Verifica se um sistema possui mensagem de aviso e retorna
	 * a mensagem. Caso nao possua, retorna null.
	 */
	public static String verificaMensagemAviso(int sistema) {
		try {
			File file = new File(getDirShared() + sistema);

			if (file.exists()) {

				FileInputStream in = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(in);
				BufferedReader bufIn = new BufferedReader(reader);
				String mensagem = "";
				String linha = null;
				while ((linha = bufIn.readLine()) != null) {
					mensagem += linha;
				}
				bufIn.close();
				reader.close();
				in.close();

				if (mensagem != null && mensagem.equals("")) {
					mensagem = null;
				}

				return mensagem;

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private static String getDirShared() {
		String dirShared = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.CAMINHO_RECURSOS);
		if (!dirShared.endsWith("/")) {
			dirShared += "/";
		}
		return dirShared;
	}

	/**
	 * Remove uma mensagem de aviso dos sistemas passados
	 * como parâmetro.
	 */
	public static void removeMensagemAviso(Integer[] sistemas) {
		for (Integer sistema : sistemas) {
			File file = new File(getDirShared() + sistema);
			file.delete();
		}
	}

	/**
	 * Exporta as mensagens de aviso inseridas anteriormente para uma
	 * variável na sessão do usuário. 
	 */
	public static void exportaMensagensAvisoParaRequest(int sistema, HttpServletRequest request) {
		String mensagemNotificacaoUsuarios = AvisoHelper.verificaMensagemAviso(sistema);
		if (!isEmpty(mensagemNotificacaoUsuarios)) {
			ListaMensagens avisos = (ListaMensagens) request.getSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
			if (avisos == null)
				avisos = new ListaMensagens();
			if (!avisos.containsWarning(mensagemNotificacaoUsuarios))
				avisos.addWarning(mensagemNotificacaoUsuarios);
			request.getSession().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, avisos);
		}
	}

}
