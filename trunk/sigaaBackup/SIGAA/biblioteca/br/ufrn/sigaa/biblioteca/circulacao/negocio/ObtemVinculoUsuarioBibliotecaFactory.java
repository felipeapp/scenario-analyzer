/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Classe que instancia e retorna a classe que contém as regras de negócio para obteção do vínculo 
 *    preferencial para realizar os empréstimos na biblioteca </p>
 *
 * <p> <i> Caso os tipos de usuários existentes na instituição e a ordem de preferencia mude. Deve-se 
 *  criar uma nova classe e configurar o parametro do sistema </i> </p>
 * 
 * @author jadson
 *
 */
public class ObtemVinculoUsuarioBibliotecaFactory {

	/**
	 * Instancia e retorna a regras para obter o vínculo dos usuários da biblioteca utilizada localmente.
	 *
	 * @return
	 */
	public ObtemVinculoUsuarioBibliotecaStrategy getEstrategiaVinculo(){
		
		return ReflectionUtils.newInstance( ParametroHelper.getInstance()
				.getParametro(ParametrosBiblioteca.NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_OBTENCAO_VINCULO) );
	}
}
