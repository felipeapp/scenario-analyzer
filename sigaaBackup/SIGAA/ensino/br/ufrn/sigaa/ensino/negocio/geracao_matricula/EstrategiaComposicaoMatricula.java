/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/03/2011
 *
 */
package br.ufrn.sigaa.ensino.negocio.geracao_matricula;

import org.hibernate.Session;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.dominio.SequenciaGeracaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Interface que define a estrat�gia de composi��o de matr�cula a ser atribu�da ao discente.
 * @author �dipo Elder F. Melo
 *
 */
public interface EstrategiaComposicaoMatricula {

	/** Retorna a sequencia que ser� utilizada para a gera��o da matr�cula. Caso a sequ�ncia n�o exista, dever� ser criada uma nova a ser persistida.
	 * @param sessao
	 * @param discente
	 * @return
	 */
	public SequenciaGeracaoMatricula getSequenciaGeracaoMatricula(Session session, Discente discente);
	
	/** Retorna uma matr�cula para ser atribu�da � um aluno. A matr�cula poder� ser composta de atributos da sequ�ncia (ano, per�odo, sequ�ncia) e do discente (n�vel de gradua��o, curso, etc.).
	 *  Ao retornar a matr�cula, a sequ�ncia permanecer� inalterada. O seu incremento dever� ser realizado no trecho de c�digo que antecede a composi��o da matr�cula.
	 *  
	 * @param sequencia
	 * @param discente
	 * @return
	 * @throws ArqException
	 */
	public long compoeMatricula(SequenciaGeracaoMatricula sequencia, Discente discente) throws ArqException;
}
