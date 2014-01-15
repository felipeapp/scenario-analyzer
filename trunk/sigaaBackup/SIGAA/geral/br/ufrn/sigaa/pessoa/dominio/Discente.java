/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '29/04/2008'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.ValidacaoVinculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * Classe mãe de todos os tipos de alunos. Usada como base para
 * DiscenteGraduacao, DiscenteTecnico e DiscenteStricto.
 */
@Entity
@Table(name = "discente", uniqueConstraints = {})
public class Discente implements PersistDB, DiscenteAdapter {

	/**
	 * Discente tipo regular
	 */
	public static final int REGULAR = 1;

	/**
	 * Discente tipo especial
	 */
	public static final int ESPECIAL = 2;
	
	/** 
	 * Tipo de Discente utilizado, para os casos onde o discente faz parte de algum 
	 * programa em rede de ensino e pertence a outra instituição de ensino
	 */
	public static final int EM_ASSOCIACAO = 3;

	/** Id do discente */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="discente_seq") })
	@Column(name = "id_discente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Ano de ingresso do discente */
	@Column(name = "ano_ingresso", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer anoIngresso;

	/** Período de ingresso do discente */
	@Column(name = "periodo_ingresso", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer periodoIngresso;

	/** Período atual que o discente se encontra, não conta os período que o discente esteve trancado */
	@Column(name = "periodo_atual")
	private Integer periodoAtual;

	/** Número de matrícula do aluno */
	@Column(name = "matricula", unique = false, nullable = true, insertable = true, updatable = true)
	private Long matricula;
	
	/** Número de matrícula antigo do aluno, utilizado para armazenar a matrícula que o aluno perde quando não comparece ao cadastramento */
	@Column(name = "matricula_antiga", unique = false, nullable = true, insertable = true, updatable = true)
	private Long matriculaAntiga;

	/** Observações que o DAE pode realizar nos discentes */
	@Column(name = "observacao", unique = false, nullable = true, insertable = true, updatable = true, length = 1000)
	private String observacao;

	/** Indica o nível do aluno, se é graduação, técnico, Pós-graduação.
	 * Os níveis estão indicados na classe <code>br.ufrn.academico.dominio.NivelEnsino</code> 
	 *
	 * <ul>
	 * 		<li>INFANTIL = 'I';</li>
	 * 		<li>MEDIO = 'M';</li>
	 * 		<li>TECNICO = 'T';</li>
	 * 		<li>BASICO = 'B';</li>
	 * 		<li>GRADUACAO = 'G';</li>
	 * 		<li>LATO = 'L';</li>
	 * 		<li>STRICTO = 'S';</li>
	 * 		<li>MESTRADO = 'E';</li>
	 * 		<li>DOUTORADO = 'D';</li>
	 * 		<li>RESIDENCIA_MEDICA = 'R';</li>
	 * </ul>
	 * 
	 */
	@Column(name = "nivel", unique = false, nullable = false, insertable = true, updatable = true)
	private char nivel;

	/**
	 * Atributo responsável pela lista dos índices acadêmicos do discente.
	 */
	@OneToMany(mappedBy="discente", fetch=FetchType.LAZY)
	private List<IndiceAcademicoDiscente> indices;
	
	/** StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.CONCLUIDO, etc.. 
	 * Olhar a classe StatusDiscente para obter todos os valores
	 * 
	 * 	<ul>
	 * 		<li>ATIVO = 1;</li>
	 * 		<li>CADASTRADO = 2;</li>
	 * 		<li>CONCLUIDO = 3;</li>
	 * 		<li>AFASTADO = 4;</li>
	 * 		<li>TRANCADO = 5;</li>
	 * 		<li>CANCELADO = 6;</li>
	 * 		<li>JUBILADO = 7;</li>
	 * 		<li>FORMANDO = 8;</li>
	 * 		<li>GRADUANDO = 9;</li>
	 * 		<li>EXCLUIDO = 10;</li>
	 * 		<li>EM_HOMOLOGACAO = 11; (apenas para discentes stricto)</li>
	 *	</ul>
	 */
	private int status;

	/** Referência ao currículo do discente */
	@ManyToOne()
	@JoinColumn(name = "id_curriculo")
	private Curriculo curriculo;

	/** Tipo de discente (regular = 1, especial = 2) */
	private Integer tipo;

	/** Carga horária integralizada do discente */
	@Column(name = "ch_integralizada", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer chIntegralizada;

	/** Prazo máximo de conclusão do curso */
	@Column(name = "prazo_conclusao", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer prazoConclusao;

	/** Data da colação de grau do discente */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_colacao_grau", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataColacaoGrau;

	/** Referência a entidade pessoa que contém os dados pessoais */
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", unique = false, nullable = true, insertable = true, updatable = true)
	private Pessoa pessoa = new Pessoa();

	/** Referência a unidade gestora do discente */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_gestora_academica", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade gestoraAcademica;

	/** Forma de ingresso do aluno no curso */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forma_ingresso", unique = false, nullable = true, insertable = true, updatable = true)
	private FormaIngresso formaIngresso = new FormaIngresso();

	/** Curso do discente */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	private Curso curso = new Curso();

	/** Referência ao arquivo da foto do discente */
	@Column(name = "id_foto")
	private Integer idFoto;

	/** Referência ao perfil pessoa do discente */
	@Transient
	private PerfilPessoa perfil;

	/** Referência ao perfil do discente, não existe relacionamento porque fica em outro banco */
	@Column(name = "id_perfil")
	private Integer idPerfil;

	/** Usuário do discente */
	@Transient
	private Usuario usuario;

	/** Data de cadastro do discente */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro de entrada do discente */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada criadoPor;

	/** Atributo que indica se o discente está na operação de matrícula do mesmo. */
	@Transient
	private boolean matricular;

	/** Retorna a String contendo os trancamentos do discente.*/
	@Transient
	private String trancamentos;

	/** Atributo responsável por retornar o semestre atual.*/
	@Transient
	private int semestreAtual;

	/** Atributo responsável por indicar a movimentação de saída do discente.*/
	@Transient
	private MovimentacaoAluno movimentacaoSaida;

	/** Atributo referente as prorrogações de ensino do discente.*/
	@Transient
	private Integer prorrogacoes;

	/** Atributo utilizado para indicar se o discente está sendo selecionado numa determinada operação de caso de uso.*/
	@Transient
	private boolean selecionado = false;
	
	/** Atributo utilizado para indicar se o discente está marcado com alguma situação especial numa determinada operação de caso de uso.*/
	@Transient
	private boolean marcado = false;
	
	/** Informa se o Discente é aluno prioritário ou não, de acordo com a pontuação do Cadastro Único; */
	@Transient
	private boolean carente = false;
	
	/** Informa se o Discente foi removido de seu grupo; */
	@Transient
	private boolean removidoGrupo;
	
	/** Ano da última matrícula válida */
	@Transient
	private Integer anoUltimaMatriculaValida;
	
	/** Período da última matrícula válida */
	@Transient
	private Integer periodoUltimaMatriculaValida;
	
	/** Id do arquivo digital do histórico, caso discente antigo */
	@Column(name = "id_historico_digital")
	private Integer idHistoricoDigital;
	
	/** Classe que representa quando o vínculo do discente ingressante foi validada. */
	@Transient
	private ValidacaoVinculo validacaoVinculo;

	public FormaIngresso getFormaIngresso() {
		return this.formaIngresso;
	}

	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	public Integer getChIntegralizada() {
		return this.chIntegralizada;
	}

	public void setChIntegralizada(Integer chIntegralizada) {
		this.chIntegralizada = chIntegralizada;
	}

	public Integer getPrazoConclusao() {
		return this.prazoConclusao;
	}

	public void setPrazoConclusao(Integer prazoConclusao) {
		this.prazoConclusao = prazoConclusao;
	}

	public Date getDataColacaoGrau() {
		return this.dataColacaoGrau;
	}

	public void setDataColacaoGrau(Date dataColacaoGrau) {
		this.dataColacaoGrau = dataColacaoGrau;
	}

	/** Matrículas em disciplinas */
	@Transient
	private Collection<MatriculaComponente> matriculasDisciplina = new HashSet<MatriculaComponente>(
			0);
	
	/** Solicitações de Apoio ao Nee */
	@Transient
	private Collection<SolicitacaoApoioNee> solicitacoesApoioNee = new HashSet<SolicitacaoApoioNee>(
			0);

	// Constructors

	/** default constructor */
	public Discente() {
	}

	/** minimal constructor */
	public Discente(int idDiscente) {
		this.id = idDiscente;
	}

	public Discente(int id, Long matricula, int status, char nivel) {
		this.id = id;
		this.matricula = matricula != null ? matricula : 0l;
		this.status = status;
		this.nivel = nivel;
	}

	public Discente(int id, Long matricula, String nome) {
		this.id = id;
		this.matricula = matricula != null ? matricula : 0l;
		this.pessoa.setNome(nome);
	}

	public Discente(int id, Long matricula, String nome, String email) {
		this.id = id;
		this.matricula = matricula != null ? matricula : 0l;
		this.pessoa.setNome(nome);
		this.pessoa.setEmail(email);
	}

	public Discente(int id, long matricula, String nome, int status) {
		this.id = id;
		this.matricula = matricula;
		this.pessoa.setNome(nome);
		this.status = status;
	}
	
	public Discente(int id, long matricula, String nome, int status, Long cpf_cnpj) {
		this.id = id;
		this.matricula = matricula;
		this.pessoa.setNome(nome);
		this.status = status;
		this.pessoa.setCpf_cnpj(cpf_cnpj);
	}

	public Discente(int id, long matricula, String nome, int status, char nivel) {
		this.id = id;
		this.matricula = matricula;
		this.pessoa.setNome(nome);
		this.status = status;
		this.nivel = nivel;
	}

	public Discente(int id, Long matricula, String nome, int status,
			Integer idCurso, String curso) {
		this.id = id;
		this.matricula = matricula;
		this.pessoa.setNome(nome);
		this.status = status;
		if (idCurso != null)
			this.curso.setId(idCurso);
		this.curso.setNome(curso);
	}

	public Discente(int id, long matricula, String nome, int status,
			char nivel, Integer idGestora, Integer idCurso, String curso) {
		this.id = id;
		this.matricula = matricula;
		this.pessoa.setNome(nome);
		this.status = status;
		this.nivel = nivel;
		if (idGestora != null) {
			this.gestoraAcademica = new Unidade();
			this.gestoraAcademica.setId(idGestora);
		}
		if (idCurso != null)
			this.curso.setId(idCurso);
		this.curso.setNome(curso);
	}

	// Property accessors

	public int getId() {
		return this.id;
	}

	public void setId(int idDiscente) {
		this.id = idDiscente;
	}

	public Pessoa getPessoa() {
		return this.pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Integer getAnoIngresso() {
		return this.anoIngresso;
	}

	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public Integer getPeriodoIngresso() {
		return this.periodoIngresso;
	}

	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	public Long getMatricula() {
		return this.matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Collection<MatriculaComponente> getMatriculasDisciplina() {
		return this.matriculasDisciplina;
	}

	public void setMatriculasDisciplina(
			Collection<MatriculaComponente> matriculaDisciplinas) {
		this.matriculasDisciplina = matriculaDisciplinas;
	}

	public Collection<SolicitacaoApoioNee> getSolicitacoesApoioNee() {
		return solicitacoesApoioNee;
	}

	public void setSolicitacoesApoioNee(
			Collection<SolicitacaoApoioNee> solicitacoesApoioNee) {
		this.solicitacoesApoioNee = solicitacoesApoioNee;
	}

	public Unidade getGestoraAcademica() {
		return gestoraAcademica;
	}

	public void setGestoraAcademica(Unidade unidadeGestora) {
		this.gestoraAcademica = unidadeGestora;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj != null) {
			Discente other = (Discente) obj;
			if (other.getId() == this.id)
				return true;
			if (other.matricula.equals(this.matricula))
				return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, matricula);
	}

	/**
	 * Retorna string contento nome e matrícula
	 *
	 * @return
	 */
	@Transient
	public String nomeMatricula() {
		return getPessoa().getNome() + "(Mat. " + getMatricula() + ")";
	}

	/**
	 * Retorna string contendo matrícula e nome
	 *
	 * @return
	 */
	@Transient
	public String getMatriculaNome() {
		return getMatricula() + " - " + getPessoa().getNome();
	}

	@Transient
	public String getMatriculaNomeNivel() {
		return getMatricula() + " - " + getPessoa().getNome() + " ("
				+ NivelEnsino.getDescricao(nivel) + ") - " + getStatusString();
	}

	@Transient
	public String getMatriculaNomeStatus() {
		return getMatricula() + " - " + getPessoa().getNome() + " ("
				+ getStatusString() + ")";
	}

	@Transient
	public String getNivelDesc() {
		return NivelEnsino.getDescricao(nivel);
	}

	public char getNivel() {
		return nivel;
	}

	@Transient
	public String getNivelStr() {
		return String.valueOf(nivel);
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Transient
	public String getNome() {
		return ((this.pessoa.getNome() == null) ? "" : this.pessoa.getNome());
	}

	@Transient
	public int getAnoEntrada() {
		return getAnoIngresso();
	}

	/**
	 * Retorna true se está na operação de matrícula e false se não
	 * 
	 * @return the matricular
	 */

	public boolean isMatricular() {
		return matricular;
	}

	/**
	 * Seta se está ou não na operação de matrícula
	 * 
	 * @param matricular
	 *            the matricular to set
	 */
	public void setMatricular(boolean matricular) {
		this.matricular = matricular;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	@Transient
	public String getStatusString() {
		return StatusDiscente.getDescricao(status);
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public Curriculo getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	/** Retorna um String do tipo de discente  */
	@Transient
	public String getTipoString() {
		String retorno = "INDEFINIDO";
		if (tipo == null)
			return retorno;

		switch (tipo.intValue()) {
		case REGULAR:
			retorno = "REGULAR";
			break;
		case ESPECIAL:
			retorno = "ESPECIAL";
			break;
		case EM_ASSOCIACAO:
			retorno = "EM ASSOCIAÇÃO";
			break;
		default:
			retorno = "INDEFINIDO";
			break;
		}

		return retorno;
	}

	@Override
	public String toString() {
		return (matricula != null ? matricula + " - " : "")
				+ (getPessoa().getNome() != null ? getPessoa().getNome() : "");
	}

	/** Retorna o id da foto */
	public Integer getIdFoto() {
		if (idFoto == null && usuario != null)
			return usuario.getIdFoto();
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	public Integer getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	@Transient
	public String getAnoPeriodoIngresso() {
		return anoIngresso + "." + periodoIngresso;
	}

	/**
	 * Retorna a data de cadastro do discente 
	 * @return the dataCadastro
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * Seta a data de cadastro do discente
	 * @param dataCadastro
	 *            the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Transient
	public boolean isRegular() {
		return tipo != null && tipo == REGULAR;
	}

	@Transient
	public boolean isEspecial() {
		return tipo != null && tipo == ESPECIAL;
	}
	
	@Transient
	public boolean isEmAssociacao() {
		return tipo != null && tipo == EM_ASSOCIACAO;
	}
	
	@Transient
	public boolean isGraduacao() {
		return nivel == NivelEnsino.GRADUACAO;
	}

	@Transient
	public boolean isTecnico() {
		return nivel == NivelEnsino.TECNICO;
	}
	
	/** Indica se o discente é de metrópole digital.
	 * @see br.ufrn.sigaa.ensino.dominio.DiscenteAdapter#isMetropoleDigital()
	 */
	@Transient
	public boolean isMetropoleDigital () {
		if (getCurso() == null)
			return false;
		
		Integer idCursoIMD = ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO);
		return getCurso().getId() == idCursoIMD;
	}
	
	@Transient
	public boolean isMedio() {
		return nivel == NivelEnsino.MEDIO;
	}
	
	@Transient
	public boolean isFormacaoComplementar() {
		return nivel == NivelEnsino.FORMACAO_COMPLEMENTAR;
	}
	
	@Transient
	public boolean isInfantil() {
		return nivel == NivelEnsino.INFANTIL;
	}

	@Transient
	public boolean isStricto() {
		return NivelEnsino.isAlgumNivelStricto(nivel);
	}
	
	@Transient
	public boolean isResidencia() {
		return nivel == NivelEnsino.RESIDENCIA;
	}

	@Transient
	public boolean isLato() {
		return nivel == NivelEnsino.LATO;
	}
	
	@Transient
	public boolean isDoutorado(){
		return nivel == NivelEnsino.DOUTORADO;
	}
	
	@Transient
	public boolean isMestrado(){
		return nivel == NivelEnsino.MESTRADO;
	}

	/**
	 * @return the usuario
	 *
	 * public Usuario getUsuario() { return usuario; }
	 *
	 * /**
	 * @param usuario
	 *            the usuario to set
	 *
	 * public void setUsuario(Usuario usuario) { this.usuario = usuario; }
	 */

	/** Retorna true se o curso é a distância, e false se não é. */
	public boolean isDiscenteEad() {
		return curso != null && curso.isADistancia();
	}

	public boolean isAtivo() {
		return StatusDiscente.isAtivo(status);
	}

	public boolean isExcluido() {
		return StatusDiscente.EXCLUIDO == status;
	}
	
	public boolean isTrancado() {
		return StatusDiscente.TRANCADO == status;
	}
	
	public boolean isDefendido() {
		return StatusDiscente.DEFENDIDO == status;
	}

	public String getTrancamentos() {
		return trancamentos;
	}

	public void setTrancamentos(String trancamentos) {
		this.trancamentos = trancamentos;
	}

	public int getSemestreAtual() {
		return semestreAtual;
	}

	public void setSemestreAtual(int semestreAtual) {
		this.semestreAtual = semestreAtual;
	}

	public MovimentacaoAluno getMovimentacaoSaida() {
		return movimentacaoSaida;
	}

	public void setMovimentacaoSaida(MovimentacaoAluno movimentacaoSaida) {
		this.movimentacaoSaida = movimentacaoSaida;
	}

	public Integer getProrrogacoes() {
		return prorrogacoes;
	}

	public void setProrrogacoes(Integer prorrogacoes) {
		this.prorrogacoes = prorrogacoes;
	}

	/** Retorna true se a movimentação é do tipo 'apostilamento'  */
	public boolean isApostilamento() {
		if (movimentacaoSaida != null
				&& Boolean.TRUE.equals(movimentacaoSaida.getApostilamento()))
			return true;
		return false;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public RegistroEntrada getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public boolean isSelecionado() {
		return this.selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/** Retorna true se o discente está cadastrado no Sigaa */
	public boolean isCadastrado(int ano, int periodo) {
		return status == StatusDiscente.CADASTRADO
				|| (anoIngresso == ano && periodoIngresso == periodo);
	}

	public boolean isConcluido() {
		return status == StatusDiscente.CONCLUIDO;
	}
	
	public boolean isCancelado() {
		return status == StatusDiscente.CANCELADO;
	}
	
	public boolean isPendenteCadastro() {
		return status == StatusDiscente.PENDENTE_CADASTRO;
	}
	
	public boolean isPreCadastro() {
		return status == StatusDiscente.PRE_CADASTRADO;
	}

	public void setCarente(boolean carente) {
		this.carente = carente;
	}
	
	public boolean isCarente() {
		return carente;
	}
	
	/** Retorna o nome do aluno concatenado com a matrícula */
	@Transient
	public String getMatriculaNomeFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div style='width:30px; float: left; text-align: right'>"
				+ matricula
				+ "&nbsp;&nbsp;</div><div style='margin-left: 80px'>"
				+ pessoa.getNome() + "</div></div>";
	}

	/** Retorna o nome do aluno concatenado com a matrícula e a situação do mesmo. */
	@Transient
	public String getMatriculaNomeSituacaoFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div style='width:30px; float: left; text-align: right'>"
				+ matricula
				+ "&nbsp;&nbsp;</div><div style='margin-left: 90px'>"
				+ pessoa.getNome()
				+ "</div><div style='margin-left: 60px'> - "
				+ StatusDiscente.getDescricao(status) + "</div></div>";
	}

	/** Retorna o nome do aluno concatenado com a matrícula e o nível de ensino do mesmo. */
	@Transient
	public String getMatriculaNomeNivelFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div style='width:30px; float: left; text-align: right'>"
				+ matricula
				+ "&nbsp;&nbsp;</div><div style='margin-left: 90px'>"
				+ pessoa.getNome()
				+ " ("
				+ NivelEnsino.getDescricao(nivel)
				+ ") </div><div style='margin-left: 90px'>"
				+ StatusDiscente.getDescricao(status) + "</div></div>";
	}

	public Integer getPeriodoAtual() {
		return periodoAtual;
	}

	public void setPeriodoAtual(Integer periodoAtual) {
		this.periodoAtual = periodoAtual;
	}
	
	/** Retorna a unidade ao qual o discente pertence */
	@Transient
	public Unidade getUnidade(){
		if( this.isGraduacao() && this.isRegular() && !isEmpty( this.getCurso() ) )
			return this.getCurso().getUnidade();
		else if( !this.isGraduacao() || !this.isRegular() )
			return this.getGestoraAcademica();
		else 
			return null;
	}

	public Integer getIdHistoricoDigital() {
		return idHistoricoDigital;
	}

	public void setIdHistoricoDigital(Integer idHistoricoDigital) {
		this.idHistoricoDigital = idHistoricoDigital;
	}

	@Override
	public Discente getDiscente() {
		return this;
	}

	public List<IndiceAcademicoDiscente> getIndices() {
		return indices;
	}
	
	/** Retorna o indice acadêmico associado ao discente */
	public IndiceAcademicoDiscente getIndice(int idIndiceAcademico) {
		if (indices != null) {
			for (IndiceAcademicoDiscente indiceDiscente : indices) {
				if (indiceDiscente.getIndice().getId() == idIndiceAcademico)
					return indiceDiscente;
			}
		}
		return null;
	}

	public void setIndices(List<IndiceAcademicoDiscente> indices) {
		this.indices = indices;
	}

	public Long getMatriculaAntiga() {
		return matriculaAntiga;
	}

	public void setMatriculaAntiga(Long matriculaAntiga) {
		this.matriculaAntiga = matriculaAntiga;
	}

	public void setRemovidoGrupo(boolean removidoGrupo) {
		this.removidoGrupo = removidoGrupo;
	}

	public boolean isRemovidoGrupo() {
		return removidoGrupo;
	}

	public Integer getAnoUltimaMatriculaValida() {
		return anoUltimaMatriculaValida;
	}

	public void setAnoUltimaMatriculaValida(Integer anoUltimaMatriculaValida) {
		this.anoUltimaMatriculaValida = anoUltimaMatriculaValida;
	}

	public Integer getPeriodoUltimaMatriculaValida() {
		return periodoUltimaMatriculaValida;
	}

	public void setPeriodoUltimaMatriculaValida(Integer periodoUltimaMatriculaValida) {
		this.periodoUltimaMatriculaValida = periodoUltimaMatriculaValida;
	}

	public void setMarcado(boolean marcado) {
		this.marcado = marcado;
	}

	public boolean isMarcado() {
		return marcado;
	}

	public void setValidacaoVinculo(ValidacaoVinculo validacaoVinculo) {
		this.validacaoVinculo = validacaoVinculo;
	}

	public ValidacaoVinculo getValidacaoVinculo() {
		return validacaoVinculo;
	}

	
}