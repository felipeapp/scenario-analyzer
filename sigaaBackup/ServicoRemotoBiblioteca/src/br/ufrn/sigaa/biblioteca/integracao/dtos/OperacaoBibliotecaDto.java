/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/11/2008
 *
 */

package br.ufrn.sigaa.biblioteca.integracao.dtos;

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
    
    /** Necess�rio para renova��o, pois devem-se calcular os prazos novamente, caso a opera��o seja desfeita */
    public Integer valorVinculoUsuario;
    
    /** A data em que a opera��o foi feita, a penas para mostrar ao usu�rio.  */
    public Date dataRealizacao;
    
    /** O prazo para renovar, devolver (utilizado no empr�stimos e renova��o para mostrar ao usu�rio e enviar por email) */
    public Date prazo;
    
    /** O idMaterial do material sobre o qual a opera��o de circula��o foi feita.  */
    public int idMaterial;
    
    /**
     * Utilizado para mostrar as informa��es do material que foi emprestado, renovado ou devolvido nos comprovantes e mensagens enviadas.
     */
    public String infoMaterial;
    
    /** O c�digo de barras do material  */
    public String codigoBarras;
    
    
    /**
     * Construtor default, obrigat�rios nos DTOs.
     */
    public OperacaoBibliotecaDto() {
    	
	}

	/**
     *  Cria uma opera��o do sistema. Utilizado no <strong>empr�stimos</strong> e <strong>devolu��es</strong>
     * 
     * @param tipo
     * @param idMaterial
     * @param idMaterial
     * @param idEmprestimo
     */
    public OperacaoBibliotecaDto (int tipo, int idMaterial, int idEmprestimo, String codigoBarras, String infoMaterial, Date dataRealizacao, Date prazo){
        this.tipo  = tipo;
        this.idMaterial = idMaterial;
        this.idEmprestimo = idEmprestimo;
        this.codigoBarras = codigoBarras;
        this.dataRealizacao = dataRealizacao;
        this.prazo = prazo;
        this.infoMaterial = infoMaterial;
    }

    /**
     *  Construtor para as opera��es de <strong>renova��o</strong>.
     * 
     * @param tipo
     * @param idMaterial
     * @param idMaterial
     * @param idEmprestimo
     * @param codigoBarras
     */
    public OperacaoBibliotecaDto (int tipo, int idMaterial, int idEmprestimo, Integer valorVinculoUsuario, String codigoBarras, String infoMaterial, Date dataRealizacao, Date prazo){
        this.tipo = tipo;
        this.idMaterial = idMaterial;
        this.valorVinculoUsuario = valorVinculoUsuario;
        this.idEmprestimo = idEmprestimo;
        this.codigoBarras = codigoBarras;
        this.dataRealizacao = dataRealizacao;
        this.prazo = prazo;
        this.infoMaterial = infoMaterial;
    }
    
    /**
     * Usado nas p�gina JSF do lado web
     * @return
     */
    public String getDataRealizacaoFormatada() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(dataRealizacao);
    }
    
    /**
     * Usado nas p�gina JSF do lado web
     * @return
     */
    public String getPrazoFormatado() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(prazo);
    }
    
    
    /**
     * Usado nas p�gina JSF do lado web
     * @return
     */
    public String getInfoMaterial(){
    	return infoMaterial;
    }
    
}