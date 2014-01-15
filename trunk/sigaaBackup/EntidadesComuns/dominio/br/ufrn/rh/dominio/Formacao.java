package br.ufrn.rh.dominio;

import br.ufrn.arq.dominio.GenericTipo;

/**
 * Formação representa se o servidor tem doutorado, mestrado, etc...
 * É redundante com Escolaridade, pois ela está armazenando nível médio, graduado, etc..
 *
 * Porém, como vem do RHNet, temos que manter a estrutura.
 *
 * @author Gleydson
 *
 */
public class Formacao extends GenericTipo {

	public static final int GRADUADO 		= 4;
	public static final int MESTRE 			= 26;
	public static final int DOUTOR 			= 27;
	public static final int ESPECIALISTA 	= 25;
	public static final int FORMACAO_PADRAO = 29;

	private int ordenacaoTitulacao;

	public Formacao() {
		super();
	}

	public Formacao(int id) {
		super(id);
	}

	public String getTipoDescricaoTitulo() {
		if (getId() == Formacao.DOUTOR)
			return "DOUTOR";
		else if (getId() == Formacao.MESTRE)
			return "MESTRE";
		else if (getId() == Formacao.ESPECIALISTA)
			return "ESPECIALISTA";
		return "";
	}

	public int getOrdenacaoTitulacao() {
		return ordenacaoTitulacao;
	}

	public void setOrdenacaoTitulacao(int ordenacaoTitulacao) {
		this.ordenacaoTitulacao = ordenacaoTitulacao;
	}

}
