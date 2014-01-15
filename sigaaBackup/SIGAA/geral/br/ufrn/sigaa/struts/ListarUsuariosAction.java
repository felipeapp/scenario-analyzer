/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/02/2007'
 *
 */
package br.ufrn.sigaa.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.ava.dao.UsuarioTurmaDao;
import br.ufrn.sigaa.form.UsuarioForm;

/**
 * Esta classe é uma action que direciona para uma visualização uma coleção de
 * usuários.
 *
 * @author Raphaela Galhardo
 *
 */
public class ListarUsuariosAction extends SigaaAbstractAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {


		UsuarioForm usuarioForm = (UsuarioForm) form;
		UsuarioTurmaDao usuarioDAO = getDAO(UsuarioTurmaDao.class, req);
		try {
			List <UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();

			if (!getErrors(req).isEmpty())
				usuarioForm.setAcao(0);

				switch (usuarioForm.getTipoBusca()) {
					case UsuarioForm.FIND_BY_LOGIN:
						String login = usuarioForm.getUsuario().getLogin();
						
						if (login != null && !login.trim().equals("")){
							usuarios = usuarioDAO.findByNomeLogin (null, login);
						}
					break;
	
					// Busca por nome, selecionando unidade e papeis
					case UsuarioForm.FIND_BY_NOME:
						String nome = usuarioForm.getNomeBusca(); 
						
						if (nome != null && !nome.trim().equals(""))
							usuarios = usuarioDAO.findByNomeLogin(nome, null);
					break;
				}
				
				if (usuarios == null)
					usuarios = new ArrayList<UsuarioGeral>();

				if (usuarios.isEmpty())
					addErro("mensagem.usuario.busca.naoEncontrado", req);

			req.setAttribute("usuarios", usuarios);

			return mapping.findForward("listarUsuarios");
		} finally {
			usuarioDAO.close();
		}
	}

}