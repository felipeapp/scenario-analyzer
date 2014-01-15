/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/01/2008 
 *
 */
package br.ufrn.sigaa.processamento.jsf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.sigaa.arq.dao.graduacao.ProcessamentoMatriculaDAO;
import br.ufrn.sigaa.arq.struts.SigaaServlet;
import br.ufrn.sigaa.processamento.batch.ListaTurmasProcessadas;

/**
 * Servlet para controle de feedback do processamento de uma matrícula.
 * 
 * @author David Pereira
 *
 */
public class FeedbackProcessamentoServlet extends SigaaServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ProcessamentoMatriculaDAO dao = new ProcessamentoMatriculaDAO();
		
		try {
			int count = 0;
			
			if ("processamento".equals(req.getParameter("tipo"))) {
				ProcessamentoMatriculaMBean bean = (ProcessamentoMatriculaMBean) req.getSession().getAttribute("processamentoMatricula");
				
				if (bean != null) {
					//int ano = bean.getAno();
					//int periodo = bean.getPeriodo();
					//char nivel = bean.getNivel();
					//boolean rematricula = bean.isRematricula();
					//count = dao.findCountTurmasAProcessar(ano, periodo, nivel);
				}
			} else {
				count = ListaTurmasProcessadas.count();
			}					
			
			resp.setContentType("text/plain");
			resp.getWriter().println(count);
			
		} finally {
			dao.close();
		}
		
	}
	
}
