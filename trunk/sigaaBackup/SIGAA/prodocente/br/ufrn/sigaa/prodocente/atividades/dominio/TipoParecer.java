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
 * Entidade que registra os tipos de parecer que um docente pode receber em sua qualificação
 *
 * @author eric
 */
@Entity
@Table(name = "tipo_parecer",schema="prodocente")
public class TipoParecer implements Validatable {

	public static final int  FAVORAVEL =1;

	public static final int  NAO_FAVORAVEL = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_parecer", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    
    @Column(name = "descricao", nullable = false)
    private String descricao;



    /** Creates a new instance of EntidadeFinanciadora */
    public TipoParecer() {
    }

    /**
     * Creates a new instance of EntidadeFinanciadora with the specified values.
     * @param idEntidadeFinanciadora the idEntidadeFinanciadora of the EntidadeFinanciadora
     */
    public TipoParecer(int id) {
        this.id = id;
    }

    /**
     * Creates a new instance of EntidadeFinanciadora with the specified values.
     * @param idEntidadeFinanciadora the idEntidadeFinanciadora of the EntidadeFinanciadora
     * @param nome the nome of the EntidadeFinanciadora
     */
    public TipoParecer(int id, String nome) {
        this.id = id;
        this.descricao = nome;
    }

    /**
     * Gets the idEntidadeFinanciadora of this EntidadeFinanciadora.
     * @return the idEntidadeFinanciadora
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the idEntidadeFinanciadora of this EntidadeFinanciadora to the specified value.
     * @param idEntidadeFinanciadora the new idEntidadeFinanciadora
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
     * Gets the nome of this Descricao.
     * @return the Descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the nome of this TipoParecer to the specified value.
     * @param nome the new nome
     */
    public void setDescricao(String nome) {
        this.descricao = nome;
    }




    /**
     * Determines whether another object is equal to this EntidadeFinanciadora.  The result is
     * <code>true</code> if and only if the argument is not null and is a EntidadeFinanciadora object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoParecer)) {
            return false;
        }
        TipoParecer other = (TipoParecer)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.TipoParecer[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista.getMensagens());

		return lista;
	}

}
