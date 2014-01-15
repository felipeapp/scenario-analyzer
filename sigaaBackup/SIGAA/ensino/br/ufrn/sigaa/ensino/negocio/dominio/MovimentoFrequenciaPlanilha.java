/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Objeto movimento que carrega todas as frequ�ncias lan�adas
 * na planilha.
 *
 * @author Fred de Castro
 *
 */
public class MovimentoFrequenciaPlanilha extends AbstractMovimentoAdapter {

	private List<Object []> listagemPlanilha;
	private Turma turma;

	public MovimentoFrequenciaPlanilha(List <Object []> listagemPlanilha, Turma turma) {
		setCodMovimento(SigaaListaComando.LANCAR_FREQUENCIA_PLANILHA);
		this.listagemPlanilha = listagemPlanilha;
		this.turma = turma;
	}

	public List<Object []> getListagemPlanilha() {
		return listagemPlanilha;
	}
	
	public void setListagemPlanilha(List<Object[]> listagemPlanilha) {
		this.listagemPlanilha = listagemPlanilha;
	}

	public Turma getTurma (){
		return turma;
	}
}