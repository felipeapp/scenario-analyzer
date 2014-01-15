/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * no acervo a partir da busca de Títulos </p>
 * 
 * 
 * <p><i> A diferença para a interface  {@link PesquisarAcervoBiblioteca}, é que neste caso a pesquisa seleciona materiais, não 
 * apenas o Título dos materiais</i> </p>
 * 
 * <p><i> A diferença para a interface  {@link PesquisarMateriaisInformacionais}, é que neste caso, a busca já começa na pesquisa no acervo para
 * selecionar os materiais de um Título, no outro caso é chamado uma tela de pesquisa padrão de materiais que por sua vez também pode 
 * chamar essa pesquisa para selecionar materiais de um Título.</i> </p>
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
	 *   <p>Método não chamado por página jsp.</p>
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setMateriaisRetornadosDaPesquisaAcervo(List<MaterialInformacional> materiais) throws ArqException;

	
	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usuário seleciona os materiais.
	 *
	 *   <p>Método não chamado por página jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionouMateriaisRetornadosDaPesquisaAcervo() throws ArqException;
	
	
	
	/** 
	 *  Método que deve ser implementado para indicar se o botão voltar deve ser mostrado ao não na tela da pesquisa no acervo.
	 *
	 *   <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *   Método que deve implementar a ação do botão voltar na tela de busca no acervo. (tela de busca por títulos)
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
	public String voltarBuscaPesquisarAcervoMateriais() throws ArqException;
	
	/**
	 *  Método que ***descreve*** a operação (caso de uso) que chamou o caso de busca no acervo
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String getDescricaoOperacaoUtilizandoBuscaAcervoMateriais();
	
}
