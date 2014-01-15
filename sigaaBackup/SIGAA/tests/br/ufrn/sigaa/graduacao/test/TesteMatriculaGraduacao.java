/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 16/09/2004
 *
 */
package br.ufrn.sigaa.graduacao.test;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.ensino.dominio.SequenciaGeracaoMatricula;
import br.ufrn.sigaa.ensino.negocio.geracao_matricula.EstrategiaComposicaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * Classe para teste de matricula em graduação de desenvolvimento.
 * 
 * @author Ricardo Wendell
 *
 */
public class TesteMatriculaGraduacao {

	/** Realiza os testes
	 * @param args
	 * @throws ArqException 
	 */
	public static void main(String[] args) throws ArqException {
		int inicio =  1368;// - 1403;
		String classe = "br.ufrn.sigaa.ensino.negocio.geracao_matricula.ComposicaoMatriculaPorAnoNivel";
		EstrategiaComposicaoMatricula estrategia = ReflectionUtils.newInstance(classe);
		Discente discente = new Discente();
		discente.setNivel(NivelEnsino.BASICO);
		discente.setAnoIngresso(2012);
		
		SequenciaGeracaoMatricula sequencia = new SequenciaGeracaoMatricula();
		sequencia.setAno(2012);
		sequencia.setNivel(NivelEnsino.BASICO);
		sequencia.setSequencia(inicio);
		for (int i = inicio; i < inicio + 35; i++) {
			sequencia.incrementarSequencia();
			// Compõe a matrícula a partir da sequência e do discente.
			Long matriculaGerada = estrategia.compoeMatricula(sequencia, discente);
			System.out.println(matriculaGerada);
		}
	}

}
