/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/03/2011
 *
 */
package br.ufrn.sigaa.ensino.negocio.geracao_matricula;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.ensino.dominio.SequenciaGeracaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Estratégia de composição de matrícula que utiliza o ano e período de ingresso do discente, e
 * uma sequência numérica, no seguinte formato: YYYYPXXXXD<br/>
 * Onde:
 * <ul>
 *   <li><b>YYYY</b> é o ano de ingresso do discente</li>
 *   <li><b>P</b> é o período de ingresso do discente</li>
 *   <li><b>XXXX</b> é um número sequencial para o ano e nível de ensino</li>
 *   <li><b>D</b> é o digito verificador</li>
 * </ul>
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ComposicaoMatriculaPorAnoPeriodo implements EstrategiaComposicaoMatricula {

	/** Retorna a sequencia que será utilizada para a geração da matrícula. Caso a sequência não exista, deverá ser criada uma nova a ser persistida. 
	 * @param sessao
	 * @param discente
	 * @return
	 */
	@Override
	public SequenciaGeracaoMatricula getSequenciaGeracaoMatricula(Session session, Discente discente) {
		Criteria c = session.createCriteria(SequenciaGeracaoMatricula.class);
		c.add(Expression.eq("ano", discente.getAnoIngresso()));
		c.add(Expression.eq("periodo", discente.getPeriodoIngresso()));
		SequenciaGeracaoMatricula sequencia = (SequenciaGeracaoMatricula) c.setLockMode(LockMode.UPGRADE).uniqueResult();
		if (sequencia == null) {
			sequencia = new SequenciaGeracaoMatricula();
			sequencia.setAno(discente.getAnoIngresso());
			sequencia.setPeriodo(discente.getPeriodoIngresso());
		}
		return sequencia;
	}

	/** Retorna uma matrícula para ser atribuída à um aluno. A matrícula poderá ser composta de atributos da sequência (ano, período, sequência) e do discente (nível de graduação, curso, etc.).
	 *  Ao retornar a matrícula, a sequência permanecerá inalterada. O seu incremento deverá ser realizado no trecho de código que antecede a composição da matrícula.
	 *  
	 * @param sequencia
	 * @param discente
	 * @return
	 * @throws ArqException
	 */
	@Override
	public long compoeMatricula(SequenciaGeracaoMatricula sequencia, Discente discente) throws ArqException {
		if (sequencia.getSequencia() > SequenciaGeracaoMatricula.LIMITE_SEQUENCIA_MATRICULA) {
			throw new ArqException("Houve um problema com a sequencia utilizada na geração da matrícula. " +
					"Por favor, contate os administradores do sistema.");
		}
		StringBuilder matricula = new StringBuilder();
		matricula.append(sequencia.getAno());
		matricula.append(sequencia.getPeriodo());
		matricula.append(UFRNUtils.completaZeros(sequencia.getSequencia(), SequenciaGeracaoMatricula.NUMERO_DIGITOS_SEQUENCIA));
		matricula.append(gerarDigitoVerificador(matricula.toString()));
		return new Long( matricula.toString());
	}
	
	/**
	 * Gerar digito verificador para a matrícula.
	 * O algoritmo foi adaptado a partir da geração de digitos
	 * verificadores das matrículas de graduação original do PontoA
	 *
	 * @param matricula
	 * @return
	 */
	public static int gerarDigitoVerificador(String matricula) {
		int length = matricula.length();
		long acc = 0;
		for ( int i = 0; i < length; i++ ) {
			acc += new Long( "" + matricula.charAt(i) ) * (length - i);
		}
		int digito = (int) ((acc * 10) % 11);
		if (digito == 10) {
			digito = 0;
		}
		return digito;
	}
}
