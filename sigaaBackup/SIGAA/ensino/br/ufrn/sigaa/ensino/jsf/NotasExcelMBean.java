/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '18/09/2009'
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.web.jsf.AbstractExcelMBean;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.stricto.dominio.ConceitoNota;
import br.ufrn.sigaa.ensino.stricto.negocio.ConceitoNotaHelper;
import br.ufrn.sigaa.ensino.util.TurmaUtil;

/**
 * Classe que gera planilhas XLS com as notas dos alunos.
 * 
 * @author Fred_Castro
 *
 */
@Component("notasExcel") @Scope("request")
public class NotasExcelMBean extends AbstractExcelMBean{

	/** Objeto que guarda a turma selecionada. */
	private Turma turma;
	
	/** Objeto que guarda as informa��es das configura��es da turma selecionada. */
	private ConfiguracoesAva config;
	
	/** Refer�ncia ao bean de consolida��o de turmas. */
	private ConsolidarTurmaMBean ctBean;

	public NotasExcelMBean() {
	}	
	
	public NotasExcelMBean(ConsolidarTurmaMBean ctBean) {
		this.turma = ctBean.getTurma();
		this.config = ctBean.getConfig();
		this.ctBean = ctBean;
	}

	/**
	 * Cria a planilha que ser� exportada.
	 * 
	 * @param wb
	 * @return
	 */
	@Override
	protected void buildExcelDocument(HSSFWorkbook wb) throws Exception , DAOException {
		
		int x = 1, y = 1;
		
		HSSFSheet sheet = wb.createSheet();
		sheet.setProtect(true);
		
		// Adicionando Estilo
		HSSFFont negrito = wb.createFont();
		negrito.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle style = wb.createCellStyle();
		style.setFont(negrito);
		style.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		style.setBorderRight(HSSFCellStyle.BORDER_HAIR);
		style.setLocked(true);
		
		HSSFCellStyle styleDescricao = wb.createCellStyle();
		styleDescricao.setFont(negrito);
		styleDescricao.setLocked(true);
		
		HSSFCellStyle styleBordaEsquerda = wb.createCellStyle();
		styleBordaEsquerda.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleBordaEsquerda.setLocked(true);
		
		HSSFCellStyle styleBordaDireita = wb.createCellStyle();
		styleBordaDireita.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleBordaDireita.setLocked(true);
		
		HSSFCellStyle styleBordaBaixo = wb.createCellStyle();
		styleBordaBaixo.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleBordaBaixo.setLocked(true);
		
		HSSFCellStyle styleBordaCima = wb.createCellStyle();
		styleBordaCima.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleBordaCima.setLocked(true);
		
		HSSFCellStyle styleBordaBaixoCima = wb.createCellStyle();
		styleBordaBaixoCima.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleBordaBaixoCima.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleBordaBaixoCima.setLocked(true);
		
		HSSFDataFormat format = wb.createDataFormat();

		HSSFCellStyle styleCampo = wb.createCellStyle();
		styleCampo.setFont(negrito);
		styleCampo.setDataFormat((short) 2);
		styleCampo.setLocked(true);
		styleCampo.setDataFormat(format.getFormat("0.0"));
		styleCampo.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleCampo.setBorderRight(HSSFCellStyle.BORDER_HAIR);

		HSSFCellStyle styleCampoEditavel = wb.createCellStyle();
		styleCampoEditavel.setFont(negrito);
		styleCampoEditavel.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		styleCampoEditavel.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleCampoEditavel.setDataFormat((short) 2);
		styleCampoEditavel.setLocked(false);
		styleCampoEditavel.setDataFormat(format.getFormat("0.0"));
		styleCampoEditavel.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleCampoEditavel.setBorderRight(HSSFCellStyle.BORDER_HAIR);
		
		HSSFCellStyle styleCampoInteiroEditavel = wb.createCellStyle();
		styleCampoInteiroEditavel.setFont(negrito);
		styleCampoInteiroEditavel.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		styleCampoInteiroEditavel.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleCampoInteiroEditavel.setDataFormat((short) 2);
		styleCampoInteiroEditavel.setLocked(false);
		styleCampoInteiroEditavel.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		styleCampoInteiroEditavel.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleCampoInteiroEditavel.setBorderRight(HSSFCellStyle.BORDER_HAIR);
		
		HSSFCellStyle styleHeader = wb.createCellStyle();
		styleHeader.setFont(negrito);
		styleHeader.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		styleHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleHeader.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleHeader.setBorderRight(HSSFCellStyle.BORDER_HAIR);
		
		HSSFCellStyle styleSubHeader = wb.createCellStyle();
		styleSubHeader.setFont(negrito);
		styleSubHeader.setFillForegroundColor((short) 44);
		styleSubHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleSubHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleSubHeader.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
		styleSubHeader.setBorderRight(HSSFCellStyle.BORDER_HAIR);
 
		criarCelula(sheet, 1, y++, "PLANILHA DE NOTAS", HSSFCell.CELL_TYPE_STRING, styleDescricao);
		
		criarCelula(sheet, 1, y++, turma.getDescricaoSemDocente(), HSSFCell.CELL_TYPE_STRING, styleDescricao);
		
		criarCelula(sheet, 1, y++, "", HSSFCell.CELL_TYPE_STRING, styleDescricao);
		
		if ( ctBean.isCompetencia() )
			criarCelula(sheet, 1, y++, "Digite \"Apto\" ou \"N�o Apto\" na coluna Resultado.", HSSFCell.CELL_TYPE_STRING, styleDescricao);
		else if ( ctBean.isConceito() )
			criarCelula(sheet, 1, y++, "Digite o conceito do aluno com letra mai�scula na coluna Resultado.", HSSFCell.CELL_TYPE_STRING, styleDescricao);
		else	
			criarCelula(sheet, 1, y++, "Digite as notas das unidades utilizando v�rgula para separar a casa decimal.", HSSFCell.CELL_TYPE_STRING, styleDescricao);
	
		criarCelula(sheet, 1, y++, "O campo faltas deve ser preenchido com o n�mero de faltas do aluno durante o per�odo letivo.", HSSFCell.CELL_TYPE_STRING, styleDescricao);
		criarCelula(sheet, 1, y++, "As notas das unidades n�o v�o para o hist�rico do aluno, no entanto, aparecem em seu portal.", HSSFCell.CELL_TYPE_STRING, styleDescricao);

		criarCelula(sheet, 1, y++, "Altere somente as c�lulas em amarelo.", HSSFCell.CELL_TYPE_STRING, styleDescricao);

		y++;
		
		criarCelula(sheet, x++, y, "Matr�cula", HSSFCell.CELL_TYPE_STRING, styleHeader);
		criarCelula(sheet, x++, y, "Nome", HSSFCell.CELL_TYPE_STRING, styleHeader);
		
		if (ctBean.getNotas() == null && !ctBean.isConceito() && !ctBean.isCompetencia())
			throw new NegocioException ("Esta turma n�o possui matr�culas.");
		
		// Escreve as unidades. Elas ocupam a quantidade de atividades em colunas.
		if( ctBean.isNota())
		{
			for (NotaUnidade u : ctBean.getNotas()){
				criarCelula(sheet, x, y, "Unid. " + u.getUnidade(), HSSFCell.CELL_TYPE_STRING, styleHeader);
				
				sheet.addMergedRegion(new Region(y, (short) x, y, (short) (x+u.getNumeroAvaliacoes()-1)));
				
				x += u.getNumeroAvaliacoes();
				
				// Se for uma turma de ead, adiciona as notas do tutor.
				if (ctBean.isEad() && ctBean.isDuasNotas()){
					criarCelula(sheet, x++, y, "Unid. " + u.getUnidade() + " x " + ctBean.getPesoProfessor() + "%", HSSFCell.CELL_TYPE_STRING, styleHeader);
					criarCelula(sheet, x++, y, "Tutor " + u.getUnidade(), HSSFCell.CELL_TYPE_STRING, styleHeader);
					criarCelula(sheet, x++, y, "Tutor " + u.getUnidade() + " x " + ctBean.getPesoTutor() + "%", HSSFCell.CELL_TYPE_STRING, styleHeader);
				}
			}
		}
		
		// Vari�vel que guarda a lista de todos os conceitos, vari�vel usada no m�todo formulaConceito. Nescess�ria devido a coopera��o.
		Collection<ConceitoNota> conceitos = null;
		
		if( ctBean.isConceito() )
			conceitos = ctBean.getConceitos();
				
		if (ctBean.isNota() && !ctBean.isLato() && !ctBean.isEad())
			criarCelula(sheet, x++, y, "Rec.", HSSFCell.CELL_TYPE_STRING, styleHeader);
		
		if (ctBean.isEad() && ctBean.isUmaNota())
			criarCelula(sheet, x++, y, "Tutor", HSSFCell.CELL_TYPE_STRING, styleHeader);
		
		if (ctBean.isNota() && ctBean.isEad())
			criarCelula(sheet, x++, y, "Rep.", HSSFCell.CELL_TYPE_STRING, styleHeader);
		
		sheet.setColumnWidth((short)x, (short)2);
		criarCelula(sheet, x++, y, "Resultado", HSSFCell.CELL_TYPE_STRING, styleHeader);
		
		criarCelula(sheet, x++, y, "Faltas", HSSFCell.CELL_TYPE_STRING, styleHeader);
		criarCelula(sheet, x, y, "Sit.", HSSFCell.CELL_TYPE_STRING, styleHeader);
		
		y++;
		x = 3;
		
		// Escreve a label das atividades.
		if (ctBean.isAvaliacao()){

			criarCelula(sheet, 1, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
			criarCelula(sheet, 2, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
			
			for (NotaUnidade u : ctBean.getNotas()){
				
				if ( u.getAvaliacoes() != null )
				{	
					for (Avaliacao a : u.getAvaliacoes())
						criarCelula(sheet, x++, y, a.getAbreviacao(), HSSFCell.CELL_TYPE_STRING, styleSubHeader);
				}
				
				// Escreve a label da unidade.
				criarCelula(sheet, x++, y, "   Nota   ", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
				
				if (ctBean.isEad()){
					criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
					criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
					criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
				}
			}
			
			criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
			criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
			criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);
			criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_STRING, styleSubHeader);

			y++;
		}
				
		// vari�vel que guarda a frequ�ncia m�nima para ser aprovado numa turma, ele � usada para fazer apenas uma busca no banco na hora de escrever a situa��o do usu�rio.
		float frequenciaMinima = ctBean.getParametrosAcademicos().getFrequenciaMinima();
		int minutosAulaRegular = ctBean.getParametrosAcademicos().getMinutosAulaRegular();
		// Escreve as notas dos alunos.
		for (MatriculaComponente mc : turma.getMatriculasDisciplina()){
			
			x = 1;
			int somaDosPesos = 0;
			int numeroDeUnidades = 0;
			
			// Escreve matr�cula e nome.
			criarCelula(sheet, x++, y, ""+mc.getDiscente().getMatricula(), HSSFCell.CELL_TYPE_STRING, style);
			criarCelula(sheet, x++, y, mc.getDiscente().getPessoa().getNome(), HSSFCell.CELL_TYPE_STRING, style);
			
			// Vari�veis que guardam a coluna onde a nota de cada unidade foi escrita. Auxiliam na gera��o das f�rmulas
			int auxUnidade = 0;
			int [] unidade = new int [10];
			
			// Escreve as notas do aluno
			for (NotaUnidade nu : mc.getNotas()){
				
				int somaPesos = 0;
				String formulaUnidade = "";
				String formula = "";
				String aux1 = "";
				String aux2 = "";
				
				Character tipoMedia = null;
								
				if (config != null)
					tipoMedia = config.getTipoMediaAvaliacoes(nu.getUnidade());
				
				if (ctBean.isAvaliacao()){
					
					if ( nu.getAvaliacoes() != null )
					{	
						for (Avaliacao a : nu.getAvaliacoes()){
													
							if (tipoMedia != null)
								switch (tipoMedia){
									case 'P':
										aux1 += "IF(AND("+ getLetraCelula(x) + (y + 1) +"<>\"-\";"+ getLetraCelula(x) + (y + 1) +"<>\"\");";
										aux2 += ";\"-\")";				
										formulaUnidade += (formulaUnidade.equals("") ? "" : "+" ) + getLetraCelula(x) + (y + 1) + "*" + a.getPeso();
										somaPesos += a.getPeso();
									break;
									case 'A': case 'S': 
										aux1 += "IF(AND("+ getLetraCelula(x) + (y + 1) +"<>\"-\";"+ getLetraCelula(x) + (y + 1) +"<>\"\");";
										aux2 += ";\"-\")";	
										formulaUnidade += (formulaUnidade.equals("") ? "" : "+") + getLetraCelula(x) + (y + 1); break;
								}
							
							if (a.getNota() != null)
								criarCelula(sheet, x++, y, ""+a.getNota(), HSSFCell.CELL_TYPE_NUMERIC, styleCampoEditavel);
							else
								criarCelula(sheet, x++, y, "-", HSSFCell.CELL_TYPE_STRING, styleCampoEditavel);
						}
					}	
				}
				unidade[auxUnidade++] = x;

				if (tipoMedia == null || config == null || nu.getAvaliacoes() == null || nu.getAvaliacoes().isEmpty()){
					if (nu.getNota() != null)
						criarCelula(sheet, x++, y, ""+nu.getNota(), HSSFCell.CELL_TYPE_NUMERIC, styleCampoEditavel);
					else
						criarCelula(sheet, x++, y, "-", HSSFCell.CELL_TYPE_STRING, styleCampoEditavel);
				} else {
					switch (tipoMedia){
						case 'P': formulaUnidade = "("+formulaUnidade+")/"+somaPesos; break;
						case 'A': formulaUnidade = "("+formulaUnidade+")/" + nu.getAvaliacoes().size(); break;
					}
					formula = aux1 + formulaUnidade + aux2;
					criarCelula(sheet, x++, y, formula, HSSFCell.CELL_TYPE_FORMULA, styleCampo);
				}
				
				if (ctBean.isEad() && ctBean.isDuasNotas()){
					String formulaProfessor = "IF( OR(" +getLetraCelula(x-1)+(y+1)+"=\"\","+getLetraCelula(x-1)+(y+1)+"=\"-\"),"+
												"\"-\","+
												getLetraCelula(x-1)+(y+1)+"*"+(ctBean.getPesoProfessor() / 100.0)+")";
					criarCelula(sheet, x++, y, formulaProfessor, HSSFCell.CELL_TYPE_FORMULA, styleCampo);
					
					if (nu.getUnidade() == 1){
						if (mc.getNotaTutor() != null){
							criarCelula(sheet, x++, y, ""+mc.getNotaTutor(), HSSFCell.CELL_TYPE_NUMERIC, styleCampo);
						} else
							x++;
						String formulaTutor1 = "IF( OR(" +getLetraCelula(x-1)+(y+1)+"=\"\","+getLetraCelula(x-1)+(y+1)+"=\"-\"),"+
													"\"-\","+
													getLetraCelula(x-1)+(y+1)+"*"+(ctBean.getPesoTutor() / 100.0)+")";
						criarCelula(sheet, x++, y, formulaTutor1, HSSFCell.CELL_TYPE_FORMULA, styleCampo);
					} else if (nu.getUnidade() == 2){
						if (mc.getNotaTutor2() != null){
							criarCelula(sheet, x++, y, ""+mc.getNotaTutor2(), HSSFCell.CELL_TYPE_NUMERIC, styleCampo);
						} else
							x++;
						String formulaTutor2 = "IF( OR(" +getLetraCelula(x-1)+(y+1)+"=\"\","+getLetraCelula(x-1)+(y+1)+"=\"-\"),"+
													"\"-\","+
													getLetraCelula(x-1)+(y+1)+"*"+(ctBean.getPesoTutor() / 100.0)+")";
						criarCelula(sheet, x++, y, formulaTutor2, HSSFCell.CELL_TYPE_FORMULA, styleCampo);
					}
				}
				
				if( ctBean.isNota() )
				{
					somaDosPesos += Integer.parseInt(nu.getPeso());
					numeroDeUnidades++;
				}	
			}
			
			if (ctBean.isNota() && !ctBean.isLato() && !ctBean.isEad()){
				if (mc.getRecuperacao() != null)
					criarCelula(sheet, x++, y, ""+mc.getRecuperacao(), HSSFCell.CELL_TYPE_NUMERIC, styleCampoEditavel);
				else
					criarCelula(sheet, x++, y, "-", HSSFCell.CELL_TYPE_STRING, styleCampoEditavel);
			}
			
			if (ctBean.isEad() && ctBean.isUmaNota()){
				if (mc.getNotaTutor() != null)
					criarCelula(sheet, x++, y, ""+mc.getNotaTutor(), HSSFCell.CELL_TYPE_NUMERIC, styleCampo);
				else
					x++;
			}
			
			if (ctBean.isNota() && ctBean.isEad()){
				
				if (mc.getRecuperacao() != null)
					criarCelula(sheet, x++, y, ""+mc.getRecuperacao(), HSSFCell.CELL_TYPE_NUMERIC, styleCampoEditavel);
				else
					criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_STRING, styleCampoEditavel);
			}
			
			// Exibe a nota do aluno.
			if (ctBean.isNota()){
				
				if ( !ctBean.getTurma().isConsolidada() ){
					if (ctBean.isEad()){
						String auxFormula = "";
						String menor = "";
											
						for (int i = 0; i < auxUnidade; i++){
							// auxFormula: Soma das notas do professor(80%) e do tutor(20%) da todas as unidades.
							auxFormula += (auxFormula.equals("") ? "" : "+") + "SUM(" + getLetraCelula(unidade[i]+1)+(y+1)+","+getLetraCelula(unidade[i]+3)+(y+1)+")";
							menor += (menor.equals("") ? "" : ",") + "SUM(" + getLetraCelula(unidade[i]+1)+(y+1)+","+getLetraCelula(unidade[i]+3)+(y+1)+")";
						}
						
						// Pega a menor nota de todas as unidade
						menor = "MIN(" + menor + ")";
						
						//Se a reposi��o for a menor nota, a nota do aluno permanece igual, sen�o a reposi��o substitui a menor nota. 
						criarCelula(sheet, x++, y,
								"IF(OR("+getLetraCelula(x-2)+(y+1)+"=\"\","+getLetraCelula(x-2)+(y+1)+"<="+menor+")," +
									"(("+auxFormula+")/"+auxUnidade+")," +
									"(((("+auxFormula+"+"+getLetraCelula(x-2)+(y+1)+"-"+menor+")/"+auxUnidade+"))))", HSSFCell.CELL_TYPE_FORMULA, styleCampo);
					}else {
						
						String unidades = "";
						String mediaParcial = "";
						String recup = "";
						String formula = "";
						String formulaRec = ""; 
						
						// F�rmulas auxiliares que comp�e a pr�-formula
						String aux1 = "";
	
						// Esta f�rmula identifica se o resultado deve ser exibido ou n�o
						// O resultado s� deve ser exibido caso todas as notas estejam preenchidas corretamente
						String preFormula = "";
						
						String[] pesos = TurmaUtil.getArrayPesosUnidades(turma);
						String pesoUnidade = "";

						if ( numeroDeUnidades == 0 )
							numeroDeUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
						
						for (int i = 0 ; i < numeroDeUnidades ; i++ ) {
							
							// Parte da f�rmula que verifica se todas as notas foram preenchidas. 
							aux1 += (unidades.equals("") ? "OR(" : ";") + getLetraCelula(unidade[i]) + (y + 1) +"=\"-\"," + getLetraCelula(unidade[i]) + (y + 1) +"=\"\"";
						
							pesoUnidade = pesos[i];
							unidades += (unidades.equals("") ? "" : "+") + "("+getLetraCelula(unidade[i])+(y+1)+ "*" + pesoUnidade + "*10)/10";
						}
						
						mediaParcial = "(round(((" + unidades + ")/" + somaDosPesos + ")*10;0)/10)";
						aux1 += ")";
						
						recup = getLetraCelula(x-1)+""+(y+1);
						formulaRec = "round(((" + mediaParcial + "*10+" + recup + "*10)/20);1)"; 
						formula = mediaParcial + ",";
						preFormula = "IF("+aux1+";\"-\";"+"IF(OR("+getLetraCelula(x-1)+(y+1)+"=\"\","+getLetraCelula(x-1)+(y+1)+"<0,"+getLetraCelula(x-1)+(y+1)+"=\"-\"),";
						
						criarCelula(sheet, x++, y,
								preFormula +
								formula +
								formulaRec + ")))", HSSFCell.CELL_TYPE_FORMULA, styleCampo);
					}
				} else	{
					criarCelula(sheet, x++, y, mc.getMediaFinal().toString() , HSSFCell.CELL_TYPE_FORMULA, styleCampo);
				}
			}
			
			if (ctBean.isConceito()){
				if (mc.getConceitoChar() != null)
					criarCelula(sheet, x++, y, ""+mc.getConceitoChar(), HSSFCell.CELL_TYPE_STRING, styleCampoEditavel);
				else
					x++;
			}
			
			if (ctBean.isCompetencia()){
				String result = null;
				if ( mc.getApto() == null )
					result = "";
				else if ( mc.getApto() == true )
					result = "Apto";
				else if ( mc.getApto() == false )
					result = "N�o Apto";
				
				criarCelula(sheet, x++, y, result , HSSFCell.CELL_TYPE_STRING, styleCampoEditavel);
			}
			
			// Exibe as faltas do aluno
			if (mc.getNumeroFaltas() != null)
				criarCelula(sheet, x++, y, ""+mc.getNumeroFaltas(), HSSFCell.CELL_TYPE_NUMERIC, styleCampoInteiroEditavel);
			else
				criarCelula(sheet, x++, y, "", HSSFCell.CELL_TYPE_NUMERIC, styleCampoInteiroEditavel);
			
			// Escreve a situa��o do Usu�rio.
			if (ctBean.isNota()){

				String situacao = "";
				situacao += 
					"IF(OR("+getLetraCelula(x-2)+(y+1)+"=\"-\","+getLetraCelula(x-2)+(y+1)+"=\"\")," + 
						"\"-\" ," +	
						"IF("+getLetraCelula(x-1)+(y+1)+">" + turma.getDisciplina().getMaximoFaltasPermitido(frequenciaMinima, minutosAulaRegular) + "," +
							"IF("+getLetraCelula(x-2)+(y+1)+">="+ctBean.getMediaMinima()+","+
								"\"REPF\"," +
								"\"REMF\"" + ")," +
							"IF(OR("+getLetraCelula(x-3)+(y+1)+"=\"\","+getLetraCelula(x-3)+(y+1)+"=\"-\"),"+
								"IF("+getLetraCelula(x-2)+(y+1)+">="+ctBean.getMediaMinimaPassarPorMedia()+"," +
									"\"APR\"," +
									"IF("+getLetraCelula(x-2)+(y+1)+">=3," +
										"\"REC\"," +
										"\"REP\"" +
									")" +
								")," +	
								"IF("+getLetraCelula(x-2)+(y+1)+">=" +ctBean.getMediaMinima()+ "," +
									"\"APR\"," +
									"\"REP\"" +
								")" +
							")" +
						")" +
					")";
				
				criarCelula(sheet, x, y,situacao, HSSFCell.CELL_TYPE_FORMULA, style);
			}

			if ( ctBean.isConceito() )
			{	
				criarCelula(sheet, x, y,
						"IF("+getLetraCelula(x-1)+(y+1)+">" + turma.getDisciplina().getMaximoFaltasPermitido(frequenciaMinima, minutosAulaRegular) + "," +
							"IF("+formulaConceito(getLetraCelula(x-2)+(y+1),conceitos)+">="+ctBean.getMediaMinimaPassarPorMedia()+"," + 
								"\"REPF\"," +
								"\"REMF\"" + ")," +
							"IF("+getLetraCelula(x-3)+(y+1)+"=\"\"," +
								"IF("+formulaConceito(getLetraCelula(x-2)+(y+1),conceitos)+">="+ctBean.getMediaMinimaPassarPorMedia()+"," +
									"\"APR\"," +
									"IF("+formulaConceito(getLetraCelula(x-2)+(y+1),conceitos)+">=3," +
										"\"REC\"," +
										"\"REP\"" +
									")" +
								")," +
								"IF("+formulaConceito(getLetraCelula(x-2)+(y+1),conceitos)+">=" +ctBean.getMediaMinima()+ "," +
									"\"APR\"," +
									"\"REP\"" +
								")" +
							")" +
						")", HSSFCell.CELL_TYPE_FORMULA, style);
			}
			
			if ( ctBean.isCompetencia() )
			{	
				String formulaNapto = "IF("+getLetraCelula(x-2)+(y+1)+"=\"N�o Apto\"," + "\"REP\"," +"\"--\"" +	")";
				String formulaApto = "IF("+getLetraCelula(x-2)+(y+1)+"=\"Apto\"," + "\"APR\"," + formulaNapto + ")"; 
				String formulaFaltas = "IF("+getLetraCelula(x-2)+(y+1)+"=\"Apto\"," + "\"REPF\",\"REMF\"),";
				String formula = "IF("+getLetraCelula(x-1)+(y+1)+">" + turma.getDisciplina().getMaximoFaltasPermitido(frequenciaMinima, minutosAulaRegular) + "," +
									formulaFaltas + formulaApto +")";
				
				criarCelula(sheet, x, y, formula, HSSFCell.CELL_TYPE_FORMULA, style);
			}
			y++;
		}
		
		x++;
		
		// Adiciona as bordas da tabela
		for (int i = 1; i < 8; i++){
			criarCelula(sheet, 0, i, "", HSSFCell.CELL_TYPE_STRING, styleBordaDireita);
			criarCelula(sheet, x, i, "", HSSFCell.CELL_TYPE_STRING, styleBordaEsquerda);
		}
		
		for (int i = 9; i < y; i++){
			criarCelula(sheet, 0, i, "", HSSFCell.CELL_TYPE_STRING, styleBordaDireita);
			criarCelula(sheet, x, i, "", HSSFCell.CELL_TYPE_STRING, styleBordaEsquerda);
		}
		
		for (int i = 1; i < x; i++){
			criarCelula(sheet, i, 0, "", HSSFCell.CELL_TYPE_STRING, styleBordaBaixo);
			criarCelula(sheet, i, 8, "", HSSFCell.CELL_TYPE_STRING, styleBordaBaixoCima);
			criarCelula(sheet, i, y, "", HSSFCell.CELL_TYPE_STRING, styleBordaCima);
		}
		
		
		// Mescla as primeiras linhas.
		sheet.addMergedRegion(new Region(1, (short) 1, 1, (short) (x - 1)));
		sheet.addMergedRegion(new Region(2, (short) 1, 2, (short) (x - 1)));
		sheet.addMergedRegion(new Region(3, (short) 1, 3, (short) (x - 1)));
		sheet.addMergedRegion(new Region(4, (short) 1, 4, (short) (x - 1)));
		sheet.addMergedRegion(new Region(5, (short) 1, 5, (short) (x - 1)));
		sheet.addMergedRegion(new Region(6, (short) 1, 6, (short) (x - 1)));
		sheet.addMergedRegion(new Region(7, (short) 1, 7, (short) (x - 1)));
		
		// Ajusta a largura das colunas.
		sheet.setColumnWidth((short) 0, (short) (3*256));
		sheet.setColumnWidth((short) 1, (short) (15*256));
		
		sheet.autoSizeColumn((short) 2);

		for (int i = 3; i < x; i++)
		{
			// Melhorando a visualiza��o da planilha dos m�todos de avalia��o de conceito e compet�ncia
			if ( ctBean.isCompetencia() || ctBean.isConceito() )
				sheet.setColumnWidth((short) i, (short) (8*256));
			else
				sheet.setColumnWidth((short) i, (short) (6*256));
		}
		sheet.setColumnWidth((short) (x-3), (short) (10*256));
		sheet.setColumnWidth((short) x, (short) (3*256));
		
	}
	
	/**
	 * Cria uma c�lula com o texto e o estilo passados.
	 * 
	 * @param planilha
	 * @param coluna
	 * @param linha
	 * @param texto
	 * @param estilo
	 * @return
	 */
	private HSSFCell criarCelula (HSSFSheet planilha, int coluna, int linha, String texto, int tipo, HSSFCellStyle estilo){
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

	@Override
	protected String getFileName() {
		return "notas_"+turma.getDisciplina().getCodigo().replaceAll("\\.","")+"_T"+turma.getCodigo()+"_"+turma.getAnoPeriodo().replaceAll("\\.","")+".xls";
	}
	
	/**
	 * Retorna a letra que representa a coluna referente ao n�mero passado, de acordo com a JXL.
	 * 
	 * @param celula
	 * @return
	 */
	private String getLetraCelula (int celula){
		switch (celula){
			case 0: return "A";
			case 1: return "B";
			case 2: return "C";
			case 3: return "D";
			case 4: return "E";
			case 5: return "F";
			case 6: return "G";
			case 7: return "H";
			case 8: return "I";
			case 9: return "J";
			case 10: return "K";
			case 11: return "L";
			case 12: return "M";
			case 13: return "N";
			case 14: return "O";
			case 15: return "P";
			case 16: return "Q";
			case 17: return "R";
			case 18: return "S";
			case 19: return "T";
			case 20: return "U";
		}
		
		return "A";
	}
	
	/**
	 * Importa a planilha preenchida pelo professor.<br/><br/>
	 * 
	 * <ul>
	 * 		<li>M�todo chamado pelo m�todo importarPlanilha na classe ConsolidarTurmaMBean.</li>
	 * 		<li>N�o � utilizado por JSPs.</li>
	 * </ul>
	 * 
	 * @throws IOException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */

	public void importarPlanilha () throws NegocioException, IOException, HibernateException, DAOException {
		
		UploadedFile arquivoPlanilha = ctBean.getArquivoPlanilha();
		if (!FilenameUtils.isExtension(arquivoPlanilha.getName(), "xls")) {
			addMensagemErro("Apenas arquivos com extens�o XLS podem ser importados.");
			return;
		}
		
		HSSFWorkbook wb = new HSSFWorkbook(arquivoPlanilha.getInputStream());
		
		HSSFSheet planilha = wb.getSheetAt(0);
		
		short x = 1;
		int y = 9;
		
		HSSFRow linha = null;
		HSSFCell celula = null;
		
		// Verifica se o arquivo enviado � desta turma.
		linha = planilha.getRow(2);
		if ( linha != null )
			celula = linha.getCell((short)1);
		
		if (celula == null || !ctBean.getTurma().getDescricaoSemDocente().equals(celula.getRichStringCellValue().getString()))
			throw new NegocioException ("A planilha enviada n�o corresponde a esta turma.");
		
		// Mapa que guarda os nomes das avalia��es e indica em qual �ndice elas est�o.
		Map <String, Short> avaliacoes = new HashMap <String, Short> ();
		// Lista que guarda os nomes das avalia��es na ordem correta.
		List <String> nomesAvaliacoes = new ArrayList <String> ();

		linha = planilha.getRow(y);
		celula = linha.getCell(x);
		
		String aux = "";
		
		boolean encontrou = false;
		short posRec = 0, posFaltas = 0;
		// Identifica a posi��o da recupera��o e faltas.				
		while (!encontrou && x < 1000){
			celula = linha.getCell(x++);
			
			if (celula != null && celula.getRichStringCellValue() != null){
				aux = celula.getRichStringCellValue().getString();
				if (aux.equals("Rec.") || aux.equals("Rep."))
					posRec = (short) (x-1);
				
				if (aux.equals("Faltas"))
					posFaltas = (short) (x-1);								
			}
			if ( !ctBean.isConceito() && !ctBean.isCompetencia() )
				encontrou = (posRec > 0 && posFaltas > 0);
			else if ( ctBean.isConceito() || ctBean.isCompetencia() )
				encontrou = ( posFaltas > 0 );
		}
		
		// Salva o �ndice da recupera��o e faltas.
		if ( !ctBean.isConceito() && !ctBean.isCompetencia() )
			avaliacoes.put("Rec.", posRec);	
		avaliacoes.put("Faltas", posFaltas);
			
			
		final int X_REC = x-1;
		
		// Identifica se alguma unidade foi dividida.
		boolean dividido = false;
		
		x = 2;
		y = 10;
		celula = planilha.getRow(y).getCell(x);
		
		// Verifica se alguma unidade foi dividida.
		if (celula.getRichStringCellValue() == null || StringUtils.isEmpty(celula.getRichStringCellValue().getString()))
			dividido = true;
		else {
			dividido = false;
			y = 9;
		}
		
		linha = planilha.getRow(y);
		
		int c = 1;
		
		// Identifica os �ndices de todas as avalia��es.
		while (x < X_REC){
			
			if (linha.getCell(x) != null && linha.getCell(x).getRichStringCellValue() != null)
				aux = linha.getCell(x).getRichStringCellValue().getString().trim();

			// Ignora as notas dos tutores e identifica a unidade, caso seja dividida.
			if (( dividido && !StringUtils.isEmpty(aux) || !dividido && aux.startsWith("Unid.") && !aux.contains("x") ) 
				|| ( (ctBean.isConceito() || ctBean.isCompetencia()) && aux.equals("Resultado"))){
				String nomeAvaliacao = aux + (aux.equals("Nota") ? " " + c++ : "");
				avaliacoes.put(nomeAvaliacao, x);
				nomesAvaliacoes.add(nomeAvaliacao);
			}
			
			x++;
		}
		
		if ( !ctBean.isConceito() && !ctBean.isCompetencia() )
			nomesAvaliacoes.add("Rec.");
		
		nomesAvaliacoes.add("Faltas");
		
		y++;
		
		linha = planilha.getRow(y);
		boolean fim = false;
		
		Map <Long, List <Double>> matriculaNotas = new HashMap <Long, List <Double>> ();
		ArrayList<String> notaConceitos = getDescricoesConceito();
		
		// L� as notas dos alunos
		while (!fim){
			
			linha = planilha.getRow(y);
			
			celula = linha.getCell((short) 1);
			
			String matricula = "";
			if (celula.getRichStringCellValue() != null)
				matricula = celula.getRichStringCellValue().getString();
			
			// Se encontrou uma matr�cula, pega as notas.
			if (!StringUtils.isEmpty(matricula)){
				
				List <Double> notas = new ArrayList <Double> ();
				
				// Para cada avalia��o encontrada, pega os valores.
				for (String avaliacao : nomesAvaliacoes){
					
					Double nota = 0.0;
					boolean semNota = false;
					
					celula = linha.getCell(avaliacoes.get(avaliacao));
					if (celula != null){
						if (celula.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
						{	
							nota = celula.getNumericCellValue();
	
							if ( avaliacao.equals("Faltas") && nota > ctBean.getMaxFaltasTotal() )
								throw new NegocioException ("N�mero de Faltas Inv�lido. O n�mero m�ximo de faltas �: " +ctBean.getMaxFaltasTotal());
							
							if ( (ctBean.isConceito() || ctBean.isCompetencia()) && !avaliacao.equals("Faltas") )
								throw new NegocioException ("Valores num�ricos n�o s�o v�lidos como notas no m�todo de avalia��o por " + ( ctBean.isCompetencia() ? "Compet�ncia." : "Conceito"));
						}
						else if (celula.getCellType() == HSSFCell.CELL_TYPE_FORMULA){
							nota = celula.getNumericCellValue();
							
							String notaVazia = celula.getRichStringCellValue().getString();
							if ( notaVazia.equals("-") && ctBean.isNota() )
							{
								nota = -1.0;
								semNota = true;
							}	
							
							if ( avaliacao.equals("Faltas") && nota > ctBean.getMaxFaltasTotal() )
								throw new NegocioException ("N�mero de Faltas Inv�lido. O n�mero m�ximo de faltas �: " +ctBean.getMaxFaltasTotal());
							
							if ( (ctBean.isConceito() || ctBean.isCompetencia()))
								throw new NegocioException ("F�rmulas n�o s�o permitidas no m�todo de avalia��o por " + ( ctBean.isCompetencia() ? "Compet�ncia." : "Conceito"));
						} else {
							String auxNota = celula.getRichStringCellValue().getString();
							
							if (!StringUtils.isEmpty (auxNota))
								try {
									if ( ctBean.isConceito() )
									{										
										if ( notaConceitos.contains(auxNota) )
											nota = ConceitoNotaHelper.getValorConceito(auxNota);
										else if ( auxNota.equals("") || auxNota.equals("-") )
											nota = -1.0;
										else if ( avaliacao.equals("Faltas") )
											throw new NegocioException (auxNota + " n�o � um valor v�lido para a coluna Faltas.");
										else	
											throw new NegocioException (auxNota + " n�o � um valor v�lido para a coluna Resultado.");
									}
									else if ( ctBean.isCompetencia() )
									{
										if ( auxNota.equalsIgnoreCase("apto") )
											nota = 1.0;
										else if ( auxNota.equalsIgnoreCase("n�o apto") || auxNota.equalsIgnoreCase("nao apto"))
											nota = 0.0;
										else if ( auxNota.equals("") )
											nota = -1.0;
										else if ( avaliacao.equals("Faltas") )
											throw new NegocioException (auxNota + " n�o � um valor v�lido para a coluna Faltas.");
										else	
											throw new NegocioException (auxNota + " n�o � um valor v�lido para a coluna Resultado.");
									}
									else
									{
										if ( auxNota.equals("-") && ctBean.isNota() )
										{
											nota = -1.0;
											semNota = true;
										}
										else
											nota = Double.parseDouble(auxNota);
									}	
								} catch (NumberFormatException e){
									throw new NegocioException ("As notas devem conter valores num�ricos.");
								}
							else {
								nota = -1.0;
								semNota = true;
							}
						}
					}
					
					if (!semNota && !ctBean.isConceito() && !ctBean.isCompetencia() && (nota < 0 || nota > 10) && !avaliacao.equals("Faltas"))
						throw new NegocioException ("As notas devem conter valor entre zero e dez.");

					notas.add(nota);
				}
				
				matriculaNotas.put(Long.parseLong(matricula), notas);
				
				y++;
			// Se n�o encontrou uma matr�cula, finaliza.
			} else
				fim = true;
		}
		
		// Salva no bean as informa��es lidas do arquivo.
		ctBean.setNomesAvaliacoesImportacao(nomesAvaliacoes);
		ctBean.setMatriculasNotasImportacao(matriculaNotas);
		
		List <Long> matriculas = new ArrayList <Long> ();
		
		ctBean.setMatriculasImportacao(matriculas);
	}
	
	/**
	 * Retorna uma formula Excel que faz o mapeamento entre uma c�lula da planilha que cont�m uma string que representa um conceito 
	 * e o valor deste conceito. Usada para calcular a situa��o do aluno cujo m�todo de avalia��o � por conceito. 
	 * N�o � utilizado por JSPs.
	 * 
	 * @param nota
	 * @param conceitos
	 * @return formula, uma f�rmula de mapeamento.
	 */
	public String formulaConceito ( String nota , Collection<ConceitoNota> conceitos )
	{
		String[] notasConceitoChar = new String[conceitos.size()];
		Integer[] notasConceitoValor = new Integer[conceitos.size()];
		int index = 0;
		
		for ( ConceitoNota conceito : conceitos )
		{
			notasConceitoChar[index] = conceito.getConceito();
			notasConceitoValor[index] = conceito.getValor();
			index++;
		}	
		
		String formula = "";

		for ( int i = 0 ; i <= notasConceitoChar.length-1 ; i++ )
		{
			formula += (formula.equals("") ? "" : ",") + "IF(" + nota + "=" + "\"" + notasConceitoChar[i] + "\"," + notasConceitoValor[i];   
		}
		for ( int i = 0 ; i <= notasConceitoChar.length-1 ; i++ )
			formula += ")";
		
		return formula;
	}
		
	/**
	 * Retorna uma lista de String com a descri��o de todos os conceitos
	 * N�o � utilizado por JSPs.
	 * 
	 * @return notaConceito.
	 */
	public ArrayList<String> getDescricoesConceito() throws DAOException
	{
		ArrayList<String> notaConceitos = null;
		if ( ctBean.isConceito() )
		{	
			List<ConceitoNota> conceitos = (List<ConceitoNota>) ctBean.getConceitos();
			notaConceitos = new ArrayList<String>();
			
			for ( ConceitoNota conceito : conceitos )
			{
				notaConceitos.add(conceito.getConceito());
			}
		}
		return notaConceitos;
	}
	
	/**
	 * Configura a planilha antes de exibi-la.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ensino/turma/buscar_turma.jsp</li>
	 * </ul>
	 */
	public void iniciar(ActionEvent evt) throws Exception  {
		
		Integer id = getParameterInt("id");
		Turma t = getGenericDAO().findByPrimaryKey(id, Turma.class);
		
		if (t == null) {
			addMensagemErro("Turma n�o encontrada.");
			return;
		}
		ConsolidarTurmaMBean consolidarBean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
		consolidarBean.setTurma(t);
		consolidarBean.prepararTurma(t);
		this.turma = consolidarBean.getTurma();
		this.config = consolidarBean.getConfig();
		this.ctBean = consolidarBean;
		try {
		buildSheet();
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return ;
		}
	}
	
}
