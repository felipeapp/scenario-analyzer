/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * SIGAA
 *
 * Created on 16/01/2012
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import br.ufrn.arq.web.jsf.AbstractExcelMBean;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/**
 * MBean reponsável por gerar a planilha XLS de contatos dos participantes de uma ação de extensão
 * 
 * @author julio
 */
public class ContatosExcelParticipanteAcaoExtensaoMBean extends AbstractExcelMBean{

	/**  Os participantes cujas informações vão ser impressas. */
	private List<ParticipanteAcaoExtensao> participantes;
	
	/** O titulo da atividade ou sub atividade de extensão cujos participantes estão sendo listados. **/
	private String tituloAtividade;
	
	public ContatosExcelParticipanteAcaoExtensaoMBean(String tituloAtividade, List<ParticipanteAcaoExtensao> participantes){
		this.participantes = participantes;
		this.tituloAtividade = tituloAtividade;
	}
	
	
	/**
	 * Criar planilha a ser exportada
	 * 
	 * @param wb
	 */
	@Override
	protected void buildExcelDocument(HSSFWorkbook wb) throws Exception {
		int x=1;
		
		HSSFSheet sheet = wb.createSheet();
		
		HSSFFont negrito = wb.createFont();
		negrito.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setFont(negrito);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		
		HSSFCellStyle styleDescricaoTop = wb.createCellStyle();
		styleDescricaoTop.setFont(negrito);
		styleDescricaoTop.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleDescricaoTop.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleDescricaoTop.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleDescricaoTop.setLocked(true);
		
		HSSFCellStyle styleDescricaoBottom = wb.createCellStyle();
		styleDescricaoBottom.setFont(negrito);
		styleDescricaoBottom.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleDescricaoBottom.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleDescricaoBottom.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleDescricaoBottom.setLocked(true);
		
		HSSFCellStyle styleBordaCompleta = wb.createCellStyle();
		styleBordaCompleta.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleBordaCompleta.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
		styleBordaCompleta.setBorderRight(HSSFCellStyle.BORDER_HAIR);
		styleBordaCompleta.setBorderTop(HSSFCellStyle.BORDER_HAIR);
		styleBordaCompleta.setLocked(true);
		
		HSSFCellStyle styleBordaCompletaLast = wb.createCellStyle();
		styleBordaCompletaLast.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleBordaCompletaLast.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
		styleBordaCompletaLast.setBorderRight(HSSFCellStyle.BORDER_HAIR);
		styleBordaCompletaLast.setBorderTop(HSSFCellStyle.BORDER_HAIR);
		styleBordaCompletaLast.setLocked(true);
		
		HSSFCellStyle styleHeader = wb.createCellStyle();
		styleHeader.setFont(negrito);
		styleHeader.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		styleHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleHeader.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleHeader.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleHeader.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
		styleHeader.setBorderRight(HSSFCellStyle.BORDER_HAIR);
		styleHeader.setLocked(true);
		
		HSSFCellStyle styleHeaderRight = wb.createCellStyle();
		styleHeaderRight.setFont(negrito);
		styleHeaderRight.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		styleHeaderRight.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleHeaderRight.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleHeaderRight.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleHeaderRight.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleHeaderRight.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleHeaderRight.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
		styleHeaderRight.setLocked(true);
		
		HSSFCellStyle styleHeaderLeft = wb.createCellStyle();
		styleHeaderLeft.setFont(negrito);
		styleHeaderLeft.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		styleHeaderLeft.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleHeaderLeft.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleHeaderLeft.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleHeaderLeft.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleHeaderLeft.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleHeaderLeft.setBorderRight(HSSFCellStyle.BORDER_HAIR);
		styleHeaderLeft.setLocked(true);
		
		HSSFCellStyle styleBodyLeft = wb.createCellStyle();
		styleBodyLeft.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleBodyLeft.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleBodyLeft.setLocked(true);
		
		HSSFCellStyle styleBodyRight = wb.createCellStyle();
		styleBodyRight.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleBodyRight.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleBodyRight.setLocked(true);
		
		HSSFCellStyle styleBodyLeftLast = wb.createCellStyle();
		styleBodyLeftLast.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleBodyLeftLast.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleBodyLeftLast.setLocked(true);
		
		HSSFCellStyle styleBodyRightLast = wb.createCellStyle();
		styleBodyRightLast.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleBodyRightLast.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleBodyRightLast.setLocked(true);
		
		criarCelula(sheet, x++, 1, "PLANILHA DE CONTATOS DOS PARTICIPANTES", HSSFCell.CELL_TYPE_STRING, styleDescricaoTop);
		criarCelula(sheet, x++, 1, "AÇÃO: "+tituloAtividade, HSSFCell.CELL_TYPE_STRING, styleDescricaoBottom);
		x++;
		criarCelula(sheet, x++, 1, "Nome", HSSFCell.CELL_TYPE_STRING, styleHeaderLeft);
		criarCelula(sheet, x-1, 2, "E-mail", HSSFCell.CELL_TYPE_STRING, styleHeader);
		criarCelula(sheet, x-1, 3, "Telefone", HSSFCell.CELL_TYPE_STRING, styleHeader);
		criarCelula(sheet, x-1, 4, "Celular", HSSFCell.CELL_TYPE_STRING, styleHeaderRight);
		criarCelula(sheet, x-1, 5, "Endereço", HSSFCell.CELL_TYPE_STRING, styleHeaderRight);
		
		Iterator<ParticipanteAcaoExtensao> it = participantes.iterator();
		
		while(it.hasNext()){
			
			ParticipanteAcaoExtensao p = it.next();
			
			criarCelula(sheet, x++, 1, p.getCadastroParticipante().getNome(), HSSFCell.CELL_TYPE_STRING, styleBodyLeft);
			criarCelula(sheet, x-1, 2, p.getCadastroParticipante().getEmail(), HSSFCell.CELL_TYPE_STRING, styleBordaCompleta);
			
			criarCelula(sheet, x-1, 3, ( p.getCadastroParticipante().getTelefone() != null ? p.getCadastroParticipante().getTelefone() : "---"), HSSFCell.CELL_TYPE_STRING, styleBordaCompleta);
			criarCelula(sheet, x-1, 4, ( p.getCadastroParticipante().getCelular()  != null ? p.getCadastroParticipante().getCelular()  : "---"), HSSFCell.CELL_TYPE_STRING, styleBordaCompleta);
			criarCelula(sheet, x-1, 5, p.getCadastroParticipante().getEnderecoCompleto(), HSSFCell.CELL_TYPE_STRING, styleBodyRight);
		}
		
		
		sheet.addMergedRegion(new Region(1,(short) 1, 1, (short)4));
		sheet.addMergedRegion(new Region(2,(short) 1, 2, (short)4));
		sheet.addMergedRegion(new Region(3,(short) 1, 3, (short)4));
		
		sheet.setColumnWidth((short) 0, (short) (3*256));
		sheet.setColumnWidth((short) 1, (short) (45*256)); // nome
		sheet.setColumnWidth((short) 2, (short) (30*256)); // email
		sheet.setColumnWidth((short) 3, (short) (30*256)); // telenone
		sheet.setColumnWidth((short) 4, (short) (30*256)); // celuclar
		sheet.setColumnWidth((short) 5, (short) (120*256)); // endereço
	}

	/**
	 * Retorna o nome do documento
	 */
	@Override
	protected String getFileName() {
		return "ContatosParticipantesAcao_"+tituloAtividade.replaceAll("\\.","")+".csv";
	}
	
	/**
	 * Cria uma célula com o texto e o estilo passados.
	 * 
	 * @param planilha
	 * @param coluna
	 * @param linha
	 * @param texto
	 * @param estilo
	 * @return
	 */
	private HSSFCell criarCelula (HSSFSheet planilha, int linha, int coluna, String texto, int tipo, HSSFCellStyle estilo){
		HSSFRow row = planilha.getRow(linha);
		if (row == null)
			row = planilha.createRow((short) linha);
		
		HSSFCell celula = row.getCell((short) coluna);
		
		if (celula == null)
			celula = row.createCell((short) coluna);

		if (estilo != null)
			celula.setCellStyle(estilo);
		
		if (tipo == HSSFCell.CELL_TYPE_FORMULA)
			celula.setCellFormula(texto);
		else if (tipo == HSSFCell.CELL_TYPE_NUMERIC)
			celula.setCellValue(Double.parseDouble(texto));
		else
			setText(celula, texto);

		celula.setCellType(tipo);
		
		return celula;
	}

}
