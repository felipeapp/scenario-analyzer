/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 16/01/2009 
 *
 */

package br.ufrn.sigaa.processamento.batch;

import br.ufrn.sigaa.processamento.dominio.ResultadoProcessamento;
import br.ufrn.sigaa.processamento.dominio.TurmaProcessada;

/**
 * Thread que consome as turmas para gerar os arquivos 
 * com o resultado do processamento da matrícula.
 *
 * @author David Pereira
 *
 */
public class ResultadoThread extends Thread {

	private String servletContextPath;
	
	private boolean rematricula;

	public ResultadoThread(boolean rematricula, String servletContextPath) {
		this.rematricula = rematricula;
		this.servletContextPath = servletContextPath;
	}

	@Override
	public void run() {

		try {
			int i = 0;
			while (ListaTurmasProcessadas.hasTurma()) {
				TurmaProcessada turma = ListaTurmasProcessadas.getTurma();
				ResultadoProcessamento.criar(turma, rematricula, servletContextPath);
				System.out.println(++i);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
