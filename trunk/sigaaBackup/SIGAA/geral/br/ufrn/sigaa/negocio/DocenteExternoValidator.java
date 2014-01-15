/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 8, 2007
 *
 */
package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe com as validações sobre o cadastro de usuários para docentes externos
 * @author Victor Hugo
 *
 */
public class DocenteExternoValidator {

	/**
	 * Valida o cadastro de usuário do docente externo
	 * @param usuario
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaUsuarioDocente(Usuario usuario, ListaMensagens lista ) throws DAOException{
		
		if (usuario.getUnidade() == null || usuario.getUnidade().getId() == 0 ) {
			lista.addErro("É obrigatório informar o departamento.");
		}

		if (usuario.getEmail() == null || "".equals(usuario.getEmail().trim())) {
			lista.addErro("É obrigatório informar o e-mail.");
		}

		if (usuario.getLogin() == null || "".equals(usuario.getLogin().trim())) {
			lista.addErro("É obrigatório informar o login.");
		}
		
		if (usuario.getLogin().length() > 20) {
			lista.addErro("O login deve possui no máximo 20 caracteres");
		}

		if (usuario.getSenha() == null || "".equals(usuario.getSenha().trim())) {
			lista.addErro("É obrigatório informar a senha.");
		}

		if (usuario.getSenha() != null && usuario.getConfirmaSenha() != null && !usuario.getSenha().equals(usuario.getConfirmaSenha())) {
			lista.addErro("A senha não confere com a confirmação.");
		}
		
		if(usuario.getLogin() != null || !"".equals(usuario.getLogin().trim())){
			UsuarioDao dao = DAOFactory.getInstance().getDAO(UsuarioDao.class);
			try {
				Usuario u = (Usuario) dao.findByLogin( usuario.getLogin() );
				if( u != null && u.getId() > 0 ){
					lista.addErro("Já existe um usuário no sistema com o login informado.");
				}
			} finally {
				if(dao != null)
					dao.close();
			}
		}
		
	}
	
}
