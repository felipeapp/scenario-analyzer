/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/11/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * <p> MBean que gerencia o caso de uso que vai permitir ao administrador geral do sistema de biblioteca alterar o valor de alguns 
 * parâmetro utilizados no sistema. Esse MBean específico configura os parâmetros gerais do sistema. </p>
 * <p> Os parâmetros alterados aqui são válidos para todas as bibliotecas do sistema </p>
 * 
 *
 * <p> <i> <strong> Observação: </strong> Alguns parâmetros são implementações internas no sistema como o id da biblioteca central, esse não faz 
 * sentido que o usuário possa alterar </i> </p>
 * 
 * @author jadson
 * @see ConfiguraParametrosProcessosTecnicosMBean
 * @see ConfiguraParametrosCirculacaoMBean
 * @see ConfiguraParametrosInformacaoReferenciaMBean
 *
 */
@Component("configuraParametrosGeraisBibliotecaMBean")
@Scope("request")
public class ConfiguraParametrosGeraisBibliotecaMBean  extends AbstractConfiguraParametrosBiblioteca{


	/**
	 *
	 * OBSERVAÇÃO:  Caso adicione um novo parêmetro no sistema e queira que o usuário possa alterá-lo, adicione-o a essa lista. 
	 * <br/>
	 * <br/> 
	 * IMPORTANTE, MUITA ATENÇÃO: Caso altere essa ordem, lembrem-se de alterar a ordem na página, pois cada  posição na página implica em um tipo de campo e máscara diferente. 
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraParametros()
	 */
	@Override
	protected void configuraParametros() {
		
		// Adicione nessa lista o parâmetro e a posição que ele deve parecer na tela para o usuário
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.CODIGO_IDENTIFICACAO_LIBRARY_OF_CONGRESS, 0);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.DESCRICAO_SUB_SISTEMA_BIBLIOTECA, 1);
		
		// Ajustando parâmetros não obrigatórios
		codigosParametrosAlteraveisOpcionais.add(ParametrosBiblioteca.CODIGO_IDENTIFICACAO_LIBRARY_OF_CONGRESS);
		
		// A classe pai vai validar o tamanho do texto digitado pelo usuário
		codigosParametrosValidarTamanho.put(ParametrosBiblioteca.DESCRICAO_SUB_SISTEMA_BIBLIOTECA, 35); 
		
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraPaginaListaParametros(java.lang.String)
	 */
	@Override
	protected void configuraPaginaListaParametros() {
		this.paginaListaParametrosBiblioteca = "/biblioteca/listaParametrosGeraisSistemaBiblioteca.jsp";
	}

	
}
