package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fontes de pesquisa utilizadas no Levantamento Bibliografico
 * 
 * @author agostinho
 *
 */

public class FonteLevantBibliografico {

	public static final int LIVRO = 1;
	public static final int PERIODICO = 2;
	public static final int MULTIMEIOS = 3;
	public static final int OUTROS = 4;
	public static final int TESE_DISSERTACAO = 5;

	private int id;
	private String descricao;
	
	
	public FonteLevantBibliografico(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}
	
	public static Collection<FonteLevantBibliografico> getFontesDisponiveis() {
		List<FonteLevantBibliografico> listaPeriodos = new ArrayList<FonteLevantBibliografico>();
		
		listaPeriodos.add( new FonteLevantBibliografico(LIVRO, "Livro") );
		listaPeriodos.add( new FonteLevantBibliografico(TESE_DISSERTACAO, "Tese e/ou Dissertação") );
		listaPeriodos.add( new FonteLevantBibliografico(PERIODICO, "Periodico") );
		listaPeriodos.add( new FonteLevantBibliografico(MULTIMEIOS, "Multimeios") );
		listaPeriodos.add( new FonteLevantBibliografico(OUTROS, "Outros") );
		
		return listaPeriodos;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
