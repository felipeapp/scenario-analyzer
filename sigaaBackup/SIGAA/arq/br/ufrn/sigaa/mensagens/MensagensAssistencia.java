/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 23/09/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do Módulo Assistência ao Estudante.
 * 
 * @author Agostinho
 *
 */
public class MensagensAssistencia {
	/**  Prefixo para estas mensagens. */
	static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.SAE.getId() + "_";
	/** Data inválida */
	public static final String DATA_INVALIDA = PREFIX + "1";
	/** Data Inicial Maior que a data Final*/
	public static final String DATA_INICIAL_MAIOR_DATA_FINAL = PREFIX + "2";
	
	
}
