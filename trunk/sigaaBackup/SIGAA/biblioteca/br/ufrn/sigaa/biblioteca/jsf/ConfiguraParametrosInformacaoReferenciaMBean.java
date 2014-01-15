/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
* par�metro utilizados na parte de informa��o e refer�ncia, como por exemplo, o texto impresso na ficha catalogr�fica gerada pelo sistema. </p>
* <p> Os par�metros alterados aqui s�o v�lidos para todas as bibliotecas do sistema. </p>
* 
* @author jadson
*
*/
@Component("configuraParametrosInformacaoReferenciaMBean")
@Scope("request")
public class ConfiguraParametrosInformacaoReferenciaMBean extends AbstractConfiguraParametrosBiblioteca{

	/**
	 * OBSERVA��O:  Caso adicione um novo par�metro no sistema e queira que o usu�rio possa alter�-lo, adicione-o a essa lista.
	 * 
	 * <br/>
	 * <br/> 
	 * IMPORTANTE, MUITA ATEN��O: Caso altere essa ordem, lembrem-se de alterar a ordem na p�gina, pois cada  posi��o na p�gina implica em um tipo de campo e m�scara diferente.
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraParametros()
	 */
	@Override
	protected void configuraParametros() {
		// Adicione nessa lista o par�metro e a posi��o que ele deve parecer na tela para o usu�rio
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.TEXTO_TITULO_FICHA_CATALOGRAFICA, 0);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.UNIDADE_FEDERAL_IMPRESSO_FICHA_CATALOGRAFICA, 1);
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraPaginaListaParametros()
	 */
	@Override
	protected void configuraPaginaListaParametros() {
		this.paginaListaParametrosBiblioteca =  "/biblioteca/listaParametrosInformacaoReferenciaSistemaBiblioteca.jsp";
	}
	
	
}
