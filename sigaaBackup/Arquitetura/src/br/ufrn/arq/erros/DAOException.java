/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 13/09/2004
 */
package br.ufrn.arq.erros;

/**
 * Esta é a exceção base disparada por qualquer erro que ocorra na camada de persistência
 * 
 * @author Gleydson Lima
 *  
 */
public class DAOException extends ArqException {

    public DAOException(String msg) {
        super(msg);
        printStackTrace();
    }
    
    public DAOException(Exception e) {
        super(e);
    }
    
    public DAOException(String mensagem, Exception e) {
    	super(mensagem, e);
    }
    
    public DAOException(int codErro, String msg) {
        super(codErro, msg);
        printStackTrace();
    }
}