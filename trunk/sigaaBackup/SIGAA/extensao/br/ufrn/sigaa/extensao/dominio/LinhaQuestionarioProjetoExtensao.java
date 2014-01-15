package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Linha auxiliar referente a a exporta��o dos dados dos question�rios respondidos.
 * @author guerethes
 */
public class LinhaQuestionarioProjetoExtensao {

	/** Projeto Resposta do question�rio */
	private int idProjeto;

	/** Projeto Resposta do question�rio */
	private String tituloProjeto;

	/** Descri��o da pergunta do question�rio */
	private Collection<String> perguntas;
	
	/** Respostas question�rios */
	private ArrayList<String> respostas;

	public int getIdProjeto() {
		return idProjeto;
	}

	public void setIdProjeto(int idProjeto) {
		this.idProjeto = idProjeto;
	}

	public String getTituloProjeto() {
		return tituloProjeto;
	}

	public void setTituloProjeto(String tituloProjeto) {
		this.tituloProjeto = tituloProjeto;
	}

	public Collection<String> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(Collection<String> perguntas) {
		this.perguntas = perguntas;
	}

	public ArrayList<String> getRespostas() {
		return respostas;
	}

	public void setRespostas(ArrayList<String> respostas) {
		this.respostas = respostas;
	}

	public String respostas() {
		String resposta = "";
		for (String resp : this.respostas) {
			resposta += resp + ";";
		}
		resposta += "\n";
		return resposta.replaceAll("\\r|\\n", ""); 
	}

	public String cabecalho() {
		String cabecalho = "";
		int idProjeto = 0;
	
		cabecalho : for (String perguntas : this.perguntas) {
			if ( idProjeto == 0 || idProjeto == this.idProjeto ) {
				cabecalho += perguntas + ";";
			} else
				break cabecalho;
			idProjeto = this.idProjeto;
		}
		cabecalho += "\n";
		return cabecalho;
	}

}