/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 07/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * <p>MBean que gerencia a página de pesquisa PupUp do sistema.</p>
 * 
 *  <p>Utilizado na tela de catalogação, para permitir aos operadores do sistema realizarem busca no acervo enquanto estão
 * realizando uma catalogação. </p>
 * 
 * @author jadson
 * @since 07/11/2011
 * @version 1.0 Criação da classe
 *
 */
@Component(value="pesquisaPopUpTituloCatalograficoMBean")
@Scope(value="request")
public class PesquisaPopUpTituloCatalograficoMBean extends PesquisaTituloCatalograficoMBean{

	
	/**
	 * Define a página de busca de busca pop up de títulos no acervo
	 */
	public static final String PAGINA_PESQUISA_TITULO_POP_UP = "/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaTitulosPopUp.jsp";

	
	/**
	 * 
	 * <p>Sobre esqueve o método da classe pai para retornar as pesquisa para a página pop up</p>
	 *
	 * <br/>
	 * Método não chamado por jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean#telaPesquisaTitulo()
	 */
	@Override
	public String telaPesquisaTitulo() {
		return forward(PAGINA_PESQUISA_TITULO_POP_UP);
	}
	
	
	
	
}
