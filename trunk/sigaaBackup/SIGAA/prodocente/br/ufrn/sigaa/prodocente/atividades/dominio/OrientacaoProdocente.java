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
import javax.persistence.FetchType;
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
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Entidade que representa as informações de OrientacaoProdocente
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "orientacao",schema="prodocente")
public class OrientacaoProdocente implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_orientacao", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    
    @Column(name="titulo")
    private String titulo;

    @Column(name="paginas")
    private int paginas;

    @Column(name="programa")
    private String programa;

    @Column(name = "nome_aluno")
    private String nomeAluno;

    @Column(name = "informacao")
    private String informacao;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @JoinColumn(name = "id_tipo_orientacao_docente", referencedColumnName = "id_tipo_orientacao_docente")
    @ManyToOne
    private TipoOrientacaoDocente orientacao;

    @JoinColumn(name = "id_discente", referencedColumnName = "id_discente")
    @ManyToOne
    private Discente aluno;

    @Column(name = "data_inicio")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @Column(name = "data_fim")
    @Temporal(TemporalType.DATE)
    private Date dataFim;

    @Column(name = "data_defesa")
    @Temporal(TemporalType.DATE)
    private Date dataDefesa;

    @JoinColumn(name = "id_ies", referencedColumnName = "id")
    @ManyToOne
    private InstituicoesEnsino ies = new InstituicoesEnsino();

    @JoinColumn(name ="id_departamento",referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento = new Unidade();

    @JoinColumn(name = "id_entidade_financiadora",referencedColumnName = "id_entidade_financiadora")
    @ManyToOne
    private EntidadeFinanciadora entidadeFinanciadora = new EntidadeFinanciadora();

    @JoinColumn(name = "id_tipo_orientacao", referencedColumnName = "id_tipo_orientacao")
    @ManyToOne
    private TipoOrientacao tipoOrientacao;

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area")
	private AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_sub_area")
	private AreaConhecimentoCnpq subArea = new AreaConhecimentoCnpq();

    /** Creates a new instance of OrientacaoProdocente */
    public OrientacaoProdocente() {
    }

    /**
     * Creates a new instance of OrientacaoProdocente with the specified values.
     * @param id the id of the OrientacaoProdocente
     */
    public OrientacaoProdocente(Integer id) {
        this.id = id;
    }



    /**
     * Gets the id of this OrientacaoProdocente.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this OrientacaoProdocente to the specified value.
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
     * Gets the nomeAluno of this OrientacaoProdocente.
     * @return the nomeAluno
     */
    public String getNomeAluno() {
        return this.nomeAluno;
    }

    /**
     * Sets the nomeAluno of this OrientacaoProdocente to the specified value.
     * @param nomeAluno the new nomeAluno
     */
    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }

    /**
     * Gets the servidor of this OrientacaoProdocente.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this OrientacaoProdocente to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }


    /**
	 * @return the orientacao
	 */
	public TipoOrientacaoDocente getOrientacao() {
		return orientacao;
	}

	/**
	 * @param orientacao the orientacao to set
	 */
	public void setOrientacao(TipoOrientacaoDocente orientacao) {
		this.orientacao = orientacao;
	}

	/**
     * Gets the dataInicio of this OrientacaoProdocente.
     * @return the dataInicio
     */
    public Date getDataInicio() {
        return this.dataInicio;
    }

    /**
     * Sets the dataInicio of this OrientacaoProdocente to the specified value.
     * @param dataInicio the new dataInicio
     */
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * Gets the dataFim of this OrientacaoProdocente.
     * @return the dataFim
     */
    public Date getDataFim() {
        return this.dataFim;
    }

    /**
     * Sets the dataFim of this OrientacaoProdocente to the specified value.
     * @param dataFim the new dataFim
     */
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    /**
     * Gets the dataAlteracao of this OrientacaoProdocente.
     * @return the dataAlteracao
     */
    public Date getDataDefesa() {
        return this.dataDefesa;
    }

    /**
     * Sets the dataAlteracao of this OrientacaoProdocente to the specified value.
     * @param dataAlteracao the new dataAlteracao
     */
    public void setDataDefesa(Date dataDefesa) {
        this.dataDefesa = dataDefesa;
    }

    /**
     * Gets the tipoOrientacao of this OrientacaoProdocente.
     * @return the tipoOrientacao
     */
    public TipoOrientacao getTipoOrientacao() {
        return this.tipoOrientacao;
    }

    /**
     * Sets the tipoOrientacao of this OrientacaoProdocente to the specified value.
     * @param tipoOrientacao the new tipoOrientacao
     */
    public void setTipoOrientacao(TipoOrientacao tipoOrientacao) {
        this.tipoOrientacao = tipoOrientacao;
    }


    public Discente getAluno() {
		return aluno;
	}

	public void setAluno(Discente aluno) {
		this.aluno = aluno;
	}

	/**
     * Determines whether another object is equal to this OrientacaoProdocente.  The result is
     * <code>true</code> if and only if the argument is not null and is a OrientacaoProdocente object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof OrientacaoProdocente)) {
            return false;
        }
        OrientacaoProdocente other = (OrientacaoProdocente)object;
        if (this.id != other.id && (this.id == 0)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Orientacao[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getServidor(),"Docente", lista.getMensagens());
		ValidatorUtil.validateRequired(getTitulo(),"Titulo", lista.getMensagens());
		ValidatorUtil.validateRequired(getOrientacao(),"Tipo de Orientacao Docente", lista.getMensagens());
		ValidatorUtil.validateRequired(getIes(),"Instituicao", lista.getMensagens());
		ValidatorUtil.validateRequired(getEntidadeFinanciadora(),"Agencia Financiadora", lista.getMensagens());
		ValidatorUtil.validateRequired(getArea(),"Area", lista.getMensagens());
		ValidatorUtil.validateRequired(getSubArea(),"Sub-Area", lista.getMensagens());
		ValidatorUtil.validateRequired(getDataInicio(),"Data de Inicio", lista.getMensagens());
		ValidatorUtil.validateRequired(getTipoOrientacao(),"Tipo Orientação", lista.getMensagens());
		ValidatorUtil.validateRequired(getOrientacao(),"Tipo Orientação Docente", lista.getMensagens());
		ValidatorUtil.validateRequired(getAluno(),"Aluno", lista.getMensagens());
		ValidatorUtil.validateRequired(getDataDefesa(),"Data de Defesa", lista.getMensagens());
		ValidatorUtil.validateRequired(getPrograma(),"Programa", lista.getMensagens());
		return lista;
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
	 * @return the departamento
	 */
	public Unidade getDepartamento() {
		return departamento;
	}

	/**
	 * @param departamento the departamento to set
	 */
	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	/**
	 * @return the entidadeFinanciadora
	 */
	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return entidadeFinanciadora;
	}

	/**
	 * @param entidadeFinanciadora the entidadeFinanciadora to set
	 */
	public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

	/**
	 * @return the area
	 */
	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	/**
	 * @return the subArea
	 */
	public AreaConhecimentoCnpq getSubArea() {
		return subArea;
	}

	/**
	 * @param subArea the subArea to set
	 */
	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * @return the paginas
	 */
	public int getPaginas() {
		return paginas;
	}

	/**
	 * @param paginas the paginas to set
	 */
	public void setPaginas(int paginas) {
		this.paginas = paginas;
	}

	/**
	 * @return the programa
	 */
	public String getPrograma() {
		return programa;
	}

	/**
	 * @param programa the programa to set
	 */
	public void setPrograma(String programa) {
		this.programa = programa;
	}

	/**
	 * @return the informacao
	 */
	public String getInformacao() {
		return informacao;
	}

	/**
	 * @param informacao the informacao to set
	 */
	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

}
