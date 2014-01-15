/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 06/11/2007
*
*/

package br.ufrn.sigaa.arq.struts;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.jsf.SistemaMBean;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Action responsável pelo processo de logon no SIPAC a partir do SIGAA
 *
 * @author Gleydson
 *
 */
public class LogonSistemasAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping map, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		if ( req.getParameter("sistema").equalsIgnoreCase("sipac") ) {
			logonSipac(map,form,req,res);
		} else if ( req.getParameter("sistema").equalsIgnoreCase("sigrh") ) {
			logonSigrh(map,form,req,res);
		} else if (req.getParameter("sistema").equalsIgnoreCase("sigadmin")) {
			logonSigadmin(req, res);
		} else if (req.getParameter("sistema").equalsIgnoreCase("sigpp")) {
			logonSigpp(req, res);
		}
		
		return null;
	}

	public void logonSigadmin(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String linkSigadmin = ((!ValidatorUtil.isEmpty(RepositorioDadosInstitucionais.getLinkSigadmin())) ? RepositorioDadosInstitucionais.getLinkSigadmin() : req.getRequestURL().toString().split(req.getRequestURI())[0]);
		
		// Verificando se o SIGAdmin está ativo e acessível.
		PassaporteLogon passaporte = new PassaporteLogon();
		passaporte.setSistemaOrigem(Sistema.SIGAA);
		passaporte.setSistemaAlvo(Sistema.SIGADMIN);
		passaporte.setHora(new Date());
		passaporte.setLogin(getUsuarioLogado(req).getLogin());
		passaporte.setIdUsuario(getUsuarioLogado(req).getId());
		passaporte.setValidade(System.currentTimeMillis() + 30000);
		passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);

		prepareMovimento(ArqListaComando.CADASTRAR_PASSAPORTE, req);

		execute(passaporte, req);

		String url = linkSigadmin + "/admin/logar.jsf?login=" + passaporte.getLogin() + "&passaporte=true";

		res.sendRedirect(url);
	}
	
	/**
	 * Realizando logon no SIPAC para a leitura de memorandos eletrônicos.
	 * Verifica se o sistema está ativo e acessível.
	 * 
	 * @author Weinberg Souza
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public boolean logonSipac(HttpServletRequest req, HttpServletResponse res, boolean portalSigaa) throws Exception {
		
		boolean sucessoLogon = false;
		
		String linkSipac = ((!ValidatorUtil.isEmpty(RepositorioDadosInstitucionais.getLinkSipac())) ? RepositorioDadosInstitucionais.getLinkSipac() : req.getRequestURL().toString().split(req.getRequestURI())[0]);
		
		// Verificando se o SIPAC está ativo e acessível.
		if(new SistemaMBean().verificaAtivoAcessivel(Sistema.SIPAC, linkSipac)) {
			
			PassaporteLogon passaporte = new PassaporteLogon();
			Usuario user = (Usuario) getUsuarioLogado(req);
			passaporte.setLogin(user.getLogin());
			passaporte.setIdUsuario(user.getId());
			passaporte.setSistemaAlvo(Sistema.SIPAC);
			passaporte.setSistemaOrigem(Sistema.SIGAA);
			long tempo = System.currentTimeMillis();
			tempo += 30000; // 30s de timeout
			passaporte.setValidade(tempo);
			passaporte.setHora(new Date());
			passaporte.setIdUnidade(user.getUnidade().getId());
			
			passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);
			
			prepareMovimento(ArqListaComando.CADASTRAR_PASSAPORTE, req);
			
			execute(passaporte,req);
			
			String url = linkSipac + "/sipac/logon.do?login="+passaporte.getLogin() + "&passaporte=true&memorandos=true";
			
			if ( portalSigaa )
				url += "&painelMemorandos=true";
			
			if (req.getParameter("url") != null) {
				url += ("&redirect=true&url=" + req.getParameter("url"));
			}
		
			res.sendRedirect(url);
			
			sucessoLogon = true;			
		}
		
		return sucessoLogon;
	}

	
	/**
	 * Realizando logon no SIPAC sem a necessidade de leitura dos memorandos eletrônicos.
	 * Verifica se o sistema está ativo e acessível.
	 * @param map
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward logonSipac(ActionMapping map, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		PassaporteLogon passaporte = new PassaporteLogon();
		Usuario user = (Usuario) getUsuarioLogado(req);
		passaporte.setLogin(user.getLogin());
		passaporte.setIdUsuario(user.getId());
		passaporte.setSistemaAlvo(Sistema.SIPAC);
		passaporte.setSistemaOrigem(Sistema.SIGAA);
		long tempo = System.currentTimeMillis();
		tempo = tempo + 30000; // 30s de timeout
		passaporte.setValidade(tempo);
		passaporte.setHora(new Date());
		passaporte.setIdUnidade(user.getUnidade().getId());

		passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);

		prepareMovimento(ArqListaComando.CADASTRAR_PASSAPORTE, req);

		execute(passaporte,req);

		String url = RepositorioDadosInstitucionais.getLinkSipac() + "/sipac/logon.do?login="+passaporte.getLogin() + "&passaporte=true";
		
		if (req.getParameter("url") != null) {
			url += ("&redirect=true&url=" + req.getParameter("url"));
		}
		
		res.sendRedirect(url);

		return null;
	}

	/**
	 * Realizando logon no SIGRH por meio de passaporte.
	 * Verifica se o sistema está ativo e acessível.
	 * @param map
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward logonSigrh(ActionMapping map, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {


		PassaporteLogon passaporte = new PassaporteLogon();
		Usuario user = (Usuario) getUsuarioLogado(req);
		passaporte.setLogin(user.getLogin());
		passaporte.setIdUsuario(user.getId());
		passaporte.setSistemaAlvo(Sistema.SIGRH);
		passaporte.setSistemaOrigem(Sistema.SIGAA);
		long tempo = System.currentTimeMillis();
		tempo = tempo + 30000; // 30s de timeout
		passaporte.setValidade(tempo);
		passaporte.setHora(new Date());

		passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);

		prepareMovimento(ArqListaComando.CADASTRAR_PASSAPORTE, req);

		execute(passaporte,req);

		String url = RepositorioDadosInstitucionais.getLinkSigrh() + "/sigrh/Logon?login=" +passaporte.getLogin() + "&idUsuario=" + user.getId();

		if (req.getParameter("url") != null) {
			url += ("&url=" + req.getParameter("url"));
		}
		
		res.sendRedirect(url);
		
		return null;
	}
	
	
	/**
	 * Realizando logon no SIGPP por meio de passaporte.
	 * Verifica se o sistema está ativo e acessível.
	 * @param map
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward logonSigpp(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Usuario user = (Usuario) getUsuarioLogado(req);
		String url = req.getParameter("url");
		
		PassaporteLogon passaporte = new PassaporteLogon();
		passaporte.setLogin(user.getLogin());
		passaporte.setIdUsuario(user.getId());
		passaporte.setSistemaAlvo(Sistema.SIGPP);
		passaporte.setSistemaOrigem(Sistema.SIGAA);
		long tempo = System.currentTimeMillis();
		tempo = tempo + 30000; // 30s de timeout
		passaporte.setValidade(tempo);
		passaporte.setHora(new Date());

		passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);

		try {
			prepareMovimento(ArqListaComando.CADASTRAR_PASSAPORTE, req);
			
			execute(passaporte,req);
		} catch (ArqException e) {
			e.printStackTrace();
		} catch (NegocioException e) {
			e.printStackTrace();
		}
		
		if (url != null)
			res.sendRedirect(RepositorioDadosInstitucionais.getLinkSigpp() + "/sigpp/Logon?login=" +passaporte.getLogin() + "&passaporte=true&idUsuario=" + user.getId()	
					+ "&urlRedirect=" + url);
		else
			res.sendRedirect(RepositorioDadosInstitucionais.getLinkSigpp() + "/sigpp/Logon?login=" +passaporte.getLogin() + "&passaporte=true&idUsuario=" + user.getId());	
		
		return null;
	}
}
