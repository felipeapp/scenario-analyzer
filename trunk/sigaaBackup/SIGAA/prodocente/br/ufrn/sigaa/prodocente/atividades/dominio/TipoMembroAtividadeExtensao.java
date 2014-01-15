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
 * Entidade que representa as informações de Tipo Membro Ativividade Extensao
 *
 * @author eric
 */
@Entity
@Table(name = "tipo_membro_ativividade_extensao",schema="prodocente")
public class TipoMembroAtividadeExtensao implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_membro_atividade_extensao", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    @Column(name = "descricao")
    private String descricao;

    /** Creates a new instance of TipoMembroAtivividadeExtensao */
    public TipoMembroAtividadeExtensao() {
    }

    /**
     * Creates a new instance of TipoMembroAtivividadeExtensao with the specified values.
     * @param id the id of the TipoMembroAtivividadeExtensao
     */
    public TipoMembroAtividadeExtensao(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this TipoMembroAtivividadeExtensao.
     * @return the id
     */
    public int getId() {
        return this.id;
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
	public void setAtivo(Boolean ativo)	{ 
		this.ativo = ativo; 
	}

    /**
     * Sets the id of this TipoMembroAtivividadeExtensao to the specified value.
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the descricao of this TipoMembroAtivividadeExtensao.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this TipoMembroAtivividadeExtensao to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Determines whether another object is equal to this TipoMembroAtivividadeExtensao.  The result is
     * <code>true</code> if and only if the argument is not null and is a TipoMembroAtivividadeExtensao object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoMembroAtividadeExtensao)) {
            return false;
        }
        TipoMembroAtividadeExtensao other = (TipoMembroAtividadeExtensao)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.TipoMembroAtivividadeExtensao[id=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

}
