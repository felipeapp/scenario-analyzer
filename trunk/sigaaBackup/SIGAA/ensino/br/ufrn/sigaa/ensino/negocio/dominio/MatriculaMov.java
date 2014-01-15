/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 28/09/2006
 *
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Movimento para cadastro de matrículas
 *
 * @author David Ricardo
 *
 */
public class MatriculaMov extends AbstractMovimentoAdapter {

	private Collection<? extends DiscenteAdapter> discentes;

	private Collection<Turma> turmas;

	private SituacaoMatricula situacao;
	
	/**
	 * @return the discente
	 */
	public Collection<? extends DiscenteAdapter> getDiscentes() {
		return discentes;
	}

	/**
	 * @param discente the discente to set
	 */
	public void setDiscentes(Collection<? extends DiscenteAdapter> discentes) {
		this.discentes = discentes;
	}

	/**
	 * @return the turmas
	 */
	public Collection<Turma> getTurmas() {
		return turmas;
	}

	/**
	 * @param turmas the turmas to set
	 */
	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public boolean contains(ComponenteCurricular d) {
		for (Turma t : turmas) {
			if (t.getDisciplina().equals(d))
				return true;
		}
		return false;
	}

	public SituacaoMatricula getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoMatricula situacao) {
		this.situacao = situacao;
	}


}
