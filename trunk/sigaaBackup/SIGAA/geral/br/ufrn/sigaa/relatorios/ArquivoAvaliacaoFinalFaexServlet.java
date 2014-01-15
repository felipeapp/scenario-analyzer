/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '18/04/2008'
 *
 */
package br.ufrn.sigaa.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;

/**
 * Servlet para gerar o arquivo no formato requerido pela STTU para importação.
 *
 * @author leonardo
 *
 */
public class ArquivoAvaliacaoFinalFaexServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		UsuarioGeral user = (UsuarioGeral) req.getSession(false).getAttribute("usuario");
		if (user == null) {
			throw new ServletException("Usuário não logado");
		}
		if (!user.isUserInRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO, SigaaPapeis.GESTOR_EXTENSAO)) {
			throw new ServletException("Usuário não autorizado a realizar a operação");
		}
		
		gerarArquivoResumoAvaliacaoFinalFaex(req, resp);
	}

	
	/** 
	 * @param req
	 * @param res
	 */
	private void gerarArquivoResumoAvaliacaoFinalFaex(HttpServletRequest req, HttpServletResponse res) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		
		AtividadeExtensaoDao dao = null;
		try {
			
			res.setContentType("application/csv");
			res.setHeader("Content-Disposition", "inline; filename=lista_avaliacao_final_faex_"+sdf.format(new Date())+".csv");
			
			dao = DAOFactory.getInstance().getDAO( AtividadeExtensaoDao.class );
			
			PrintWriter out = res.getWriter();
			out.println(dao.findListaAcoesAvaliacaoFinal(CalendarUtils.getAnoAtual()));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(dao != null)	dao.close();
		}
	}

}