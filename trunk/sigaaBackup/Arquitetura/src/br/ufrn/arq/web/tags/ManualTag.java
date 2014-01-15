/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 04/01/2010
 */
package br.ufrn.arq.web.tags;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dao.MaterialTreinamentoDAO;
import br.ufrn.comum.dominio.MaterialTreinamento;

/**
 * Tag para exibição dos manuais existentes para um caso de uso.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class ManualTag extends TagSupport {

	private String codigo;
	
	@Override
	public int doStartTag() throws JspException {
		
		MaterialTreinamentoDAO dao = new MaterialTreinamentoDAO();
		
		try {
			List<MaterialTreinamento> materiais = dao.findByCodigoUc(codigo);
			JspWriter out = pageContext.getOut();
			
			if (!isEmpty(materiais)) {
				out.println("<div class=\"descricaoOperacao\">");
				out.println("<img src=\"/shared/img/blackboard.png\" align=\"left\" style=\"padding-right: 40px\"/>");
				out.println("Esta operação possui material para ajuda ao usuário: ");
				out.println("<ul>");
				for (MaterialTreinamento mat : materiais) {
					out.println("<li><a href=\"" + mat.getEndereco() + "\" target=\"_blank\">Clique aqui</a> para acessar o(a) " + mat.getTipoMaterialTreinamento().getDenominacao() + "</li>");
				}
				out.println("</ul>");
				out.println("</div>");
			}
			
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		return super.doStartTag();
	}

	@Override
	public void release() {
		codigo = null;
		super.release();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
}
