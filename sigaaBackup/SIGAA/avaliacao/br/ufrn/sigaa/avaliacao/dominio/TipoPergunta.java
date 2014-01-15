/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/11/2011
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

/**
 * Enumera��o dos tipos de pergunta utilizados na Avalia��o Institucional: Nota,
 * Sim/N�o, �nica Escolha ou M�ltipla Escolha.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
public enum TipoPergunta {

	// N�O ALTERAR ESTA ORDEM
	/** Tipo de pergunta indefinido. */ 
	INDEFINIDO,
	/** Define o tipo NOTA de pergunta */
	PERGUNTA_NOTA,
	/** Define o tipo SIM/N�O de pergunta */
	PERGUNTA_SIM_NAO,
	/** Define o tipo MULTIPLA ESCOLHA de pergunta */
	PERGUNTA_MULTIPLA_ESCOLHA,
	/** Define o tipo ESCOLHA �NICA de pergunta */
	PERGUNTA_UNICA_ESCOLHA;
	
	/** Retorna uma descri��o textual do tipo de pergunta.
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case INDEFINIDO: return "INDEFINIDO";
		case PERGUNTA_NOTA: return "NOTA";
		case PERGUNTA_SIM_NAO: return "SIM/N�O";
		case PERGUNTA_MULTIPLA_ESCOLHA: return "M�LTIPLA ESCOLHA";
		case PERGUNTA_UNICA_ESCOLHA: return "�NICA ESCOLHA";
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
		throw new IllegalArgumentException("Tipo de pergunta n�o definido.");
				
	}
}
