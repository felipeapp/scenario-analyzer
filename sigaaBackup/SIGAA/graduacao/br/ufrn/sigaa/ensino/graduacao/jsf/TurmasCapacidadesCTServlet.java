/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '03/04/2012'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.SigaaServlet;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoDadosTurmasCT;

/**
 * Servlet que busca as turmas de as turmas e capacidades oferecidas pelo CT,
 * ou que utilizarão espaços de aula gerenciados por ele.
 *
 * @author Fred_Castro
 *
 */
public class TurmasCapacidadesCTServlet extends SigaaServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			
			if (!req.isSecure()){
				res.setStatus(HttpStatus.SC_FORBIDDEN);
				throw new NegocioException ("Para esta operação, é necessário utilizar o protocolo HTTPS");
			}
			
			MovimentoDadosTurmasCT mov = new MovimentoDadosTurmasCT ();
			mov.setCodMovimento(SigaaListaComando.DADOS_TURMAS_CT);
			
			mov.setLogin(req.getParameter("login"));
			mov.setSenha(req.getParameter("senha"));
			mov.setAno(Integer.parseInt(req.getParameter("ano")));
			mov.setPeriodo(Integer.parseInt(req.getParameter("periodo")));
			
			prepareMovimento(SigaaListaComando.DADOS_TURMAS_CT, req);
			
			String rs = (String) execute(mov, req);
			
			String [] aux = rs.split("\n");
			
			if (aux.length == 1){
				res.setStatus(HttpStatus.SC_BAD_REQUEST);
				res.getWriter().write("Não foram encontradas turmas abertas de CT para o ano e período informados");
			} else {
				res.setStatus(HttpStatus.SC_OK);
				res.setContentType("text/csv");
				res.getWriter().write(rs);
			}
			
		} catch (NumberFormatException e){
			res.setStatus(HttpStatus.SC_BAD_REQUEST);
			res.getWriter().write("Para esta operação, é necessário enviar o ano e o período para consulta");
		} catch (NegocioException e) {
			res.setStatus(HttpStatus.SC_FORBIDDEN);
			res.getWriter().write(e.getMessage());
		} catch (ArqException e) {
			res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().write(e.getMessage());
		}
	}
}
