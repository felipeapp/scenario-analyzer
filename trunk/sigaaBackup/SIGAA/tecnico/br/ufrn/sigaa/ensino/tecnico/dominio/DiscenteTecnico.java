/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/09/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Representa o aluno dos níveis técnico e médio
 */
@Entity
@Table(name = "discente_tecnico", schema = "tecnico", uniqueConstraints = {})
public class DiscenteTecnico implements DiscenteAdapter, Comparable<DiscenteTecnico> {

	/** Chave primária */
	@Id 
	@Column(name="id_discente")
	private int id;
	
	/** Referencia entidade com as informações comuns a todos os discentes */
	@ManyToOne 
	@JoinColumn(name="id_discente", insertable=false, updatable=false)
	private Discente discente = new Discente();
	
	/** Processo seletivo no qual o aluno ingressou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivo processoSeletivo;
	
	/** Turma de entrada em que o discente foi cadastrado */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_turma_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private TurmaEntradaTecnico turmaEntradaTecnico = new TurmaEntradaTecnico();

	/** Tipo de regime do aluno, se será interno, semi-interno ou externo */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_regime_aluno", unique = false, nullable = true, insertable = true, updatable = true)
	private TipoRegimeAluno tipoRegimeAluno = new TipoRegimeAluno();

	/** Define se o aluno participou do exame nacional de cursos */
	@Column(name = "participou_enc", unique = false, nullable = true, insertable = true, updatable = true)
	private boolean participouEnc;

	/** Define se o discente concluiu o ensino médio */
	@Column(name = "concluiu_em", unique = false, nullable = true, insertable = true, updatable = true)
	private boolean concluiuEnsinoMedio;
	
	/** Total de carga horária de todos os tipos de componentes optativos (ou complementares) integralizada. */
	@Column(name = "ch_optativa_integralizada")
	private Short chOptativaIntegralizada;
	
	/** Total de carga horária pendente de todos os componentes optativos para esse aluno. 
	 * Esse valor é calculado com base na CH optativa mínima do currículo do aluno. */
	@Column(name = "ch_optativa_pendente")
	private Short chOptativaPendente;
	
	/** Total de carga horária de todos os tipos de componentes optativos (ou complementares) integralizada. */
	@Column(name = "ch_obrigatoria_integralizada")
	private Integer chObrigatoriaIntegralizada;
	
	/** Total de carga horária pendente de todos os componentes optativos para esse aluno. 
	 * Esse valor é calculado com base na CH optativa mínima do currículo do aluno. */
	@Column(name = "ch_obrigatoria_pendente")
	private Integer chObrigatoriaPendente;	
	
	/** Referencia o currículo atual do aluno */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_estrutura_curricular", unique = false, nullable = true, insertable = true, updatable = true)
	private EstruturaCurricularTecnica estruturaCurricularTecnica = new EstruturaCurricularTecnica();

	/** Utilizado no resultado da busca exibindo a média geral do discente no curso */
	@Transient
	private float mediaGeral;
	
	/** Utilizado no formulário de cadastro do discente para controlar as opções de cursos disponíveis */
	@Transient
	private boolean hasVariosCursos = true;
	
	/** Utilizado na operação de cadastramento do discente. */
	@Transient
	private Integer opcaoCadastramento;
	
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_opcao_polo_grupo", unique = false, nullable = true, insertable = true, updatable = true)
	private OpcaoPoloGrupo opcaoPoloGrupo = new OpcaoPoloGrupo();
		
	
	/** default constructor */
	public DiscenteTecnico() {
	}
	public DiscenteTecnico(int id) {
		setId(id);
	}
	public DiscenteTecnico(int id, long matricula, String nome, int status) {
		setId(id);
		setMatricula(getMatricula());
		getPessoa().setNome(nome);
		setStatus(status);
	}
	/** full constructor */
	public DiscenteTecnico(FormaIngresso formaIngresso,
			TurmaEntradaTecnico turmaEntradaTecnico,
			TipoRegimeAluno tipoRegimeAluno,
			Integer chIntegralizada, boolean formando, Integer prazoConclusao,
			Date dataColacaoGrau, boolean participouEnc, boolean anistiado) {
		this.turmaEntradaTecnico = turmaEntradaTecnico;
		this.tipoRegimeAluno = tipoRegimeAluno;
		this.participouEnc = participouEnc;
	}

	public boolean isConcluiuEnsinoMedio() {
		return concluiuEnsinoMedio;
	}
	public void setConcluiuEnsinoMedio(boolean concluiuEnsinoMedio) {
		this.concluiuEnsinoMedio = concluiuEnsinoMedio;
	}

	public TurmaEntradaTecnico getTurmaEntradaTecnico() {
		return this.turmaEntradaTecnico;
	}

	public void setTurmaEntradaTecnico(TurmaEntradaTecnico turmaEntradaTecnico) {
		this.turmaEntradaTecnico = turmaEntradaTecnico;
	}

	public TipoRegimeAluno getTipoRegimeAluno() {
		return this.tipoRegimeAluno;
	}

	public void setTipoRegimeAluno(TipoRegimeAluno tipoRegimeAluno) {
		this.tipoRegimeAluno = tipoRegimeAluno;
	}

	public boolean isParticipouEnc() {
		return this.participouEnc;
	}

	public void setParticipouEnc(boolean participouEnc) {
		this.participouEnc = participouEnc;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}

	@Transient
	public Curso getCursoTecnico() {
		return getTurmaEntradaTecnico().getCursoTecnico();
	}
	
	public float getMediaGeral() {
		return mediaGeral;
	}
	
	public void setMediaGeral(float mediaGeral) {
		this.mediaGeral = mediaGeral;
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
		return false;
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
	public ProcessoSeletivo getProcessoSeletivo() {
		return processoSeletivo;
	}
	public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}
	@Override
	public int compareTo(DiscenteTecnico o) {
		return (new Float(mediaGeral)).compareTo(new Float(o.getMediaGeral()));
	}
	
	@Override
	public Collection<SolicitacaoApoioNee> getSolicitacoesApoioNee() {
		return null;
	}
	
	@Override
	public void setSolicitacoesApoioNee(
			Collection<SolicitacaoApoioNee> solicitacoesApoioNee) {
	}
	
	public boolean isHasVariosCursos() {
		return hasVariosCursos;
	}
	public void setHasVariosCursos(boolean hasVariosCursos) {
		this.hasVariosCursos = hasVariosCursos;
	}
	public EstruturaCurricularTecnica getEstruturaCurricularTecnica() {
		return estruturaCurricularTecnica;
	}
	public void setEstruturaCurricularTecnica(
			EstruturaCurricularTecnica estruturaCurricularTecnica) {
		this.estruturaCurricularTecnica = estruturaCurricularTecnica;
	}
	public Short getChOptativaIntegralizada() {
		return chOptativaIntegralizada;
	}
	public void setChOptativaIntegralizada(Short chOptativaIntegralizada) {
		this.chOptativaIntegralizada = chOptativaIntegralizada;
	}
	public Short getChOptativaPendente() {
		return chOptativaPendente;
	}
	public void setChOptativaPendente(Short chOptativaPendente) {
		this.chOptativaPendente = chOptativaPendente;
	}
	public Integer getChObrigatoriaIntegralizada() {
		return chObrigatoriaIntegralizada;
	}
	public void setChObrigatoriaIntegralizada(Integer chObrigatoriaIntegralizada) {
		this.chObrigatoriaIntegralizada = chObrigatoriaIntegralizada;
	}
	public Integer getChObrigatoriaPendente() {
		return chObrigatoriaPendente;
	}
	public void setChObrigatoriaPendente(Integer chObrigatoriaPendente) {
		this.chObrigatoriaPendente = chObrigatoriaPendente;
	}
	public Integer getOpcaoCadastramento() {
		return opcaoCadastramento;
	}
	public void setOpcaoCadastramento(Integer opcaoCadastramento) {
		this.opcaoCadastramento = opcaoCadastramento;
	}
	
	public boolean isPreCadastrado () {
		return getStatus() == StatusDiscente.PRE_CADASTRADO;
	}
	
	public OpcaoPoloGrupo getOpcaoPoloGrupo() {
		return opcaoPoloGrupo;
	}
	public void setOpcaoPoloGrupo(OpcaoPoloGrupo opcaoPoloGrupo) {
		this.opcaoPoloGrupo = opcaoPoloGrupo;
	}
	
}
