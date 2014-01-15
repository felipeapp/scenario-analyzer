/* Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 01/03/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.CatalogacaoMBean;

/**
 * <p>Contém os tipos de dados MARC suportados pelo sistema.</p>
 *
 * <p> <i> Várias entidades e casos de uso do sistema utilizam esses valores para saber se estamos 
 * trabalhando com Títulos ou Autoridades.</i> </p>
 * 
 * @author jadson
 * @see Etiqueta
 * @see PlanilhaCatalogacao
 * @see CatalogacaoMBean
 */
public final class TipoCatalogacao {

	/** Valor que identifica vários objeto usado na parte da catalogação bibliotegráfica:  Etiquetas, Planilhas, Tipo de Catalogação, etc... */
	public static final short BIBLIOGRAFICA = 1;
	
	/** Valor que identifica vários objeto usado na parte da catalogação de autoridades:  Etiquetas, Planilhas,  Tipo de Catalogação, etc... */
	public static final short AUTORIDADE = 2;
}
