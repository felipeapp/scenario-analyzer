/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 23/09/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do M�dulo Assist�ncia ao Estudante.
 * 
 * @author Agostinho
 *
 */
public class MensagensAssistencia {
	/**  Prefixo para estas mensagens. */
	static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.SAE.getId() + "_";
	/** Data inv�lida */
	public static final String DATA_INVALIDA = PREFIX + "1";
	/** Data Inicial Maior que a data Final*/
	public static final String DATA_INICIAL_MAIOR_DATA_FINAL = PREFIX + "2";
	
	
}
