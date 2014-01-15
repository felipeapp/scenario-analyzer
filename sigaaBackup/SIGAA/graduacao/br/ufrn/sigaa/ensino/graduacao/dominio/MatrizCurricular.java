/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Entidade que representa unicamente um curso através da combinação de:
 * curso, turno, grauAcademico e habilitação. Uma matriz pode ou não ter habilitação.
 *
 * @author Gleydson
 * @author André
 */
@Entity
@Table(name = "matriz_curricular", schema = "graduacao")
@Cache ( usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MatrizCurricular implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_matriz_curricular", nullable = false)
	private int id;

	/** Indica se a matriz curricular possui habilitação. */
	@Column(name = "possui_habilitacao")
	private Boolean possuiHabilitacao;
	
	/** Indica se a matriz curricular possui ênfase. */
	@Column(name = "possui_enfase")
	private Boolean possuiEnfase;

	/** Data de extinção da matriz curricular. */
	@Column(name = "data_extincao")
	@Temporal(TemporalType.DATE)
	private Date dataExtincao;

	/** Data de início de funcionamento da matriz curricular. */
	@Column(name = "data_inicio_funcionamento")
	@Temporal(TemporalType.DATE)
	private Date dataInicioFuncionamento;

	/** Ato normativo de autorização de funcionamento da matriz curricular. */
	@Column(name="autorizacao_ato_normativo")
	private String autorizacaoAtoNormativo;

	/** Data do ato normativo de autorização de funcionamento da matriz curricular. */
	@Column(name="autorizacao_ato_data")
	@Temporal(TemporalType.DATE)
	private Date autorizacaoAtoData;

	/** Data de publicação do ato normativo de autorização de funcionamento da matriz curricular. */
	@Column(name="autorizacao_publicacao")
	@Temporal(TemporalType.DATE)
	private Date autorizacaoPublicacao;

	/** Indica se a matriz curricular é ativa para utilização no sistema. */
	@Column(name = "ativo")
	private Boolean ativo;

	/** {@link TipoRegimeLetivo Tipo de regime letivo} desta matriz curricular.*/
	@ManyToOne()
	@JoinColumn(name = "id_tipo_regime_letivo")
	private TipoRegimeLetivo regimeLetivo;

	/** {@link SituacaoDiploma Situação do diploma} desta matriz curricular.*/
	@ManyToOne()
	@JoinColumn(name = "id_situacao_diploma")
	private SituacaoDiploma situacaoDiploma;

	/** {@link TipoSistemaCurricular Tipo de sistemas curricular} desta matriz curricular.*/
	@ManyToOne()
	@JoinColumn(name = "id_tipo_sistema_curricular")
	private TipoSistemaCurricular tipoSistemaCurricular;

	/** Campus ao qual esta matriz curricular está associada. */
	@ManyToOne()
	@JoinColumn(name = "id_campus")
	private CampusIes campus;

	/** {@link SituacaoCursoHabil Situação de atividade}  desta matriz curricular. */
	@ManyToOne()
	@JoinColumn(name = "id_situacao")
	private SituacaoCursoHabil situacao;

	/** {@link GrauAcademico Grau acadêmico} conferido por esta matriz curricular. */
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

	/** {@link Enfase Ênfase} desta matriz curricular. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_enfase")
	private Enfase enfase;
	
	/** {@link RegistroEntrada Registro de Entrada} do usuário que cadastrou a matriz curricular. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_cadastro")
	private RegistroEntrada criadoPor;

	/** Data de cadastro da matriz curricular. */
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date criadoEm;

	/** {@link RegistroEntrada Registro de Entrada} do usuário que atualizou a matriz curricular.*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_atualizacao")
	private RegistroEntrada atualizadoPor;

	/** Data de atualização da matriz curricular. */
	@Column(name = "data_atualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date atualizadoEm;
	
	/** Código da matriz curricular no INEP ({@linkplain http://www.inep.org.br}).*/
	@Column(name="codigo_inep")
	private String codigoINEP;
	
	/** Indica se a matriz curricular permite colação de grau.
	 * Para as matrizes curriculares que forem apostilamento o valor deverá ser false.*/
	@Column(name="permite_colacao_grau")
	private boolean permiteColacaoGrau = true;
	
	
	/** Indica se a matriz curricular permite colação de grau.
	 * 
	 * @return Para as matrizes curriculares que forem apostilamento o valor deverá ser false.
	 */
	public boolean isPermiteColacaoGrau() {
		return permiteColacaoGrau;
	}

	/** Seta se a matriz curricular permite colação de grau.
	 * @param permiteColacaoGrau Para as matrizes curriculares que forem apostilamento o valor deverá ser false.
	 */
	public void setPermiteColacaoGrau(boolean permiteColacaoGrau) {
		this.permiteColacaoGrau = permiteColacaoGrau;
	}

	/** Construtor padrão. */
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

	/** Retorna a  Situação de atividade desta matriz curricular. 
	 * @return
	 */
	public SituacaoCursoHabil getSituacao() {
		return situacao;
	}

	/** Seta a  Situação de atividade desta matriz curricular.
	 * @param situacao
	 */
	public void setSituacao(SituacaoCursoHabil situacao) {
		this.situacao = situacao;
	}

	/** Indica se a matriz curricular é ativa para utilização no sistema. 
	 * @return
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/** Seta se a matriz curricular é ativa para utilização no sistema.
	 * @param ativo
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o campus ao qual esta matriz curricular está associada. 
	 * @return
	 */
	public CampusIes getCampus() {
		return campus;
	}

	/** Seta o campus ao qual esta matriz curricular está associada.
	 * @param campus
	 */
	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}

	/** Retorna a data de extinção da matriz curricular. 
	 * @return
	 */
	public Date getDataExtincao() {
		return dataExtincao;
	}

	/** Seta a data de extinção da matriz curricular.
	 * @param dataExtincao
	 */
	public void setDataExtincao(Date dataExtincao) {
		this.dataExtincao = dataExtincao;
	}

	/** Retorna a Data de início de funcionamento da matriz curricular. 
	 * @return
	 */
	public Date getDataInicioFuncionamento() {
		return dataInicioFuncionamento;
	}

	/** Seta a Data de início de funcionamento da matriz curricular.
	 * @param dataInicioFuncionamento
	 */
	public void setDataInicioFuncionamento(Date dataInicioFuncionamento) {
		this.dataInicioFuncionamento = dataInicioFuncionamento;
	}

	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Indica se a matriz curricular possui habilitação. 
	 * @return
	 */
	public Boolean getPossuiHabilitacao() {
		return possuiHabilitacao;
	}

	/** Seta se a matriz curricular possui habilitação.
	 * @param possuiHabilitacao
	 */
	public void setPossuiHabilitacao(Boolean possuiHabilitacao) {
		this.possuiHabilitacao = possuiHabilitacao;
	}

	/**  Retorna a situação do diploma desta matriz curricular.
	 * @return
	 */
	public SituacaoDiploma getSituacaoDiploma() {
		return situacaoDiploma;
	}

	/**  Seta a situação do diploma desta matriz curricular.
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
	 * tipoSistemaCurricular, situação, possuiHabilitacao, autorizacaoAtoNormativo,
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
		ValidatorUtil.validateRequired(situacao, "Situação", erros);
		ValidatorUtil.validateRequired(possuiHabilitacao, "Possui Habilitação", erros);
		ValidatorUtil.validateRequired(possuiEnfase, "Possui Ênfase", erros);
		ValidatorUtil.validateRequired(autorizacaoAtoNormativo, "Ato Normativo da Autorização", erros);
		ValidatorUtil.validateRequired(autorizacaoAtoData, "Data do Ato Normativo da Autorização", erros);
		ValidatorUtil.validateRequired(autorizacaoPublicacao, "Publicação da Autorização", erros);
		if (dataExtincao != null)
			ValidatorUtil.validateMinValue(dataExtincao, dataInicioFuncionamento, "Data de Extinção", erros);
		if (getPossuiEnfase() != null && getPossuiEnfase().booleanValue())
			ValidatorUtil.validateRequired(enfase, "Ênfase", erros);
		validateMinValue(autorizacaoPublicacao, autorizacaoAtoData, "Data da Publicação", erros);
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

	/** Seta a habilitação dada pela matriz curricular.
	 * @param habilitacao
	 */
	public void setHabilitacao(Habilitacao habilitacao) {
		this.habilitacao = habilitacao;
	}

	/** Retorna o grau acadêmico conferido por esta matriz curricular. 
	 * @return
	 */
	public GrauAcademico getGrauAcademico() {
		return grauAcademico;
	}

	/** Seta o grau acadêmico conferido por esta matriz curricular.
	 * @param grauAcademico
	 */
	public void setGrauAcademico(GrauAcademico grauAcademico) {
		this.grauAcademico = grauAcademico;
	}

	/**
	 * Retorna uma descrição textual desta matriz curricular no formato: nome do
	 * curso, seguido por "-", seguido por nome do município (caso não nulo),
	 * seguido por nome da habilitação (caso não nula), seguido por "-", seguido
	 * por nome da ênfase (caso não nula), seguido por "-", seguido pela sigla
	 * do turno, seguido por "-", seguido por uma descrição do grau acadêmico.
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
	 * Retorna uma descrição textual desta matriz curricular no formato: nome do
	 * curso, seguido por "-", seguido por nome da habilitação (caso não nula),
	 * seguido por "-", seguido por nome da ênfase (caso não nula), seguido por
	 * "-", seguido pela sigla do turno, seguido por "-", seguido por uma
	 * descrição do grau acadêmico.
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
	 * Retorna uma descrição textual desta matriz curricular no formato: nome do
	 * curso, seguido por "-", seguido por nome da habilitação (caso não nula),
	 * seguido por "-", seguido por nome da ênfase (caso não nula), seguido por
	 * "-", seguido pela sigla do turno, seguido por "-", seguido por uma
	 * descrição do grau acadêmico.
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
	 * Retorna uma descrição textual desta matriz curricular, sem o nome do curso.
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
	 * Retorna uma descrição textual mínima desta matriz curricular, acrescentando no final se a matriz é inativa.
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

	/** Compara este objeto ao passado por parâmetro, comparando o atributo "id".
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna uma descrição textual da matriz curricular.
	 * @see #getDescricao()
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

	/** Indica se o grau acadêmico desta matriz curricular é de Licenciatura Plena.
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
	
	/** Retorna o ato normativo de autorização de funcionamento da matriz curricular. 
	 * @return
	 */
	public String getAutorizacaoAtoNormativo() {
		return autorizacaoAtoNormativo;
	}

	/** Seta o ato normativo de autorização de funcionamento da matriz curricular.
	 * @param autorizacaoAtoNormativo
	 */
	public void setAutorizacaoAtoNormativo(String autorizacaoAtoNormativo) {
		this.autorizacaoAtoNormativo = autorizacaoAtoNormativo;
	}

	/** Retorna a data do ato normativo de autorização de funcionamento da matriz curricular. 
	 * @return
	 */
	public Date getAutorizacaoAtoData() {
		return autorizacaoAtoData;
	}

	/** Seta a data do ato normativo de autorização de funcionamento da matriz curricular.
	 * @param autorizacaoAtoData
	 */
	public void setAutorizacaoAtoData(Date autorizacaoAtoData) {
		this.autorizacaoAtoData = autorizacaoAtoData;
	}

	/** Retorna a data de publicação do ato normativo de autorizaÃ§Ã£o de funcionamento da matriz curricular. 
	 * @return
	 */
	public Date getAutorizacaoPublicacao() {
		return autorizacaoPublicacao;
	}

	/** Seta a data de publicação do ato normativo de autorizaÃ§Ã£o de funcionamento da matriz curricular.
	 * @param autorizacaoPublicacao
	 */
	public void setAutorizacaoPublicacao(Date autorizacaoPublicacao) {
		this.autorizacaoPublicacao = autorizacaoPublicacao;
	}

	/** Retorna o registro de Entrada do usuário que cadastrou a matriz curricular. 
	 * @return
	 */
	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	/** Seta o registro de Entrada do usuário que cadastrou a matriz curricular.
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

	/** Retorna o registro de Entrada do usuário que atualizou a matriz curricular.
	 * @return
	 */
	public RegistroEntrada getAtualizadoPor() {
		return atualizadoPor;
	}

	/** Seta o registro de Entrada do usuário que atualizou a matriz curricular.
	 * @param atualizadoPor
	 */
	public void setAtualizadoPor(RegistroEntrada atualizadoPor) {
		this.atualizadoPor = atualizadoPor;
	}

	/** Retorna a Data de atualização da matriz curricular. 
	 * @return
	 */
	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	/** Seta a Data de atualização da matriz curricular.
	 * @param atualizadoEm
	 */
	public void setAtualizadoEm(Date atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}
	
	/** Retorna o código da matriz curricular no INEP.
	 * @return
	 */
	public String getCodigoINEP() {
		return codigoINEP;
	}

	/** Seta o código da matriz curricular no INEP.
	 * @param codigoINEP
	 */
	public void setCodigoINEP(String codigoINEP) {
		this.codigoINEP = codigoINEP;
	}

	/** Retorna a ênfase desta matriz curricular. 
	 * @return
	 */
	public Enfase getEnfase() {
		return enfase;
	}

	/** Seta a ênfase desta matriz curricular. 
	 * @param enfase
	 */
	public void setEnfase(Enfase enfase) {
		this.enfase = enfase;
	}

	/** Indica se a matriz curricular possui ênfase. 
	 * @return
	 */
	public Boolean getPossuiEnfase() {
		return possuiEnfase;
	}

	/** Seta se a matriz curricular possui ênfase.
	 * @param possuiEnfase
	 */
	public void setPossuiEnfase(Boolean possuiEnfase) {
		this.possuiEnfase = possuiEnfase;
	}
}
