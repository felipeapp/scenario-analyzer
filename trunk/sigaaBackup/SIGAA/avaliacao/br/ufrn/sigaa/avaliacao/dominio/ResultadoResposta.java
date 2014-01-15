/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 21/08/2008 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Classe que armazena a quantidade de respostas dadas à uma determinada pergunta na Avaliação Institucional.
 * @author David Pereira
 *
 */
public class ResultadoResposta implements Comparable<ResultadoResposta> {

	/** ID do {@link GrupoPerguntas grupo de perguntas} correspondente à este resultado. */
	private int idGrupo;
	
	/** ID da {@link Pergunta pergunta} correspondente à este resultado. */
	private int idPergunta;
	
	/** Lista de respostas dadas à pergunta. */
	private List<Object> resposta;

	/** Indica se este resultado é referente ao processamento de perguntas com múltipla escolha. */
	private boolean processamentoMultiplaEscolha;
	
	/** Construtor padrão. */
	public ResultadoResposta() {
	}
	
	/** Construtor parametrizado.
	 * @param idGrupo
	 * @param idPergunta
	 */
	public ResultadoResposta(Integer idGrupo, Integer idPergunta) {
		this.idGrupo = idGrupo;
		this.idPergunta = idPergunta;
	}
	
	/** Adiciona uma resposta à lista de respostas.
	 * @param resposta
	 */
	public void addResposta(Object resposta) {
		if (this.resposta == null)
			this.resposta = new ArrayList<Object>();
		this.resposta.add(resposta);
	}

	/** Retorna o ID do  grupo de perguntas correspondente à este resultado. 
	 * @return
	 */
	public int getIdGrupo() {
		return idGrupo;
	}

	/** Seta o ID do  grupo de perguntas correspondente à este resultado. 
	 * @param idGrupo
	 */
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}

	/** Retorna o ID da  pergunta correspondente à este resultado. 
	 * @return
	 */
	public int getIdPergunta() {
		return idPergunta;
	}

	/** Seta o ID da  pergunta correspondente à este resultado. 
	 * @param idPergunta
	 */
	public void setIdPergunta(int idPergunta) {
		this.idPergunta = idPergunta;
	}
	
	/** Retorna a lista de respostas dadas à pergunta. 
	 * @return
	 */
	public List<Object> getResposta() {
		return resposta;
	}

	/** Seta a lista de respostas dadas à pergunta.
	 * @param resposta
	 */
	public void setResposta(List<Object> resposta) {
		this.resposta = resposta;
	}

	/** Compara este objeto ao passado por parâmetro, comparando o ID do grupo e o ID da pergunta.. 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ResultadoResposta o) {
		int comp1 = Integer.valueOf(idGrupo).compareTo(o.idGrupo);
		
		if (comp1 == 0) {
			return Integer.valueOf(idPergunta).compareTo(o.idPergunta);
		}
		
		return comp1;
	}
	
	/** Retorna uma representação textual deste objeto no formato de uma lista de respostas separado por ';'  
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Object> it = resposta.iterator(); it.hasNext(); ) {
			Object r = it.next();
			sb.append(r);
			if (it.hasNext())
				sb.append(";");
		}
		return sb.toString();
	}

	/** Retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idGrupo;
		result = prime * result + idPergunta;
		return result;
	}

	/** Indica se este objeto é igual ao passado no parâmetro, comparando o ID do grupo de perguntas, e o ID da pergunta. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultadoResposta other = (ResultadoResposta) obj;
		if (idGrupo != other.idGrupo)
			return false;
		if (idPergunta != other.idPergunta)
			return false;
		return true;
	}

	/** Indica se este resultado é referente ao processamento de perguntas com múltipla escolha. 
	 * @return
	 */
	public boolean isProcessamentoMultiplaEscolha() {
		return processamentoMultiplaEscolha;
	}

	/** Seta se este resultado é referente ao processamento de perguntas com múltipla escolha. 
	 * @param processamentoMultiplaEscolha
	 */
	public void setProcessamentoMultiplaEscolha(boolean processamentoMultiplaEscolha) {
		this.processamentoMultiplaEscolha = processamentoMultiplaEscolha;
	}
	
}
