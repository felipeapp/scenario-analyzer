/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 16/01/2009
 *
 */

package br.ufrn.sigaa.processamento.batch;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao;

/**
 * Classe que armazena a lista de alunos a serem pr�-processados
 *
 * @author David Pereira
 *
 */
public class ListaAlunosPreProcessamento extends ListaProcessamentoBatch<Integer> {

	@Override
	public void carregar(int ano, int periodo, boolean rematricula, ProcessamentoMatriculaDao dao) {
		try {
			lista.clear();

			List<Integer> alunosBuscados = dao.findAlunosPreProcessamento(ano, periodo, rematricula);
			if (!isEmpty(alunosBuscados)) {
				for ( Integer dg : alunosBuscados )
					lista.add(dg);
			}
			total = lista.size();
		} finally {
			dao.close();
		}		
	}
	
}
