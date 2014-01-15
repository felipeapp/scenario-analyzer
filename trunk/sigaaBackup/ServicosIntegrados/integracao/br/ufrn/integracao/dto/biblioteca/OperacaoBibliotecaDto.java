/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/11/2008
 *
 */

package br.ufrn.integracao.dto.biblioteca;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe que representa uma operação realizada pelo sistema desktop.
 * 
 * O usuário possuirá uma lista das 10 últimas operações e podendo cancelar a última operação, 
 * caso tenha sido realizada por engano. 
 * 
 * @author jadson
 * @since 26/11/2008
 * @version 1.0 criacao da classe
 *
 */
@SuppressWarnings("serial")
public class OperacaoBibliotecaDto implements Serializable {

	private static final long serialVersionUID = 1L;
 
	// As tres operações possíveis de serem usadas na parte de circulação //
    public static final int OPERACAO_EMPRESTIMO = 0;
    public static final int OPERACAO_RENOVACAO = 1;
    public static final int OPERACAO_DEVOLUCAO = 2;
  
    
    /** O id do empréstimo */
    public int idEmprestimo;
    
    /** 
     * O tipo de operação realizado sobre o empréstimo.
     * 
     * @see {@code OPERACAO_EMPRESTIMO }   
     * @see {@code OPERACAO_RENOVACAO }   
     * @see {@code OPERACAO_DEVOLUCAO }   
     *  
     */
    public int tipo;
    
    /** Necessário para renovação, pois devem-se calcular os prazos novamente. */
    public int tipoUsuario = -1;
    
    /** A data em que a operação foi feita, a penas para mostrar ao usuário.  */
    public Date data;
    
    /** O idMaterial do material sobre o qual a operação de circulação foi feita.  */
    public int idMaterial;
    
    /** O código de barras do material  */
    public String codigoBarras;
    
    public OperacaoBibliotecaDto() {
    }
    
    /**
     *  Cria uma operação do sistema
     * 
     * @param tipo
     * @param idMaterial
     * @param idMaterial
     * @param idEmprestimo
     */
    public OperacaoBibliotecaDto (int tipo, int idMaterial, int idEmprestimo, String codigoBarras){
        this.tipo  = tipo;
        this.idMaterial = idMaterial;
        this.idEmprestimo = idEmprestimo;
        this.codigoBarras = codigoBarras;
        this.data = new Date();
    }

    /**
     *  Construtor para as operações de renovação.
     * 
     * @param tipo
     * @param idMaterial
     * @param idMaterial
     * @param idEmprestimo
     * @param codigoBarras
     */
    public OperacaoBibliotecaDto (int tipo, int idMaterial, int idEmprestimo, int tipoUsuario, String codigoBarras){
        this.tipo = tipo;
        this.idMaterial = idMaterial;
        this.tipoUsuario = tipoUsuario;
        this.idEmprestimo = idEmprestimo;
        this.codigoBarras = codigoBarras;
        this.data = new Date();
    }
    
    public String getDataFormatada() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(data);
    }
}