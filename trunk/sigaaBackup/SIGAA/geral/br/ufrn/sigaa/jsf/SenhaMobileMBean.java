/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '20/06/2008'
 *
 */
package br.ufrn.sigaa.jsf;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.UsuarioMobileDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.UsuarioMobile;
import br.ufrn.sigaa.negocio.MovimentoSenhaMobile;

@Component("senhaMobileMBean") 
@Scope("session")
public class SenhaMobileMBean extends SigaaAbstractController<UsuarioMobile> {
	
	public String iniciar() throws HibernateException, DAOException {
		clear();
		return forward("/geral/discente/criar_senha_mobile.jsf");
	}
	
	private void clear() {
		obj = new UsuarioMobile();
	}

	
	public String cadastrar() throws ArqException, NegocioException {

		if ( validarSenhaSigaa() ) {

			UsuarioMobileDao usuarioMobileDao = getDAO(UsuarioMobileDao.class);
			Usuario usuario = getDAO(UsuarioDao.class).findByPrimaryKey(getUsuarioLogado().getId());
			
			obj = usuarioMobileDao.findUsuarioMobileByUsuarioLogado(getUsuarioLogado().getId());
			obj.setUsuario(usuario);
			
			String senha1 = getParameter("senha1");
			String senha2 = getParameter("senha2");
			
			if (!senha1.equals("") || !senha2.equals("")) {
				
				if ( !validarSenhasDigitadas(senha1, senha2) ) {
				
						if ( UFRNUtils.verificaEntropiaSenhaMobile(senha1, new StringBuffer())) {
							Usuario usuarioLogado = getUsuarioLogado();				
							MovimentoSenhaMobile mov = new MovimentoSenhaMobile();
							
							if ( obj.getId() == 0 ) {
								prepareMovimento(SigaaListaComando.CADASTRAR_SENHA_MOBILE);
								mov.setCodMovimento(SigaaListaComando.CADASTRAR_SENHA_MOBILE);
								
								obj = new UsuarioMobile();
								obj.setSenhaMobile( senha1 );
								obj.setUsuario(usuarioLogado);
								mov.setUsuarioMobile(obj);
							}
							else {
								prepareMovimento(SigaaListaComando.ALTERAR_SENHA_MOBILE);
								mov.setCodMovimento(SigaaListaComando.ALTERAR_SENHA_MOBILE);
								
								obj.setSenhaMobile( senha1 );
								obj.setUsuario(usuarioLogado);
								mov.setUsuarioMobile(obj);
							}
							
							execute(mov, getCurrentRequest());
							
							addMensagemInformation("Senha cadastrada com sucesso!");
							 return redirect("/sigaa/verPortalDiscente.do");
						}
						else {
							addMensagemWarning("Sua senha de acesso móvel não foi considerada segura - tente novamente.");
						}
				}
				return null;
			}
			else {
				addMensagemErro("As senhas não podem ser vazias!");
				return null;
			}		
		}
		else {
			addMensagemErro("A senha do sistema SIGAA está incorreta!");
		}
		return null;
	}

	public String cancelar() {
		return forward("/portais/discente/discente.jsp");
	}
	
	private boolean validarSenhaSigaa() throws ArqException {
		String senhaSigaa = getParameter("senhaSigaa");
		return UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), senhaSigaa);
	}
	
	private boolean validarSenhasDigitadas(String senha1, String senha2) {
		boolean temErros = false;
		
		if (!senha1.equals( senha2) ) {
			addMensagemErro("As senhas digitadas NÃO são iguais!");
			temErros = true;
		}
		if (senha1.length() < 6 || senha2.length() < 6) {
			addMensagemErro("A senha precisa ter 6 dígitos");
			temErros = true;
		}
		
		return temErros;
	}
	
}