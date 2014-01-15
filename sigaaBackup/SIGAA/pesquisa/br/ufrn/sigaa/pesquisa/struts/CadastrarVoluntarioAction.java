/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Action responsável pelo cadastro de planos de trabalho para voluntários
 *
 * @author Ricardo Wendell
 *
 */
@Deprecated
public class CadastrarVoluntarioAction extends AbstractCrudAction {

	/**
	 * Iniciar o caso de uso
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		MembroProjetoDiscenteForm discenteForm = (MembroProjetoDiscenteForm) form;
		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);

		Collection<ProjetoPesquisa> projetos = null;

		// Verificar se o usuário é um docente e buscar projetos de pesquisa
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		if (usuario.getServidor() != null) {
			 projetos = projetoDao.findAtivosByMembro((Pessoa) getUsuarioLogado(req).getPessoa());
		} else {
			addMensagemErro("Somente docentes podem cadastrar voluntários de projetos", req);
			return getMappingSubSistema(req, mapping);
		}

		// Verificar se o docente é membro de algum projeto de pesquisa
		if (projetos == null || projetos.isEmpty()) {
			addMensagemErro("Você não é membro de projetos de pesquisa aos quais possa cadastrar um plano de trabalho para voluntário", req);
			return getMappingSubSistema(req, mapping);
		} else {
			req.setAttribute("projetos", projetos);
		}

		prepareMovimento(SigaaListaComando.CADASTRAR_VOLUNTARIO_PESQUISA, req);
		discenteForm.getObj().getPlanoTrabalho().setOrientador(usuario.getServidor());
		req.setAttribute(mapping.getName(), discenteForm);
		return mapping.findForward("form");
	}

	/**
	 * Efetuar o cadastro do plano de trabalho e do voluntário
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cadastrar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		MembroProjetoDiscenteForm discenteForm = (MembroProjetoDiscenteForm) form;
		PlanoTrabalho plano = discenteForm.getObj().getPlanoTrabalho();

		// Validar
		ListaMensagens erros = validar(discenteForm);
		if (!erros.isEmpty()) {
			addMensagens(erros.getMensagens(), req);
			return popular(mapping, form, req, res);
		}

		try {
			plano.setCodMovimento(SigaaListaComando.CADASTRAR_VOLUNTARIO_PESQUISA);
			plano.setMembroProjetoDiscente(discenteForm.getObj());
			execute(plano, req);
			addMensagem(new MensagemAviso("Plano de Trabalho cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION), req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return popular(mapping, form, req, res);
		}

		return getMappingSubSistema(req, mapping);
	}

	/**
	 * Serve para validar o Cadastro de um voluntário 
	 * 
	 * @param membroForm
	 * @return
	 */
	private ListaMensagens validar(MembroProjetoDiscenteForm membroForm) {
		ListaMensagens erros = new ListaMensagens();

		PlanoTrabalho plano =  membroForm.getObj().getPlanoTrabalho();

		if ( plano.getProjetoPesquisa().getId() <= 0 ) {
			ValidatorUtil.validateRequired(null, "Projeto de Pesquisa", erros);
		}

		if ( membroForm.getObj().getDiscente().getId() <= 0 ) {
			erros.addErro("É necessário selecionar um discente como ");
		}

		if ( plano.getTitulo() == null || "".equals(plano.getTitulo().trim()) ) {
			ValidatorUtil.validateRequired(null, "Título do Plano de Trabalho", erros);
		}
		if ( plano.getTipoBolsa().getId() <= 0 ) {
			ValidatorUtil.validateRequired(null, "Tipo da Bolsa", erros);
		}
		else {
			plano.setTitulo(plano.getTitulo().trim());
			if (plano.getTitulo().length() > 300) {
				ValidatorUtil.validateMaxLength(plano.getTitulo(), 300, "Título", erros);
			}
		}

		return erros;
	}

}
