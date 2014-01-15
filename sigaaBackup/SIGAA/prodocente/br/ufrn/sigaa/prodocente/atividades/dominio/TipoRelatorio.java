/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '30/01/2007'
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
 * Entidade que representa as informações de Tipo Relatorio
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "tipo_relatorio",schema="prodocente")
public class TipoRelatorio implements Validatable {

	public static final int ASSESSORIA = 1;
	public static final int ASSESSORIA_CONSULTORIA = 2;
	public static final int ATIVIDADE_TECNICA = 3;
	public static final int CURSO = 4;
	public static final int EVENTO = 5;
	public static final int PROJETO = 6;
	public static final int RELATORIO = 7;
	public static final int RELATORIO_EXTENSAO = 8;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @Column(name = "id_tipo_relatorio", nullable = false)
    private int id;

    @Column(name = "descricao")
    private String descricao;


    /** Creates a new instance of TipoRelatorio */
    public TipoRelatorio() {
    }

    /**
     * Creates a new instance of TipoRelatorio with the specified values.
     * @param idTipoRelatorio the idTipoRelatorio of the TipoRelatorio
     */
    public TipoRelatorio(int idTipoRelatorio) {
        this.id = idTipoRelatorio;
    }

    /**
     * Gets the idTipoRelatorio of this TipoRelatorio.
     * @return the idTipoRelatorio
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the idTipoRelatorio of this TipoRelatorio to the specified value.
     * @param idTipoRelatorio the new idTipoRelatorio
     */
    public void setId(int idTipoRelatorio) {
        this.id = idTipoRelatorio;
    }

    /**
     * Gets the descricao of this TipoRelatorio.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this TipoRelatorio to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Determines whether another object is equal to this TipoRelatorio.  The result is
     * <code>true</code> if and only if the argument is not null and is a TipoRelatorio object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoRelatorio)) {
            return false;
        }
        TipoRelatorio other = (TipoRelatorio)object;
        if (this.id != other.id  || this.id ==0) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.TipoRelatorio[idTipoRelatorio=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

}
