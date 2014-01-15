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
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Atividades de ensino estão no sistema e existem pois contam com
 * dados antigos migrados de sistemas anteriores.
 *
 */
@Deprecated
@Entity
@Table(name = "atividade_ensino",schema="prodocente")
public class AtividadeEnsino implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_atividade_ensino", nullable = false)
    private int id;
    
	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;


    @Column(name = "remunerada")
    private Boolean remunerada;

    @Column(name = "cadastro_siape")
    private Integer cadastroSiape;

    @Column(name = "cadastro_ufrn")
    private String cadastroUfrn;

    @Column(name = "codigo_disciplina")
    private String codigoDisciplina;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @Column(name = "codigo_turma")
    private String codigoTurma;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "disciplina")
    private String disciplina;

    @Column(name = "carga_horaria")
    private String cargaHoraria;

    @Column(name = "semestre")
    private String semestre;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "informacoes")
    private String informacoes;

    @Column(name = "carga_horaria_teorica")
    private String cargaHorariaTeorica;

    @Column(name = "carga_horaria_pratica")
    private String cargaHorariaPratica;

    @Column(name = "num_alunos")
    private Integer numAlunos;

    @Column(name = "num_docentes")
    private Integer numDocentes;

    @Column(name = "aprovados")
    private Integer aprovados;

    @Column(name = "rep_media")
    private Integer repMedia;

    @Column(name = "rep_freq")
    private Integer repFreq;

    @Column(name = "horesta")
    private Integer horesta;

    @Column(name = "creditos")
    private Integer creditos;

    @Column(name = "carga_horaria_docente")
    private String cargaHorariaDocente;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "programa")
    private String nomeCurso;

    @JoinColumn(name = "id_programa", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade programa;

    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento;

    @Column(name = "pago")
    private Boolean pago;

    @Column(name = "flag_temp_atividades_ensino")
    private String flagTempAtividadesEnsino;

    @JoinColumn(name = "id_programa_residencia_medica", referencedColumnName = "id_programa_residencia_medica")
    @ManyToOne
    private ProgramaResidenciaMedica residencia;

    @JoinColumn(name = "id_tipo_atividade_ensino", referencedColumnName = "id_tipo_atividade_ensino")
    @ManyToOne
    private TipoAtividadeEnsino tipoAtividadeEnsino;

    @Transient
    private boolean lato;

    @Transient
    private boolean stricto;

    public void setTipoAtividadeEnsino(TipoAtividadeEnsino tipoAtividadeEnsino) {
		this.tipoAtividadeEnsino = tipoAtividadeEnsino;
	}

	/** Creates a new instance of AtividadeEnsino */
    public AtividadeEnsino() {
    }

    /**
     * Creates a new instance of AtividadeEnsino with the specified values.
     * @param idAtividadeEnsino the idAtividadeEnsino of the AtividadeEnsino
     */
    public AtividadeEnsino(Integer idAtividadeEnsino) {
        this.id= idAtividadeEnsino;
    }

    /**
     * Gets the idAtividadeEnsino of this AtividadeEnsino.
     * @return the idAtividadeEnsino
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the idAtividadeEnsino of this AtividadeEnsino to the specified value.
     * @param idAtividadeEnsino the new idAtividadeEnsino
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
     * Gets the remunerada of this AtividadeEnsino.
     * @return the remunerada
     */
    public Boolean getRemunerada() {
        return this.remunerada;
    }

    /**
     * Sets the remunerada of this AtividadeEnsino to the specified value.
     * @param remunerada the new remunerada
     */
    public void setRemunerada(Boolean remunerada) {
        this.remunerada = remunerada;
    }

    /**
     * Gets the cadastroSiape of this AtividadeEnsino.
     * @return the cadastroSiape
     */
    public Integer getCadastroSiape() {
        return this.cadastroSiape;
    }

    /**
     * Sets the cadastroSiape of this AtividadeEnsino to the specified value.
     * @param cadastroSiape the new cadastroSiape
     */
    public void setCadastroSiape(Integer cadastroSiape) {
        this.cadastroSiape = cadastroSiape;
    }

    /**
     * Gets the cadastroUfrn of this AtividadeEnsino.
     * @return the cadastroUfrn
     */
    public String getCadastroUfrn() {
        return this.cadastroUfrn;
    }

    /**
     * Sets the cadastroUfrn of this AtividadeEnsino to the specified value.
     * @param cadastroUfrn the new cadastroUfrn
     */
    public void setCadastroUfrn(String cadastroUfrn) {
        this.cadastroUfrn = cadastroUfrn;
    }

    /**
     * Gets the codigoDisciplina of this AtividadeEnsino.
     * @return the codigoDisciplina
     */
    public String getCodigoDisciplina() {
        return this.codigoDisciplina;
    }

    /**
     * Sets the codigoDisciplina of this AtividadeEnsino to the specified value.
     * @param codigoDisciplina the new codigoDisciplina
     */
    public void setCodigoDisciplina(String codigoDisciplina) {
        this.codigoDisciplina = codigoDisciplina;
    }

    /**
     * Gets the periodoInicio of this AtividadeEnsino.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this AtividadeEnsino to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the periodoFim of this AtividadeEnsino.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this AtividadeEnsino to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Gets the codigoTurma of this AtividadeEnsino.
     * @return the codigoTurma
     */
    public String getCodigoTurma() {
        return this.codigoTurma;
    }

    /**
     * Sets the codigoTurma of this AtividadeEnsino to the specified value.
     * @param codigoTurma the new codigoTurma
     */
    public void setCodigoTurma(String codigoTurma) {
        this.codigoTurma = codigoTurma;
    }

    /**
     * Gets the tipo of this AtividadeEnsino.
     * @return the tipo
     */
    public String getTipo() {
        return this.tipo;
    }

    /**
     * Sets the tipo of this AtividadeEnsino to the specified value.
     * @param tipo the new tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Gets the disciplina of this AtividadeEnsino.
     * @return the disciplina
     */
    public String getDisciplina() {
        return this.disciplina;
    }

    /**
     * Sets the disciplina of this AtividadeEnsino to the specified value.
     * @param disciplina the new disciplina
     */
    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    /**
     * Gets the cargaHoraria of this AtividadeEnsino.
     * @return the cargaHoraria
     */
    public String getCargaHoraria() {
        return this.cargaHoraria;
    }

    /**
     * Sets the cargaHoraria of this AtividadeEnsino to the specified value.
     * @param cargaHoraria the new cargaHoraria
     */
    public void setCargaHoraria(String cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    /**
     * Gets the semestre of this AtividadeEnsino.
     * @return the semestre
     */
    public String getSemestre() {
        return this.semestre;
    }

    /**
     * Sets the semestre of this AtividadeEnsino to the specified value.
     * @param semestre the new semestre
     */
    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    /**
     * Gets the ano of this AtividadeEnsino.
     * @return the ano
     */
    public Integer getAno() {
        return this.ano;
    }

    /**
     * Sets the ano of this AtividadeEnsino to the specified value.
     * @param ano the new ano
     */
    public void setAno(Integer ano) {
        this.ano = ano;
    }

    /**
     * Gets the informacoes of this AtividadeEnsino.
     * @return the informacoes
     */
    public String getInformacoes() {
        return this.informacoes;
    }

    /**
     * Sets the informacoes of this AtividadeEnsino to the specified value.
     * @param informacoes the new informacoes
     */
    public void setInformacoes(String informacoes) {
        this.informacoes = informacoes;
    }

    /**
     * Gets the cargaHorariaTeorica of this AtividadeEnsino.
     * @return the cargaHorariaTeorica
     */
    public String getCargaHorariaTeorica() {
        return this.cargaHorariaTeorica;
    }

    /**
     * Sets the cargaHorariaTeorica of this AtividadeEnsino to the specified value.
     * @param cargaHorariaTeorica the new cargaHorariaTeorica
     */
    public void setCargaHorariaTeorica(String cargaHorariaTeorica) {
        this.cargaHorariaTeorica = cargaHorariaTeorica;
    }

    /**
     * Gets the cargaHorariaPratica of this AtividadeEnsino.
     * @return the cargaHorariaPratica
     */
    public String getCargaHorariaPratica() {
        return this.cargaHorariaPratica;
    }

    /**
     * Sets the cargaHorariaPratica of this AtividadeEnsino to the specified value.
     * @param cargaHorariaPratica the new cargaHorariaPratica
     */
    public void setCargaHorariaPratica(String cargaHorariaPratica) {
        this.cargaHorariaPratica = cargaHorariaPratica;
    }

    /**
     * Gets the numAlunos of this AtividadeEnsino.
     * @return the numAlunos
     */
    public Integer getNumAlunos() {
        return this.numAlunos;
    }

    /**
     * Sets the numAlunos of this AtividadeEnsino to the specified value.
     * @param numAlunos the new numAlunos
     */
    public void setNumAlunos(Integer numAlunos) {
        this.numAlunos = numAlunos;
    }

    /**
     * Gets the numDocentes of this AtividadeEnsino.
     * @return the numDocentes
     */
    public Integer getNumDocentes() {
        return this.numDocentes;
    }

    /**
     * Sets the numDocentes of this AtividadeEnsino to the specified value.
     * @param numDocentes the new numDocentes
     */
    public void setNumDocentes(Integer numDocentes) {
        this.numDocentes = numDocentes;
    }

    /**
     * Gets the aprovados of this AtividadeEnsino.
     * @return the aprovados
     */
    public Integer getAprovados() {
        return this.aprovados;
    }

    /**
     * Sets the aprovados of this AtividadeEnsino to the specified value.
     * @param aprovados the new aprovados
     */
    public void setAprovados(Integer aprovados) {
        this.aprovados = aprovados;
    }

    /**
     * Gets the repMedia of this AtividadeEnsino.
     * @return the repMedia
     */
    public Integer getRepMedia() {
        return this.repMedia;
    }

    /**
     * Sets the repMedia of this AtividadeEnsino to the specified value.
     * @param repMedia the new repMedia
     */
    public void setRepMedia(Integer repMedia) {
        this.repMedia = repMedia;
    }

    /**
     * Gets the repFreq of this AtividadeEnsino.
     * @return the repFreq
     */
    public Integer getRepFreq() {
        return this.repFreq;
    }

    /**
     * Sets the repFreq of this AtividadeEnsino to the specified value.
     * @param repFreq the new repFreq
     */
    public void setRepFreq(Integer repFreq) {
        this.repFreq = repFreq;
    }

    /**
     * Gets the horesta of this AtividadeEnsino.
     * @return the horesta
     */
    public Integer getHoresta() {
        return this.horesta;
    }

    /**
     * Sets the horesta of this AtividadeEnsino to the specified value.
     * @param horesta the new horesta
     */
    public void setHoresta(Integer horesta) {
        this.horesta = horesta;
    }

    /**
     * Gets the creditos of this AtividadeEnsino.
     * @return the creditos
     */
    public Integer getCreditos() {
        return this.creditos;
    }

    /**
     * Sets the creditos of this AtividadeEnsino to the specified value.
     * @param creditos the new creditos
     */
    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    /**
     * Gets the cargaHorariaDocente of this AtividadeEnsino.
     * @return the cargaHorariaDocente
     */
    public String getCargaHorariaDocente() {
        return this.cargaHorariaDocente;
    }

    /**
     * Sets the cargaHorariaDocente of this AtividadeEnsino to the specified value.
     * @param cargaHorariaDocente the new cargaHorariaDocente
     */
    public void setCargaHorariaDocente(String cargaHorariaDocente) {
        this.cargaHorariaDocente = cargaHorariaDocente;
    }

    /**
     * Gets the idServidor of this AtividadeEnsino.
     * @return the idServidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the idServidor of this AtividadeEnsino to the specified value.
     * @param idServidor the new idServidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }


    /**
     * Gets the pago of this AtividadeEnsino.
     * @return the pago
     */
    public Boolean getPago() {
        return this.pago;
    }

    /**
     * Sets the pago of this AtividadeEnsino to the specified value.
     * @param pago the new pago
     */
    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    /**
     * Gets the flagTempAtividadesEnsino of this AtividadeEnsino.
     * @return the flagTempAtividadesEnsino
     */
    public String getFlagTempAtividadesEnsino() {
        return this.flagTempAtividadesEnsino;
    }

    /**
     * Sets the flagTempAtividadesEnsino of this AtividadeEnsino to the specified value.
     * @param flagTempAtividadesEnsino the new flagTempAtividadesEnsino
     */
    public void setFlagTempAtividadesEnsino(String flagTempAtividadesEnsino) {
        this.flagTempAtividadesEnsino = flagTempAtividadesEnsino;
    }



    /**
     * Determines whether another object is equal to this AtividadeEnsino.  The result is
     * <code>true</code> if and only if the argument is not null and is a AtividadeEnsino object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AtividadeEnsino)) {
            return false;
        }
        AtividadeEnsino other = (AtividadeEnsino)object;
        if (this.id != other.id && (this.id ==0)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.AtividadeEnsino[idAtividadeEnsino=" + id + "]";
    }


	public Unidade getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}



	/**
	 * @return the nomeCurso
	 */
	public String getNomeCurso() {
		return nomeCurso;
	}

	/**
	 * @param nomeCurso the nomeCurso to set
	 */
	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}

	public TipoAtividadeEnsino getTipoAtividadeEnsino() {
		return tipoAtividadeEnsino;
	}


	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getServidor(), "Docente", lista.getMensagens());
		ValidatorUtil.validateRequired(getAno(), "Ano", lista.getMensagens());
		ValidatorUtil.validateRequired(getSemestre(), "Semestre", lista.getMensagens());
		//ValidatorUtil.validateRequired(getTipo(), "Tipo", lista.getMensagens());
		//ValidatorUtil.validateRequired(getPeriodoInicio(), "Periodo Inicio", lista.getMensagens());
		ValidatorUtil.validateRequired(getCodigoTurma(), "Código da Turma", lista.getMensagens());
		ValidatorUtil.validateRequired(getDisciplina(), "Nome da Disciplina", lista.getMensagens());
		ValidatorUtil.validateRequired(getDepartamento(), "Departamento", lista.getMensagens());
		ValidatorUtil.validateRequired(getCargaHoraria(), "Carga Horária Disciplina" , lista.getMensagens());
		ValidatorUtil.validateRequired(getCargaHorariaPratica(), "Carga Horária Pratica", lista.getMensagens());
		ValidatorUtil.validateRequired(getCargaHorariaDocente(), "Carga Horária Docente", lista.getMensagens());
		ValidatorUtil.validateRequired(getCargaHorariaTeorica(), "Carga Horária Teórica", lista.getMensagens());
		ValidatorUtil.validateRequired(getNumAlunos(), "Numeros de Alunos", lista.getMensagens());
		ValidatorUtil.validateRequired(getNumDocentes(), "Numeros de Docentes", lista.getMensagens());
		if(lato)
			ValidatorUtil.validateRequired(getNomeCurso(), "Nome do Curso", lista.getMensagens());
		else if(stricto)
			ValidatorUtil.validateRequired(getPrograma(), "Programa", lista.getMensagens());
		else
			ValidatorUtil.validateRequired(getResidencia(), "Nome do Curso", lista.getMensagens());
		return lista;
	}

	/**
	 * @return the programa
	 */
	public Unidade getPrograma() {
		return programa;
	}

	/**
	 * @param programa the programa to set
	 */
	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	/**
	 * @return the residencia
	 */
	public ProgramaResidenciaMedica getResidencia() {
		return residencia;
	}

	/**
	 * @param residencia the residencia to set
	 */
	public void setResidencia(ProgramaResidenciaMedica residencia) {
		this.residencia = residencia;
	}

	/**
	 * @return the lato
	 */
	public boolean isLato() {
		return lato;
	}

	/**
	 * @param lato the lato to set
	 */
	public void setLato(boolean lato) {
		this.lato = lato;
	}

	/**
	 * @return the stricto
	 */
	public boolean isStricto() {
		return stricto;
	}

	/**
	 * @param stricto the stricto to set
	 */
	public void setStricto(boolean stricto) {
		this.stricto = stricto;
	}



}
