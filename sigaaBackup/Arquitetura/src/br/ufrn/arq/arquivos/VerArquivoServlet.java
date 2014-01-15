/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/11/2008
 */
package br.ufrn.arq.arquivos;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.web.UFRNServlet;

/**
 * Servlet para download de arquivos da base arquivos
 * 
 * @author Gleydson Lima
 * 
 */
@SuppressWarnings("serial")
public class VerArquivoServlet extends UFRNServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String arquivo = req.getParameter("idArquivo");
		if ( arquivo == null ) {
			// compatibilizar com os dados de produções do SIGAA para ter uma servlet só
			arquivo =  req.getParameter("idProducao"); 
			if ( arquivo == null ) {
				arquivo =  req.getParameter("idFoto"); 
			}
		}
		
		String auxSalvar = req.getParameter("salvar");
		Boolean salvar = Boolean.valueOf(auxSalvar != null ? req.getParameter("salvar") : "true");
		
		if (arquivo != null && !arquivo.equals("")) {

			int idArquivo = new Integer(arquivo);

			String key = req.getParameter("key");
			String generatedKey = UFRNUtils.generateArquivoKey(idArquivo);

			if (key != null && key.equals(generatedKey)) {
				
				if (req.getParameter("formato") != null && "pdf".equalsIgnoreCase(req.getParameter("formato"))) {
					res.sendRedirect("/servicos/converterArquivoPdf?idArquivo=" + arquivo);
					//req.getRequestDispatcher("/servicos/converterArquivoPdf").forward(req, res);
					
				} else {
					try {
						EnvioArquivoHelper.recuperaArquivo(res, idArquivo, salvar);						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				res.getWriter().print("Acesso Negado!");
				res.getWriter().flush();
			}
		}
	}

	
}
