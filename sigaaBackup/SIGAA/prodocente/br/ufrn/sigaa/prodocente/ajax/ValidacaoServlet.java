/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '12/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.SigaaServlet;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ValidacaoProducao;

/**
 * Servlet para validar uma produção intelectual do docente.
 *
 * @author Gleydson
 *
 */
public class ValidacaoServlet extends SigaaServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException {

		req.setAttribute("sistema", Sistema.SIGAA);
		Integer idProducao = new Integer(req.getParameter("id").trim());
		Boolean validado = new Boolean(req.getParameter("validado"));

		ValidacaoProducao validacao = new ValidacaoProducao();
		validacao.setProducao(new Producao(idProducao));
		validacao.setValidado(validado);
		validacao.setUsuario(new Usuario(getUsuarioLogado(req).getId()));

		validacao.setDataValidacao(new Date());

		PrintWriter out = res.getWriter();
		try {
			prepareMovimento(SigaaListaComando.VALIDAR_PRODUCAO, req);
			execute(validacao, req);
			out.println("Opera&ccedil;&atilde;o Realizada com sucesso - Produ&ccedil;&atilde;o "
					+ ((validado) ? "Validada" : "Invalidada"));
			/*(out.println("<br>Clique em " +
					"<a href=''><img src='/sigaa/img/refresh.png'></a> para recarregar lista de produ&ccedil;&otilde;es"); */
		} catch (NegocioException e) {
			Iterator<MensagemAviso> it = e.getListaMensagens().getErrorMessages().iterator();
			out.println("Erro:");
			while (it.hasNext()) {
				MensagemAviso aviso = it.next();
				out.println("* " + aviso.getMensagem() + "<br>");
			}

		} catch (ArqException e) {
			out.println(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}

		out.close();

	}

}
