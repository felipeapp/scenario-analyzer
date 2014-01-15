 /*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Aug 22, 2007
 *
 */
package br.ufrn.sigaa.arq.seguranca;

import static br.ufrn.arq.seguranca.SigaaPapeis.ADMINISTRADOR_DAE;
import static br.ufrn.arq.seguranca.SigaaPapeis.ADMINISTRADOR_SIGAA;
import static br.ufrn.arq.seguranca.SigaaPapeis.CDP;
import static br.ufrn.arq.seguranca.SigaaPapeis.COORDENADOR_CURSO;
import static br.ufrn.arq.seguranca.SigaaPapeis.DAE;
import static br.ufrn.arq.seguranca.SigaaPapeis.SECRETARIA_CENTRO;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Verificador para um determinado padrão de URL se o usuário possui acesso
 *
 * @author Gleydson
 *
 */

public class URLCheckAccess {

	public static List<URLAcesso> urlsProtegidas = new ArrayList<URLAcesso>();

	public static void processRequest(HttpServletRequest req)
			throws SegurancaException {

		String requestURL = req.getRequestURL().toString();
		Usuario user = (Usuario) req.getSession().getAttribute("usuario");
		if (user != null) {

			for (URLAcesso acesso : urlsProtegidas) {
				acesso.verificaPermissao(requestURL, user);
			}

		}

	}


	static {

		urlsProtegidas.add(new URLAcesso("/graduacao/",
				new int[] { ADMINISTRADOR_DAE, CDP, DAE, COORDENADOR_CURSO, SECRETARIA_CENTRO } ));

		urlsProtegidas.add(new URLAcesso("/administracao/", new int[] { ADMINISTRADOR_SIGAA } ));
	}

}