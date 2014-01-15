/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 23/04/2009
 */
package br.ufrn.arq.web.exceptions;

import java.io.IOException;
import java.io.PrintWriter;

import javax.el.ELException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.JasperException;
import org.apache.jasper.el.JspELException;
import org.springframework.beans.BeanInstantiationException;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.UserAgent;
import br.ufrn.arq.util.ErrorUtils;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe para tratar exceções não tratadas pela aplicação.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 */
public class ExceptionHandler {

	private ExceptionChain chain = new ExceptionChain();
	
	public void config() {
		chain.config();
	}
	
	public void handleException(Throwable e, HttpServletRequest req, HttpServletResponse res, int sistema, long initOperacao) throws ServletException, IOException {
		Boolean device =  (Boolean) req.getSession().getAttribute("device");
		
		try {
			//Em caso de erro
			UsuarioGeral user = (UsuarioGeral) req.getSession().getAttribute("usuario");
			
			if ( user != null && user.getRegistroEntrada() != null ) {
				if ( user.getRegistroEntrada().getCanal() == RegistroEntrada.CANAL_DESKTOP || user.getRegistroEntrada().getCanal() == RegistroEntrada.CANAL_DEVICE ) {
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} else {
					res.setStatus(HttpServletResponse.SC_OK);
				}
 			} else if (device != null && device) {
 				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
 			} else {
 				res.setStatus(HttpServletResponse.SC_OK);
 			}
			
			if ( e != null )
				e.printStackTrace();
			if ( e != null && e.getCause() != null )
				e.getCause().printStackTrace();
	
			if ( e instanceof ServletException )
				e = ErrorUtils.traceFacesException((ServletException) e);
			
			if ( e instanceof ServletException ) {
				if (((ServletException) e).getRootCause() != null) {
					e = ((ServletException) e).getRootCause();
				}
			}
			
			if ( e instanceof JasperException ) {
				if (e.getCause() != null) {
					Throwable cause = e.getCause().getCause();
					if (cause == null)
						e = e.getCause();
					else
						e = cause;
				}
			}
			
			if ( e instanceof JspELException || e instanceof ELException ) {
				if (e != null && e.getCause() != null) {
					Throwable cause = e.getCause().getCause();
					if (cause == null)
						e = e.getCause();
					else
						e = cause;
				}
			}
			
			if ( e instanceof BeanInstantiationException )
				e = e.getCause();
			
			ExceptionHandlerResult result = chain.executar(e, req, res);
	
			if (!result.isIgnore() && !result.isStop()) {
				//Em caso de erro, envia alerta para a equipe de desenvolvimento
				//com o detalhamento do erro ocorrido
				ErrorUtils.enviaAlerta(e, req);
			}
	
			if(device != null && device) {
				res.setContentType("text/html");  
				PrintWriter out = res.getWriter();  
				out.close();
			} else if (result.isContinue()) {
				req.setAttribute("erro", e);
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/geral.jsp").forward(req, res);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
			
			ErrorUtils.enviaAlerta(ex, req);
			
			if(device == null) {
				req.setAttribute("erro", ex);
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/geral.jsp").forward(req, res);
			}
			
		} finally {
			UsuarioGeral user = (UsuarioGeral) req.getSession().getAttribute("usuario");
			
			try {
				if (user != null && user.getRegistroEntrada() != null) {
					if (req.getAttribute("NO_LOGGER") == null) {
						//Registrando o log da operação que provocou o erro.
						UserAgent.logaOperacaoErro(req.getRequestURL().toString(), System.currentTimeMillis() - initOperacao, 
								user.getRegistroEntrada().getId(), ErrorUtils.parametrosToString(req), e, sistema);
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
}
