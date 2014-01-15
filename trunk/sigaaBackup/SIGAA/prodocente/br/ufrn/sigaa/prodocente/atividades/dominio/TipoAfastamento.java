/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Tipos de afastamento tempor�rios que um servidor pode realizar
 *
 * @author eric
 */
@Entity
@Table(name = "afastamento",schema="prodocente")
public class TipoAfastamento implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_afastamento", nullable = false)
    private int id;

    
	/**
	 * Ao remover as produ��es e atividades, as mesmas n�o ser�o removidas da base de dados,
	 * apenas o campo ativo ser� marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

	
    
    @Column(name = "descricao")
    private String descricao;


    /** Creates a new instance of Afastamento */
    public TipoAfastamento() {
    }

    /**
     * Creates a new instance of Afastamento with the specified values.
     * @param idAfastamento the idAfastamento of the Afastamento
     */
    public TipoAfastamento(int idAfastamento) {
        this.id = idAfastamento;
    }

    /**
     * Gets the descricao of this Afastamento.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this Afastamento to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }




    /**
     * Determines whether another object is equal to this Afastamento.  The result is
     * <code>true</code> if and only if the argument is not null and is a Afastamento object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoAfastamento)) {
            return false;
        }
        TipoAfastamento other = (TipoAfastamento)object;
        if (this.id != other.id || this.id == 0) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Afastamento[idAfastamento=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(), "Descri��o", lista);
		return lista;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Ao remover as produ��es e atividades, as mesmas n�o ser�o removidas da base de dados,
	 * apenas o campo ativo ser� marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo() {	return this.ativo; }

	/**
	 * Ao remover as produ��es e atividades, as mesmas n�o ser�o removidas da base de dados,
	 * apenas o campo ativo ser� marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)	{ this.ativo = ativo; }



}
