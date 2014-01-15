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
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra os tipos de bolsa que um docente pode receber, incluindo às de produtividade do CNPq
 *
 * @author eric
 */
@Entity
@Table(name = "tipo_bolsa",schema="prodocente")
public class TipoBolsaProdocente implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_bolsa", nullable = false)
    private int id;

    @Column(name = "descricao")
    private String descricao;

	@Column(name = "produtividade")
	private boolean produtividade;

    private boolean ativo;

    /** Creates a new instance of TipoBolsaProdocente */
    public TipoBolsaProdocente() {
    }

    /**
     * Creates a new instance of TipoBolsaProdocente with the specified values.
     * @param id the id of the TipoBolsaProdocente
     */
    public TipoBolsaProdocente(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this TipoBolsaProdocente.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this TipoBolsaProdocente to the specified value.
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the descricao of this TipoBolsaProdocente.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this TipoBolsaProdocente to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }



    /**
     * Determines whether another object is equal to this TipoBolsaProdocente.  The result is
     * <code>true</code> if and only if the argument is not null and is a TipoBolsaProdocente object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoBolsaProdocente)) {
            return false;
        }
        TipoBolsaProdocente other = (TipoBolsaProdocente)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.TipoBolsa[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);

		return lista;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isProdutividade() {
		return this.produtividade;
	}

	public void setProdutividade(boolean produtividade) {
		this.produtividade = produtividade;
	}



}
