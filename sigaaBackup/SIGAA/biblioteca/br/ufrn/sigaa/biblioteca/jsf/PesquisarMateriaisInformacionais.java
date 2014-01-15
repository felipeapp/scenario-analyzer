/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> <i> A busca vai ser realizada pelo código de barras na tela padrão de pesquisa por código de barras do sistema.
 * Essa tela de pesquisa por código de barras possui um link para tamtém poder selecionar os materiais de um Título. </i> </p>
 * 
 * <p>Para utilizar a buscas de materiais pelo Título essa perquisa por sua vez pode chamar a <code>PesquisarAcervoMateriaisInformacionais</code> </p>
 * 
 * <p>Para implementar a busca padrão de materiais por código de barras do sistema é usado o MBean  <code>PesquisaMateriaisInformacionaisMBean</code>.
 *    Essa interface deve ser chamada a partir a partir desse MBean: 
 * </p>
 * 
 * <p>
 *      <code>
 *              getMBean ("pesquisaMateriaisInformacionaisMBean").iniciarBusca(this, "Titulo", 
 *				" descricao ", "Texto Botão");
 *		</code>		 
 * </p>
 * 
 * <p>
 * 		<strong> OBSERVAÇÃO: </strong> Deve ser colocando um <a4j:keepAlive> para cada bean que usa essa pesquisa nas páginas de busca no acervo: 
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
	 *  Método não chamado por nenhuma JSP.
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setMateriaisPesquisaPadraoMateriais(List<MaterialInformacional> materiais) throws ArqException;

	
	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usuário seleciona os materiais.
	 *
	 *   <br/>
	 *  Método não chamado por nenhuma JSP.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionouMateriaisPesquisaPadraoMateriais() throws ArqException;
	
	
	/**
	 *   Método que deve implementar a ação do botão voltar na tela padrão de busca dos materiais.
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa/biblioteca/pesquisaPadraoMaterialInformacional.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarBuscaPesquisaPadraoMateriais() throws ArqException;
	
	
	
}
