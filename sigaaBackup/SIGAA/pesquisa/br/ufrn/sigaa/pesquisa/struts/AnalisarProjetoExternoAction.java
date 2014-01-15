/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
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
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.form.ProjetoPesquisaForm;
import br.ufrn.sigaa.pesquisa.negocio.AnalisarProjetoExternoValidator;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoProjetoPesquisa;

/**
 * Entidade respons�vel pela an�lise do projeto Externo
 * 
 * @author Victor Hugo
 */
public class AnalisarProjetoExternoAction extends AbstractCrudAction {

	/** FORM FORWARDS */
	public static final String	LISTAR			= "listar";
	public static final String	ANALISAR		= "analisar";


	/**
	 * M�todo respons�vel pela listagem dos Projetos Externos.
	 */
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		ProjetoPesquisaDao daoProjeto = getDAO( ProjetoPesquisaDao.class, req );

		Collection<ProjetoPesquisa> projetos;// = daoProjeto.findParaAnalise();

		String tipoBusca = req.getParameter("tipoBusca");
		if( tipoBusca != null ){

			if (tipoBusca.equals("grandeArea")) {
				projetos = daoProjeto.findExternosSubmetidos();
			} else{
				projetos = daoProjeto.findExternosSubmetidos();
			}

		}else{
			projetos = daoProjeto.findExternosSubmetidos();
		}

		req.setAttribute("lista", projetos);
		return mapping.findForward(LISTAR);
	}

	/**
	 * carrega o projeto para a an�lise
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		ProjetoPesquisa projeto = projetoForm.getObj();
		GenericDAO dao = getGenericDAO(req);

		projetoForm.setObj( dao.findByPrimaryKey( projeto.getId(), ProjetoPesquisa.class ) );

		projetoForm.getObj().getMembrosProjeto().iterator();
		projetoForm.getObj().getFinanciamentosProjetoPesq().iterator();
		//projetoForm.getObj().getPlanosTrabalho().iterator();
		projetoForm.getObj().getCronogramas().iterator();

		prepareMovimento(SigaaListaComando.ANALISAR_PROJETO_EXTERNO, req);

		//req.setAttribute(mapping.getName(), planoTrabalhoForm);
		//return mapping.findForward(FORM_AVALIACAO);

		return mapping.findForward(ANALISAR);
	}

	/**
	 * M�todo respons�vel pela aprova��o de um projeto externo
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward aprovar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;

		projetoForm.getObj().setAprovado( true );

		return chamaModelo(mapping, form, req, res);
	}

	/**
	 * M�todo respons�vel pela n�o aprova��o ou reprova��o de um projeto Externo
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward reprovar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		projetoForm.getObj().setAprovado( false );

		return chamaModelo(mapping, form, req, res);
	}

	/**
	 * M�todo respons�vel por invocar o modelo do projeto externo  
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public ActionForward chamaModelo(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		ProjetoPesquisa projeto = projetoForm.getObj();
		
		MovimentoProjetoPesquisa movProjeto = new MovimentoProjetoPesquisa();
		movProjeto.setCodMovimento( SigaaListaComando.ANALISAR_PROJETO_EXTERNO );
		movProjeto.setProjeto(projeto);
		ListaMensagens lista = new ListaMensagens();
		AnalisarProjetoExternoValidator.validaProjetoExterno( projeto, lista );

		if (lista.isErrorPresent()) {
			return mapping.findForward(ANALISAR);
		}

		try {
			execute( movProjeto, req );
			addInformation("Analise de projeto de pesquisa externo realizada com sucesso!", req);
		} catch (NegocioException e) {
			addInformation("Analise de projeto de pesquisa externo falhou!", req);
			e.printStackTrace();
		}

		return list(mapping, form, req, res);

	}

	/**
	 * Cancela a a��o e sai do caso de uso
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cancelar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		removeSession("formAnalisarProjetoExterno", request);

		return super.cancelar(mapping, form, request, response);

	}

}
