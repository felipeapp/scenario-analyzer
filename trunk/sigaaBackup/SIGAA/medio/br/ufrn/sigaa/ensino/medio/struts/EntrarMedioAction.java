/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 25/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CURSO_ATUAL;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Action que controla a entrada no menu do ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class EntrarMedioAction extends SigaaAbstractAction {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.SECRETARIA_MEDIO,SigaaPapeis.PEDAGOGICO_MEDIO}, req);
		
		Usuario usr = (Usuario) getUsuarioLogado(req);
		usr.setNivelEnsino(NivelEnsino.MEDIO);
		req.getSession().setAttribute("nivel", NivelEnsino.MEDIO);
		req.setAttribute("hideSubsistema", Boolean.TRUE);
		setSubSistemaAtual(req, SigaaSubsistemas.MEDIO);

		DadosAcesso dados = getAcessoMenu(req);
		
		req.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametros(usr.getVinculoAtivo().getUnidade(), usr.getNivelEnsino(), null, null, null));
		
		if(isUserInRole(req, SigaaPapeis.PEDAGOGICO_MEDIO)){
			dados.setPedagogico(true);
			for (Papel p : usr.getPapeis()){
				if (p.getSubSistema().getId() == SigaaSubsistemas.MEDIO.getId() &&
					p.getId() != SigaaPapeis.PEDAGOGICO_MEDIO) {
					dados.setPedagogico(false);
					break;
				}
			}
		} else {
			dados.setPedagogico(false);
		}
			
		if(isUserInRole(req, SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO)){
			CoordenacaoCursoDao coordCursoDao = getDAO(CoordenacaoCursoDao.class, req);
			
			if (usr.getServidor() != null){
				Collection<CoordenacaoCurso> coordenacoes = coordCursoDao.findByServidor(usr.getServidor().getId(), null, NivelEnsino.MEDIO, null);
				
				if (coordenacoes != null && !coordenacoes.isEmpty()) {
					dados.setCursos(new ArrayList<Curso>());
					for (CoordenacaoCurso c : coordenacoes) {
						dados.getCursos().add(c.getCurso());
						c.getCurso().getDescricaoCompleta();
						c.getCurso().getDescricao();
						
						req.getSession().setAttribute(CURSO_ATUAL, c.getCurso());
					}
				}				
			}
			dados.incrementaTotalSistemas();
			
		}
		
		/** Se o usuário está associado a algum curso, pega o calendário vigente do curso, 
		 * caso contrário pega o global vigente */
		CalendarioAcademico cal = null;
		if (getAcessoMenu(req).getCursos() != null) {
			Curso curso = getAcessoMenu(req).getCursos().iterator().next();
			cal = CalendarioAcademicoHelper.getCalendario(curso);
		} 
		
		if (cal == null)
			cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalMedio();
		
		req.getSession().setAttribute(CALENDARIO_SESSAO, cal);

		return mapping.findForward("sucesso");
	}	

}
