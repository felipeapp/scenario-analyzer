/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Valida��es sobre os par�metros do m�dulo de pesquisa
 * 
 * @author Victor Hugo
 *
 */
public class ParametrosPesquisaValidator {

	
	/**
	 * M�todo respons�vel pela valida��o dos par�metros de pesquisa.
	 * 
	 * Invocado pela classe: ParametrosPesquisaAction.java
	 * 
	 * @param parametros
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaParametros(ParametrosPesquisaForm parametros, ListaMensagens lista) throws DAOException{
		
		validaInt(parametros.getToleranciaSubmissao(), "Toler�ncia de Submiss�o", lista);
		validaInt(parametros.getDuracaoMaximaProjetos(), "Dura��o M�xima de projetos", lista);
		validaInt(parametros.getLimiteCotasProjeto(), "Limite de Cotas de Projeto", lista);
		validaInt(parametros.getLimiteCotasOrientador(), "Limite de Cotas de Orientador", lista);
	}
	
}
