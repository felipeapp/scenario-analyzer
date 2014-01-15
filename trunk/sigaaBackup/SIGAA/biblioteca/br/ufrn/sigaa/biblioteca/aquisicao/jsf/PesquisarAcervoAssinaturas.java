/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 05/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;


/**
*
* <p>Interface que deve ser implementada por qualquer MBean que queira realizar buscas no acervo de assinaturas da biblioteca. </p>
* <p><i> ( Quando a busca vem de outro caso de uso e ap�s selecionar uma assinatura o fluxo deve retorna para o caso de uso chamador ) </i> </p>
* 
* <p><i> <strong> Serve para padronizar as buscas no acervo, existindo apenas uma p�gina de consulta que � utilizada por todos os caso de uso, melhorando a manuten��o do sistema. <strong> </i> </p>
* 
* @author jadson
* @see {@link AssinaturaPeriodicoMBean}
* @see {@link RegistraChegadaFasciculoMBean}
*/
public interface PesquisarAcervoAssinaturas {

	/**
	 *   Retorna ao MBean que chamou a busca a assinatura selecionado.
	 *
	 *   <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setAssinatura(Assinatura assinatura) throws ArqException;

	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usu�rio seleciona a assinatura no acervo da biblioteca.
	 *
	 *   <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionaAssinatura() throws ArqException;
	
	
	/**
	 *   M�todo que deve implementar a a��o do bot�o voltar na tela de busca do acervo da biblioteca.
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war//biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarBuscaAssinatura() throws ArqException;
	
	
	/**
	 *  M�todo que deve ser implementado para indicar se o bot�o voltar deve ser mostrado ao n�o na tela da pesquisa de assinaturas no acervo
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war//biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public boolean isUtilizaVoltarBuscaAssinatura();
	
}
