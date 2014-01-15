/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p><i> ( Quando a busca vem de outro caso de uso e após selecionar um título o fluxo deve retorna para o caso de uso chamador ) </i> </p>
 * 
 * @author jadson
 * @see {@link PesquisaInternaBibliotecaMBean}
 */
public interface PesquisarAcervoBiblioteca {

	/**
	 *   Retorna ao MBean que chamou a busca o título selecionado. (As buscas do acervo da biblioteca sempre trabalhando com o objeto cache
	 *   , a partir dele é possíve obter as informações completas no Titulo)
	 *
	 *   <p>Método não chamado por página jsp.</p>
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setTitulo(CacheEntidadesMarc cache) throws ArqException;

	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usuário seleciona o título no acervo da biblioteca.
	 *
	 *   <p>Método não chamado por página jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionaTitulo() throws ArqException;
	
	
	/**
	 *   Método que deve implementar a ação do botão voltar na tela de busca do acervo da biblioteca.
	 *
	 *     <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *  Método que deve ser implementado para indicar se o botão voltar deve ser mostrado ao não na tela da pesquisa no acervo
	 *
	 *    <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
