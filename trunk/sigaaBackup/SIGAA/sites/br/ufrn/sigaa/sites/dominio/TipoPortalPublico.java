/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/04/2009
 *
 */
package br.ufrn.sigaa.sites.dominio;

/**
 * A classe enum que representa os tipos de portais públicos do SIGAA
 *  
 * @author sist-sigaa-12
 *
 */
public enum TipoPortalPublico{
	UNIDADE, CURSO, DESCONHECIDO;
	
	public static TipoPortalPublico getTipo(String url) {
		if ( url.contains("programa") || url.contains("departamento") || url.contains("centro") )
			return UNIDADE;
		else if ( url.contains("curso") )
			return CURSO;
		else
			return DESCONHECIDO;
	}
	
}
