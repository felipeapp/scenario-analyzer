/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * <p>  MBean para realiza a busca no acervo de artigos na parte interna do sistema </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
@Component(value="pesquisaInternaArtigosBibliotecaMBean")
@Scope(value="request")
public class PesquisaInternaArtigosBibliotecaMBean extends PesquisaPublicaArtigosBibliotecaMBean{

	/**
	 * Define a página de busca interna para o acervo de artigos.
	 */
	public static final String PAGINA_BUSCA_INTERNA_ACERVO_ARTIGO = "/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervoArtigos.jsp";

	
	/**
	 *  Inicia a busca interna para visualizar os resultados do acervo de artigos
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 *    <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 *    <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *    <li>/sigaa.war/portais/discente/include/biblioteca.jsp</li>
	 *     <li>/sigaa.war/portais/discente/medio/menu_discente_medio.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarBuscaArtigo(){
		return telaBuscaPublicaAcervoArtigos();
	}
	
	/**
	 * Sobrescreve a página pública com a página interna de busca no acervo de artigos.
	 * 
	 * Método não chamado por jsp.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaPublicaBibliotecaMBean#telaBuscaPublicaAcervoArtigos()
	 */
	@Override
	public String telaBuscaPublicaAcervoArtigos() {
		return forward(PAGINA_BUSCA_INTERNA_ACERVO_ARTIGO);
	}
	
}
