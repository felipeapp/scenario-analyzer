/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/05/2007
 *
 */
package br.ufrn.comum.dominio;

import java.util.HashMap;

/**
 * Classe que armazena em forma de constantes as categorias que uma unidade pode assumir
 * 
 * @author Raphaela Galhardo
 * @author Gleydson Lima
 *
 */
public class CategoriaUnidade {

	public static final int UNIDADE = 1; // Administração Central, Centros,

	public static final int SUB_UNIDADE = 2; // Departamentos, Pró-Reitorias

	public static final int SETOR = 3; // Setor de Execução Orçamentária, Contabilidade, etc

	public static final int CONVENIO = 4; // Convênios

	@Deprecated
	public static final int PESQUISA = 5; // Unidade de Pesquisa, vinculada a um professor
	@Deprecated
	public static final int EXTENSAO = 6; // Unidade de Extensão, vinculada a um professor
	
	/** Refere-se a uma unidade de pesquisa */
	public static final int ORCAMENTO_INTERNO = 7; 
	
	/** Refere-se a uma unidade de extensão */
	public static final int ORCAMENTO_EXTERNO_DESCENTRALIZADO = 8; 
	
	private static final HashMap<Integer, String> categorias = new HashMap<Integer, String>();
	
	static{
		
		categorias.put(UNIDADE, "UNIDADE");
		categorias.put(SUB_UNIDADE, "SUB UNIDADE");
		categorias.put(SETOR, "SETOR");
		categorias.put(CONVENIO, "CONVÊNIO");
		categorias.put(PESQUISA, "PESQUISA");
		categorias.put(EXTENSAO, "EXTENSÃO");
		categorias.put(ORCAMENTO_INTERNO, "ORÇAMENTO INTERNO");
		categorias.put(ORCAMENTO_EXTERNO_DESCENTRALIZADO, "ORÇAMENTO EXTERNO/DESCENTRALIZADO");
	}
	
	/**
	 * Retorna o nome da categoria pelo identificador passado
	 * @param categoria
	 * @return
	 */
	public static String getDescricao(Integer categoria){
		
		return categorias.get(categoria);
	}
	
	
}

