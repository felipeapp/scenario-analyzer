/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * par�metro utilizados no sistema. Esse MBean espec�fico configura os par�metros gerais do sistema. </p>
 * <p> Os par�metros alterados aqui s�o v�lidos para todas as bibliotecas do sistema </p>
 * 
 *
 * <p> <i> <strong> Observa��o: </strong> Alguns par�metros s�o implementa��es internas no sistema como o id da biblioteca central, esse n�o faz 
 * sentido que o usu�rio possa alterar </i> </p>
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
	 * OBSERVA��O:  Caso adicione um novo par�metro no sistema e queira que o usu�rio possa alter�-lo, adicione-o a essa lista. 
	 * <br/>
	 * <br/> 
	 * IMPORTANTE, MUITA ATEN��O: Caso altere essa ordem, lembrem-se de alterar a ordem na p�gina, pois cada  posi��o na p�gina implica em um tipo de campo e m�scara diferente. 
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraParametros()
	 */
	@Override
	protected void configuraParametros() {
		
		// Adicione nessa lista o par�metro e a posi��o que ele deve parecer na tela para o usu�rio
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.CODIGO_IDENTIFICACAO_LIBRARY_OF_CONGRESS, 0);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.DESCRICAO_SUB_SISTEMA_BIBLIOTECA, 1);
		
		// Ajustando par�metros n�o obrigat�rios
		codigosParametrosAlteraveisOpcionais.add(ParametrosBiblioteca.CODIGO_IDENTIFICACAO_LIBRARY_OF_CONGRESS);
		
		// A classe pai vai validar o tamanho do texto digitado pelo usu�rio
		codigosParametrosValidarTamanho.put(ParametrosBiblioteca.DESCRICAO_SUB_SISTEMA_BIBLIOTECA, 35); 
		
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraPaginaListaParametros(java.lang.String)
	 */
	@Override
	protected void configuraPaginaListaParametros() {
		this.paginaListaParametrosBiblioteca = "/biblioteca/listaParametrosGeraisSistemaBiblioteca.jsp";
	}

	
}
