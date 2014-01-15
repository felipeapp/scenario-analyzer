/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 06/12/2006
 *
 */
package br.ufrn.sigaa.arq.ajax;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PROGRAMA_ATUAL;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Servlet que busca componentes curriculares
 *
 * @author Gleydson
 *
 */
public class DisciplinaServlet extends SigaaAjaxServlet {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		DisciplinaDao dao = new DisciplinaDao();
		ComponenteCurricularDao componenteDao = new ComponenteCurricularDao();

		// Create xml schema
		AjaxXmlBuilder builder = new AjaxXmlBuilder();
		try {
			Collection<ComponenteCurricular> disciplinas = null;

			String parametro = findParametroLike("disciplina", request);
			String nivel = request.getParameter("nivel");
			String tipoComponente = request.getParameter("tipoComponente");
			String todosOsProgramasStr = request.getParameter("todosOsProgramas");
			String ativoStr = request.getParameter("componentesAtivos");
			boolean todosOsProgramas = false;
			boolean ativos = true;
			if( !isEmpty(todosOsProgramasStr) )
				todosOsProgramas = Boolean.valueOf(todosOsProgramasStr);

			if( !isEmpty(ativoStr) )
				ativos = Boolean.valueOf(ativoStr);

			if (nivel == null || "".equals(nivel)) {
				nivel = String.valueOf(getNivelEnsino(request));
			}

			// testa para considerar a gestora acadêmica
			int gestoraAcademica = -1;
			if (NivelEnsino.isAlgumNivelStricto(nivel.charAt(0)) && !todosOsProgramas ) {
				String p = request.getParameter("programa");
				if (!getAcessoMenu(request).isPpg()){
					Unidade programaAtual = (Unidade) request.getSession().getAttribute(PROGRAMA_ATUAL);
					if (p != null) {
						gestoraAcademica = new Integer(p);
					} else if (programaAtual != null){
						gestoraAcademica = programaAtual.getId();
					}
				}
			}

			if ("T".equalsIgnoreCase(nivel) && gestoraAcademica == -1) {
				gestoraAcademica = getUnidadeUsuario(request).getId();
			}


			if ("A".equalsIgnoreCase(tipoComponente)) {
				disciplinas = dao.findByNomeAtividadeAtivos(parametro, NivelEnsino.GRADUACAO, null);
			}
			// Somente componentes passíveis de abertura de turmas
			else if ("TURMAS".equalsIgnoreCase(tipoComponente) && nivel.charAt(0) == 'G' ) {
				disciplinas = componenteDao.findAllGraduacaoPassiveisTurma(parametro);
			}
			else {
				if (nivel.charAt(0) == NivelEnsino.GRADUACAO)
					disciplinas = dao.findByNomeOuCodigo(parametro, gestoraAcademica, nivel.charAt(0), null);
				else if (nivel.charAt(0) != NivelEnsino.LATO)
					disciplinas = dao.findByNomeOuCodigo(parametro, gestoraAcademica, nivel.charAt(0),ativos);
				else
					disciplinas = dao.findByNomeOuCodigo(parametro, 0, nivel.charAt(0));
			}

			for (ComponenteCurricular disc : disciplinas) {
				builder.addItem(disc.getDescricao(), String.valueOf(disc.getId()));
			}

			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
			componenteDao.close();
		}
	}

}