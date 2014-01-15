/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.struts.SigaaServlet;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;

/** Servlet responsável por redefinir a senha de acesso à Área Pessoal do Candidato no Vestibular.
 * @author Édipo Elder F. Melo
 *
 */
public class RecuperaSenhaServlet extends SigaaServlet{

	/** Recebe os parâmetros da requisição e, caso validado, redireciona o usuário para o formulário de alteração de senha.
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);
		try {
			int id = Integer.parseInt(req.getParameter("id"));
			String hash = req.getParameter("chave");
			PessoaVestibular pessoa = dao.findByPrimaryKey(id, PessoaVestibular.class);
			if (pessoa.getChaveRecuperacaoSenha() != null && pessoa.getChaveRecuperacaoSenha().equals(hash)
					&& pessoa.getValidadeRecuperacaoSenha() != null && !pessoa.getValidadeRecuperacaoSenha().before(new Date())) {
				req.getRequestDispatcher("/public/vestibular/acompanhamento/nova_senha.jsf").forward(req, res);
			} else {
				req.getRequestDispatcher("/public/vestibular/acompanhamento/nao_recuperado.jsf").forward(req, res);
			} 			
		} catch(Exception e) {
		} finally {
			dao.close();
		}
		
	}
}
