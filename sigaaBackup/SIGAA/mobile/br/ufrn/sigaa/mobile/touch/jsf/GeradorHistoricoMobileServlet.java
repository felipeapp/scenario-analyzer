/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/05/2012
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;


/**
 * Servlet utilizada para geração de histórico a partir de dispositivos móveis.
 * Essa servlet é necessária para que o download feito em dispositivos android seja realizado corretamente.
 * 
 * @author Renan de Oliveira
 *
 */
public class GeradorHistoricoMobileServlet extends HttpServlet {
	    	
	/**
	 * Chama o MBean responsável por realizar o download do histórico.
	 * 
	 * Não chamado por JSPs.
	 * 	 
	 * @throws ServletException, IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {    	    						
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
		HistoricoMBean histMBean = (HistoricoMBean) ac.getBean("historico");
		
		request.setAttribute("sistema", Integer.parseInt(request.getParameter("sistema")));				
		
		try {
			histMBean.setUsuarioLogado((Usuario) request.getSession().getAttribute("usuario"));
			histMBean.selecionaDiscente(request, response);
		} catch (ArqException e) {
			e.printStackTrace();
			request.setAttribute("mensagensMobileErro", e.getMessage());
		}
				
    }


    

}
