/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Estrat�gia de composi��o de matr�cula que utiliza o ano, n�vel de ensino, e
 * uma sequ�ncia num�rica, no seguinte formato: YYYYNXXXXD<br/>
 * Onde:
 * <ul>
 *   <li><b>YYYY</b> � o ano de ingresso do discente</li>
 *   <li><b>N</b> � o digito referente a faixa do n�vel</li>
 *   <li><b>XXXX</b> � um n�mero sequencial para o ano e n�vel de ensino</li>
 *   <li><b>D</b> � o digito verificador</li>
 * </ul>
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class ComposicaoMatriculaPorAnoNivel implements EstrategiaComposicaoMatricula {

	/** Retorna a sequencia que ser� utilizada para a gera��o da matr�cula. Caso a sequ�ncia n�o exista, dever� ser criada uma nova a ser persistida. 
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

	/** Retorna uma matr�cula para ser atribu�da � um aluno. A matr�cula poder� ser composta de atributos da sequ�ncia (ano, per�odo, sequ�ncia) e do discente (n�vel de gradua��o, curso, etc.).
	 *  Ao retornar a matr�cula, a sequ�ncia permanecer� inalterada. O seu incremento dever� ser realizado no trecho de c�digo que antecede a composi��o da matr�cula.
	 *  
	 * @param sequencia
	 * @param discente
	 * @return
	 * @throws ArqException
	 */
	@Override
	public long compoeMatricula(SequenciaGeracaoMatricula sequencia, Discente discente) throws ArqException {
		if (sequencia.getSequencia() > SequenciaGeracaoMatricula.LIMITE_SEQUENCIA_MATRICULA) {
			throw new ArqException("Houve um problema com a sequencia utilizada na gera��o da matr�cula. " +
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
	 * Retorna o d�gito referente ao nivel
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
	 * Gerar digito verificador para a matr�cula.
	 * O algoritmo foi adaptado a partir da gera��o de digitos
	 * verificadores das matr�culas de gradua��o original do PontoA
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