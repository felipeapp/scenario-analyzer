package br.ufrn.sigaa.prodocente.relatorios.jsf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.web.jsf.AbstractExcelMBean;
import br.ufrn.sigaa.arq.dao.prodocente.ClassificacaoRelatorioDao;

@Component @Scope("request")
public class RelatorioAvaliacaoExcelMBean extends AbstractExcelMBean {
 
	private List<HashMap<String, Object>> list;
	
	@Override
	protected String getFileName() {
		return "RelatórioAvaliaçãoPesquisa.xls";
	}
 
	@Override
	protected void buildExcelDocument(HSSFWorkbook wb) throws Exception {
 
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA);
		
		HSSFSheet sheet = wb.createSheet();
 
		// Adicionando Estilo
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont negrito = wb.createFont();
		negrito.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(negrito);
 
		HSSFRow cabecalho = sheet.createRow(0);
		
		HSSFCell centro = cabecalho.createCell((short) 0);
		centro.setCellStyle(style);
		setText(centro, "Centro");
 
		HSSFCell departamento = cabecalho.createCell((short) 1);
		departamento.setCellStyle(style);
		setText(departamento, "Departamento");

		HSSFCell docente = cabecalho.createCell((short) 2);
		docente.setCellStyle(style);
		setText(docente, "Docente");

		HSSFCell ipi = cabecalho.createCell((short) 3);
		ipi.setCellStyle(style);
		setText(ipi, "Ipi");

		
		// Percorrendo lista de papéis e adicionando linhas
		int i = 1;
		ClassificacaoRelatorioDao dao = getDAO(ClassificacaoRelatorioDao.class);
		list = dao.relatorioAvaliacaoPesquisa(Integer.parseInt(getParameter("id")));
 
		Iterator<HashMap<String, Object>> it = list.iterator();
		while(it.hasNext()){
			Map<String, Object> l = it.next();

			HSSFRow row = sheet.createRow(i);
 
			// Cria primeira coluna
			HSSFCell cell1 = row.createCell((short) 0);
			setText(cell1, (String) l.get("centro")); 

			// Cria a segunda coluna
			HSSFCell cell2 = row.createCell((short) 1);
			setText(cell2, (String) l.get("depto"));

			// Cria a terceira coluna
			HSSFCell cell3 = row.createCell((short) 2);
 			setText(cell3, (String) l.get("docente"));

			// Cria a quarta coluna
			HSSFCell cell4 = row.createCell((short) 3);
			setText(cell4, String.valueOf(l.get("ipi")));

			i++;
		}
 
	}
}