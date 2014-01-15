/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 26/07/2007
 * 
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/**
 * Entidade, especialização de um {@link Discente}, que armazena os dados específicos dos alunos de Graduação.
 *
 * @author Gleydson
 */
@Entity
@Table(name = "discente_graduacao", schema = "graduacao")
public class DiscenteGraduacao implements DiscenteAdapter, Validatable {

	/** Identificador da classe de discente de graduação.*/
	@Id
	@Column(name="id_discente_graduacao")
	private int id;
	
	/** Objeto de relacionamento do discente de graduação com a classe {@link Discente} utilizada pela implementação de {@link DiscenteAdapter}*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_discente_graduacao", insertable=false, updatable=false)
	private Discente discente;
	
	/** Indica a quantidade de semestres que o discente conseguiu "aproveitar" para iniciar o curso. */
	@Column(name = "perfil_inicial")
	private Integer perfilInicial = 0;

	/** Participação que aluno o teve no ENADE Ingressante. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_enade_ingressante")
	private ParticipacaoEnade participacaoEnadeIngressante;
	
	/** Data de realização da prova do ENADE Ingressante. */
	@Column(name = "data_prova_enade_ingressante")
	private Date dataProvaEnadeIngressante;

	/** Participação que aluno o teve no ENADE Concluinte. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_enade_concluinte")
	private ParticipacaoEnade participacaoEnadeConcluinte;
	
	/** Data de realização da prova do ENADE Concluinte. */
	@Column(name = "data_prova_enade_concluinte")
	private Date dataProvaEnadeConcluinte;
	
	/** Coluna migrada do pontoA. */
	private Boolean anistiado;

	/** Índice de Rendimento Acadêmico. */
	private float ira;
	
	/** Número de pontos obtidos no vestibular. */
	private Float pontosVestibular;

	/** Classificação alcançada no vestibular. */
	private Short classificacaoVestibular;

	/** Indica quantos COMPONENTES (não mais só atividades) obrigatórios o discente ainda não pagou */
	@Column(name = "total_atividades_pendentes")
	private Short totalAtividadesPendentes;

	/** Total de CH integralizada para esse aluno. */
	@Column(name = "ch_total_integralizada")
	private Short chTotalIntegralizada;
	
	/** Total de CH integralizada realizada com aproveitamentos. */
	@Column(name = "ch_integralizada_aproveitamentos")
	private Short chIntegralizadaAproveitamentos;
	
	/** Total de CH pendente a ser integralizado para esse discente. Esse valor é obtido a partir do total mínimo de CH indicado no currículo do aluno. */
	@Column(name = "ch_total_pendente")
	private Short chTotalPendente;

	/** Total de carga horária de todos os tipos de componentes optativos (ou complementares) integralizada. */
	@Column(name = "ch_optativa_integralizada")
	private Short chOptativaIntegralizada;
	
	/** Total de carga horária pendente de todos os componentes optativos para esse aluno. Esse valor é calculado com base na CH optativa mínima do currículo do aluno. */
	@Column(name = "ch_optativa_pendente")
	private Short chOptativaPendente;

	/** Total de carga horária dos componentes (EXCETO os do tipo Atividade) obrigatórios integralizada. */
	@Column(name = "ch_nao_atividade_obrig_integ")
	private Short chNaoAtividadeObrigInteg;
	
	/** Total de carga horária pendente dos componentes (EXCETO do tipo Atividade) obrigatórios para esse aluno. Esse valor é calculado com base na CH de componentes obrigatórios (exceto atividades) do currículo do aluno. */
	@Column(name = "ch_nao_atividade_obrig_pendente")
	private Short chNaoAtividadeObrigPendente;
	
	/** Total de carga horária de todos os componentes obrigatórios do tipo Atividade integralizada. */
	@Column(name = "ch_atividade_obrig_integ")
	private Short chAtividadeObrigInteg;
	
	/** Total de carga horária pendente dos componentes do tipo Atividade obrigatórios para esse aluno. Esse valor é calculado com base na CH de componentes obrigatórios do tipo atividades do currículo do aluno. */
	@Column(name = "ch_atividade_obrig_pendente")
	private Short chAtividadeObrigPendente;

	/** Total de carga horária de aula integralizada de todos os componentes (que possuam esse tipo de CH). */
	@Column(name = "ch_aula_integralizada")
	private Short chAulaIntegralizada;
	
	/** Total de carga horária de aula pendente de todos os componentes (que possuam esse tipo de CH). Esse valor é calculado com base na CH de aula obrigatória do currículo do aluno. */
	@Column(name = "ch_aula_pendente")
	private Short chAulaPendente;
	
	/** Total de carga horária de laboratório integralizado de todos os componentes (que possuam esse tipo de CH). */
	@Column(name = "ch_lab_integralizada")
	private Short chLabIntegralizada;
	
	/** Total de carga horária de laboratório pendente de todos os componentes (que possuam esse tipo de CH). Esse valor é calculado com base na CH de laboratório obrigatória do currículo do aluno. */
	@Column(name = "ch_lab_pendente")
	private Short chLabPendente;
	
	/** Total de carga horária de estágio integralizada de todos os componentes (que possuam esse tipo de CH). */
	@Column(name = "ch_estagio_integralizada")
	private Short chEstagioIntegralizada;

	/** Total de carga horária de estágio pendente de todos os componentes (que possuam esse tipo de CH). Esse valor é calculado com base na CH de estágio obrigatória do currículo do aluno. */
	@Column(name = "ch_estagio_pendente")
	private Short chEstagioPendente;

	/** Total de créditos integralizados para esse aluno. */
	@Column(name = "cr_total_integralizados")
	private Short crTotalIntegralizados;
	
	/** Total de créditos pendentes a serem integralizado para esse discente. Esse valor é obtido a partir do total mínimo de créditos indicado no currículo do aluno. */
	@Column(name = "cr_total_pendentes")
	private Short crTotalPendentes;

	/** Quantidade de créditos de componentes extra-curriculares integralizados para esse aluno. */
	@Column(name = "cr_extra_integralizados")
	private Short crExtraIntegralizados;

	/** Total de crédito dos componentes (EXCETO os do tipo Atividade) obrigatórios integralizada. */
	@Column(name = "cr_nao_atividade_obrig_integralizado")
	private Short crNaoAtividadeObrigInteg;
	
	/** Total de crédito pendente dos componentes (EXCETO do tipo Atividade) obrigatórios para esse aluno. Esse valor é calculado com base nos créditos de componentes obrigatórios (exceto atividades) do currículo do aluno. */
	@Column(name = "cr_nao_atividade_obrig_pendente")
	private Short crNaoAtividadeObrigPendente;

	/** Total de créditos de laboratório integralizado de todos os componentes (que possuam esse tipo de crédito). */
	@Column(name = "cr_lab_integralizado")
	private Short crLabIntegralizado;

	/** Total de créditos de laboratório pendente de todos os componentes (que possuam esse tipo de crédito). Esse valor é calculado com base nos créditos de laboratório obrigatórios do currículo do aluno. */
	@Column(name = "cr_lab_pendente")
	private Short crLabPendente;

	/** Total de crédito de estágio integralizado de todos os componentes (que possuam esse tipo de crédito). */
	@Column(name = "cr_estagio_integralizado")
	private Short crEstagioIntegralizado;

	/** Total de crédito de estágio pendente de todos os componentes (que possuam esse tipo de crédito). Esse valor é calculado com base nos créditos de estágio obrigatórios do currículo do aluno. */
	@Column(name = "cr_estagio_pendente")
	private Short crEstagioPendente;
	
	/** Total de crédito de aula integralizado de todos os componentes (que possuam esse tipo de crédito). */
	@Column(name = "cr_aula_integralizado")
	private Short crAulaIntegralizado;

	/** Total de crédito de aula pendente de todos os componentes (que possuam esse tipo de crédito). Esse valor é calculado com base nos créditos de aula obrigatórios do currículo do aluno. */
	@Column(name = "cr_aula_pendente")
	private Short crAulaPendente;

	/** Total de grupos de optativas pendentes. */
	@Column(name = "total_grupos_optativas_pendentes")
	private Short totalGruposOptativasPendentes;
	
	/** Prazo máximo para conclusão, em semestres, do curso em AnoPeriodo. Esse dado é obtido a partir do número de semestres máximos do currículo do aluno e do número de prorrogações que for realizados para ele. */
	private Short prazoMaximoConclusao;

	/** Matriz curricular que o aluno está vinculado. */
	@JoinColumn(name = "id_matriz_curricular")
	@ManyToOne(fetch=FetchType.EAGER)
	private MatrizCurricular matrizCurricular;

	/** Pólo que o aluno está vinculado (para alunos EAD). */
	@JoinColumn(name = "id_polo")
	@ManyToOne(fetch=FetchType.EAGER)
	private Polo polo;

	/** Nome da escola em que o aluno concluiu o ensino médio. */
	@Column(name = "escola_conclusao_medio")
	private String escolaConclusaoMedio; //60

	/** Ano de conclusão do ensino médio. */
	@Column(name = "ano_conclusao_medio")
	private Short anoConclusaoMedio;

	/** Cidade em que o aluno concluiu o ensino médio. */
	@Column(name = "cidade_conclusao_medio")
	private String cidadeConclusaoMedio; //30

	/** UF em que o aluno concluiu o ensino médio. */
	@Column(name = "uf_conclusao_medio")
	private String ufConclusaoMedio; //2

	/** País em que o aluno concluiu o ensino médio. */
	@Column(name = "pais_conclusao_medio")
	private String paisConclusaoMedio; //30

	/** Indica se o aluno PODE ser formando de acordo com as turmas matriculadas. Usado no processamento de matrícula. */
	@Column(name = "possivel_formando")
	private Boolean possivelFormando;

	/** Tutor ao qual o aluno de EAD é vinculado. */
	@Transient
	private TutorOrientador tutorEad;

	/** Vínculo entre o aluno e seu orientador acadêmico. */
	@Transient
	private OrientacaoAcademica orientacaoAcademica;

	/** Momento da última atualização dos totais (cargas horárias e créditos integralizados e pendentes) e status do aluno. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column (name="ultima_atualizacao_totais")
	private Date ultimaAtualizacaoTotais;

	/** Trabalho de fim de curso. */
	@Transient
	private TrabalhoFimCurso trabalhoConclusaoCurso;
	
	/** Indica se o discente deve constar da lista de colação de grau. */
	@Column (name="cola_grau")
	private Boolean colaGrau = true;
	
	/** Tutor ao qual o aluno é vinculado. */
	@Transient
	private ArrayList<TutorOrientador> tutores;
	
	/** Atributo referente as antecipações de ensino do discente.*/
	@Transient
	private Integer antecipacoes;
	
	/** Utilizado na operação de cadastramento do discente. */
	@Transient
	private Integer opcaoCadastramento;
	
	/** Indica o valor do perfil inicial do discente, quando alterado pelo administrador so sistema acadêmico.. */
	@Column (name="perfil_inicial_alterado")
	private Integer perfilInicialAlterado;
	
	/** Atributo transient utilizado para exibir o tipo de rendimento acadêmico do aluno nos documentos da instituição.*/
	@Transient
	private String rendimentoAcademino;
	
	/** Construtor padrão. */
	public DiscenteGraduacao() {
		discente = new Discente();
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param matricula
	 * @param nome
	 */
	public DiscenteGraduacao(int id, Long matricula, String nome) {
		discente = new Discente(id, matricula, nome);
		this.id = id;
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param matricula
	 * @param ira
	 * @param nome
	 */
	public DiscenteGraduacao(int id, Long matricula, float ira, String nome) {
		discente = new Discente(id, matricula, nome);
		this.id = id;
		this.ira = ira;
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param matricula
	 * @param status
	 * @param nivel
	 * @param polo
	 */
	public DiscenteGraduacao(int id, Long matricula, int status, char nivel, Polo polo) {
		discente = new Discente();
		discente.setId(id);
		setId(id);
		setMatricula(matricula != null ? matricula : 0l);
		setStatus(status);
		setNivel(nivel);
		this.polo = polo;
	}
	
	/**
	 * Construtor parametrizado.
	 * @param id
	 * @param matricula
	 * @param nome
	 * @param status
	 * @param nivel
	 */
	public DiscenteGraduacao(int id, Long matricula, String nome, int status) {
		discente = new Discente(id, matricula, nome, status);
		this.id = id;
	}
	
	/**
	 * Construtor parametrizado.
	 * @param id
	 * @param matricula
	 * @param nome
	 * @param status
	 * @param nivel
	 */
	public DiscenteGraduacao(int id, Long matricula, String nome, int status, char nivel) {
		discente = new Discente(id, matricula, nome, status, nivel);
		this.id = id;
	}
	

	/** Construtor parametrizado.
	 * @param idDiscente
	 */
	public DiscenteGraduacao(int idDiscente) {
		discente = new Discente(idDiscente);
		this.id = idDiscente;
	}
	
	public DiscenteGraduacao(int idDiscente, char nivel) {
		this(idDiscente);
		this.setNivel(nivel);
	}

	/** Retorna o pólo que o aluno está vinculado (para alunos EAD).
	 * @return
	 */
	public Polo getPolo() {
		return polo;
	}

	/** Seta o pólo que o aluno está vinculado (para alunos EAD).
	 * @param polo
	 */
	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	/** Valida os dados do discente.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	/** Coluna migrada do pontoA.
	 * @return
	 */
	public Boolean getAnistiado() {
		return anistiado;
	}

	/** Coluna migrada do pontoA.
	 * @param anistiado
	 */
	public void setAnistiado(Boolean anistiado) {
		this.anistiado = anistiado;
	}

	/** Retorna a classificação alcançada no vestibular.
	 * @return
	 */
	public Short getClassificacaoVestibular() {
		return classificacaoVestibular;
	}

	/** Seta a classificação alcançada no vestibular.
	 * @param classificacaovestibular
	 */
	public void setClassificacaoVestibular(Short classificacaovestibular) {
		this.classificacaoVestibular = classificacaovestibular;
	}

	/** Retorna o Índice de Rendimento Acadêmico.
	 * @return
	 */
	public float getIra() {
		return ira;
	}

	/** Seta o Índice de Rendimento Acadêmico.
	 * @param ira
	 */
	public void setIra(float ira) {
		this.ira = ira;
	}

	/** Retorna a matriz curricular que o aluno está vinculado.
	 * @return
	 */
	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	/** Seta a matriz curricular que o aluno está vinculado.
	 * @param matrizCurricular
	 */
	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	/** Indica a quantidade de semestres que o discente conseguiu "aproveitar" para iniciar o curso.
	 * @return
	 */
	public Integer getPerfilInicial() {
		return perfilInicialAlterado == null ? perfilInicial : perfilInicial + perfilInicialAlterado;
	}

	/** Seta a quantidade de semestres que o discente conseguiu "aproveitar" para iniciar o curso.
	 * @param perfilInicial
	 */
	public void setPerfilInicial(Integer perfilInicial) {
		this.perfilInicial = perfilInicial;
	}

	/** Retorna o número de pontos obtidos no vestibular.
	 * @return
	 */
	public Float getPontosVestibular() {
		return pontosVestibular;
	}

	/** Seta o número de pontos obtidos no vestibular.
	 * @param pontosvestibular
	 */
	public void setPontosVestibular(Float pontosvestibular) {
		this.pontosVestibular = pontosvestibular;
	}

	/** Retorna o total de crédito dos componentes (EXCETO os do tipo Atividade) obrigatórios integralizada.
	 * @return
	 */
	public Short getCrNaoAtividadeObrigInteg() {
		return crNaoAtividadeObrigInteg;
	}

	/** Seta o total de crédito dos componentes (EXCETO os do tipo Atividade) obrigatórios integralizada.
	 * @param crNaoAtividadeObrigInteg
	 */
	public void setCrNaoAtividadeObrigInteg(Short crNaoAtividadeObrigInteg) {
		this.crNaoAtividadeObrigInteg = crNaoAtividadeObrigInteg;
	}

	/** Retorna o total de crédito pendente dos componentes (EXCETO do tipo Atividade) obrigatórios para esse aluno.
	 * @return
	 */
	public Short getCrNaoAtividadeObrigPendente() {
		return crNaoAtividadeObrigPendente;
	}
 
	/** Seta o total de crédito pendente dos componentes (EXCETO do tipo Atividade) obrigatórios para esse aluno.
	 * @param crNaoAtividadeObrigPendente
	 */
	public void setCrNaoAtividadeObrigPendente(Short crNaoAtividadeObrigPendente) {
		this.crNaoAtividadeObrigPendente = crNaoAtividadeObrigPendente;
	}

	/** Retorna o total de crédito de estágio pendente de todos os componentes (que possuam esse tipo de crédito).
	 * @return
	 */
	public Short getCrEstagioPendente() {
		return crEstagioPendente;
	}

	/** Seta o total de crédito de estágio pendente de todos os componentes (que possuam esse tipo de crédito).
	 * @param crEstagioPendente
	 */
	public void setCrEstagioPendente(Short crEstagioPendente) {
		this.crEstagioPendente = crEstagioPendente;
	}

	/** Retorna o total de crédito de estágio integralizado de todos os componentes (que possuam esse tipo de crédito).
	 * @return
	 */
	public Short getCrEstagioIntegralizado() {
		return crEstagioIntegralizado;
	}

	/** Seta o total de crédito de estágio integralizado de todos os componentes (que possuam esse tipo de crédito).
	 * @param crEstagioIntegralizado
	 */
	public void setCrEstagioIntegralizado(Short crEstagioIntegralizado) {
		this.crEstagioIntegralizado = crEstagioIntegralizado;
	}

	/** Retorna o total de créditos de laboratório pendente de todos os componentes (que possuam esse tipo de crédito).
	 * @return
	 */
	public Short getCrLabPendente() {
		return crLabPendente;
	}

	/** Seta o total de créditos de laboratório pendente de todos os componentes (que possuam esse tipo de crédito).
	 * @param crLabPendente
	 */
	public void setCrLabPendente(Short crLabPendente) {
		this.crLabPendente = crLabPendente;
	}

	/** Retorna o total de créditos de laboratório integralizado de todos os componentes (que possuam esse tipo de crédito).
	 * @return
	 */
	public Short getCrLabIntegralizado() {
		return crLabIntegralizado;
	}

	/** Seta o total de créditos de laboratório integralizado de todos os componentes (que possuam esse tipo de crédito).
	 * @param crLabIntegralizado
	 */
	public void setCrLabIntegralizado(Short crLabIntegralizado) {
		this.crLabIntegralizado = crLabIntegralizado;
	}

	/** Retorna o total de carga horária de aula pendente de todos os componentes (que possuam esse tipo de CH).
	 * @return
	 */
	public Short getChAulaPendente() {
		return chAulaPendente;
	}

	/** Seta o total de carga horária de aula pendente de todos os componentes (que possuam esse tipo de CH).
	 * @param chAulaPendente
	 */
	public void setChAulaPendente(Short chAulaPendente) {
		this.chAulaPendente = chAulaPendente;
	}

	/** Retorna o total de carga horária de aula integralizada de todos os componentes (que possuam esse tipo de CH).
	 * @return
	 */
	public Short getChAulaIntegralizada() {
		return chAulaIntegralizada;
	}

	/** Seta o total de carga horária de aula integralizada de todos os componentes (que possuam esse tipo de CH).
	 * @param chAulaIntegralizada
	 */
	public void setChAulaIntegralizada(Short chAulaIntegralizada) {
		this.chAulaIntegralizada = chAulaIntegralizada;
	}

	/** Retorna o total de carga horária de todos os tipos de componentes optativos (ou complementares) integralizada.
	 * @return
	 */
	public Short getChOptativaIntegralizada() {
		return chOptativaIntegralizada;
	}

	/** Seta o total de carga horária de todos os tipos de componentes optativos (ou complementares) integralizada.
	 * @param chOptativaIntegralizada
	 */
	public void setChOptativaIntegralizada(Short chOptativaIntegralizada) {
		this.chOptativaIntegralizada = chOptativaIntegralizada;
	}

	/** Retorna o total de carga horária pendente de todos os componentes optativos para esse aluno.
	 * @return
	 */
	public Short getChOptativaPendente() {
		return chOptativaPendente;
	}

	/** Seta o total de carga horária pendente de todos os componentes optativos para esse aluno.
	 * @param chOptativaPendente
	 */
	public void setChOptativaPendente(Short chOptativaPendente) {
		this.chOptativaPendente = chOptativaPendente;
	}

	/** Retorna o total de carga horária dos componentes (EXCETO os do tipo Atividade) obrigatórios integralizada.
	 * @return
	 */
	public Short getChNaoAtividadeObrigInteg() {
		return chNaoAtividadeObrigInteg;
	}

	/** Seta o total de carga horária dos componentes (EXCETO os do tipo Atividade) obrigatórios integralizada.
	 * @param chNaoAtividadeObrigInteg
	 */
	public void setChNaoAtividadeObrigInteg(Short chNaoAtividadeObrigInteg) {
		this.chNaoAtividadeObrigInteg = chNaoAtividadeObrigInteg;
	}

	/** Retorna o total de carga horária pendente dos componentes (EXCETO do tipo Atividade) obrigatórios para esse aluno.
	 * @return
	 */
	public Short getChNaoAtividadeObrigPendente() {
		return chNaoAtividadeObrigPendente;
	}

	/** Seta o total de carga horária pendente dos componentes (EXCETO do tipo Atividade) obrigatórios para esse aluno.
	 * @param chNaoAtividadeObrigPendente
	 */
	public void setChNaoAtividadeObrigPendente(Short chNaoAtividadeObrigPendente) {
		this.chNaoAtividadeObrigPendente = chNaoAtividadeObrigPendente;
	}

	/** Retorna o total de carga horária de estágio pendente de todos os componentes (que possuam esse tipo de CH).
	 * @return
	 */
	public Short getChEstagioPendente() {
		return chEstagioPendente;
	}

	/** Seta o total de carga horária de estágio pendente de todos os componentes (que possuam esse tipo de CH).
	 * @param chEstagioPendente
	 */
	public void setChEstagioPendente(Short chEstagioPendente) {
		this.chEstagioPendente = chEstagioPendente;
	}
 
	/** Retorna o total de carga horária de laboratório integralizado de todos os componentes (que possuam esse tipo de CH).
	 * @return
	 */
	public Short getChEstagioIntegralizada() {
		return chEstagioIntegralizada;
	}

	/** Seta o total de carga horária de laboratório integralizado de todos os componentes (que possuam esse tipo de CH).
	 * @param chEstagioIntegralizada
	 */
	public void setChEstagioIntegralizada(Short chEstagioIntegralizada) {
		this.chEstagioIntegralizada = chEstagioIntegralizada;
	}

	/** Retorna o total de carga horária de laboratório pendente de todos os componentes (que possuam esse tipo de CH).
	 * @return
	 */
	public Short getChLabPendente() {
		return chLabPendente;
	}

	/** Seta o total de carga horária de laboratório pendente de todos os componentes (que possuam esse tipo de CH).
	 * @param chLabPendente
	 */
	public void setChLabPendente(Short chLabPendente) {
		this.chLabPendente = chLabPendente;
	}


	/** Retorna o total de carga horária de todos os componentes obrigatórios do tipo Atividade integralizada.
	 * @return
	 */
	public Short getChAtividadeObrigInteg() {
		return chAtividadeObrigInteg;
	}

	/** Seta o total de carga horária de todos os componentes obrigatórios do tipo Atividade integralizada.
	 * @param chAtividadeObrigInteg
	 */
	public void setChAtividadeObrigInteg(Short chAtividadeObrigInteg) {
		this.chAtividadeObrigInteg = chAtividadeObrigInteg;
	}

	/** Retorna o prazo máximo para conclusão, em semestres, do curso em AnoPeriodo.
	 * @return
	 */
	public Short getPrazoMaximoConclusao() {
		return prazoMaximoConclusao;
	}

	/** Seta o prazo máximo para conclusão, em semestres, do curso em AnoPeriodo.
	 * @param prazoMaximoConclusao
	 */
	public void setPrazoMaximoConclusao(Short prazoMaximoConclusao) {
		this.prazoMaximoConclusao = prazoMaximoConclusao;
	}

	/** Retorna o tutor ao qual o aluno de EAD é vinculado.
	 * @return
	 */
	public TutorOrientador getTutorEad() {
		return tutorEad;
	}

	/** Seta o tutor ao qual o aluno de EAD é vinculado.
	 * @param tutorEad
	 */
	public void setTutorEad(TutorOrientador tutorEad) {
		this.tutorEad = tutorEad;
	}

	/** Retorna o ano de conclusão do ensino médio.
	 * @return
	 */
	public Short getAnoConclusaoMedio() {
		return anoConclusaoMedio;
	}

	/** Seta o ano de conclusão do ensino médio.
	 * @param anoConclusaoMedio
	 */
	public void setAnoConclusaoMedio(Short anoConclusaoMedio) {
		this.anoConclusaoMedio = anoConclusaoMedio;
	}

	/** Retorna a cidade em que o aluno concluiu o ensino médio.
	 * @return
	 */
	public String getCidadeConclusaoMedio() {
		return cidadeConclusaoMedio;
	}

	/** Seta a cidade em que o aluno concluiu o ensino médio.
	 * @param cidadeConclusaoMedio
	 */
	public void setCidadeConclusaoMedio(String cidadeConclusaoMedio) {
		this.cidadeConclusaoMedio = cidadeConclusaoMedio;
	}

	/** Retorna o nome da escola em que o aluno concluiu o ensino médio.
	 * @return
	 */
	public String getEscolaConclusaoMedio() {
		return escolaConclusaoMedio;
	}

	/** Seta o nome da escola em que o aluno concluiu o ensino médio.
	 * @param escolaConclusaoMedio
	 */
	public void setEscolaConclusaoMedio(String escolaConclusaoMedio) {
		this.escolaConclusaoMedio = escolaConclusaoMedio;
	}

	/** Retorna o país em que o aluno concluiu o ensino médio.
	 * @return
	 */
	public String getPaisConclusaoMedio() {
		return paisConclusaoMedio;
	}

	/** Seta o país em que o aluno concluiu o ensino médio.
	 * @param paisConclusaoMedio
	 */
	public void setPaisConclusaoMedio(String paisConclusaoMedio) {
		this.paisConclusaoMedio = paisConclusaoMedio;
	}

	/** Retorna a UF em que o aluno concluiu o ensino médio.
	 * @return
	 */
	public String getUfConclusaoMedio() {
		return ufConclusaoMedio;
	}

	/** Seta a UF em que o aluno concluiu o ensino médio.
	 * @param ufConclusaoMedio
	 */
	public void setUfConclusaoMedio(String ufConclusaoMedio) {
		this.ufConclusaoMedio = ufConclusaoMedio;
	}

	/** Retorna o total de carga horária de laboratório integralizado de todos os componentes (que possuam esse tipo de CH).
	 * @return
	 */
	public Short getChLabIntegralizada() {
		return chLabIntegralizada;
	}

	/** Seta o total de carga horária de laboratório integralizado de todos os componentes (que possuam esse tipo de CH).
	 * @param totalHoraLaboratorioPaga
	 */
	public void setChLabIntegralizada(Short totalHoraLaboratorioPaga) {
		this.chLabIntegralizada = totalHoraLaboratorioPaga;
	}

	/** Retorna a quantidade de créditos de componentes extra-curriculares integralizados para esse aluno.
	 * @return
	 */
	public Short getCrExtraIntegralizados() {
		return crExtraIntegralizados;
	}

	/** Seta a quantidade de créditos de componentes extra-curriculares integralizados para esse aluno.
	 * @param crExtraIntegralizados
	 */
	public void setCrExtraIntegralizados(Short crExtraIntegralizados) {
		this.crExtraIntegralizados = crExtraIntegralizados;
	}

	/** Retorna o total de CH integralizada para esse aluno.
	 * @return
	 */
	public Short getChTotalIntegralizada() {
		return chTotalIntegralizada;
	}

	/** Seta o total de CH integralizada para esse aluno.
	 * @param chTotalIntegralizada
	 */
	public void setChTotalIntegralizada(Short chTotalIntegralizada) {
		this.chTotalIntegralizada = chTotalIntegralizada;
	}
	
	public Short getChIntegralizadaAproveitamentos() {
		return chIntegralizadaAproveitamentos;
	}

	public void setChIntegralizadaAproveitamentos(Short chIntegralizadaAproveitamentos) {
		this.chIntegralizadaAproveitamentos = chIntegralizadaAproveitamentos;
	}

	/** Retorna o total de CH pendente a ser integralizado para esse discente.
	 * @return
	 */
	public Short getChTotalPendente() {
		return chTotalPendente;
	}

	/** Sea o total de CH pendente a ser integralizado para esse discente.
	 * @param chTotalPendente
	 */
	public void setChTotalPendente(Short chTotalPendente) {
		this.chTotalPendente = chTotalPendente;
	}

	/** Retorna o total de carga horária pendente dos componentes do tipo Atividade obrigatórios para esse aluno.
	 * @return
	 */
	public Short getChAtividadeObrigPendente() {
		return chAtividadeObrigPendente;
	}

	/** Seta o total de carga horária pendente dos componentes do tipo Atividade obrigatórios para esse aluno.
	 * @param chAtividadeObrigPendente
	 */
	public void setChAtividadeObrigPendente(Short chAtividadeObrigPendente) {
		this.chAtividadeObrigPendente = chAtividadeObrigPendente;
	}

	/** Retorna o total de créditos integralizados para esse aluno.
	 * @return
	 */
	public Short getCrTotalIntegralizados() {
		return crTotalIntegralizados;
	}

	/** Seta o total de créditos integralizados para esse aluno.
	 * @param crTotalIntegralizados
	 */
	public void setCrTotalIntegralizados(Short crTotalIntegralizados) {
		this.crTotalIntegralizados = crTotalIntegralizados;
	}

	/** Retorna o total de créditos pendentes a serem integralizado para esse discente.
	 * @return
	 */
	public Short getCrTotalPendentes() {
		return crTotalPendentes;
	}

	/** Seta o total de créditos pendentes a serem integralizado para esse discente.
	 * @param crTotalPendentes
	 */
	public void setCrTotalPendentes(Short crTotalPendentes) {
		this.crTotalPendentes = crTotalPendentes;
	}

	/** Retorna o total de crédito de aula integralizado de todos os componentes (que possuam esse tipo de crédito).
	 * @return
	 */
	public Short getCrAulaIntegralizado() {
		return crAulaIntegralizado;
	}

	/** Seta o total de crédito de aula integralizado de todos os componentes (que possuam esse tipo de crédito).
	 * @param crAulaIntegralizado
	 */
	public void setCrAulaIntegralizado(Short crAulaIntegralizado) {
		this.crAulaIntegralizado = crAulaIntegralizado;
	}

	/** Retorna o total de crédito de aula pendente de todos os componentes (que possuam esse tipo de crédito).
	 * @return
	 */
	public Short getCrAulaPendente() {
		return crAulaPendente;
	}

	/** Seta o total de crédito de aula pendente de todos os componentes (que possuam esse tipo de crédito).
	 * @param crAulaPendente
	 */
	public void setCrAulaPendente(Short crAulaPendente) {
		this.crAulaPendente = crAulaPendente;
	}

	/** Incrementa o valor de créditos integralizados do aluno.
	 * @param creditos valor a incrementar
	 */
	public void incCrTotalIntegralizados(int creditos) {
		if (crTotalIntegralizados == null)
			crTotalIntegralizados = (short) creditos;
		else
			crTotalIntegralizados = (short) (creditos + crTotalIntegralizados);
	}

	/** Incrementa o valor da CH integralizada do aluno.
	 * @param cargaHoraria
	 */
	public void incChTotalIntegralizada(int cargaHoraria) {
		if (chTotalIntegralizada == null)
			chTotalIntegralizada = (short) cargaHoraria;
		else 
			chTotalIntegralizada = (short) (chTotalIntegralizada + cargaHoraria); 
	}

	/** Incrementa o valor da CH integralizada dos componentes optativos.
	 * @param cargaHoraria
	 */
	public void incChOptativaIntegralizada(int cargaHoraria) {
		if (chOptativaIntegralizada == null)
			chOptativaIntegralizada = (short) cargaHoraria;
		else 
			chOptativaIntegralizada = (short) (chOptativaIntegralizada + cargaHoraria);
	}

	/** Incrementa o valor da CH integralizada de componentes obrigatórios (exceto atividades).
	 * @param cargaHoraria
	 */
	public void incChNaoAtividadeObrigInteg(int cargaHoraria) {
		if (chNaoAtividadeObrigInteg == null)
			chNaoAtividadeObrigInteg = (short) cargaHoraria;
		else
			chNaoAtividadeObrigInteg = (short) (chNaoAtividadeObrigInteg + cargaHoraria);
	}

	/** Incrementa o valor da CH integralizada das atividades obrigatórias.
	 * @param cargaHoraria
	 */
	public void incChAtividadeObrigInteg(int cargaHoraria) {
		if (chAtividadeObrigInteg == null)
			chAtividadeObrigInteg = (short) cargaHoraria;
		else
			chAtividadeObrigInteg = (short) (chAtividadeObrigInteg + cargaHoraria);
	}

	/** Incrementa o total de créditos de componentes obrigatórios (exceto atividades). 
	 * @param creditos
	 */
	public void incCrNaoAtividadeObrigInteg(int creditos) {
		if (crNaoAtividadeObrigInteg == null)
			crNaoAtividadeObrigInteg = (short) creditos;
		else
			crNaoAtividadeObrigInteg = (short) (crNaoAtividadeObrigInteg + creditos);
	}

	/** Retorna o momento da última atualização dos totais (cargas horárias e créditos integralizados e pendentes) e status do aluno.
	 * @return
	 */
	public Date getUltimaAtualizacaoTotais() {
		return ultimaAtualizacaoTotais;
	}

	/** Seta o momento da última atualização dos totais (cargas horárias e créditos integralizados e pendentes) e status do aluno.
	 * @param ultimaAtualizacaoTotais
	 */
	public void setUltimaAtualizacaoTotais(Date ultimaAtualizacaoTotais) {
		this.ultimaAtualizacaoTotais = ultimaAtualizacaoTotais;
	}

	/** Indica quantos COMPONENTES (não mais só atividades) obrigatórios o discente ainda não pagou.
	 * @return
	 */
	public Short getTotalAtividadesPendentes() {
		return totalAtividadesPendentes;
	}

	/** Seta quantos COMPONENTES (não mais só atividades) obrigatórios o discente ainda não pagou.
	 * @param totalAtividadesPendentes
	 */
	public void setTotalAtividadesPendentes(Short totalAtividadesPendentes) {
		this.totalAtividadesPendentes = totalAtividadesPendentes;
	}

	/** Retorna o vínculo entre o aluno e seu orientador acadêmico.
	 * @return
	 */
	public OrientacaoAcademica getOrientacaoAcademica() {
		return orientacaoAcademica;
	}

	/** Seta o vínculo entre o aluno e seu orientador acadêmico.
	 * @param orientacaoAcademica
	 */
	public void setOrientacaoAcademica(OrientacaoAcademica orientacaoAcademica) {
		this.orientacaoAcademica = orientacaoAcademica;
	}

	/** Indica se o discente é de Ensino à Distância.
	 * @return
	 */
	public boolean isEAD() {
		return polo != null;
	}

	/** Retorna o trabalho de fim de curso.
	 * @param trabalhoConclusaoCurso
	 */
	public void setTrabalhoConclusaoCurso(TrabalhoFimCurso trabalhoConclusaoCurso) {
		this.trabalhoConclusaoCurso = trabalhoConclusaoCurso;
	}

	/** Seta o trabalho de fim de curso.
	 * @return
	 */
	public TrabalhoFimCurso getTrabalhoConclusaoCurso() {
		return trabalhoConclusaoCurso;
	}

	/** Indica se o aluno PODE ser formando de acordo com as turmas matriculadas.
	 * @return
	 */
	public Boolean getPossivelFormando() {
		return possivelFormando;
	}

	/** Seta se o aluno PODE ser formando de acordo com as turmas matriculadas.
	 * @param possivelFormando
	 */
	public void setPossivelFormando(Boolean possivelFormando) {
		this.possivelFormando = possivelFormando;
	}

	/** Seta o total de grupos de optativas pendentes.
	 * @param totalGruposOptativasPendentes
	 */
	public void setTotalGruposOptativasPendentes(Short totalGruposOptativasPendentes) {
		this.totalGruposOptativasPendentes = totalGruposOptativasPendentes;
	}

	/** Retorna o total de grupos de optativas pendentes.
	 * @return
	 */
	public Short getTotalGruposOptativasPendentes() {
		return totalGruposOptativasPendentes;
	}

	/** Indica se o discente deve constar da lista de colação de grau.
	 * @return
	 */
	public Boolean getColaGrau() {
		// por padrão o valor é true, caso null
		if (colaGrau == null) return new Boolean(true);
		else return colaGrau;
	}

	/** Seta se o discente deve constar da lista de colação de grau.
	 * @param colaGrau
	 */
	public void setColaGrau(Boolean colaGrau) {
		// por padrão o valor é true, caso null
		if (colaGrau == null)
			this.colaGrau = true;
		else 
			this.colaGrau = colaGrau;
	}
	
	/** Retorna o número de períodos de antecipações do prazo de conclusão do curso.
	 * @return
	 */
	public Integer getAntecipacoes() {
		return antecipacoes;
	}

	/** Seta o número de períodos de antecipações do prazo de conclusão do curso.
	 * @return
	 */
	public void setAntecipacoes(Integer antecipacoes) {
		this.antecipacoes = antecipacoes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return (getMatricula() != null ? getMatricula() + " - " : "")
				+ (getPessoa().getNome() != null ? getPessoa().getNome() : "");
	}

	@Override
	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}
	
	@Override
	public int getAnoEntrada() {
		return discente.getAnoEntrada();
	}

	@Override
	public Integer getAnoIngresso() {
		return discente.getAnoIngresso();
	}

	@Override
	public String getAnoPeriodoIngresso() {
		return discente.getAnoPeriodoIngresso();
	}

	@Override
	public Integer getChIntegralizada() {
		return discente.getChIntegralizada();
	}

	@Override
	public RegistroEntrada getCriadoPor() {
		return discente.getCriadoPor();
	}

	@Override
	public Curriculo getCurriculo() {
		return discente.getCurriculo();
	}

	@Override
	public Curso getCurso() {
		return discente.getCurso();
	}

	@Override
	public Date getDataCadastro() {
		return discente.getDataCadastro();
	}

	@Override
	public Date getDataColacaoGrau() {
		return discente.getDataColacaoGrau();
	}

	@Override
	public FormaIngresso getFormaIngresso() {
		return discente.getFormaIngresso();
	}

	@Override
	public Unidade getGestoraAcademica() {
		return discente.getGestoraAcademica();
	}

	@Override
	public Integer getIdFoto() {
		return discente.getIdFoto();
	}

	@Override
	public Integer getIdHistoricoDigital() {
		return discente.getIdHistoricoDigital();
	}

	@Override
	public Integer getIdPerfil() {
		return discente.getIdPerfil();
	}

	@Override
	public Long getMatricula() {
		return discente.getMatricula();
	}

	@Override
	public String getMatriculaNome() {
		return discente.getMatriculaNome();
	}

	@Override
	public String getMatriculaNomeFormatado() {
		return discente.getMatriculaNomeFormatado();
	}

	@Override
	public String getMatriculaNomeNivel() {
		return discente.getMatriculaNomeNivel();
	}

	@Override
	public String getMatriculaNomeNivelFormatado() {
		return discente.getMatriculaNomeNivelFormatado();
	}

	@Override
	public String getMatriculaNomeSituacaoFormatado() {
		return discente.getMatriculaNomeSituacaoFormatado();
	}

	@Override
	public String getMatriculaNomeStatus() {
		return discente.getMatriculaNomeStatus();
	}

	@Override
	public Collection<MatriculaComponente> getMatriculasDisciplina() {
		return discente.getMatriculasDisciplina();
	}
	
	@Override
	public Collection<SolicitacaoApoioNee> getSolicitacoesApoioNee() {
		return discente.getSolicitacoesApoioNee();
	}

	@Override
	public MovimentacaoAluno getMovimentacaoSaida() {
		return discente.getMovimentacaoSaida();
	}

	@Override
	public char getNivel() {
		return discente.getNivel();
	}

	@Override
	public String getNivelDesc() {
		return discente.getNivelDesc();
	}

	@Override
	public String getNivelStr() {
		return discente.getNivelStr();
	}

	@Override
	public String getNome() {
		return discente.getNome();
	}
	
	public String getNomeOficial() {
		return discente.getPessoa().getNomeOficial();
	}

	@Override
	public String getObservacao() {
		return discente.getObservacao();
	}

	@Override
	public PerfilPessoa getPerfil() {
		return discente.getPerfil();
	}

	@Override
	public Integer getPeriodoAtual() {
		return discente.getPeriodoAtual();
	}

	@Override
	public Integer getPeriodoIngresso() {
		return discente.getPeriodoIngresso();
	}

	@Override
	public Pessoa getPessoa() {
		return discente.getPessoa();
	}

	@Override
	public Integer getPrazoConclusao() {
		return discente.getPrazoConclusao();
	}

	@Override
	public Integer getProrrogacoes() {
		return discente.getProrrogacoes();
	}

	@Override
	public int getSemestreAtual() {
		return discente.getSemestreAtual();
	}

	@Override
	public int getStatus() {
		return discente.getStatus();
	}

	@Override
	public String getStatusString() {
		return discente.getStatusString();
	}

	@Override
	public Integer getTipo() {
		return discente.getTipo();
	}

	@Override
	public String getTipoString() {
		return discente.getTipoString();
	}

	@Override
	public String getTrancamentos() {
		return discente.getTrancamentos();
	}

	@Override
	public Unidade getUnidade() {
		return discente.getUnidade();
	}

	@Override
	public Usuario getUsuario() {
		return discente.getUsuario();
	}

	@Override
	public boolean isApostilamento() {
		return discente.isApostilamento();
	}

	@Override
	public boolean isAtivo() {
		return discente.isAtivo();
	}

	@Override
	public boolean isCadastrado(int ano, int periodo) {
		return discente.isCadastrado(ano, periodo);
	}

	@Override
	public boolean isCarente() {
		return discente.isCarente();
	}

	@Override
	public boolean isConcluido() {
		return discente.isConcluido();
	}

	@Override
	public boolean isDefendido() {
		return discente.isDefendido();
	}

	@Override
	public boolean isDiscenteEad() {
		if (getCurso() != null) {
			return getCurso().isADistancia();
		}
		
		return getPolo() != null;
	}

	@Override
	public boolean isDoutorado() {
		return discente.isDoutorado();
	}

	@Override
	public boolean isGraduacao() {
		return discente.isGraduacao();
	}

	@Override
	public boolean isInfantil() {
		return discente.isInfantil();
	}

	@Override
	public boolean isLato() {
		return discente.isLato();
	}

	@Override
	public boolean isMatricular() {
		return discente.isMatricular();
	}

	@Override
	public boolean isMestrado() {
		return discente.isMestrado();
	}

	@Override
	public boolean isRegular() {
		return discente.isRegular();
	}

	@Override
	public boolean isResidencia() {
		return discente.isResidencia();
	}

	@Override
	public boolean isSelecionado() {
		return discente.isSelecionado();
	}

	@Override
	public boolean isStricto() {
		return discente.isStricto();
	}

	@Override
	public boolean isTecnico() {
		return discente.isTecnico();
	}
	
	@Override
	public boolean isMetropoleDigital() {
		return discente.isMetropoleDigital();
	}

	@Override
	public boolean isMedio() {
		return discente.isMedio();
	}	

	@Override
	public boolean isFormacaoComplementar() {
		return discente.isFormacaoComplementar();
	}

	@Override
	public boolean isTrancado() {
		return discente.isTrancado();
	}
	
	public boolean isGraduando() {
		return discente.getStatus() == StatusDiscente.GRADUANDO;
	}

	@Override
	public String nomeMatricula() {
		return discente.nomeMatricula();
	}

	@Override
	public void setAnoIngresso(Integer anoIngresso) {
		discente.setAnoIngresso(anoIngresso);
	}

	@Override
	public void setCarente(boolean carente) {
		discente.setCarente(carente);
	}

	@Override
	public void setChIntegralizada(Integer chIntegralizada) {
		discente.setChIntegralizada(chIntegralizada);
	}

	@Override
	public void setCriadoPor(RegistroEntrada criadoPor) {
		discente.setCriadoPor(criadoPor);
	}

	@Override
	public void setCurriculo(Curriculo curriculo) {
		discente.setCurriculo(curriculo);
	}

	@Override
	public void setCurso(Curso curso) {
		discente.setCurso(curso);
	}

	@Override
	public void setDataCadastro(Date dataCadastro) {
		discente.setDataCadastro(dataCadastro);
	}

	@Override
	public void setDataColacaoGrau(Date dataColacaoGrau) {
		discente.setDataColacaoGrau(dataColacaoGrau);
	}

	@Override
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		discente.setFormaIngresso(formaIngresso);
	}

	@Override
	public void setGestoraAcademica(Unidade unidadeGestora) {
		discente.setGestoraAcademica(unidadeGestora);
	}

	@Override
	public void setIdFoto(Integer idFoto) {
		discente.setIdFoto(idFoto);
	}

	@Override
	public void setIdHistoricoDigital(Integer idHistoricoDigital) {
		discente.setIdHistoricoDigital(idHistoricoDigital);
	}

	@Override
	public void setIdPerfil(Integer idPerfil) {
		discente.setIdPerfil(idPerfil);
	}

	@Override
	public void setMatricula(Long matricula) {
		discente.setMatricula(matricula);
	}

	@Override
	public void setMatricular(boolean matricular) {
		discente.setMatricular(matricular);
	}

	@Override
	public void setMatriculasDisciplina(Collection<MatriculaComponente> matriculaDisciplinas) {
		discente.setMatriculasDisciplina(matriculaDisciplinas);
	}


	@Override
	public void setSolicitacoesApoioNee(Collection<SolicitacaoApoioNee> solicitacoesApoioNee) {
		discente.setSolicitacoesApoioNee(solicitacoesApoioNee);
	}

	
	@Override
	public void setMovimentacaoSaida(MovimentacaoAluno movimentacaoSaida) {
		discente.setMovimentacaoSaida(movimentacaoSaida);
	}

	@Override
	public void setNivel(char nivel) {
		discente.setNivel(nivel);
	}

	@Override
	public void setObservacao(String observacao) {
		discente.setObservacao(observacao);
	}

	@Override
	public void setPerfil(PerfilPessoa perfil) {
		discente.setPerfil(perfil);
	}

	@Override
	public void setPeriodoAtual(Integer periodoAtual) {
		discente.setPeriodoAtual(periodoAtual);
	}

	@Override
	public void setPeriodoIngresso(Integer periodoIngresso) {
		discente.setPeriodoIngresso(periodoIngresso);
	}

	@Override
	public void setPessoa(Pessoa pessoa) {
		discente.setPessoa(pessoa);
	}

	@Override
	public void setPrazoConclusao(Integer prazoConclusao) {
		discente.setPrazoConclusao(prazoConclusao);
	}

	@Override
	public void setProrrogacoes(Integer prorrogacoes) {
		discente.setProrrogacoes(prorrogacoes);
	}

	@Override
	public void setSelecionado(boolean selecionado) {
		discente.setSelecionado(selecionado);
	}

	@Override
	public void setSemestreAtual(int semestreAtual) {
		discente.setSemestreAtual(semestreAtual);
	}

	@Override
	public void setStatus(int status) {
		discente.setStatus(status);
	}

	@Override
	public void setTipo(Integer tipo) {
		discente.setTipo(tipo);
	}

	@Override
	public void setTrancamentos(String trancamentos) {
		discente.setTrancamentos(trancamentos);
	}

	@Override
	public void setUsuario(Usuario usuario) {
		discente.setUsuario(usuario);
	}
	
	/**
	 * Verifica se o discente possui índices públicos calculados
	 * 
	 * @return
	 */
	public boolean isPossuiIndices() {
		return isPassivelCalculoIndices() && !getIndicesAcademicosExibiveis().isEmpty();
	}

	/**
	 * Verifica se o discente deve possuir os índices acadêmicos definidos em IndiceAcademico
	 * 
	 * @return
	 */
	public boolean isPassivelCalculoIndices() {
		String mesAno = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.MES_ANO_LIMITE_ALUNOS_ANTIGOS_INATIVOS);
		Calendar dataBase = CalendarUtils.getInstance("MM/yyyy", mesAno);
		
		boolean exibeIndices = true;
		if(discente.getDiscente().getMovimentacaoSaida() != null){
			if(discente.getDiscente().getMovimentacaoSaida().getTipoMovimentacaoAluno().isPermanente() && discente.getDiscente().getMovimentacaoSaida().getDataRetorno() == null 
					&& !discente.getDiscente().getMovimentacaoSaida().getDataOcorrencia().after(dataBase.getTime())){
				exibeIndices = false;
			}
		}
		return exibeIndices;
	}

	public String getTipoRendimentoAcademino(){
		return (isPossuiIndices() ? getRendimentoAcademino() : "IRA:"); 
	} 
	
	/** Retorna uma coleção de índices acadêmicos que podem ser exibidos no histórico.
	 * @return
	 */
	public Collection<IndiceAcademicoDiscente> getIndicesAcademicosExibiveis() {
		List<IndiceAcademicoDiscente> indices = new ArrayList<IndiceAcademicoDiscente>();
		if (discente.getDiscente().getIndices() != null) {
			for (IndiceAcademicoDiscente indiceAcademico : discente.getDiscente().getIndices())
				if (indiceAcademico.getIndice().isAtivo() && indiceAcademico.getIndice().isExibidoHistorico())
					indices.add(indiceAcademico);
		}
		
		Collections.sort( indices, new Comparator<IndiceAcademicoDiscente>() {

			public int compare(IndiceAcademicoDiscente o1, IndiceAcademicoDiscente o2) {
				
				Integer value1 = new Integer(o1.getIndice().getOrdem());
				Integer value2 = new Integer(o2.getIndice().getOrdem());
				
				return value1.compareTo(value2);
			}
					
		});
		
		return indices;
	}

	/** Método responsável por incrementar a carga horária aproveitada.*/
	public void incChAproveitada(int cargaHoraria) {
		if (chIntegralizadaAproveitamentos == null)
			chIntegralizadaAproveitamentos = (short) cargaHoraria;
		else 
			chIntegralizadaAproveitamentos = (short) (chIntegralizadaAproveitamentos + cargaHoraria);  
	}
	
	public ParticipacaoEnade getParticipacaoEnadeIngressante() {
		return participacaoEnadeIngressante;
	}

	public void setParticipacaoEnadeIngressante(
			ParticipacaoEnade participacaoEnadeIngressante) {
		this.participacaoEnadeIngressante = participacaoEnadeIngressante;
	}

	public ParticipacaoEnade getParticipacaoEnadeConcluinte() {
		return participacaoEnadeConcluinte;
	}

	public void setParticipacaoEnadeConcluinte(
			ParticipacaoEnade participacaoEnadeConcluinte) {
		this.participacaoEnadeConcluinte = participacaoEnadeConcluinte;
	}

	public Date getDataProvaEnadeIngressante() {
		return dataProvaEnadeIngressante;
	}

	public void setDataProvaEnadeIngressante(Date dataProvaEnadeIngressante) {
		this.dataProvaEnadeIngressante = dataProvaEnadeIngressante;
	}

	public Date getDataProvaEnadeConcluinte() {
		return dataProvaEnadeConcluinte;
	}

	public void setDataProvaEnadeConcluinte(Date dataProvaEnadeConcluinte) {
		this.dataProvaEnadeConcluinte = dataProvaEnadeConcluinte;
	}

	public Integer getOpcaoCadastramento() {
		return opcaoCadastramento;
	}

	public void setOpcaoCadastramento(Integer opcaoCadastramento) {
		this.opcaoCadastramento = opcaoCadastramento;
	}

	public Integer getPerfilInicialAlterado() {
		return perfilInicialAlterado;
	}

	public void setPerfilInicialAlterado(Integer perfilInicialAlterado) {
		this.perfilInicialAlterado = perfilInicialAlterado;
	}

	public String getRendimentoAcademino() {
		return rendimentoAcademino;
	}

	public void setRendimentoAcademino(String rendimentoAcademino) {
		this.rendimentoAcademino = rendimentoAcademino;
	}

	public void setTutores(ArrayList<TutorOrientador> tutores) {
		this.tutores = tutores;
	}

	public ArrayList<TutorOrientador> getTutores() {
		return tutores;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DiscenteGraduacao)
			return discente.equals(((DiscenteGraduacao) obj).getDiscente());
		else if (obj instanceof Discente)
			return discente.equals(obj);
		else return false;
	}

	@Override
	public int hashCode() {
		return discente.hashCode();
	}		
}
