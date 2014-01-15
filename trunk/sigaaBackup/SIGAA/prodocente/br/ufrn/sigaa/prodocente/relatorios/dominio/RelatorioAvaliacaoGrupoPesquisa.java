/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/04/2009'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Classe auxiliar utilizada para montar a view do relatório
 * para avaliação de grupos de pesquisa.
 * 
 * @author Leonardo Campos
 *
 */
public class RelatorioAvaliacaoGrupoPesquisa {
	
	public static final int ART_COMP_PER_IND_NAC = 1;
	public static final int ART_COMP_PER_IND_INT = 2;
	
	public static final int AN_EVT_RES_LOC = 3;
	public static final int AN_EVT_RES_REG = 4;
	public static final int AN_EVT_RES_NAC = 5;
	public static final int AN_EVT_RES_INT = 6;
	
	public static final int AN_EVT_RES_EX_LOC = 7;
	public static final int AN_EVT_RES_EX_REG = 8;
	public static final int AN_EVT_RES_EX_NAC = 9;
	public static final int AN_EVT_RES_EX_INT = 10;
	
	public static final int AN_EVT_RES_TC_LOC = 11;
	public static final int AN_EVT_RES_TC_REG = 12;
	public static final int AN_EVT_RES_TC_NAC = 13;
	public static final int AN_EVT_RES_TC_INT = 14;
	
	public static final int LIVRO_ISBN_NAC = 15;
	public static final int LIVRO_ISBN_INT = 16;
	
	public static final int CAP_LIVRO_ISBN_NAC = 17;
	public static final int CAP_LIVRO_ISBN_INT = 18;
	
	public static Collection<Integer> getItensRelatorio(){
		ArrayList<Integer> itens = new ArrayList<Integer>();
		itens.add(ART_COMP_PER_IND_NAC);
		itens.add(ART_COMP_PER_IND_INT);
		itens.add(AN_EVT_RES_LOC);
		itens.add(AN_EVT_RES_REG);
		itens.add(AN_EVT_RES_NAC);
		itens.add(AN_EVT_RES_INT);
		itens.add(AN_EVT_RES_EX_LOC);
		itens.add(AN_EVT_RES_EX_REG);
		itens.add(AN_EVT_RES_EX_NAC);
		itens.add(AN_EVT_RES_EX_INT);
		itens.add(AN_EVT_RES_TC_LOC);
		itens.add(AN_EVT_RES_TC_REG);
		itens.add(AN_EVT_RES_TC_NAC);
		itens.add(AN_EVT_RES_TC_INT);
		itens.add(LIVRO_ISBN_NAC);
		itens.add(LIVRO_ISBN_INT);
		itens.add(CAP_LIVRO_ISBN_NAC);
		itens.add(CAP_LIVRO_ISBN_INT);
		return itens;
	}
	
	public static int getTeto(int itemRelatorio){
		switch(itemRelatorio){
			case AN_EVT_RES_LOC:
			case AN_EVT_RES_REG: 
				return 2;
			case AN_EVT_RES_NAC:
			case AN_EVT_RES_INT:
			case AN_EVT_RES_EX_LOC:
			case AN_EVT_RES_EX_REG:
				return 3;
			case AN_EVT_RES_EX_NAC:
			case AN_EVT_RES_EX_INT:
				return 4;
			case AN_EVT_RES_TC_LOC:
			case AN_EVT_RES_TC_REG:
				return 5;
			default:
				return Integer.MAX_VALUE;
		}
	}
	
	private Map<Integer, LinhaRelatorioGrupoPesquisa> resultado;
	
	private Set<String> docentes;

	public RelatorioAvaliacaoGrupoPesquisa() {
		this.resultado = new TreeMap<Integer, LinhaRelatorioGrupoPesquisa>();
		this.docentes = new TreeSet<String>();
	}
	
	public Map<Integer, LinhaRelatorioGrupoPesquisa> getResultado() {
		return resultado;
	}

	public void setResultado(Map<Integer, LinhaRelatorioGrupoPesquisa> resultado) {
		this.resultado = resultado;
	}

	public Set<String> getDocentes() {
		return docentes;
	}

	public void setDocentes(Set<String> docentes) {
		this.docentes = docentes;
	}
	
	
}
