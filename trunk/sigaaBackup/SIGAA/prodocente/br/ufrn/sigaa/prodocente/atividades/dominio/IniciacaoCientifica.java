/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '06/12/2006'
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
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Entidade que representa as informações de IniciacaoCientifica
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "iniciacao_cientifica",schema="prodocente")
public class IniciacaoCientifica implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_iniciacao_cientifica", nullable = false)
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

    @Column(name = "instituicao")
    private String instituicao;

    @JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq subArea;

    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento;

    @Column(name = "nome_projeto")
    private String nomeProjeto;

    @Column(name = "orientando")
    private String orientando;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "informacao")
    private String informacao;

    //No banco de desenvolvimento esta como sendo entidade_financiadora
    @JoinColumn(name = "id_agencia_financiadora", referencedColumnName = "id_entidade_financiadora")
    @ManyToOne
    private EntidadeFinanciadora entidadeFinanciadora;


    /** Creates a new instance of IniciacaoCientifica */
    public IniciacaoCientifica() {
    }

    /**
     * Creates a new instance of IniciacaoCientifica with the specified values.
     * @param id the id of the IniciacaoCientifica
     */
    public IniciacaoCientifica(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this IniciacaoCientifica.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this IniciacaoCientifica to the specified value.
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
     * Gets the area of this IniciacaoCientifica.
     * @return the area
     */
    public AreaConhecimentoCnpq getArea() {
        return this.area;
    }

    /**
     * Sets the area of this IniciacaoCientifica to the specified value.
     * @param area the new area
     */
    public void setArea(AreaConhecimentoCnpq area) {
        this.area = area;
    }

    /**
     * Gets the instituicao of this IniciacaoCientifica.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this IniciacaoCientifica to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    /**
     * Gets the subArea of this IniciacaoCientifica.
     * @return the subArea
     */
    public AreaConhecimentoCnpq getSubArea() {
        return this.subArea;
    }

    /**
     * Sets the subArea of this IniciacaoCientifica to the specified value.
     * @param subArea the new subArea
     */
    public void setSubArea(AreaConhecimentoCnpq subArea) {
        this.subArea = subArea;
    }

    /**
     * Gets the departamento of this IniciacaoCientifica.
     * @return the departamento
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the departamento of this IniciacaoCientifica to the specified value.
     * @param departamento the new departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /**
     * Gets the nomeProjeto of this IniciacaoCientifica.
     * @return the nomeProjeto
     */
    public String getNomeProjeto() {
        return this.nomeProjeto;
    }

    /**
     * Sets the nomeProjeto of this IniciacaoCientifica to the specified value.
     * @param nomeProjeto the new nomeProjeto
     */
    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    /**
     * Gets the orientando of this IniciacaoCientifica.
     * @return the orientando
     */
    public String getOrientando() {
        return this.orientando;
    }

    /**
     * Sets the orientando of this IniciacaoCientifica to the specified value.
     * @param orientando the new orientando
     */
    public void setOrientando(String orientando) {
        this.orientando = orientando;
    }

    /**
     * Gets the periodoInicio of this IniciacaoCientifica.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this IniciacaoCientifica to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the periodoFim of this IniciacaoCientifica.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this IniciacaoCientifica to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Gets the servidor of this IniciacaoCientifica.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this IniciacaoCientifica to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the informacao of this IniciacaoCientifica.
     * @return the informacao
     */
    public String getInformacao() {
        return this.informacao;
    }

    /**
     * Sets the informacao of this IniciacaoCientifica to the specified value.
     * @param informacao the new informacao
     */
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    /**
     * Gets the entidadeFinanciadora of this IniciacaoCientifica.
     * @return the entidadeFinanciadora
     */
    public EntidadeFinanciadora getEntidadeFinanciadora() {
        return this.entidadeFinanciadora;
    }

    /**
     * Sets the entidadeFinanciadora of this IniciacaoCientifica to the specified value.
     * @param entidadeFinanciadora the new entidadeFinanciadora
     */
    public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
        this.entidadeFinanciadora = entidadeFinanciadora;
    }

	/**
     * Determines whether another object is equal to this IniciacaoCientifica.  The result is
     * <code>true</code> if and only if the argument is not null and is a IniciacaoCientifica object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IniciacaoCientifica)) {
            return false;
        }
        IniciacaoCientifica other = (IniciacaoCientifica)object;
        if (this.id != other.id && (this.id == 0 || this.id != other.id)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String  toString() {
        return "br.ufrn.sigaa.prodocente.dominio.IniciacaoCientifica[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getNomeProjeto(), "Nome Do Projeto", lista);
		ValidatorUtil.validateRequired(getInstituicao(), "Instituição", lista);
		ValidatorUtil.validateRequiredId(getArea().getId(), "Area", lista);
		ValidatorUtil.validateRequiredId(getSubArea().getId(), "Sub-Area", lista);
		ValidatorUtil.validateRequiredId(getDepartamento().getId(), "Departamento", lista);
		ValidatorUtil.validateRequiredId(getEntidadeFinanciadora().getId(), "Agêcia Financiadora", lista);
		ValidatorUtil.validateRequired(getOrientando(), "Orientando", lista);
		ValidatorUtil.validateRequiredId(getServidor().getId(), "Servidor", lista);
		return lista;
	}

	public String getItemView() {
		return "  <td>"+getOrientando()+ "</td>" +
			   "  <td>"+Formatador.getInstance().formatarData(periodoInicio)+" - "
			  + Formatador.getInstance().formatarData(periodoFim)+"</td>";
	}

	public String getTituloView() {
		return  "  <td>Atividade</td>" +
				"  <td>Período</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("orientando", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}
	
}
