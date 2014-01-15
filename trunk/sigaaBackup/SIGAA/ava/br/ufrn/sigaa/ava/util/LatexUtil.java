package br.ufrn.sigaa.ava.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Métodos utilitários para operações com latex. 
 * 
 * @author Diego Jácome
 *
 */
public class LatexUtil {

	/** Delimitador de abertura de fórmula latex*/
	private static final String DELIMITADOR_ABERTURA_FORMULA_1 = "\\(";
	/** Delimitador de fechamento de fórmula latex*/
	private static final String DELIMITADOR_FECHAMENTO_FORMULA_1 = "\\)";
	/** Delimitador de abertura de fórmula latex*/
	private static final String DELIMITADOR_ABERTURA_FORMULA_2 = "\\[";
	/** Delimitador de fechamento de fórmula latex*/
	private static final String DELIMITADOR_FECHAMENTO_FORMULA_2 = "\\]";

	/**
	 * Dado uma string, busca todas as ocorrênicas de fórmulas latex dentro de \( \)
	 * substitui a fórmula por suas respectivas imagens.
	 * @param texto
	 * @return
	 * @throws IOException 
	 */
	public static String createStringImqLatex(String texto, ListaMensagens erros) throws IOException {
		
		if ( possuiFormulaLatex(texto) ){
			String conteudo = texto;
			String[] formulas = getFormulasLatex(conteudo);
			conteudo = adicionaMaisDelimitadores(conteudo);
			
			for ( String formula : formulas ) {
				formula = StringUtils.unescapeHTML(formula);
				String image = formulaToImage(formula,erros);
				conteudo = conteudo.replace("__"+formula+"__", image);
			}
			
			conteudo = substituiDelimitadores(conteudo);
			return conteudo;
		}
		return texto;
	}

	/**
	 * Remove os delimitadoes latex de uma string
	 * @param conteudo
	 * @return
	 * @throws IOException 
	 */
	private static String substituiDelimitadores(String conteudo) {
		conteudo = conteudo.replace(DELIMITADOR_ABERTURA_FORMULA_1, "");
		conteudo = conteudo.replace(DELIMITADOR_FECHAMENTO_FORMULA_1, "");
		conteudo = conteudo.replace(DELIMITADOR_ABERTURA_FORMULA_2, "<p>");
		conteudo = conteudo.replace(DELIMITADOR_FECHAMENTO_FORMULA_2, "</p>");
		return conteudo;
	}

	/**
	 * Adiciona mais delimitadoes a fórmula latex de uma string
	 * @param conteudo
	 * @return
	 * @throws IOException 
	 */
	private static String adicionaMaisDelimitadores(String conteudo) {
		conteudo = conteudo.replace(DELIMITADOR_ABERTURA_FORMULA_1, "\\(__");
		conteudo = conteudo.replace(DELIMITADOR_FECHAMENTO_FORMULA_1, "__\\)");
		conteudo = conteudo.replace(DELIMITADOR_ABERTURA_FORMULA_2, "\\[__");
		conteudo = conteudo.replace(DELIMITADOR_FECHAMENTO_FORMULA_2, "__\\]");
		return conteudo;
	}
	
	/**
	 * Retorna um  vetor de strings contendo todas as ocorrências de fórmulas latex no texto.
	 * @param texto
	 * @return
	 */
	public static String [] getFormulasLatex( String texto ){
		String [] res = StringUtils.substringsBetween(texto, DELIMITADOR_ABERTURA_FORMULA_1, DELIMITADOR_FECHAMENTO_FORMULA_1);
		if ( res != null )
			return res;
		res = StringUtils.substringsBetween(texto, DELIMITADOR_ABERTURA_FORMULA_2, DELIMITADOR_FECHAMENTO_FORMULA_2);
		if ( res != null )
			return res;
		return null;
	}
	
	/**
	 * Verifica se existe ocorrência de fórmulas latex no texto.
	 * @param texto
	 * @return
	 */
	public static boolean possuiFormulaLatex( String texto ){
		if ( texto == null ) return false;		
		String [] formulas = getFormulasLatex(texto);
		if ( formulas != null && formulas.length > 0 )
			return true;
		else return false;
		
	}
	
	/**
	 * Transforma fórmula latex numa tag img.
	 * @param formula
	 * @return
	 */
	private static String formulaToImage(String formula, ListaMensagens erros) throws IOException {
		String formulaLatex = StringUtils.toAsciiHtml(formula);
		
		try {
			TeXFormula fomule = new TeXFormula(formulaLatex);
			TeXIcon ti = fomule.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15);
			BufferedImage originalImage = new BufferedImage(ti.getIconWidth(), ti.getIconHeight(), BufferedImage.TYPE_3BYTE_BGR);
			
			Graphics2D g = (Graphics2D) originalImage.getGraphics();
			g.setBackground(Color.white);
			g.clearRect(0,0,ti.getIconWidth(), ti.getIconHeight());
			
			ti.paintIcon(new JLabel(), originalImage.getGraphics(), 0, 0);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( originalImage, "png", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();

			Integer idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, imageInByte, "image/png", "formula.png");
			String image = "<img style=\"vertical-align:middle;\" alt=\"\" src=\"/shared/verImagem?salvar=false&idArquivo="+idArquivo+"&key="+UFRNUtils.generateArquivoKey(idArquivo)+"\">";
			return image;
			
		} catch (ParseException e) {
			erros.addErro("Fórmula latex inválida.");
		}
		return ""; 
			
		
	}
}
