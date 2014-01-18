/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 11/01/2010
 *
 */	
package br.ufrn.sigaa.arq.struts;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Servlet que loga no SIGRH a partir do SIGAA.
 *
 * @author Arlindo Rodrigues
 *
 */
public class LogonSigrhServlet extends SigaaServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		req.setAttribute("sistema", Sistema.SIGRH);

		// utiliza o MBean j� usado para a entrada dentro de um JSF-Context.
		Usuario user = (Usuario) req.getSession(true).getAttribute("usuario");
		
		PassaporteLogon passaporte = new PassaporteLogon();
		passaporte.setLogin(user.getLogin());
		passaporte.setIdUsuario(user.getId());
		passaporte.setSistemaAlvo(Sistema.SIGRH);
		passaporte.setSistemaOrigem(Sistema.SIGAA);
		long tempo = System.currentTimeMillis();
		tempo = tempo + 30000; // 30s de timeout
		passaporte.setValidade(tempo);
		passaporte.setHora(new Date());
		
		/* Verifica se foi informada alguma a��o para ser redirecionada */
		String acao = "";
		if (req.getSession(true).getAttribute("acao") != null){
			passaporte.setAcao((String) req.getSession(true).getAttribute("acao"));
			req.getSession(false).removeAttribute("acao");						
			acao = "&acao="+passaporte.getAcao();
		}		
		
		passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);

		try {
			prepareMovimento(ArqListaComando.CADASTRAR_PASSAPORTE, req);
			
			execute(passaporte,req);
		} catch (ArqException e) {
			e.printStackTrace();
		} catch (NegocioException e) {
			e.printStackTrace();
		}
		
		res.sendRedirect("/sigrh/Logon?login=" +passaporte.getLogin() + "&passaporte=true&idUsuario=" + user.getId()+acao);		
		
	}

}