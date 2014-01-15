/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 06/12/2006
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Servlet para buscar as turmas de um aluno em
 * um semestre por ajax.
 *
 * @author David Ricardo
 *
 */
public class TurmasDiscenteServlet extends SigaaAjaxServlet {

	@Override
	public String getXmlContent(HttpServletRequest req, HttpServletResponse res) throws Exception {
	
		DiscenteDao dao = new DiscenteDao();
		
		try {
			int idDiscente = RequestUtils.getIntParameter(req, "idDiscente");
			
			Collection<Turma> turmas = dao.findTurmasMatriculadas(idDiscente);
			return new AjaxXmlBuilder().addItems(turmas, "descricao", "id").toString();
					
		} finally {
			dao.close();
		}
	}
	
}
