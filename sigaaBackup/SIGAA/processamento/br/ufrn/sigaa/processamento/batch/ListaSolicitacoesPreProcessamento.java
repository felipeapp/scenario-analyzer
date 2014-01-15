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

import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao;

/**
 * Classe que armazena a lista de solicitações de matrícula que
 * devem virar matrículas em espera.
 *
 * @author David Pereira
 *
 */
public class ListaSolicitacoesPreProcessamento extends ListaProcessamentoBatch<SolicitacaoMatricula> {

	@Override
	public void carregar(int ano, int periodo, boolean rematricula, ProcessamentoMatriculaDao dao) {
		try {
			lista.clear();

			List<SolicitacaoMatricula> solicitacoes = dao.findSolicitacoesMatricula(ano, periodo, rematricula);
			for ( SolicitacaoMatricula sol : solicitacoes )
				lista.add(sol);

			total = lista.size();
		} finally {
			dao.close();
		}
	}


}
