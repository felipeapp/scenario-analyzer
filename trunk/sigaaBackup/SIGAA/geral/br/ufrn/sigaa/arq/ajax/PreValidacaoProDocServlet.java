/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '23/01/2007'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.prodocente.producao.dominio.Patente;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 *
 * Responsável pela pré-validação das produções intelectuais: - retorna página jsp
 * que deve ser carrega na view
 *
 * @author André
 *
 */
public class PreValidacaoProDocServlet extends SigaaAjaxServlet {

	/**
	 * Responsável por carregar e exibir a Produção 
	 */
	@Override
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int id = 0;
		if (request.getParameter("id") != null)
			id = Integer.parseInt(request.getParameter("id"));

		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);
		Producao p;
		try {
			 p = dao.findByPrimaryKey(id, Producao.class);
		} finally {
			dao.close();
		}
		popularProducao(p);

		if (request.getParameter("idArquivo") != null) {
			int idArquivo = Integer.parseInt(request.getParameter("idArquivo"));
			return getDirBaseProducao(p, "/sigaa/prodocente/producao/") + "/view.jsf?ajaxRequest=true&id=" + id + "&idArquivo=" + idArquivo;
		}
		return getDirBaseProducao(p, "/sigaa/prodocente/producao/") + "/view.jsf?ajaxRequest=true&id=" + id;
	}

	/**
	 * Popular atributos lazy da produção
	 *
	 * @param p
	 */
	private void popularProducao(Producao p) {
		if (p instanceof Patente) {
			((Patente) p).getPatrocinadora().iterator();
		}
	}

	/**
	 * Responsável por montar o caminho da produção para a exibição.
	 * 
	 * @param p
	 * @param dirBase
	 * @return
	 */
	public String getDirBaseProducao(Producao p, String dirBase) {

		String mBeanClassName = p.getClass().toString();
		String mBean = mBeanClassName.substring(mBeanClassName.lastIndexOf(".") + 1);
		if (dirBase.equals("")) {
			return dirBase;
		} else {
			return dirBase + mBean;
		}
	}
}
