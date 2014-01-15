package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface contendo todos os parâmetros do módulo de ouvidoria.
 * @author suelton
 *
 */
public interface ParametrosOuvidoria {
	
	/**
	 * Indica a partir de quantos dias os usuários com manifestações pendentes de análise receberão notificações.
	 * */
	public static final String DIAS_RESTANTES_NOTIFICACAO_MANIFESTACAO =  Sistema.SIGAA + "_" + SigaaSubsistemas.OUVIDORIA.getId() + "_1";

}
