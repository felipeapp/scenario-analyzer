/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 28/11/2008
 */
package br.ufrn.sigaa.assistencia.remoto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Servlet para visualização da foto do usuário
 *
 * @author geyson
 *
 */
@SuppressWarnings("serial")
public class VerFotoIdentificacaoRu extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		if (req.getParameter("cpf") != null
				&& !req.getParameter("cpf").equals("")) {

			if ( !req.getParameter("passkey").equals("biosinfo") ) {
				return;
			}
			
			Long cpf = new Long(req.getParameter("cpf"));
			try {
				res.setContentType("image/jpeg");
				JdbcTemplate template = new JdbcTemplate(Database.getInstance().getComumDs());
				@SuppressWarnings("rawtypes")
				List fotos = template.queryForList("select foto from comum.identificacao_pessoa where cpf = " + cpf);
				if ( fotos.size() > 0 ) {
					@SuppressWarnings("rawtypes")
					byte[] imagem = UFRNUtils.redimensionaJPG((byte[]) ((Map) fotos.get(0)).get("foto"), 129, 151);
					res.getOutputStream().write( imagem );
					res.flushBuffer();
				}
			} catch (Exception e) {
				System.err.println("Erro ao visualizar arquivo: " + e.getMessage());
			}
		}

	}
}
