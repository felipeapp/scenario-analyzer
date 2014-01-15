/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/12/2008
 *
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.prodocente.atividades.dominio.Estagio;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/**
 * Movimento para o Registro de Atividades.
 * 
 * @author Andre Dantas
 *
 */
public class RegistroAtividadeMov extends AbstractMovimentoAdapter {

	private MatriculaComponente matricula;
	
	private Estagio estagio;
	
	private TrabalhoFimCurso trabalhoFimCurso;

	private boolean registroTutor;
	
	private boolean orientador;
	

	public RegistroAtividadeMov() {}
	
	/**
	 * @param matricula
	 * @param estagio
	 * @param trabalhoFimCurso
	 */
	public RegistroAtividadeMov(MatriculaComponente matricula, Estagio estagio, TrabalhoFimCurso trabalhoFimCurso) {
		super();
		this.matricula = matricula;
		this.estagio = estagio;
		this.trabalhoFimCurso = trabalhoFimCurso;
	}

	/**
	 * @return the matricula
	 */
	public MatriculaComponente getMatricula() {
		return matricula;
	}

	/**
	 * @param matricula the matricula to set
	 */
	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	/**
	 * @return the estagio
	 */
	public Estagio getEstagio() {
		return estagio;
	}

	/**
	 * @param estagio the estagio to set
	 */
	public void setEstagio(Estagio estagio) {
		this.estagio = estagio;
	}

	/**
	 * @return the trabalhoFimCurso
	 */
	public TrabalhoFimCurso getTrabalhoFimCurso() {
		return trabalhoFimCurso;
	}

	/**
	 * @param trabalhoFimCurso the trabalhoFimCurso to set
	 */
	public void setTrabalhoFimCurso(TrabalhoFimCurso trabalhoFimCurso) {
		this.trabalhoFimCurso = trabalhoFimCurso;
	}

	public boolean isRegistroTutor() {
		return this.registroTutor;
	}

	public void setRegistroTutor(boolean registroTutor) {
		this.registroTutor = registroTutor;
	}

	public boolean isOrientador() {
		return orientador;
	}

	public void setOrientador(boolean orientador) {
		this.orientador = orientador;
	}
	
	
}
