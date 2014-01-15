package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface contendo todos os par�metros do m�dulo de ouvidoria.
 * @author suelton
 *
 */
public interface ParametrosOuvidoria {
	
	/**
	 * Indica a partir de quantos dias os usu�rios com manifesta��es pendentes de an�lise receber�o notifica��es.
	 * */
	public static final String DIAS_RESTANTES_NOTIFICACAO_MANIFESTACAO =  Sistema.SIGAA + "_" + SigaaSubsistemas.OUVIDORIA.getId() + "_1";

}
