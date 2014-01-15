/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 20/09/2006
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;

/**
 * Busca ajax de cursos técnicos para tag de auto-completar
 *
 * @author David Ricardo
 *
 */
public class CursoTecnicoServlet extends SigaaAjaxServlet {

	@Override
	public String getXmlContent(HttpServletRequest req, HttpServletResponse res) throws Exception {
		CursoDao dao = null;
		try {
			String model = findParametroLike("cursoTecnico.nome", req);

			String tipo = findParametroLike("tipoCurso", req);

			Class busca = CursoTecnico.class;

			dao = new CursoDao();

			String resultado;
			if ( tipo.equals("lato" ) ) {
				model = findParametroLike("curso.nome", req);
				if( model.equals("") )
					model = findParametroLike("cursoLato.nome", req);
				busca = CursoLato.class;
				resultado = new AjaxXmlBuilder().addItems(dao.findByNome(model, -1,
						busca, getNivelEnsino(req), null), "nome", "id").toString();
			} else
				resultado = new AjaxXmlBuilder().addItems(dao.findByNome(model, getUnidadeUsuario(req).getId(),
						busca, getNivelEnsino(req), null), "nome", "id").toString();

			// Create xml schema
			return resultado;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
