/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 30/11/2004
 */
package br.ufrn.arq.dominio;


/**
 * Classe utilizada para mapear tipos que são formados por id e denominacao
 * 
 * @author Gleydson Lima
 *  
 */
public class GenericTipo extends AbstractMovimento implements PersistDB {

    public static final int NAO_INFORMADO = -1;

	/** Identificador da tabela */
    protected int id;

    /** Denominação do elemento */
    protected String denominacao;

    /**
     * Construtor padrão.
     */
    public GenericTipo() {
        
    }
    
    public GenericTipo(int id) {
        this.id = id;
    }
    
    /**
     * Construtor
     * @param id
     * @param denominacao
     */
    public GenericTipo(int id, String denominacao) {
        this.id = id;
        this.denominacao = denominacao;
    }
    
    /**
     * @return Retorna denominacao.
     */
    public String getDenominacao() {
        return denominacao;
    }

    /**
     * @param denominacao
     *            The denominacao to set.
     */
    public void setDenominacao(String denominacao) {
        this.denominacao = denominacao;
    }

    /**
     * @return Retorna id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GenericTipo other = (GenericTipo) obj;
		if (id != other.id)
			return false;
		return true;
	}
}