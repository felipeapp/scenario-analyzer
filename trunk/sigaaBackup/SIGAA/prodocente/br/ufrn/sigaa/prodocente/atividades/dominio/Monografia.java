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
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que representa as informações de Monografia
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "monografia",schema="prodocente")
public class Monografia implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_monografia", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    @Column(name = "instituicao")
    private String instituicao;

    @JoinColumn(name = "id_area", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq area;

    @JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq subArea;

    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "orientando")
    private String orientando;

    @JoinColumn(name = "id_aluno",referencedColumnName = "id_discente")
    @ManyToOne
    private Discente aluno;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "paginas")
    private Integer paginas;

    @Column(name = "informacao")
    private String informacao;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @Column(name= "orientacao")
    private Character orientacao;

    @Column(name = "validacao")
    private Boolean validacao;

    @JoinColumn(name = "id_tipo_orientacao", referencedColumnName = "id_tipo_orientacao")
    @ManyToOne
    private TipoOrientacao tipoOrientacao;

    @Column(name = "data_publicacao")
    @Temporal(TemporalType.DATE)
    private Date dataPublicacao;

    @Column(name="discente_externo")
    private Boolean DiscenteExterno;

    /** Creates a new instance of Monografia */
    public Monografia() {
    }

    /**
     * Creates a new instance of Monografia with the specified values.
     * @param id the id of the Monografia
     */
    public Monografia(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this Monografia.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this Monografia to the specified value.
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
     * Gets the instituicao of this Monografia.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this Monografia to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    /**
     * Gets the area of this Monografia.
     * @return the area
     */
    public AreaConhecimentoCnpq getArea() {
        return this.area;
    }

    /**
     * Sets the area of this Monografia to the specified value.
     * @param area the new area
     */
    public void setArea(AreaConhecimentoCnpq area) {
        this.area = area;
    }

    /**
     * Gets the subArea of this Monografia.
     * @return the subArea
     */
    public AreaConhecimentoCnpq getSubArea() {
        return this.subArea;
    }

    /**
     * Sets the subArea of this Monografia to the specified value.
     * @param subArea the new subArea
     */
    public void setSubArea(AreaConhecimentoCnpq subArea) {
        this.subArea = subArea;
    }

    /**
     * Gets the idDepartamento of this Monografia.
     * @return the idDepartamento
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the idDepartamento of this Monografia to the specified value.
     * @param idDepartamento the new idDepartamento
     */
    public void setDepartamento(Unidade idDepartamento) {
        this.departamento = idDepartamento;
    }

    /**
     * Gets the titulo of this Monografia.
     * @return the titulo
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Sets the titulo of this Monografia to the specified value.
     * @param titulo the new titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Gets the orientando of this Monografia.
     * @return the orientando
     */
    public String getOrientando() {
        return this.orientando;
    }

    /**
     * Sets the orientando of this Monografia to the specified value.
     * @param orientando the new orientando
     */
    public void setOrientando(String orientando) {
        this.orientando = orientando;
    }

    /**
     * Gets the periodoInicio of this Monografia.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this Monografia to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the paginas of this Monografia.
     * @return the paginas
     */
    public Integer getPaginas() {
        return this.paginas;
    }

    /**
     * Sets the paginas of this Monografia to the specified value.
     * @param paginas the new paginas
     */
    public void setPaginas(Integer paginas) {
        this.paginas = paginas;
    }

    /**
     * Gets the informacao of this Monografia.
     * @return the informacao
     */
    public String getInformacao() {
        return this.informacao;
    }

    /**
     * Sets the informacao of this Monografia to the specified value.
     * @param informacao the new informacao
     */
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    /**
     * Gets the idServidor of this Monografia.
     * @return the idServidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the idServidor of this Monografia to the specified value.
     * @param idServidor the new idServidor
     */
    public void setServidor(Servidor idServidor) {
        this.servidor = idServidor;
    }

    /**
     * Gets the periodoFim of this Monografia.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this Monografia to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Gets the validacao of this Monografia.
     * @return the validacao
     */
    public Boolean getValidacao() {
        return this.validacao;
    }

    /**
     * Sets the validacao of this Monografia to the specified value.
     * @param validacao the new validacao
     */
    public void setValidacao(Boolean validacao) {
        this.validacao = validacao;
    }

    /**
     * Gets the tipoOrientacao of this Monografia.
     * @return the tipoOrientacao
     */
    public TipoOrientacao getTipoOrientacao() {
        return this.tipoOrientacao;
    }

    /**
     * Sets the tipoOrientacao of this Monografia to the specified value.
     * @param tipoOrientacao the new tipoOrientacao
     */
    public void setTipoOrientacao(TipoOrientacao tipoOrientacao) {
        this.tipoOrientacao = tipoOrientacao;
    }


    /**
     * Determines whether another object is equal to this Monografia.  The result is
     * <code>true</code> if and only if the argument is not null and is a Monografia object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Monografia)) {
            return false;
        }
        Monografia other = (Monografia)object;
        if (this.id != other.id && (this.id == 0 || this.id !=other.id)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Monografia[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getTitulo(),"Titulo", lista);
		//Só valida se não for discente externo
		if(!getDiscenteExterno())
			ValidatorUtil.validateRequired(getAluno(),"Orientando", lista);
		if (getDiscenteExterno()) //se for discente externo só precisa da string do nome
			ValidatorUtil.validateRequired(getOrientando(),"Orientando", lista);

		ValidatorUtil.validateRequired(getInstituicao(),"Instituição", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(),"Periodo Inicio", lista);
		ValidatorUtil.validateRequired(getArea(),"Area", lista);
		ValidatorUtil.validateRequired(getSubArea(),"Sub-Area", lista);
		ValidatorUtil.validateRequired(getDepartamento(),"Departamento", lista);
		ValidatorUtil.validateRequired(getTipoOrientacao(),"Monografia", lista);
		if(getOrientacao().equals('0')){
			MensagemAviso erro = new MensagemAviso("Orientação : Campo obrigatório não informado",TipoMensagemUFRN.ERROR);
			lista.getMensagens().add(erro);
		}
		return lista;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public String getItemView() {
		return "  <td>"+titulo+ "</td>" +
			   "  <td>TFC</td>" +
			   "  <td>"+Formatador.getInstance().formatarData(periodoInicio) + " - " 
			   + Formatador.getInstance().formatarData(periodoFim) + "</td>";
	}

	public String getTituloView() {
		return  "  <td>Nome do Projeto</td>" +
				"  <td>Tipo</td>" +
				"  <td>Período</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		return itens;
	}

	/**
	 * @return the aluno
	 */
	public Discente getAluno() {
		return aluno;
	}

	/**
	 * @param aluno the aluno to set
	 */
	public void setAluno(Discente aluno) {
		this.aluno = aluno;
	}

	/**
	 * @return the orientacao
	 */
	public Character getOrientacao() {
		return orientacao;
	}

	/**
	 * @param orientacao the orientacao to set
	 */
	public void setOrientacao(Character orientacao) {
		this.orientacao = orientacao;
	}

	public Boolean getDiscenteExterno(){ 
		return this.DiscenteExterno; 
	}
	
	public void setDiscenteExterno(Boolean discenteExterno) { 
		this.DiscenteExterno = discenteExterno; 
	}

	public float getQtdBase() {
		return 1;
	}


}
