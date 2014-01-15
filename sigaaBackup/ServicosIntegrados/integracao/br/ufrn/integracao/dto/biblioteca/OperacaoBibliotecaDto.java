/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe que representa uma opera��o realizada pelo sistema desktop.
 * 
 * O usu�rio possuir� uma lista das 10 �ltimas opera��es e podendo cancelar a �ltima opera��o, 
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
 
	// As tres opera��es poss�veis de serem usadas na parte de circula��o //
    public static final int OPERACAO_EMPRESTIMO = 0;
    public static final int OPERACAO_RENOVACAO = 1;
    public static final int OPERACAO_DEVOLUCAO = 2;
  
    
    /** O id do empr�stimo */
    public int idEmprestimo;
    
    /** 
     * O tipo de opera��o realizado sobre o empr�stimo.
     * 
     * @see {@code OPERACAO_EMPRESTIMO }   
     * @see {@code OPERACAO_RENOVACAO }   
     * @see {@code OPERACAO_DEVOLUCAO }   
     *  
     */
    public int tipo;
    
    /** Necess�rio para renova��o, pois devem-se calcular os prazos novamente. */
    public int tipoUsuario = -1;
    
    /** A data em que a opera��o foi feita, a penas para mostrar ao usu�rio.  */
    public Date data;
    
    /** O idMaterial do material sobre o qual a opera��o de circula��o foi feita.  */
    public int idMaterial;
    
    /** O c�digo de barras do material  */
    public String codigoBarras;
    
    public OperacaoBibliotecaDto() {
    }
    
    /**
     *  Cria uma opera��o do sistema
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
     *  Construtor para as opera��es de renova��o.
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