/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/01 - 11:56:16
 */
package br.ufrn.sigaa.arq.expressao;

/**
 * Nó da árvore de verificação de expressões 
 * 
 * @author David Pereira
 *
 */
public class NoArvore {
	
	private NoArvore esquerda;
	private NoArvore direita;
	
	private boolean visitado;
	private String label;
	
	public NoArvore(String label) {
		this.label = label;
	}

	public NoArvore(String label, NoArvore esquerda, NoArvore direita) {
		this.label = label;
		this.direita = direita;
		this.esquerda = esquerda;
	}
	
	/**
	 * @return the esquerda
	 */
	public NoArvore getEsquerda() {
		return esquerda;
	}

	/**
	 * @param esquerda the esquerda to set
	 */
	public void setEsquerda(NoArvore esquerda) {
		this.esquerda = esquerda;
	}

	/**
	 * @return the direita
	 */
	public NoArvore getDireita() {
		return direita;
	}

	/**
	 * @param direita the direita to set
	 */
	public void setDireita(NoArvore direita) {
		this.direita = direita;
	}

	/**
	 * Marca um nó e seus filhos como não-visitados
	 */
	public void naoVisitado() {
		naoVisitado(this);
	}
	
	private void naoVisitado(NoArvore no) {
		no.visitado = false;
		if (no.esquerda != null)
			naoVisitado(no.esquerda);
		if (no.direita != null)
			naoVisitado(no.direita);
	}
	 
	/**
	 * Marca um nó como visitado
	 */
	public void visitado() {
		this.visitado = true;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	/**
	 * Identifica se o nó foi visitado pelo verificador de expressões
	 */
	public boolean isVisitado() {
		return visitado;
	}
	
	/**
	 * Identifica se um nó é folha ou não
	 */
	public boolean isFolha() {
		return this.esquerda == null && this.direita == null;
	}

	public void adicionaFilho(NoArvore filho) {
		if (this.direita == null) {
			this.direita = filho;
		} else {
			this.esquerda = filho;
		}
	}
	
}

