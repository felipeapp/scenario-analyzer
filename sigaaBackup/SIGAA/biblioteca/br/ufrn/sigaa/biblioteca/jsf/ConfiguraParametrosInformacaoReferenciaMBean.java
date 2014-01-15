/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 02/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
*
* <p> MBean que gerencia o caso de uso que vai permitir ao administrador geral do sistema de biblioteca alterar o valor de alguns 
* parâmetro utilizados na parte de informação e referência, como por exemplo, o texto impresso na ficha catalográfica gerada pelo sistema. </p>
* <p> Os parâmetros alterados aqui são válidos para todas as bibliotecas do sistema. </p>
* 
* @author jadson
*
*/
@Component("configuraParametrosInformacaoReferenciaMBean")
@Scope("request")
public class ConfiguraParametrosInformacaoReferenciaMBean extends AbstractConfiguraParametrosBiblioteca{

	/**
	 * OBSERVAÇÃO:  Caso adicione um novo parêmetro no sistema e queira que o usuário possa alterá-lo, adicione-o a essa lista.
	 * 
	 * <br/>
	 * <br/> 
	 * IMPORTANTE, MUITA ATENÇÃO: Caso altere essa ordem, lembrem-se de alterar a ordem na página, pois cada  posição na página implica em um tipo de campo e máscara diferente.
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraParametros()
	 */
	@Override
	protected void configuraParametros() {
		// Adicione nessa lista o parâmetro e a posição que ele deve parecer na tela para o usuário
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.TEXTO_TITULO_FICHA_CATALOGRAFICA, 0);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.UNIDADE_FEDERAL_IMPRESSO_FICHA_CATALOGRAFICA, 1);
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraPaginaListaParametros()
	 */
	@Override
	protected void configuraPaginaListaParametros() {
		this.paginaListaParametrosBiblioteca =  "/biblioteca/listaParametrosInformacaoReferenciaSistemaBiblioteca.jsp";
	}
	
	
}
