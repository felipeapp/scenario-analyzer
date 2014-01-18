/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe mãe de todos os tipos de Cursos da instituição. É a base para todos os demais tipos de cursos no sistema.
 */
@Entity
@Table(name = "curso", uniqueConstraints = {})
@Inheritance(strategy = InheritanceType.JOINED)
public class Curso implements Validatable, Comparable<Curso> {

	/** Constante responsável por indicar o identificador do curso de administração a distância.	 */
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

	/** Referência a unidade do curso */
	private Unidade unidade;
	
	/** Referência uma segunda unidade que o curso pode estar vinculado */
	private Unidade unidade2;

	/** No caso de curso de stricto, esse já é o código da CAPES */
	private String codigo;

	/** Nome do curso */
	private String nome;

	/** Diz se o curso está ativo */
	private boolean ativo = true;

	/** Nível do curso (Graduação - G, Mestrado E, etc */
	private char nivel;
	
	/** Descrição do Nível do curso (Graduação, Mestrado, Técnico etc */
	@Transient
	private String descricaoNivel;

	/** Município do curso */
	private Municipio municipio;

	/** Natureza do curso */
	private NaturezaCurso naturezaCurso;

	/** Modalidade de educação do curso (presencial, a distância, etc) */
	private ModalidadeEducacao modalidadeEducacao;

	/** Tipo de oferta do curso (semestral, anual, etc) */
	private TipoOfertaCurso tipoOfertaCurso;
	
	/** Tipo de oferta das disciplinas do curso (semestral, anual, etc) */
	private TipoOfertaDisciplina tipoOfertaDisciplina;

	/** Coordenações do curso */
	private Set<CoordenacaoCurso> coordenacoesCursos = new HashSet<CoordenacaoCurso>(0);

	/** Matrizes curriculares do curso */
	private List<MatrizCurricular> matrizesCurriculares;

	/** Convênio do curso, caso tenha (probásica, pronera, etc) */
	private ConvenioAcademico convenio;

	/** Área de conhecimento cnpq do curso */
	private AreaConhecimentoCnpq areaCurso;

	/** Área de conhecimento do vestibular */
	private AreaConhecimentoVestibular areaVestibular;

	/** Titulação no gênero masculino que os discentes do curso stricto sensu obtém ao concluírem. */
	private String titulacaoMasculino;
	
	/** Titulação no gênero feminino que os discentes do curso stricto sensu obtém ao concluírem. */
	private String titulacaoFeminino;
	
	/** Arquivo do projeto político-pedagógico */
	private Integer idArquivo;

	/** Campo de controle utilizado nas jsp's */
	private boolean selecionado;

	/** Unidade da coordenação do curso */
	private Unidade unidadeCoordenacao;

	/** String com código do programa CAPES, apenas para cursos de pós graduação */
	private String codProgramaCAPES;

	/** Tipo de curso STRICTO (mestrado profissional, acadêmico, doutorado, etc) */
	private TipoCursoStricto tipoCursoStricto;

	/** Nome sem acentos */
	private String nomeAscii;

	/**
	 * Define o tipo da organização administrativa de um curso, só existe esse
	 * atributo para curso stricto
	 */
	private OrganizacaoAdministrativa organizacaoAdministrativa;

	/* Dados de Reconhecimento do Curso */
	/** Portaria de reconhecimento do curso  (graduação) / Portaria MEC (Pós-graduação, #33133)  */
	private String reconhecimentoPortaria;
	/** Data do decreto do reconhecimento /  Data de Início de Funcionamento  (Pós-graduação, #33133) */
	private Date dataDecreto;
	/** Dou de reconhecimento (graduação) / Data de publicação (Pós-graduação, #33133) */
	private Date dou;
	/** Campo transiente de controle  */
	private Date dataHistorico;
	
	/** Indica se o curso possui reconhecimento. (Atributo transiente) */
	private boolean reconhecimento;

	/** Código INEP do curso */
	private String codigoINEP;

	/**
	 * Este atributo diz se a coordenação de curso pode realizar matrículas dos
	 * alunos deste curso da mesma forma que alunos de probásica e alunos
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

	/** Registro entrada da última atualização */
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data da última atualização */
	@AtualizadoEm
	private Date dataAtualizacao;
	
	/** Link para o site contendo mais informação sobre o curso. */
	private String website;
	/** Áreas que o profissional, ao concluir o curso, pode trabalhar. */
	private String campoAtuacao;
	/** Capacidade (lista de aptidões) que o aluno possui ao terminar o curso. */
	private String perfilProfissional;
	/** As competências e habilidades que o aluno possui ao terminar o curso. */
	private String competenciasHabilidades;
	/** Metodologia a ser adotada para a consecução da proposta pedagógica.*/
	private String metodologia;
	/** As formas de gestão do curso inseridas no projeto político pedagógico. */
	private String gestaoCurso;
	/** A avaliação do curso inserida no projeto político pedagógico. */
	private String avaliacaoCurso;
	
	/**
	 * Atributo TRANSIENT que guarda a coordenação ativa atual.
	 *
	 * @author Edson Aníbal
	 */
	private CoordenacaoCurso coordenacaoAtual;
	
	/**
	 * Ciclo de formação do curso, podendo ser um ou dois ciclos.
	 */
	private TipoCicloFormacao tipoCicloFormacao;
	
	/** Campo de controle para informar se o curso de stricto sensu pertence a uma rede de ensino. */
	private boolean rede = false;
	
	/** Associação de curso a instituições de ensino. */
	private List<DadosCursoRede> cursoInstituicoesEnsino;

	/** Caso o curso não tenha concluintes, calcular média de MCs dos alunos dessa unidade, para cálculo do MCN */
	private Unidade unidadeReferenciaMcn;

	/** Indica se uma pessoa pode ter mais de um vínculo ativo de aluno nesse curso simultaneamente */
	private boolean permiteAlunosVariosVinculos = false;
	
//	private MetodologiaAvaliacao metodologiaEad;
	
	/**
	 * Atributo TRANSIENT que guarda a coordenação ativa atual.
	 *
	 * @author Edson Aníbal
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
	 * Método responsável por retornar a string contendo o nome completo do curso junto com a sigla da unidade do mesmo.
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
	 * Método utilizado para setar o nome do curso
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
	 * Os filhos devem implementar esse método
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
		ValidatorUtil.validateRequired(municipio, "Município de Andamento do Curso", erros);
		ValidatorUtil.validateRequired(modalidadeEducacao, "Forma de Participação do Aluno", erros);
		ValidatorUtil.validateRequired(unidade, "Unidade Responsável", erros);
		ValidatorUtil.validateRequired(tipoOfertaDisciplina, "Tipo de Oferta de Disciplina", erros);
		
		
		if ((!ValidatorUtil.isEmpty(unidade) && !ValidatorUtil.isEmpty(unidade2)) && unidade.equals(unidade2)) {
			erros.addErro("A Unidade Responsável 2 não pode ser igual a Unidade Responsável.");
		}

		if (nivel == NivelEnsino.GRADUACAO) {
			ValidatorUtil.validateRequired(areaCurso, "Área do Curso", erros);
			ValidatorUtil.validateRequired(naturezaCurso, "Natureza do Curso", erros);
			ValidatorUtil.validateRequired(areaVestibular, "Área de Conhecimento do Vestibular", erros);
			ValidatorUtil.validateRequired(areaSesu, "Área SESu", erros);
			ValidatorUtil.validateRequired(tipoCicloFormacao, "Tipo de Ciclo de Formação", erros);

		}
		
		if (NivelEnsino.isAlgumNivelStricto(nivel)) {
			// ValidatorUtil.validateRequired(codigo, "Código CAPES do Curso",
			// erros);
			ValidatorUtil.validateRequired(codProgramaCAPES, "Código CAPES do Programa", erros);
			ValidatorUtil.validateRequired(tipoCursoStricto, "Categoria", erros);
			ValidatorUtil.validateRequired(areaCurso, "Área do Curso", erros);
			ValidatorUtil.validateRequired(organizacaoAdministrativa, "Organização Administrativa", erros);
			ValidatorUtil.validateRequired(titulacaoMasculino, "Titulação no Gênero Masculino", erros);
			ValidatorUtil.validateRequired(titulacaoFeminino, "Titulação no Gênero Feminino", erros);
			ValidatorUtil.validateRequired(dataDecreto, "Data de Início de Funcionamento", erros);
			
			tipoOferta = "Periodicidade de Ingresso";
			
		} else {
			ValidatorUtil.validateRequired(unidadeCoordenacao, "Unidade da Coordenação", erros);
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
	 * Método utilizado para verificar se o curso tem convenio estabelecido do tipo PRO-BÁSICA
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
	 * Método utilizado para verificar se o curso tem convenio estabelecido do tipo PARFOR
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
	 * Método responsável por retornar o nome do curso de Stricto concatenado o nível de ensino do mesmo.
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
	 * Método responsável por retornar o nome do curso de Stricto concatenado o tipo de curso stricto.
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
	 * Método responsável por retornar o nome do curso no formato simples, apenas o nome e sem derivações.
	 */
	@Transient
	public String getNomeSimples(){
		return this.nome;
	}
	
	
	/**
	 * Método responsável por retornar o nome do curso de Stricto de forma completa,
	 * com diversas informações sobre o mesmo concatenadas ao nome, como: município e tipo de Curso.
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
	 * Método responsável por retorna a situação do curso como Ativo ou Inativo, 
	 * conforme a lista de cursos da coordenação do curso em transação.
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
	 * Método responsável por setar o nome do curo em ASCII.
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
	 * Método responsável pela comparação de curso utilizando os atributos descricao e municipio.
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
	 * Retorna a descrição do nível. Se o nível ainda não tiver
	 * uma descrição, então é criada e retornada.
	 * @return descrição do nível
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
