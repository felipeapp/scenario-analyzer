/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/10/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.struts;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.RelatoriosLatoDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.form.RelatoriosLatoForm;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoAlunosCurso;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoCursosCentro;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoEntradas;

/**
 * Action responsável por gerar relatórios do lato sensu
 * 
 * @author Leonardo
 *
 */
@Deprecated
/** Utilizar a Classe RelatoriosLatoMBean */
public class RelatoriosLatoAction extends SigaaAbstractAction {

	
	public ActionForward popularEntradasAnoSintetico(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		RelatoriosLatoForm relForm = (RelatoriosLatoForm) form;
		
		relForm.setAnoFim( getAnoAtual() );
		req.setAttribute(mapping.getName(), relForm);
		
		req.getSession().setAttribute("defaultCancel", null);
		
		return mapping.findForward("formEntradasAno");
	}
	
	public ActionForward entradasAnoSintetico(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		RelatoriosLatoForm relForm = (RelatoriosLatoForm) form;
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class, req);
		
		// Validar formulário
		ArrayList<MensagemAviso> erros = new ArrayList<MensagemAviso>();
		relForm.validarAnos(erros);
		if ( !erros.isEmpty() ) {
			addMensagens(erros, req);
			return mapping.findForward("formEntradasAno");
		}
		if (relForm.getAnoInicio() > relForm.getAnoFim()){
			addMensagemErro("O Ano Inicial deve ser menor que o Ano Final",req);
			return mapping.findForward("formEntradasAno");
		}
		// Realizar a consulta
		Map<Integer, LinhaRelatorioSinteticoEntradas> relatorio = dao.findEntradasAno(relForm.getAnoInicio(), relForm.getAnoFim());
		
		if ( relatorio == null || relatorio.isEmpty() ) {
			addMensagemErro("Não foram encontradas entradas para os critérios informados", req);
			return mapping.findForward("formEntradasAno");
		}
		
		req.setAttribute("form", relForm);
		req.setAttribute("relatorio", relatorio);
		return mapping.findForward("entradasAnoSintetico");
	}
	
	public ActionForward alunosCursoSintetico(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class, req);
		
		// Realizar a consulta
		Map<CursoLato, LinhaRelatorioSinteticoAlunosCurso> relatorio = dao.findAlunosCurso(null, null, null);
		
		if ( relatorio == null || relatorio.isEmpty() ) {
			addMensagemErro("Não foi possível gerar o relatório", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		req.setAttribute("relatorio", relatorio);
		return mapping.findForward("alunosCursoSintetico");
	}
	
	public ActionForward cursosCentroSintetico(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class, req);
		
		// Realizar a consulta
		Map<Unidade, LinhaRelatorioSinteticoCursosCentro> relatorio = dao.findCursosCentro(null,null);
		
		if ( relatorio == null || relatorio.isEmpty() ) {
			addMensagemErro("Não foi possível gerar o relatório", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		req.setAttribute("relatorio", relatorio);
		return mapping.findForward("cursosCentroSintetico");
	}
}
