/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/11/2011
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

/**
 * Enumeração dos tipos de pergunta utilizados na Avaliação Institucional: Nota,
 * Sim/Não, Única Escolha ou Múltipla Escolha.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public enum TipoPergunta {

	// NÃO ALTERAR ESTA ORDEM
	/** Tipo de pergunta indefinido. */ 
	INDEFINIDO,
	/** Define o tipo NOTA de pergunta */
	PERGUNTA_NOTA,
	/** Define o tipo SIM/NÃO de pergunta */
	PERGUNTA_SIM_NAO,
	/** Define o tipo MULTIPLA ESCOLHA de pergunta */
	PERGUNTA_MULTIPLA_ESCOLHA,
	/** Define o tipo ESCOLHA ÚNICA de pergunta */
	PERGUNTA_UNICA_ESCOLHA;
	
	/** Retorna uma descrição textual do tipo de pergunta.
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case INDEFINIDO: return "INDEFINIDO";
		case PERGUNTA_NOTA: return "NOTA";
		case PERGUNTA_SIM_NAO: return "SIM/NÃO";
		case PERGUNTA_MULTIPLA_ESCOLHA: return "MÚLTIPLA ESCOLHA";
		case PERGUNTA_UNICA_ESCOLHA: return "ÚNICA ESCOLHA";
		default:
			return null;
		}
	}
	
	public boolean isUnicaEscolha() {
		return this == PERGUNTA_UNICA_ESCOLHA;
	}
	
	public boolean isMultiplaEscolha() {
		return this == PERGUNTA_MULTIPLA_ESCOLHA;
	}
	
	public boolean isNota() {
		return this == PERGUNTA_NOTA;
	}
	
	public boolean isSimNao() {
		return this == PERGUNTA_SIM_NAO;
	}
	
	public String getDescricao() {
		return toString();
	}
	
	/** Retorna o tipo de pergunta para a ordem expecificada.
	 * @param ordem
	 * @return
	 */
	public static TipoPergunta valueOf(int ordem) {
		for (TipoPergunta tipo : values()) {
			if (tipo.ordinal() == ordem) {
				return tipo;
			}
		}
		throw new IllegalArgumentException("Tipo de pergunta não definido.");
				
	}
}
