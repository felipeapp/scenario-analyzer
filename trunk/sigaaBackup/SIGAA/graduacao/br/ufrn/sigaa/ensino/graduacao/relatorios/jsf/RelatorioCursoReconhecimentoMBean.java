/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 23/09/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Relatório para listar todos os cursos de graduação 
 * com reconhecimento, data de decreto e publicação no DOU.

 * @author David Pereira
 *
 */
@Component @Scope("request")
public class RelatorioCursoReconhecimentoMBean extends SigaaAbstractController {

	private Unidade centro = new Unidade();
	
	public String iniciar() {
		return forward("/graduacao/relatorios/form_relatorio_curso_reconhecimento.jsp");
	}
	
	public String gerar() throws JRException, SQLException, IOException {
		
		Map<String, Object> hs = new HashMap<String, Object>();
		hs.put("unidade", centro.getId());
		hs.put("subSistema", getSubSistema().getNome());
        hs.put("subSistemaLink", getSubSistema().getLink());

		JasperPrint prt = JasperFillManager.fillReport(getReportSIGAA("trf6787_Curso_Reconhecimento.jasper"), hs, Database.getInstance().getSigaaConnection());
		
		//Verifica se a consulta feita pelo relatorio encontrou algum resultado.
        //Caso a cosulta interna não tenha obtido êxito o número de páginas será zero. 
		if(prt == null || ValidatorUtil.isEmpty(prt.getPages())) {
        	addMensagemWarning("Não há resultados a serem mostrados.");
        	return null;
        }

		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=relatorio_cursos.pdf");
		
		JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());
		
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}
	
}
