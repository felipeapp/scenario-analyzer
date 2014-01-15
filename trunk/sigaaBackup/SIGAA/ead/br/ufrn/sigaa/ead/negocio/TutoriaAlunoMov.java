/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/09 - 18:39:49
 */
package br.ufrn.sigaa.ead.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.dominio.TutoriaAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Classe de movimento para Tuturia Aluno
 * @author David Pereira
 *
 */
public class TutoriaAlunoMov extends AbstractMovimentoAdapter {

	private Collection<DiscenteGraduacao> discentes;
	
	private TutorOrientador tutor;
	
	private TutoriaAluno tutoria;

	/**
	 * @return the discentes
	 */
	public Collection<DiscenteGraduacao> getDiscentes() {
		return discentes;
	}

	/**
	 * @param discentes the discentes to set
	 */
	public void setDiscentes(Collection<DiscenteGraduacao> discentes) {
		this.discentes = discentes;
	}

	/**
	 * @return the tutor
	 */
	public TutorOrientador getTutor() {
		return tutor;
	}

	/**
	 * @param tutor the tutor to set
	 */
	public void setTutor(TutorOrientador tutor) {
		this.tutor = tutor;
	}

	/**
	 * @return the tutoria
	 */
	public TutoriaAluno getTutoria() {
		return tutoria;
	}

	/**
	 * @param tutoria the tutoria to set
	 */
	public void setTutoria(TutoriaAluno tutoria) {
		this.tutoria = tutoria;
	}
	
}
