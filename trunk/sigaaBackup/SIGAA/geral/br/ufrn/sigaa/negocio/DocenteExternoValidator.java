/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe com as valida��es sobre o cadastro de usu�rios para docentes externos
 * @author Victor Hugo
 *
 */
public class DocenteExternoValidator {

	/**
	 * Valida o cadastro de usu�rio do docente externo
	 * @param usuario
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaUsuarioDocente(Usuario usuario, ListaMensagens lista ) throws DAOException{
		
		if (usuario.getUnidade() == null || usuario.getUnidade().getId() == 0 ) {
			lista.addErro("� obrigat�rio informar o departamento.");
		}

		if (usuario.getEmail() == null || "".equals(usuario.getEmail().trim())) {
			lista.addErro("� obrigat�rio informar o e-mail.");
		}

		if (usuario.getLogin() == null || "".equals(usuario.getLogin().trim())) {
			lista.addErro("� obrigat�rio informar o login.");
		}
		
		if (usuario.getLogin().length() > 20) {
			lista.addErro("O login deve possui no m�ximo 20 caracteres");
		}

		if (usuario.getSenha() == null || "".equals(usuario.getSenha().trim())) {
			lista.addErro("� obrigat�rio informar a senha.");
		}

		if (usuario.getSenha() != null && usuario.getConfirmaSenha() != null && !usuario.getSenha().equals(usuario.getConfirmaSenha())) {
			lista.addErro("A senha n�o confere com a confirma��o.");
		}
		
		if(usuario.getLogin() != null || !"".equals(usuario.getLogin().trim())){
			UsuarioDao dao = DAOFactory.getInstance().getDAO(UsuarioDao.class);
			try {
				Usuario u = (Usuario) dao.findByLogin( usuario.getLogin() );
				if( u != null && u.getId() > 0 ){
					lista.addErro("J� existe um usu�rio no sistema com o login informado.");
				}
			} finally {
				if(dao != null)
					dao.close();
			}
		}
		
	}
	
}
