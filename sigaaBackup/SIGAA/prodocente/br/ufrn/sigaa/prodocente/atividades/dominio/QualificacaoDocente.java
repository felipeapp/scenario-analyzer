/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que contém os dados de qualificações acadêmicas de docentes da instituição
 *
 * @author eric
 */
@Entity
@Table(name = "qualificacao_docente",schema="prodocente")
public class QualificacaoDocente implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_qualificacao_docente", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo = true;


    @Column(name="qualificacao")
    private String qualificacao;

    @Column(name = "instituicao")
    private String instituicao;

    @Column(name = "data_final")
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    @JoinColumn(name = "id_tipo_qualificacao", referencedColumnName = "id_tipo_qualificacao")
    @ManyToOne
    private TipoQualificacao tipoQualificacao;

    @Column(name = "data_inicial")
    @Temporal(TemporalType.DATE)
    private Date dataInicial;

    @Column(name = "afastado")
    private Boolean afastado;

    @Column(name = "afastamento_formal")
    private Boolean afastamentoFormal;

    @JoinColumn(name = "id_tipo_parecer", referencedColumnName = "id_tipo_parecer")
    @ManyToOne
    private TipoParecer tipoParecer;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @JoinColumn(name = "id_pais", referencedColumnName = "id_pais")
    @ManyToOne
    private Pais pais;

    @Column(name = "orientador")
    private String orientador;

    @OneToMany(mappedBy="qualificacaoDocente", cascade = {CascadeType.ALL})
    private Collection<DisciplinaQualificacao> disciplinaQualificacao;

    /** Creates a new instance of QualificacaoDocente */
    public QualificacaoDocente() {
    }

    /**
     * Creates a new instance of QualificacaoDocente with the specified values.
     * @param id the id of the QualificacaoDocente
     */
    public QualificacaoDocente(Integer id) {
        this.id = id;
    }


    /**
     * Gets the id of this QualificacaoDocente.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this QualificacaoDocente to the specified value.
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


    public String getQualificacao()
    {
    	return this.qualificacao;
    }
    public void setQualificacao(String qualificacao)
    {
    	this.qualificacao = qualificacao;
    }


    /**
     * Gets the instituicao of this QualificacaoDocente.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this QualificacaoDocente to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    /**
     * Gets the dataFinal of this QualificacaoDocente.
     * @return the dataFinal
     */
    public Date getDataFinal() {
        return this.dataFinal;
    }

    /**
     * Sets the dataFinal of this QualificacaoDocente to the specified value.
     * @param dataFinal the new dataFinal
     */
    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    /**
     * Gets the dataInicial of this QualificacaoDocente.
     * @return the dataInicial
     */
    public Date getDataInicial() {
        return this.dataInicial;
    }

    /**
     * Sets the dataInicial of this QualificacaoDocente to the specified value.
     * @param dataInicial the new dataInicial
     */
    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }


    /**
     * Gets the servidor of this QualificacaoDocente.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this QualificacaoDocente to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the pais of this QualificacaoDocente.
     * @return the pais
     */
    public Pais getPais() {
        return this.pais;
    }

    /**
     * Sets the pais of this QualificacaoDocente to the specified value.
     * @param pais the new pais
     */
    public void setPais(Pais pais) {
        this.pais = pais;
    }

    /**
     * Gets the orientador of this QualificacaoDocente.
     * @return the orientador
     */
    public String getOrientador() {
        return this.orientador;
    }

    /**
     * Sets the orientador of this QualificacaoDocente to the specified value.
     * @param orientador the new orientador
     */
    public void setOrientador(String orientador) {
        this.orientador = orientador;
    }

    /**
     * Determines whether another object is equal to this QualificacaoDocente.  The result is
     * <code>true</code> if and only if the argument is not null and is a QualificacaoDocente object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof QualificacaoDocente)) {
            return false;
        }
        QualificacaoDocente other = (QualificacaoDocente)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.Qualificacao[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getInstituicao(),"Instituição", lista);
		ValidatorUtil.validateRequired(getOrientador(),"Orientador", lista);
		ValidatorUtil.validateRequired(getServidor(),"Docente", lista);
		if ( getQualificacao().equals("0") )
			ValidatorUtil.validateRequired(null,"Qualificação", lista);
		ValidatorUtil.validateRequired(getPais(),"País", lista);
		ValidatorUtil.validateRequired(getTipoParecer(),"Parecer", lista);
		ValidatorUtil.validateRequired(getDataInicial(),"Data Início", lista);
		
		ValidatorUtil.validaInicioFim(getDataInicial(), getDataFinal(), "Data de Início", lista);
		
		ValidatorUtil.validateRequired(getServidor(),"Docente", lista);
		if ( getDisciplinaQualificacao() != null && getDisciplinaQualificacao().isEmpty() )
			lista.addErro("É necessário informar pelo menos uma disciplina.");
		
		return lista;
	}

	public Boolean getAfastado() {
		return afastado;
	}

	public void setAfastado(Boolean afastado) {
		this.afastado = afastado;
	}

	public Boolean getAfastamentoFormal() {
		return afastamentoFormal;
	}

	public void setAfastamentoFormal(Boolean afastamentoFormal) {
		this.afastamentoFormal = afastamentoFormal;
	}

	public TipoParecer getTipoParecer() {
		return tipoParecer;
	}

	public void setTipoParecer(TipoParecer tipoParecer) {
		this.tipoParecer = tipoParecer;
	}

	public TipoQualificacao getTipoQualificacao() {
		return tipoQualificacao;
	}

	public void setTipoQualificacao(TipoQualificacao tipoQualificacao) {
		this.tipoQualificacao = tipoQualificacao;
	}

	public Collection<DisciplinaQualificacao> getDisciplinaQualificacao() {
		return disciplinaQualificacao;
	}

	public void setDisciplinaQualificacao(
			Collection<DisciplinaQualificacao> disciplinaQualificacao) {
		this.disciplinaQualificacao = disciplinaQualificacao;
	}

	public String getItemView() {
		return "  <td>"+getTipoQualificacao().getDescricao()+ "</td>" +
			   "  <td>"+Formatador.getInstance().formatarData(dataInicial)+" - "+ Formatador.getInstance().formatarData(dataFinal)+"</td>";
	}

	public String getTituloView() {
		return  "    <td>Nível do Curso</td>" +
				"    <td>Período do Afastamento</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("tipoQualificacao.descricao", "descricaoTipoQualificacao");
		itens.put("dataInicial", null);
		itens.put("dataFinal", null);
		return itens;
	}

	@Transient
	public void setDescricaoTipoQualificacao( String descricao ) {
		if ( this.getTipoQualificacao() == null ) {
			this.setTipoQualificacao( new TipoQualificacao() );
		}
		this.getTipoQualificacao().setDescricao(descricao);
	}

	public float getQtdBase() {
		return 1;
	}

}
