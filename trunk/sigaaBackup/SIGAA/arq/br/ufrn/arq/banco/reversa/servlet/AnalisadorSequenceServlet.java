/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/01/2010
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.banco.reversa.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.web.UFRNServlet;

/**
 * @author Johnny Marçal
 * 
 */
public class AnalisadorSequenceServlet extends UFRNServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		try {
			
			UFRNUtils.checkRole(getUsuarioLogado(request), SigaaPapeis.ADMINISTRADOR_SIGAA);
			response.sendRedirect("administracao/consultar_entidades_mapeadas.jsf");
			
		} catch (SegurancaException e) {
			
			e.printStackTrace();
		}
				
	}
	
}