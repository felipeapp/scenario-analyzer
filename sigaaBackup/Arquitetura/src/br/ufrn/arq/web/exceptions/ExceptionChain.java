/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 23/04/2009
 */
package br.ufrn.arq.web.exceptions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.dominio.SubSistema;

/**
 * Chain of Responsibility para executar o processamento de exceções
 * no exception handler do view filter em cadeias sequenciais. Este processamento é importante
 * para poder tratar de forma mais amigável as exceções dos frameworks JSF e Hibernate.
 * 
 * Refatoração do código localizado anteriormente no ViewFilter.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class ExceptionChain {

	private ExceptionChain next;

	public void config() {
		setNext(new ArqExceptions()).
		setNext(new JsfExceptions()).
		setNext(new HibernateExceptions());
	}
	
	public ExceptionHandlerResult executar(Throwable e, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		ExceptionHandlerResult result = processar(e, req, res);
		if (result.isContinue()) {
			if (next != null)
				result = next.executar(e, req, res);
		}
		
		return result;
	}
	
	public ExceptionHandlerResult processar(Throwable e, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		return ExceptionHandlerResult.CONTINUE;
		
	}

	public ExceptionChain setNext(ExceptionChain next) {
		this.next = next;
		return next;
	}

	/**
	 * Redireciona o usuário para o subsistema que ele está usando.
	 */
	protected void redirectSubSistema(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String reqUrl = request.getRequestURI();
		
		if (reqUrl.indexOf("/public/") != -1) {
			response.sendRedirect(request.getContextPath() + "/public/");
		} else {
			Object subsistema = request.getSession().getAttribute("subsistema");
			String link = request.getContextPath() + "/verMenuPrincipal.do";
			SubSistema ss = null;
			
			if (subsistema instanceof SubSistema)  {
				ss = (SubSistema) subsistema;
				if (ss != null)
					link = ss.getLink();
			} else {
				String idSubsistema = (String) request.getSession().getAttribute("subsistema");
				GenericDAO dao = new GenericSharedDBDao();
				
				try {
					ss = dao.findByPrimaryKey(Integer.parseInt(idSubsistema), SubSistema.class);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					dao.close();
				}
				
				if (ss != null)
					link = request.getContextPath() + "/" + ss.getLink();
			}

			response.sendRedirect(link);
		}
	}

	protected void addMensagemErro(String msg, HttpServletRequest req, HttpServletResponse res) throws IOException {
		HttpSession session = req.getSession();

		if (session.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION) == null)
			session.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, new ListaMensagens());

		ListaMensagens errosAnt = (ListaMensagens) session.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
		if (errosAnt != null) {
			errosAnt.addErro(msg);
			session.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, errosAnt);
		}
		
		res.sendRedirect(req.getRequestURL().toString());
	}
	
}
