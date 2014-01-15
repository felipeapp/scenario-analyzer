/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/05/2010
 */
package br.ufrn.arq.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;

/**
 * Servlet para exibir o brasão da instituição.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class BrasaoInstituicaoServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String logo = RepositorioDadosInstitucionais.get("brasaoInstituicao");
		
		URL url = new URL(logo);
		URLConnection conn = url.openConnection();
		InputStream is = conn.getInputStream();
		
		int c;
		while ((c = is.read()) != -1) {
			resp.getOutputStream().write((char) c);
		}
	}
	
}
