/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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

/** Interface que define a estratégia de composição de matrícula a ser atribuída ao discente.
 * @author Édipo Elder F. Melo
 *
 */
public interface EstrategiaComposicaoMatricula {

	/** Retorna a sequencia que será utilizada para a geração da matrícula. Caso a sequência não exista, deverá ser criada uma nova a ser persistida.
	 * @param sessao
	 * @param discente
	 * @return
	 */
	public SequenciaGeracaoMatricula getSequenciaGeracaoMatricula(Session session, Discente discente);
	
	/** Retorna uma matrícula para ser atribuída à um aluno. A matrícula poderá ser composta de atributos da sequência (ano, período, sequência) e do discente (nível de graduação, curso, etc.).
	 *  Ao retornar a matrícula, a sequência permanecerá inalterada. O seu incremento deverá ser realizado no trecho de código que antecede a composição da matrícula.
	 *  
	 * @param sequencia
	 * @param discente
	 * @return
	 * @throws ArqException
	 */
	public long compoeMatricula(SequenciaGeracaoMatricula sequencia, Discente discente) throws ArqException;
}
