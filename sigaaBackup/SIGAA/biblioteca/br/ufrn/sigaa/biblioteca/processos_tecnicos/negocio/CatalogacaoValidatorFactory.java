/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;


/**
 *
 * <p>Retorna as regras de valida��o utilizadas na cataloga��o de T�tulos e Autoridades. </p>
 *
 * <p> <i> Pode ser implementado de acordo com as regras utilizadas em cada institui��o. </i> </p>
 * 
 * @author jadson
 *
 */
public class CatalogacaoValidatorFactory {
	
		public static CatalogacaoValidator getRegrasValidacao(){
			return new CatalogacaoValidatorImpl();  // Alterar para alguma classe que implemente regras espec�ficas n�o utilizadas na UFRN.
		}
}
