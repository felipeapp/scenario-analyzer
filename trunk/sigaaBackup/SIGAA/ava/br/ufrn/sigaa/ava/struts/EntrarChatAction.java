/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 17/09/2008 
 */
package br.ufrn.sigaa.ava.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.chat.ChatEngine;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Action para entrar num chat de turma virtual passando apenas 
 * o id da turma, sem a necessidade de passar o passkey.
 *  
 * @author David Pereira
 *
 */
public class EntrarChatAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		int idChat = Integer.parseInt(req.getParameter("idchat"));
		int idUsuario = Integer.parseInt(req.getParameter("idusuario"));
		String origem = req.getParameter("origem"); // de onde o usuário originou a abertura da sala de chat (portal discente ou turma vitual, por exemplo)
		
		String passKey = ChatEngine.generatePassKey(getGenericDAO(req).findByPrimaryKey(idUsuario, Usuario.class), idChat);
		
		res.sendRedirect("/shared/EntrarChat?idchat=" + idChat + "&idusuario=" + idUsuario + "&passkey=" + passKey + "&chatName=" + req.getParameter("chatName") + 
				"&origem=" + origem);
		return null;
	}
	
}
