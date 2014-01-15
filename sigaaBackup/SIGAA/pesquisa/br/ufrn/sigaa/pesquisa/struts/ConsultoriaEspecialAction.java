/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.sigaa.arq.dao.pesquisa.ConsultoriaEspecialDao;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.ConsultoriaEspecial;
import br.ufrn.sigaa.pesquisa.form.ConsultoriaEspecialForm;

/**
 * Action responsável pelo cadastro de consultorias especiais
 *
 * @author Ricardo Wendell
 *
 */
public class ConsultoriaEspecialAction extends AbstractCrudAction {

	/**
	 * Realiza a edição(Alteração) de uma consultoria Especial
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ActionForward retorno = super.edit(mapping, form, req, res);

		ConsultoriaEspecialDao dao = getDAO(ConsultoriaEspecialDao.class, req);

		// Buscar as consultorias cadastradas
		req.setAttribute("consultorias", dao.findAll());

		return retorno;
	}

	/**
	 * Método responsável por inserir um novo Consultor especial
	 */
	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ConsultoriaEspecialForm consultoriaForm = (ConsultoriaEspecialForm) form;
		
		consultoriaForm.validate(req);
		
		if (flushOnlyErros(req)) {
			return edit(mapping, form, req, res);
		}
		
		if( consultoriaForm.getObj().getId() == 0 && consultoriaForm.getObj().getConsultor().getId() > 0 ){
			Collection<ConsultoriaEspecial> col = getGenericDAO(req).findByExactField(
					ConsultoriaEspecial.class, "consultor.id", consultoriaForm.getObj().getConsultor().getId());
			if(!col.isEmpty()){
				addMensagemErro("O consultor informado já é um consultor especial.", req);
				return edit(mapping, form, req, res);
			}
		}
		
		super.persist(mapping, form, req, res);

		if (!flushErros(req)) {
			consultoriaForm.setObj( new ConsultoriaEspecial() );
		}

		return edit(mapping, form, req, res);
	}

	/**
	 * Método responsável por remover um Consultor Especial
	 */
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		super.remove(mapping, form, req, res);

		if (!flushErros(req)) {
			ConsultoriaEspecialForm consultoriaForm = (ConsultoriaEspecialForm) form;
			consultoriaForm.setObj( new ConsultoriaEspecial() );
		}

		return edit(mapping, form, req, res);
	}

}
