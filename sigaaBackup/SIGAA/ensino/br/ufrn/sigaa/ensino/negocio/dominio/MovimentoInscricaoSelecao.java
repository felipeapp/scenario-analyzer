/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;

/**
 * Movimento utilizado durante o cadastro de uma inscri��o em processos seletivos
 * 
 * 
 * @author wendell
 *
 */
@SuppressWarnings("serial")
public class MovimentoInscricaoSelecao extends AbstractMovimentoAdapter {

	/** Inscri��o do Candidato. */
	private InscricaoSelecao inscricaoSelecao;
	
	/** Respostas do candidato ao question�rio. */
	private QuestionarioRespostas questionarioRespostas;
	
	/** Arquivo do Projeto enviado pelo Candidato. */
	private UploadedFile arquivoProjeto;

	/** Construtor padr�o. */
	public MovimentoInscricaoSelecao() {
	}
	
	/** Construtor parametrizado.
	 * @param inscricaoSelecao
	 */
	public MovimentoInscricaoSelecao(InscricaoSelecao inscricaoSelecao) {
		this.inscricaoSelecao = inscricaoSelecao;
	}

	/** Retorna a Inscri��o do Candidato. 
	 * @return
	 */
	public InscricaoSelecao getInscricaoSelecao() {
		return inscricaoSelecao;
	}

	/** Seta a Inscri��o do Candidato. 
	 * @param inscricaoSelecao
	 */
	public void setInscricaoSelecao(InscricaoSelecao inscricaoSelecao) {
		this.inscricaoSelecao = inscricaoSelecao;
	}

	/** Retorna as respostas do candidato ao question�rio. 
	 * @return Respostas do candidato ao question�rio.
	 */
	public QuestionarioRespostas getQuestionarioRespostas() {
		return questionarioRespostas;
	}

	/** Seta as respostas do candidato ao question�rio.
	 * @param questionarioRespostas Respostas do candidato ao question�rio.
	 */
	public void setQuestionarioRespostas(QuestionarioRespostas questionarioRespostas) {
		this.questionarioRespostas = questionarioRespostas;
	}

	/** Retorna o arquivo do Projeto enviado pelo Candidato.
	 * @return Arquivo do Projeto enviado pelo Candidato.
	 */
	public UploadedFile getArquivoProjeto() {
		return arquivoProjeto;
	}

	/** Seta o arquivo do Projeto enviado pelo Candidato.
	 * @param arquivoProjeto Arquivo do Projeto enviado pelo Candidato.
	 */
	public void setArquivoProjeto(UploadedFile arquivoProjeto) {
		this.arquivoProjeto = arquivoProjeto;
	}
	
}
