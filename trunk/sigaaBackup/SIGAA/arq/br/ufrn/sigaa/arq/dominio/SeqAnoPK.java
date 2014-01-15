/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 29/12/2004
 *
 */
package br.ufrn.sigaa.arq.dominio;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * Classe de Chave Prim�ria Composta da Classe SeqAno
 * 
 * @author Gleydson Lima
 *  
 */
@Embeddable
public class SeqAnoPK implements Serializable {

	/** Tipo se sequ�ncia. */
    private SeqAno tipo;

    /** Ano da sequ�ncia. */
    private int ano;

    /** Construtor paramaterizado.
     * 
     * @param tipo
     * @param ano
     */
    public SeqAnoPK(SeqAno tipo, int ano) {
        this.tipo = tipo;
        this.ano = ano;
    }

    /** Construtor padr�o. */
    public SeqAnoPK() {
        
    }
    
    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public SeqAno getTipo() {
        return tipo;
    }

    public void setTipo(SeqAno tipo) {
        this.tipo = tipo;
    }

    /** Compara dois objetos SeqAnoPk, comparando o tipo e o ano de ambos.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof SeqAnoPK) {
            SeqAnoPK pk = (SeqAnoPK) obj;
            if (pk.getTipo() == tipo && pk.getAno() == ano) {
                return true;
            }
        }
        return false;

    }

    /** Retorna o c�dio hash deste objeto
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return super.hashCode();
    }
}