/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo;

import java.io.Serializable;
import java.util.List;

/**
 * Contem as informacoes relativas a requisicoo do registro de um lote de materiais no inventario do acervo.
 *
 * <strong>OBSERVACAO 1 .:</strong>   A VM do coletor trabalha na versao 1.3 do Java.
 * <strong>OBSERVACAO 2 .:</strong>   Nao usar acentos nos comentarios porque essas classes sao copiadas para o 
 * netbens e dar erro de codificacao se usar.
 *
 * @author Felipe
 */
public class ParametrosRegistrarMateriaisColetorDto implements Serializable {
    
	/**
	 * ID do inventario.
	 */
    public int idInventario;
    /**
     * Lista de materiais. 
     */
	public List<String> codigosBarras;
    
    /**
     * ID do operador.
     */
    public int idOperador;
    
}
