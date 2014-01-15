package br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo;

import java.io.Serializable;
import java.util.List;

/**
 * Contem as informacoes relativas ao resultado do processamento do registro de um lote de materiais no inventario do acervo.
 * 
 * <strong>OBSERVACAO 1 .:</strong>   A VM do coletor trabalha na versao 1.3 do Java.
 * <strong>OBSERVACAO 2 .:</strong>   Nao usar acentos nos comentarios porque essas classes sao copiadas para o 
 * netbens e dar erro de codificacao se usar.
 * 
 * @author Felipe
 *
 */
public class ParametrosRetornoRegistrarMateriaisDto implements Serializable {
	/**
	 * As mensagens associadas ao processamento de cada material.
	 */
	public String mensagens;
	/**
	 * Lista com os codigos de barras dos materiais registrados com sucesso.
	 *
	 */
	public List<String> itensConcluidos;
}
