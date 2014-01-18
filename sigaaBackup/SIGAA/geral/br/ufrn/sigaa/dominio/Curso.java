/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '20/10/2008'
 *
 */
package br.ufrn.sigaa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.AreaSesu;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.NaturezaCurso;
import br.ufrn.sigaa.ensino.dominio.TipoCicloFormacao;
import br.ufrn.sigaa.ensino.dominio.TipoOfertaCurso;
import br.ufrn.sigaa.ensino.dominio.TipoOfertaDisciplina;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.stricto.dominio.OrganizacaoAdministrativa;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.vestibular.dominio.AreaConhecimentoVestibular;

/**
 * Classe m�e de todos os tipos de Cursos da institui��o. � a base para todos os demais tipos de cursos no sistema.
 */
@Entity
@Table(name = "curso", uniqueConstraints = {})
@Inheritance(strategy = InheritanceType.JOINED)
public class Curso implements Validatable, Comparable<Curso> {

	/** Constante respons�vel por indicar o identificador do curso de administra��o a dist�ncia.	 */
	public static final int CURSO_ADMINISTRACAO_A_DISTANCIA = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ID_CURSO_ADMINISTRACAO_A_DISTANCIA); 
	
	/**
	 * URL de acesso ao documento de reconhecimento e conceito 
	 * de curso no sistema CAPES
	 */
	public static final String ENDERECO_URL_CAPES = 
		"http://conteudoweb.capes.gov.br/conteudoweb/ProjetoRelacaoCursosServlet?acao=detalhamentoIes&codigoPrograma=";
	
	// Fields
	/** Atributo utilizado para representar o id do curso */
	private int id;

	/** Refer�ncia a unidade do curso */
	private Unidade unidade;
	
	/** Refer�ncia uma segunda unidade que o curso pode estar vinculado */
	private Unidade unidade2;

	/** No caso de curso de stricto, esse j� � o c�digo da CAPES */
	private String codigo;

	/** Nome do curso */
	private String nome;

	/** Diz se o curso est� ativo */
	private boolean ativo = true;

	/** N�vel do curso (Gradua��o - G, Mestrado E, etc */
	private char nivel;
	
	/** Descri��o do N�vel do curso (Gradua��o, Mestrado, T�cnico etc */
	@Transient
	private String descricaoNivel;

	/** Munic�pio do curso */
	private Municipio municipio;

	/** Natureza do curso */
	private NaturezaCurso naturezaCurso;

	/** Modalidade de educa��o do curso (presencial, a dist�ncia, etc) */
	private ModalidadeEducacao modalidadeEducacao;

	/** Tipo de oferta do curso (semestral, anual, etc) */
	private TipoOfertaCurso tipoOfertaCurso;
	
	/** Tipo de oferta das disciplinas do curso (semestral, anual, etc) */
	private TipoOfertaDisciplina tipoOfertaDisciplina;

	/** Coordena��es do curso */
	private Set<CoordenacaoCurso> coordenacoesCursos = new HashSet<CoordenacaoCurso>(0);

	/** Matrizes curriculares do curso */
	private List<MatrizCurricular> matrizesCurriculares;

	/** Conv�nio do curso, caso tenha (prob�sica, pronera, etc) */
	private ConvenioAcademico convenio;

	/** �rea de conhecimento cnpq do curso */
	private AreaConhecimentoCnpq areaCurso;

	/** �rea de conhecimento do vestibular */
	private AreaConhecimentoVestibular areaVestibular;

	/** Titula��o no g�nero masculino que os discentes do curso stricto sensu obt�m ao conclu�rem. */
	private String titulacaoMasculino;
	
	/** Titula��o no g�nero feminino que os discentes do curso stricto sensu obt�m ao conclu�rem. */
	private String titulacaoFeminino;
	
	/** Arquivo do projeto pol�tico-pedag�gico */
	private Integer idArquivo;

	/** Campo de controle utilizado nas jsp's */
	private boolean selecionado;

	/** Unidade da coordena��o do curso */
	private Unidade unidadeCoordenacao;

	/** String com c�digo do programa CAPES, apenas para cursos de p�s gradua��o */
	private String codProgramaCAPES;

	/** Tipo de curso STRICTO (mestrado profissional, acad�mico, doutorado, etc) */
	private TipoCursoStricto tipoCursoStricto;

	/** Nome sem acentos */
	private String nomeAscii;

	/**
	 * Define o tipo da organiza��o administrativa de um curso, s� existe esse
	 * atributo para curso stricto
	 */
	private OrganizacaoAdministrativa organizacaoAdministrativa;

	/* Dados de Reconhecimento do Curso */
	/** Portaria de reconhecimento do curso  (gradua��o) / Portaria MEC (P�s-gradua��o, #33133)  */
	private String reconhecimentoPortaria;
	/** Data do decreto do reconhecimento /  Data de In�cio de Funcionamento  (P�s-gradua��o, #33133) */
	private Date dataDecreto;
	/** Dou de reconhecimento (gradua��o) / Data de publica��o (P�s-gradua��o, #33133) */
	private Date dou;
	/** Campo transiente de controle  */
	private Date dataHistorico;
	
	/** Indica se o curso possui reconhecimento. (Atributo transiente) */
	private boolean reconhecimento;

	/** C�digo INEP do curso */
	private String codigoINEP;

	/**
	 * Este atributo diz se a coordena��o de curso pode realizar matr�culas dos
	 * alunos deste curso da mesma forma que alunos de prob�sica e alunos
	 * ingressantes
	 */
	private boolean podeMatricular;

	/** 
	 * Atributo utilizado para representar as Areas SESU (Secretaria de Ensino Superior)
	 * @see AreaSesu 
	 */
	private AreaSesu areaSesu;
	
	/** Registro entrada de cadastro do curso */
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data de cadastro */
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada da �ltima atualiza��o */
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data da �ltima atualiza��o */
	@AtualizadoEm
	private Date dataAtualizacao;
	
	/** Link para o site contendo mais informa��o sobre o curso. */
	private String website;
	/** �reas que o profissional, ao concluir o curso, pode trabalhar. */
	private String campoAtuacao;
	/** Capacidade (lista de aptid�es) que o aluno possui ao terminar o curso. */
	private String perfilProfissional;
	/** As compet�ncias e habilidades que o aluno possui ao terminar o curso. */
	private String competenciasHabilidades;
	/** Metodologia a ser adotada para a consecu��o da proposta pedag�gica.*/
	private String metodologia;
	/** As formas de gest�o do curso inseridas no projeto pol�tico pedag�gico. */
	private String gestaoCurso;
	/** A avalia��o do curso inserida no projeto pol�tico pedag�gico. */
	private String avaliacaoCurso;
	
	/**
	 * Atributo TRANSIENT que guarda a coordena��o ativa atual.
	 *
	 * @author Edson An�bal
	 */
	private CoordenacaoCurso coordenacaoAtual;
	
	/**
	 * Ciclo de forma��o do curso, podendo ser um ou dois ciclos.
	 */
	private TipoCicloFormacao tipoCicloFormacao;
	
	/** Campo de controle para informar se o curso de stricto sensu pertence a uma rede de ensino. */
	private boolean rede = false;
	
	/** Associa��o de curso a institui��es de ensino. */
	private List<DadosCursoRede> cursoInstituicoesEnsino;

	/** Caso o curso n�o tenha concluintes, calcular m�dia de MCs dos alunos dessa unidade, para c�lculo do MCN */
	private Unidade unidadeReferenciaMcn;

	/** Indica se uma pessoa pode ter mais de um v�nculo ativo de aluno nesse curso simultaneamente */
	private boolean permiteAlunosVariosVinculos = false;
	
//	private MetodologiaAvaliacao metodologiaEad;
	
	/**
	 * Atributo TRANSIENT que guarda a coordena��o ativa atual.
	 *
	 * @author Edson An�bal
	 */
	@Transient
	public CoordenacaoCurso getCoordenacaoAtual() {
		return coordenacaoAtual;
	}

	public void setCoordenacaoAtual(CoordenacaoCurso coordenacaoAtual) {
		this.coordenacaoAtual = coordenacaoAtual;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_area_curso")
	public AreaConhecimentoCnpq getAreaCurso() {
		return areaCurso;
	}

	public void setAreaCurso(AreaConhecimentoCnpq areaCurso) {
		this.areaCurso = areaCurso;
	}

	/** Default constructor */
	public Curso() {
		unidade = new Unidade();
	}

	/** Minimal constructor */
	public Curso(int idCurso) {
		this.id = idCurso;
	}

	public Curso(int id, String nome, int unidade, String municipio, char nivel) {
		this.id = id;
		this.nome = nome;
		this.unidade = new Unidade(unidade);
		this.nivel = nivel;
		this.municipio = new Municipio();
		this.municipio.setNome(municipio);
	}

	public Curso(int id, String nome, int unidade, String municipio,
			char nivel, int areaVestibular) {
		this.id = id;
		this.nome = nome;
		this.unidade = new Unidade(unidade);
		this.nivel = nivel;
		this.municipio = new Municipio();
		this.municipio.setNome(municipio);
		this.areaVestibular = new AreaConhecimentoVestibular(areaVestibular);
	}

	/**
	 * M�todo respons�vel por retornar a string contendo o nome completo do curso junto com a sigla da unidade do mesmo.
	 * @return
	 */
	@Transient
	public String getNomeCompleto() {
		return nome
				+ (unidade != null && unidade.getSigla() != null ? "/"
						+ unidade.getSigla() : "");
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_area_conhecimento_vestibular", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoVestibular getAreaVestibular() {
		return areaVestibular;
	}

	public void setAreaVestibular(AreaConhecimentoVestibular areaVestibular) {
		this.areaVestibular = areaVestibular;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_curso", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idCurso) {
		this.id = idCurso;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Unidade getUnidade() {
		return this.unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade2")
	public Unidade getUnidade2() {
		return unidade2;
	}
	
	public void setUnidade2(Unidade unidade2) {
		this.unidade2 = unidade2;
	}

	@Column(name = "codigo", unique = false, nullable = true, insertable = true, updatable = true, length = 12)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigoIes) {
		this.codigo = codigoIes;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true)
	public String getNome() {
		return this.nome;
	}
	
	
	/**
	 * M�todo utilizado para setar o nome do curso
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
		setNomeAscii(nome);
	}

	@Column(name = "ativo", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isAtivo() {
		return this.ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Os filhos devem implementar esse m�todo
	 *
	 * @return
	 */
	@Transient
	public Set<ComponenteCurricular> getDisciplinas() {
		return null;
	}

	@Column(name = "nivel", unique = false, nullable = false, insertable = true, updatable = true)
	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_municipio")
	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_modalidade_educacao")
	public ModalidadeEducacao getModalidadeEducacao() {
		return modalidadeEducacao;
	}

	public void setModalidadeEducacao(ModalidadeEducacao modalidadeEducacao) {
		this.modalidadeEducacao = modalidadeEducacao;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_natureza_curso")
	public NaturezaCurso getNaturezaCurso() {
		return naturezaCurso;
	}

	public void setNaturezaCurso(NaturezaCurso naturezaCurso) {
		this.naturezaCurso = naturezaCurso;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_convenio")
	public ConvenioAcademico getConvenio() {
		return convenio;
	}

	public void setConvenio(ConvenioAcademico convenio) {
		this.convenio = convenio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_oferta_curso")
	public TipoOfertaCurso getTipoOfertaCurso() {
		return tipoOfertaCurso;
	}

	public void setTipoOfertaCurso(TipoOfertaCurso tipoOfertaCurso) {
		this.tipoOfertaCurso = tipoOfertaCurso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_oferta_disciplina")
	public TipoOfertaDisciplina getTipoOfertaDisciplina() {
		return tipoOfertaDisciplina;
	}

	public void setTipoOfertaDisciplina(
			TipoOfertaDisciplina tipoOfertaDisciplina) {
		this.tipoOfertaDisciplina = tipoOfertaDisciplina;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		String tipoOferta = "Tipo de Oferta do Curso";

		ValidatorUtil.validateRequired(nome, "Nome", erros);
		ValidatorUtil.validateRequired(municipio, "Munic�pio de Andamento do Curso", erros);
		ValidatorUtil.validateRequired(modalidadeEducacao, "Forma de Participa��o do Aluno", erros);
		ValidatorUtil.validateRequired(unidade, "Unidade Respons�vel", erros);
		ValidatorUtil.validateRequired(tipoOfertaDisciplina, "Tipo de Oferta de Disciplina", erros);
		
		
		if ((!ValidatorUtil.isEmpty(unidade) && !ValidatorUtil.isEmpty(unidade2)) && unidade.equals(unidade2)) {
			erros.addErro("A Unidade Respons�vel 2 n�o pode ser igual a Unidade Respons�vel.");
		}

		if (nivel == NivelEnsino.GRADUACAO) {
			ValidatorUtil.validateRequired(areaCurso, "�rea do Curso", erros);
			ValidatorUtil.validateRequired(naturezaCurso, "Natureza do Curso", erros);
			ValidatorUtil.validateRequired(areaVestibular, "�rea de Conhecimento do Vestibular", erros);
			ValidatorUtil.validateRequired(areaSesu, "�rea SESu", erros);
			ValidatorUtil.validateRequired(tipoCicloFormacao, "Tipo de Ciclo de Forma��o", erros);

		}
		
		if (NivelEnsino.isAlgumNivelStricto(nivel)) {
			// ValidatorUtil.validateRequired(codigo, "C�digo CAPES do Curso",
			// erros);
			ValidatorUtil.validateRequired(codProgramaCAPES, "C�digo CAPES do Programa", erros);
			ValidatorUtil.validateRequired(tipoCursoStricto, "Categoria", erros);
			ValidatorUtil.validateRequired(areaCurso, "�rea do Curso", erros);
			ValidatorUtil.validateRequired(organizacaoAdministrativa, "Organiza��o Administrativa", erros);
			ValidatorUtil.validateRequired(titulacaoMasculino, "Titula��o no G�nero Masculino", erros);
			ValidatorUtil.validateRequired(titulacaoFeminino, "Titula��o no G�nero Feminino", erros);
			ValidatorUtil.validateRequired(dataDecreto, "Data de In�cio de Funcionamento", erros);
			
			tipoOferta = "Periodicidade de Ingresso";
			
		} else {
			ValidatorUtil.validateRequired(unidadeCoordenacao, "Unidade da Coordena��o", erros);
		}

		ValidatorUtil.validateRequired(tipoOfertaCurso, tipoOferta, erros);
		/*
		 * if (unidadesSedes.size()== 0 ) erros.addErro(("O curso deve possuir
		 * ao menos uma unidade-sede"));
		 */

		return erros;
	}

	@Override
	public String toString() {
		return getNomeCompleto();
	}

	@Transient
	public boolean isADistancia() {
		return modalidadeEducacao != null && modalidadeEducacao.isADistancia();
	}
	
	@Transient
	public boolean isPresencial() {
		return modalidadeEducacao != null && modalidadeEducacao.isPresencial();
	}
	
	/**
	 * M�todo utilizado para verificar se o curso tem convenio estabelecido do tipo PRO-B�SICA
	 * @return
	 */
	@Transient
	public boolean isProbasica() {
		if (this.convenio != null) {
			return this.convenio.getId() == ConvenioAcademico.PROBASICA;
		}
		return false;
	}

	/**
	 * M�todo utilizado para verificar se o curso tem convenio estabelecido do tipo PARFOR
	 * @return
	 */
	@Transient
	public boolean isParfor() {
		if (this.convenio != null) {
			return this.convenio.isParfor();
		}
		return false;
	}
	/**
	 * M�todo respons�vel por retornar o nome do curso de Stricto concatenado o n�vel de ensino do mesmo.
	 * @return
	 */
	@Transient
	public String getNomeCursoStricto() {
		if (nivel == NivelEnsino.MESTRADO || nivel == NivelEnsino.DOUTORADO)
			return nome + " - " + NivelEnsino.getDescricao(nivel);
		else	
			return nome;
	}

	/**
	 * M�todo respons�vel por retornar o nome do curso de Stricto concatenado o tipo de curso stricto.
	 * @return
	 */
	@Transient
	public String getNomeTipoCursoStricto() {
		if (isStricto())
			return nome + " - " + getTipoCursoStricto().getDescricao();
		else	
			return nome;
	}
	
	/**
	 * M�todo respons�vel por retornar o nome do curso no formato simples, apenas o nome e sem deriva��es.
	 */
	@Transient
	public String getNomeSimples(){
		return this.nome;
	}
	
	
	/**
	 * M�todo respons�vel por retornar o nome do curso de Stricto de forma completa,
	 * com diversas informa��es sobre o mesmo concatenadas ao nome, como: munic�pio e tipo de Curso.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		StringBuilder descricao = new StringBuilder();
		descricao.append(getNomeCompleto());
		if (municipio != null && municipio.getNome() != null) {
			descricao.append(" - " + municipio.getNome());
		}
	
		if (isADistancia()) {
			descricao.append(" - EAD");
		}
		if (convenio != null && convenio.getDescricao() != null) {
			descricao.append(" - " + convenio.getDescricao());
		}
		return descricao.toString().trim().equals("null") ? "" : descricao.toString();
	}

	@Column(name = "id_arquivo")
	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	@Transient
	public String getDescricaoCompleta() {
		return nome
				+ (getMunicipio() != null ? " - "
						+ getMunicipio().getNome().toUpperCase() : "")
				+ (isProbasica() ? " - "
						+ getConvenio().getDescricao().toUpperCase() : "")		
				+ (modalidadeEducacao != null ? " - "
						+ modalidadeEducacao.getDescricao().toUpperCase() : "")
				+ (getUnidade() != null ? " - "
						+ getUnidade().getSigla() : "");
	}

	@Transient
	public String getDescricaoNivelEnsino() {
		return nome
				+ (getMunicipio() != null ? " - "
						+ getMunicipio().getNome().toUpperCase() : "")
				+ " - " + getNivelDescricao();
	}
	
	@Transient
	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(nivel);
	}
		
	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade_coordenacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Unidade getUnidadeCoordenacao() {
		return unidadeCoordenacao;
	}

	public void setUnidadeCoordenacao(Unidade unidadeCoordenacao) {
		this.unidadeCoordenacao = unidadeCoordenacao;
	}

	@Column(name = "cod_programa_capes")
	public String getCodProgramaCAPES() {
		return codProgramaCAPES;
	}

	public void setCodProgramaCAPES(String codProgramaCAPES) {
		this.codProgramaCAPES = codProgramaCAPES;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_curso_stricto")
	public TipoCursoStricto getTipoCursoStricto() {
		return tipoCursoStricto;
	}

	public void setTipoCursoStricto(TipoCursoStricto tipoCursoStricto) {
		this.tipoCursoStricto = tipoCursoStricto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_organizacao_administrativa")
	public OrganizacaoAdministrativa getOrganizacaoAdministrativa() {
		return organizacaoAdministrativa;
	}

	public void setOrganizacaoAdministrativa(
			OrganizacaoAdministrativa organizacaoAdministrativa) {
		this.organizacaoAdministrativa = organizacaoAdministrativa;
	}

	public String getReconhecimentoPortaria() {
		return reconhecimentoPortaria;
	}

	public void setReconhecimentoPortaria(String reconhecimentoPortaria) {
		this.reconhecimentoPortaria = reconhecimentoPortaria;
	}

	public Date getDataDecreto() {
		return dataDecreto;
	}

	public void setDataDecreto(Date dataDecreto) {
		this.dataDecreto = dataDecreto;
	}

	public Date getDou() {
		return dou;
	}

	public void setDou(Date dou) {
		this.dou = dou;
	}

	@Transient
	public Date getDataHistorico() {
		return dataHistorico;
	}

	public void setDataHistorico(Date dataHistorico) {
		this.dataHistorico = dataHistorico;
	}

	@Transient
	public boolean isReconhecimento() {
		return reconhecimento;
	}

	public void setReconhecimento(boolean reconhecimento) {
		this.reconhecimento = reconhecimento;
	}

	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "curso")
	public Set<CoordenacaoCurso> getCoordenacoesCursos() {
		return this.coordenacoesCursos;
	}

	public void setCoordenacoesCursos(Set<CoordenacaoCurso> coordenacaoCursos) {
		this.coordenacoesCursos = coordenacaoCursos;
	}

	@Column(name = "pode_matricular")
	public boolean isPodeMatricular() {
		return podeMatricular;
	}

	public void setPodeMatricular(boolean podeMatricular) {
		this.podeMatricular = podeMatricular;
	}

	@Transient
	public List<MatrizCurricular> getMatrizesCurriculares() {
		return this.matrizesCurriculares;
	}

	public void setMatrizesCurriculares(
			List<MatrizCurricular> matrizesCurriculares) {
		this.matrizesCurriculares = matrizesCurriculares;
	}

	/**
	 * M�todo respons�vel por retorna a situa��o do curso como Ativo ou Inativo, 
	 * conforme a lista de cursos da coordena��o do curso em transa��o.
	 * @return
	 */
	@Transient
	public CoordenacaoCurso getCoordenacaoAtiva() {
		CoordenacaoCurso ativa = null;
		for (CoordenacaoCurso cc : coordenacoesCursos) {
			if (cc.isAtivo()) {
				ativa = cc;
				break;
			}
		}
		return ativa;
	}

	@Column(name = "codigo_inep")
	public String getCodigoINEP() {
		return codigoINEP;
	}

	public void setCodigoINEP(String codigoINEP) {
		this.codigoINEP = codigoINEP;
	}

	@Column(name = "titulacao_masculino")
	public String getTitulacaoMasculino() {
		return titulacaoMasculino;
	}

	public void setTitulacaoMasculino(String titulacaoMasculino) {
		this.titulacaoMasculino = titulacaoMasculino;
	}
	
	@Column(name = "titulacao_feminino")
	public String getTitulacaoFeminino() {
		return titulacaoFeminino;
	}

	public void setTitulacaoFeminino(String titulacaoFeminino) {
		this.titulacaoFeminino = titulacaoFeminino;
	}
	
	/**
	 * M�todo respons�vel por setar o nome do curo em ASCII.
	 * @param nomeAscii
	 */
	public void setNomeAscii(String nomeAscii) {
		if (nomeAscii != null) {
			this.nomeAscii = StringUtils.toAscii(nomeAscii);
		}
	}
	
	@Transient
	public String getEnderecoUrlCapes(){
		return (ENDERECO_URL_CAPES + InstituicoesEnsino.COD_CAPES_UFRN + getCodProgramaCAPES());
	}

	@Column(name = "nome_ascii")
	public String getNomeAscii() {
		return nomeAscii;
	}

	@Transient
	public boolean isStricto() {
		return NivelEnsino.isAlgumNivelStricto(getNivel());
	}
	
	@Transient
	public boolean isResidencia() {
		return NivelEnsino.RESIDENCIA == nivel;
	}
	
	@Transient
	public boolean isMestrado() {
		return NivelEnsino.MESTRADO == nivel;
	}
	
	@Transient
	public boolean isMestradoProfissional() {
		return NivelEnsino.MESTRADO == nivel 
			&& ( !ValidatorUtil.isEmpty(tipoCursoStricto) 
					&& tipoCursoStricto.equals(TipoCursoStricto.MESTRADO_PROFISSIONAL) );
	}
	
	@Transient
	public boolean isDoutorado() {
		return NivelEnsino.DOUTORADO == nivel;
	}
	
	@Transient
	public boolean isGraduacao() {
		return NivelEnsino.GRADUACAO == getNivel();
	}
	
	@Transient
	public boolean isTecnico() {
		return NivelEnsino.TECNICO == getNivel();
	}
	
	@Transient
	public boolean isFormacaoComplementar() {
		return NivelEnsino.FORMACAO_COMPLEMENTAR == getNivel();
	}
	
	@Transient
	public boolean isLato() {
		return NivelEnsino.LATO == getNivel();
	}
	
	@Transient
	public boolean isMedio() {
		return NivelEnsino.MEDIO == getNivel();
	}

	/**
	 * M�todo respons�vel pela compara��o de curso utilizando os atributos descricao e municipio.
	 */
	public int compareTo(Curso c) {
		return new CompareToBuilder()
			.append(this.getDescricao(), c.getDescricao())
			.append(this.getMunicipio(), c.getMunicipio())
			.toComparison();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	@Column(name = "website")
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Column(name = "campo_atuacao")
	public String getCampoAtuacao() {
		return campoAtuacao;
	}

	public void setCampoAtuacao(String campoAtuacao) {
		this.campoAtuacao = campoAtuacao;
	}

	@Column(name = "perfil_profissional")
	public String getPerfilProfissional() {
		return perfilProfissional;
	}

	public void setPerfilProfissional(String perfilProfissional) {
		this.perfilProfissional = perfilProfissional;
	}

	@Column(name = "competencias_habilidades")
	public String getCompetenciasHabilidades() {
		return competenciasHabilidades;
	}

	public void setCompetenciasHabilidades(String competenciasHabilidades) {
		this.competenciasHabilidades = competenciasHabilidades;
	}

	@Column(name = "metodologia")
	public String getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	@Column(name = "gestao_curso")
	public String getGestaoCurso() {
		return gestaoCurso;
	}

	public void setGestaoCurso(String gestaoCurso) {
		this.gestaoCurso = gestaoCurso;
	}

	@Column(name = "avaliacao_curso")
	public String getAvaliacaoCurso() {
		return avaliacaoCurso;
	}

	public void setAvaliacaoCurso(String avaliacaoCurso) {
		this.avaliacaoCurso = avaliacaoCurso;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_sesu")
	public AreaSesu getAreaSesu() {
		return areaSesu;
	}

	public void setAreaSesu(AreaSesu areaSesu) {
		this.areaSesu = areaSesu;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_ciclo_formacao")
	public TipoCicloFormacao getTipoCicloFormacao() {
		return tipoCicloFormacao;
	}
	
	public void setTipoCicloFormacao(TipoCicloFormacao tipoCicloFormacao) {
		this.tipoCicloFormacao = tipoCicloFormacao;
	}
	
	@Transient
	public void setDescricaoNivel(String descricaoNivel) {
		this.descricaoNivel = descricaoNivel;
	}
	
	/**
	 * Retorna a descri��o do n�vel. Se o n�vel ainda n�o tiver
	 * uma descri��o, ent�o � criada e retornada.
	 * @return descri��o do n�vel
	 */
	@Transient
	public String getDescricaoNivel() {
		if( !isEmpty(nivel) && isEmpty(descricaoNivel) ){
			descricaoNivel = NivelEnsino.getDescricao(nivel);
		}	
		return descricaoNivel;
	}
	
	@Column(name = "rede", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isRede() {
		return rede;
	}

	public void setRede(boolean rede) {
		this.rede = rede;
	}

	@OneToMany(cascade = {}, mappedBy = "curso")
	public List<DadosCursoRede> getCursoInstituicoesEnsino() {
		return cursoInstituicoesEnsino;
	}

	public void setCursoInstituicoesEnsino(
			List<DadosCursoRede> cursoInstituicoesEnsino) {
		this.cursoInstituicoesEnsino = cursoInstituicoesEnsino;
	}

	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_unidade_referencia_mcn")
	public Unidade getUnidadeReferenciaMcn() {
		return unidadeReferenciaMcn;
	}

	public void setUnidadeReferenciaMcn(Unidade unidadeReferenciaMcn) {
		this.unidadeReferenciaMcn = unidadeReferenciaMcn;
	}

	@Column(name = "permitealunosvariosvinculos", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isPermiteAlunosVariosVinculos() {
		return permiteAlunosVariosVinculos;
	}

	public void setPermiteAlunosVariosVinculos(boolean permiteAlunosVariosVinculos) {
		this.permiteAlunosVariosVinculos = permiteAlunosVariosVinculos;
	}

//	@Transient
//	public MetodologiaAvaliacao getMetodologiaEad() {
//		return metodologiaEad;
//	}
//
//	public void setMetodologiaEad(MetodologiaAvaliacao metodologiaEad) {
//		this.metodologiaEad = metodologiaEad;
//	}

	
}
