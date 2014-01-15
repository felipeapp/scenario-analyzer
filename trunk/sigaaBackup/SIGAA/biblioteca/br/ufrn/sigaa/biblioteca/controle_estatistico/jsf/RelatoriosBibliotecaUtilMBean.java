/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Managed Bean utilit�rio para a parte de relat�rios da biblioteca.
 *
 * @author Fred_Castro
 * @author Br�ulio
 * @since 26/12/2008
 * @vesion 28/06/2011 Jadson - deixando nesse MBean apenas a fun��o de configurar as sub abas dos 
 *    relat�rios e demais op��es que venham a aparecer.
 */

@Component("relatoriosBibliotecaUtilMBean")
@Scope("request")
public class RelatoriosBibliotecaUtilMBean extends SigaaAbstractController <RelatorioBiblioteca> {

	/**
	 *  M�todo respons�vel por configurar a aba padr�o a ser exibida dentro do menu dos relat�rios
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
			return "tabProcessosTecnicos";  // aba padr�o
	}
	
	/**
	 * 
	 *  Esse m�todo s� existe para n�o dar erro quando o rich:tabPanel tenta submeter o valor da aba.
	 *  Mas esse valor n�o � utilizado.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/controle_estatistico</li>
	 *   </ul>
	 */
	public void setTabAtiva(String tabAtiva) {
		// N�o remover esse m�todo sen�o vai dar erro.
	}
	
	
}