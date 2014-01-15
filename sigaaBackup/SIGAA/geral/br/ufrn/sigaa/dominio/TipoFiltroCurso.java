/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/01/2008'
 *
 */
package br.ufrn.sigaa.dominio;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

/**
 * Classe utilizada em alguns relatórios para filtrar 
 * cursos de acordo com certas características
 * 
 * @author Ricardo Wendell
 *
 */
public class TipoFiltroCurso {

	public static final int CURSO_SELECIONADO = 1;

	public static final int PRESENCIAIS = 2;
	
	public static final int A_DISTANCIA = 3;
	
	public static final int PROBASICA = 4;
	
	public static Collection<SelectItem> getAllCombo() {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(CURSO_SELECIONADO, "SOMENTE O CURSO SELECIONADO"));
		itens.add(new SelectItem(PRESENCIAIS, "TODOS OS CURSOS PRESENCIAIS"));
		itens.add(new SelectItem(A_DISTANCIA, "TODOS OS CURSOS A DISTÂNCIA"));
		itens.add(new SelectItem(PROBASICA, "TODOS OS CURSOS PROBASICA"));
		return itens;
	}
	
	public static String getDescricaoFiltro(int filtro) {
		switch(filtro) {
			case PRESENCIAIS: return "TODOS OS CURSOS PRESENCIAIS";
			case A_DISTANCIA: return "TODOS OS CURSOS A DISTÂNCIA";
			case PROBASICA: return "TODOS OS CURSOS PROBASICA";
			default: return "";
		}
	}
}
