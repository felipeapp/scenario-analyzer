/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 29/08/2009
 */
package br.ufrn.sigaa.biblioteca.util;

import java.awt.Color;
import java.awt.Image;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.lang.StringUtils;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.UFRNException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EtiquetaImpressaoDataSource;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.GeraEtiquetaImpressaoMBean.CampoEtiquetaLombada;

import com.lowagie.text.pdf.Barcode128;

/**
 *
 * Classe respons�vel por auxiliar a gera��o das etiquetas da biblioteca em formato PDF 
 * usando JasperReport
 *
 * @author jadson
 * @author Fred_Castro
 * 
 * @since 29/08/2008
 * @version 1.0 cria��o da classe
 *
 */
public class EtiquetasImpressaoUtil {
	
	
	/**
	 * Exporta o PDF com as etiquetas para o response do contexto externo atual do faces em formato PDF.
	 * 
	 * @param arquivoJasper
	 * @param dados
	 * @throws ArqException
	 */
	public static void gerarPdfParaContextoAtualDoFaces (String arquivoJasper, EtiquetaImpressaoDataSource dados, Map<String, Object> paramentros) throws ArqException {
		
		// Pegando a inst�ncia atual do contexto do faces.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		// Pegando o response do contexto do faces.
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
	
		
		// Definindo que o mime-type ser� PDF.
		response.setContentType("application/pdf");

		// Definindo que a disposi��o do conte�do ser� inline (dentro do
		// pr�prio browser) e o nome do arquivo ser� etiquetas'NomedoTipo'.pdf
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		String data = formato.format(new Date());
		data = data.replace(" ", "_");
		response.setHeader("Content-disposition", "attachment; filename=etiquetas" + data + ".pdf");

		try {
			String path = "/br/ufrn/sigaa/biblioteca/arquivos/";
			
			// Pega o arquivo jasper, que precisa estar no mesmo diret�rio do .class desta classe.
			InputStream input = EtiquetasImpressaoUtil.class.getResourceAsStream(path+arquivoJasper);
			JasperPrint prt = JasperFillManager.fillReport(input, paramentros, dados);
            JasperExportManager.exportReportToPdfStream(prt, response.getOutputStream());
           
            // Finaliza a resposta.
            facesContext.responseComplete();
            
		} catch (Exception e) {
			throw new ArqException(e);
		}
	}
	
	
	/**
	 * <p>Monta os dados que v�o ser impressos na etiqueta de lombada. </p>
	 * 
	 * <p>Os dados nessa etiqueta depedem dos dados adicionados pelo usu�rio na p�gina de impress�o</p>
	 * 
	 * <p>Cada informa��o do material selecionada vai ser impressa em uma linha da etiqueta de lombada</p>
	 * 
	 * <p> Exce��o � o n�mero de chamda, neste caso cada espa�o em banco do numeroDeChamada vai gerar uma nova linha </p>
	 * 
	 * <p> Para cada linha da etiqueta � verificando se n�o passa do tamanho m�ximo de caracteres para que o texto 
	 * n�o ultrapasse a borda da etiqueta. </p>
	 * 
	 * @param numeroDeChamada
	 * @return
	 * @throws NegocioException
	 */
	public static String montaTextoLombada (List<CampoEtiquetaLombada> campos, MaterialInformacional m)  {
		
		List<String> linhasCampos = new ArrayList<String>();
		
		for (CampoEtiquetaLombada campo : campos) {
			linhasCampos.addAll(  campo.getLinhasEtiquetaLombada(m));
		}
		
		String [] linhas = linhasCampos.toArray(new String[0]);
		
		String resultado = "";
		
		String separador = "";
		
		for (String l: linhas){
			
			resultado += separador + l;
			
			if (separador.equals(""))
				separador = "\n";
		}

		return resultado;
	}
	
	
	/**
	 * Gera a imagem com o c�digo de barras passado.
	 * 
	 * @param codigoBarras
	 * @return null se o c�digo de barras for null ou vazio.
	 * @throws UFRNException
	 */
	public static Image geraImagemCodigoBarras(String codigoBarras){

		if (StringUtils.isEmpty(codigoBarras))
			return null;
		
		Barcode128 code128 = new Barcode128();
		code128.setCode(codigoBarras);
		
		return code128.createAwtImage(Color.BLACK, Color.WHITE);
	}
	
	
}