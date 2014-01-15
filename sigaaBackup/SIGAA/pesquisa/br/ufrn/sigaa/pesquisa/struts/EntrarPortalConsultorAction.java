/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.pesquisa.CalendarioPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultorDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultoriaEspecialDao;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ConsultoriaEspecial;
import br.ufrn.sigaa.pesquisa.jsf.PortalConsultorMBean;

/**
 * Action respons�vel por popular os dados necess�rios e efetuar o redirecionamento
 * para o portal do consultor
 *
 * @author Ricardo Wendell
 *
 */
public class EntrarPortalConsultorAction extends AbstractCrudAction {

	/**
	 * Respons�vel pela execu��o de entrar no portal do consultor
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		ConsultorDao consultorDao = getDAO(ConsultorDao.class, req);
		ConsultoriaEspecialDao especialDao = getDAO(ConsultoriaEspecialDao.class, req);
		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);
		try {
			Usuario usuario = (Usuario) getUsuarioLogado(req);
			Date hoje = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH);
			
			Consultor consultor = null;
			if(usuario.getServidor() != null)
				consultor = consultorDao.findByServidor(usuario.getServidor());
			else
				consultor = usuario.getConsultor();
			
			// Verificar se o usu�rio � um consultor
			if ( consultor == null ) {
				addMensagemErro("Somente consultores podem acessar o Portal do Consultor", req);
				if ( isNotEmpty( usuario.getConsultor() ) ) {
					return getMappingSubSistema(req, mapping);
				} else {
					res.sendRedirect("http://" + req.getServerName() + ":8080" + req.getContextPath() + "/acessoConsultor?codigo=" +  req.getParameter("codigo") );
					return null;
				}
			}

			ConsultoriaEspecial consultoriaEspecial = especialDao.findByConsultor( consultor );
			if ( !(consultoriaEspecial != null &&
					hoje.compareTo( consultoriaEspecial.getDataInicio()) >= 0 &&
					hoje.compareTo( consultoriaEspecial.getDataFim() ) <= 0 )) {
				
				consultoriaEspecial = null;
			}
			
			// Verificar se o per�odo de avalia��es est� aberto
			CalendarioPesquisa calendario = calendarioDao.findVigente();
			if ( consultoriaEspecial == null && ( isEmpty( calendario.getInicioAvaliacaoConsultores() ) || isEmpty( calendario.getFimEnvioAvaliacaoConsultores() )  || 
					hoje.before(calendario.getInicioAvaliacaoConsultores()) || hoje.after(calendario.getFimEnvioAvaliacaoConsultores()))) {
				addMensagemErro(" O per�odo para avalia��o de projetos est� encerrado! ", req);
				return getMappingSubSistema(req, mapping);
			}
			
			if ( consultoriaEspecial != null && 
					!(hoje.compareTo( consultoriaEspecial.getDataInicio()) >= 0 &&
					hoje.compareTo( consultoriaEspecial.getDataFim() ) <= 0 )) {
				
				addMensagemErro("O per�odo definido como Consultor Especial est� encerrado! ", req);
				return getMappingSubSistema(req, mapping);
				
			}
			
			PortalConsultorMBean mBean = getMBean("portalConsultorMBean", req, res);
			mBean.clear();
			consultor.setConsultorEspecial( consultoriaEspecial != null );
			mBean.setObj(consultor);
			mBean.carregarProjetos();
			
			setSubSistemaAtual(req, SigaaSubsistemas.PORTAL_CONSULTOR);
			
		} finally {
			consultorDao.close();
			especialDao.close();
			calendarioDao.close();
		}
		
		return mapping.findForward("sucesso");
	}

}