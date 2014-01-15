/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe utilizada para encapsular dados para o processador de inscrição para o
 * vestibular.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class MovimentoInscricaoVestibular extends AbstractMovimentoAdapter {
	/** Inscrição a ser persistida. */
	private InscricaoVestibular inscricaoVestibular;
	/** Resposta do candidato ao questionário sócio-econômico a se persistida. */
	private QuestionarioRespostas respostasQuestionario;
	/** Senha a ser enviada por email ao candidato. */
	private String senha;
	
	public MovimentoInscricaoVestibular() {
	}
	
	/**
	 * Retorna a inscrição a ser persistida.
	 * 
	 * @return
	 */
	public InscricaoVestibular getInscricaoVestibular() {
		return inscricaoVestibular;
	}

	/**
	 * Seta a inscrição a ser persistida.
	 * 
	 * @param inscricaoVestibular
	 */
	public void setInscricaoVestibular(InscricaoVestibular inscricaoVestibular) {
		this.inscricaoVestibular = inscricaoVestibular;
	}

	/**
	 * Retorna a resposta do candidato ao questionário sócio-econômico a se
	 * persistida.
	 * 
	 * @return
	 */
	public QuestionarioRespostas getRespostasQuestionario() {
		return respostasQuestionario;
	}

	/**
	 * Seta a resposta do candidato ao questionário sócio-econômico a se
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
