/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '22/01/2010'
 *
 */

package br.ufrn.sigaa.ava.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;

/**
 * Movimento responsável por enviar ao processador as notas alteradas pela importação da planilha.
 * 
 * @author Fred_Castro
 *
 */

public class MovimentoImportacaoNotasPlanilha extends AbstractMovimentoAdapter{

	/** Lista de matrículas que serão atualizadas.*/
	private List <MatriculaComponente> matriculasAAtualizar;
	
	/** Lista de notas que serão atualizadas. */
	private List <NotaUnidade> notasAAtualizar;
	
	/** Lista de avaliações que serão atualizadas. */
	private List <Avaliacao> avaliacoesAAtualizar;
	
	public MovimentoImportacaoNotasPlanilha(List<MatriculaComponente> matriculasAAtualizar, List<NotaUnidade> notasAAtualizar, List<Avaliacao> avaliacoesAAtualizar) {
		this.matriculasAAtualizar = matriculasAAtualizar;
		this.notasAAtualizar = notasAAtualizar;
		this.avaliacoesAAtualizar = avaliacoesAAtualizar;
		
		this.setCodMovimento(SigaaListaComando.IMPORTAR_NOTAS_PLANILHA);
	}

	public List<MatriculaComponente> getMatriculasAAtualizar() {
		return matriculasAAtualizar;
	}

	public void setMatriculasAAtualizar(
			List<MatriculaComponente> matriculasAAtualizar) {
		this.matriculasAAtualizar = matriculasAAtualizar;
	}

	public List<NotaUnidade> getNotasAAtualizar() {
		return notasAAtualizar;
	}

	public void setNotasAAtualizar(List<NotaUnidade> notasAAtualizar) {
		this.notasAAtualizar = notasAAtualizar;
	}

	public List<Avaliacao> getAvaliacoesAAtualizar() {
		return avaliacoesAAtualizar;
	}

	public void setAvaliacoesAAtualizar(List<Avaliacao> avaliacoesAAtualizar) {
		this.avaliacoesAAtualizar = avaliacoesAAtualizar;
	}
}
