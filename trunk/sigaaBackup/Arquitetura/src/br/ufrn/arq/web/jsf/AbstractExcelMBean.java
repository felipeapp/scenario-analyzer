/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 05/05/2009
 */
package br.ufrn.arq.web.jsf;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * Classe para construir planilhas do Excel
 * utilizando o Apache POI.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public abstract class AbstractExcelMBean extends AbstractController {

	/**
	 * Método para construir a planilha e renderizar o response.
	 * Não pode ser sobrescrito em classes filhas. 
	 * @return
	 * @throws Exception
	 */
	public final String buildSheet() throws Exception {
		new AbstractExcelView() {
			@Override
			protected void buildExcelDocument(@SuppressWarnings("rawtypes") Map modelMap, HSSFWorkbook wb, HttpServletRequest req, HttpServletResponse res) throws Exception {
				AbstractExcelMBean.this.buildExcelDocument(wb);
			}
		}.render(getModelMap(), getCurrentRequest(), getCurrentResponse());
		
		getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\"" + getFileName() + ".xls\"");
		getCurrentResponse().setContentType("application/xls");
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}

	protected abstract void buildExcelDocument(HSSFWorkbook wb) throws Exception;
	
	/**
	 * Retorna o nome do arquivo excel a ser gerado.
	 * @return
	 */
	protected abstract String getFileName();

	/**
	 * Utilizado para passar dados para a planilha do excel.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Map getModelMap() {
		return null;
	}
	
	/**
	 * Atribui um texto a uma determinada célula da planilha.
	 */
	@SuppressWarnings("deprecation")
	protected void setText(HSSFCell cell, String text) {
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(text);
	}		
	
}
