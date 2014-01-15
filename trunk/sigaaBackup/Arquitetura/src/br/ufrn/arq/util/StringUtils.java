/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/04/2006
 */
package br.ufrn.arq.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Métodos úteis para se trabalhar com Strings.
 *
 * @author David Ricardo
 * @author Gleydson Lima
 * @author Johnny Marçal
 *
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

	
	private static final String PLAIN_ASCII =  
	      "AaEeIiOoUu"    // grave
	    + "AaEeIiOoUuYy"  // acute
	    + "AaEeIiOoUuYy"  // circumflex
	    + "AaEeIiOoUuYyNn"  // tilde
	    + "AaEeIiOoUuYy"  // umlaut
	    + "Aa"            // ring
	    + "Cc"            // cedilla
	    ;

	private static final String UNICODE =
	      "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"             // grave
	    + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" // acute
	    + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" // circumflex
	    + "\u00C3\u00E3EeIiÕõUuYyÑñ" // tilde
	    + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" // umlaut
	    + "\u00C5\u00E5"                                                             // ring
	    + "\u00C7\u00E7"                                                             // cedilla
	    ;

	private static Map<String, String> htmlAccents;
	
	static {
		htmlAccents = new HashMap<String, String>();
		htmlAccents.put("&aacute;", "á");	htmlAccents.put("&Aacute;", "Á");
		htmlAccents.put("&agrave;", "à");	htmlAccents.put("&Agrave;", "À");
		htmlAccents.put("&acirc;", "â");	htmlAccents.put("&Acirc;", "Â");
		htmlAccents.put("&atilde;", "ã");	htmlAccents.put("&Atilde;", "Ã");
		htmlAccents.put("&auml;", "ä");		htmlAccents.put("&Auml;", "Ä");
		htmlAccents.put("&aring;", "å");	htmlAccents.put("&Aring;", "Å");
		htmlAccents.put("&aelig;","æ");		htmlAccents.put("&Aelig;","Æ");
		
		htmlAccents.put("&eacute;", "é");	htmlAccents.put("&Eacute;", "É");
		htmlAccents.put("&egrave;", "è");	htmlAccents.put("&Egrave;", "È");
		htmlAccents.put("&ecirc;", "ê");	htmlAccents.put("&Ecirc;", "Ê");
		htmlAccents.put("&euml;", "ë");		htmlAccents.put("&Euml;", "Ë");
		
		htmlAccents.put("&iacute;", "í");	htmlAccents.put("&Iacute;", "Í");
		htmlAccents.put("&igrave;", "ì");	htmlAccents.put("&Igrave;", "Ì");
		htmlAccents.put("&icirc;", "î");	htmlAccents.put("&Icirc;", "Î");
		htmlAccents.put("&iuml;", "ï");		htmlAccents.put("&Iuml;", "Ï");
		
		htmlAccents.put("&oacute;", "ó");	htmlAccents.put("&Oacute;", "Ó");
		htmlAccents.put("&ograve;", "ò");	htmlAccents.put("&Ograve;", "Ò");
		htmlAccents.put("&ocirc;", "ô");	htmlAccents.put("&Ocirc;", "Ô");
		htmlAccents.put("&otilde;", "õ");	htmlAccents.put("&Otilde;", "Õ");
		htmlAccents.put("&ouml;", "ö");		htmlAccents.put("&Ouml;", "Ö");
		htmlAccents.put("&oslash;","ø"); 	htmlAccents.put("&Oslash;","Ø");
		
		htmlAccents.put("&uacute;", "ú");	htmlAccents.put("&Uacute;", "Ú");
		htmlAccents.put("&ugrave;", "ù");	htmlAccents.put("&Ugrave;", "Ù");
		htmlAccents.put("&ucirc;", "û");	htmlAccents.put("&Ucirc;", "Û");
		htmlAccents.put("&uuml;", "ü");		htmlAccents.put("&Uuml;", "Ü");
				
		htmlAccents.put("&ccedil;","ç"); 	htmlAccents.put("&Ccedil;","Ç");
	    htmlAccents.put("&szlig;","ß"); 	htmlAccents.put("&nbsp;"," ");
	    
	    htmlAccents.put("&ordm;","º"); 		htmlAccents.put("&deg;","°");
	    htmlAccents.put("&ordf;","ª"); 		htmlAccents.put("&ndash;","-");
	}

	/** Transforma HTML Entities (http://en.wikipedia.org/wiki/HTML_entities) em caracteres Unicode. */
	public static final String unescapeHTML(String source) {
		return StringEscapeUtils.unescapeHtml(source);
	}

	/** Troca todos os ' por '' */
	public static String escapeBackSlash(String oldString) {
		return oldString.replaceAll("'", "''");
	}

	/** Troca todos os ' por \' e transforma a string para uppercase */
	public static String escapeBackSlashUpper(String oldString) {
		return escapeBackSlash(oldString.toUpperCase());
	}

	/** Converte a string para ascii */
	public static String toAscii(String oldString) {
		if ( oldString == null ) {
			return oldString;
		}
		StringBuffer sb = new StringBuffer();
		int n = oldString.length();
		for (int i = 0; i < n; i++) {
			char c = oldString.charAt(i);
			int pos = UNICODE.indexOf(c);
			if (pos > -1) {
				sb.append(PLAIN_ASCII.charAt(pos));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Converte as duas strings para ASCII e compara se o conteúdo é igual ignorando case sensitive. Retorna TRUE se as string forem iguais.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean compareInAscii(String a, String b) {
		
		if (a == null || b == null)
			return false;
		
		return toAscii(a.trim()).equalsIgnoreCase(toAscii(b.trim()));
		
	}

	
	/**
	 * Troca os códigos de caracteres acentuados do html (como &amp;aacute;) pela letra correspondente
	 * 
	 * @param oldString
	 * @return
	 */
	public static String toHtml(String oldString) {
		for (Entry<String, String> entry : htmlAccents.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			oldString = oldString.replaceAll(key, value);
		}
		return oldString;
	}
	
	/**
	 * Remove espaços em brancos repetidos no texto.
	 * 
	 * @param texto
	 * @return
	 */
	public static String removeEspacosRepetidos(String texto) {
		
		if (texto == null)
			return "";
		
		return texto.replaceAll("( )+", " ").trim();
	}
	
	/** 
	 * Converte a string para ascii, trocando códigos de caracteres 
	 * acentuados do html (como &amp;aacute;) pela letra correspondente;
	 */
	public static String toAsciiHtml(String oldString) {
		for (Entry<String, String> entry : htmlAccents.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			oldString = oldString.replaceAll(key, value);
		}
		
		return toAscii(oldString);
	}
	
	/**
	 * Troca os códigos de caracteres acentuados do html (como &amp;aacute;) pela letra correspondente
	 * 
	 * @param oldString
	 * @return
	 */
	public static String toText(String oldString) {
		for (Entry<String, String> entry : htmlAccents.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			oldString = oldString.replaceAll(value,key);
		}
		return oldString;
	}
	/** Transforma a string para ascii e converte para uppercase */
	public static String toAsciiAndUpperCase(String oldString) {
		String toAscii = toAscii(oldString);
		if(toAscii == null) {
			return toAscii;
		} else {
			return toAscii.toUpperCase();
		}
	}

	/**
	 * Retorna <tt>true</tt> se a String for diferente de null e não for vazia.
	 */
	public final static boolean notEmpty(String s) {
		return !isEmpty(s);
	}

	/**
	 * Retorna <tt>true</tt> se a String for igual a null ou vazia
	 */
	public final static boolean isEmpty(String s) {
		return (s == null || s.trim().length() == 0);
	}

	/**
	 * Retorna o total de caracteres diferentes de uma String.
	 */
	public final static int getDensidade(String str) {

		str = str.toLowerCase();

		TreeSet<Character> caracteres = new TreeSet<Character>();
		for ( int a = 0; a < str.length(); a++) {
			caracteres.add(str.charAt(a));
		}

		return caracteres.size();

	}

	/**
	 * Extrai um long da string, pegando todos os números e desconsiderando os caracteres da mesma
	 */
	public final static Long extractLong(String s) {
		if (s == null || s.trim().equals("")) {
			return null;
		}
		if (org.apache.commons.lang.StringUtils.isNumeric(s)) {
			return Long.parseLong(s);
		}
		StringBuffer integer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (org.apache.commons.lang.StringUtils.isNumeric(s.charAt(i)+"")) {
				integer.append(s.charAt(i));
			}
		}
		if (integer.length() > 0 && integer.length() <= String.valueOf(Long.MAX_VALUE).length()) {
			return new Long(integer.toString());
		} else {
			return null;
		}
	}

	/**
	 * Extrai um integer da string, pegando todos os números e desconsiderando os caracteres da mesma
	 */
	public final static Integer extractInteger(String s) {
		if (s == null || s.trim().equals("")) {
			return null;
		}
		if (org.apache.commons.lang.StringUtils.isNumeric(s)) {
			return Integer.parseInt(s);
		}
		StringBuffer integer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (org.apache.commons.lang.StringUtils.isNumeric(s.charAt(i)+"")) {
				integer.append(s.charAt(i));
			}
		}
		if (integer.length() > 0) {
			return new Integer(integer.toString().trim());
		} else {
			return null;
		}
	}
	
	public final static String extractAlgarismos(String s) {
		if (s == null || s.trim().equals("")) {
			return null;
		}
		if (org.apache.commons.lang.StringUtils.isNumeric(s)) {
			return s;
		}
		StringBuffer integer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (org.apache.commons.lang.StringUtils.isNumeric(s.charAt(i)+"")) {
				integer.append(s.charAt(i));
			}
		}
		return integer.toString();
		
	}

	/**
	 * Função para gerar uma sequência de caracteres aleatórios de acordo com o tamanho informado
	 */
	public final static String generatePassword(int tamanho) {
		StringBuilder password = new StringBuilder( tamanho > 0 ? tamanho : 0 );

		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		int total = chars.length();

		for(int i = 0; i < tamanho; i++) {
		    password.append( chars.charAt( ((Double) Math.floor((Math.random() * total))).intValue()) );
		}
		return password.toString();
	}

	/**
	 * Transforma a String para o encoding Latin9
	 */
	public final static String toLatin9(String s) {
		try {
			if ( s == null ) {
				return null;
			}
			return  new String(s.getBytes(), "ISO8859_15_FDIS");
		} catch (UnsupportedEncodingException e) {
			System.err.println(" ------  ERRO NA CONVERSÃO PARA LATIN9 ------");
			return toAscii(s);
		}
	}

	/**
	 * Remove da string os caracteres que são inválidos no encoding UTF-8;
	 */
	public static String removeInvalidUtf8(String s) {
		if (s != null) {
			String utf8 = s;
			while(utf8.indexOf(0x00) != -1) {
				utf8 = utf8.replace((char) 0x00, ' ');
			}
			return utf8;
		}
		return s;
	}

	/**
	 * Converte caracteres de escape e caracteres especiais do html
	 * em HTML entities.
	 */
	public static String converteJavaToHtml(String texto) {

		StringBuilder textoFmt = new StringBuilder();
		
		if (texto != null) {
			for (int i = 0; i < texto.length(); i++) {
				switch (texto.charAt(i)) {
				case '\n':
					textoFmt.append("<br />");
					break;
				case '\r':
					break;
				case '\"':
					textoFmt.append("&quot;");
					break;
				case '<':
					textoFmt.append("&lt;");
					break;
				case '>':
					textoFmt.append("&gt;");
					break;
				default:
					textoFmt.append(texto.charAt(i));
					break;
				}
			}
		}
		
		return textoFmt.toString();
	}

	/**
	 * Separa uma String com tamanho grande em várias linhas.
	 */
	public static String divideEmLinhas(String oldString, int maximo) {
		StringBuilder sb = new StringBuilder(oldString);
		StringBuilder newString = new StringBuilder();

		while (sb.length() > 0) {
			int indice = sb.length();

			if (maximo < sb.length())  {
				indice = buscaUltimoEspacoEmBranco(sb.substring(0, maximo));
				if (indice == -1) {
					indice = maximo;
				}
			}

			newString.append(sb.substring(0, indice) + System.getProperty("line.separator"));
			sb.replace(0, indice, "");
		}

		return newString.toString();
	}

	/**
	 * Busca a posição do último espaço em branco de uma String
	 */
	public static int buscaUltimoEspacoEmBranco(String str) {
		for (int i = str.length() - 1; i >= 0; i--) {
			if (str.charAt(i) == ' ') {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Retorna o número de palavras de uma String.
	 */
	public static int wordCount(String s) {
		if (s != null) {
			return Pattern.compile("[\\W]+").split(s.trim()).length;
		}
		return 0;
	}

	/**
	 * Limita a quantidade de caracteres de um texto sem cortar a palavra:
	 *
	 * @author Mário Rizzi
	 * @param 	str - texto a ser limitado
	 * 			tam - quantidade de caracteres
	 */
	public static String limitTxt(String str,Integer tam){
		String palavras[] = str.split(" ");
		str = "";
		Integer comp=0;

		for(int i=0;i<palavras.length;i++){
		   comp += palavras[i].length();
		   if(i< palavras.length){
			  comp ++;
		      if(comp<=tam) {
				str +=  palavras[i]+" ";
		      } else{
		    	if(isEmpty(str)) str=palavras[i].substring(0,tam);
		    	str +="...";
		      	break;
		      }
		   }
		}

		return str.trim();
	}

	/** 
	 * Transforma uma String em camel case em formato legível por humanos, separando palavras
	 * por espaços.
	 */
	public static String humanFormat(String oldString) {
		if (oldString == null) {
			return null;
		}
		if (oldString.length() == 0) {
			return "";
		}

		char[] toCharArray = oldString.substring(1).toCharArray();
		StringBuilder builder = new StringBuilder();
		builder.append(String.valueOf(oldString.charAt(0)).toUpperCase());
		for (char c : toCharArray) {
			if(Character.isUpperCase(c)){
				builder.append(" ");
			}
			builder.append(c);
		}
		return builder.toString();
	}

	/**
	 * Remove o caracter carriage return (\\r) da string.
	 */
	public static String removeCarriageReturn(String s) {
		if (s == null) {
			return null;
		}
		return s.trim().replaceAll("[\r\n]+", "\n");
	}

	/** Retira tags html de uma String */
	public static String stripHtmlTags(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("\\<.*?>","");
	}
	
	/** Retira tags html de uma String */
	public static String stripHtmlTagsAndHtmlName(String html) {
		if (html == null) {
			return null;
		}
		html = html.replaceAll("\\<.*?>","");
		return toHtml(html);
	}
	
	/** Retorna true se existirem letras na String */
	public static boolean hasLetter(String str) {
		return hasLetter(str, 1);
	}

	/** Retorna true se existirem números na String */
	public static boolean hasNumber(String str) {
		return hasNumber(str, 1);
	}

	/** Retorna true se existirem letras em uma quantidade mínima na String */
	public static boolean hasLetter(String str, int minQty) {
		int qtd = 0;
		for (int i = 0; i < str.length(); i++) {
			qtd += (Character.isLetter(str.charAt(i)) ? 1 : 0);
		}
		return qtd >= minQty;
	}

	/** Retorna true se existirem números em uma quantidade mínima na String */
	public static boolean hasNumber(String str, int minQty) {
		int qtd = 0;
		for (int i = 0; i < str.length(); i++) {
			qtd += (Character.isDigit(str.charAt(i)) ? 1 : 0);
		}
		return qtd >= minQty;
	}


	/**
	 * Cabeçalho para e-mail HTML.
	 */
	public static String htmlMailHead() {
		return "<html>" +
				"<head>" +
				"<style> body { font-family: Verdana; font-size: 10pt } table { font-size: 10pt } </style> " +
				"</head> " +
				"<body>";
	}

	/**
	 * Rodapé para e-mail HTML.
	 */
	public static String htmlMailTail() {
		return "</font> <br> <i> Esta mensagem é automática e não deve ser respondida </i>" +
				"</body></html>";
	}


	/** Se o conteúdo da string for qualquer uma dessas strings será considerado como lero lero imediatamente. */
	private static List<String> leroLeroPatterns = new ArrayList<String>();
	static{
		leroLeroPatterns.add("será informado oportunamente");
		leroLeroPatterns.add("a definir");
	}

	private static List<Character> vogais = new ArrayList<Character>();
	static{
		vogais.add('a');
		vogais.add('á');
		vogais.add('e');
		vogais.add('é');
		vogais.add('i');
		vogais.add('o');
		vogais.add('ó');
		vogais.add('u');
		vogais.add('y');

		vogais.add('A');
		vogais.add('Á');
		vogais.add('E');
		vogais.add('É');
		vogais.add('I');
		vogais.add('O');
		vogais.add('Ó');
		vogais.add('U');
		vogais.add('Y');

		vogais.add('ã');
		vogais.add('Ã');
	}

	/**
	 * Este método identifica se a string passada contém Lero-Lero!
	 * 
	 * @param text
	 * @param tamanhoMinimo tamanho mínimo da string
	 * @param minimoCaracteres  mínimo de caracteres diferentes que a string deve conter
	 * @param minimoPalavras  mínimo de palavras diferentes que a string deve conter
	 * @return retorna uma String descrevendo a inconsistência encontrada e,  caso não contenha lero-lero retorna nulo. 
	 * CASO os parâmetros não sejam informados estes são os valores DEFAULT:
	 * 		tamanhoMinimo = 50;
	 * 		minimoCaracteres = 10;
	 * 		minimoPalavras = 10;
	 */
	public static String containsLeroLero(String text, Integer tamanhoMinimo, Integer minimoCaracteres, Integer minimoPalavras){

		text = text.toLowerCase();

		/** VALORES DEFAULT */
		if( tamanhoMinimo == null ) {
			tamanhoMinimo = 50;
		}
		if( minimoCaracteres == null ) {
			minimoCaracteres = 10;
		}
		if( minimoPalavras == null ) {
			minimoPalavras = 10;
		}

		for( String s : leroLeroPatterns ){
			if( toAscii( s.trim() ).equalsIgnoreCase( toAscii( text.trim() ) ) ) {
				return "O conteúdo não pode ser '" + text.trim() + "'";
			}
		}

		//wordCount
		if( wordCount(text) < minimoPalavras){
			return "O conteúdo deve possuir no mínimo " + minimoPalavras + " palavras.";
		}
		
		HashSet<Character> caracteres = new HashSet<Character>();
		for( char c : text.toCharArray() ){
			caracteres.add(c);
		}

		/**
		 * testando tamanho minimo da string
		 */
		if( text.length() < tamanhoMinimo ){
			return "O conteúdo deve possuir no mínimo " + tamanhoMinimo + " caracteres.";
		}

		
		/** tem q ter pelo menos o :totalCaracteres informado*/
		if( caracteres.size() < minimoCaracteres ){
			return "O conteúdo deve possuir no mínimo " + minimoCaracteres + " caracteres diferentes.";
		}

		/**
		 * verificando se contem 4 vogais ou 4 consoantes consecutivos..
		 */
		int contVogais = 0;
		int contConsoantes = 0;
		for( char c : text.toCharArray() ){
			if( !Character.isLetter(c) ){
				contConsoantes = 0;
				contVogais = 0;
				continue;
			}

			if( vogais.contains( toAscii( Character.toString(c) ).charAt(0) ) ){
				contVogais++;
				contConsoantes = 0;
			}
			else{
				contConsoantes++;
				contVogais = 0;
			}

			if( contVogais > 5 ){
				//System.out.println( "contVogais:" + contVogais + "\n contConsoantes" + contConsoantes );
				//return true;
				return "O conteúdo não pode ter 5 vogais consecutivas.";
			}
			
			if( contConsoantes > 5 )
				return "O conteúdo não pode ter 5 consoantes consecutivas.";
		}

		return null;
	}

	/**
	 * Transforma uma lista de objetos em uma String com as descrições
	 * dos objetos separadas por vírgulas e o último objeto separado por "e".
	 * Exemplo: [1, 2, 3, 4] -> 1, 2, 3 e 4
	 *
	 * @param itens
	 * @return
	 */
	public static String transformaEmLista(List<?> itens) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < itens.size(); i++) {
			Object item = itens.get(i);

			if ((i > 0) && (i < itens.size() - 1)) {
				sb.append(", ");
			}
			if ((i > 0) && (i == itens.size() - 1)) {
				sb.append(" e ");
			}

			sb.append(item.toString());
		}

		return sb.toString();
	}
	
	/**
	 * Remove os símbolos de uma string com CPF ou CNPJ.
	 */
	public static String removeSimbolosCPFCNPJ(String cpfcnpj) {
		if (cpfcnpj == null) {
			return null;
		}
		return cpfcnpj.trim().replaceAll("[./-]*","");
	}

	/**
	 * Remove os caracteres carriage return (\\r) e line break (\\n)
	 * de uma String.
	 */
	public static String removeLineBreak(String s) {
		if (s == null) {
			return null;
		}
		return s.trim().replaceAll("[\r\n|\n|\r|\n\r]+", " ");
	}

	/**
	 * Não deixa aparecer ponto e vírgula na String para uso e CSV;
	 */
	public static String removePV(String src) {
		if ( src != null ) {
			return src.replace(";", " ");
		} else {
			return src;
		}
	}

	/**
	 * Remove as quebras de linhas e pontos e vírgulas
	 */
	public static String removeLineBreakAndPV(String s) {
		if (s == null) {
			return null;
		}
		return removePV(s.trim().replaceAll("[\r\n|\n|\r|\n\r]+", " "));
	}

	/**
	 * Método responsável pela remoção dos comentários introduzidos no TyneMCE.
	 */
	public static String removerComentarios(String texto){
		if(texto != null) {
			//Enquanto haver inícios de comentários
			texto = removerComentario(texto, "<!--" , "-->" , 3);
			texto = removerComentario(texto, "&lt;!--" , "--&gt;" , 6);

			//remoção dos comentários
			while(texto.contains("!--")){
				int inicioComentarioWord = texto.indexOf("!--");
				int fimComentarioWord = texto.indexOf(">", inicioComentarioWord);
				if(fimComentarioWord != -1) {
					if(inicioComentarioWord != -1 && inicioComentarioWord == 0)
						texto = texto.substring(fimComentarioWord+1);
					else if(fimComentarioWord <= texto.length() && inicioComentarioWord != 0)
						texto = texto.substring(0, inicioComentarioWord) + texto.substring(fimComentarioWord + 1);
				} else
					break;
			}
		}
		return texto;
	}

	/**
	 * Remove comentários de um texto de acordo com o padrão de abertura e fechamento do comentário.
	 */
	private static String removerComentario(String texto, String tipoComAbertura, String tipoComFechamento, int index) {
		while(texto.contains(tipoComAbertura)){
			//Pegar a primeira ocorrência do comentário
			int aberturaComentario = texto.indexOf(tipoComAbertura);

			//Pegar a segunda ocorrência do comentário
			int segundaAbertura = -1;
			if(aberturaComentario != -1){
				segundaAbertura = texto.indexOf(tipoComAbertura, aberturaComentario + 1);
			}
			//Caso Só exista um comentário
			if(segundaAbertura == -1){
				int finalComentario = texto.indexOf(tipoComFechamento);
				//Se existir um final para o comentário
				if(finalComentario != -1 && aberturaComentario > 1){
					//Não tem texto depois
					if(finalComentario+ index >= texto.length())
						texto = texto.substring(0, aberturaComentario);
					else
						texto = texto.substring(0, aberturaComentario) + texto.substring(finalComentario + index);
				}else if (finalComentario != -1 && aberturaComentario <= 1){
					texto = texto.substring(finalComentario + index);
				}else//Se não existir
					break;
			}else{//Se Existirem mais de uma abertura
				int finalComentario = texto.indexOf(tipoComFechamento);
				//Se existir um final para o comentário
				if(finalComentario != -1 && finalComentario > aberturaComentario){
					if(aberturaComentario > 1){
						texto = texto.substring(0, aberturaComentario) + texto.substring(finalComentario+ index);
					}else{
						texto = texto.substring(finalComentario + index);
					}
				}else//Se não existir
					break;
			}
		}
		return texto;
	}

	/**
	 * Retorna os números do começo de uma String passada como argumento, caso haja.
	 */
	public static Long getNumerosIniciais(String conteudo){
		Long numRetorno = 0l;
		
		if (conteudo != null){
			String retorno = conteudo.replaceAll("\\D+\\w*", "");
			
			if (!retorno.equals("")) {
				try {
					numRetorno = Long.parseLong(retorno);
				} catch (NumberFormatException e) {
					numRetorno = 0l;
				}
			}
		}
		return (numRetorno.equals(new Long(0))?null:numRetorno);
	}

	public static String[] dividirPorTamanho(String str, int tamanho) {
		List<String> partes = new ArrayList<String>();
		
		while(!isEmpty(str)) {
			partes.add(str.substring(0, tamanho));
			str = str.substring(tamanho);
		}
		
		return partes.toArray(new String[partes.size()]);
	}

	public static String primeriaMaiuscula (String s) {
		if(isEmpty(s)) return s;
		else 
			return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
	public static String toCamelCase(String s) {
		StringBuilder sb = new StringBuilder();
		boolean primeira = true;
		for(String parte : s.split(" |_")) {
			if (primeira) {
				sb.append(parte.toLowerCase());
				primeira = false;
			} else {
				sb.append( Character.toUpperCase(parte.charAt(0)));
				sb.append(parte.substring(1).toLowerCase());
			}
		}
		return sb.toString();
		
	}

	/**
	 * Verifica se todos os caracteres de uma string são iguais. retorna falso se a string for vazia.
	 * @param s
	 * @return
	 */
	public static boolean todosCaracteresIguais (String s){
		if (!isEmpty(s)){
			char primeiroCaractere = s.charAt(0);
			for (int i = 1; i < s.length(); i++) {
				char caractereAtual = s.charAt(i);
				if (caractereAtual != primeiroCaractere){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Identifica se uma string é um número de ponto flutuante válido.
	 * @param s
	 * @return
	 */
	public static boolean isDouble(String s) {
		try{
		   Double.valueOf(s);
		   return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
