/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 15/02/2008 
 *
 */
package br.ufrn.sigaa.processamento.jsf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.struts.SigaaServlet;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Servlet para buscar o arquivo contendo o resultado do processamento
 * de uma turma.
 * 
 * @author David Pereira
 *
 */
public class BuscaRelatorioProcessamentoServlet extends SigaaServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			String caminho = ParametroHelper.getInstance().getParametro(ConstantesParametro.CAMINHO_RESULTADO_PROCESSAMENTO);
			
			GenericDAO dao = new GenericSigaaDAO();
			
			try {
				Turma t = dao.findByPrimaryKey(new Integer(req.getParameter("idTurma")), Turma.class);
				caminho += "/" + t.getAno() + "" + t.getPeriodo();
			} finally {
				dao.close();
			}
			
			if (req.getParameter("rematricula") != null) {
				caminho = caminho + "R";
			}
			
			try {
				Scanner sc = new Scanner(new File(caminho + "/" + req.getParameter("idTurma") + ".html"));
				while(sc.hasNext())
					out.println(sc.next());
			
				out.flush();
			} catch(FileNotFoundException e) {
				out.write("<script type=\"text/javascript\">");
				out.write("alert('Não existe relatório de processamento para essa turma.');");
				out.write("window.close();");
				out.write("</script>");
			}
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
	}
	
}
