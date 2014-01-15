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
* par�metro utilizados na parte de processos t�cnicos, como por exemplo, os c�digos utilizado na coopera��o com a FGV. </p>
* <p> Os par�metros alterados aqui s�o v�lidos para todas as bibliotecas do sistema. </p>
* 
* @author jadson
*
*/
@Component("configuraParametrosProcessosTecnicosMBean")
@Scope("request")
public class ConfiguraParametrosProcessosTecnicosMBean extends AbstractConfiguraParametrosBiblioteca{

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
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.CODIGO_DA_BIBLIOTECA_CATALOGO_COLETIVO, 0);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.VERSAO_DO_PROGRAMA_REGISTRO_ALTERADO, 1);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.CODIGO_INSTITUICAO_CATALOGACAO, 2);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.IDIOMA_CATALOGACAO, 3);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.SIGLAS_IDENTIFICAO_TRABALHOS_ACADEMICOS, 4);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.CODIGO_NUMERO_CONTROLE_BIBLIOGRAFICO, 5);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.CODIGO_NUMERO_CONTROLE_AUTORIDADES, 6);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.DIAS_RETARDO_MATERIAL_DISPONIVEL_ACERVO, 7);
		
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraPaginaListaParametros()
	 */
	
	@Override
	protected void configuraPaginaListaParametros() {
		this.paginaListaParametrosBiblioteca ="/biblioteca/listaParametrosProcessosTecnicosSistemaBiblioteca.jsp";
		
	}
	
	
}
