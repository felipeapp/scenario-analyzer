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
 * Entidade que registra os tipos de orienta��es que um docente da institui��o pode realizar
 *
 * @author eric
 */
@Entity
@Table(name = "tipo_orientacao",schema="prodocente")
public class TipoOrientacao implements Validatable {

	public static final int GRADUACAO = 1;

	public static final int ESPECIALIZACAO = 2;

	public static final int MESTRADO = 3;

	public static final int DOUTORADO = 4;

	public static final int OUTRA = 5;

	public static final int RESIDENCIA_MEDICA = 6;

	public static final int POS_DOUTORADO =7;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_orientacao", nullable = false)
    private int id;

	/**
	 * Ao remover as produ��es e atividades, as mesmas n�o ser�o removidas da base de dados,
	 * apenas o campo ativo ser� marcado como FALSE
	 */
	private Boolean ativo;

    private String descricao;

	private Character nivelEnsino;

    /** Creates a new instance of TipoOrientacao */
    public TipoOrientacao() {
    }

    /**
     * Creates a new instance of TipoOrientacao with the specified values.
     * @param id the id of the TipoOrientacao
     */
    public TipoOrientacao(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this TipoOrientacao.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this TipoOrientacao to the specified value.
     * @param id the new id
     */
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



    /**
     * Gets the descricao of this TipoOrientacao.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this TipoOrientacao to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Determines whether another object is equal to this TipoOrientacao.  The result is
     * <code>true</code> if and only if the argument is not null and is a TipoOrientacao object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoOrientacao)) {
            return false;
        }
        TipoOrientacao other = (TipoOrientacao)object;
        if (this.id != other.id && this.id == 0) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.TipoOrientacao[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(), "Descricao", lista);
		ValidatorUtil.validateRequired(getNivelEnsino(), "N�vel de Ensino", lista);
		return lista;
	}

	public Character getNivelEnsino() {
		return nivelEnsino;
	}

	public void setNivelEnsino(Character nivelEnsino) {
		this.nivelEnsino = nivelEnsino;
	}

}
