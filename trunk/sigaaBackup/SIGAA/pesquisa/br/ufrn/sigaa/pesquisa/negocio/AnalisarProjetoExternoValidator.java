/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe para fazer as valida��es do caso de uso de analisar projeto externo
 * @author Victor Hugo
 */
public class AnalisarProjetoExternoValidator {

	/**
	 * projeto precisa ser Externo e estar com a situa��o SUBMETIDO
	 * 
	 * M�todo invocado na classe AnalisarProjetoExternoAction.java
	 * @param projeto
	 * @param lista
	 */
	public static void validaProjetoExterno(ProjetoPesquisa projeto, ListaMensagens lista) {

		if( projeto.isInterno() )
			lista.addErro("O projeto de pesquisa deve ser externo.");

		if( projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.SUBMETIDO )
			lista.addErro("O projeto deve estar com a situa��o submetido.");

	}

}
