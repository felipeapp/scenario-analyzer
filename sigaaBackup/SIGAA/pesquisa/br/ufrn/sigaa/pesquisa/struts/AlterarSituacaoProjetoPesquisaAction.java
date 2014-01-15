/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.form.AlterarSituacaoProjetoPesquisaForm;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Action responsável por alterar a situação e/ou tipo do projeto de pesquisa
 *
 * @author Leonardo
 * @author Ricardo Wendell
 *
 */
public class AlterarSituacaoProjetoPesquisaAction extends AbstractCrudAction{

	/**
	 * Popular formulário de alteração
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		AlterarSituacaoProjetoPesquisaForm aForm = (AlterarSituacaoProjetoPesquisaForm) form;

		// Buscar projeto
		EditalPesquisaDao dao = getDAO(EditalPesquisaDao.class, req);

		Integer id = 0;
		Boolean interno = null;
		if (aForm.getProjeto().getId() == 0) {
			id = new Integer(req.getParameter("idProjeto"));
		} else {
			id = aForm.getProjeto().getId();
			interno = aForm.getProjeto().isInterno();
		}

		ProjetoPesquisa projeto = dao.findByPrimaryKey(id, ProjetoPesquisa.class);

		if ( interno != null ) {
			projeto.setInterno( interno );
		}

		if ( projeto.getEdital() == null ) {
			projeto.setEdital( new EditalPesquisa());
		}

		aForm.setProjeto(projeto);

		// Preparar movimento
		prepareMovimento(SigaaListaComando.ALTERAR_SITUACAO_PROJETO_PESQUISA, req);

		// Popular dados auxiliares
		req.setAttribute("situacoesProjeto", dao.findByExactField(TipoSituacaoProjeto.class, "contexto", "P", "asc", "id" ));
		req.setAttribute("editais", dao.findAllAtivos());

		Formatador f = Formatador.getInstance();
		aForm.setDataInicio( f.formatarData( projeto.getDataInicio() ) );
		aForm.setDataFim( f.formatarData( projeto.getDataFim() ) );

		// Redirecionar
		req.setAttribute(mapping.getName(), aForm);
		return mapping.findForward(FORM);
	}

	/**
	 * Confirmar alteração do status e/ou tipo do projeto
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward confirmar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		AlterarSituacaoProjetoPesquisaForm aForm = (AlterarSituacaoProjetoPesquisaForm) form;
		ProjetoPesquisa projeto = aForm.getProjeto();

		// Validar dados da alteração
		validarAlteracao( aForm, req );
		if (flushErros(req)) {
			aForm.setProjeto( projeto );
			req.setAttribute(mapping.getName(), aForm);
			return popular(mapping, form, req, res);
		}

		// Executar atualização
		try  {
			MovimentoProjetoPesquisa movProjeto = new MovimentoProjetoPesquisa();
			movProjeto.setCodMovimento(SigaaListaComando.ALTERAR_SITUACAO_PROJETO_PESQUISA);
			movProjeto.setProjeto(projeto);
			projeto = (ProjetoPesquisa) execute(movProjeto, req);
		} catch (NegocioException e) {
			aForm.setProjeto( projeto );
			req.setAttribute(mapping.getName(), aForm);
			return popular(mapping, form, req, res);
		}

		addInformation("Projeto " + projeto.getCodigo() + " alterado com sucesso!", req);
		return mapping.findForward( getSubSistemaCorrente(req).getForward() );
	}

	private void validarAlteracao(AlterarSituacaoProjetoPesquisaForm form, HttpServletRequest req) {
		ProjetoPesquisa projeto = form.getProjeto();

		if ( !projeto.isInterno() ) {
			ListaMensagens erros = new ListaMensagens();

			projeto.setDataInicio( ValidatorUtil.validaData( form.getDataInicio() , "Início do projeto", erros) );
			projeto.setDataFim( ValidatorUtil.validaData( form.getDataFim() , "Fim do projeto", erros) );

			addMensagens(erros.getMensagens(), req);
		}

	}
}
