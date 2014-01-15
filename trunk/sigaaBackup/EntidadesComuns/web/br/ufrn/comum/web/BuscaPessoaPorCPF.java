package br.ufrn.comum.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dao.PessoaDAO;
import br.ufrn.comum.dominio.PessoaGeral;

/**
 * Servlet que busca o nome da pessoa na base comum de pessoas.
 *
 * @author Gleydson
 *
 */
public class BuscaPessoaPorCPF extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		PessoaDAO dao = new PessoaDAO();
		try {
			String cpfSemPontos = Formatador.getInstance().parseStringCPFCNPJ(req.getParameter("cpf"));
			PessoaGeral p = dao.findByCpfCnpj(new Long(cpfSemPontos));
			if ( p != null ) {
				res.getWriter().println(p.getNome());
				res.getWriter().flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

	}

}
