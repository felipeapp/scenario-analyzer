/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
* <p><i> ( Quando a busca vem de outro caso de uso e após selecionar uma assinatura o fluxo deve retorna para o caso de uso chamador ) </i> </p>
* 
* <p><i> <strong> Serve para padronizar as buscas no acervo, existindo apenas uma página de consulta que é utilizada por todos os caso de uso, melhorando a manutenção do sistema. <strong> </i> </p>
* 
* @author jadson
* @see {@link AssinaturaPeriodicoMBean}
* @see {@link RegistraChegadaFasciculoMBean}
*/
public interface PesquisarAcervoAssinaturas {

	/**
	 *   Retorna ao MBean que chamou a busca a assinatura selecionado.
	 *
	 *   <p>Método não chamado por página jsp.</p>
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setAssinatura(Assinatura assinatura) throws ArqException;

	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usuário seleciona a assinatura no acervo da biblioteca.
	 *
	 *   <p>Método não chamado por página jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionaAssinatura() throws ArqException;
	
	
	/**
	 *   Método que deve implementar a ação do botão voltar na tela de busca do acervo da biblioteca.
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war//biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarBuscaAssinatura() throws ArqException;
	
	
	/**
	 *  Método que deve ser implementado para indicar se o botão voltar deve ser mostrado ao não na tela da pesquisa de assinaturas no acervo
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war//biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public boolean isUtilizaVoltarBuscaAssinatura();
	
}
