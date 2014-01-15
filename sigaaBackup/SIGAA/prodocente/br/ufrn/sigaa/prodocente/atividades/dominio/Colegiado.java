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
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoComissaoColegiado;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoMembroColegiado;

/**
 * Entidade que representa as informações de Colegiado
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "colegiado", schema="prodocente")
public class Colegiado implements Validatable {

	@JoinColumn(name = "id_tipo_membro_colegiado", referencedColumnName = "id_tipo_membro_colegiado")
	@ManyToOne
    private TipoMembroColegiado tipoMembroColegiado;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_colegiado", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    
    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento;

    @Column(name = "instituicao")
    private String instituicao;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @Column(name = "comissao")
    private String comissao;

    @Column(name = "informacao")
    private String informacao;

    @Column(name = "nato")
    private Boolean nato;

    @Column(name = "numero_reunioes")
    private Integer numeroReunioes;

    @Column(name = "validacao")
    private boolean validacao;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;


    @JoinColumn(name = "id_tipo_comissao_colegiado", referencedColumnName = "id_tipo_comissao_colegiado")
    @ManyToOne
    private TipoComissaoColegiado tipoComissaoColegiado;

    @JoinColumn(name="id_instituicao", referencedColumnName="id" )
    @ManyToOne
    private InstituicoesEnsino ies = new InstituicoesEnsino();

    /** Creates a new instance of Colegiado */
    public Colegiado() {
    }

    /**
     * Creates a new instance of Colegiado with the specified values.
     * @param id the id of the Colegiado
     */
    public Colegiado(Integer id) {
        this.id = id;
    }


    /**
     * Gets the tipoMembroColegiado of this Colegiado.
     * @return the tipoMembroColegiado
     */
    public TipoMembroColegiado getTipoMembroColegiado() {
        return this.tipoMembroColegiado;
    }

    /**
     * Sets the tipoMembroColegiado of this Colegiado to the specified value.
     * @param tipoMembroColegiado the new tipoMembroColegiado
     */
    public void setTipoMembroColegiado(TipoMembroColegiado tipoMembroColegiado) {
        this.tipoMembroColegiado = tipoMembroColegiado;
    }

    /**
     * Gets the id of this Colegiado.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this Colegiado to the specified value.
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
     * Gets the departamento of this Colegiado.
     * @return the departamento
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the departamento of this Colegiado to the specified value.
     * @param departamento the new departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /**
     * Gets the instituicao of this Colegiado.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this Colegiado to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    /**
     * Gets the periodoInicio of this Colegiado.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this Colegiado to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the periodoFim of this Colegiado.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this Colegiado to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Gets the comissao of this Colegiado.
     * @return the comissao
     */
    public String getComissao() {
        return this.comissao;
    }

    /**
     * Sets the comissao of this Colegiado to the specified value.
     * @param comissao the new comissao
     */
    public void setComissao(String comissao) {
        this.comissao = comissao;
    }

    /**
     * Gets the informacao of this Colegiado.
     * @return the informacao
     */
    public String getInformacao() {
        return this.informacao;
    }

    /**
     * Sets the informacao of this Colegiado to the specified value.
     * @param informacao the new informacao
     */
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    /**
	 * @return the ies
	 */
	public InstituicoesEnsino getIes() {
		return ies;
	}

	/**
	 * @param ies the ies to set
	 */
	public void setIes(InstituicoesEnsino ies) {
		this.ies = ies;
	}

	/**
     * Gets the nato of this Colegiado.
     * @return the nato
     */
    public Boolean getNato() {
        return this.nato;
    }

    /**
     * Sets the nato of this Colegiado to the specified value.
     * @param nato the new nato
     */
    public void setNato(Boolean nato) {
        this.nato = nato;
    }

    /**
     * Gets the numeroReunioes of this Colegiado.
     * @return the numeroReunioes
     */
    public Integer getNumeroReunioes() {
        return this.numeroReunioes;
    }

    /**
     * Sets the numeroReunioes of this Colegiado to the specified value.
     * @param numeroReunioes the new numeroReunioes
     */
    public void setNumeroReunioes(Integer numeroReunioes) {
        this.numeroReunioes = numeroReunioes;
    }

    /**
     * Gets the validacao of this Colegiado.
     * @return the validacao
     */
    public boolean getValidacao() {
        return this.validacao;
    }

    /**
     * Sets the validacao of this Colegiado to the specified value.
     * @param validacao the new validacao
     */
    public void setValidacao(boolean validacao) {
        this.validacao = validacao;
    }

    /**
     * Gets the servidor of this Colegiado.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this Colegiado to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }


    /**
     * Gets the tipoComissaoColegiado of this Colegiado.
     * @return the tipoComissaoColegiado
     */
    public TipoComissaoColegiado getTipoComissaoColegiado() {
        return this.tipoComissaoColegiado;
    }

    /**
     * Sets the tipoComissaoColegiado of this Colegiado to the specified value.
     * @param tipoComissaoColegiado the new tipoComissaoColegiado
     */
    public void setTipoComissaoColegiado(TipoComissaoColegiado tipoComissaoColegiado) {
        this.tipoComissaoColegiado = tipoComissaoColegiado;
    }


    /**
     * Determines whether another object is equal to this Colegiado.  The result is
     * <code>true</code> if and only if the argument is not null and is a Colegiado object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Colegiado)) {
            return false;
        }
        Colegiado other = (Colegiado)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.Colegiado[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getPeriodoInicio() , "Periodo Inicio", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getDepartamento().getId() , "Departamento", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getServidor().getId() , "Docente", lista.getMensagens());

		ValidatorUtil.validateRequiredId(getIes().getId() , "Instituição", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getTipoComissaoColegiado().getId() , "Tipo Comissão Colegiado", lista.getMensagens());
		ValidatorUtil.validateRequired(getComissao() , "Comissão", lista.getMensagens());
		ValidatorUtil.validateRequired(getNumeroReunioes() , "Numero de Reuniões", lista.getMensagens());
		return lista;
	}

}