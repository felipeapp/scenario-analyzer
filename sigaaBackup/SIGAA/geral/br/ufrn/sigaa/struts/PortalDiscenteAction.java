/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/09/2006
 *
 */
package br.ufrn.sigaa.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalHelper;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.medio.jsf.PortalDiscenteMedioMBean;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.portal.jsf.PortalDiscenteMBean;

/** Action responsável por redirecionar para o Portal do Discente.
 * @author Andre Dantas
 *
 */
public class PortalDiscenteAction extends SigaaAbstractAction {

	/** 
	 * Redireciona para o portal do Discente.
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		
		String retorno;

		Usuario usuario = (Usuario) getUsuarioLogado(req);

		DiscenteAdapter discente = usuario.getDiscenteAtivo();

		if (discente == null) {
			throw new SegurancaException("O usuário não possui um registro de discente associado");
		}

		setSubSistemaAtual(req, SigaaSubsistemas.PORTAL_DISCENTE);
		usuario.setNivelEnsino(discente.getNivel());
		req.getSession().setAttribute("nivel", discente.getNivel());

		
		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(discente);
		req.getSession().setAttribute(PARAMETROS_SESSAO, param);
		
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
		req.getSession().setAttribute(CALENDARIO_SESSAO, cal);
		
		// está no período de avaliação?
		if (cal.isPeriodoAvaliacaoInstitucional()) {
			
			int ano = cal.getAnoAnterior();
			int periodo = cal.getPeriodoAnterior();
		
			// avisado da avaliação
			Boolean avisado = (Boolean) req.getSession().getAttribute("avisadoAvaliacaoInstitucional");
			
			Boolean podeAvaliar = (Boolean) req.getSession().getAttribute("podeAvaliarDocente");
			CalendarioAvaliacao calAvaliacao = null;
			if (podeAvaliar == null) {
				calAvaliacao = AvaliacaoInstitucionalHelper.getCalendarioAvaliacaoAtivo(discente);
				if (calAvaliacao == null)
					podeAvaliar = false;
				else
					podeAvaliar =  AvaliacaoInstitucionalHelper.aptoPreencherAvaliacaoVigente(discente, calAvaliacao.getFormulario());
				req.getSession().setAttribute("podeAvaliarDocente",podeAvaliar);
			}
			
			// pode avaliar e já foi avisado na sessão?
			if (podeAvaliar && (avisado == null || !avisado)) {
				req.getSession().setAttribute("avisadoAvaliacaoInstitucional", true);
		
				// já preencheu a avaliação?
				if (calAvaliacao != null && !getDAO(AvaliacaoInstitucionalDao.class, req)
						.isAvaliacaoFinalizada(discente.getDiscente(), ano, periodo, calAvaliacao.getFormulario().getId()))
					retorno = "avaliacaoInstitucionalDiscente";
				else 
					retorno = "sucesso";
			} else {
				retorno = "sucesso";
			}
		} else {
			retorno = "sucesso";
		}
		
		// reserta o mbean que controla o portal do discente
		if ( !discente.isMedio() ) {
			PortalDiscenteMBean mbean = getMBean("portalDiscente", req, response);
			
			if (mbean != null)
				mbean.resetBean();
			
			//discente avisado sobre problema com o CPF inválido.
			Boolean avisadoCPF = (Boolean) req.getSession().getAttribute("avisadoCpfInvalido");
			//Validação de CPF do Discente
			if ( mbean != null && (avisadoCPF == null || !avisadoCPF) && (retorno.equals("sucesso")) ){
				req.getSession().setAttribute("avisadoCpfInvalido", true);
				mbean.validacaoCpfDiscente();
			}	
		} else {
			PortalDiscenteMedioMBean mbean = getMBean("portalDiscenteMedio", req, response);
			
			if (mbean != null)
				mbean.resetBean();
			
			retorno = "sucessoMedio";
		}
		return mapping.findForward(retorno);
	}
	
}