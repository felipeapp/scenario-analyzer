/**
 * 
 */
package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 * Movimento com ações específicas para o cadastro de docentes externos.
 * 
 * @author Leonardo
 *
 */
public class MovimentoCadastroDocenteExterno extends MovimentoCadastro {

	
	public static final int ACAO_GERAR_MATRICULA = 21;
	public static final int ACAO_NAO_GERAR_MATRICULA = 22;
	
}
