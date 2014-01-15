/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
    
    /** Necessário para renovação, pois devem-se calcular os prazos novamente, caso a operação seja desfeita */
    public Integer valorVinculoUsuario;
    
    /** A data em que a operação foi feita, a penas para mostrar ao usuário.  */
    public Date dataRealizacao;
    
    /** O prazo para renovar, devolver (utilizado no empréstimos e renovação para mostrar ao usuário e enviar por email) */
    public Date prazo;
    
    /** O idMaterial do material sobre o qual a operação de circulação foi feita.  */
    public int idMaterial;
    
    /**
     * Utilizado para mostrar as informações do material que foi emprestado, renovado ou devolvido nos comprovantes e mensagens enviadas.
     */
    public String infoMaterial;
    
    /** O código de barras do material  */
    public String codigoBarras;
    
    
    /**
     * Construtor default, obrigatórios nos DTOs.
     */
    public OperacaoBibliotecaDto() {
    	
	}

	/**
     *  Cria uma operação do sistema. Utilizado no <strong>empréstimos</strong> e <strong>devoluções</strong>
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
     *  Construtor para as operações de <strong>renovação</strong>.
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
     * Usado nas página JSF do lado web
     * @return
     */
    public String getDataRealizacaoFormatada() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(dataRealizacao);
    }
    
    /**
     * Usado nas página JSF do lado web
     * @return
     */
    public String getPrazoFormatado() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(prazo);
    }
    
    
    /**
     * Usado nas página JSF do lado web
     * @return
     */
    public String getInfoMaterial(){
    	return infoMaterial;
    }
    
}