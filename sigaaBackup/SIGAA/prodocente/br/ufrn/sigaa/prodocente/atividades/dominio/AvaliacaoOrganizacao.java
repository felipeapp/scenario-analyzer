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
import java.util.HashMap;

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
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que representa as informações de uma avaliação da organização.
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "avaliacao_organizacao",schema="prodocente")
public class AvaliacaoOrganizacao implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_avaliacao_organizacao", nullable = false)
    private int id;
    
	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    @JoinColumn(name = "id_area", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq area;

    @JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq subArea;

    @Column(name = "veiculo")
    private String veiculo;

    @Column(name = "local")
    private String local;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @Column(name = "informacao")
    private String informacao;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "validacao")
    private String validacao;

    @JoinColumn(name = "id_tipo_avaliacao_organizacao", referencedColumnName = "id_tipo_avaliacao_organizacao_evento")
    @ManyToOne
    private TipoAvaliacaoOrganizacaoEvento tipoAvaliacaoOrganizacao;

    @JoinColumn(name = "id_tipo_participacao", referencedColumnName = "id_tipo_participacao")
    @ManyToOne
    private TipoParticipacao tipoParticipacao;

    @JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
    @ManyToOne
    private TipoRegiao tipoRegiao;

    
    /** Creates a new instance of AvaliacaoOrganizacao */
    public AvaliacaoOrganizacao() {
    }

    /**
     * Creates a new instance of AvaliacaoOrganizacao with the specified values.
     * @param idAvaliacaoOrganizacao the idAvaliacaoOrganizacao of the AvaliacaoOrganizacao
     */
    public AvaliacaoOrganizacao(int idAvaliacaoOrganizacao) {
        this.id = idAvaliacaoOrganizacao;
    }

    /**
     * Gets the idAvaliacaoOrganizacao of this AvaliacaoOrganizacao.
     * @return the idAvaliacaoOrganizacao
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the idAvaliacaoOrganizacao of this AvaliacaoOrganizacao to the specified value.
     * @param idAvaliacaoOrganizacao the new idAvaliacaoOrganizacao
     */
    public void setId(int id) {
        this.id = id;
    }
    
	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo() {	
		return this.ativo; 
	}

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)	{ 
		this.ativo = ativo; 
	}

    /**
     * Gets the idArea of this AvaliacaoOrganizacao.
     * @return the idArea
     */
    public AreaConhecimentoCnpq getArea() {
        return this.area;
    }

    /**
     * Sets the idArea of this AvaliacaoOrganizacao to the specified value.
     * @param idArea the new idArea
     */
    public void setArea(AreaConhecimentoCnpq area) {
        this.area = area;
    }

    /**
     * Gets the idSubarea of this AvaliacaoOrganizacao.
     * @return the idSubarea
     */
    public AreaConhecimentoCnpq getSubArea() {
        return this.subArea;
    }

    /**
     * Sets the idSubarea of this AvaliacaoOrganizacao to the specified value.
     * @param idSubarea the new idSubarea
     */
    public void setSubArea(AreaConhecimentoCnpq subArea) {
        this.subArea = subArea;
    }

    /**
     * Gets the veiculo of this AvaliacaoOrganizacao.
     * @return the veiculo
     */
    public String getVeiculo() {
        return this.veiculo;
    }

    /**
     * Sets the veiculo of this AvaliacaoOrganizacao to the specified value.
     * @param veiculo the new veiculo
     */
    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    /**
     * Gets the local of this AvaliacaoOrganizacao.
     * @return the local
     */
    public String getLocal() {
        return this.local;
    }

    /**
     * Sets the local of this AvaliacaoOrganizacao to the specified value.
     * @param local the new local
     */
    public void setLocal(String local) {
        this.local = local;
    }

    /**
     * Gets the periodoInicio of this AvaliacaoOrganizacao.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this AvaliacaoOrganizacao to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the periodoFim of this AvaliacaoOrganizacao.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this AvaliacaoOrganizacao to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Gets the informacao of this AvaliacaoOrganizacao.
     * @return the informacao
     */
    public String getInformacao() {
        return this.informacao;
    }

    /**
     * Sets the informacao of this AvaliacaoOrganizacao to the specified value.
     * @param informacao the new informacao
     */
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    /**
     * Gets the idServidor of this AvaliacaoOrganizacao.
     * @return the idServidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the idServidor of this AvaliacaoOrganizacao to the specified value.
     * @param idServidor the new idServidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the validacao of this AvaliacaoOrganizacao.
     * @return the validacao
     */
    public String getValidacao() {
    	return this.validacao;
    }

    /**
     * Sets the validacao of this AvaliacaoOrganizacao to the specified value.
     * @param validacao the new validacao
     */
    public void setValidacao(String validacao) {
    	this.validacao = validacao;
    }

    /**
     * Gets the idTipoAvaliacaoOrganizacao of this AvaliacaoOrganizacao.
     * @return the idTipoAvaliacaoOrganizacao
     */
    public TipoAvaliacaoOrganizacaoEvento getTipoAvaliacaoOrganizacao() {
        return this.tipoAvaliacaoOrganizacao;
    }

    /**
     * Sets the idTipoAvaliacaoOrganizacao of this AvaliacaoOrganizacao to the specified value.
     * @param idTipoAvaliacaoOrganizacao the new idTipoAvaliacaoOrganizacao
     */
    public void setTipoAvaliacaoOrganizacao(TipoAvaliacaoOrganizacaoEvento tipoAvaliacaoOrganizacao) {
        this.tipoAvaliacaoOrganizacao = tipoAvaliacaoOrganizacao;
    }

    /**
     * Gets the idTipoParticipacao of this AvaliacaoOrganizacao.
     * @return the idTipoParticipacao
     */
    public TipoParticipacao getTipoParticipacao() {
        return this.tipoParticipacao;
    }

    /**
     * Sets the idTipoParticipacao of this AvaliacaoOrganizacao to the specified value.
     * @param idTipoParticipacao the new idTipoParticipacao
     */
    public void setTipoParticipacao(TipoParticipacao tipoParticipacao) {
        this.tipoParticipacao = tipoParticipacao;
    }

    /**
     * Gets the idTipoRegiao of this AvaliacaoOrganizacao.
     * @return the idTipoRegiao
     */
    public TipoRegiao getTipoRegiao() {
        return this.tipoRegiao;
    }

    /**
     * Sets the idTipoRegiao of this AvaliacaoOrganizacao to the specified value.
     * @param idTipoRegiao the new idTipoRegiao
     */
    public void setTipoRegiao(TipoRegiao tipoRegiao) {
        this.tipoRegiao = tipoRegiao;
    }

    /**
     * Determines whether another object is equal to this AvaliacaoOrganizacao.  The result is
     * <code>true</code> if and only if the argument is not null and is a AvaliacaoOrganizacao object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AvaliacaoOrganizacao)) {
            return false;
        }
        AvaliacaoOrganizacao other = (AvaliacaoOrganizacao)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.AvaliacaoOrganizacao[idAvaliacaoOrganizacao=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(getTipoRegiao().getId(),"Tipo Região", lista);
		ValidatorUtil.validateRequiredId(getServidor().getId(),"Servidor", lista);
		ValidatorUtil.validateRequiredId(getArea().getId(), "Area", lista);
		ValidatorUtil.validateRequiredId(getSubArea().getId(), "Sub-Area", lista);
		ValidatorUtil.validateRequired(getPeriodoFim(), "Período Fim", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Período Inicio", lista);
		ValidatorUtil.validateRequiredId(getTipoAvaliacaoOrganizacao().getId(),"Tipo de Avaliação Organização", lista);
		ValidatorUtil.validateRequiredId(getTipoParticipacao().getId(),"Tipo Participação", lista);
		ValidatorUtil.validateRequiredId(getTipoRegiao().getId(),"Tipo Região", lista);
		return lista;
	}

	public String getItemView() {
		return "  <td>"+getVeiculo()+ "</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(periodoInicio)+" - "
			   + Formatador.getInstance().formatarData(periodoFim)+"</td>";
	}

	public String getTituloView() {
		return  "  <td>Atividade</td>" +
				"  <td style=\"text-align:center\">Período</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("veiculo", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}
}
