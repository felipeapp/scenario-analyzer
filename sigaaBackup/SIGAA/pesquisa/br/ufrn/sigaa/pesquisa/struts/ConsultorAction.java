/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.form.ConsultorForm;

/**
 * Action responsável pelo cadastro e atualização dos dados
 * dos consultores
 *
 * @author Ricardo Wendell
 *
 */
public class ConsultorAction extends AbstractCrudAction {

	/**
	 * Responsável pela edição(Alteração) dos consultores
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		super.edit(mapping, form, req, res);
		ConsultorForm cForm = (ConsultorForm) form;

		if (req.getAttribute("remove") == null) {
			if (cForm.getObjAsPersistDB().getId() == 0) {
				prepareMovimento(SigaaListaComando.CADASTRAR_CONSULTOR, req);
			} else {
				prepareMovimento(ArqListaComando.ALTERAR, req);
			}
		}
		
		req.getSession().setAttribute("interno", cForm.getObj().isInterno());
		return mapping.findForward(FORM);
	}
	
	/**
	 * Responsável pela mundaça de status de interno para externo e vice-versa.
	 */
	public ActionForward mudarStatus(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		ConsultorForm cForm = (ConsultorForm) getForm(form);
		
		boolean interno = !((Boolean) req.getSession().getAttribute("interno"));
		super.edit(mapping, form, req, res);
	
		cForm.getObj().setInterno( interno );
		req.getSession().setAttribute("interno", interno);
		
		return mapping.findForward(FORM);
	}	
	
	/**
	 * Esse método consiste no cadastro ou alteração de um Consultor
	 */
	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		ConsultorForm consultorForm = (ConsultorForm) getForm(form);
		
		consultorForm.getObj().setInterno( (Boolean) req.getSession().getAttribute("interno") );
		
		if ( ValidatorUtil.isNotEmpty( consultorForm.getObj().getNome() ) ) {
			consultorForm.getObj().setNome( consultorForm.getObj().getNome().toUpperCase().replaceAll("[\\W&&[^\\s]]", "").replaceAll("\\d", "").trim() );
		} else {
			String nomeAscii = consultorForm.getObj().getServidor().getPessoa().getNomeAscii();
			String replaceAll = nomeAscii.replaceAll("[\\W&&[^\\s]]", "");
			String trim = replaceAll.replaceAll("\\d", "").trim();
			consultorForm.getObj().setNome( trim );
		}
		
		consultorForm.checkRole(req);
		consultorForm.validate(req);
		
		if ( !consultorForm.hasError(req) ) {

			PersistDB obj = getForm(form).getObjAsPersistDB();
			MovimentoCadastro mov = new MovimentoCadastro();
			
			if (obj.getId() == 0) {
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_CONSULTOR );
			} else {
				Consultor consultor = (Consultor) getGenericDAO(req).findByPrimaryKeyLock(obj.getId(), Consultor.class);
				((Consultor) obj).setCodigo(consultor.getCodigo());
				((Consultor) obj).setSenha(consultor.getSenha());
				if (!consultor.isInterno()) {
					((Consultor) obj).setServidor(null);
					((Consultor) obj).setInterno(consultor.isInterno());
				}
				mov.setCodMovimento(ArqListaComando.ALTERAR);
			}
			mov.setObjMovimentado(obj);

			try {
				execute(mov, req);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), req);
				req.setAttribute(mapping.getName(), form);
				return edit(mapping, form, req, res);
			}

			getForm(form).clear();
			removeSession(mapping.getName(), req);

			addMessage(req, "Operação realizada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			return list(mapping, form, req, res);

		} else {
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}

	}

	/**
	 * Gera uma tela com a visualização com as informações dos consultores
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward viewCredenciais(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		int id = getParameterInt(req, "id", 0);
		if(id == 0)
			return mapping.findForward(getSubSistemaCorrente(req).getForward());

		ConsultorForm cForm = (ConsultorForm) form;
		cForm.setObj( getGenericDAO(req).findByPrimaryKey(id, Consultor.class) );
		
		cForm.getObj().setUrlAcesso(ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_ACESSO) +
				req.getContextPath() + Consultor.ENDERECO_ACESSO + "?" +
							Consultor.CODIGO_CONSULTOR + "=" + Consultor.PREFIXO_USUARIO + cForm.getObj().getCodigo());
		
		req.setAttribute(mapping.getName(), cForm);
		
		return mapping.findForward("credenciais");
	}

}