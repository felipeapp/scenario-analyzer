/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.struts;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;

/**
 * Action que controla a entrada no menu Lato Sensu.
 * 
 * @author Gleydson
 *
 */
public class EntrarLatoAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//Verifica se o usuário possui o papel adequado
		checkRole(new int[] {SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO}, request);

		Usuario usr = (Usuario) getUsuarioLogado(request);
		usr.setNivelEnsino(NivelEnsino.LATO);
		request.setAttribute("hideSubsistema", Boolean.TRUE);
		request.getSession().setAttribute("nivel", NivelEnsino.LATO);

		carregarParametrosCalendarioAtual(request);
		
		//Popula o módulo que o usuário está tentando acessar
		String destino  = request.getParameter("destino");
		
		//Caso o acesso não seja feito através do Menu Principal
		if ( destino == null && getSubSistemaAtual(request) > 0 ) {
		
			if( getSubSistemaAtual(request) == SigaaSubsistemas.LATO_SENSU.getId() )
				destino = "lato";
			else if( getSubSistemaAtual(request) == SigaaSubsistemas.PORTAL_COORDENADOR_LATO.getId() )
				destino = "coordenacao";
			else
				throw new NegocioException("Usuário com acesso a coordenação e secretaria do curso de lato. Informe o destino");
		
		}
		
		//Caso o destino seja Portal da Coordenador de Lato 
		if(destino != null && destino.equals("coordenacao")){

			DadosAcesso dados = getAcessoMenu(request);
			if (dados.getCursos() == null)
				dados.setCursos(new ArrayList<Curso>());
			
			CursoLatoDao latoDao = getDAO(CursoLatoDao.class, request);

			Collection<Curso> cursos = dados.getCursos();
			
			if (isEmpty(cursos) && usr.getServidor() != null && isUserInRole(request, SigaaPapeis.COORDENADOR_LATO) ) {
				cursos = latoDao.findAllCoordenadoPor(usr.getServidor().getId());
			} else if(isEmpty(cursos) && isUserInRole(request, SigaaPapeis.SECRETARIA_LATO)) {
				cursos.add(latoDao.findById(usr.getVinculoAtivo().getSecretariaUnidade().getCurso().getId()));
			}

			if (!isEmpty(cursos)){ 
				CursoLato cursoLato = (CursoLato) cursos.iterator().next();
				cursoLato.getDisciplinas().iterator();
				cursoLato.getDescricaoCompleta();
				cursoLato.getDescricao();
				usr.setCursoLato(cursoLato);
				for (Curso c : cursos) {
					if (!dados.getCursos().contains(c)) {
						dados.getCursos().add(c);
					}
				}
				request.getSession().setAttribute(SigaaAbstractController.CURSO_ATUAL, cursoLato);
			}
		    setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_COORDENADOR_LATO);
			return mapping.findForward("menuCoordenador");
		}
			
		setSubSistemaAtual(request, SigaaSubsistemas.LATO_SENSU);
		return mapping.findForward("menuGestor");
	}


}
