/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 29/05/2009
 *
 */	
package br.ufrn.sigaa.assistencia.relatorio.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.avaliacao.dominio.Pergunta;

/**
 * Classe para encapsular os dados do relat�rio
 * 
 * @author Henrique Andr�
 *
 */
public class RelatorioQuestionarioCadastroUnico {

	private Pergunta pergunta = new Pergunta();
	private List<LinhaRespostaQuestionario> linhaResposta = new ArrayList<LinhaRespostaQuestionario>();

	public void addAlternativa(LinhaRespostaQuestionario linha) {
		linhaResposta.add(linha);
	}
	
	/**
	 * N�mero de pessoas que responderam a Pergunta 
	 */
	private long totalParticipantes;
	
	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public List<LinhaRespostaQuestionario> getLinhaResposta() {
		return linhaResposta;
	}

	public void setLinhaResposta(List<LinhaRespostaQuestionario> linhaResposta) {
		this.linhaResposta = linhaResposta;
	}

	public long getTotalParticipantes() {
		return totalParticipantes;
	}

	public void setTotalParticipantes(long totalParticipantes) {
		this.totalParticipantes = totalParticipantes;
	}

}
