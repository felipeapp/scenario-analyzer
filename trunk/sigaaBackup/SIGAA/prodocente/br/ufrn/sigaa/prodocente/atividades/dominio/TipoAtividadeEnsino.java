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

/**
 * Entidade que representa as informações de Tipo Atividade Ensino
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "tipo_atividade_ensino",schema="prodocente")
public class TipoAtividadeEnsino implements Validatable {


	public static final TipoAtividadeEnsino GRADUACAO = new TipoAtividadeEnsino(1);
	public static final TipoAtividadeEnsino MESTRADO = new TipoAtividadeEnsino(2);
	public static final TipoAtividadeEnsino DOUTORADO = new TipoAtividadeEnsino(3);
	public static final TipoAtividadeEnsino SEGUNDO_GRAU = new TipoAtividadeEnsino(4);
	public static final TipoAtividadeEnsino ESPECIALIZACAO = new TipoAtividadeEnsino(5);
	public static final TipoAtividadeEnsino RESIDENCIA_MEDICA = new TipoAtividadeEnsino(6);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_atividade_ensino", nullable = false)
    private int id;

    @Column(name = "descricao")
    private String descricao;

    /** Creates a new instance of TipoAtividadeEnsino */
    public TipoAtividadeEnsino() {
    }

    /**
     * Creates a new instance of TipoAtividadeEnsino with the specified values.
     * @param id the id of the TipoAtividadeEnsino
     */
    public TipoAtividadeEnsino(int id) {
        this.id = id;
    }

    /**
     * Gets the id of this TipoAtividadeEnsino.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this TipoAtividadeEnsino to the specified value.
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the descricao of this TipoAtividadeEnsino.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this TipoAtividadeEnsino to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Determines whether another object is equal to this TipoAtividadeEnsino.  The result is
     * <code>true</code> if and only if the argument is not null and is a TipoAtividadeEnsino object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoAtividadeEnsino)) {
            return false;
        }
        TipoAtividadeEnsino other = (TipoAtividadeEnsino)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.TipoAtividadeEnsino[id=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

}
