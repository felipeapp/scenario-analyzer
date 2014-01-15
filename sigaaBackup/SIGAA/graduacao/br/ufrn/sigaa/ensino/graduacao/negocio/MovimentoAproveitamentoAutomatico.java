/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 6, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * 
 * @author Victor Hugo
 * 
 */
public class MovimentoAproveitamentoAutomatico extends AbstractMovimentoAdapter {

	/**
	 * matriculas a serem aproveitadas
	 */
	private Collection<MatriculaComponente> matriculas;

	/**
	 * discente de origem do aproveitamento
	 */
	private DiscenteGraduacao discenteOrigem;

	/**
	 * discente de destino do aproveitamento
	 */
	private DiscenteGraduacao discenteDestino;

	public DiscenteGraduacao getDiscenteDestino() {
		return discenteDestino;
	}

	public void setDiscenteDestino(DiscenteGraduacao discenteDestino) {
		this.discenteDestino = discenteDestino;
	}

	public DiscenteGraduacao getDiscenteOrigem() {
		return discenteOrigem;
	}

	public void setDiscenteOrigem(DiscenteGraduacao discenteOrigem) {
		this.discenteOrigem = discenteOrigem;
	}

	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

}
