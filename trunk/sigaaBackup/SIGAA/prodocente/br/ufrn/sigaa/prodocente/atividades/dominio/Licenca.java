/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que representa as informações de Licenca
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "licenca",schema="prodocente")
public class Licenca implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_licenca", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    
    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @JoinColumn(name = "id_afastamento", referencedColumnName = "id_afastamento")
    @ManyToOne
    private TipoAfastamento afastamento;

    /** Creates a new instance of Licenca */
    public Licenca() {
    }

    /**
     * Creates a new instance of Licenca with the specified values.
     * @param id the id of the Licenca
     */
    public Licenca(Integer id) {
        this.id = id;
    }


    /**
     * Gets the id of this Licenca.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this Licenca to the specified value.
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
     * Gets the servidor of this Licenca.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this Licenca to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the periodoinicio of this Licenca.
     * @return the periodoinicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoinicio of this Licenca to the specified value.
     * @param periodoinicio the new periodoinicio
     */
    public void setPeriodoInicio(Date periodoinicio) {
        this.periodoInicio = periodoinicio;
    }

    /**
     * Gets the periodofim of this Licenca.
     * @return the periodofim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodofim of this Licenca to the specified value.
     * @param periodofim the new periodofim
     */
    public void setPeriodoFim(Date periodofim) {
        this.periodoFim = periodofim;
    }

    /**
     * Gets the afastamento of this Licenca.
     * @return the afastamento
     */
    public TipoAfastamento getAfastamento() {
        return this.afastamento;
    }

    /**
     * Sets the afastamento of this Licenca to the specified value.
     * @param afastamento the new afastamento
     */
    public void setAfastamento(TipoAfastamento afastamento) {
        this.afastamento = afastamento;
    }


    /**
     * Determines whether another object is equal to this Licenca.  The result is
     * <code>true</code> if and only if the argument is not null and is a Licenca object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Licenca)) {
            return false;
        }
        Licenca other = (Licenca)object;
        if (this.id != other.id || (this.id == 0)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Licenca[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(getServidor().getId(),"Docente", lista.getMensagens());
		ValidatorUtil.validateRequired(getPeriodoInicio(),"Data Inicio", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getAfastamento().getId(),"Tipo de Afastamento", lista.getMensagens());

		return lista;
	}

}
