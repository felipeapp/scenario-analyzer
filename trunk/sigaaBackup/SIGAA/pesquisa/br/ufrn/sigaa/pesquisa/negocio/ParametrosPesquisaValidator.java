/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/01/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validaInt;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.pesquisa.form.ParametrosPesquisaForm;

/**
 * Validações sobre os parâmetros do módulo de pesquisa
 * 
 * @author Victor Hugo
 *
 */
public class ParametrosPesquisaValidator {

	
	/**
	 * Método responsável pela validação dos parâmetros de pesquisa.
	 * 
	 * Invocado pela classe: ParametrosPesquisaAction.java
	 * 
	 * @param parametros
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaParametros(ParametrosPesquisaForm parametros, ListaMensagens lista) throws DAOException{
		
		validaInt(parametros.getToleranciaSubmissao(), "Tolerância de Submissão", lista);
		validaInt(parametros.getDuracaoMaximaProjetos(), "Duração Máxima de projetos", lista);
		validaInt(parametros.getLimiteCotasProjeto(), "Limite de Cotas de Projeto", lista);
		validaInt(parametros.getLimiteCotasOrientador(), "Limite de Cotas de Orientador", lista);
	}
	
}
