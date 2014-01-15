package br.ufrn.academico.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Tipos de Unidades Acadêmicas, devem ser conhecidas tanto no SIPAC ocmo no
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
	
	public static final int COORDENACAO_CURSO_LATO = 8; // Para atender ao caso das coordenações de cursos de especialização.

	public static final int PROGRAMA_RESIDENCIA = 9;
	
	public static String getDescricao(int tipo) {

		switch (tipo) {
		case DEPARTAMENTO:
			return "Departamento";
		case ESCOLA:
			return "Escola";
		case PROGRAMA_POS:
			return "Programa de Pós-Graduação";
		case CENTRO:
			return "Centro Acadêmico";
		case UNID_ACADEMICA_ESPECIALIZADA:
			return "Unidade Acadêmica Especializada";
		case COORDENACAO_CURSO:
		case COORDENACAO_CURSO_LATO:
			return "Coordenação de Curso";
		case ORGAO_SUPLEMENTAR:
			return "Órgão Suplementar";
		case PROGRAMA_RESIDENCIA:
			return "Programa de Residência";
		}
		return "Desconhecido";

	}

	public static List<SelectItem> tipos = new ArrayList<SelectItem>();

	static {

		tipos.add(new SelectItem(CENTRO,"Centro Acadêmico"));
		tipos.add(new SelectItem(DEPARTAMENTO,"Departamento"));
		tipos.add(new SelectItem(ESCOLA,"Escola"));
		tipos.add(new SelectItem(PROGRAMA_POS,"Programa de Pós-Graduação"));
		tipos.add(new SelectItem(UNID_ACADEMICA_ESPECIALIZADA,"Unidade Acadêmica Especializada"));
		tipos.add(new SelectItem(COORDENACAO_CURSO,"Coordenação de Curso"));
		tipos.add(new SelectItem(ORGAO_SUPLEMENTAR, getDescricao(ORGAO_SUPLEMENTAR)));
		tipos.add(new SelectItem(PROGRAMA_RESIDENCIA, getDescricao(PROGRAMA_RESIDENCIA)));

	}

}
