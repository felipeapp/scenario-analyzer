/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/03/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;

/**
 * Esse Managed Bean é responsável pela geração de um arquivo pdf com todos os bolsistas
 * de pesquisa que estão ativos.
 * 
 * @author Henrique
 */
public class ListaPresencaMBean extends SigaaAbstractController<Object> {

	/**
	 * Construtor padrão
	 * @return
	 * @throws Exception
	 */
	public String visualizaListaPresenca() throws Exception {
		criarListaPresenca();
		return null;
	}

	/**
	 * Esse caso de uso tem a função de criar uma lista de presença é gerar um pdf com esses dados. 
	 *  
	 * @throws Exception
	 */
	private void criarListaPresenca() throws Exception {

		String path = "/br/ufrn/sigaa/relatorios/fontes/ListaPresencaBolsistasAtivosPesquisa.jasper";

		// Abrir arquivo com o template
		InputStream template = this.getClass().getResourceAsStream(path);

		// Geração do relatório para o formato JasperPrint. Pode ser exportado
		// para outros formatos
		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class);

		Collection<MembroProjetoDiscente> bolsistas = dao.findAtivos();

		if (isEmpty(bolsistas)) {
			addMensagemErro("Não há nenhum bolsista ativo.");
			return;
		}

		JRBeanCollectionDataSource rds = new JRBeanCollectionDataSource(
				bolsistas);

		HashMap<String, String> parametros = new HashMap<String, String>();

		JasperPrint relatorio = JasperFillManager.fillReport(template,
				parametros, rds);

		byte[] pdf = JasperExportManager.exportReportToPdf(relatorio);
		HttpServletResponse response = getCurrentResponse();

		response.setContentType("application/pdf");
		response.setContentLength(pdf.length);
		response.setHeader("Content-disposition", "inline; filename=\""
				+ "listapresenca_BolsitasAtivos.pdf" + "\"");
		response.getOutputStream().write(pdf);

		FacesContext.getCurrentInstance().responseComplete();
	}

}