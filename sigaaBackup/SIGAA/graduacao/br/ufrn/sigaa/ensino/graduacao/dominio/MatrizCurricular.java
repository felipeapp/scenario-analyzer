/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 10/01/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.SituacaoCursoHabil;
import br.ufrn.sigaa.ensino.dominio.SituacaoDiploma;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeLetivo;
import br.ufrn.sigaa.ensino.dominio.TipoSistemaCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;

/**
 * Entidade que representa unicamente um curso atrav�s da combina��o de:
 * curso, turno, grauAcademico e habilita��o. Uma matriz pode ou n�o ter habilita��o.
 *
 * @author Gleydson
 * @author Andr�
 */
@Entity
@Table(name = "matriz_curricular", schema = "graduacao")
@Cache ( usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MatrizCurricular implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_matriz_curricular", nullable = false)
	private int id;

	/** Indica se a matriz curricular possui habilita��o. */
	@Column(name = "possui_habilitacao")
	private Boolean possuiHabilitacao;
	
	/** Indica se a matriz curricular possui �nfase. */
	@Column(name = "possui_enfase")
	private Boolean possuiEnfase;

	/** Data de extin��o da matriz curricular. */
	@Column(name = "data_extincao")
	@Temporal(TemporalType.DATE)
	private Date dataExtincao;

	/** Data de in�cio de funcionamento da matriz curricular. */
	@Column(name = "data_inicio_funcionamento")
	@Temporal(TemporalType.DATE)
	private Date dataInicioFuncionamento;

	/** Ato normativo de autoriza��o de funcionamento da matriz curricular. */
	@Column(name="autorizacao_ato_normativo")
	private String autorizacaoAtoNormativo;

	/** Data do ato normativo de autoriza��o de funcionamento da matriz curricular. */
	@Column(name="autorizacao_ato_data")
	@Temporal(TemporalType.DATE)
	private Date autorizacaoAtoData;

	/** Data de publica��o do ato normativo de autoriza��o de funcionamento da matriz curricular. */
	@Column(name="autorizacao_publicacao")
	@Temporal(TemporalType.DATE)
	private Date autorizacaoPublicacao;

	/** Indica se a matriz curricular � ativa para utiliza��o no sistema. */
	@Column(name = "ativo")
	private Boolean ativo;

	/** {@link TipoRegimeLetivo Tipo de regime letivo} desta matriz curricular.*/
	@ManyToOne()
	@JoinColumn(name = "id_tipo_regime_letivo")
	private TipoRegimeLetivo regimeLetivo;

	/** {@link SituacaoDiploma Situa��o do diploma} desta matriz curricular.*/
	@ManyToOne()
	@JoinColumn(name = "id_situacao_diploma")
	private SituacaoDiploma situacaoDiploma;

	/** {@link TipoSistemaCurricular Tipo de sistemas curricular} desta matriz curricular.*/
	@ManyToOne()
	@JoinColumn(name = "id_tipo_sistema_curricular")
	private TipoSistemaCurricular tipoSistemaCurricular;

	/** Campus ao qual esta matriz curricular est� associada. */
	@ManyToOne()
	@JoinColumn(name = "id_campus")
	private CampusIes campus;

	/** {@link SituacaoCursoHabil Situa��o de atividade}  desta matriz curricular. */
	@ManyToOne()
	@JoinColumn(name = "id_situacao")
	private SituacaoCursoHabil situacao;

	/** {@link GrauAcademico Grau acad�mico} conferido por esta matriz curricular. */
	@ManyToOne()
	@JoinColumn(name = "id_grau_academico")
	private GrauAcademico grauAcademico;

	/** {@link Turno} de aulas da matriz curricular. */
	@ManyToOne()
	@JoinColumn(name = "id_turno")
	private Turno turno;

	/** {@link Curso} da matriz curricular. */
	@ManyToOne()
	@JoinColumn(name = "id_curso")
	private Curso curso;

	/** {@link Habilitacao} dada pela matriz curricular. */
	@ManyToOne()
	@JoinColumn(name = "id_habilitacao")
	private Habilitacao habilitacao;

	/** {@link Enfase �nfase} desta matriz curricular. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_enfase")
	private Enfase enfase;
	
	/** {@link RegistroEntrada Registro de Entrada} do usu�rio que cadastrou a matriz curricular. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_cadastro")
	private RegistroEntrada criadoPor;

	/** Data de cadastro da matriz curricular. */
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date criadoEm;

	/** {@link RegistroEntrada Registro de Entrada} do usu�rio que atualizou a matriz curricular.*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_atualizacao")
	private RegistroEntrada atualizadoPor;

	/** Data de atualiza��o da matriz curricular. */
	@Column(name = "data_atualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date atualizadoEm;
	
	/** C�digo da matriz curricular no INEP ({@linkplain http://www.inep.org.br}).*/
	@Column(name="codigo_inep")
	private String codigoINEP;
	
	/** Indica se a matriz curricular permite cola��o de grau.
	 * Para as matrizes curriculares que forem apostilamento o valor dever� ser false.*/
	@Column(name="permite_colacao_grau")
	private boolean permiteColacaoGrau = true;
	
	
	/** Indica se a matriz curricular permite cola��o de grau.
	 * 
	 * @return Para as matrizes curriculares que forem apostilamento o valor dever� ser false.
	 */
	public boolean isPermiteColacaoGrau() {
		return permiteColacaoGrau;
	}

	/** Seta se a matriz curricular permite cola��o de grau.
	 * @param permiteColacaoGrau Para as matrizes curriculares que forem apostilamento o valor dever� ser false.
	 */
	public void setPermiteColacaoGrau(boolean permiteColacaoGrau) {
		this.permiteColacaoGrau = permiteColacaoGrau;
	}

	/** Construtor padr�o. */
	public MatrizCurricular() {
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public MatrizCurricular(int id) {
		setId(id);
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param curso
	 */
	public MatrizCurricular(int id, Curso curso){
		setId(id);
		setCurso(curso);
	}

	/** Retorna a  Situa��o de atividade desta matriz curricular. 
	 * @return
	 */
	public SituacaoCursoHabil getSituacao() {
		return situacao;
	}

	/** Seta a  Situa��o de atividade desta matriz curricular.
	 * @param situacao
	 */
	public void setSituacao(SituacaoCursoHabil situacao) {
		this.situacao = situacao;
	}

	/** Indica se a matriz curricular � ativa para utiliza��o no sistema. 
	 * @return
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/** Seta se a matriz curricular � ativa para utiliza��o no sistema.
	 * @param ativo
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o campus ao qual esta matriz curricular est� associada. 
	 * @return
	 */
	public CampusIes getCampus() {
		return campus;
	}

	/** Seta o campus ao qual esta matriz curricular est� associada.
	 * @param campus
	 */
	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}

	/** Retorna a data de extin��o da matriz curricular. 
	 * @return
	 */
	public Date getDataExtincao() {
		return dataExtincao;
	}

	/** Seta a data de extin��o da matriz curricular.
	 * @param dataExtincao
	 */
	public void setDataExtincao(Date dataExtincao) {
		this.dataExtincao = dataExtincao;
	}

	/** Retorna a Data de in�cio de funcionamento da matriz curricular. 
	 * @return
	 */
	public Date getDataInicioFuncionamento() {
		return dataInicioFuncionamento;
	}

	/** Seta a Data de in�cio de funcionamento da matriz curricular.
	 * @param dataInicioFuncionamento
	 */
	public void setDataInicioFuncionamento(Date dataInicioFuncionamento) {
		this.dataInicioFuncionamento = dataInicioFuncionamento;
	}

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Indica se a matriz curricular possui habilita��o. 
	 * @return
	 */
	public Boolean getPossuiHabilitacao() {
		return possuiHabilitacao;
	}

	/** Seta se a matriz curricular possui habilita��o.
	 * @param possuiHabilitacao
	 */
	public void setPossuiHabilitacao(Boolean possuiHabilitacao) {
		this.possuiHabilitacao = possuiHabilitacao;
	}

	/**  Retorna a situa��o do diploma desta matriz curricular.
	 * @return
	 */
	public SituacaoDiploma getSituacaoDiploma() {
		return situacaoDiploma;
	}

	/**  Seta a situa��o do diploma desta matriz curricular.
	 * @param situacaoDiploma
	 */
	public void setSituacaoDiploma(SituacaoDiploma situacaoDiploma) {
		this.situacaoDiploma = situacaoDiploma;
	}

	/** Retorna o tipo de regime letivo desta matriz curricular.
	 * @return
	 */
	public TipoRegimeLetivo getRegimeLetivo() {
		return regimeLetivo;
	}

	/** Seta o tipo de regime letivo desta matriz curricular.
	 * @param tipoRegimeLetivo
	 */
	public void setRegimeLetivo(TipoRegimeLetivo tipoRegimeLetivo) {
		regimeLetivo = tipoRegimeLetivo;
	}

	/** Retorna o  tipo de sistemas curricular desta matriz curricular.
	 * @return
	 */
	public TipoSistemaCurricular getTipoSistemaCurricular() {
		return tipoSistemaCurricular;
	}

	/** Seta o  tipo de sistemas curricular desta matriz curricular.
	 * @param tipoSistemaCurricular
	 */
	public void setTipoSistemaCurricular(TipoSistemaCurricular tipoSistemaCurricular) {
		this.tipoSistemaCurricular = tipoSistemaCurricular;
	}

	/** Retorna o turno de aulas da matriz curricular.  
	 * @return
	 */
	public Turno getTurno() {
		return turno;
	}

	/** Seta o turno de aulas da matriz curricular.
	 * @param turno
	 */
	public void setTurno(Turno turno) {
		this.turno = turno;
	}

	/** Valida os dados da matriz curricular: curso, turno, grauAcademico, regimeLetivo,
	 * tipoSistemaCurricular, situa��o, possuiHabilitacao, autorizacaoAtoNormativo,
	 * autorizacaoAtoData, autorizacaoPublicacao, dataExtincao.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(curso, "Curso", erros);
		ValidatorUtil.validateRequired(campus, "Campus", erros);
		if (curso != null && !curso.isADistancia())
			ValidatorUtil.validateRequired(turno, "Turno", erros);
		ValidatorUtil.validateRequired(grauAcademico, "Modalidade", erros);
		ValidatorUtil.validateRequired(regimeLetivo, "Regime Letivo", erros);
		ValidatorUtil.validateRequired(tipoSistemaCurricular, "Sistema Curricular", erros);
		ValidatorUtil.validateRequired(situacao, "Situa��o", erros);
		ValidatorUtil.validateRequired(possuiHabilitacao, "Possui Habilita��o", erros);
		ValidatorUtil.validateRequired(possuiEnfase, "Possui �nfase", erros);
		ValidatorUtil.validateRequired(autorizacaoAtoNormativo, "Ato Normativo da Autoriza��o", erros);
		ValidatorUtil.validateRequired(autorizacaoAtoData, "Data do Ato Normativo da Autoriza��o", erros);
		ValidatorUtil.validateRequired(autorizacaoPublicacao, "Publica��o da Autoriza��o", erros);
		if (dataExtincao != null)
			ValidatorUtil.validateMinValue(dataExtincao, dataInicioFuncionamento, "Data de Extin��o", erros);
		if (getPossuiEnfase() != null && getPossuiEnfase().booleanValue())
			ValidatorUtil.validateRequired(enfase, "�nfase", erros);
		validateMinValue(autorizacaoPublicacao, autorizacaoAtoData, "Data da Publica��o", erros);
		return erros;
	}
	
	/** Retorna o curso da matriz curricular. 
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso da matriz curricular.
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna a habilitacao dada pela matriz curricular. 
	 * @return
	 */
	public Habilitacao getHabilitacao() {
		return habilitacao;
	}

	/** Seta a habilita��o dada pela matriz curricular.
	 * @param habilitacao
	 */
	public void setHabilitacao(Habilitacao habilitacao) {
		this.habilitacao = habilitacao;
	}

	/** Retorna o grau acad�mico conferido por esta matriz curricular. 
	 * @return
	 */
	public GrauAcademico getGrauAcademico() {
		return grauAcademico;
	}

	/** Seta o grau acad�mico conferido por esta matriz curricular.
	 * @param grauAcademico
	 */
	public void setGrauAcademico(GrauAcademico grauAcademico) {
		this.grauAcademico = grauAcademico;
	}

	/**
	 * Retorna uma descri��o textual desta matriz curricular no formato: nome do
	 * curso, seguido por "-", seguido por nome do munic�pio (caso n�o nulo),
	 * seguido por nome da habilita��o (caso n�o nula), seguido por "-", seguido
	 * por nome da �nfase (caso n�o nula), seguido por "-", seguido pela sigla
	 * do turno, seguido por "-", seguido por uma descri��o do grau acad�mico.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricao() {
		StringBuffer descricao = new StringBuffer();
		if (curso != null && curso.getNome() != null)
			if (curso.getMunicipio() != null)
				descricao.append(curso.getNome() + " - " + curso.getMunicipio().getNome() + " - ");
			else
				descricao.append(curso.getNome() + " - ");
		if (curso != null && curso.getModalidadeEducacao() != null)
			descricao.append(curso.getModalidadeEducacao().getDescricao() + " - ");		
		if (habilitacao != null && habilitacao.getNome() != null)
			descricao.append(habilitacao.getNome() + " - ");
		if (enfase != null && enfase.getNome() != null)
			descricao.append(enfase.getNome() + " - ");
		if (turno != null && turno.getSigla() != null) {
			descricao.append(turno.getSigla() + " - ");
		}
		if (grauAcademico != null && grauAcademico.getDescricao() != null)
			descricao.append(grauAcademico.getDescricao() + " - ");
		if (descricao.length() > 0)
			descricao.delete(descricao.lastIndexOf(" - "), descricao.length());
		return descricao.toString();
	}
	
	/**
	 * Retorna uma descri��o textual desta matriz curricular no formato: nome do
	 * curso, seguido por "-", seguido por nome da habilita��o (caso n�o nula),
	 * seguido por "-", seguido por nome da �nfase (caso n�o nula), seguido por
	 * "-", seguido pela sigla do turno, seguido por "-", seguido por uma
	 * descri��o do grau acad�mico.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoSemMunicipio() {
		StringBuffer descricao = new StringBuffer();
		if (curso != null && curso.getNome() != null)
			descricao.append(curso.getNome() + " - ");
		if (curso != null && curso.getModalidadeEducacao() != null)
			descricao.append(curso.getModalidadeEducacao().getDescricao() + " - ");		
		if (habilitacao != null && habilitacao.getNome() != null)
			descricao.append(habilitacao.getNome() + " - ");
		if (enfase != null && enfase.getNome() != null)
			descricao.append(enfase.getNome() + " - ");
		if (turno != null && turno.getSigla() != null) {
			descricao.append(turno.getSigla() + " - ");
		}
		if (grauAcademico != null && grauAcademico.getDescricao() != null)
			descricao.append(grauAcademico.getDescricao() + " - ");
		if (descricao.length() > 0)
			descricao.delete(descricao.lastIndexOf(" - "), descricao.length());
		return descricao.toString();
	}
	
	/**
	 * Retorna uma descri��o textual desta matriz curricular no formato: nome do
	 * curso, seguido por "-", seguido por nome da habilita��o (caso n�o nula),
	 * seguido por "-", seguido por nome da �nfase (caso n�o nula), seguido por
	 * "-", seguido pela sigla do turno, seguido por "-", seguido por uma
	 * descri��o do grau acad�mico.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoSemEnfase() {
		StringBuffer descricao = new StringBuffer();
		if (curso != null && curso.getNome() != null)
			if (curso.getMunicipio() != null)
				descricao.append(curso.getNome() + " - " + curso.getMunicipio().getNome() + " - ");
			else
				descricao.append(curso.getNome() + " - ");
		if (curso.getModalidadeEducacao() != null)
			descricao.append(curso.getModalidadeEducacao().getDescricao() + " - ");		
		if (habilitacao != null && habilitacao.getNome() != null)
			descricao.append(habilitacao.getNome() + " - ");
		if (turno != null && turno.getSigla() != null) {
			descricao.append(turno.getSigla() + " - ");
		}
		if (grauAcademico != null && grauAcademico.getDescricao() != null)
			descricao.append(grauAcademico.getDescricao() + " - ");
		if (descricao.length() > 0)
			descricao.delete(descricao.lastIndexOf(" - "), descricao.length());
		return descricao.toString();
	}


	/**
	 * Retorna uma descri��o textual desta matriz curricular, sem o nome do curso.
	 * @see #getDescricao()
	 * @return
	 */
	@Transient
	public String getDescricaoMin() {
		StringBuffer descricao = new StringBuffer();
		if (habilitacao != null && habilitacao.getNome() != null)
			descricao.append(habilitacao.getNome() + " - ");
		if (enfase != null && enfase.getNome() != null)
			descricao.append(enfase.getNome() + " - ");		
		if (turno != null && turno.getSigla() != null) {
			descricao.append(turno.getSigla() + " - ");
		}
		if (grauAcademico != null && grauAcademico.getDescricao() != null)
			descricao.append(grauAcademico.getDescricao() + " - ");
		if (descricao.lastIndexOf(" - ") > 0)
			descricao.delete(descricao.lastIndexOf(" - "), descricao.length());
		return descricao.toString();
	}

	/**
	 * Retorna uma descri��o textual m�nima desta matriz curricular, acrescentando no final se a matriz � inativa.
	 * @return
	 */
	public String getDescricaoMinInativa() {
		StringBuffer descricao = new StringBuffer();
		if (habilitacao != null && habilitacao.getNome() != null)
			descricao.append(habilitacao.getNome() + " - ");
		if (turno != null && turno.getSigla() != null) {
			descricao.append(turno.getSigla() + " - ");
		}
		if (grauAcademico != null && grauAcademico.getDescricao() != null)
			descricao.append(grauAcademico.getDescricao() + " - ");
		if ( !ativo )
			descricao.append("(Inativa)");
		if (descricao.lastIndexOf(" - ") > 0)
			descricao.delete(descricao.lastIndexOf(" - "), descricao.length());
		return descricao.toString();
	}

	/** Compara este objeto ao passado por par�metro, comparando o atributo "id".
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna uma descri��o textual da matriz curricular.
	 * @see #getDescricao()
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

	/** Indica se o grau acad�mico desta matriz curricular � de Licenciatura Plena.
	 * @return
	 */
	@Transient
	public boolean isLicenciaturaPlena() {
		return getGrauAcademico() != null && getGrauAcademico().getId() == GrauAcademico.LICENCIATURA_PLENA;
	}

	@Transient
	public boolean isBacharelado() {
		return getGrauAcademico() != null && getGrauAcademico().getId() == GrauAcademico.BACHARELADO;
	}	
	
	/** Retorna o ato normativo de autoriza��o de funcionamento da matriz curricular. 
	 * @return
	 */
	public String getAutorizacaoAtoNormativo() {
		return autorizacaoAtoNormativo;
	}

	/** Seta o ato normativo de autoriza��o de funcionamento da matriz curricular.
	 * @param autorizacaoAtoNormativo
	 */
	public void setAutorizacaoAtoNormativo(String autorizacaoAtoNormativo) {
		this.autorizacaoAtoNormativo = autorizacaoAtoNormativo;
	}

	/** Retorna a data do ato normativo de autoriza��o de funcionamento da matriz curricular. 
	 * @return
	 */
	public Date getAutorizacaoAtoData() {
		return autorizacaoAtoData;
	}

	/** Seta a data do ato normativo de autoriza��o de funcionamento da matriz curricular.
	 * @param autorizacaoAtoData
	 */
	public void setAutorizacaoAtoData(Date autorizacaoAtoData) {
		this.autorizacaoAtoData = autorizacaoAtoData;
	}

	/** Retorna a data de publica��o do ato normativo de autorização de funcionamento da matriz curricular. 
	 * @return
	 */
	public Date getAutorizacaoPublicacao() {
		return autorizacaoPublicacao;
	}

	/** Seta a data de publica��o do ato normativo de autorização de funcionamento da matriz curricular.
	 * @param autorizacaoPublicacao
	 */
	public void setAutorizacaoPublicacao(Date autorizacaoPublicacao) {
		this.autorizacaoPublicacao = autorizacaoPublicacao;
	}

	/** Retorna o registro de Entrada do usu�rio que cadastrou a matriz curricular. 
	 * @return
	 */
	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	/** Seta o registro de Entrada do usu�rio que cadastrou a matriz curricular.
	 * @param criadoPor
	 */
	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	/** Retorna a data de cadastro da matriz curricular. 
	 * @return
	 */
	public Date getCriadoEm() {
		return criadoEm;
	}

	/** Seta a data de cadastro da matriz curricular.
	 * @param criadoEm
	 */
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/** Retorna o registro de Entrada do usu�rio que atualizou a matriz curricular.
	 * @return
	 */
	public RegistroEntrada getAtualizadoPor() {
		return atualizadoPor;
	}

	/** Seta o registro de Entrada do usu�rio que atualizou a matriz curricular.
	 * @param atualizadoPor
	 */
	public void setAtualizadoPor(RegistroEntrada atualizadoPor) {
		this.atualizadoPor = atualizadoPor;
	}

	/** Retorna a Data de atualiza��o da matriz curricular. 
	 * @return
	 */
	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	/** Seta a Data de atualiza��o da matriz curricular.
	 * @param atualizadoEm
	 */
	public void setAtualizadoEm(Date atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}
	
	/** Retorna o c�digo da matriz curricular no INEP.
	 * @return
	 */
	public String getCodigoINEP() {
		return codigoINEP;
	}

	/** Seta o c�digo da matriz curricular no INEP.
	 * @param codigoINEP
	 */
	public void setCodigoINEP(String codigoINEP) {
		this.codigoINEP = codigoINEP;
	}

	/** Retorna a �nfase desta matriz curricular. 
	 * @return
	 */
	public Enfase getEnfase() {
		return enfase;
	}

	/** Seta a �nfase desta matriz curricular. 
	 * @param enfase
	 */
	public void setEnfase(Enfase enfase) {
		this.enfase = enfase;
	}

	/** Indica se a matriz curricular possui �nfase. 
	 * @return
	 */
	public Boolean getPossuiEnfase() {
		return possuiEnfase;
	}

	/** Seta se a matriz curricular possui �nfase.
	 * @param possuiEnfase
	 */
	public void setPossuiEnfase(Boolean possuiEnfase) {
		this.possuiEnfase = possuiEnfase;
	}
}
