/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/09/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.form.PlanoTrabalhoForm;

/**
 * Action responsável pela finalização de todos os planos de trabalho
 * de uma determinada cota. Todos os bolsistas vinculados também devem
 * ter suas bolsas finalizadas.
 *
 * @author Ricardo Wendell
 *
 */
public class FinalizarPlanosTrabalhoAction extends SigaaAbstractAction {

	/**
	 * Responsável por iniciar o processo de finalização de planos de trabalho,
	 * e redirecionar para o formulário de escolha da cota.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 *	</ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA, req);

		GenericDAO dao = getGenericDAO(req);
		req.setAttribute("cotas", dao.findAll(CotaBolsas.class, "descricao", "desc"));
		return mapping.findForward("form");
	}


	/**
	 * Responsável por buscar todos os planos de uma cota selecionada e exibi-los na tela de confirmação.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/FinalizacaoPlanosTrabalho/form.jsp</li>
	 *	</ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward buscarPlanos(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA, req);
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		PlanoTrabalho plano = planoForm.getObj();

		// Buscar cota selecionada
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);

		// Buscar planos ativos da cota selecionada
		CotaBolsas cota = planoDao.findByPrimaryKey(plano.getCota().getId(), CotaBolsas.class);
		Collection<PlanoTrabalho> planos = planoDao.findAtivosByCota(cota);

		if ( planos.isEmpty() ) {
			addMensagemErro("Não foram encontrados planos de trabalho com status EM ANDAMENTO pertencentes a cota " + cota.getDescricao(), req);
			return iniciar(mapping, planoForm, req, res);
		}

		// Preparar movimento
		prepareMovimento(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_COM_COTA, req);

		plano.setCota(cota);
		req.setAttribute("planos", planos);
		req.setAttribute(mapping.getName(), planoForm);
		return mapping.findForward("resumo");
	}


	/**
	 * Responsável por finalizar, pelo Gestor de Pesquisa, os Planos de Trabalho de uma determinada cota.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/FinalizacaoPlanosTrabalho/resumo.jsp</li>
	 *	</ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward confirmar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA, req);
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		PlanoTrabalho plano = planoForm.getObj();
		Integer total = 0;
		
		// Prepara movimento
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_COM_COTA);
		mov.setObjMovimentado(plano);
		
		try {
			total = (Integer) execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward("resumo");
		}

		CotaBolsas cota = getGenericDAO(req).findByPrimaryKey(plano.getCota().getId(), CotaBolsas.class);
		plano.setCota(cota);
		req.setAttribute(mapping.getName(), planoForm);

		req.setAttribute("total", total);
		return mapping.findForward("comprovante");
	}
	
	/**
	 * Responsável por finalizar, pelo do docente, um Plano de Trabalho sem cota.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/lista_orientador.jsp</li>
	 *	</ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward finalizar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.DOCENTE, req);
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		PlanoTrabalho plano = planoForm.getObj();
		
		prepareMovimento(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_SEM_COTA, req);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_SEM_COTA);
		mov.setObjMovimentado(plano);
		
		try {
			execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward("listaOrientador");
		}
		
		PlanoTrabalhoAction p = new PlanoTrabalhoAction();
		p.listarPorOrientador(mapping, planoForm, req, res);
		
		addInformation("Plano de Trabalho finalizado com sucesso!", req);
		
		return mapping.findForward("listaOrientador");
	}

}
