/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/04/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.form.EditalSinteticoForm;

/**
 * Relatório sintético de acordo com o edital de pesquisa
 *
 * @author Gleydson
 * @author Leonardo
 *
 */
public class RelatorioSinteticoAction extends SigaaAbstractAction {

	/**
	 * Popula os dados necessários e inicia a operação para emissão do Relatório de Submissão de Projetos
	 * Chamado na JSP:
	 * \WEB-INF\jsp\pesquisa\menu\relatorio.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularEditais(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		EditalPesquisaDao dao = getDAO(EditalPesquisaDao.class, req);
		req.setAttribute("editais", dao.findAll(EditalPesquisa.class));
		return mapping.findForward("form_projetos");

	}

	/**
	 * Popula os dados necessários e inicia a operação para emissão do Relatório de Cotas Solicitadas
	 * Chamado na JSP:
	 * \WEB-INF\jsp\pesquisa\menu\relatorio.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularCotas(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		EditalPesquisaDao dao = getDAO(EditalPesquisaDao.class, req);

		req.setAttribute("centros", dao.findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc"));
		req.setAttribute("cotas", dao.findAll(CotaBolsas.class));
		return mapping.findForward("form_cotas");

	}

	/**
	 * Busca os dados de acordo com os parâmetros selecionados e exibe o Relatório de Submissão de Projetos
	 * Chamado na JSP: 
	 * \WEB-INF\jsp\pesquisa\relatorios\form_edital_sintetico.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward buscarProjetos(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class, req);
		int idEdital = Integer.parseInt(req.getParameter("idEdital"));

		req.setAttribute("relatorio", dao.findBySumarioEdital(idEdital));

		return mapping.findForward("relatorioProjetos");

	}

	/**
	 * Busca os dados de acordo com os parâmetros selecionados e exibe o Relatório de Cotas Solicitadas
	 * Chamado na JSP: 
	 * \WEB-INF\jsp\pesquisa\relatorios\form_cotas_sintetico.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward buscarCotas(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		EditalSinteticoForm relatorioForm = (EditalSinteticoForm) form;

		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class, req);
		int idCota = Integer.parseInt(req.getParameter("idEdital"));
		if(idCota <= 0){
			addMensagemErro("Cota: Campo obrigatório não informado.", req);
			return popularCotas(mapping, relatorioForm, req, res);
		}

		req.setAttribute("relatorio_", dao.findBySumarioCotaEdital(idCota, relatorioForm.getCentro(), relatorioForm.isAgruparPorDepartamento() ));
		req.setAttribute("cota", dao.findByPrimaryKey(idCota, CotaBolsas.class));
		req.setAttribute("centro_", relatorioForm.getCentro().getId() > 0 ? dao.findByPrimaryKey(relatorioForm.getCentro().getId(), Unidade.class) : "TODOS");
		req.setAttribute("filtroCentro", relatorioForm.getCentro().getId() > 0);
		req.setAttribute("agruparPorDepartamento" ,  relatorioForm.isAgruparPorDepartamento() );

		return mapping.findForward("relatorioCotas");

	}
}
