package br.ufrn.arq.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.ParametrosChat;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe que implementa o motor de funcionamento do Chat.
 * 
 * @author Gleydson
 *
 */
public class ChatEngine {


	public static String getRootDir() {
		return ParametroHelper.getInstance().getParametro(ParametrosChat.DIRETORIO_RAIZ_CHAT);
	}
	
	/**
	 * Diretório base para armazenamento das conversas
	 */
	public static String getDirBaseConversas() {
		
		String diretorio = getRootDir() + "/conversas";
		File f = new File ( diretorio );
		if ( ! f.exists() )  f.mkdir();
		
		return diretorio;
	}
	
	/**
	 * Diretório base para os usuários logados
	 */
	public static String getDirBaseUserOnline() {
		
		String diretorio = getRootDir() + "/users";
		File f = new File ( diretorio );
		if ( ! f.exists() )  f.mkdir();
		
		return diretorio;
		
	}
	
	/**
	 * Recupera o próximo id da mensagem
	 * @return
	 */
	private synchronized static long getNextIdMsg() {
		JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getComumDs());
		return jt.queryForLong("select nextval('chat_sequence')");
	}
	
	/**
	 * realiza a criação do Chat no sistema de arquivos
	 * @param idChat
	 */
	private static void createOrUpdateChat(int idChat) {
		//chatLocation.put(idChat, rootDir);
		
		File f = new File(getDirBaseConversas() + "/" + idChat);
		if ( f.exists() && f.isDirectory() ) {
			f.setLastModified(System.currentTimeMillis());
		} else {
			f.mkdirs();
		}
	}
	
	/**
	 * realiza a criação do Chat no sistema de arquivos
	 * @param idChat
	 */
	public synchronized  static void createChat(int idChat) {
		createOrUpdateChat(idChat);
		
		// verifica se o cleaner tá ativo
		//if ( cleaner == null ) {
		//	cleaner = new ChatCleanerThread();
		//	cleaner.start();
		//}
		
	}
	
	/**
	 * armazena a mensagem enviada ao chat através
	 * da serialização do objeto com o milisegundo atual.
	 * 
	 * @param idChat
	 * @param msg
	 * @throws IOException
	 */
	private  static void storeMessage(int idChat, ChatMessage msg) throws IOException {
		
		long idMsg = getNextIdMsg();
		String fileName = String.valueOf(getDirBaseConversas() + "/" + idChat + "/" + idMsg);
		FileOutputStream out = new FileOutputStream(fileName,true);
		ObjectOutputStream outObj = new ObjectOutputStream(out);
		msg.setIdMessage(idMsg);
		outObj.writeObject(msg);
		outObj.close();
	}
	
	/**
	 * Transforma um arquivo de mensagem em objeto novamente
	 *
	 * @param f
	 * @return
	 * @throws IOException
	 */
	private  static ChatMessage unmarshallMessage(File f) throws IOException {
		FileInputStream in = new FileInputStream(f);
		ObjectInputStream inObj = new ObjectInputStream(in);
		ChatMessage cMsg;
		try {
			cMsg = (ChatMessage) inObj.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} 
		inObj.close();
		return cMsg;
	}
	
	/**
	 * Constroi um objeto ChatMessage e o armazena
	 * @param idChat
	 * @param user
	 * @param message
	 * @param idFoto
	 * @throws IOException
	 */
	public  static void postMessage(int idChat, UsuarioChat user,  String message, Integer idFoto )  throws IOException {
		ChatMessage msg = new ChatMessage();
		msg.setIdChat(idChat);
		msg.setIdUsuario(user.getId());
		msg.setNomeUsuario(user.getNome());
		msg.setMensagem(message);
		msg.setIdFoto(idFoto);
		msg.setData(new Date());
		
		storeMessage(idChat, msg);
		updateUserTimestamp(user, idChat);
	}
	
	/**
	 * Recupera as mensagens do Chat que tem identificadores maior que a o lastIdMessage passado
	 * 
	 * @param idChat
	 * @param lastIdMessage
	 * @return
	 * @throws IOException
	 */
	public  static List<ChatMessage> recoveryMessages(int idChat, long lastIdMessage) throws IOException {
		
		File f = new File(getDirBaseConversas() + "/" + idChat + "");
		File[] msgs = f.listFiles();
		ArrayList<ChatMessage> msgsList = new ArrayList<ChatMessage>();
		if (msgs != null) {
			for (int i = 0; i < msgs.length; i++) {
				ChatMessage msg = unmarshallMessage(msgs[i]);
				if ( msg.getIdMessage() > lastIdMessage ) {
					msgsList.add(msg);
				}
			}
		}
		Collections.sort(msgsList);
		return msgsList;
	}
	
	public static String generatePassKey(UsuarioGeral user, int idChat) {
		return generatePassKey(usuarioGeralParaChat(user), idChat);
	}
	
	public static String generatePassKey(UsuarioChat user, int idChat) {
		String password = "M@v1d";
		String passKey = UFRNUtils.toSHA1Digest(idChat + user.getLogin() + password);
		
		return passKey;
		
	}
	
	public static String registerUser(UsuarioGeral user, int idChat) throws IOException {
		return registerUser(usuarioGeralParaChat(user), idChat);
	}
	
	public static String registrarMinistrante(UsuarioGeral user, int idChat) throws IOException {
		
		// Remove o ministrante atual
		List <UsuarioChat> us = listMembers(idChat);
		for (UsuarioChat u : us)
			if (u.isMinistrante()){
				u.setMinistrante(false);
				registerUser(u, idChat);
			}
		
		// Registra o novo usuário como ministrante do chat.
		UsuarioChat usuario = usuarioGeralParaChat(user);
		usuario.setMinistrante(true);
		
		return registerUser(usuario, idChat);
	}
	
	/**
	 * Registra o usuário no Chat.
	 * @param user
	 * @param idChat
	 * @throws IOException
	 */
	public static String registerUser(UsuarioChat user, int idChat) throws IOException {
		
		File f = new File(getDirBaseUserOnline() + "/" + idChat);
		f.mkdirs();
		File fUser = new File(f.getPath() + "/" + user.getLogin());
		
		FileChannel channel = new RandomAccessFile(fUser, "rw").getChannel();
		FileLock lock = null;

		try {
			lock = channel.tryLock();
			FileOutputStream fout = new FileOutputStream(fUser);
			ObjectOutputStream oOut = new ObjectOutputStream(fout);
			oOut.writeObject(user);
			oOut.flush();
			fout.close();
		} catch(OverlappingFileLockException e) {
			
		} finally {
			lock.release();
			channel.close();
		}
		
		return generatePassKey(user, idChat);
	}
	
	/**
	 * Retira o usuário no Chat.
	 * @param user
	 * @param idChat
	 * @throws IOException
	 */
	public  static void unregisterUser(UsuarioChat user, int idChat) throws IOException {
		
		File fUser = new File(getDirBaseUserOnline() + "/" + idChat + "/" + user.getLogin());
		fUser.delete();
	}
	
	/**
	 * Atualiza a data do arquivo do usuário. Utilizado para verificar se o usuário está ativo no chat.
	 * @param user
	 * @param idChat
	 * @throws IOException
	 */
	public  static void updateUserTimestamp(UsuarioChat user, int idChat) throws IOException {
		File fUser = new File(getDirBaseUserOnline() + "/" + idChat + "/" + user.getLogin());
		fUser.setLastModified(System.currentTimeMillis());
	}
	
	/**
	 * Recupera todo o conteúdo do chat em uma String para que possa ser possível
	 * visualizar o que foi discutido no chat posteriormente.
	 * @param idChat
	 * @return
	 * @throws IOException 
	 */
	public static String getFullChatContent(int idChat) throws IOException {
		return getChatContent(idChat, null, null);
	}
	
	/**
	 * Recupera todo o conteúdo do chat em uma String para que possa ser possível
	 * visualizar o que foi discutido no chat posteriormente.
	 * @param idChat
	 * @return
	 * @throws IOException 
	 */
	public static String getChatContent(int idChat, Date inicio, Date fim) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<ChatMessage> messages = recoveryMessages(idChat, 0);
		
		StringBuilder sb = new StringBuilder();
		for (ChatMessage message : messages) {
			
			if ((inicio == null && fim == null) || (message.getData().compareTo(inicio) >= 0 && message.getData().compareTo(fim) <= 0)) {
				sb.append("<strong>" + message.getNomeUsuario() + " diz (" + sdf.format(message.getData()) + "):</strong><br/>");
				sb.append(message.getMensagem() + "<br/><br/>");
			}
			
		}
		
		return sb.toString();
	}
	
	/**
	 * Retorna a lista de usuários que foram serializados em arquivos
	 * @param user
	 * @param idChat
	 * @throws IOException
	 */
	public  static List<UsuarioChat> listMembers(int idChat) throws IOException {
		
		File f = new File(getDirBaseUserOnline() + "/" + idChat );
		File[] usuarios = f.listFiles();
		
		if (usuarios != null) {
			List<UsuarioChat> users = new ArrayList<UsuarioChat>(usuarios.length);
			
			for (int i = 0; i < usuarios.length; i++) {
				FileInputStream fin = new FileInputStream(usuarios[i]);
				ObjectInputStream oin = new ObjectInputStream(fin);
				UsuarioChat user = null;
				try {
					user = (UsuarioChat) oin.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} 
				users.add(user);
				oin.close();
			}
			
			return users;
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * Retorna o total de usuários on-line no chat
	 * @param idChat
	 * @return
	 */
	public static int getTotalUserOnLine(int idChat) {
		
		File f = new File(getDirBaseUserOnline() + "/" + idChat );
		String users[] = f.list();
		if ( users != null ) {
			return users.length;
		} else {
			return 0;
		}
	}
	
	public static UsuarioChat usuarioGeralParaChat(UsuarioGeral usuario) {
		UsuarioChat chat = new UsuarioChat();
		chat.setId(usuario.getId());
		chat.setIdFoto(usuario.getIdFoto());
		chat.setLogin(usuario.getLogin());
		chat.setNome(usuario.getNome());
		return chat;
	}
	
	/**
	 * Verifica se o usuário está online no chat especificado
	 * 
	 * @param idUsuario identificador do usuário que está sendo procurado
	 * @param idChat identificador do chat a procurar pelo usuário
	 * @return
	 * @throws IOException
	 */
	public static boolean isUsuarioOnlineChat (int idUsuario, int idChat) throws IOException{
		List <UsuarioChat> us = ChatEngine.listMembers(idChat);
		
		boolean usuarioOnline = false;
		if (us != null && !us.isEmpty())
			for (UsuarioChat u : us){
				if (u.getId() == idUsuario){
					usuarioOnline = true;
					break;
				}
			}
		
		return usuarioOnline;
	}
}