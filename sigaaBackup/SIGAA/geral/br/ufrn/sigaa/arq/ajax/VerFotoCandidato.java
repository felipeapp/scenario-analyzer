package br.ufrn.sigaa.arq.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.jsf.ValidacaoFotoCandidatoMBean;

/**
 * Servlet que serve para ver as fotos ainda não salvas pelo candidato do vestibular.
 *
 * @author Jean Guerethes
 */
public class VerFotoCandidato extends HttpServlet {

	/**
	 * Pega a lista dos candidatos que terão as fotos alteradas e carrega as imagens atuais.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/ValidacaoFoto/atualizacao_foto_lote.jsp</li>
	 * </ul>
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		org.springframework.web.context.WebApplicationContext webApp = WebApplicationContextUtils.getWebApplicationContext(req.getSession().getServletContext());
		
		ValidacaoFotoCandidatoMBean mBean = (ValidacaoFotoCandidatoMBean) webApp.getBean("validacaoFotoBean");

		Long cpf = new Long(req.getParameter("cpf"));
		try {
			res.setContentType("image/jpeg");
			for (PessoaVestibular p : mBean.getPessoaVestibular()) {
				if (p.getCpf_cnpj().equals(cpf)) {
					byte[] imagem = UFRNUtils.redimensionaJPG((byte[]) (p.getImagem().getBytes()), 200, 200);
					res.getOutputStream().write(imagem);
					res.flushBuffer();
				}
			}
		} catch (Exception e) {
			System.err.println("Erro ao visualizar arquivo: " + e.getMessage());
		}
	}
}