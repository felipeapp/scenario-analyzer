/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '03/10/2006'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.commons.beanutils.PropertyUtils;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Servlet que busca Pessoa a partir do CPF por AJAX
 *
 * @author Andre M Dantas
 *
 */
public class PessoaPorCpfServlet extends SigaaAjaxServlet {

	/**
	 * Monta um AjaxXmlBuilder com os itens ('propriedade', valor)
	 * @param obj
	 * @param properties
	 * @return
	 */
	public AjaxXmlBuilder buildXml(Object obj, String... properties) {
		AjaxXmlBuilder axb = new AjaxXmlBuilder();
		for (String prop : properties) {
			Object value = null;
			try {
				value = PropertyUtils.getProperty(obj, prop);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (value instanceof Date) {
				value = Formatador.getInstance().formatarData((Date) value);
			}
			axb.addItem(prop, value == null ? "" : value.toString());
		}
		return axb;
	}

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GenericDAOImpl dao = new GenericDAOImpl(Sistema.SIGAA);

		try {
			String model = Formatador.getInstance().parseStringCPFCNPJ(request.getParameter("cpf"));
			long cpf = Long.parseLong(model);
			Collection<Pessoa> pessoas = dao.findByExactField(Pessoa.class, "cpf_cnpj", cpf);
			if (!isEmpty(pessoas)) {
				Pessoa p = pessoas.iterator().next();
				// Create xml schema
				return buildXml(p, "nome", "nomeMae", "nomePai", "dataNascimento", "id"
				/*, e o resto q tem na jsp*/)
				.toString();
			}
			
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
