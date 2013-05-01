package br.com.ecommerce.arq.util;

/**
 * Classe utilit�ria para se mecher com Strings
 * @author Rodrigo Dutra de Oliveira
 *
 */
public class StringUtils {

	public StringUtils(){
		
	}
	
	/**
	 * Valida endere�os de email
	 * @param email
	 * 
	 * @return
	 */
	public static boolean validaEmail(String email) {
		return (email != null && email.length() > 0
				&& email.trim().equals(email) && email.contains(".")
				&& email.contains("@") && !email.contains(" "));
	}
	
	/**
	 * Remove as tags html
	 * @param html
	 * @return
	 */
	public static String stripHtmlTags(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("\\<.*?>","");
	}
	
	/**
	 * Transforma toda ocorr�ncia de uma aspas simples em aspas duplicadas.
	 * Utilizado para constru��o de queries SQL.
	 *
	 * @param original
	 * @return
	 */
	public static String trataAspasSimples(String original) {
		return original.replaceAll("'", "''");
	}
}
