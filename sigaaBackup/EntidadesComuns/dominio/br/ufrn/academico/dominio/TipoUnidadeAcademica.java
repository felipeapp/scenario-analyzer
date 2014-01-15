package br.ufrn.academico.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Tipos de Unidades Acad�micas, devem ser conhecidas tanto no SIPAC ocmo no
 * SIGAA.
 *
 * @author Gleydson
 *
 */
public class TipoUnidadeAcademica {

	public static final int DEPARTAMENTO = 1;

	public static final int ESCOLA = 2;

	public static final int PROGRAMA_POS = 3;

	public static final int CENTRO = 4;

	public static final int UNID_ACADEMICA_ESPECIALIZADA = 5;

	public static final int COORDENACAO_CURSO = 6;

	public static final int ORGAO_SUPLEMENTAR = 7; // Para atender ao caso do Museu.
	// O museu possui componentes (MCC).
	
	public static final int COORDENACAO_CURSO_LATO = 8; // Para atender ao caso das coordena��es de cursos de especializa��o.

	public static final int PROGRAMA_RESIDENCIA = 9;
	
	public static String getDescricao(int tipo) {

		switch (tipo) {
		case DEPARTAMENTO:
			return "Departamento";
		case ESCOLA:
			return "Escola";
		case PROGRAMA_POS:
			return "Programa de P�s-Gradua��o";
		case CENTRO:
			return "Centro Acad�mico";
		case UNID_ACADEMICA_ESPECIALIZADA:
			return "Unidade Acad�mica Especializada";
		case COORDENACAO_CURSO:
		case COORDENACAO_CURSO_LATO:
			return "Coordena��o de Curso";
		case ORGAO_SUPLEMENTAR:
			return "�rg�o Suplementar";
		case PROGRAMA_RESIDENCIA:
			return "Programa de Resid�ncia";
		}
		return "Desconhecido";

	}

	public static List<SelectItem> tipos = new ArrayList<SelectItem>();

	static {

		tipos.add(new SelectItem(CENTRO,"Centro Acad�mico"));
		tipos.add(new SelectItem(DEPARTAMENTO,"Departamento"));
		tipos.add(new SelectItem(ESCOLA,"Escola"));
		tipos.add(new SelectItem(PROGRAMA_POS,"Programa de P�s-Gradua��o"));
		tipos.add(new SelectItem(UNID_ACADEMICA_ESPECIALIZADA,"Unidade Acad�mica Especializada"));
		tipos.add(new SelectItem(COORDENACAO_CURSO,"Coordena��o de Curso"));
		tipos.add(new SelectItem(ORGAO_SUPLEMENTAR, getDescricao(ORGAO_SUPLEMENTAR)));
		tipos.add(new SelectItem(PROGRAMA_RESIDENCIA, getDescricao(PROGRAMA_RESIDENCIA)));

	}

}
