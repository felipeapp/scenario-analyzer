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
* parâmetro utilizados na parte de processos técnicos, como por exemplo, os códigos utilizado na cooperação com a FGV. </p>
* <p> Os parâmetros alterados aqui são válidos para todas as bibliotecas do sistema. </p>
* 
* @author jadson
*
*/
@Component("configuraParametrosProcessosTecnicosMBean")
@Scope("request")
public class ConfiguraParametrosProcessosTecnicosMBean extends AbstractConfiguraParametrosBiblioteca{

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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraPaginaListaParametros()
	 */
	
	@Override
	protected void configuraPaginaListaParametros() {
		this.paginaListaParametrosBiblioteca ="/biblioteca/listaParametrosProcessosTecnicosSistemaBiblioteca.jsp";
		
	}
	
	
}
