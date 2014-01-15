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

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.ensino.dominio.SequenciaGeracaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Estratégia de composição de matrícula que utiliza o ano, nível de ensino, e
 * uma sequência numérica, no seguinte formato: YYYYNXXXXD<br/>
 * Onde:
 * <ul>
 *   <li><b>YYYY</b> é o ano de ingresso do discente</li>
 *   <li><b>N</b> é o digito referente a faixa do nível</li>
 *   <li><b>XXXX</b> é um número sequencial para o ano e nível de ensino</li>
 *   <li><b>D</b> é o digito verificador</li>
 * </ul>
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ComposicaoMatriculaPorAnoNivel implements EstrategiaComposicaoMatricula {

	/** Retorna a sequencia que será utilizada para a geração da matrícula. Caso a sequência não exista, deverá ser criada uma nova a ser persistida. 
	 * @param sessao
	 * @param discente
	 * @return
	 */
	@Override
	public SequenciaGeracaoMatricula getSequenciaGeracaoMatricula(Session session, Discente discente) {
		Criteria c = session.createCriteria(SequenciaGeracaoMatricula.class);
		c.add(Expression.eq("nivel", SequenciaGeracaoMatricula.getNivelGeracaoMatricula(discente.getNivel())));
		c.add(Expression.eq("ano", discente.getAnoIngresso()));
		SequenciaGeracaoMatricula sequencia = (SequenciaGeracaoMatricula) c.setLockMode(LockMode.UPGRADE).uniqueResult();
		if (sequencia == null) {
			sequencia = new SequenciaGeracaoMatricula();
			sequencia.setNivel(discente.getNivel());
			sequencia.setAno(discente.getAnoIngresso());
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
		matricula.append(getFaixaNivel(discente.getNivel()));
		matricula.append(UFRNUtils.completaZeros(sequencia.getSequencia(), SequenciaGeracaoMatricula.NUMERO_DIGITOS_SEQUENCIA));
		matricula.append(gerarDigitoVerificador(matricula.toString()));
		return new Long( matricula.toString());
	}
	
	/**
	 * Retorna o dígito referente ao nivel
	 * de ensino do discente
	 *
	 * @return
	 */
	private int getFaixaNivel(char nivel) {
		nivel = SequenciaGeracaoMatricula.getNivelGeracaoMatricula(nivel);
		switch (nivel) {
			case NivelEnsino.GRADUACAO: return 0;
			case NivelEnsino.STRICTO: 	return 1;
			case NivelEnsino.LATO: 		return 2;
			case NivelEnsino.BASICO: 	return 3;
			case NivelEnsino.RESIDENCIA: 	return 4;
			case NivelEnsino.FORMACAO_COMPLEMENTAR: return 5;
			default: return 9;
		}
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
