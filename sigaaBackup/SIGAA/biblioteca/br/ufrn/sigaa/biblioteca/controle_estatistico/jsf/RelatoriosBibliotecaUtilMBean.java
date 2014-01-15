/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 31/08/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.RelatorioBiblioteca;

/**
 * Managed Bean utilitário para a parte de relatórios da biblioteca.
 *
 * @author Fred_Castro
 * @author Bráulio
 * @since 26/12/2008
 * @vesion 28/06/2011 Jadson - deixando nesse MBean apenas a função de configurar as sub abas dos 
 *    relatórios e demais opções que venham a aparecer.
 */

@Component("relatoriosBibliotecaUtilMBean")
@Scope("request")
public class RelatoriosBibliotecaUtilMBean extends SigaaAbstractController <RelatorioBiblioteca> {

	/**
	 *  Método responsável por configurar a aba padrão a ser exibida dentro do menu dos relatórios
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/controle_estatistico</li>
	 *   </ul>
	 */
	public String getTabAtiva(){
		String aba = (String) getCurrentSession().getAttribute("aba");
		String subAba = (String) getCurrentSession().getAttribute("subAba");
		
		if("relatorios".equalsIgnoreCase(aba) && StringUtils.notEmpty(subAba) )
			return subAba;
		else
			return "tabProcessosTecnicos";  // aba padrão
	}
	
	/**
	 * 
	 *  Esse método só existe para não dar erro quando o rich:tabPanel tenta submeter o valor da aba.
	 *  Mas esse valor não é utilizado.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/controle_estatistico</li>
	 *   </ul>
	 */
	public void setTabAtiva(String tabAtiva) {
		// Não remover esse método senão vai dar erro.
	}
	
	
}