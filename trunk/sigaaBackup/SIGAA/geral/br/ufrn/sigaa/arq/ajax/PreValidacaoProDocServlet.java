/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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
 * Respons�vel pela pr�-valida��o das produ��es intelectuais: - retorna p�gina jsp
 * que deve ser carrega na view
 *
 * @author Andr�
 *
 */
public class PreValidacaoProDocServlet extends SigaaAjaxServlet {

	/**
	 * Respons�vel por carregar e exibir a Produ��o 
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
	 * Popular atributos lazy da produ��o
	 *
	 * @param p
	 */
	private void popularProducao(Producao p) {
		if (p instanceof Patente) {
			((Patente) p).getPatrocinadora().iterator();
		}
	}

	/**
	 * Respons�vel por montar o caminho da produ��o para a exibi��o.
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
