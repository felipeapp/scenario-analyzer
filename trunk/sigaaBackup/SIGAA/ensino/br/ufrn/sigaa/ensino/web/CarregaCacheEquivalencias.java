/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/01/2011
 */
package br.ufrn.sigaa.ensino.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.struts.SigaaServlet;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Servlet que carrega o cache de equivalências usado na matricula on-line.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 */
public class CarregaCacheEquivalencias extends SigaaServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		String pass = req.getParameter("pass");
		
		// só para evitar requests quaisquer.
		if ( "3q1v".equals(pass) ) {
			try {
				UFRNUtils.checkRole(getUsuarioLogado(req), SigaaPapeis.ADMINISTRADOR_SIGAA);
			
				CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
				
				// busca todos os ids de componentes de curriculos ativos que foram oferecidos no semestre
				ComponenteCurricularDao dao = DAOFactory.getInstance().getDAO(ComponenteCurricularDao.class, req);
				List<Integer> componentes = dao.buscaComponentesOferecidosNoSemestre(calendario.getAno(), calendario.getPeriodo(), NivelEnsino.GRADUACAO);
				
				// chama o método findEquivalencia de ComponenteCurricularDao para ele popular o cache completo
				for (Integer id : componentes) {
					dao.findEquivalencia(id);
				}
			} catch (DAOException e) {
				e.printStackTrace();
			} catch (SegurancaException e) {
				resp.getWriter().println("Acesso não autorizado!");
			}
			
		} else {
			resp.getWriter().println("Acesso não autorizado!");
		}
	
	
	}
	
}
