/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/04/2009
 *
 */
package br.ufrn.sigaa.ensino.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Action para gerar o histórico do discente a partir de outros sistemas.
 * 
 * Permite acesso ao histórico do discente recuperado a partir do SIGAA.
 * 
 * @author David Pereira
 *
 */
public class GerarHistoricoAcessoExternoAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		long matricula = Long.parseLong(req.getParameter("matricula"));
		String key = req.getParameter("key");
		int idToken = Integer.parseInt( req.getParameter("id") );
		int usuario = Integer.parseInt(req.getParameter("usuario"));
		int idRegistroEntrada = Integer.parseInt(req.getParameter("registroEntrada"));
		DiscenteDao dao = new DiscenteDao();
		
		try {
			TokenGenerator generator = getBean(req, "tokenGenerator");
			
			if (generator.isTokenValid(idToken, key)) {
				generator.invalidateToken(idToken);
				Usuario usr = dao.findByPrimaryKey(usuario, Usuario.class);
				RegistroEntrada registro = new RegistroEntrada();
				registro.setId(idRegistroEntrada);
				usr.setRegistroEntrada(registro);
				boolean logado = false;
				if (req.getSession().getAttribute("usuario") == null) {
					req.getSession().setAttribute("usuario", usr);
				} else {
					logado = true;
				}
				HistoricoMBean bean = getBean(req, "historico");
				Discente discente = dao.findByMatricula(matricula);
				bean.setDiscente(discente);
				bean.setUsuarioLogado(usr);
				bean.setAutenticado(true);		
				bean.selecionaDiscente(req, res);
				bean.setVerificando(false);			
				
				if (!logado)
					req.getSession().removeAttribute("usuario");
				
			} else {
				res.getWriter().println("Página expirada.");
			}

		} finally {
			dao.close();
		}
		return null;
	}
	
}
