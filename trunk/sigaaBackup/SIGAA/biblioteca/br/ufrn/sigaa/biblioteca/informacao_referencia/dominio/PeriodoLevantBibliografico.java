/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 30/10/2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe auxiliar para verifica��o de Per�odos dispon�veis utilizados pelo Levantamento Bibliogr�fico.
 * 
 * @author agostinho
 *
 */

public class PeriodoLevantBibliografico {
	/** Identificador do per�odo de "Ano em Curso" */
	public static final int ANO_EM_CURSO = 1;
	/** Identificador do per�odo "Indiferente"*/
	public static final int INDIFERENTE = 2;
	/** Identificador do per�odo Outros */
	public static final int OUTROS = 3;
	/** Id do per�odo */
	private int id;
	/** Descri��o do Per�odo */
	private String descricao;

	/**
	 * Retorna todos os per�odos dispon�veis.
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
	 * Construtor da classe, passando o id e descri��o do per�odo
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
