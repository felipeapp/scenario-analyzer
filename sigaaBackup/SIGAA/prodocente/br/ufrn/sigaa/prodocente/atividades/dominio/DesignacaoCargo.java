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
 * Entidade que representa as informações de DesignacaoCargo
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "designacao_cargo",schema="prodocente")
public class DesignacaoCargo implements Validatable {

	public static final int VICE_CHEFE_DEPARTAMENTO = 27;

	public static final int VICE_COORDENADOR_CURSO_GRADUACAO = 28;

	public static final int VICE_COORDENADOR_CURSO_POS_GRADUACAO = 29;

	public static final int COORDENADOR_CURSO_PRO_BASICA_NAO_REMUNERADO = 131;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_designacao_cargo", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    
    @Column(name = "descricao")
    private String descricao;

    @Column(name = "gratificada")
    private Boolean gratificada;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "codigo_rhnet")
    private String codigoRhnet;

    /** Creates a new instance of DesignacaoCargo */
    public DesignacaoCargo() {
    }

    /**
     * Creates a new instance of DesignacaoCargo with the specified values.
     * @param id the id of the DesignacaoCargo
     */
    public DesignacaoCargo(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this DesignacaoCargo.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this DesignacaoCargo to the specified value.
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
     * Gets the descricao of this DesignacaoCargo.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this DesignacaoCargo to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Gets the gratificada of this DesignacaoCargo.
     * @return the gratificada
     */
    public Boolean getGratificada() {
        return this.gratificada;
    }

    /**
     * Sets the gratificada of this DesignacaoCargo to the specified value.
     * @param gratificada the new gratificada
     */
    public void setGratificada(Boolean gratificada) {
        this.gratificada = gratificada;
    }

    /**
     * Gets the tipo of this DesignacaoCargo.
     * @return the tipo
     */
    public String getTipo() {
        return this.tipo;
    }

    /**
     * Sets the tipo of this DesignacaoCargo to the specified value.
     * @param tipo the new tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Gets the codigoRhnet of this DesignacaoCargo.
     * @return the codigoRhnet
     */
    public String getCodigoRhnet() {
        return this.codigoRhnet;
    }

    /**
     * Sets the codigoRhnet of this DesignacaoCargo to the specified value.
     * @param codigoRhnet the new codigoRhnet
     */
    public void setCodigoRhnet(String codigoRhnet) {
        this.codigoRhnet = codigoRhnet;
    }


    /**
     * Determines whether another object is equal to this DesignacaoCargo.  The result is
     * <code>true</code> if and only if the argument is not null and is a DesignacaoCargo object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DesignacaoCargo)) {
            return false;
        }
        DesignacaoCargo other = (DesignacaoCargo)object;
        if (this.id != other.id || (this.id ==0 )) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.DesignacaoCargo[id=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

}
