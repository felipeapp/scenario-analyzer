/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/04/2009
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;

/**
 * Classe utilizada para encapsular dados para o processador de inscri��o para o
 * vestibular.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class MovimentoInscricaoVestibular extends AbstractMovimentoAdapter {
	/** Inscri��o a ser persistida. */
	private InscricaoVestibular inscricaoVestibular;
	/** Resposta do candidato ao question�rio s�cio-econ�mico a se persistida. */
	private QuestionarioRespostas respostasQuestionario;
	/** Senha a ser enviada por email ao candidato. */
	private String senha;
	
	public MovimentoInscricaoVestibular() {
	}
	
	/**
	 * Retorna a inscri��o a ser persistida.
	 * 
	 * @return
	 */
	public InscricaoVestibular getInscricaoVestibular() {
		return inscricaoVestibular;
	}

	/**
	 * Seta a inscri��o a ser persistida.
	 * 
	 * @param inscricaoVestibular
	 */
	public void setInscricaoVestibular(InscricaoVestibular inscricaoVestibular) {
		this.inscricaoVestibular = inscricaoVestibular;
	}

	/**
	 * Retorna a resposta do candidato ao question�rio s�cio-econ�mico a se
	 * persistida.
	 * 
	 * @return
	 */
	public QuestionarioRespostas getRespostasQuestionario() {
		return respostasQuestionario;
	}

	/**
	 * Seta a resposta do candidato ao question�rio s�cio-econ�mico a se
	 * persistida.
	 * 
	 * @param respostasQuestionario
	 */
	public void setRespostasQuestionario(
			QuestionarioRespostas respostasQuestionario) {
		this.respostasQuestionario = respostasQuestionario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}
