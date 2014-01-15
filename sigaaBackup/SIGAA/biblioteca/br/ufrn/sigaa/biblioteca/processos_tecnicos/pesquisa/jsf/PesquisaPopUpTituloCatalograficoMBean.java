/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>MBean que gerencia a p�gina de pesquisa PupUp do sistema.</p>
 * 
 *  <p>Utilizado na tela de cataloga��o, para permitir aos operadores do sistema realizarem busca no acervo enquanto est�o
 * realizando uma cataloga��o. </p>
 * 
 * @author jadson
 * @since 07/11/2011
 * @version 1.0 Cria��o da classe
 *
 */
@Component(value="pesquisaPopUpTituloCatalograficoMBean")
@Scope(value="request")
public class PesquisaPopUpTituloCatalograficoMBean extends PesquisaTituloCatalograficoMBean{

	
	/**
	 * Define a p�gina de busca de busca pop up de t�tulos no acervo
	 */
	public static final String PAGINA_PESQUISA_TITULO_POP_UP = "/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaTitulosPopUp.jsp";

	
	/**
	 * 
	 * <p>Sobre esqueve o m�todo da classe pai para retornar as pesquisa para a p�gina pop up</p>
	 *
	 * <br/>
	 * M�todo n�o chamado por jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean#telaPesquisaTitulo()
	 */
	@Override
	public String telaPesquisaTitulo() {
		return forward(PAGINA_PESQUISA_TITULO_POP_UP);
	}
	
	
	
	
}
