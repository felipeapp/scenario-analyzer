/*
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
 * Tipos de participação de um servidor em um grupo do PET
 *
 * @author eric
 */
@Entity
@Table(name = "classificacao_pet",schema="prodocente")
public class ClassificacaoPet implements Validatable {

	public static final int TUTOR = 1;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_classificacao_pet", nullable = false)
    private int id;
    
	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;


    @Column(name = "descricao")
    private String descricao;

    /** Creates a new instance of ClassificacaoPet */
    public ClassificacaoPet() {
    }

    /**
     * Creates a new instance of ClassificacaoPet with the specified values.
     * @param id the id of the ClassificacaoPet
     */
    public ClassificacaoPet(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this ClassificacaoPet.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this ClassificacaoPet to the specified value.
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
     * Gets the descricao of this ClassificacaoPet.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this ClassificacaoPet to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    /**
     * Determines whether another object is equal to this ClassificacaoPet.  The result is
     * <code>true</code> if and only if the argument is not null and is a ClassificacaoPet object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClassificacaoPet)) {
            return false;
        }
        ClassificacaoPet other = (ClassificacaoPet)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.ClassificacaoPet[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista.getMensagens());
		return lista;
	}

}
