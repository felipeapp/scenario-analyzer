/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 30/10/2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe auxiliar para verificação de Períodos disponíveis utilizados pelo Levantamento Bibliográfico.
 * 
 * @author agostinho
 *
 */

public class PeriodoLevantBibliografico {
	/** Identificador do período de "Ano em Curso" */
	public static final int ANO_EM_CURSO = 1;
	/** Identificador do período "Indiferente"*/
	public static final int INDIFERENTE = 2;
	/** Identificador do período Outros */
	public static final int OUTROS = 3;
	/** Id do período */
	private int id;
	/** Descrição do Período */
	private String descricao;

	/**
	 * Retorna todos os períodos disponíveis.
	 * @return
	 */
	public static List<PeriodoLevantBibliografico> getPeriodosDisponiveis() {

		List<PeriodoLevantBibliografico> listaPeriodos = new ArrayList<PeriodoLevantBibliografico>();

		listaPeriodos.add( new PeriodoLevantBibliografico(ANO_EM_CURSO, "Ano em Curso") );
		listaPeriodos.add( new PeriodoLevantBibliografico(INDIFERENTE, "Indiferente") );
		listaPeriodos.add( new PeriodoLevantBibliografico(OUTROS, "Outros") );

		return listaPeriodos; 
	}

	/**
	 * Construtor da classe, passando o id e descrição do período
	 * @param id
	 * @param descricao
	 */
	public PeriodoLevantBibliografico(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
