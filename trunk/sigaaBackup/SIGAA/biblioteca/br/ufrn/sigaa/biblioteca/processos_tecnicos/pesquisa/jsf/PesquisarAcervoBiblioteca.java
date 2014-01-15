/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 12/07/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;

/**
 *
 * <p>Interface que deve ser implementada por qualquer MBean que queira realizar buscas no acervo da biblioteca. </p>
 * <p><i> ( Quando a busca vem de outro caso de uso e ap�s selecionar um t�tulo o fluxo deve retorna para o caso de uso chamador ) </i> </p>
 * 
 * @author jadson
 * @see {@link PesquisaInternaBibliotecaMBean}
 */
public interface PesquisarAcervoBiblioteca {

	/**
	 *   Retorna ao MBean que chamou a busca o t�tulo selecionado. (As buscas do acervo da biblioteca sempre trabalhando com o objeto cache
	 *   , a partir dele � poss�ve obter as informa��es completas no Titulo)
	 *
	 *   <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setTitulo(CacheEntidadesMarc cache) throws ArqException;

	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usu�rio seleciona o t�tulo no acervo da biblioteca.
	 *
	 *   <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionaTitulo() throws ArqException;
	
	
	/**
	 *   M�todo que deve implementar a a��o do bot�o voltar na tela de busca do acervo da biblioteca.
	 *
	 *     <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarBuscaAcervo() throws ArqException;
	
	
	/**
	 *  M�todo que deve ser implementado para indicar se o bot�o voltar deve ser mostrado ao n�o na tela da pesquisa no acervo
	 *
	 *    <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public boolean isUtilizaVoltarBuscaAcervo();
	
}
