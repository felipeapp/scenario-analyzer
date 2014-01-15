/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * O curr�culo de um curso � composto de um elenco de disciplinas caracterizadas
 * por um c�digo, denomina��o, carga hor�ria, n�mero de cr�ditos, ementa e
 * bibliografia b�sica, agrupadas nas �reas de concentra��o e de dom�nio conexo,
 * de acordo com o respectivo conte�do program�tico.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "curriculo", schema = "graduacao")
public class Curriculo implements Validatable {

	// constantes
	/** Constante que define o valor m�ximo para o prazo de conclus�o do mestrado. */
	public static final int PRAZO_MAXIMO_CONCLUSAO_MESTRADO = 36;
	/** Constante que define o valor m�ximo para o prazo de conclus�o do doutorado. */
	public static final int PRAZO_MAXIMO_CONCLUSAO_DOUTORADO = 60;
	/** Constante que define o valor m�ximo para o prazo de conclus�o do curso de gradua��o. */
	public static final int PRAZO_MAXIMO_CONCLUSAO_GRADUACAO = 20;
	/** Constante que define a situa��o ABERTA do curr�culo. */
	public static final int SITUACAO_ABERTO = 2;
	/** Constate que define a situa��o FECHADA do curr�culo. */
	public static final int SITUACAO_FECHADO = 1;
	/** Constante que define o valor do ano m�nimo de entrada em vigor do curr�culo. */
	private static final int ANO_MINIMO_ENTRADA_EM_VIGOR = 1930;

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })	
	@Column(name = "id_curriculo", nullable = false)
	private int id;

	/** C�digo do curr�culo, normalmente num�rico, com dois d�gitos, precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...). */
	private String codigo;

	/** N�mero de cr�ditos m�nimo, m�ximo e ideal por semestre que um discente pode realizar
	 *  matr�cula em turmas em um semestre. 
	 */
	private Integer crMaximoSemestre, crMinimoSemestre, crIdealSemestre;

	/** Carga hor�ria m�nima que um discente deve cumprir em um semestre */
	@Column(name = "ch_minima_semestre")
	private Integer chMinimaSemestre;

	/** Carga hor�ria m�xima que um discente deve cumprir em um semestre */
	@Column(name = "ch_maxima_semestre")
	private Integer chMaximaSemestre;
	
	/** Ano inicial de vigora��o do curr�culo. */
	private Integer anoEntradaVigor;
	/** Per�odo inicial de vigora��o do curr�culo. */
	private Integer periodoEntradaVigor;

	/** Prazo m�nimo de conclus�o do curr�culo.
	 * Para cursos de stricto sensu, esses "semestres" s�o expressos em MESES. */
	@Column(name = "semestre_conclusao_minimo")
	private Integer semestreConclusaoMinimo;

	/** Prazo ideal (regulamentar, no caso de cursos de stricto sensu) de conclus�o do curr�culo.
	 * Para cursos de stricto sensu, esses "semestres" s�o expressos em MESES. */
	@Column(name = "semestre_conclusao_ideal")
	private Integer semestreConclusaoIdeal;

	/** Prazo m�ximo de conclus�o do curr�culo.
	 * Para cursos de stricto sensu, esses "semestres" s�o expressos em MESES. */
	@Column(name = "semestre_conclusao_maximo")
	private Integer semestreConclusaoMaximo;

	/** Prazo m�nimo de conclus�o do curr�culo, expressos em MESES. */
	@Column(name = "meses_conclusao_minimo")
	private Integer mesesConclusaoMinimo;

	/** Prazo ideal de conclus�o do curr�culo, expressos em MESES. */
	@Column(name = "meses_conclusao_ideal")
	private Integer mesesConclusaoIdeal;

	/** Prazo m�ximo de conclus�o do curr�culo, expressos em MESES. */
	@Column(name = "meses_conclusao_maximo")
	private Integer mesesConclusaoMaximo;

	/** Lista de componentes curriculares do curr�culo. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "curriculo", fetch=FetchType.LAZY)
	private List<CurriculoComponente> curriculoComponentes;

	/** Lista de componentes curriculares optativas do curr�culo.  */
	@Transient //@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="curriculo")
	private List<OptativaCurriculoSemestre> optativasCurriculoSemestre;
	
	/** Matriz curricular ao qual este curr�culo pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_matriz")
	private MatrizCurricular matriz;

	/** Curso ao qual este curr�culo pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;

	/** Carga Hor�ria m�nima de componentes optativos que o discente deve cursar. */
	@Column(name = "ch_optativas_minima")
	private Integer chOptativasMinima;

	/** Carga Hor�ria total de componentes que o discente deve cursar. */
	@Column(name = "ch_total_minima")
	private int chTotalMinima;

	/** N�mero de cr�ditos m�nimos de componentes que o discente deve cursar. */
	@Column(name = "cr_total_minimo")
	private int crTotalMinimo;

	/** Carga Hor�ria de Atividades Obrigat�rias que o discente deve cursar. */
	@Column(name = "ch_atividade_obrigatoria")
	private int chAtividadeObrigatoria;

	/** Carga Hor�ria de Atividades N�o Obrigat�rias que o discente deve cursar. */
	@Column(name = "ch_nao_atividade_obrigatoria")
	private int chNaoAtividadeObrigatoria;

	/** N�mero de cr�ditos de Atividades Obrigat�rias que o discente deve cursar. */
	@Column(name = "cr_nao_atividade_obrigatorio")
	private int crNaoAtividadeObrigatorio;

	/** Carga Hor�ria m�xima de componente eletivos no curr�culo. */
	@Column(name = "max_eletivos")
	private int maxEletivos;	
	
	/** Indica a situa��o do curr�culo (aberto ou fechado para edi��o). */
	private Integer situacao;

	/** Cole��o de �nfases do curr�culo.  */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "curriculo")
	private Collection<GrupoOptativas> enfases;

	/** Indica se o curr�culo est� ativo, permitindo sua utiliza��o no SIGAA. */
	@Column(name = "ativo")
	private Boolean ativo = true;
	
	/** N�mero de cr�dito total de aulas te�ricas obrigat�rias. */
	@Transient
	private int crTeoricos;
	/** N�mero de cr�dito total de aulas pr�ticas obrigat�rias. */
	@Transient
	private int crPraticos;
	/** Carga Hor�ria total de aulas te�ricas obrigat�rias. */
	@Transient
	private int chTeoricos;
	/** Carga Hor�ria total de aulas pr�ticas obrigat�rias. */
	@Transient
	private int chPraticos;
	/** Carga Hor�ria de atividades acad�mica especificas obrigat�rias. */
	@Transient
	private int chAAE;

	/** Registro de Entrada do usu�rio de cadastrou o curr�culo. */	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data de cadastro do curr�culo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro de Entrada do usu�rio que modificou o curr�culo. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data de altera��o do curr�culo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;
	
	/** Quantidade de alunos no curr�culo. */
	@Transient
	private long qtdAlunos;
	
	/** Refer�ncia ao componente curricular do Trabalho de Conclus�o de Curso Definitivo para este curr�culo. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_componente_tcc_definitivo")
	private ComponenteCurricular tccDefinitivo;
	
	/** Construtor padr�o.  */
	public Curriculo() {
	}

	/** Construtor parametrizado. */
	public Curriculo(int idCurriculo) {
		id = idCurriculo;
	}
	
	/** Retorna a cole��o de �nfases do curr�culo. 
	 * @return
	 */
	public Collection<GrupoOptativas> getEnfases() {
		return enfases;
	}

	/** Seta a cole��o de �nfases do curr�culo.
	 * @param enfases
	 */
	public void setEnfases(Collection<GrupoOptativas> enfases) {
		this.enfases = enfases;
	}

	/** Retorna a lista de componentes curriculares do curr�culo.
	 * @return
	 */
	public List<CurriculoComponente> getCurriculoComponentes() {
		return curriculoComponentes;
	}

	/** Seta a lista de componentes curriculares do curr�culo. 
	 * @param curriculoComponentes
	 */
	public void setCurriculoComponentes(List<CurriculoComponente> curriculoComponentes) {
		this.curriculoComponentes = curriculoComponentes;
	}

	/** Retorna o curso ao qual este curr�culo pertence.
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso ao qual este curr�culo pertence. 
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna o ano inicial de vigora��o do curr�culo. 
	 * @return
	 */
	public Integer getAnoEntradaVigor() {
		return anoEntradaVigor;
	}

	/** Seta o ano inicial de vigora��o do curr�culo. 
	 * @param anoEntradaVigor
	 */
	public void setAnoEntradaVigor(Integer anoEntradaVigor) {
		this.anoEntradaVigor = anoEntradaVigor;
	}

	/** Retorna o c�digo do curr�culo, normalmente num�rico, com dois d�gitos, precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...).
	 * @return
	 */
	public String getCodigo() {
		return codigo;
	}

	/** Seta o c�digo do curr�culo, normalmente num�rico, com dois d�gitos, precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...). 
	 * @param codigo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * Retorna o n�mero de cr�ditos m�nimo, m�ximo e ideal por semestre que um
	 * discente pode realizar matr�cula em turmas em um semestre.
	 * 
	 * @return
	 */
	public Integer getCrIdealSemestre() {
		return crIdealSemestre;
	}

	/**
	 * Seta o n�mero de cr�ditos m�nimo, m�ximo e ideal por semestre que um
	 * discente pode realizar matr�cula em turmas em um semestre.
	 * 
	 * @param crIdealSemestre
	 */
	public void setCrIdealSemestre(Integer crIdealSemestre) {
		this.crIdealSemestre = crIdealSemestre;
	}

	/**
	 * Retorna o n�mero de cr�ditos m�nimo, m�ximo e ideal por semestre que um
	 * discente pode realizar matr�cula em turmas em um semestre.
	 * 
	 * @return
	 */
	public Integer getCrMaximoSemestre() {
		return crMaximoSemestre;
	}

	/**
	 * Seta o n�mero de cr�ditos m�nimo, m�ximo e ideal por semestre que um
	 * discente pode realizar matr�cula em turmas em um semestre.
	 * 
	 * @param crMaximoSemestre
	 */
	public void setCrMaximoSemestre(Integer crMaximoSemestre) {
		this.crMaximoSemestre = crMaximoSemestre;
	}

	/**
	 * Retorna o n�mero de cr�ditos m�nimo, m�ximo e ideal por semestre que um
	 * discente pode realizar matr�cula em turmas em um semestre.
	 * 
	 * @return
	 */
	public Integer getCrMinimoSemestre() {
		return crMinimoSemestre;
	}

	/**
	 * Seta o n�mero de cr�ditos m�nimo, m�ximo e ideal por semestre que um
	 * discente pode realizar matr�cula em turmas em um semestre.
	 * 
	 * @param crMinimoSemestre
	 */
	public void setCrMinimoSemestre(Integer crMinimoSemestre) {
		this.crMinimoSemestre = crMinimoSemestre;
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

	/** Retorna a matriz curricular ao qual este curr�culo pertence.
	 * @return
	 */
	public MatrizCurricular getMatriz() {
		return matriz;
	}

	/** Seta a matriz curricular ao qual este curr�culo pertence. 
	 * @param matriz
	 */
	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	/** Retorna o per�odo inicial de vigora��o do curr�culo.
	 * @return
	 */
	public Integer getPeriodoEntradaVigor() {
		return periodoEntradaVigor;
	}

	/** Seta o per�odo inicial de vigora��o do curr�culo. 
	 * @param periodoEntradaVigor
	 */
	public void setPeriodoEntradaVigor(Integer periodoEntradaVigor) {
		this.periodoEntradaVigor = periodoEntradaVigor;
	}

	/**
	 * Retorna o prazo ideal (regulamentar, no caso de cursos de stricto sensu)
	 * de conclus�o do curr�culo. Para cursos de stricto sensu, esses
	 * "semestres" s�o expressos em MESES.
	 * 
	 * @return
	 */
	public Integer getSemestreConclusaoIdeal() {
		return semestreConclusaoIdeal;
	}

	/**
	 * Seta o prazo ideal (regulamentar, no caso de cursos de stricto sensu) de
	 * conclus�o do curr�culo. Para cursos de stricto sensu, esses "semestres"
	 * s�o expressos em MESES.
	 * 
	 * @param semestreConclusaoIdeal
	 */
	public void setSemestreConclusaoIdeal(Integer semestreConclusaoIdeal) {
		this.semestreConclusaoIdeal = semestreConclusaoIdeal;
	}

	/**
	 * Retorna o prazo m�ximo de conclus�o do curr�culo. Para cursos de stricto
	 * sensu, esses "semestres" s�o expressos em MESES.
	 * 
	 * @return
	 */
	public Integer getSemestreConclusaoMaximo() {
		return semestreConclusaoMaximo;
	}

	/**
	 * Seta o prazo m�ximo de conclus�o do curr�culo. Para cursos de stricto
	 * sensu, esses "semestres" s�o expressos em MESES.
	 * 
	 * @param semestreConclusaoMaximo
	 */
	public void setSemestreConclusaoMaximo(Integer semestreConclusaoMaximo) {
		this.semestreConclusaoMaximo = semestreConclusaoMaximo;
	}

	/**
	 * Retorna o prazo m�nimo de conclus�o do curr�culo. Para cursos de stricto
	 * sensu, esses "semestres" s�o expressos em MESES.
	 * 
	 * @return
	 */
	public Integer getSemestreConclusaoMinimo() {
		return semestreConclusaoMinimo;
	}

	/**
	 * Seta o prazo m�nimo de conclus�o do curr�culo. Para cursos de stricto
	 * sensu, esses "semestres" s�o expressos em MESES.
	 * 
	 * @param semestreConclusaoMinimo
	 */
	public void setSemestreConclusaoMinimo(Integer semestreConclusaoMinimo) {
		this.semestreConclusaoMinimo = semestreConclusaoMinimo;
	}

	/**
	 * Valida os dados do curr�culo: curso, anoEntradaVigor,
	 * periodoEntradaVigor, semestreConclusaoMinimo, semestreConclusaoIdeal,
	 * semestreConclusaoMaximo, chOptativasMinima, semestreConclusaoMaximo,
	 * crIdealSemestre, crMaximoSemestre, crMinimoSemestre, chMinimaSemestre,
	 */
	public ListaMensagens validate() {
		// n�o possui nenhuma regra para validar os componentes curriculares
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(codigo, "C�digo", erros);
		if (curso.isStricto())
			ValidatorUtil.validateRequired(curso.getUnidade(), "Programa", erros);
		
		if(curso.getId() != 0 && curso.isResidencia())
			ValidatorUtil.validateRequired(curso.getUnidade(), "Programa", erros);
		ValidatorUtil.validateRequired(curso, "Curso", erros);

		ValidatorUtil.validateRequired(anoEntradaVigor, "Ano de Implanta��o", erros);
		ValidatorUtil.validateMinValue(anoEntradaVigor, ANO_MINIMO_ENTRADA_EM_VIGOR, "Ano de Implanta��o", erros);
		ValidatorUtil.validateRequired(periodoEntradaVigor, "Per�odo de Implanta��o", erros);
		ValidatorUtil.validateMinValue(periodoEntradaVigor, 1, "Per�odo de Implanta��o", erros);
		ValidatorUtil.validateMaxValue(periodoEntradaVigor, 2, "Per�odo de Implanta��o", erros);
		
		// resid�ncia em sa�de n�o obriga carga hor�ria
		if (isEmpty(curso) || !curso.isResidencia()) { 
			ValidatorUtil.validateRequired(chOptativasMinima, "Carga Hor�ria Optativa M�nima", erros);
			ValidatorUtil.validateMinValue(chOptativasMinima, 0, "Carga Hor�ria Optativa M�nima", erros);
			ValidatorUtil.validateRequired(chMinimaSemestre, "Carga Hor�ria Por Per�odo Letivo M�nima", erros);
			ValidatorUtil.validateMinValue(chMinimaSemestre, 0, "Carga Hor�ria Por Per�odo Letivo M�nima", erros);
		}
		
		ValidatorUtil.validateRequired(semestreConclusaoMinimo, "Prazo para Conclus�o M�nimo", erros);
		ValidatorUtil.validateRequired(semestreConclusaoIdeal, "Prazo para Conclus�o " + (curso.isGraduacao() ? "M�dio" : "Regulamentar"), erros);
		ValidatorUtil.validateRequired(semestreConclusaoMaximo, "Prazo para Conclus�o M�ximo", erros);
		
		// intervalos de semestre de conclus�o
//		ValidatorUtil.validateRange(semestreConclusaoIdeal, semestreConclusaoMinimo, semestreConclusaoMaximo, "Per�odo de Conclus�o M�dio", erros);
		
		if(semestreConclusaoMaximo!=null && semestreConclusaoIdeal!= null && semestreConclusaoIdeal > semestreConclusaoMaximo) {
			erros.addErro("O Prazo Para Conclus�o Regulamentar deve ser menor ou igual que o Prazo Para Conclus�o M�ximo.");
		}
		
		//ValidatorUtil.validateMinValue(semestreConclusaoMaximo, semestreConclusaoIdeal, "Prazo para Conclus�o M�ximo", erros);
		ValidatorUtil.validateMaxValue(semestreConclusaoMinimo, semestreConclusaoIdeal, "Prazo para Conclus�o M�nimo", erros);


		if (curso.isGraduacao()) {
			ValidatorUtil.validateRequired(matriz, "Matriz Curricular", erros);

			// Valida��o de limites de cr�ditos por semestre
			ValidatorUtil.validateRange(crIdealSemestre, crMinimoSemestre, crMaximoSemestre, "N�mero ideal de cr�ditos por per�odo letivo", erros);
			ValidatorUtil.validateMinValue(crMaximoSemestre, crIdealSemestre, "N�mero m�ximo de cr�ditos por per�odo letivo", erros);
			ValidatorUtil.validateMaxValue(crMinimoSemestre, crIdealSemestre, "N�mero m�nimo de cr�ditos por per�odo letivo", erros);

			// Valida��o de limites de cargas hor�rias
			ValidatorUtil.validateRequired(chMinimaSemestre, "Carga Hor�ria m�nima por per�odo letivo", erros);
			ValidatorUtil.validateMinValue(chMinimaSemestre , 0, "Carga Hor�ria m�nima por per�odo letivo", erros);
//			ValidatorUtil.validateRequired(chMaximaSemestre, "Carga Hor�ria m�xima por per�odo letivo", mensagens);
//			ValidatorUtil.validateMinValue(chMaximaSemestre , chMinimaSemestre, "Carga Hor�ria m�xima por per�odo letivo", mensagens);
			
			
			if (getSemestreConclusaoIdeal() != null && getSemestreConclusaoIdeal() > PRAZO_MAXIMO_CONCLUSAO_GRADUACAO) {
				erros.addErro("Valor muito alto para n�mero de semestres");
			}

		} else {
			// resid�ncia em sa�de n�o obriga carga hor�ria
			if (curso.getId() != 0 && !curso.isResidencia()) {
				
				ValidatorUtil.validateRequired(chMinimaSemestre , "Carga Hor�ria m�nima por per�odo letivo", erros);
				if (curso.getNivel() == NivelEnsino.DOUTORADO)
					ValidatorUtil.validateMaxValue(semestreConclusaoMaximo, PRAZO_MAXIMO_CONCLUSAO_DOUTORADO, "Prazo para Conclus�o M�ximo", erros);
				else if (curso.getNivel() == NivelEnsino.MESTRADO)
					ValidatorUtil.validateMaxValue(semestreConclusaoMaximo, PRAZO_MAXIMO_CONCLUSAO_MESTRADO, "Prazo para Conclus�o M�ximo", erros);
	
				// Valida��o de limites de cr�ditos por semestre
				ValidatorUtil.validateMinValue(crMaximoSemestre, crIdealSemestre, "N�mero m�ximo de cr�ditos por per�odo letivo", erros);
				ValidatorUtil.validateMaxValue(crMinimoSemestre, crIdealSemestre, "N�mero m�nimo de cr�ditos por per�odo letivo", erros);
				
			}
		}

		return erros;
	}

	/** Indica se o curr�culo � de cursos de gradua��o.
	 * @return
	 */
	@Transient
	public boolean isGraduacao() {
		return matriz != null && curso != null && curso.isGraduacao();
	}
	
	/**
	 * Indica se o n�vel de ensino do curr�culo � de Resid�ncia em Sa�de
	 * 
	 * @return
	 */
	@Transient
	public boolean isResidencia() {
		return curso != null && curso.getNivel() == NivelEnsino.RESIDENCIA;
	}

	/** Retorna uma representa��o textual do ano-per�odo de entrada em vigor do curr�culo.
	 * @return
	 */
	@Transient
	public String getAnoPeriodo() {
		return anoEntradaVigor + "." + periodoEntradaVigor;
	}

	/** Retorna uma descri��o textual do curr�culo, informando o c�digo e o ano-per�odo de entrada em vigor.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		return codigo + " - "+  getAnoPeriodo();
	}

	/**
	 * Retorna uma descri��o textual do curr�culo, informando o n�vel de ensino,
	 * no ano-per�odo de entrada em vigor e o c�digo do curr�culo.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoCompleta() {
		StringBuilder desc = new StringBuilder();
		
		if (curso != null && !isEmpty(curso.getNivel()))
			desc.append(NivelEnsino.getDescricao(curso.getNivel()) + " - ");		
		
		desc.append(getAnoPeriodo() + " - " + codigo);
		
		return desc.toString();
	}
	
	/**
	 * Retorna uma descri��o do curr�culo, informando o curso ao qual pertence,
	 * o ano-per�odo de entrada em vigor e o c�digo.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoCursoCurriculo() {
		StringBuilder desc = new StringBuilder();
		
		if (curso != null && !isEmpty(curso.getDescricao()))
			desc.append(curso.getDescricao() + " - ");		
		
		desc.append(getAnoPeriodo() + " - " + codigo);
		
		return desc.toString();
	}	
	
	/** Retorna uma descri��o do curr�culo, informando a matriz curricular, o c�digo e
	 * o ano-per�odo de entrada em vigor do curr�culo.
	 * @return
	 */
	@Transient
	public String getMatrizDescricao() {
		return matriz.getDescricaoMin() + " - "+ codigo + " ("+  getAnoPeriodo()+")";
	}

	/** Retorna a {@link #getDescricao() descri��o} do curr�culo.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

	/** Retorna a carga Hor�ria de Atividades Obrigat�rias que o discente deve cursar.
	 * @return
	 */
	public int getChAtividadeObrigatoria() {
		return chAtividadeObrigatoria;
	}

	/** Seta a carga Hor�ria de Atividades Obrigat�rias que o discente deve cursar. 
	 * @param chAtividadeObrigatoria
	 */
	public void setChAtividadeObrigatoria(int chAtividadeObrigatoria) {
		this.chAtividadeObrigatoria = chAtividadeObrigatoria;
	}

	/** Retorna a carga Hor�ria de Atividades N�o Obrigat�rias que o discente deve cursar.
	 * @return
	 */
	public int getChNaoAtividadeObrigatoria() {
		return chNaoAtividadeObrigatoria;
	}

	/** Seta a carga Hor�ria de Atividades N�o Obrigat�rias que o discente deve cursar. 
	 * @param chNaoAtividadeObrigatoria
	 */
	public void setChNaoAtividadeObrigatoria(int chNaoAtividadeObrigatoria) {
		this.chNaoAtividadeObrigatoria = chNaoAtividadeObrigatoria;
	}

	/** Retorna o n�mero de cr�ditos de Atividades Obrigat�rias que o discente deve cursar.  
	 * @return
	 */
	public int getCrNaoAtividadeObrigatorio() {
		return crNaoAtividadeObrigatorio;
	}

	/** Seta o n�mero de cr�ditos de Atividades Obrigat�rias que o discente deve cursar.
	 * @param crNaoDisciplinaObrigatorio
	 */
	public void setCrNaoAtividadeObrigatorio(int crNaoDisciplinaObrigatorio) {
		crNaoAtividadeObrigatorio = crNaoDisciplinaObrigatorio;
	}

	/** Retorna a carga Hor�ria m�nima de componentes optativos que o discente deve cursar. 
	 * @return the chOptativasMinima
	 */
	public Integer getChOptativasMinima() {
		return chOptativasMinima;
	}

	/** Seta a carga Hor�ria m�nima de componentes optativos que o discente deve cursar.
	 * @param chOptativasMinima the chOptativasMinima to set
	 */
	public void setChOptativasMinima(Integer chOptativasMinima) {
		this.chOptativasMinima = chOptativasMinima;
	}

	/** Retorna a carga Hor�ria total de componentes que o discente deve cursar. 
	 * @return the chTotalMinima
	 */
	public int getChTotalMinima() {
		return chTotalMinima;
	}

	/** seta a carga Hor�ria total de componentes que o discente deve cursar.
	 * @param chTotalMinima 
	 */
	public void setChTotalMinima(int chTotalMinima) {
		this.chTotalMinima = chTotalMinima;
	}

	/** Retorna o n�mero de cr�ditos m�nimos de componentes que o discente deve cursar. 
	 * @return 
	 */
	public int getCrTotalMinimo() {
		return crTotalMinimo;
	}

	/** Seta o n�mero de cr�ditos m�nimos de componentes que o discente deve cursar.
	 * @param crTotalMinimo 
	 */
	public void setCrTotalMinimo(int crTotalMinimo) {
		this.crTotalMinimo = crTotalMinimo;
	}

	/** Calcula o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/** Compara este objeto com o passado por par�metro, verificando se as chaves prim�rias s�o iguais.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Indica a situa��o do curr�culo (aberto ou fechado para edi��o).
	 * 
	 * @return Os poss�veis valores s�o {@link #SITUACAO_ABERTO} e
	 *         {@link #SITUACAO_FECHADO}
	 */
	public Integer getSituacao() {
		return situacao;
	}

	/**
	 * Seta a situa��o do curr�culo (aberto ou fechado para edi��o).
	 * 
	 * @param situacao
	 *            Os poss�veis valores s�o {@link #SITUACAO_ABERTO} e
	 *            {@link #SITUACAO_FECHADO}
	 */
	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}

	/** Indica se o curr�culo est� aberto para edi��o.
	 * @return
	 */
	public boolean isAberto() {
		return situacao != null && situacao == SITUACAO_ABERTO;
	}

	/** Retorna o n�mero de cr�dito total de aulas te�ricas obrigat�rias. 
	 * @return
	 */
	public int getCrTeoricos() {
		return crTeoricos;
	}

	/** Seta o n�mero de cr�dito total de aulas te�ricas obrigat�rias.
	 * @param crTeoricos
	 */
	public void setCrTeoricos(int crTeoricos) {
		this.crTeoricos = crTeoricos;
	}

	/** Retorna o n�mero de cr�dito total de aulas pr�ticas obrigat�rias.
	 * @return
	 */
	public int getCrPraticos() {
		return crPraticos;
	}

	/** Seta o n�mero de cr�dito total de aulas pr�ticas obrigat�rias. 
	 * @param crPraticos
	 */
	public void setCrPraticos(int crPraticos) {
		this.crPraticos = crPraticos;
	}

	/** Retorna a carga Hor�ria total de aulas te�ricas obrigat�rias. 
	 * @return
	 */
	public int getChTeoricos() {
		return chTeoricos;
	}

	/** Seta a carga Hor�ria total de aulas te�ricas obrigat�rias.
	 * @param chTeoricos
	 */
	public void setChTeoricos(int chTeoricos) {
		this.chTeoricos = chTeoricos;
	}

	/** Retorna a carga Hor�ria total de aulas pr�ticas obrigat�rias.
	 * @return
	 */
	public int getChPraticos() {
		return chPraticos;
	}

	/** Seta a carga Hor�ria total de aulas pr�ticas obrigat�rias. 
	 * @param chPraticos
	 */
	public void setChPraticos(int chPraticos) {
		this.chPraticos = chPraticos;
	}

	/** Retorna a Carga Hor�ria de atividades acad�mica especificas obrigat�rias.
	 * @return
	 */
	public int getChAAE() {
		return chAAE;
	}

	/** Seta a Carga Hor�ria de atividades acad�mica especificas obrigat�rias. 
	 * @param chAAE
	 */
	public void setChAAE(int chAAE) {
		this.chAAE = chAAE;
	}

	/** Retorna o Prazo m�ximo de conclus�o do curr�culo, expressos em MESES.
	 * @return
	 */
	public Integer getMesesConclusaoMaximo() {
		return mesesConclusaoMaximo;
	}

	/** Seta o Prazo m�ximo de conclus�o do curr�culo, expressos em MESES. 
	 * @param mesesConclusaoMaximo
	 */
	public void setMesesConclusaoMaximo(Integer mesesConclusaoMaximo) {
		this.mesesConclusaoMaximo = mesesConclusaoMaximo;
	}

	/** Retorna o Prazo m�nimo de conclus�o do curr�culo, expressos em MESES. 
	 * @return
	 */
	public Integer getMesesConclusaoMinimo() {
		return mesesConclusaoMinimo;
	}

	/** Seta o Prazo m�nimo de conclus�o do curr�culo, expressos em MESES.
	 * @param mesesConclusaoMinimo
	 */
	public void setMesesConclusaoMinimo(Integer mesesConclusaoMinimo) {
		this.mesesConclusaoMinimo = mesesConclusaoMinimo;
	}

	/** Retorna o prazo ideal de conclus�o do curr�culo, expressos em MESES. 
	 * @return
	 */
	public Integer getMesesConclusaoIdeal() {
		return mesesConclusaoIdeal;
	}

	/** Seta o prazo ideal de conclus�o do curr�culo, expressos em MESES. 
	 * @param mesesConclusaoIdeal
	 */
	public void setMesesConclusaoIdeal(Integer mesesConclusaoIdeal) {
		this.mesesConclusaoIdeal = mesesConclusaoIdeal;
	}

	/** Retorna a lista de componentes curriculares optativas do curr�culo.
	 * @return
	 */
	public List<OptativaCurriculoSemestre> getOptativasCurriculoSemestre() {
		return optativasCurriculoSemestre;
	}

	/** Seta a lista de componentes curriculares optativas do curr�culo. 
	 * @param optativasCurriculoSemestre
	 */
	public void setOptativasCurriculoSemestre(List<OptativaCurriculoSemestre> optativasCurriculoSemestre) {
		this.optativasCurriculoSemestre = optativasCurriculoSemestre;
	}

	/** Retorna a carga hor�ria m�nima que um discente deve cumprir em um semestre
	 * @return
	 */
	public Integer getChMinimaSemestre() {
		return chMinimaSemestre;
	}

	/** Seta a carga hor�ria m�nima que um discente deve cumprir em um semestre 
	 * @param chMinimaSemestre
	 */
	public void setChMinimaSemestre(Integer chMinimaSemestre) {
		this.chMinimaSemestre = chMinimaSemestre;
	}

	/** Retorna a carga hor�ria m�xima que um discente deve cumprir em um semestre
	 * @return
	 */
	public Integer getChMaximaSemestre() {
		return chMaximaSemestre;
	}

	/** Seta a carga hor�ria m�xima que um discente deve cumprir em um semestre 
	 * @param chMaximaSemestre
	 */
	public void setChMaximaSemestre(Integer chMaximaSemestre) {
		this.chMaximaSemestre = chMaximaSemestre;
	}

	/** Indica se o curr�culo est� ativo, permitindo sua utiliza��o no SIGAA. 
	 * @return
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/** Seta se o curr�culo est� ativo, permitindo sua utiliza��o no SIGAA. 
	 * @param ativo
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o registro de Entrada do usu�rio de cadastrou o curr�culo.
	 * @return
	 */
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	/** Seta o registro de Entrada do usu�rio de cadastrou o curr�culo. 
	 * @param registroCadastro
	 */
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	/** Retorna a data de cadastro do curr�culo.
	 * @return
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data de cadastro do curr�culo. 
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna o registro de Entrada do usu�rio que modificou o curr�culo.
	 * @return
	 */
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o registro de Entrada do usu�rio que modificou o curr�culo. 
	 * @param registroAtualizacao
	 */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Retorna a data de altera��o do curr�culo.
	 * @return
	 */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/** Seta a data de altera��o do curr�culo. 
	 * @param dataAlteracao
	 */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/** Retorna a quantidade de alunos no curr�culo.
	 * @return
	 */
	public long getQtdAlunos() {
		return qtdAlunos;
	}

	/** Seta a quantidade de alunos no curr�culo. 
	 * @param qtdAlunos
	 */
	public void setQtdAlunos(long qtdAlunos) {
		this.qtdAlunos = qtdAlunos;
	}

	/** Retorna o n�mero m�ximo de componentes eletivos no curr�culo.
	 * @return
	 */	
	public int getMaxEletivos() {
		return maxEletivos;
	}

	/**
	 * Seta o n�mero m�ximo de componentes eletivos no curr�culo.
	 * @param maxEletivos
	 */
	public void setMaxEletivos(int maxEletivos) {
		this.maxEletivos = maxEletivos;
	}	
	
	/**
	 * Retorna os componentes curriculares do curr�culo 
	 * 
	 * @return
	 */
	public Collection<ComponenteCurricular> getComponentesCurriculares() {
		Collection<ComponenteCurricular> componentes = new ArrayList<ComponenteCurricular>();
		for(CurriculoComponente curriculoComp : curriculoComponentes) {
			componentes.add(curriculoComp.getComponente());
		}
		return componentes;
	}

	public ComponenteCurricular getTccDefinitivo() {
		return tccDefinitivo;
	}

	public void setTccDefinitivo(ComponenteCurricular tccDefinitivo) {
		this.tccDefinitivo = tccDefinitivo;
	}
	
}
