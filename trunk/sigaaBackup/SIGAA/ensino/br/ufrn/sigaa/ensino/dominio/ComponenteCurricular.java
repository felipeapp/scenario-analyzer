/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '13/11/2008'
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;
import br.ufrn.sigaa.ensino.latosensu.dominio.ComponenteCursoLato;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/**
 * Entidade que representa uma unidade de estrutura��o did�tico-pedag�gica, correspondendo, por exemplo,
 * a disciplinas, m�dulos, blocos e atividades acad�micas espec�ficas.
 * 
 * @author Henrique
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "componente_curricular", schema = "ensino")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ComponenteCurricular implements PersistDB, Validatable, Comparable<ComponenteCurricular> {

	// Constantes que definem o status inativo do componente.
	/** Constante que define o componente ativo. */
	public static final int COMPONENTE_ATIVO = 0;
	/** Constante que define o componente como desativado. */
	public static final int DESATIVADO = 1;
	/** Constante que define que o componente est� aguardando confirma��o da solicita��o de cria��o do componente para tornar-se ativo. */
	public static final int AGUARDANDO_CONFIRMACAO = 2;
	/** Constante que define o componente n�o est� ativo uma vez que a solicita��o de cria��o do componente curricular foi negada. */
	public static final int CADASTRO_NEGADO = 3;

	// Atributos
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="ensino.componente_seq") }) 	
	@Column(name = "id_disciplina", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** A unidade ao qual o componente pertence. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade unidade = new Unidade();

	/** Os detalhes atuais do componente. */
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_detalhe")
	private ComponenteDetalhes detalhes = new ComponenteDetalhes();

	/** Conte�do das aulas das disciplinas de ensino a dist�ncia. */
	@OneToMany(mappedBy="disciplina", fetch=FetchType.LAZY)
	private Collection<ItemPrograma> itemPrograma;

	/** C�digo do componente. */
	private String codigo;

	/** N�vel de ensino ao qual o componente pertence. */
	@Column(name = "nivel", unique = false, nullable = true, insertable = true, updatable = true, length = 1)
	private char nivel;

	/** Quantidade m�xima de matr�culas que um discente pode realizar neste componente. */
	@Column(name = "qtd_max_matriculas")
	private int qtdMaximaMatriculas = 1;

	/** Programa atual do componente. */
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_programa")
	private ComponenteCurricularPrograma programa = new ComponenteCurricularPrograma();

	/** Tipo do componente. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_componente")
	private TipoComponenteCurricular tipoComponente = new TipoComponenteCurricular();

	/** Forma de participa��o dos docentes e discentes. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forma_participacao")
	private FormaParticipacaoAtividade formaParticipacao = new FormaParticipacaoAtividade();
	
	/** Tipo da atividade. Apenas os componentes do tipo atividade tem este atributo. */
	@ManyToOne (fetch = FetchType.EAGER )
	@JoinColumn(name = "id_tipo_atividade")
	private TipoAtividade tipoAtividade = new TipoAtividade();

	/** Tipo da atividade complementar. Apenas o componente do tipo atividade complementar tem este atributo. */
	@ManyToOne (fetch = FetchType.EAGER )
	@JoinColumn(name = "id_tipo_atividade_complementar")
	private TipoAtividadeComplementar tipoAtividadeComplementar = new TipoAtividadeComplementar();

	/** N�mero de unidades que o componente tem, utilizado para gerar a planilha de notas dos alunos matriculados */
	@Column(name = "num_unidades")
	private Integer numUnidades; // unidades de avalia��o

	/** Indica se este componente estar� dispon�vel para que os alunos realizem matr�cula online. */
	private boolean matriculavel = true;

	/** Indica se  o componente precisa de m�dia final. Caso n�o precise o resultado � apenas aprovado ou reprovado. */
	private boolean necessitaMediaFinal = true;

	/** Data de cadastro do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usu�rio que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data da �ltima atualiza��o do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Registro entrada do usu�rio que realizou a �ltima atualiza��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Conjunto de componentes por curso de lato sensu, para indicar a quais cursos Lato o componente pertence. */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "disciplina")
	private Set<ComponenteCursoLato> componentesCursoLato = new HashSet<ComponenteCursoLato>(0);

	/** Bibliografia indicada para estudo do componente curricular. Usado em lato-sensu, nos demais casos, dever� haver o programa do componente. */
	@Column(name = "bibliografia")
	private String bibliografia;

	/** Indica o motivo do componente estar inativo: pode ter sido removido, tirado de circula��o, ou ainda aguardando confirma��o de cadastro do CDP. */
	@Column(name = "status_inativo")
	private Integer statusInativo;

	/** Caso o componentes seja do tipo SubUnidade, esse atributo � uma refer�ncia ao seu bloco respons�vel. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_bloco_subunidade")
	private ComponenteCurricular blocoSubUnidade;

	/** Sub-unidades para componentes do tipo bloco */
	@OneToMany(mappedBy = "blocoSubUnidade", fetch=FetchType.LAZY)
	private List<ComponenteCurricular> subUnidades;

	/** Indica se o componente � ativo ou n�o */
	@CampoAtivo(true)
	private boolean ativo = true;

	/** Indica se o chefe pode criar turma deste componente sem que seja necess�rio uma solicita��o de turma. */
	@Column(name = "turmas_sem_solicitacao")
	private boolean turmasSemSolicitacao;

	/**
	 * Indica que o componente possui subturmas e ter� um tratamento
	 * diferenciado nas opera��es que envolvem turmas como solicita��o, cria��o,
	 * consolida��o, turma virtual, etc
	 */
	@Column(name = "aceita_subturma")
	private boolean aceitaSubturma = false;
	
	/** Indica se este componente ser� exclu�do da Avalia��o Institucional. */ 
	@Column(name = "excluir_avaliacao_institucional")
	private Boolean excluirAvaliacaoInstitucional = false;
	
	/** C�digo utilizado para saber se o componente foi migrado do PontoA. */
	@Column(name = "codmergpa")
	private String codmergpa;
	
	/** Curso a qual o componente faz parte. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	private Curso curso;
	
	/** Campo utilizando para armazenar o nome do curso, quando o componente curricular for cadastrado para um novo Curso. */
	@Column(name = "curso_novo")
	private String cursoNovo; 
	
	/** Indica se o componente curricular ter� o conte�do vari�vel.
	 *  Utilizado como crit�rio para decidir se vai ser poss�vel duplicidade de matr�cula. */
	@Column(name = "conteudo_variavel")
	private boolean conteudoVariavel = false;
	
	// Atributos Transientes
	/** Indica, no hist�rico, se o discente est� matriculado em um componente pendente. Este atributo � transiente. */
	@Transient
	private boolean matriculado;

	/** Indica, no hist�rico, se o discente est� matriculado em um componente equivalente. Este atributo � transiente. */
	@Transient
	private boolean matriculadoEmEquivalente;

	/** Cole��o de componentes curriculares que possuem este componente curricular como equivalente. Este atributo � transiente. */
	@Transient
	private Collection<ComponenteCurricular> inversosEquivalentes;
	
	/** Cole��o de componentes curriculares que possuem este componente curricular como pr�-requisito. Este atributo � transiente. */
	@Transient
	private Collection<ComponenteCurricular> inversosPreRequisitos;
	
	/** Cole��o de componentes curriculares que possuem este componente curricular como c�-requisito. Este atributo � transiente. */
	@Transient
	private Collection<ComponenteCurricular> inversosCoRequisitos;
	
	/** Cole��o das equival�ncias especificas do componentes curricular. Este atributo � transiente. */
	@Transient
	private Collection<EquivalenciaEspecifica> equivalenciaEspecifica;

	/** Cole��o das equival�ncias especificas inversas do componentes curricular. Este atributo � transiente. */
	@Transient
	private Collection<EquivalenciaEspecifica> inversosEquivalenciaEspecifica;
	
	/** Cole��o das express�es espec�ficas de curriculo do componentes curricular. Este atributo � transiente. */
	@Transient
	private Collection<ExpressaoComponenteCurriculo> expressoesEspecificaCurriculo;
	
	/** Quantidade de horas/cr�dito deste componente. Este atributo � transiente. */
	@Transient
	private int horasCredito;
	
	/** Auxilia no aproveitamento de componente, para realizar o cancelamento caso j� esteja matriculado */
	@Transient
	private boolean permiteCancelarMatricula;	
	
	/** Auxilia no cadastro do componente, para realizar a convers�o cr�ditos/carga hor�ria */
	@Transient
	private ParametrosGestoraAcademica parametros;
	
	/** Informa temporariamente se o componente curricular est� integralizado. */
	@Transient
	private boolean integralizado;
	
	/** Informa temporariamente a carga hor�ria contabilizada para esse componente. */
	@Transient
	private int cargaHorariaContabilizada;
	
	/** Modalidade de educa��o para o qual o componente curricular ser� usado. */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_modalidade_educacao")
	private ModalidadeEducacao modalidadeEducacao = new ModalidadeEducacao();
	
	/** Define o n�mero m�ximo de docentes simult�neos na turma. */
	@Column(name="num_max_docentes")
	private int numMaxDocentes = 1;
	
	// Construtores
	/** Construtor Padr�o */
	public ComponenteCurricular() {
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public ComponenteCurricular(int id) {
		this.id = id;
	}

	/** Construtor parametrizado.
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param nivel
	 * @param unidade
	 */
	public ComponenteCurricular(String nomeDisciplina, int chTotal, char nivel,
			int unidade) {
		this.detalhes.setNome(nomeDisciplina);
		this.detalhes.setChTotal(chTotal);
		this.nivel = nivel;
		this.unidade.setId(unidade);
	}

	/** Construtor parametrizado.
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param crAula
	 * @param crLaboratorio
	 * @param crEstagio
	 * @param nivel
	 * @param unidade
	 */
	public ComponenteCurricular(String nomeDisciplina, int chTotal, int crAula,
			int crLaboratorio, int crEstagio, char nivel, int unidade) {
		this.detalhes.setNome(nomeDisciplina);
		this.detalhes.setChTotal(chTotal);
		this.detalhes.setCrAula(crAula);
		this.detalhes.setCrLaboratorio(crLaboratorio);
		this.detalhes.setCrEstagio(crEstagio);
		this.nivel = nivel;
		this.unidade.setId(unidade);
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param nivel
	 */
	public ComponenteCurricular(int id, String codigo, String nomeDisciplina,
			int chTotal, char nivel) {
		this.detalhes.setCodigo(codigo);
		this.codigo = codigo;
		this.detalhes.setNome(nomeDisciplina);
		this.detalhes.setChTotal(chTotal);
		this.nivel = nivel;
		this.id = id;
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param nivel
	 * @param crTotal
	 * @param tipo
	 */
	public ComponenteCurricular(int id, String codigo, String nomeDisciplina,
			int chTotal, char nivel, int crTotal, TipoComponenteCurricular tipo) {
		this.detalhes.setCodigo(codigo);
		this.codigo = codigo;
		this.detalhes.setNome(nomeDisciplina);
		this.detalhes.setChTotal(chTotal);
		this.nivel = nivel;
		this.id = id;
		this.detalhes.setCrTotal(crTotal);
		this.setTipoComponente( tipo );
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nomeDisciplina
	 * @param equivalencia
	 * @param chTotal
	 * @param nivel
	 */
	public ComponenteCurricular(int id, String codigo, String nomeDisciplina,
			 String equivalencia, int chTotal, char nivel) {
		this.detalhes.setCodigo(codigo);
		this.codigo = codigo;
		this.detalhes.setNome(nomeDisciplina);
		this.detalhes.setChTotal(chTotal);
		this.nivel = nivel;
		this.id = id;
		setEquivalencia(equivalencia);
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param nivel
	 * @param idBlocoSubUnidade
	 */
	public ComponenteCurricular(int id, String codigo, String nomeDisciplina,
			int chTotal, char nivel, int idBlocoSubUnidade) {
		this.detalhes.setCodigo(codigo);
		this.codigo = codigo;
		this.detalhes.setNome(nomeDisciplina);
		this.detalhes.setChTotal(chTotal);
		this.nivel = nivel;
		this.id = id;
		this.blocoSubUnidade = new ComponenteCurricular(idBlocoSubUnidade);
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param nivel
	 * @param tipo
	 */
	public ComponenteCurricular(int id, String codigo, String nomeDisciplina,
			int chTotal, char nivel, String tipo) {
		this.setCodigo(codigo);
		this.detalhes.setNome(nomeDisciplina);
		this.detalhes.setChTotal(chTotal);
		this.nivel = nivel;
		this.id = id;
		tipoComponente.setDescricao(tipo);
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param crTotal
	 * @param nivel
	 * @param tipo
	 */
	public ComponenteCurricular(int id, String codigo, String nomeDisciplina,
			int chTotal, int crTotal, char nivel, String tipo) {
		this.setCodigo(codigo);
		this.detalhes.setNome(nomeDisciplina);
		this.detalhes.setChTotal(chTotal);
		this.detalhes.setCrTotal(crTotal);
		this.nivel = nivel;
		this.id = id;
		tipoComponente.setDescricao(tipo);
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param crTotal
	 * @param nivel
	 * @param tipo
	 * @param ativo
	 */
	public ComponenteCurricular(int id, String codigo, String nomeDisciplina,
			int chTotal, int crTotal, char nivel, String tipo, boolean ativo) {
		this(id, codigo, nomeDisciplina, chTotal, crTotal, nivel, tipo);
		this.ativo = ativo;
	}
	

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nomeDisciplina
	 * @param chTotal
	 * @param crTotal
	 * @param nivel
	 * @param unidade
	 */
	public ComponenteCurricular(int id, String codigo, String nomeDisciplina,
			int chTotal, int crTotal, char nivel, Integer unidade) {
		this.detalhes.setNome(nomeDisciplina);
		this.setCodigo(codigo);
		this.detalhes.setChTotal(chTotal);
		this.detalhes.setCrTotal(crTotal);
		this.nivel = nivel;
		this.id = id;
		// Evitar problemas com componentes sem unidade
		if (unidade != null) {
			this.unidade = new Unidade(unidade);
		}
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nome
	 */
	public ComponenteCurricular(int id, String codigo, String nome) {
		setId(id);
		setNome(nome);
		setCodigo(codigo);
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nome
	 * @param equivalencia
	 * @param coRequisito
	 * @param preRequisito
	 */
	public ComponenteCurricular(int id, String codigo, String nome, String equivalencia, String coRequisito, String preRequisito) {
		setId(id);
		setNome(nome);
		setCodigo(codigo);
		detalhes.setEquivalencia(equivalencia);
		detalhes.setCoRequisito(coRequisito);
		detalhes.setPreRequisito(preRequisito);
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nome
	 * @param a
	 * @param s
	 */
	public ComponenteCurricular(int id, String codigo, String nome, boolean ativo,
			Integer statusInativo) {
		setId(id);
		setNome(nome);
		setCodigo(codigo);
		this.ativo = ativo;
		this.statusInativo = statusInativo;
	}

	/** Retorna a chave prim�ria. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return this.id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idDisciplina) {
		this.id = idDisciplina;
	}

	/** Retorna a unidade ao qual o componente pertence. 
	 * @return
	 */
	public Unidade getUnidade() {
		return this.unidade;
	}

	/** Seta a unidade ao qual o componente pertence.
	 * @param unidade
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/** Retorna o nome do componente curricular.
	 * @return
	 */
	@Transient
	public String getNome() {
		return detalhes.getNome();
	}

	/** Seta o nome do componente curricular.
	 * @param nome
	 */
	public void setNome(String nome) {
		detalhes.setNome(nome);
	}

	/** Retorna a carga hor�ria de aulas.
	 * @return
	 */
	@Transient
	public int getChAula() {
		return detalhes.getChAula();
	}

	/** Seta a carga hor�ria de aulas.
	 * @param chAula
	 */
	public void setChAula(int chAula) {
		detalhes.setChAula(chAula);
	}

	/** Retorna a carga hor�ria de laborat�rio.
	 * @return
	 */
	@Transient
	public int getChLaboratorio() {
		return detalhes.getChLaboratorio();
	}

	/** Seta a carga hor�ria de laborat�rio.
	 * @param chLaboratorio
	 */
	public void setChLaboratorio(int chLaboratorio) {
		detalhes.setChLaboratorio(chLaboratorio);
	}

	/** Retorna a carga hor�ria de Est�gio.
	 * @return
	 */
	@Transient
	public int getChEstagio() {
		return detalhes.getChEstagio();
	}

	/** Seta a carga hor�ria de Est�gio.
	 * @param chEstagio
	 */
	public void setChEstagio(int chEstagio) {
		detalhes.setChEstagio(chEstagio);
	}

	/** Retorna a ementa do componente. 
	 * @return
	 */
	@Transient
	public String getEmenta() {
		return detalhes.getEmenta();
	}

	/** Seta a ementa do componente. 
	 * @param ementa
	 */
	public void setEmenta(String ementa) {
		detalhes.setEmenta(ementa);
	}

	/** Retorna o c�digo do componente. 
	 * @return
	 */
	public String getCodigo() {
		return codigo;
	}

	/** Seta o c�digo do componente.
	 * @param codigo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/** Retorna o c�digo e o nome do componente.
	 * @return
	 */
	@Transient
	public String getCodigoNome() {
		return codigo + " - " + detalhes.getNome();
	}

	/** Retorna o n�vel de ensino ao qual o componente pertence. 
	 * @return
	 */
	public char getNivel() {
		return this.nivel;
	}

	/** Seta o n�vel de ensino ao qual o componente pertence.
	 * @param tipo
	 */
	public void setNivel(char tipo) {
		this.nivel = tipo;
	}

	/** Retorna a carga hor�ria total do componente curricular.
	 * @return
	 */
	@Transient
	public int getChTotal() {
		if (detalhes != null)
			return detalhes.getChTotal();
		return 0;
	}
	
	/** Retorna a carga hor�ria total de aulas do componente curricular.
	 * @return
	 */
	@Transient
	public int getChTotalAula() {
		if (detalhes != null)
			return detalhes.getChTotalAula();
		return 0;
	}

	/** Seta a carga hor�ria total do componente curricular.
	 * @param ch
	 */
	public void setChTotal(int ch) {
		this.detalhes.setChTotal(ch);
	}

	/**
	 * Retorna o total de cr�ditos do componente curricular.
	 *
	 * @return
	 */
	@Transient
	public int getCrTotal() {
		try {
			if( (nivel == NivelEnsino.TECNICO || nivel == NivelEnsino.LATO)
				&& detalhes.getCrTotal() == 0 
				&& detalhes.getCrAula() + detalhes.getCrLaboratorio() + detalhes.getCrEstagio() == 0){
				ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(nivel);
				return getChTotal() / parametros.getHorasCreditosAula(); 
			}else{
				if (detalhes.getCrTotal() == 0)
					return (detalhes.getCrAula() + detalhes.getCrLaboratorio() + detalhes.getCrEstagio() + detalhes.getCrEad());
				else
					return detalhes.getCrTotal();
			}
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Retorna a descri��o do n�vel de ensino do componente Curricular. 
	 * @return
	 */
	@Transient
	public String getDescricaoNivelEnsino(){
		return NivelEnsino.getDescricao(nivel);
	}

	/** Compara o ID e o nome deste componente com o passado por par�metro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "detalhes.nome");
	}

	/** Calcula e retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (detalhes != null)
			return HashCodeUtil.hashAll(id, detalhes.getNome());
		else
			return HashCodeUtil.hashAll(id);
	}

	/** Valida os atributos do componente curricular. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {

		ListaMensagens erros = new ListaMensagens();

		if (NivelEnsino.STRICTO == this.nivel)
			ValidatorUtil.validateRequired(unidade, "Programa", erros);
		else
			ValidatorUtil.validateRequired(unidade, "Unidade Respons�vel", erros);
		
		if (nivel != NivelEnsino.TECNICO)
			ValidatorUtil.validateRequired(codigo, "C�digo", erros);
		
		ValidatorUtil.validateRequired(detalhes.getNome(), "Nome", erros);
		

		if (nivel == NivelEnsino.GRADUACAO || nivel == NivelEnsino.STRICTO) {
			ValidatorUtil.validaIntGt(detalhes.getCrEstagio(), "Cr�ditos de Est�gio", erros);
			ValidatorUtil.validaIntGt(detalhes.getCrAula(), "Cr�ditos Te�rico", erros);
			ValidatorUtil.validaIntGt(detalhes.getCrLaboratorio(), "Cr�ditos Pr�tico", erros);
		} else if(nivel != NivelEnsino.RESIDENCIA && nivel != NivelEnsino.MEDIO) {
			ValidatorUtil.validaIntGt(getChEstagio(), "Carga Hor�ria de Est�gio", erros);
			ValidatorUtil.validaIntGt(getChAula(), "Carga Hor�ria de Aula", erros);
			ValidatorUtil.validaIntGt(getChLaboratorio(), "Carga Hor�ria de Laborat�rio", erros);
		}
		
		if (isTrabalhoConclusaoCurso() || isMigradoPontoA()) {
			// PODE ter cr�ditos e carga hor�ria ZERO
		} else if (isDisciplina()) {
			if (getCrTotal() == 0 && !isTecnico()) {
				erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Cr�ditos");
				return erros;
			}
		} else if (!isBloco() && isNecessitaCargaHoraria() && getChTotal() == 0 && !isResidencia()) {
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Carga Hor�ria");
			return erros;
		}

		if ( !isBloco() && isAceitaSubturma() && getChLaboratorio() <= 0) {
			erros.addMensagem(MensagensGraduacao.SOMENTE_TURMA_COM_LABORATORIO_PODE_TER_SUBTURMA, 
					isTecnico() ? "Carga Hor�ria Pr�tica" : "carga hor�ria de cr�ditos Pr�ticos");
		}
		// Se for atividade
		if (isAtividade()) {
			// validar o tipo de atividade
			ValidatorUtil.validateRequired(getTipoAtividade(), "Tipo de Atividade", erros);
			// validar a forma de participa��o			
			ValidatorUtil.validateRequired(getFormaParticipacao(), "Forma Participa��o", erros);
			
			// e se for est�gio ou TCC, validar carga hor�ria do docente
			if (isEstagio()) {
				if (detalhes.getChTotal() > 0)
					ValidatorUtil.validateMaxValue(detalhes.getChDedicadaDocente(), getChTotal(), "Carga Hor�ria Dedicada do Docente", erros);
				else
					validateMinValue(detalhes.getChTotal(), 1, "Carga Hor�ria Total", erros);
			}
		}
		
		if (isGraduacao() && isAtividadeColetiva())
			validateMinValue(detalhes.getChNaoAula(), 1, "Carga Hor�ria de N�o Aula", erros);
		
		if (!isBloco()) {
			if (!NivelEnsino.isAlgumNivelStricto(nivel) && !NivelEnsino.isResidenciaMedica(nivel) ) { // n�o precisa pro t�cnico
				ValidatorUtil.validateMaxLength(detalhes.getEmenta(), 2000, this.isAtividade() ? "Descri��o" : "Ementa", erros);
				ValidatorUtil.validateRequired(detalhes.getEmenta(), this.isAtividade() ? "Descri��o" : "Ementa", erros);
			} else if (!NivelEnsino.isResidenciaMedica(nivel)) {
				ValidatorUtil.validateMaxLength(detalhes.getEmenta(), 2000, this.isAtividade() ? "Descri��o" : "Ementa", erros);
				ValidatorUtil.validateRequired(detalhes.getEmenta(), this.isAtividade() ? "Descri��o" : "Ementa", erros);
				if (isGraduacao() && !(isModulo() || isBloco() || isAtividadeColetiva())){
					ValidatorUtil.validateRequired(bibliografia, "Refer�ncias", erros);					
				}
			}
		}

		if (NivelEnsino.isResidenciaMedica(nivel)) {
			ValidatorUtil.validateMaxLength(detalhes.getEmenta(), 2000, this.isAtividade() ? "Descri��o" : "Ementa", erros);
			ValidatorUtil.validateRequired(detalhes.getEmenta(), this.isAtividade() ? "Descri��o" : "Ementa", erros);
		}

		if (tipoComponente.getId() != 0)
			if (tipoComponente.getId() == TipoComponenteCurricular.ATIVIDADE && nivel != NivelEnsino.STRICTO) {
				ValidatorUtil.validateRequired(tipoAtividade, "Tipo de Atividade", erros);
			}
		
		if ( ( isTecnico() && !isBloco() ) || nivel == NivelEnsino.MEDIO) {
			//tipoComponente.setId(TipoComponenteCurricular.DISCIPLINA);
			ValidatorUtil.validateMaxLength(detalhes.getEmenta(), 2000, this.isAtividade() ? "Descri��o" : "Ementa", erros);
			ValidatorUtil.validateRequired(detalhes.getEmenta(), this.isAtividade() ? "Descri��o" : "Ementa", erros);
		}
		
		if ( ( isTecnico() && !isBloco() ) && getChEstagio() == 0 && getChAula() == 0 && getChLaboratorio() == 0) {
			erros.addErro("Total de carga hor�ria inv�lida.");
		}
		
		if (nivel == NivelEnsino.MEDIO && getChTotal() == 0)
			erros.addErro("Total de carga hor�ria inv�lida.");
		
		if (tipoComponente.getId() == 0 ) {
			erros.addErro("� necess�rio selecionar o Tipo do Componente");
		}
		
		validateMinValue(numMaxDocentes, 1, "N�mero M�ximo de Docentes", erros);

		return erros;
	}

	/** Indica se o componente curricular � do tipo Bloco.
	 * @return
	 */
	@Transient
	public boolean isBloco() {
		return tipoComponente != null && tipoComponente.getId() == TipoComponenteCurricular.BLOCO;
	}

	/** Indica se o componente curricular � subUnidade.
	 * @return
	 */
	@Transient
	public boolean isSubUnidade() {
		return getBlocoSubUnidade() != null;
	}

	/** Indica se o componente curricular � do tipo M�dulo
	 * @return
	 */
	@Transient
	public boolean isModulo() {
		return tipoComponente != null && tipoComponente.getId() == TipoComponenteCurricular.MODULO;
	}

	/** Indica se o componente curricular � do tipo Disciplina.
	 * @return
	 */
	@Transient
	public boolean isDisciplina() {
		return tipoComponente != null && tipoComponente.getId() == TipoComponenteCurricular.DISCIPLINA;
	}

	/** Indica se o componente curricular � do tipo Atividade.
	 * @return
	 */
	@Transient
	public boolean isAtividade() {
		return tipoComponente != null && tipoComponente.getId() == TipoComponenteCurricular.ATIVIDADE;
	}
	
	/** Indica se o componente curricular � do tipo Atividade.
	 * @param tipo
	 * @return
	 */
	@Transient
	private boolean isAtividade(int tipo) {
		return tipoAtividade != null && tipoAtividade.getId() == tipo;
	}

	/** Indica se o componente curricular � do tipo Atividade Complementar de Stricto Sensu.
	 * @return
	 */
	@Transient
	public boolean isComplementarStricto() {
		return isAtividade(TipoAtividade.COMPLEMENTAR_STRICTO);
	}
	
	/** Indica se o componente curricular � do tipo Atividade Complementar.
	 * @return
	 */
	@Transient
	public boolean isAtividadeComplementar() {
		return isAtividade(TipoAtividade.COMPLEMENTAR) || isAtividade(TipoAtividade.COMPLEMENTAR_TECNICO);
	}
	
	/** Indica se o componente curricular � do tipo Trabalho de Conclus�o de Curso.
	 * @return
	 */
	@Transient
	public boolean isTrabalhoConclusaoCurso() {
		return isAtividade(TipoAtividade.TRABALHO_CONCLUSAO_CURSO) ||
				isAtividade(TipoAtividade.TRABALHO_CONCLUSAO_CURSO_TECNICO);
	}

	/** Indica se o componente curricular � do tipo Est�gio.
	 * @return
	 */
	@Transient
	public boolean isEstagio() {
		return isAtividade(TipoAtividade.ESTAGIO) || isAtividade(TipoAtividade.ESTAGIO_TECNICO);
	}

	/** Indica se o componente curricular � do tipo Profici�ncia.
	 * @return
	 */
	@Transient
	public boolean isProficiencia() {
		return isAtividade(TipoAtividade.PROFICIENCIA);
	}

	/** Indica se o componente curricular � do tipo Qualifica��o.
	 * @return
	 */
	@Transient
	public boolean isQualificacao() {
		return isAtividade(TipoAtividade.QUALIFICACAO);
	}

	/** Indica se o componente curricular � do tipo Tese.
	 * @return
	 */
	@Transient
	public boolean isTese() {
		return isAtividade(TipoAtividade.TESE);
	}

	/** Indica se o componente curricular � do tipo Atividade Coletiva.
	 * @return
	 */
	@Transient
	public boolean isAtividadeColetiva() {
		return isAtividade() && formaParticipacao != null && formaParticipacao.getId() == FormaParticipacaoAtividade.ESPECIAL_COLETIVA;
	}

	/** Indica se o componente curricular � do tipo Atividade Coletiva ou M�dulo.
	 * @return
	 */
	@Transient
	public boolean isModuloOuAtividadeColetiva() {
		return isAtividadeColetiva() || isModulo();
	}


	/**
	 * Valida as regras espec�ficas de um componente de bloco.
	 * @return
	 */
	public ListaMensagens validateBloco() {
		ListaMensagens erros = new ListaMensagens();
		if (subUnidades == null || subUnidades.size() == 0)
			erros.addErro("Bloco deve possuir pelo menos uma sub-unidade");
		return erros;
	}

	/**
	 * Valida as regras espec�ficas das subunidades dos componentes de sub-unidade.
	 * @return
	 */
	public ListaMensagens validateSubUnidade() {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(detalhes.getNome(), "Nome", erros);
		// validar cr�ditos se a subUnidade for do tipo DISCIPLINA e carga hor�ria se for do tipo MODULO
		if( this.isDisciplina() ){
			ValidatorUtil.validaIntGt(detalhes.getCrAula(), "Cr�ditos Te�ricos", erros);
			ValidatorUtil.validaIntGt(detalhes.getCrLaboratorio(), "Cr�ditos Pr�ticos", erros);
			ValidatorUtil.validaInt(detalhes.getCrAula()+detalhes.getCrLaboratorio(), "Cr�ditos", erros);
		} else if( this.isModuloOuAtividadeColetiva() || this.isModulo()){
			ValidatorUtil.validaIntGt(getChAula(), "Carga Hor�ria Te�rica", erros);
			ValidatorUtil.validaIntGt(getChLaboratorio(), "Carga Hor�ria Pr�tica", erros);
			ValidatorUtil.validaInt(detalhes.getChAula()+detalhes.getChLaboratorio(), "Carga Hor�ria", erros);
		}
		ValidatorUtil.validateMaxLength(detalhes.getEmenta(), 1000, "Ementa", erros);
		ValidatorUtil.validateRequired(detalhes.getEmenta(), "Ementa", erros);

		return erros;
	}

	/** Retorna uma descri��o textual deste componente curricular.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		StringBuilder sb = new StringBuilder();
		
		if (getCodigo() != null) {
			sb.append(getCodigo());
		}

		sb.append(" - ");
		
		if (detalhes != null && detalhes.getNome() != null) {
			sb.append(detalhes.getNome());
		}
		
		sb.append(" - ");
		
		sb.append(getChTotal() + "h");
		
		return sb.toString();
	}

	/** Retorna uma descri��o resumida deste componente curricular. 
	 * @return
	 */
	@Transient
	public String getDescricaoResumida() {
		return getCodigo() + " - " + (detalhes != null ? detalhes.getNome() : "");
	}

	/** Retorna os detalhes atuais do componente. 
	 * @return
	 */
	public ComponenteDetalhes getDetalhes() {
		return detalhes;
	}

	/** Seta os detalhes atuais do componente.
	 * @param detalhes
	 */
	public void setDetalhes(ComponenteDetalhes detalhes) {
		this.detalhes = detalhes;
	}

	/** Retorna a quantidade de horas/cr�dito deste componente. Este atributo � transiente. 
	 * @return
	 */
	public int getHorasCredito() {
		return horasCredito;
	}

	/** Seta a quantidade de horas/cr�dito deste componente. Este atributo � transiente.
	 * @param horasCredito
	 */
	public void setHorasCredito(int horasCredito) {
		this.horasCredito = horasCredito;
	}

	/** Retorna os corequisitos deste componente. 
	 * @return
	 */
	@Transient
	public String getCoRequisito() {
		return detalhes.getCoRequisito();
	}

	/** Seta os corequisitos deste componente. 
	 * @param coRequisito
	 */
	public void setCoRequisito(String coRequisito) {
		this.detalhes.setCoRequisito(coRequisito);
	}

	/** Retorna as equival�ncias deste componente. 
	 * @return
	 */
	@Transient
	public String getEquivalencia() {
		return detalhes.getEquivalencia();
	}

	/** Seta as equival�ncias deste componente.
	 * @param equivalencia
	 */
	public void setEquivalencia(String equivalencia) {
		this.detalhes.setEquivalencia(equivalencia);
	}

	/** Retorna os prerequisitos deste componente. 
	 * @return
	 */
	@Transient
	public String getPreRequisito() {
		if( detalhes != null )
			return detalhes.getPreRequisito();
		return null;
	}

	/** Seta os prerequisitos deste componente.
	 * @param preRequisito
	 */
	public void setPreRequisito(String preRequisito) {
		this.detalhes.setPreRequisito(preRequisito);
	}

	/** Retorna o tipo do componente. 
	 * @return
	 */
	public TipoComponenteCurricular getTipoComponente() {
		return tipoComponente;
	}

	/** Seta o tipo do componente.
	 * @param tipoComponente
	 */
	public void setTipoComponente(TipoComponenteCurricular tipoComponente) {
		this.tipoComponente = tipoComponente;
	}

	/** Retorna o n�mero de unidades que o componente tem, utilizado para gerar a planilha de notas dos alunos matriculados 
	 * @return
	 */
	public Integer getNumUnidades() {
		return numUnidades;
	}

	/** Seta o n�mero de unidades que o componente tem, utilizado para gerar a planilha de notas dos alunos matriculados
	 * @param numUnidades
	 */
	public void setNumUnidades(Integer numUnidades) {
		this.numUnidades = numUnidades;
	}

	/** Retorna a bibliografia indicada para estudo do componente curricular. Usado em lato-sensu, nos demais casos, dever� haver o programa do componente. 
	 * @return
	 */
	public String getBibliografia() {
		return bibliografia;
	}

	/** Seta a bibliografia indicada para estudo do componente curricular. Usado em lato-sensu, nos demais casos, dever� haver o programa do componente.
	 * @param bibliografia
	 */
	public void setBibliografia(String bibliografia) {
		this.bibliografia = bibliografia;
	}

	/** Retorna o conjunto de componentes por curso de lato sensu, para indicar a quais cursos Lato o componente pertence. 
	 * @return
	 */
	public Set<ComponenteCursoLato> getComponentesCursoLato() {
		return this.componentesCursoLato;
	}

	/** Seta o conjunto de componentes por curso de lato sensu, para indicar a quais cursos Lato o componente pertence.
	 * @param componentesCursoLato
	 */
	public void setComponentesCursoLato(
			Set<ComponenteCursoLato> componentesCursoLato) {
		this.componentesCursoLato = componentesCursoLato;
	}

	/** Retorna o tipo da atividade complementar. Apenas o componente do tipo atividade complementar tem este atributo. 
	 * @return
	 */
	public TipoAtividadeComplementar getTipoAtividadeComplementar() {
		return tipoAtividadeComplementar;
	}

	/** Seta o tipo da atividade complementar. Apenas o componente do tipo atividade complementar tem este atributo.
	 * @param tipoAtividadeComplementar
	 */
	public void setTipoAtividadeComplementar(
			TipoAtividadeComplementar tipoAtividadeComplementar) {
		this.tipoAtividadeComplementar = tipoAtividadeComplementar;
	}

	/** Retorna o tipo da atividade. Apenas os componentes do tipo atividade tem este atributo. 
	 * @return
	 */
	public TipoAtividade getTipoAtividade() {
		return tipoAtividade;
	}

	/** Seta o tipo da atividade. Apenas os componentes do tipo atividade tem este atributo.
	 * @param tipoAtividade
	 */
	public void setTipoAtividade(TipoAtividade tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}

	/** Uma descri��o textual do n�vel de ensino deste componente curricular.
	 * @return
	 */
	@Transient
	public String getNivelDesc() {
		if (getNivel() == 'S') {
			return "P�S-GRADUA��O";
		} else {
			return NivelEnsino.getDescricao(getNivel());
		}
	}

	/** Retorna o motivo do componente estar inativo: pode ter sido removido, tirado de circula��o, ou ainda aguardando confirma��o de cadastro do CDP.  
	 * @return
	 */
	public Integer getStatusInativo() {
		return statusInativo;
	}

	/** Seta o motivo do componente estar inativo: pode ter sido removido, tirado de circula��o, ou ainda aguardando confirma��o de cadastro do CDP.
	 * @param statusInativo
	 */
	public void setStatusInativo(Integer statusInativo) {
		this.statusInativo = statusInativo;
	}

	/** Retorna uma descri��o textual do status inativo deste componente curricular.
	 * @return
	 */
	@Transient
	public String getStatusInativoDesc() {
		if (statusInativo != null)
			switch (statusInativo) {
			case DESATIVADO:
				return "Desativado";
			case AGUARDANDO_CONFIRMACAO:
				return "Aguardando Confirma��o";
			case CADASTRO_NEGADO:
				return "Cadastro Negado";
			default:
				return "";
			}
		else
			return "-";
	}

	/** Indica se a solicita��o de cadastro do componente curricular foi negada.
	 * @return
	 */
	@Transient
	public boolean getFoiNegado() {
		return getStatusInativo() != null
				&& getStatusInativo() == ComponenteCurricular.CADASTRO_NEGADO;
	}

	/** Indica se a solicita��o de cadastro do componente curricular est� aguardando an�lise.
	 * @return
	 */
	@Transient
	public boolean isAguardandoConfirmacao() {
		return getStatusInativo() != null
				&& getStatusInativo() == ComponenteCurricular.AGUARDANDO_CONFIRMACAO;
	}
	
	/** Retorna uma descri��o textual deste objeto.
	 * @see #getDescricaoResumida()
	 */
	@Override
	public String toString() {
		return getDescricaoResumida();
	}

	/** Retorna, caso o componentes seja do tipo SubUnidade, a refer�ncia ao seu bloco respons�vel. 
	 * @return
	 */
	public ComponenteCurricular getBlocoSubUnidade() {
		return blocoSubUnidade;
	}

	/** Seta, caso o componentes seja do tipo SubUnidade, a refer�ncia ao seu bloco respons�vel.
	 * @param blocoSubUnidade
	 */
	public void setBlocoSubUnidade(ComponenteCurricular blocoSubUnidade) {
		this.blocoSubUnidade = blocoSubUnidade;
	}

	/** Retorna as entidades que representam uma unidade de estrutura��o did�tico-pedag�gica, correspondendo, por exemplo, a disciplinas, m�dulos, blocos e atividades acad�micas espec�ficas.
	 * @return
	 */
	public List<ComponenteCurricular> getSubUnidades() {
		return subUnidades;
	}

	/** Seta as entidades que representam uma unidade de estrutura��o did�tico-pedag�gica, correspondendo, por exemplo, a disciplinas, m�dulos, blocos e atividades acad�micas espec�ficas.
	 * @param subUnidades
	 */
	public void setSubUnidades(List<ComponenteCurricular> subUnidades) {
		this.subUnidades = subUnidades;
	}

	/** Indica se este componente estar� dispon�vel para que os alunos realizem matr�cula online. 
	 * @return
	 */
	public boolean isMatriculavel() {
		return matriculavel;
	}

	/** Seta se este componente estar� dispon�vel para que os alunos realizem matr�cula online. 
	 * @param matriculavel
	 */
	public void setMatriculavel(boolean matriculavel) {
		this.matriculavel = matriculavel;
	}

	/** Indica se o componente precisa de m�dia final. Caso n�o precise o resultado � apenas aprovado ou reprovado. 
	 * @return
	 */
	public boolean isNecessitaMediaFinal() {
		return necessitaMediaFinal;
	}

	/** Seta se o componente precisa de Carga Hor�ria. 
	 * @return
	 */
	public boolean isNecessitaCargaHoraria() {
		return !isProficiencia() && !isTese() && !isQualificacao() && ( isStricto() && !isAtividadeComplementar() && !isComplementarStricto() );
	}
	
	/** Seta se o componente precisa de m�dia final. Caso n�o precise o resultado � apenas aprovado ou reprovado. 
	 * @param necessitaMediaFinal
	 */
	public void setNecessitaMediaFinal(boolean necessitaMediaFinal) {
		this.necessitaMediaFinal = necessitaMediaFinal;
	}

	/** Indica, no hist�rico, se o discente est� matriculado em um componente pendente. Este atributo � transiente. 
	 * @return
	 */
	public boolean isMatriculado() {
		return matriculado;
	}

	/** Seta, no hist�rico, se o discente est� matriculado em um componente pendente. Este atributo � transiente. 
	 * @param matriculado
	 */
	public void setMatriculado(boolean matriculado) {
		this.matriculado = matriculado;
	}

	/** Retorna o n�mero m�ximo de faltas permitido.
	 * @param porcFrequenciaMinima
	 * @param minutosAulaRegular
	 * @return
	 */
	@Transient
	public int getMaximoFaltasPermitido(float porcFrequenciaMinima, int minutosAulaRegular) {
		int chTotal = getChTotalAula();
		int numAulas = chTotal * 60 / minutosAulaRegular;
		return (int) (numAulas - (numAulas * porcFrequenciaMinima/100.0));
	}

	/** Retorna a cole��o de componentes curriculares que possuem este componente curricular como prerequisito. Este atributo � transiente.  
	 * @return
	 */
	@Transient
	public  Collection<ComponenteCurricular> getInversosEquivalentes() {
		return inversosEquivalentes;
	}

	/** Retorna a cole��o de componentes curriculares que possuem este componente curricular como prerequisito. Este atributo � transiente. 
	 * @return
	 */
	@Transient
	public  Collection<ComponenteCurricular> getInversosPreRequisitos() {
		return inversosPreRequisitos;
	}

	/** Retorna a cole��o de componentes curriculares que possuem este componente curricular como corequisito. Este atributo � transiente. 
	 * @return
	 */
	@Transient
	public  Collection<ComponenteCurricular> getInversosCoRequisitos() {
		return inversosCoRequisitos;
	}
	
	/** Retorna a cole��o de Equival�ncias Espec�fcias do componente. Este atributo � transiente. 
	 * @return
	 */
	@Transient
	public  Collection<EquivalenciaEspecifica> getEquivalenciaEspecifica() {
		return equivalenciaEspecifica;
	}

	/**
	 * Preenche os inversos das express�es de equival�ncia, pr�-requisito e co-requisito de acordo com as express�es de cada componente da cole��o
	 * @param todosComponentes
	 * @throws ArqException
	 */
	public void preencherInversos(Collection<ComponenteCurricular> todosComponentes) throws ArqException {
		inversosEquivalentes = new ArrayList<ComponenteCurricular>(0);
		inversosPreRequisitos = new ArrayList<ComponenteCurricular>(0);
		inversosCoRequisitos = new ArrayList<ComponenteCurricular>(0);
		TreeSet<Integer> tree = new TreeSet<Integer>();
		tree.add(id);
		for (ComponenteCurricular c : todosComponentes) {
			try {
				if (c.getEquivalencia() != null && !c.getEquivalencia().equals("") && ExpressaoUtil.eval(c.getEquivalencia(), tree))
					inversosEquivalentes.add(c);
			} catch (Exception e) {
				// n�o adiciona
			}
			try {
				if (c.getPreRequisito() != null && !c.getPreRequisito().equals("") && ExpressaoUtil.eval(c.getPreRequisito(), tree))
					inversosPreRequisitos.add(c);
			} catch (Exception e) {
				// n�o adiciona
			}
			try {
				if (c.getCoRequisito() != null && !c.getCoRequisito().equals("") && ExpressaoUtil.eval(c.getCoRequisito(), tree))
					inversosCoRequisitos.add(c);
			} catch (Exception e) {
				// n�o adiciona
			}
		}
	}
	
	/**
	 * Preenche as equival�ncias espec�ficas do componente curricular.
	 * @param todosComponentes
	 * @throws ArqException
	 */
	public void preencherEquivalenciaEspecifica(Collection<EquivalenciaEspecifica> todasEquivalenciasEspecificas) throws ArqException {
		
		equivalenciaEspecifica = new ArrayList<EquivalenciaEspecifica>(0);
		TreeSet<Integer> tree = new TreeSet<Integer>();
		tree.add(id);
		for (EquivalenciaEspecifica e : todasEquivalenciasEspecificas) {
			try {
				if (e.getExpressao() != null && !e.getExpressao().equals("") && e.isEquivalenciaValendoNaData(new Date()) )
					equivalenciaEspecifica.add(e);
			} catch (Exception ex) {
				// n�o adiciona
			}
		}
	}

	/** Compara este componente curricular com o passado, ordenando-os por nome.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ComponenteCurricular o) {
		return this.getNome().compareToIgnoreCase(o.getNome());
	}

	/** Indica se o chefe pode criar turma deste componente sem que seja necess�rio uma solicita��o de turma. 
	 * @return
	 */
	public boolean isTurmasSemSolicitacao() {
		return turmasSemSolicitacao;
	}

	/** Seta se o chefe pode criar turma deste componente sem que seja necess�rio uma solicita��o de turma. 
	 * @param turmasSemSolicitacao
	 */
	public void setTurmasSemSolicitacao(boolean turmasSemSolicitacao) {
		this.turmasSemSolicitacao = turmasSemSolicitacao;
	}

	/** Indica, no hist�rico, se o discente est� matriculado em um componente equivalente. Este atributo � transiente. 
	 * @return
	 */
	public boolean isMatriculadoEmEquivalente() {
		return matriculadoEmEquivalente;
	}

	/** Seta, no hist�rico, se o discente est� matriculado em um componente equivalente. Este atributo � transiente. 
	 * @param matriculadoEmEquivalente
	 */
	public void setMatriculadoEmEquivalente(boolean matriculadoEmEquivalente) {
		this.matriculadoEmEquivalente = matriculadoEmEquivalente;
	}

	/** Indica se este componente � de stricto senso (Doutorado ou Mestrado).
	 * @return
	 */
	public boolean isStricto() {
		return NivelEnsino.isAlgumNivelStricto(nivel);
	}
	
	/** Indica se este componente � de resid�ncia em sa�de. 
	 * @return
	 */
	public boolean isResidencia() {
		return nivel == NivelEnsino.RESIDENCIA;
	}

	/** Indica se este componente � de ensino t�cnico.  
	 * @return
	 */
	public boolean isTecnico() {
		return nivel == NivelEnsino.TECNICO;
	}
	
	/** Indica se este componente � de forma��o complementar.  
	 * @return
	 */
	public boolean isFormacaoComplementar() {
		return nivel == NivelEnsino.FORMACAO_COMPLEMENTAR;
	}

	/** Indica se este componente � de gradua��o. 
	 * @return
	 */
	public boolean isGraduacao() {
		return nivel == NivelEnsino.GRADUACAO;
	}
	
	/** Indica se este componente � de ensino m�dio. 
	 * @return
	 */
	public boolean isMedio() {
		return nivel == NivelEnsino.MEDIO;
	}

	/** Retorna o conte�do das aulas das disciplinas de ensino a dist�ncia. 
	 * @return
	 */
	public Collection<ItemPrograma> getItemPrograma() {
		return itemPrograma;
	}

	/** Seta o conte�do das aulas das disciplinas de ensino a dist�ncia.
	 * @param itemPrograma
	 */
	public void setItemPrograma(Collection<ItemPrograma> itemPrograma) {
		this.itemPrograma = itemPrograma;
	}

	/** Retorna a data de cadastro do componente. 
	 * @return
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data de cadastro do componente.
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna o registro entrada do usu�rio que cadastrou.  
	 * @return
	 */
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	/** Seta o registro entrada do usu�rio que cadastrou.
	 * @param registroCadastro
	 */
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	/** Retorna a data da �ltima atualiza��o do componente. 
	 * @return
	 */
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	/** Seta a data da �ltima atualiza��o do componente.
	 * @param dataAtualizacao
	 */
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	/** Retorna o registro entrada do usu�rio que realizou a �ltima atualiza��o. 
	 * @return
	 */
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o registro entrada do usu�rio que realizou a �ltima atualiza��o.
	 * @param registroAtualizacao
	 */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Indica, no caso de atividades complementares, se deve possuir orientador ou n�o.</br>
	 * - DISCIPLINAS, M�DULOS, ATIVIDADES COLETIVAS:N�O;</br>
	 * - ATIVIDADES DE ORIENTA��O INDIVIDUAL: SIM; 
	 * @return
	 */
	public boolean isTemOrientador() {
		return !(isDisciplina() || isModulo() || isAtividadeColetiva())&&(isAtividade() && formaParticipacao.isOrientacaoIndividual());
	}

	/** Indica se o componente � ativo ou n�o 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o componente � ativo ou n�o 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Indica que o componente possui subturmas e ter� um tratamento diferenciado nas opera��es que envolvem turmas como solicita��o, cria��o, consolida��o, turma virtual, etc
	 * @return
	 */
	public boolean isAceitaSubturma() {
		return aceitaSubturma;
	}

	/** Seta se o componente possui subturmas e ter� um tratamento diferenciado nas opera��es que envolvem turmas como solicita��o, cria��o, consolida��o, turma virtual, etc
	 * @param aceitaSubturma
	 */
	public void setAceitaSubturma(boolean aceitaSubturma) {
		this.aceitaSubturma = aceitaSubturma;
	}

	/** Retorna a natureza do componente curricular: Te�rica, Pr�tica, etc.
	 * @return
	 */
	@Transient
	public String getNatureza(){
		if( (detalhes.getCrAula() > 0 || detalhes.getChAula() > 0) 
				&& (detalhes.getCrLaboratorio() > 0 || detalhes.getChLaboratorio() > 0) ){
			return "T�orica/Pr�tica";
		}else if(detalhes.getCrAula() > 0 || detalhes.getChAula() > 0){
			return "T�orica";
		}else if(detalhes.getCrLaboratorio() > 0 || detalhes.getChLaboratorio() > 0)
			return "Pr�tica";
		return "Indefinida";
	}

	/** Indica se este componente ser� exclu�do da Avalia��o Institucional. 
	 * @return true, se for exclu�do.
	 */
	public Boolean getExcluirAvaliacaoInstitucional() {
		return excluirAvaliacaoInstitucional;
	}
	
	/** Indica se este componente ser� exclu�do da Avalia��o Institucional. Caso n�o definido (null), retorna false. 
	 * @return true, se for exclu�do.
	 */
	public Boolean isExcluirAvaliacaoInstitucional() {
		return excluirAvaliacaoInstitucional == null?false:excluirAvaliacaoInstitucional;
	}

	/** Seta se este componente ser� exclu�do da Avalia��o Institucional.
	 * @param excluirAvaliacaoInstitucional true, se for exclu�do.
	 */
	public void setExcluirAvaliacaoInstitucional(
			Boolean excluirAvaliacaoInstitucional) {
		this.excluirAvaliacaoInstitucional = excluirAvaliacaoInstitucional;
	}

	/** Retorna o c�digo utilizado para saber se o componente foi migrado do PontoA. 
	 * @return
	 */
	public String getCodmergpa() {
		return codmergpa;
	}

	/** Seta o c�digo utilizado para saber se o componente foi migrado do PontoA.
	 * @param codmergpa
	 */
	public void setCodmergpa(String codmergpa) {
		this.codmergpa = codmergpa;
	}
	
	/** Indica que o componente curricular foi migrado do PontoA.
	 * @return
	 */
	public boolean isMigradoPontoA() {
		if (codmergpa != null)
			return true;
		
		return false;
	}

	/** Indica se a turma criada para este componente curricular ter� hor�rio flex�vel.<br/>
	 * - M�DULOS, ATIVIDADES COLETIVAS COM AULAS: SIM;<br/>
	 * - DISCIPLINAS: N�O;<br/>
	 * - DEMAIS ATIVIDADES: N�O SE APLICA; 
	 * @return
	 */
	public boolean isPermiteHorarioFlexivel() {
		return !isDisciplina() && (isModulo() || (isAtividadeColetiva() && getChTotalAula() > 0));
	}

	/** Retorna a forma de participa��o dos docentes e discentes. 
	 * @return
	 */
	public FormaParticipacaoAtividade getFormaParticipacao() {
		return formaParticipacao;
	}

	/** Seta a forma de participa��o dos docentes e discentes.
	 * @param formaParticipacao
	 */
	public void setFormaParticipacao(FormaParticipacaoAtividade formaParticipacao) {
		this.formaParticipacao = formaParticipacao;
	}

	/** Retorna o curso a qual o componente faz parte. 
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso a qual o componente faz parte.
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna o nome do novo curso cadastrado junto com o componente.
	 * @return the cursoNovo
	 */
	public String getCursoNovo() {
		return cursoNovo;
	}

	/** Seta o nome do novo curso cadastrado junto com o componente.
	 * @param cursoNovo the cursoNovo to set
	 */
	public void setCursoNovo(String cursoNovo) {
		this.cursoNovo = cursoNovo;
	}	

	public ParametrosGestoraAcademica getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosGestoraAcademica parametros) {
		this.parametros = parametros;
	}

	/** Indica se o docente ter� hor�rio flex�vel na turma criada para este componente curricular.<br/>
	 * - DISCIPLINAS, M�DULOS, ATIVIDADES COLETIVAS COM AULAS: SIM;<br/>
	 * - ATIVIDADES COLETIVAS SEM AULAS, DEMAIS ATIVIDADES: N�O SE APLICA; 
	 * @return
	 */
	public boolean isPermiteHorarioDocenteFlexivel() {
		return isDisciplina() || isModulo() || (isAtividadeColetiva() && getChTotalAula() > 0);
	}

	/** Retorna o programa atual do componente. 
	 * @return
	 */
	public ComponenteCurricularPrograma getPrograma() {
		return programa;
	}

	/** Seta o programa atual do componente.
	 * @param programa
	 */
	public void setPrograma(ComponenteCurricularPrograma programa) {
		this.programa = programa;
	}

	/** Retorna a quantidade m�xima de matr�culas que um discente pode realizar neste componente. 
	 * @return
	 */
	public int getQtdMaximaMatriculas() {
		return qtdMaximaMatriculas;
	}

	/** Seta a quantidade m�xima de matr�culas que um discente pode realizar neste componente.
	 * @param qtdMaximaMatriculas
	 */
	public void setQtdMaximaMatriculas(int qtdMaximaMatriculas) {
		this.qtdMaximaMatriculas = qtdMaximaMatriculas;
	}

	public boolean isLato() {
		return nivel == NivelEnsino.LATO;
	}

	/** M�todo respons�vel por verificar se o componente curricular � sujeito a cadastro de tipo de atividade. */
	@Transient
	public boolean isPassivelTipoAtividade() {
		return ( (this.tipoComponente.getId() > 0 && isAtividade())  
					|| ( isStricto() && isDisciplina() && 
					  ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.TESE_DEFINIDA_COMO_DISCIPLINA) ) );
	}

	/** Indica que o componente curricular pode ter uma turma.<br/> 
	 * - DISCIPLINAS, M�DULOS e ATIVIDADES COLETIVAS: SIM;<br/>
	 * - DEMAIS ATIVIDADES: N�O;
	 * @return
	 */
	@Transient
	public boolean isPermiteCriarTurma() {
		return isDisciplina() || isModulo() || isAtividadeColetiva() || isBloco();
	}
	
	/** Indica que se � exigido a defini��o de hor�rio na cria��o da turma.<br/> 
	 * - DISCIPLINAS, M�DULOS, ATIVIDADES COLETIVAS COM AULAS: SIM;<br/>
	 * - ATIVIDADES COLETIVAS SEM AULAS, DEMAIS ATIVIDADES: N�O;<br/>
	 * @return
	 */
	@Transient
	public boolean isExigeHorarioEmTurmas() {
		return isBloco() || isDisciplina() || isModulo() || (isAtividadeColetiva() && getChTotalAula() > 0);
	}
	
	public boolean isPermiteCancelarMatricula() {
		return permiteCancelarMatricula;
	}

	public void setPermiteCancelarMatricula(boolean permiteCancelarMatricula) {
		this.permiteCancelarMatricula = permiteCancelarMatricula;
	}

	public Collection<EquivalenciaEspecifica> getInversosEquivalenciaEspecifica() {
		return inversosEquivalenciaEspecifica;
	}

	public void setInversosEquivalenciaEspecifica(
			Collection<EquivalenciaEspecifica> inversosEquivalenciaEspecifica) {
		this.inversosEquivalenciaEspecifica = inversosEquivalenciaEspecifica;
	}

	public Collection<ExpressaoComponenteCurriculo> getExpressoesEspecificaCurriculo() {
		return expressoesEspecificaCurriculo;
	}

	public void setExpressoesEspecificaCurriculo(
			Collection<ExpressaoComponenteCurriculo> expressoesEspecificaCurriculo) {
		this.expressoesEspecificaCurriculo = expressoesEspecificaCurriculo;
	}

	public boolean isConteudoVariavel() {
		return conteudoVariavel;
	}

	public void setConteudoVariavel(boolean conteudoVariavel) {
		this.conteudoVariavel = conteudoVariavel;
	}

	public boolean isIntegralizado() {
		return integralizado;
	}

	public void setIntegralizado(boolean integralizado) {
		this.integralizado = integralizado;
	}

	public int getCargaHorariaContabilizada() {
		return cargaHorariaContabilizada;
	}

	public void setCargaHorariaContabilizada(int cargaHorariaContabilizada) {
		this.cargaHorariaContabilizada = cargaHorariaContabilizada;
	}

	public boolean isDistancia() {
		return modalidadeEducacao != null && modalidadeEducacao.isADistancia();
	}

	public int getNumMaxDocentes() {
		return numMaxDocentes;
	}

	public void setNumMaxDocentes(int numMaxDocentes) {
		this.numMaxDocentes = numMaxDocentes;
	}

	public ModalidadeEducacao getModalidadeEducacao() {
		return modalidadeEducacao;
	}

	public void setModalidadeEducacao(ModalidadeEducacao modalidadeEducacao) {
		this.modalidadeEducacao = modalidadeEducacao;
	}

}
