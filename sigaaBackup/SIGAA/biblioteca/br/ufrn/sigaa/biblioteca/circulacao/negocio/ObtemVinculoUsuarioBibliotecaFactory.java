/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 12/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * <p> Classe que instancia e retorna a classe que cont�m as regras de neg�cio para obte��o do v�nculo 
 *    preferencial para realizar os empr�stimos na biblioteca </p>
 *
 * <p> <i> Caso os tipos de usu�rios existentes na institui��o e a ordem de preferencia mude. Deve-se 
 *  criar uma nova classe e configurar o parametro do sistema </i> </p>
 * 
 * @author jadson
 *
 */
public class ObtemVinculoUsuarioBibliotecaFactory {

	/**
	 * Instancia e retorna a regras para obter o v�nculo dos usu�rios da biblioteca utilizada localmente.
	 *
	 * @return
	 */
	public ObtemVinculoUsuarioBibliotecaStrategy getEstrategiaVinculo(){
		
		return ReflectionUtils.newInstance( ParametroHelper.getInstance()
				.getParametro(ParametrosBiblioteca.NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_OBTENCAO_VINCULO) );
	}
}
