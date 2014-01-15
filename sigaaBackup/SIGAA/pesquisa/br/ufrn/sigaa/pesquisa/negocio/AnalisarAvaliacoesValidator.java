/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.ArrayList;
import java.util.Map;

import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Classe para fazer as validações na analise de projetos
 * @author Victor Hugo
 *
 */
public class AnalisarAvaliacoesValidator {

	/**
	 * Valida se todos os projetos estão e uma situação valida para que seja realizada a analise
	 * situações: DISTRIBUIDO_AUTOMATICAMENTE, AVALIACAO_INSUFICIENTE, DISTRIBUIDO_MANUALMENTE
	 * 
	 * JSP: Não invocado por jsp.
	 * Invocado pela classe: 
	 * 			/SIGAA/pesquisa/br/ufrn/sigaa/pesquisa/struts/AnalisarAvaliacoesAction.java
	 * @param analise
	 * @param form
	 * @param lista
	 */
	public static void validaAnalises(Map<ProjetoPesquisa, Integer> analise, ArrayList<MensagemAviso> lista) {

		/*
		for( ProjetoPesquisa projeto : analise.keySet() ){
			if( projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE
					|| projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE
					|| projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.AVALIACAO_INSUFICIENTE
					|| projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.CADASTRADO ){
				ValidatorUtil.addMensagemErro("Só é possível alterar a situação de projetos distribuídos ainda não analisados.", lista);
			}
		}
		*/
	}

}
