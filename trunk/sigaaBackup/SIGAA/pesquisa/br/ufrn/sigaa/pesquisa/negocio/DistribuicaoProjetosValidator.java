/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Classe com m�todos para valida��o realizadas durante distribui��es
 * de projetos de pesquisa a consultores
 *
 * @author Ricardo Wendell
 */
public class DistribuicaoProjetosValidator {

	/**
	 * Valida se o projeto est� pass�vel de distribui��o
	 *
	 * Invocado pela classe: DistribuirProjetoPesquisaAction.java 
	 * @param projeto
	 * @param lista
	 */
	public static void validaProjeto(ProjetoPesquisa projeto,
			ListaMensagens lista) {

		if(projeto.getProjeto().getMedia() != null)
			lista.addErro(" O projeto informado n�o pode ser distribu�do para avalia��o pois j� possui m�dia registrada.");
			
	}

	/**
	 * Realiza valida��o relacionadas a distribui��es manuais de projeto
	 *
	 * Invocado pela classe: DistribuirProjetoPesquisaAction.java
	 * @param consultor
	 * @param projetos
	 * @param lista
	 */
	public static void validaDistribuicaoManual(Consultor consultor, Collection<ProjetoPesquisa> projetos,
			ListaMensagens lista) {
		// Validar consultor informado
		ValidatorUtil.validateRequired(consultor, "Consultor", lista);

		// Validar cole��o de projetos
		if (projetos == null || projetos.isEmpty() ) {
			lista.addErro(" � necess�rio informar no m�nimo um projeto de pesquisa para realizar a distribui��o ");
		}
	}
}
