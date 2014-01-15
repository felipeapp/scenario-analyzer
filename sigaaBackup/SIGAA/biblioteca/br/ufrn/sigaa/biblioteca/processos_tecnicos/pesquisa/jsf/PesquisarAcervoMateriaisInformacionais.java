/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 11/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.List;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.biblioteca.jsf.PesquisaMateriaisInformacionaisMBean;
import br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 * <p>Interface que deve ser implementada por qualquer MBean que queira realizar buscas de materiais na biblioteca 
 * no acervo a partir da busca de T�tulos </p>
 * 
 * 
 * <p><i> A diferen�a para a interface  {@link PesquisarAcervoBiblioteca}, � que neste caso a pesquisa seleciona materiais, n�o 
 * apenas o T�tulo dos materiais</i> </p>
 * 
 * <p><i> A diferen�a para a interface  {@link PesquisarMateriaisInformacionais}, � que neste caso, a busca j� come�a na pesquisa no acervo para
 * selecionar os materiais de um T�tulo, no outro caso � chamado uma tela de pesquisa padr�o de materiais que por sua vez tamb�m pode 
 * chamar essa pesquisa para selecionar materiais de um T�tulo.</i> </p>
 * 
 * @author jadson
 * @see {@link PesquisaMateriaisInformacionaisMBean}
 * @see {@link PesquisarAcervoBiblioteca}
 *
 */
public interface PesquisarAcervoMateriaisInformacionais {

	/**
	 *   Configura no MBean que chamou a busca os materiais selecionado.
	 *
	 *   <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setMateriaisRetornadosDaPesquisaAcervo(List<MaterialInformacional> materiais) throws ArqException;

	
	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usu�rio seleciona os materiais.
	 *
	 *   <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionouMateriaisRetornadosDaPesquisaAcervo() throws ArqException;
	
	
	
	/** 
	 *  M�todo que deve ser implementado para indicar se o bot�o voltar deve ser mostrado ao n�o na tela da pesquisa no acervo.
	 *
	 *   <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public boolean isUtilizaBotaoVoltarBuscaPesquisarAcervoMateriais();
	
	
	/**
	 *   M�todo que deve implementar a a��o do bot�o voltar na tela de busca no acervo. (tela de busca por t�tulos)
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
	public String voltarBuscaPesquisarAcervoMateriais() throws ArqException;
	
	/**
	 *  M�todo que ***descreve*** a opera��o (caso de uso) que chamou o caso de busca no acervo
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String getDescricaoOperacaoUtilizandoBuscaAcervoMateriais();
	
}
