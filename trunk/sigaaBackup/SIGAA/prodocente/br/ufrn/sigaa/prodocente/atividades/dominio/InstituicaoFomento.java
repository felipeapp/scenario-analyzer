/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que armazena as instituições de fomento
 *
 * @author eric
 */
@Entity
@Table(name = "instituicao_fomento",schema="prodocente")
public class InstituicaoFomento implements Validatable {

	public static final int CNPQ = 2;
	public static final int UFRN = 1;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_instituicao_fomento", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;


    @Column(name = "nome")
    private String nome;

    /** Creates a new instance of InstituicaoFomento */
    public InstituicaoFomento() {
    }

    /**
     * Creates a new instance of InstituicaoFomento with the specified values.
     * @param id the id of the InstituicaoFomento
     */
    public InstituicaoFomento(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this InstituicaoFomento.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this InstituicaoFomento to the specified value.
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo() {	return this.ativo; }

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)	{ this.ativo = ativo; }


    /**
     * Gets the nome of this InstituicaoFomento.
     * @return the nome
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Sets the nome of this InstituicaoFomento to the specified value.
     * @param the new nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Determines whether another object is equal to this InstituicaoFomento.  The result is
     * <code>true</code> if and only if the argument is not null and is a InstituicaoFomento object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InstituicaoFomento)) {
            return false;
        }
        InstituicaoFomento other = (InstituicaoFomento)object;
        if (this.id != other.id || (this.id == 0)) return false;
        return true;
    }
    
    /** Calcula o código hash utilizando o ID.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.InstituicaoFomento[id=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

}
