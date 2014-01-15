/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 16/01/2009 
 *
 */

package br.ufrn.sigaa.processamento.batch;

import java.util.List;

import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao;

/**
 * Classe que armazena a lista de turmas a serem processadas
 *
 * @author David Pereira
 *
 */
public class ListaTurmasProcessar extends ListaProcessamentoBatch<Turma> {

	@Override
	public void carregar(int ano, int periodo, boolean rematricula, ProcessamentoMatriculaDao dao) {
		try {
			lista.clear();

			List<Turma> turmasBuscadas = dao.findTurmasProcessar(ano, periodo, rematricula);
			for ( Turma t : turmasBuscadas )
				lista.add(t);

			total = lista.size();
		} finally {
			dao.close();
		}
	}


}
