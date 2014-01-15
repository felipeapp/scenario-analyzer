/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/04/2008
 */
package br.ufrn.arq.usuarios;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dao.SistemaDao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe responsável por manter a lista de usuários on-line no sistema de
 * arquivos compartilhado entre os servidores do sistema.
 * 
 * A existência desta classe dar-se devido a dificuldade de saber os ousuários
 * ativos em vários servidores do cluster.
 * 
 * @author Gleydson Lima
 * 
 */
public class UserOnlineMonitor extends Thread {

	private static String DIR =  ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.DIRETORIO_USUARIOS_ONLINE);

	private static long TIMEOUT = 30 * 60 * 1000;

	private static UserOnlineMonitor thread;

	/**
	 * Recupera o total de usuários on-line
	 * 
	 * @param sistema
	 * @return
	 */
	public static int getTotalUserOnline(int sistema) {
		String[] usuarios = getDirFile(sistema).list();
		if (usuarios == null) {
			return 0;
		} else {
			return usuarios.length;
		}
	}

	/**
	 * Recupera os usuários on-line.
	 * 
	 * @param sistema
	 * @return
	 */
	public static ArrayList<UsuarioGeral> getUsersOnline(int sistema) {
		ArrayList<UsuarioGeral> usersOnline = new ArrayList<UsuarioGeral>();
		File[] usuarios = getDirFile(sistema).listFiles();
		if (usuarios != null) {
			for (int i = 0; i < usuarios.length; i++) {
				String login = usuarios[i].getName();
				String regEntrada = "0";
				
				try {
					regEntrada = FileUtils.readFileToString(usuarios[i]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				UsuarioGeral user = new UsuarioGeral();
				user.setLogin(login);
				user.setUltimaOperacao(new Date(usuarios[i].lastModified()));
				if (!isEmpty(regEntrada)) {
					user.setRegistroEntrada(new RegistroEntrada());
					user.getRegistroEntrada().setId(Integer.parseInt(regEntrada));
				}
				usersOnline.add(user);
			}
		}
		return usersOnline;
	}

	/**
	 * Registra o login do usuário
	 * 
	 * @param login
	 */
	public static void logonUser(String login, int idRegistroEntrada, int sistema) {
		synchronized (UserOnlineMonitor.class) {
			if (thread == null) {
				thread = new UserOnlineMonitor();
				thread.start();
			}
		}
		updateUser(login, idRegistroEntrada, sistema);
	}

	/**
	 * Atualiza o acesso do usuário
	 * 
	 * @param login
	 * @param sistema2 
	 */
	public static void updateUser(String login, int idRegistroEntrada, int sistema) {

		File f = new File(getDir(sistema) + login);
		try {
			f.createNewFile();
			f.setLastModified(System.currentTimeMillis());
			FileUtils.writeStringToFile(f, String.valueOf(idRegistroEntrada));
		} catch (IOException e) {

		}
	}

	/**
	 * Desloga o usuário
	 * 
	 * @param login
	 */
	public static void logoffUser(String login, int sistema) {

		File f = new File(getDir(sistema) + login);
		f.delete();

	}

	/**
	 * Rotina que executa de tempos em tempos e remove os usuários inativos
	 */
	public void cleanLogffUsers(int sistema) {

		File dirFile = new File(getDir(sistema));
		File[] arquivos = dirFile.listFiles();
		if (arquivos != null) {
			for (int i = 0; i < arquivos.length; i++) {
				if (System.currentTimeMillis() - arquivos[i].lastModified() > TIMEOUT) {
					arquivos[i].delete();
				}
			}
		}

	}

	/**
	 * Testa se um usuário está on-line
	 * 
	 * @param login
	 * @return
	 */
	public static boolean isUserOnline(String login, int sistema) {
		File f = new File(getDir(sistema) + login);
		return f.exists();
	}

	private static String getDir(int sistema) {
		return DIR + sistema + "/";
	}

	private static File getDirFile(int sistema) {
		return new File(DIR + sistema + "/");
	}

	@Override
	public void run() {
		SistemaDao dao = new SistemaDao();
		List<Sistema> sistemas = null;
		
		try {
			sistemas = dao.buscarSistemasControleUsuariosOnline();
		} finally {
			dao.close();
		}
		
		while (true) {
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				
			}
			
			for (Sistema sistema : sistemas) {
				cleanLogffUsers(sistema.getId());
			}
		}
		
	}
}