/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/12/2006'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.ensino.QualificacaoTecnicoDao;

/**
 * Retorna as qualificações possíveis de um discente, de acordo com seu curso.
 *
 * @author Andre M Dantas
 *
 */
public class QualificacoesDiscenteServlet extends SigaaAjaxServlet {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.ajaxtags.servlets.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String getXmlContent(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		QualificacaoTecnicoDao dao = new QualificacaoTecnicoDao();

		try {
			int idDiscente = RequestUtils.getIntParameter(req, "idDiscente");
			Collection qualificacoes = dao.findQualificacoesPossiveis(idDiscente);
			if (qualificacoes == null || qualificacoes.size() == 0)
				return "";
			else
				return new AjaxXmlBuilder().addItems(qualificacoes, "descricao", "id")
				.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			dao.close();
		}
	}

}
