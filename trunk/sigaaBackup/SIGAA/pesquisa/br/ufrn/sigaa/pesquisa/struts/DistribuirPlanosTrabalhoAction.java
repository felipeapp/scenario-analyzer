/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoDistribuicaoPesquisa;

/**
 * Action para distribuir planos de trabalho
 * 
 * @author ricardo
 *
 */
public class DistribuirPlanosTrabalhoAction extends AbstractCrudAction {

	/**
	 * Responsável pela edição da distribuição dos planos de trabalho
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		
		PlanoTrabalhoDao planoTrabalhoDao = getDAO(PlanoTrabalhoDao.class, req);
		
		try {
			// Buscar resumo de informações para efetuar a distribuição
			Collection<?> areasConhecimento = planoTrabalhoDao.findResumoParaDistribuicao();
			req.setAttribute("areasConhecimento", areasConhecimento);
			
			Collection<Consultor> consultoresInternos = planoTrabalhoDao.findByExactField(Consultor.class, "interno", true);
			req.setAttribute("consultoresInternos", consultoresInternos);
		} finally {
			planoTrabalhoDao.close();
		}
		
		return mapping.findForward("distribuicao");
	}
	
	/**
	 * Responsável pela distribuição dos planos de trabalho
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward distribuir(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		
		prepareMovimento(SigaaListaComando.DISTRIBUIR_PLANOS_TRABALHO_PESQUISA,req);
		
		GenericDAO dao = getGenericDAO(req);
		Map<Consultor, AreaConhecimentoCnpq> mapaDistribuicaoManual = new HashMap<Consultor, AreaConhecimentoCnpq>();
		int totalAreas = Integer.parseInt( req.getParameter("totalAreas"));
		
		for (int i = 1; i <= totalAreas; i++) {

			int idAreaConhecimentoCnpq = Integer.parseInt(req.getParameter("idGrandeArea"+i));
			AreaConhecimentoCnpq area = dao.findByPrimaryKey(idAreaConhecimentoCnpq, AreaConhecimentoCnpq.class);
			
			int idConsultor = Integer.parseInt(req.getParameter("consultor_"+idAreaConhecimentoCnpq));
			if (idConsultor > 0){  //escolheu um consultor
				Consultor consultor = dao.findByPrimaryKey(idConsultor, Consultor.class);
				mapaDistribuicaoManual.put(consultor, area);			
			}
		}
		
		MovimentoDistribuicaoPesquisa mov = new MovimentoDistribuicaoPesquisa();
		mov.setMapaDistribuicaoManual(mapaDistribuicaoManual);
		mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_PLANOS_TRABALHO_PESQUISA);
		
		execute(mov, req);
		
		addInformation("Planos de Trabalho distribuídos com sucesso!", req);
		return edit(mapping, form, req, res);
	}
	
}
