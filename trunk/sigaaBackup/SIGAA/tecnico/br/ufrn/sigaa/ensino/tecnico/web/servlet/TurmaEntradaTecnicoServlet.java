/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.tecnico.web.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.ajax.SigaaAjaxServlet;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.TurmaEntradaLatoDao;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Servlet que busca as turmas de entrada de um determinado
 * curso técnico por AJAX
 *
 * @author Andre M Dantas
 *
 */
public class TurmaEntradaTecnicoServlet extends SigaaAjaxServlet {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if ( getSubSistemaCorrente(request).equals(SigaaSubsistemas.TECNICO) ||
				getSubSistemaCorrente(request).equals(SigaaSubsistemas.MEDIO))
			return buscaTurmasEntradasTecnico(request, response);
		else if ( getSubSistemaCorrente(request).equals(SigaaSubsistemas.LATO_SENSU) ||
				getSubSistemaCorrente(request).equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO))
			return buscaTurmasEntradasLato(request, response);
		else
			return null;

	}

	private String buscaTurmasEntradasLato(HttpServletRequest request, HttpServletResponse response) {

		TurmaEntradaLatoDao dao = new TurmaEntradaLatoDao();
		try {
			String model = request.getParameter("cursoId");
			if (model == null || model.trim().equals("")) {
					return new AjaxXmlBuilder().addItems(new ArrayList<Object>(),	"descricao", "id").toString();
			}
			int id = Integer.parseInt(model);
			// busca todas as turmas de entrada do curso passado por parâmetro;
			Collection<TurmaEntradaLato> turmasEntrada = dao.findByCursoLato(id, true);
			return new AjaxXmlBuilder().addItems(turmasEntrada, "descricao", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

	private String buscaTurmasEntradasTecnico(HttpServletRequest request, HttpServletResponse response) {

		TurmaEntradaTecnicoDao dao = new TurmaEntradaTecnicoDao();
		try {
			String param = request.getParameter("curriculoId");
			AjaxXmlBuilder ajaxXmlBuilder = new AjaxXmlBuilder();
            if (param == null || param.trim().equals("")) {
					return ajaxXmlBuilder.addItems(new ArrayList<Object>(),	"descricao", "id").toString();
			}
			int id = Integer.parseInt(param);
			// busca todas as turmas de entrada do currículo passado por parâmetro
			List<TurmaEntradaTecnico> turmasEntrada = (List<TurmaEntradaTecnico>) dao.findByCurriculo(id);
			Collections.sort(turmasEntrada);
			ajaxXmlBuilder.addItem("-- SELECIONE --", "0");
			return ajaxXmlBuilder.addItems(turmasEntrada, "descricao", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}

	}

}
