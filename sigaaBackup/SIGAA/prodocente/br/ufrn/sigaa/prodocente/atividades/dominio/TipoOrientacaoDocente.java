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
 * Entidade que representa as informações de Tipo Chefia
 *
 * @author Mário Melo
 */
@Entity
//@Table(name = "tipo_orientacao_docente",schema="prodocente")
@Table(name = "tipo_orientacao",schema="prodocente")
public class TipoOrientacaoDocente implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_orientacao_docente", nullable = false)
    private int id;

    @Column(name = "descricao")
    private String descricao;

   
    public TipoOrientacaoDocente() {
	}

    public TipoOrientacaoDocente(Integer id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Determines whether another object is equal to this TipoChefia.  The result is
     * <code>true</code> if and only if the argument is not null and is a TipoChefia object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoOrientacaoDocente)) {
            return false;
        }
        TipoOrientacaoDocente other = (TipoOrientacaoDocente)object;
        if (this.id != other.id && (this.id == 0 || this.id != other.id)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.TipoOrientacaoDocente[id=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

}
