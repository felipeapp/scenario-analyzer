/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * O currículo de um curso é composto de um elenco de disciplinas caracterizadas
 * por um código, denominação, carga horária, número de créditos, ementa e
 * bibliografia básica, agrupadas nas áreas de concentração e de domínio conexo,
 * de acordo com o respectivo conteúdo programático.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "curriculo", schema = "graduacao")
public class Curriculo implements Validatable {

	// constantes
	/** Constante que define o valor máximo para o prazo de conclusão do mestrado. */
	public static final int PRAZO_MAXIMO_CONCLUSAO_MESTRADO = 36;
	/** Constante que define o valor máximo para o prazo de conclusão do doutorado. */
	public static final int PRAZO_MAXIMO_CONCLUSAO_DOUTORADO = 60;
	/** Constante que define o valor máximo para o prazo de conclusão do curso de graduação. */
	public static final int PRAZO_MAXIMO_CONCLUSAO_GRADUACAO = 20;
	/** Constante que define a situação ABERTA do currículo. */
	public static final int SITUACAO_ABERTO = 2;
	/** Constate que define a situação FECHADA do currículo. */
	public static final int SITUACAO_FECHADO = 1;
	/** Constante que define o valor do ano mínimo de entrada em vigor do currículo. */
	private static final int ANO_MINIMO_ENTRADA_EM_VIGOR = 1930;

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })	
	@Column(name = "id_curriculo", nullable = false)
	private int id;

	/** Código do currículo, normalmente numérico, com dois dígitos, precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...). */
	private String codigo;

	/** Número de créditos mínimo, máximo e ideal por semestre que um discente pode realizar
	 *  matrícula em turmas em um semestre. 
	 */
	private Integer crMaximoSemestre, crMinimoSemestre, crIdealSemestre;

	/** Carga horária mínima que um discente deve cumprir em um semestre */
	@Column(name = "ch_minima_semestre")
	private Integer chMinimaSemestre;

	/** Carga horária máxima que um discente deve cumprir em um semestre */
	@Column(name = "ch_maxima_semestre")
	private Integer chMaximaSemestre;
	
	/** Ano inicial de vigoração do currículo. */
	private Integer anoEntradaVigor;
	/** Período inicial de vigoração do currículo. */
	private Integer periodoEntradaVigor;

	/** Prazo mínimo de conclusão do currículo.
	 * Para cursos de stricto sensu, esses "semestres" são expressos em MESES. */
	@Column(name = "semestre_conclusao_minimo")
	private Integer semestreConclusaoMinimo;

	/** Prazo ideal (regulamentar, no caso de cursos de stricto sensu) de conclusão do currículo.
	 * Para cursos de stricto sensu, esses "semestres" são expressos em MESES. */
	@Column(name = "semestre_conclusao_ideal")
	private Integer semestreConclusaoIdeal;

	/** Prazo máximo de conclusão do currículo.
	 * Para cursos de stricto sensu, esses "semestres" são expressos em MESES. */
	@Column(name = "semestre_conclusao_maximo")
	private Integer semestreConclusaoMaximo;

	/** Prazo mínimo de conclusão do currículo, expressos em MESES. */
	@Column(name = "meses_conclusao_minimo")
	private Integer mesesConclusaoMinimo;

	/** Prazo ideal de conclusão do currículo, expressos em MESES. */
	@Column(name = "meses_conclusao_ideal")
	private Integer mesesConclusaoIdeal;

	/** Prazo máximo de conclusão do currículo, expressos em MESES. */
	@Column(name = "meses_conclusao_maximo")
	private Integer mesesConclusaoMaximo;

	/** Lista de componentes curriculares do currículo. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "curriculo", fetch=FetchType.LAZY)
	private List<CurriculoComponente> curriculoComponentes;

	/** Lista de componentes curriculares optativas do currículo.  */
	@Transient //@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="curriculo")
	private List<OptativaCurriculoSemestre> optativasCurriculoSemestre;
	
	/** Matriz curricular ao qual este currículo pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_matriz")
	private MatrizCurricular matriz;

	/** Curso ao qual este currículo pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;

	/** Carga Horária mínima de componentes optativos que o discente deve cursar. */
	@Column(name = "ch_optativas_minima")
	private Integer chOptativasMinima;

	/** Carga Horária total de componentes que o discente deve cursar. */
	@Column(name = "ch_total_minima")
	private int chTotalMinima;

	/** Número de créditos mínimos de componentes que o discente deve cursar. */
	@Column(name = "cr_total_minimo")
	private int crTotalMinimo;

	/** Carga Horária de Atividades Obrigatórias que o discente deve cursar. */
	@Column(name = "ch_atividade_obrigatoria")
	private int chAtividadeObrigatoria;

	/** Carga Horária de Atividades Não Obrigatórias que o discente deve cursar. */
	@Column(name = "ch_nao_atividade_obrigatoria")
	private int chNaoAtividadeObrigatoria;

	/** Número de créditos de Atividades Obrigatórias que o discente deve cursar. */
	@Column(name = "cr_nao_atividade_obrigatorio")
	private int crNaoAtividadeObrigatorio;

	/** Carga Horária máxima de componente eletivos no currículo. */
	@Column(name = "max_eletivos")
	private int maxEletivos;	
	
	/** Indica a situação do currículo (aberto ou fechado para edição). */
	private Integer situacao;

	/** Coleção de ênfases do currículo.  */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "curriculo")
	private Collection<GrupoOptativas> enfases;

	/** Indica se o currículo está ativo, permitindo sua utilização no SIGAA. */
	@Column(name = "ativo")
	private Boolean ativo = true;
	
	/** Número de crédito total de aulas teóricas obrigatórias. */
	@Transient
	private int crTeoricos;
	/** Número de crédito total de aulas práticas obrigatórias. */
	@Transient
	private int crPraticos;
	/** Carga Horária total de aulas teóricas obrigatórias. */
	@Transient
	private int chTeoricos;
	/** Carga Horária total de aulas práticas obrigatórias. */
	@Transient
	private int chPraticos;
	/** Carga Horária de atividades acadêmica especificas obrigatórias. */
	@Transient
	private int chAAE;

	/** Registro de Entrada do usuário de cadastrou o currículo. */	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data de cadastro do currículo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro de Entrada do usuário que modificou o currículo. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data de alteração do currículo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;
	
	/** Quantidade de alunos no currículo. */
	@Transient
	private long qtdAlunos;
	
	/** Referência ao componente curricular do Trabalho de Conclusão de Curso Definitivo para este currículo. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_componente_tcc_definitivo")
	private ComponenteCurricular tccDefinitivo;
	
	/** Construtor padrão.  */
	public Curriculo() {
	}

	/** Construtor parametrizado. */
	public Curriculo(int idCurriculo) {
		id = idCurriculo;
	}
	
	/** Retorna a coleção de ênfases do currículo. 
	 * @return
	 */
	public Collection<GrupoOptativas> getEnfases() {
		return enfases;
	}

	/** Seta a coleção de ênfases do currículo.
	 * @param enfases
	 */
	public void setEnfases(Collection<GrupoOptativas> enfases) {
		this.enfases = enfases;
	}

	/** Retorna a lista de componentes curriculares do currículo.
	 * @return
	 */
	public List<CurriculoComponente> getCurriculoComponentes() {
		return curriculoComponentes;
	}

	/** Seta a lista de componentes curriculares do currículo. 
	 * @param curriculoComponentes
	 */
	public void setCurriculoComponentes(List<CurriculoComponente> curriculoComponentes) {
		this.curriculoComponentes = curriculoComponentes;
	}

	/** Retorna o curso ao qual este currículo pertence.
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso ao qual este currículo pertence. 
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna o ano inicial de vigoração do currículo. 
	 * @return
	 */
	public Integer getAnoEntradaVigor() {
		return anoEntradaVigor;
	}

	/** Seta o ano inicial de vigoração do currículo. 
	 * @param anoEntradaVigor
	 */
	public void setAnoEntradaVigor(Integer anoEntradaVigor) {
		this.anoEntradaVigor = anoEntradaVigor;
	}

	/** Retorna o código do currículo, normalmente numérico, com dois dígitos, precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...).
	 * @return
	 */
	public String getCodigo() {
		return codigo;
	}

	/** Seta o código do currículo, normalmente numérico, com dois dígitos, precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...). 
	 * @param codigo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * Retorna o número de créditos mínimo, máximo e ideal por semestre que um
	 * discente pode realizar matrícula em turmas em um semestre.
	 * 
	 * @return
	 */
	public Integer getCrIdealSemestre() {
		return crIdealSemestre;
	}

	/**
	 * Seta o número de créditos mínimo, máximo e ideal por semestre que um
	 * discente pode realizar matrícula em turmas em um semestre.
	 * 
	 * @param crIdealSemestre
	 */
	public void setCrIdealSemestre(Integer crIdealSemestre) {
		this.crIdealSemestre = crIdealSemestre;
	}

	/**
	 * Retorna o número de créditos mínimo, máximo e ideal por semestre que um
	 * discente pode realizar matrícula em turmas em um semestre.
	 * 
	 * @return
	 */
	public Integer getCrMaximoSemestre() {
		return crMaximoSemestre;
	}

	/**
	 * Seta o número de créditos mínimo, máximo e ideal por semestre que um
	 * discente pode realizar matrícula em turmas em um semestre.
	 * 
	 * @param crMaximoSemestre
	 */
	public void setCrMaximoSemestre(Integer crMaximoSemestre) {
		this.crMaximoSemestre = crMaximoSemestre;
	}

	/**
	 * Retorna o número de créditos mínimo, máximo e ideal por semestre que um
	 * discente pode realizar matrícula em turmas em um semestre.
	 * 
	 * @return
	 */
	public Integer getCrMinimoSemestre() {
		return crMinimoSemestre;
	}

	/**
	 * Seta o número de créditos mínimo, máximo e ideal por semestre que um
	 * discente pode realizar matrícula em turmas em um semestre.
	 * 
	 * @param crMinimoSemestre
	 */
	public void setCrMinimoSemestre(Integer crMinimoSemestre) {
		this.crMinimoSemestre = crMinimoSemestre;
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

	/** Retorna a matriz curricular ao qual este currículo pertence.
	 * @return
	 */
	public MatrizCurricular getMatriz() {
		return matriz;
	}

	/** Seta a matriz curricular ao qual este currículo pertence. 
	 * @param matriz
	 */
	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	/** Retorna o período inicial de vigoração do currículo.
	 * @return
	 */
	public Integer getPeriodoEntradaVigor() {
		return periodoEntradaVigor;
	}

	/** Seta o período inicial de vigoração do currículo. 
	 * @param periodoEntradaVigor
	 */
	public void setPeriodoEntradaVigor(Integer periodoEntradaVigor) {
		this.periodoEntradaVigor = periodoEntradaVigor;
	}

	/**
	 * Retorna o prazo ideal (regulamentar, no caso de cursos de stricto sensu)
	 * de conclusão do currículo. Para cursos de stricto sensu, esses
	 * "semestres" são expressos em MESES.
	 * 
	 * @return
	 */
	public Integer getSemestreConclusaoIdeal() {
		return semestreConclusaoIdeal;
	}

	/**
	 * Seta o prazo ideal (regulamentar, no caso de cursos de stricto sensu) de
	 * conclusão do currículo. Para cursos de stricto sensu, esses "semestres"
	 * são expressos em MESES.
	 * 
	 * @param semestreConclusaoIdeal
	 */
	public void setSemestreConclusaoIdeal(Integer semestreConclusaoIdeal) {
		this.semestreConclusaoIdeal = semestreConclusaoIdeal;
	}

	/**
	 * Retorna o prazo máximo de conclusão do currículo. Para cursos de stricto
	 * sensu, esses "semestres" são expressos em MESES.
	 * 
	 * @return
	 */
	public Integer getSemestreConclusaoMaximo() {
		return semestreConclusaoMaximo;
	}

	/**
	 * Seta o prazo máximo de conclusão do currículo. Para cursos de stricto
	 * sensu, esses "semestres" são expressos em MESES.
	 * 
	 * @param semestreConclusaoMaximo
	 */
	public void setSemestreConclusaoMaximo(Integer semestreConclusaoMaximo) {
		this.semestreConclusaoMaximo = semestreConclusaoMaximo;
	}

	/**
	 * Retorna o prazo mínimo de conclusão do currículo. Para cursos de stricto
	 * sensu, esses "semestres" são expressos em MESES.
	 * 
	 * @return
	 */
	public Integer getSemestreConclusaoMinimo() {
		return semestreConclusaoMinimo;
	}

	/**
	 * Seta o prazo mínimo de conclusão do currículo. Para cursos de stricto
	 * sensu, esses "semestres" são expressos em MESES.
	 * 
	 * @param semestreConclusaoMinimo
	 */
	public void setSemestreConclusaoMinimo(Integer semestreConclusaoMinimo) {
		this.semestreConclusaoMinimo = semestreConclusaoMinimo;
	}

	/**
	 * Valida os dados do currículo: curso, anoEntradaVigor,
	 * periodoEntradaVigor, semestreConclusaoMinimo, semestreConclusaoIdeal,
	 * semestreConclusaoMaximo, chOptativasMinima, semestreConclusaoMaximo,
	 * crIdealSemestre, crMaximoSemestre, crMinimoSemestre, chMinimaSemestre,
	 */
	public ListaMensagens validate() {
		// não possui nenhuma regra para validar os componentes curriculares
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(codigo, "Código", erros);
		if (curso.isStricto())
			ValidatorUtil.validateRequired(curso.getUnidade(), "Programa", erros);
		
		if(curso.getId() != 0 && curso.isResidencia())
			ValidatorUtil.validateRequired(curso.getUnidade(), "Programa", erros);
		ValidatorUtil.validateRequired(curso, "Curso", erros);

		ValidatorUtil.validateRequired(anoEntradaVigor, "Ano de Implantação", erros);
		ValidatorUtil.validateMinValue(anoEntradaVigor, ANO_MINIMO_ENTRADA_EM_VIGOR, "Ano de Implantação", erros);
		ValidatorUtil.validateRequired(periodoEntradaVigor, "Período de Implantação", erros);
		ValidatorUtil.validateMinValue(periodoEntradaVigor, 1, "Período de Implantação", erros);
		ValidatorUtil.validateMaxValue(periodoEntradaVigor, 2, "Período de Implantação", erros);
		
		// residência em saúde não obriga carga horária
		if (isEmpty(curso) || !curso.isResidencia()) { 
			ValidatorUtil.validateRequired(chOptativasMinima, "Carga Horária Optativa Mínima", erros);
			ValidatorUtil.validateMinValue(chOptativasMinima, 0, "Carga Horária Optativa Mínima", erros);
			ValidatorUtil.validateRequired(chMinimaSemestre, "Carga Horária Por Período Letivo Mínima", erros);
			ValidatorUtil.validateMinValue(chMinimaSemestre, 0, "Carga Horária Por Período Letivo Mínima", erros);
		}
		
		ValidatorUtil.validateRequired(semestreConclusaoMinimo, "Prazo para Conclusão Mínimo", erros);
		ValidatorUtil.validateRequired(semestreConclusaoIdeal, "Prazo para Conclusão " + (curso.isGraduacao() ? "Médio" : "Regulamentar"), erros);
		ValidatorUtil.validateRequired(semestreConclusaoMaximo, "Prazo para Conclusão Máximo", erros);
		
		// intervalos de semestre de conclusão
//		ValidatorUtil.validateRange(semestreConclusaoIdeal, semestreConclusaoMinimo, semestreConclusaoMaximo, "Período de Conclusão Médio", erros);
		
		if(semestreConclusaoMaximo!=null && semestreConclusaoIdeal!= null && semestreConclusaoIdeal > semestreConclusaoMaximo) {
			erros.addErro("O Prazo Para Conclusão Regulamentar deve ser menor ou igual que o Prazo Para Conclusão Máximo.");
		}
		
		//ValidatorUtil.validateMinValue(semestreConclusaoMaximo, semestreConclusaoIdeal, "Prazo para Conclusão Máximo", erros);
		ValidatorUtil.validateMaxValue(semestreConclusaoMinimo, semestreConclusaoIdeal, "Prazo para Conclusão Mínimo", erros);


		if (curso.isGraduacao()) {
			ValidatorUtil.validateRequired(matriz, "Matriz Curricular", erros);

			// Validação de limites de créditos por semestre
			ValidatorUtil.validateRange(crIdealSemestre, crMinimoSemestre, crMaximoSemestre, "Número ideal de créditos por período letivo", erros);
			ValidatorUtil.validateMinValue(crMaximoSemestre, crIdealSemestre, "Número máximo de créditos por período letivo", erros);
			ValidatorUtil.validateMaxValue(crMinimoSemestre, crIdealSemestre, "Número mínimo de créditos por período letivo", erros);

			// Validação de limites de cargas horárias
			ValidatorUtil.validateRequired(chMinimaSemestre, "Carga Horária mínima por período letivo", erros);
			ValidatorUtil.validateMinValue(chMinimaSemestre , 0, "Carga Horária mínima por período letivo", erros);
//			ValidatorUtil.validateRequired(chMaximaSemestre, "Carga Horária máxima por período letivo", mensagens);
//			ValidatorUtil.validateMinValue(chMaximaSemestre , chMinimaSemestre, "Carga Horária máxima por período letivo", mensagens);
			
			
			if (getSemestreConclusaoIdeal() != null && getSemestreConclusaoIdeal() > PRAZO_MAXIMO_CONCLUSAO_GRADUACAO) {
				erros.addErro("Valor muito alto para número de semestres");
			}

		} else {
			// residência em saúde não obriga carga horária
			if (curso.getId() != 0 && !curso.isResidencia()) {
				
				ValidatorUtil.validateRequired(chMinimaSemestre , "Carga Horária mínima por período letivo", erros);
				if (curso.getNivel() == NivelEnsino.DOUTORADO)
					ValidatorUtil.validateMaxValue(semestreConclusaoMaximo, PRAZO_MAXIMO_CONCLUSAO_DOUTORADO, "Prazo para Conclusão Máximo", erros);
				else if (curso.getNivel() == NivelEnsino.MESTRADO)
					ValidatorUtil.validateMaxValue(semestreConclusaoMaximo, PRAZO_MAXIMO_CONCLUSAO_MESTRADO, "Prazo para Conclusão Máximo", erros);
	
				// Validação de limites de créditos por semestre
				ValidatorUtil.validateMinValue(crMaximoSemestre, crIdealSemestre, "Número máximo de créditos por período letivo", erros);
				ValidatorUtil.validateMaxValue(crMinimoSemestre, crIdealSemestre, "Número mínimo de créditos por período letivo", erros);
				
			}
		}

		return erros;
	}

	/** Indica se o currículo é de cursos de graduação.
	 * @return
	 */
	@Transient
	public boolean isGraduacao() {
		return matriz != null && curso != null && curso.isGraduacao();
	}
	
	/**
	 * Indica se o nível de ensino do currículo é de Residência em Saúde
	 * 
	 * @return
	 */
	@Transient
	public boolean isResidencia() {
		return curso != null && curso.getNivel() == NivelEnsino.RESIDENCIA;
	}

	/** Retorna uma representação textual do ano-período de entrada em vigor do currículo.
	 * @return
	 */
	@Transient
	public String getAnoPeriodo() {
		return anoEntradaVigor + "." + periodoEntradaVigor;
	}

	/** Retorna uma descrição textual do currículo, informando o código e o ano-período de entrada em vigor.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		return codigo + " - "+  getAnoPeriodo();
	}

	/**
	 * Retorna uma descrição textual do currículo, informando o nível de ensino,
	 * no ano-período de entrada em vigor e o código do currículo.
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
	 * Retorna uma descrição do currículo, informando o curso ao qual pertence,
	 * o ano-período de entrada em vigor e o código.
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
	
	/** Retorna uma descrição do currículo, informando a matriz curricular, o código e
	 * o ano-período de entrada em vigor do currículo.
	 * @return
	 */
	@Transient
	public String getMatrizDescricao() {
		return matriz.getDescricaoMin() + " - "+ codigo + " ("+  getAnoPeriodo()+")";
	}

	/** Retorna a {@link #getDescricao() descrição} do currículo.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

	/** Retorna a carga Horária de Atividades Obrigatórias que o discente deve cursar.
	 * @return
	 */
	public int getChAtividadeObrigatoria() {
		return chAtividadeObrigatoria;
	}

	/** Seta a carga Horária de Atividades Obrigatórias que o discente deve cursar. 
	 * @param chAtividadeObrigatoria
	 */
	public void setChAtividadeObrigatoria(int chAtividadeObrigatoria) {
		this.chAtividadeObrigatoria = chAtividadeObrigatoria;
	}

	/** Retorna a carga Horária de Atividades Não Obrigatórias que o discente deve cursar.
	 * @return
	 */
	public int getChNaoAtividadeObrigatoria() {
		return chNaoAtividadeObrigatoria;
	}

	/** Seta a carga Horária de Atividades Não Obrigatórias que o discente deve cursar. 
	 * @param chNaoAtividadeObrigatoria
	 */
	public void setChNaoAtividadeObrigatoria(int chNaoAtividadeObrigatoria) {
		this.chNaoAtividadeObrigatoria = chNaoAtividadeObrigatoria;
	}

	/** Retorna o número de créditos de Atividades Obrigatórias que o discente deve cursar.  
	 * @return
	 */
	public int getCrNaoAtividadeObrigatorio() {
		return crNaoAtividadeObrigatorio;
	}

	/** Seta o número de créditos de Atividades Obrigatórias que o discente deve cursar.
	 * @param crNaoDisciplinaObrigatorio
	 */
	public void setCrNaoAtividadeObrigatorio(int crNaoDisciplinaObrigatorio) {
		crNaoAtividadeObrigatorio = crNaoDisciplinaObrigatorio;
	}

	/** Retorna a carga Horária mínima de componentes optativos que o discente deve cursar. 
	 * @return the chOptativasMinima
	 */
	public Integer getChOptativasMinima() {
		return chOptativasMinima;
	}

	/** Seta a carga Horária mínima de componentes optativos que o discente deve cursar.
	 * @param chOptativasMinima the chOptativasMinima to set
	 */
	public void setChOptativasMinima(Integer chOptativasMinima) {
		this.chOptativasMinima = chOptativasMinima;
	}

	/** Retorna a carga Horária total de componentes que o discente deve cursar. 
	 * @return the chTotalMinima
	 */
	public int getChTotalMinima() {
		return chTotalMinima;
	}

	/** seta a carga Horária total de componentes que o discente deve cursar.
	 * @param chTotalMinima 
	 */
	public void setChTotalMinima(int chTotalMinima) {
		this.chTotalMinima = chTotalMinima;
	}

	/** Retorna o número de créditos mínimos de componentes que o discente deve cursar. 
	 * @return 
	 */
	public int getCrTotalMinimo() {
		return crTotalMinimo;
	}

	/** Seta o número de créditos mínimos de componentes que o discente deve cursar.
	 * @param crTotalMinimo 
	 */
	public void setCrTotalMinimo(int crTotalMinimo) {
		this.crTotalMinimo = crTotalMinimo;
	}

	/** Calcula o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/** Compara este objeto com o passado por parâmetro, verificando se as chaves primárias são iguais.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Indica a situação do currículo (aberto ou fechado para edição).
	 * 
	 * @return Os possíveis valores são {@link #SITUACAO_ABERTO} e
	 *         {@link #SITUACAO_FECHADO}
	 */
	public Integer getSituacao() {
		return situacao;
	}

	/**
	 * Seta a situação do currículo (aberto ou fechado para edição).
	 * 
	 * @param situacao
	 *            Os possíveis valores são {@link #SITUACAO_ABERTO} e
	 *            {@link #SITUACAO_FECHADO}
	 */
	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}

	/** Indica se o currículo está aberto para edição.
	 * @return
	 */
	public boolean isAberto() {
		return situacao != null && situacao == SITUACAO_ABERTO;
	}

	/** Retorna o número de crédito total de aulas teóricas obrigatórias. 
	 * @return
	 */
	public int getCrTeoricos() {
		return crTeoricos;
	}

	/** Seta o número de crédito total de aulas teóricas obrigatórias.
	 * @param crTeoricos
	 */
	public void setCrTeoricos(int crTeoricos) {
		this.crTeoricos = crTeoricos;
	}

	/** Retorna o número de crédito total de aulas práticas obrigatórias.
	 * @return
	 */
	public int getCrPraticos() {
		return crPraticos;
	}

	/** Seta o número de crédito total de aulas práticas obrigatórias. 
	 * @param crPraticos
	 */
	public void setCrPraticos(int crPraticos) {
		this.crPraticos = crPraticos;
	}

	/** Retorna a carga Horária total de aulas teóricas obrigatórias. 
	 * @return
	 */
	public int getChTeoricos() {
		return chTeoricos;
	}

	/** Seta a carga Horária total de aulas teóricas obrigatórias.
	 * @param chTeoricos
	 */
	public void setChTeoricos(int chTeoricos) {
		this.chTeoricos = chTeoricos;
	}

	/** Retorna a carga Horária total de aulas práticas obrigatórias.
	 * @return
	 */
	public int getChPraticos() {
		return chPraticos;
	}

	/** Seta a carga Horária total de aulas práticas obrigatórias. 
	 * @param chPraticos
	 */
	public void setChPraticos(int chPraticos) {
		this.chPraticos = chPraticos;
	}

	/** Retorna a Carga Horária de atividades acadêmica especificas obrigatórias.
	 * @return
	 */
	public int getChAAE() {
		return chAAE;
	}

	/** Seta a Carga Horária de atividades acadêmica especificas obrigatórias. 
	 * @param chAAE
	 */
	public void setChAAE(int chAAE) {
		this.chAAE = chAAE;
	}

	/** Retorna o Prazo máximo de conclusão do currículo, expressos em MESES.
	 * @return
	 */
	public Integer getMesesConclusaoMaximo() {
		return mesesConclusaoMaximo;
	}

	/** Seta o Prazo máximo de conclusão do currículo, expressos em MESES. 
	 * @param mesesConclusaoMaximo
	 */
	public void setMesesConclusaoMaximo(Integer mesesConclusaoMaximo) {
		this.mesesConclusaoMaximo = mesesConclusaoMaximo;
	}

	/** Retorna o Prazo mínimo de conclusão do currículo, expressos em MESES. 
	 * @return
	 */
	public Integer getMesesConclusaoMinimo() {
		return mesesConclusaoMinimo;
	}

	/** Seta o Prazo mínimo de conclusão do currículo, expressos em MESES.
	 * @param mesesConclusaoMinimo
	 */
	public void setMesesConclusaoMinimo(Integer mesesConclusaoMinimo) {
		this.mesesConclusaoMinimo = mesesConclusaoMinimo;
	}

	/** Retorna o prazo ideal de conclusão do currículo, expressos em MESES. 
	 * @return
	 */
	public Integer getMesesConclusaoIdeal() {
		return mesesConclusaoIdeal;
	}

	/** Seta o prazo ideal de conclusão do currículo, expressos em MESES. 
	 * @param mesesConclusaoIdeal
	 */
	public void setMesesConclusaoIdeal(Integer mesesConclusaoIdeal) {
		this.mesesConclusaoIdeal = mesesConclusaoIdeal;
	}

	/** Retorna a lista de componentes curriculares optativas do currículo.
	 * @return
	 */
	public List<OptativaCurriculoSemestre> getOptativasCurriculoSemestre() {
		return optativasCurriculoSemestre;
	}

	/** Seta a lista de componentes curriculares optativas do currículo. 
	 * @param optativasCurriculoSemestre
	 */
	public void setOptativasCurriculoSemestre(List<OptativaCurriculoSemestre> optativasCurriculoSemestre) {
		this.optativasCurriculoSemestre = optativasCurriculoSemestre;
	}

	/** Retorna a carga horária mínima que um discente deve cumprir em um semestre
	 * @return
	 */
	public Integer getChMinimaSemestre() {
		return chMinimaSemestre;
	}

	/** Seta a carga horária mínima que um discente deve cumprir em um semestre 
	 * @param chMinimaSemestre
	 */
	public void setChMinimaSemestre(Integer chMinimaSemestre) {
		this.chMinimaSemestre = chMinimaSemestre;
	}

	/** Retorna a carga horária máxima que um discente deve cumprir em um semestre
	 * @return
	 */
	public Integer getChMaximaSemestre() {
		return chMaximaSemestre;
	}

	/** Seta a carga horária máxima que um discente deve cumprir em um semestre 
	 * @param chMaximaSemestre
	 */
	public void setChMaximaSemestre(Integer chMaximaSemestre) {
		this.chMaximaSemestre = chMaximaSemestre;
	}

	/** Indica se o currículo está ativo, permitindo sua utilização no SIGAA. 
	 * @return
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/** Seta se o currículo está ativo, permitindo sua utilização no SIGAA. 
	 * @param ativo
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o registro de Entrada do usuário de cadastrou o currículo.
	 * @return
	 */
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	/** Seta o registro de Entrada do usuário de cadastrou o currículo. 
	 * @param registroCadastro
	 */
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	/** Retorna a data de cadastro do currículo.
	 * @return
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data de cadastro do currículo. 
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna o registro de Entrada do usuário que modificou o currículo.
	 * @return
	 */
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o registro de Entrada do usuário que modificou o currículo. 
	 * @param registroAtualizacao
	 */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Retorna a data de alteração do currículo.
	 * @return
	 */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/** Seta a data de alteração do currículo. 
	 * @param dataAlteracao
	 */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/** Retorna a quantidade de alunos no currículo.
	 * @return
	 */
	public long getQtdAlunos() {
		return qtdAlunos;
	}

	/** Seta a quantidade de alunos no currículo. 
	 * @param qtdAlunos
	 */
	public void setQtdAlunos(long qtdAlunos) {
		this.qtdAlunos = qtdAlunos;
	}

	/** Retorna o número máximo de componentes eletivos no currículo.
	 * @return
	 */	
	public int getMaxEletivos() {
		return maxEletivos;
	}

	/**
	 * Seta o número máximo de componentes eletivos no currículo.
	 * @param maxEletivos
	 */
	public void setMaxEletivos(int maxEletivos) {
		this.maxEletivos = maxEletivos;
	}	
	
	/**
	 * Retorna os componentes curriculares do currículo 
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
