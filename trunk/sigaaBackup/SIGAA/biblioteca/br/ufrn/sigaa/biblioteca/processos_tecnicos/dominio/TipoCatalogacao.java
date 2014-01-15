/* Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 01/03/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.CatalogacaoMBean;

/**
 * <p>Cont�m os tipos de dados MARC suportados pelo sistema.</p>
 *
 * <p> <i> V�rias entidades e casos de uso do sistema utilizam esses valores para saber se estamos 
 * trabalhando com T�tulos ou Autoridades.</i> </p>
 * 
 * @author jadson
 * @see Etiqueta
 * @see PlanilhaCatalogacao
 * @see CatalogacaoMBean
 */
public final class TipoCatalogacao {

	/** Valor que identifica v�rios objeto usado na parte da cataloga��o bibliotegr�fica:  Etiquetas, Planilhas, Tipo de Cataloga��o, etc... */
	public static final short BIBLIOGRAFICA = 1;
	
	/** Valor que identifica v�rios objeto usado na parte da cataloga��o de autoridades:  Etiquetas, Planilhas,  Tipo de Cataloga��o, etc... */
	public static final short AUTORIDADE = 2;
}
