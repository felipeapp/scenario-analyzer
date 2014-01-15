package br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo;

import java.io.Serializable;

/**
 * DTO com as informacoes do Material necessarias para o coletor.
 * 
 * <strong>OBSERVACAO 1 .:</strong>   A VM do coletor trabalha na versao 1.3 do Java.
 * <strong>OBSERVACAO 2 .:</strong>   Nao usar acentos nos comentarios porque essas classes sao copiadas para o 
 * netbens e dar erro de codificacao se usar.
 * 
 * @author Felipe
 *
 */
public class MaterialDto implements Serializable {

	/**
	 * ID do material.
	 */
	public int id;
	/**
	 * Codigo de barras do material.
	 */
	public String codigoBarras;
	/**
	 * Titulo do material.
	 */
	public String titulo;

}
