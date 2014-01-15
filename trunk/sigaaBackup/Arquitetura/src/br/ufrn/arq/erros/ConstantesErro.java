/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/11/2004
 */
package br.ufrn.arq.erros;


/**
 * Classe de Constantes de Erro para controlar os vários tipos de erros
 * disparados pela mesma exceção.
 *
 * @author Gleydson Lima
 *
 */
public class ConstantesErro {

    /** Duplicação de chamada para o EJB * */
    public static final int SOLICITACAO_JA_PROCESSADA = 1;

    /** Remoção de entidade com restrição de chave extrangeira */
    public static final int VIOLACAO_CHAVE_EXTRANGEIRA = 2;

    /** Limite de resultados ultrapassados numa busca em um dao */
    public static final int LIMITE_RESULTADOS_DAO = 3;

}