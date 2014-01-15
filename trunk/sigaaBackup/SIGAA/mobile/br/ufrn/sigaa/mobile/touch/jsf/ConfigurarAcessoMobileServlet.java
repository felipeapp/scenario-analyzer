/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/03/2012
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.FacesContextUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.struts.SigaaServlet;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Servlet utilizada para configurar o acesso ao Portal Web Mobile, inicializando parâmetros necessários.
 * 
 * @author Renan de Oliveira
 * 
 */
public class ConfigurarAcessoMobileServlet extends SigaaServlet {

	/**
	 * Realiza as configurações necessárias para acessar o Portal Web Mobile corretamente.
	 * 
	 * Chamado pelo aplicativo do SIGAA Android.
	 * 
	 * @throws ServletException, IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Usuario usuario = (Usuario) getUsuarioLogado(request);
		
		if (usuario == null) {
			response.getWriter().print("Usuário não autenticado!");
			response.getWriter().flush();
		}
		
		if (usuario.getDiscente() != null) {			
			try {
				DiscenteAdapter discente = usuario.getDiscenteAtivo();
				usuario.setNivelEnsino(discente.getNivel());
				request.getSession().setAttribute("nivel", discente.getNivel());
			
				ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(discente);
				request.getSession().setAttribute(SigaaAbstractController.PARAMETROS_SESSAO, param);				
			
				PortalDiscenteTouchMBean disBean = getMBean("portalDiscenteTouch", request, response);				
				disBean.setDiscente(usuario.getDiscente().getDiscente());

				response.sendRedirect("/sigaa/mobile/touch/menu.jsf");
			} catch (DAOException e){
				response.getWriter().print("Ocorreu um erro e a operação não pode ser realizada.");
				response.getWriter().flush();
				e.printStackTrace();
			}
			
		} else if (usuario.getServidor() != null) {			
			PortalDocenteTouchMBean docBean = getMBean("portalDocenteTouch", request, response);				
			docBean.setServidor(usuario.getServidor());
			docBean.setDocenteExterno(usuario.getDocenteExterno());
			
			response.sendRedirect("/sigaa/mobile/touch/menu.jsf");
			
		} else {			
			response.getWriter().print("Módulo Mobile atualmente disponível somente para Docentes e Discentes da Instituição.");
			response.getWriter().flush();
		}
	}
	
	/**
	 * Retorna um managed-bean existente no container do JavaServer Faces.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getMBean(String mbeanName, HttpServletRequest req, HttpServletResponse res) {
		FacesContext fc = FacesContextUtils.getFacesContext(req, res);
		return (T) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, mbeanName);
	}

}
