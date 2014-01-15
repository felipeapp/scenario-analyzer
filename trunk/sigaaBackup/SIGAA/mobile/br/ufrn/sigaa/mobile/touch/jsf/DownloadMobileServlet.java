/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/05/2012
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.FacesContextUtils;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Servlet para realizar downloads de arquivos em dispositivos móveis. Essa
 * servlet é necessária para que o download feito em dispositivos android seja
 * realizado corretamente.
 * 
 * @author Renan de Oliveira
 * 
 */
public class DownloadMobileServlet extends HttpServlet {

	/**
	 * Chama a classe responsável por realizar o download do arquivo.
	 * 
	 * Não chamado por JSPs.
	 * 
	 * @throws ServletException, IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer id = Integer.parseInt(request.getParameter("id"));
		String key = request.getParameter("key");
		String keyVerdadeira = UFRNUtils.generateArquivoKey(id);
		
		if (key.equals(keyVerdadeira)){
			FacesContext fc = FacesContextUtils.getFacesContext(request, response);
			DownloadMobileMBean downloadMB = (DownloadMobileMBean) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, "downloadMobile");
		
			try {
				downloadMB.registrarDownload();
			} catch (ArqException e) {
				e.printStackTrace();
			}		
			
			response.sendRedirect("/sigaa/verArquivo?idArquivo=" + id + "&key=" + key + "&salvar=true");
		} else {
			response.getWriter().print("Acesso Negado!");
			response.getWriter().flush();
		}

		/*try {
			EnvioArquivoHelper.recuperaArquivo(response, id, true);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("mensagensMobileErro", e.getMessage());
		}*/
	}

}
