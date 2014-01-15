/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.List;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais;

/**
 *
 * <p>Interface que deve ser implementada por qualquer MBean que queira realizar buscas de materiais na biblioteca. </p>
 *
 * <p> <i> A busca vai ser realizada pelo c�digo de barras na tela padr�o de pesquisa por c�digo de barras do sistema.
 * Essa tela de pesquisa por c�digo de barras possui um link para tamt�m poder selecionar os materiais de um T�tulo. </i> </p>
 * 
 * <p>Para utilizar a buscas de materiais pelo T�tulo essa perquisa por sua vez pode chamar a <code>PesquisarAcervoMateriaisInformacionais</code> </p>
 * 
 * <p>Para implementar a busca padr�o de materiais por c�digo de barras do sistema � usado o MBean  <code>PesquisaMateriaisInformacionaisMBean</code>.
 *    Essa interface deve ser chamada a partir a partir desse MBean: 
 * </p>
 * 
 * <p>
 *      <code>
 *              getMBean ("pesquisaMateriaisInformacionaisMBean").iniciarBusca(this, "Titulo", 
 *				" descricao ", "Texto Bot�o");
 *		</code>		 
 * </p>
 * 
 * <p>
 * 		<strong> OBSERVA��O: </strong> Deve ser colocando um <a4j:keepAlive> para cada bean que usa essa pesquisa nas p�ginas de busca no acervo: 
 *      pesquisaPadraoMaterialInformacional.jsp, pesquisaTituloCatalografico.jsp, paginaDetalhesMateriais.jsp
 * </p>
 * 
 * @author jadson
 * @see {@link PesquisaMateriaisInformacionaisMBean}
 * @see {@link PesquisarAcervoMateriaisInformacionais}
 *
 */
public interface PesquisarMateriaisInformacionais {

	
	/**
	 *   Configura no MBean que chamou a busca os materiais selecionado.
	 *   
	 *   <br/>
	 *  M�todo n�o chamado por nenhuma JSP.
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setMateriaisPesquisaPadraoMateriais(List<MaterialInformacional> materiais) throws ArqException;

	
	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usu�rio seleciona os materiais.
	 *
	 *   <br/>
	 *  M�todo n�o chamado por nenhuma JSP.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionouMateriaisPesquisaPadraoMateriais() throws ArqException;
	
	
	/**
	 *   M�todo que deve implementar a a��o do bot�o voltar na tela padr�o de busca dos materiais.
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa/biblioteca/pesquisaPadraoMaterialInformacional.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarBuscaPesquisaPadraoMateriais() throws ArqException;
	
	
	
}
