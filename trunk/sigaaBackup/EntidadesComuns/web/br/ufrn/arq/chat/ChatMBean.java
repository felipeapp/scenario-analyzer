package br.ufrn.arq.chat;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Controlador do Chat
 * 
 * @author Gleydson
 *
 */
public class ChatMBean extends AbstractController {

	/**
	 * Identificador do Chat
	 */
	private Integer idChat;
	
	private Long lastMessage;
	
	public void sendMessage(ActionEvent evt) {
		
		// recupera a mensagem
		String message = getCurrentRequest().getParameter("msg");
		// verifica se o usuário está logado
		UsuarioGeral user = (UsuarioGeral) getCurrentSession().getAttribute("usuario");
		if ( user == null ) {
			return;
		}
		
		try {
			ChatEngine.postMessage(idChat, ChatEngine.usuarioGeralParaChat(user), message, user.getIdFoto());
		} catch (IOException e) {
			addMensagemErro("Erro ao postar no Chat");
		}
		
	}
	
	public String getMembros() {
		
		
		/*
		<li class="membro">
			<img src="images/foto-default.jpg" />
			<h4>Bruno Sielly Jales Costa</h4>
			<p class="login">bruno_gnr</p>
			<p class="curso">Engenharia de Computação (200418211)</p>
			<ul class="acoes">
				<li><a href="#" alt="Escrever mensagem" class="escrever-mensagem">Escrever Mensagem</a></li>
			</ul>
		</li>
		*/
		return null;
	}
	
	/**
	 * Retorna a lista das mensagens dado o último id
	 * @return
	 * @throws IOException 
	 */
	public String getMessages() throws IOException {
		
		StringBuffer buffer = new StringBuffer(1000);

		/**
		 * Recupera as mensagens do chat tendo a ultima mensagem como a base
		 */
		List<ChatMessage> msgs = ChatEngine.recoveryMessages(idChat, lastMessage);
		/*
		 * <li>
				<h3><span class="hora">(12:43)</span> Bruno Sielly Jales Costa <span class="diz">diz:</span></h3>
				<p class="texto">Lorm ipsum dolor sit amet, consectetuer adipiscing elit. Vivamus a mi id tortor congue euismod. Aenean posuere. Aenean auctor commodo sem. Donec tempor. Etiam consectetuer ornare elit. Quisque sit amet erat sit amet pede ultrices condimentum morbi scelerisque diam in augue.</p>
			</li>
		 */
		
		for (ChatMessage msg : msgs) {
			buffer.append("<li>");
			buffer.append("<h3><span class='hora'>(" + 
					Formatador.getInstance().formatarHora(new Date(msg.getIdChat())) + ") </span> "
							+ msg.getNomeUsuario() + "<span class='diz'>diz:</span></h3>" +
							"<p class='texto'>" + msg.getMensagem() + "</p>");
			buffer.append("</li>");
			
			lastMessage =  msg.getIdMessage();
		}

		return buffer.toString();
	}
	
}
