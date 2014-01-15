/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 02/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;


/**
 *
 * <p>Retorna as regras de validação utilizadas na catalogação de Títulos e Autoridades. </p>
 *
 * <p> <i> Pode ser implementado de acordo com as regras utilizadas em cada instituição. </i> </p>
 * 
 * @author jadson
 *
 */
public class CatalogacaoValidatorFactory {
	
		public static CatalogacaoValidator getRegrasValidacao(){
			return new CatalogacaoValidatorImpl();  // Alterar para alguma classe que implemente regras específicas não utilizadas na UFRN.
		}
}
