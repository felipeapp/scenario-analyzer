/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.form.ConsultorForm;
import br.ufrn.sigaa.pesquisa.jsf.PortalConsultorMBean;

/**
 * Action responsável pela operação de emissão de certificado de consultor 
 * 
 * @author Ricardo Wendell
 * @author Jean Guerethes
 */
public class EmitirCertificadoConsultorAction extends AbstractAction {

	/**
	 * Emite o certificado de consultor
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward emitir(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ConsultorForm consultorForm = (ConsultorForm) form;
		if ( !consultorForm.isPropesq() ) {
			ListaMensagens lista = verificarPendencias(mapping, form, req, res);
			if ( !isEmpty( lista ) ) {
				addMensagens(lista.getMensagens(), req);
				String context = req.getContextPath();
				String url = "/verPortalConsultor.do";
				res.sendRedirect(context + url);
			}
		}
		
		Consultor consultor = consultorForm.getObj();

		// Buscar dados para o preenchimento do certificado
		GenericDAO dao = getGenericDAO(req);
		try {
			consultor = dao.findByPrimaryKey(consultor.getId(), Consultor.class);
		} finally {
			dao.close();
		}

		// Preparar dados para a geração do certificado
		ArrayList<Consultor> consultores = new ArrayList<Consultor>();
		consultores.add(consultor);

		HashMap<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("ano", CalendarUtils.getAnoAtual());
		parametros.put("data_certificado", Formatador.getInstance().formatarDataDiaMesAno( new Date() ) );

		// Gerar certificado
	    JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("certificado_consultores.jasper"),
	    		parametros,
	    		new JRBeanCollectionDataSource(consultores));

	    res.setContentType("application/pdf");
        res.addHeader("Content-Disposition", "attachment; filename=certificado.pdf");
        JasperExportManager.exportReportToPdfStream(prt,res.getOutputStream());

		return null;
	}

	/**
	 * Verifica se há pendencias para a emissão do certificado de consultoria.
	 */
	public ListaMensagens verificarPendencias(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws DAOException{
		
		ListaMensagens lista = new ListaMensagens();
		
		PortalConsultorMBean mBean = getMBean("portalConsultorMBean", req, res);
		
		if ( mBean.hasProjetosPendetes() )
			lista.addErro("Não é possível emitir o certificado pois ainda há projeto pendentes de avaliação.");
		
		if ( mBean.hasPlanoPendete() )
			lista.addErro("Não é possível emitir o certificado pois ainda há planos pendentes de avaliação.");
		
		return lista;
	}

}