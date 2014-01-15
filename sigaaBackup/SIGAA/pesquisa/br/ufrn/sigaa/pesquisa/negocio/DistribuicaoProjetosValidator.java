/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe com métodos para validação realizadas durante distribuições
 * de projetos de pesquisa a consultores
 *
 * @author Ricardo Wendell
 */
public class DistribuicaoProjetosValidator {

	/**
	 * Valida se o projeto está passível de distribuição
	 *
	 * Invocado pela classe: DistribuirProjetoPesquisaAction.java 
	 * @param projeto
	 * @param lista
	 */
	public static void validaProjeto(ProjetoPesquisa projeto,
			ListaMensagens lista) {

		if(projeto.getProjeto().getMedia() != null)
			lista.addErro(" O projeto informado não pode ser distribuído para avaliação pois já possui média registrada.");
			
	}

	/**
	 * Realiza validação relacionadas a distribuições manuais de projeto
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

		// Validar coleção de projetos
		if (projetos == null || projetos.isEmpty() ) {
			lista.addErro(" É necessário informar no mínimo um projeto de pesquisa para realizar a distribuição ");
		}
	}
}
