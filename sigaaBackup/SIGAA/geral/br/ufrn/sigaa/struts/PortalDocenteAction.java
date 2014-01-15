/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '19/09/2006'
 *
 */
package br.ufrn.sigaa.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CURSO_ATUAL;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.acesso.AcessoServidor;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Action responsável por redirecionar para o Portal do Docente
 * 
 * @author ricardo
 *
 */
public class PortalDocenteAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_DOCENTE);
		
		DadosAcesso dados = getAcessoMenu(request);
		Usuario usuario = (Usuario) getUsuarioLogado(request);
		
		//Carrega os editais abertos
		GenericDAO dao = getGenericDAO(request);
		Collection<Edital> editais = dao.findAll(Edital.class);
		request.setAttribute( "editais", editais );
		
		clearSessionWeb(request);
		
		if (getAcessoMenu(request).isChefeDepartamento()) {
			request.getSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
			usuario.setNivelEnsino(NivelEnsino.GRADUACAO);
			request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(usuario));
			request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
			
			new AcessoServidor().processar(dados, usuario, request);
			
		}
		
		//Carrega a quantidade de orientações acadêmicas e acordo o papel que o docente possui. 
		OrientacaoAcademicaDao orientacaoDAO = getDAO(OrientacaoAcademicaDao.class, request);
		if (usuario.getServidorAtivo() != null) {
				
			if( usuario.isUserInRole(SigaaPapeis.ORIENTADOR_ACADEMICO) )
				dados.setOrientacoesAcademicas( orientacaoDAO.findTotalOrientandosAtivos(
						usuario.getServidorAtivo().getId(), OrientacaoAcademica.ACADEMICO));
				
			if( usuario.isUserInRole(SigaaPapeis.ORIENTADOR_STRICTO) )
				dados.setOrientacoesStricto( orientacaoDAO.findTotalOrientandosAtivos(
					usuario.getServidorAtivo().getId(),	OrientacaoAcademica.ORIENTADOR));
				
			if( usuario.isUserInRole(SigaaPapeis.CO_ORIENTADOR_STRICTO) )				
					dados.setCoOrientacoesStricto( orientacaoDAO.findTotalOrientandosAtivos(
					usuario.getServidorAtivo().getId(),	OrientacaoAcademica.CoORIENTADOR) );
				
		}
		// limpa dados de sessões de outros módulos
		request.getSession().removeAttribute(CURSO_ATUAL);
		return mapping.findForward("sucesso");
	}

}
