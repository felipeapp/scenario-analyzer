/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.struts;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.HistoricoSituacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoSituacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.form.HistoricoSituacaoForm;

/**
 * Action com as operações de gerenciamento do Histórico de modificações da Situação de uma Proposta
 * de Curso Lato.
 * 
 * @author Eric Moura
 *
 */
public class HistoricoSituacaoAction extends AbstractCrudAction {

	/* Constantes dos forwards */
	private static final String HISTORICO = "historico";


	/**
	 * Popula todas as propostas de curso lato senso para ter a lista
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		CursoLatoDao dao = getDAO(CursoLatoDao.class, req);
		Collection<CursoLato> listapropostas = null;

		try {
			listapropostas = dao.findAllOtimizado();
			req.setAttribute("listaproposta", listapropostas);

		} finally {
			dao.close();
		}
		return mapping.findForward(LISTAR);
	}

	/**
	 * Preenche o form para alteração de status da proposta
	 */
	public ActionForward cadastroStatus(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		GenericDAO dao = getGenericDAO(req);

		Collection<SituacaoProposta> situacoesproposta = null;

		try {
			int idCurso =  RequestUtils.getIntParameter(req,"id");
			CursoLato cursoLato = dao.findByPrimaryKey( idCurso, CursoLato.class );

			HistoricoSituacaoForm historicoSituacaoForm = (HistoricoSituacaoForm) form;
			historicoSituacaoForm.setCursoLato( cursoLato );
			historicoSituacaoForm.getObj().setProposta( cursoLato.getPropostaCurso() );
			historicoSituacaoForm.getObj().setDataCadastro(new Date(System.currentTimeMillis()));
			historicoSituacaoForm.getObj().setSituacao( cursoLato.getPropostaCurso().getSituacaoProposta() );

			if (req.getSession().getAttribute("situacoesproposta") == null) {
				situacoesproposta = dao.findAll(SituacaoProposta.class);
				addSession("situacoesproposta", situacoesproposta, req);
			}
		} finally {
			dao.close();
		}
		prepareMovimento(SigaaListaComando.GRAVAR_HISTORICO_SITUACAO, req);
		return mapping.findForward(FORM);
	}

	/**
	 * Preenche os dados e joga para tela do histórico
	 */
	public ActionForward verHistorico(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		GenericDAO dao = getGenericDAO(req);
		HistoricoSituacaoDao daoH = new HistoricoSituacaoDao();
		Collection<HistoricoSituacao> historicoSituacoes = null;

		try{
			int idCurso =  RequestUtils.getIntParameter(req,"id");
			CursoLato curso = dao.findByPrimaryKey( idCurso, CursoLato.class );
			historicoSituacoes = daoH.findByProposta( curso.getPropostaCurso().getId() );

			req.setAttribute("curso", curso);
			req.setAttribute("historicoSituacoes", historicoSituacoes);
		} finally {
			dao.close();
			daoH.close();
		}
		return mapping.findForward(HISTORICO);
	}

	/**
	 * Persiste a alteração de status da proposta
	 */
	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		HistoricoSituacaoForm hForm = (HistoricoSituacaoForm) getForm(form);
		hForm.checkRole(req);
		hForm.validate(req);

		if (hForm.getObj().getProposta().getSituacaoProposta().getId() == 0) {
			addMensagemErro("Selecione uma Nova Situação", req);
			return mapping.findForward(FORM);
		}
		if (!flushErros(req)) {

			PersistDB obj = hForm.getObjAsPersistDB();
			MovimentoCadastro mov = new MovimentoCadastro();
			hForm.beforePersist(req);
			mov.setObjMovimentado(obj);

			//if (obj.getId() == 0) {
			mov.setCodMovimento(SigaaListaComando.GRAVAR_HISTORICO_SITUACAO);
		    /*} else {
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				req.setAttribute("voltando", true);
			  }*/
			execute(mov, req);
			addMessage(req, "Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
			//hForm.clear();
			hForm = new HistoricoSituacaoForm();
			
		} else {
			req.setAttribute(mapping.getName(), form);
		}
		return popular(mapping, form, req, res);
	}

}
