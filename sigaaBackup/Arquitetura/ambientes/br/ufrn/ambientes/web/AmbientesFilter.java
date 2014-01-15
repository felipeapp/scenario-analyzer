/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2010
 */
package br.ufrn.ambientes.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.ambientes.dominio.ConstantesParametrosAmbientes;
import br.ufrn.arq.parametrizacao.ParametroHelper;

/**
 * Filtro de servlet para verificar se a segurança do ambiente está
 * ativa e se existe um usuário de ambiente em sessão. Se não existir,
 * redireciona para a tela de login.
 *  
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class AmbientesFilter implements Filter {

	private boolean segurancaAmbienteAtiva;
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (segurancaAmbienteAtiva) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			
			String usuario = null;
			
			for (Cookie cookie : req.getCookies()) {
				if ("usuarioAmbiente".equals(cookie.getName())) {
					usuario = cookie.getValue();
					break;
				}
			}
			
			if (usuario == null && req.getRequestURI().indexOf("/public/ambiente/login.jsf") == -1) {
				res.sendRedirect(req.getContextPath() + "/public/ambiente/login.jsf?url=" + req.getRequestURI());
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		segurancaAmbienteAtiva = ParametroHelper.getInstance().getParametroBoolean(ConstantesParametrosAmbientes.SEGURANCA_AMBIENTE_ATIVA);
	}

}
