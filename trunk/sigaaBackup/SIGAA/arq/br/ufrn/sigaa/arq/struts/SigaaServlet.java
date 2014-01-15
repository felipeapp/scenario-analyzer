/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.arq.struts;

import java.rmi.RemoteException;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Servlet usada por classes de AJAX que não herdam o BaseAjaxServlet do
 * ajax-tags.
 *
 * @author Gleydson
 *
 */
public class SigaaServlet extends HttpServlet {

	/**
	 * Retorna o usuário logado
	 * @param req
	 * @return
	 */
	public UsuarioGeral getUsuarioLogado(HttpServletRequest req) {
		return (UsuarioGeral) req.getSession(false).getAttribute("usuario");
	}

	/**
	 * Nível de ensino corrente
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	public char getNivelEnsino(HttpServletRequest req) throws ArqException  {
		char nivel = SigaaSubsistemas.getNivelEnsino(getSubSistemaCorrente(req));
		if ( nivel == ' ') {
			Character nivelSession = (Character) req.getSession().getAttribute("nivel");
			if ( nivelSession == null ) {
				throw new ArqException("Nivel Necessita de definição para esta operação");
			}
			return nivelSession;
		}
		return nivel;
	}

	/**
	 * SubSistema que o usuário está usando
	 * @param req
	 * @return
	 */
	public SubSistema getSubSistemaCorrente(HttpServletRequest req) {
		return (SubSistema) req.getSession().getAttribute("subsistema");
	}

	/**
	 * Unidade do usuário logado
	 * @param req
	 * @return
	 */
	public Unidade getUnidadeUsuario(HttpServletRequest req) {
		return ((Usuario) req.getSession(false).getAttribute("usuario")).getUnidade();
	}

	public String findParametroLike(String likeParam, HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Enumeration<String> parametros = request.getParameterNames();
		while ( parametros.hasMoreElements() ) {
			String parameter = parametros.nextElement();
			if ( parameter.indexOf(likeParam) != -1) {
				return request.getParameter(parameter);
			}
		}
		return "";
	}

	/**
	 * Chama o modelo
	 * @param mov
	 * @param req
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object execute(Movimento mov, HttpServletRequest req) throws NegocioException, ArqException, RemoteException {
		return getUserDelegate(req).execute(mov, req);
	}

	/**
	 * Retorna o Delegate do Usuário e cria caso ainda não possua
	 *
	 * @param req
	 * @return
	 */
	protected FacadeDelegate getUserDelegate(HttpServletRequest req)
			throws ArqException {

		HttpSession session = req.getSession(false);
		if (session == null) {
			throw new ArqException("Sessão não ativada");
		} else {

			FacadeDelegate facade = (FacadeDelegate) session
					.getAttribute("userFacade");
			if (facade == null) {
				String jndiName = (String) getServletContext()
						.getAttribute("jndiName");
				facade = new FacadeDelegate(jndiName);
				session.setAttribute("userFacade", facade);
			}
			return facade;
		}
	}

	protected void atualizarObj(PersistDB obj, HttpServletRequest req, Comando comando) throws Exception {
		prepareMovimento(comando, req);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		mov.setUsuarioLogado(getUsuarioLogado(req));
		execute(mov, req);
	}
	protected void atualizarObj(PersistDB obj, HttpServletRequest req) throws Exception {
		atualizarObj(obj, req, ArqListaComando.ALTERAR);
	}

	public void prepareMovimento(Comando comando, HttpServletRequest req)
		throws ArqException, RemoteException, NegocioException {
		getUserDelegate(req).prepare(comando.getId(), req);

	}

}
