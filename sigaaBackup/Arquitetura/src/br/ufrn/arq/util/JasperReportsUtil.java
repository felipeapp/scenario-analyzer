/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 23/11/2007
 */
package br.ufrn.arq.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

/**
 * Classe que auxilia na exportacao de relatórios para os diferentes formatos disponíveis.
 *
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
public class JasperReportsUtil {

	public static final String PATH_RELATORIOS_SIGAA = "/br/ufrn/sigaa/relatorios/fontes/";

	/**
	 * Exporta um relatório para o formato especificado.
	 *
	 * @param report
	 * @param nomeArquivo
	 * @param response
	 * @param formato
	 * @throws JRException
	 * @throws IOException
	 */
	public static void exportar(JasperPrint report, String nomeArquivo, HttpServletRequest request, HttpServletResponse response, String formato) throws JRException, IOException {
		if ( formato == null ) formato = "pdf";

		if ( formato.equals("pdf") ) {
			exportarPdf(report, nomeArquivo, response);
		} else if ( formato.equals("xls") ){
			exportarXls(report, nomeArquivo, response);
		} else if ( formato.equals("html") ){
			exportarHtml(report, nomeArquivo, request, response);
		} else if ( formato.equals("rtf") ){
			exportarRTF(report, nomeArquivo, response);
		}
	}

	/**
	 * Exportar relatório em formato PDF.
	 *
	 * @param report
	 * @param nomeArquivo
	 * @param response
	 * @throws JRException
	 * @throws IOException
	 */
	public static void exportarPdf(JasperPrint report, String nomeArquivo, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo);
		JasperExportManager.exportReportToPdfStream(report, response.getOutputStream());
	}

	/**
	 * Exportar relatório em formato XLS (Excel).
	 *
	 * @param report
	 * @param nomeArquivo
	 * @param response
	 * @throws JRException
	 * @throws IOException
	 */
	public static void exportarXls(JasperPrint report, String nomeArquivo, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/xls");
		response.addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo);

		// NÃO DÁ SUPORTE A IMAGENS
//		JRXlsExporter exporter = new JRXlsExporter();
//
//		exporter.setParameter(JRExporterParameter.JASPER_PRINT, report);
//		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
//		exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
//		exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
//		exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);

		// ESTA API DÁ SUPORTE A IMAGENS
		JExcelApiExporter exporter = new JExcelApiExporter();

		exporter.setParameter(JExcelApiExporterParameter.JASPER_PRINT, report);
		exporter.setParameter(JExcelApiExporterParameter.OUTPUT_STREAM, response.getOutputStream());
		exporter.setParameter(JExcelApiExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
		exporter.setParameter(JExcelApiExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		exporter.setParameter(JExcelApiExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);

		exporter.exportReport();
	}

	/**
	 * Realiza exportação do JasperReport para formato RTF, lido pelo Word e OppenOffice.
	 * @param report
	 * @param nomeArquivo
	 * @param response
	 * @throws JRException
	 * @throws IOException
	 */
	public static void exportarRTF(JasperPrint report, String nomeArquivo, HttpServletResponse response) throws JRException, IOException {

		response.addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo);

		// ESTA API DÁ SUPORTE A IMAGENS
		JRRtfExporter exporter = new JRRtfExporter();

		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING,"UTF-8");
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, report);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());

		exporter.exportReport();
	}

	/**
	 * Exportar relatório para o formato HTML
	 *
	 * @param report
	 * @param nomeArquivo
	 * @param response
	 * @throws JRException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void exportarHtml(JasperPrint report, String nomeArquivo, HttpServletRequest request, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("text/html");

		JRHtmlExporter exporter = new JRHtmlExporter();

		exporter.setParameter(JRExporterParameter.JASPER_PRINT, report);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());

		request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, report);
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, new HashMap());
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, request.getContextPath() + "/image?image=");
		exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
		exporter.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.setParameter(JRHtmlExporterParameter.SIZE_UNIT, JRHtmlExporterParameter.SIZE_UNIT_POINT);		
		exporter.exportReport();
	}

	/**
	 * Retorna o InputStream relacionado com o relatório
	 *
	 * @param reportName
	 * @return
	 */
	public static InputStream getReportSIGAA(String reportName) {
		return JasperReportsUtil.class.getResourceAsStream(PATH_RELATORIOS_SIGAA + reportName);
	}

	/**
	 * Retorna o InputStream relacionado com o relatório
	 *
	 * @param reportName
	 * @return
	 */
	public static InputStream getReport(String basePackage, String reportName) {
		return JasperReportsUtil.class.getResourceAsStream(basePackage + reportName);
	}

	/**
	 * Retorna o InputStream relacionado com o relatório
	 *
	 * @param reportName
	 * @return
	 */
	public static InputStream getReportSIGRH(String reportName) {
		return JasperReportsUtil.class.getResourceAsStream("/br/ufrn/sigrh/relatorios/fontes/" + reportName);
	}

	/**
	 * Retorna o InputStream relacionado com o relatório
	 *
	 * @param reportName
	 * @return
	 */
	public static InputStream getReportSIPAC(String reportName) {
		return JasperReportsUtil.class.getResourceAsStream("/br/ufrn/sipac/relatorios/fontes/" + reportName);
	}
}
