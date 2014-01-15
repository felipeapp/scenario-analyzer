/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/03/2010
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PeriodoIndicacaoReuni;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.ModalidadeBolsaExterna;

/**
 * Entidade que representa um Plano de Docência Assistida Enviado pelo Docente.
 * 
 * Docência assistida é a atuação do pós-graduando em atividades acadêmicas sob a 
 * supervisão direta de professor do quadro efetivo da UFRN, de acordo com plano aprovado 
 * pelo colegiado e pelo departamento responsável pelo componente curricular.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="plano_docencia_assistida", schema="stricto_sensu")
public class PlanoDocenciaAssistida implements Validatable {
	
	/** Status que indica que o plano está cadastrado, permitindo ser alterado. */
	public static final int CADASTRADO = 1;
	/** Status que indica que o plano foi submetido, não permitindo ser alterado */
	public static final int SUBMETIDO = 2;
	/** Status que indica que o plano foi aprovado */
	public static final int APROVADO = 3;
	/** Status que indica que o plano foi reprovado */
	public static final int REPROVADO = 4;
	/** Status que indica que o plano foi Solicitado Alteração do Plano */
	public static final int SOLICITADO_ALTERACAO = 5;
	/** Status que indica que o plano foi Concluído */
	public static final int CONCLUIDO = 6;
	/** Status que indica que foi solicitado análise do relatório Semestral */
	public static final int ANALISE_RELATORIO = 7;	
	/** Status que indica que o plano foi Solicitado Alteração do Relatório Semestral */
	public static final int SOLICITADO_ALTERACAO_RELATORIO = 8;	
	/** Status que indica que o plano foi cancelado */
	public static final int CANCELADO = 9;	
	/** Descrição dos Status */
	public static final Map<Integer, String> DESCRICOES_STATUS;
	static {
		DESCRICOES_STATUS = new HashMap<Integer, String>();
		DESCRICOES_STATUS.put(CADASTRADO, "CADASTRADO");
		DESCRICOES_STATUS.put(SUBMETIDO, "SUBMETIDO");
		DESCRICOES_STATUS.put(APROVADO, "APROVADO");
		DESCRICOES_STATUS.put(REPROVADO, "REPROVADO");
		DESCRICOES_STATUS.put(SOLICITADO_ALTERACAO, "SOLICITADO ALTERAÇÃO NO PLANO");
		DESCRICOES_STATUS.put(CONCLUIDO, "CONCLUÍDO");
		DESCRICOES_STATUS.put(ANALISE_RELATORIO, "RELATÓRIO EM ANÁLISE");
		DESCRICOES_STATUS.put(SOLICITADO_ALTERACAO_RELATORIO, "SOLICITADO ALTERAÇÃO NO RELATÓRIO");
		DESCRICOES_STATUS.put(CANCELADO, "CANCELADO");
	}	
	
	/** Construtor */
	public PlanoDocenciaAssistida() {

	}
	
	/**
	 * Chave primária da indicação.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_plano_docencia_assistida")
	private int id;	
	
	/**
	 * Discente que enviou o plano.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private DiscenteStricto discente;	
	
	/**
	 * Componente curricular que o discente deseja lecionar.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricular componenteCurricular;
	
	/**
	 * Docente da atividade informada.
	 * É informado no caso de atividade que não possui turma.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor = new Servidor();	
	
	
	/**
	 * Período de Indicação Bolsa REUNI.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_periodo_indicacao_reuni")
	private PeriodoIndicacaoReuni periodoIndicacaoBolsa;	
	
	/**
	 * Curso que o discente deseja lecionar.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;
	
	/**
	 * Modalidade da Bolsa, Caso tiver.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_modalidade_bolsa_externa")
	private ModalidadeBolsaExterna modalidadeBolsa;	
	
	/**
	 * Atividade do Plano de Docência Assistida
	 */
	@OneToMany(mappedBy = "planoDocenciaAssistida", fetch = FetchType.LAZY)
	private List<AtividadeDocenciaAssistida> atividadeDocenciaAssistida = new ArrayList<AtividadeDocenciaAssistida>();
	
	/**
	 * Turmas do plano de Docência Assistida
	 */
	@OneToMany(mappedBy = "planoDocenciaAssistida", cascade = CascadeType.ALL, fetch = FetchType.EAGER)	
	private List<TurmaDocenciaAssistida> turmaDocenciaAssistida = new ArrayList<TurmaDocenciaAssistida>();		
	
	/**
	 * Histórico de Movimentações do Plano de Docência Assistida
	 */
	@OneToMany(mappedBy = "planoDocenciaAssistida", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<HistoricoPlanoDocenciaAssistida> historicoMovimentacoes = new ArrayList<HistoricoPlanoDocenciaAssistida>();
	
	/**
	 * Pareceres do Plano de Docência Assistida
	 */
	@OneToMany(mappedBy = "planoDocenciaAssistida", fetch = FetchType.LAZY)
	private List<ParecerPlanoDocenciaAssistida> parecerDocenciaAssistida = new ArrayList<ParecerPlanoDocenciaAssistida>();	
	
	/**
	 * Caso não for nenhuma das modalidades de bolsas, informa neste atributo.
	 */
	@Column(name = "outra_modalidade")
	private String outraModalidade;
	
	/**
	 * Justificativa para escolha do componente curricular.
	 */
	private String justificativa;
	
	/**
	 * Objetivos a serem seguidos.
	 */
	private String objetivos;
	
	/**
	 * Indica se a indicação está ativa ou não.
	 */
	private boolean ativo;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/** Ano do Plano de Docência Assistida */
	private Integer ano;
	/** Período do Plano de Docência Assistida */
	private Integer periodo;
	/** Status do Plano de Docência Assistida */
	private int status;
	
	/**
	 * Observação caso for reprovado o plano de trabalho.
	 */
	private String observacao;
	
	/**
	 * Análise da contribuição para formação docente
	 */
	private String analise;
	
	/**
	 * Sugestões para o plano de docência assistida
	 */
	private String sugestoes;
	
	/** Arquivo anexado */
	@Column(name="id_arquivo")
	private Integer idArquivo;	
	
	/**
	 * Auxilia no cadastro para indicar se o discente é bolsista ou não.
	 */
	@Transient
	private boolean bolsista;
	
	/**
	 * Auxilia para identificar os planos modificados pela PPG
	 */
	@Transient
	private boolean modificadoPorPPG;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteStricto getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	public void setComponenteCurricular(ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}	

	/**
	 * Compara o ID e do estágio com o passado por parâmetro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}


	/** 
	 * Calcula e retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}	

	/**
	 * Indica se é bolsista ou não
	 * @return
	 */
	public boolean isBolsista() {
	
		if ( !isEmpty( getModalidadeBolsa() ) || !isEmpty( getOutraModalidade() ) )
			setBolsista(true);
		
		return bolsista;
		
	}

	public void setBolsista(boolean bolsista) {
		this.bolsista = bolsista;
	}

	public ModalidadeBolsaExterna getModalidadeBolsa() {
		return modalidadeBolsa;
	}

	public void setModalidadeBolsa(ModalidadeBolsaExterna modalidadeBolsa) {
		this.modalidadeBolsa = modalidadeBolsa;
	}

	public String getOutraModalidade() {
		return outraModalidade;
	}

	public void setOutraModalidade(String outraModalidade) {
		this.outraModalidade = outraModalidade;
	}

	/**
	 * Valida os atributos do plano. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate() 
	 */		
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(discente, "Discente", lista);
		
		if (isBolsista() && ValidatorUtil.isEmpty(modalidadeBolsa) && ValidatorUtil.isEmpty(outraModalidade))
			ValidatorUtil.validateRequired(modalidadeBolsa, "Modalidade Bolsa", lista);			
		
		ValidatorUtil.validateRequired(componenteCurricular, "Componente Curricular", lista);
		ValidatorUtil.validateRequired(curso, "Curso de Graduação", lista);
		
		if (componenteCurricular.isAtividade())
			ValidatorUtil.validateRequired(servidor, "Docente", lista);
		
		ValidatorUtil.validateRequired(justificativa, "Justificativa", lista);
		ValidatorUtil.validateRequired(objetivos, "Objetivo", lista);
		
		ValidatorUtil.validateRequired(atividadeDocenciaAssistida, "Atividade", lista);	
		return lista;
	}

	/**
	 * Retorna as atividades do plano
	 * @return
	 */
	public List<AtividadeDocenciaAssistida> getAtividadeDocenciaAssistida() {

		if (atividadeDocenciaAssistida == null)
			return null;
		
		Collections.sort( atividadeDocenciaAssistida, new Comparator<AtividadeDocenciaAssistida>(){
			public int compare(AtividadeDocenciaAssistida arg0,	AtividadeDocenciaAssistida arg1) {
				return new CompareToBuilder()
				.append(arg0.getDataInicio(), arg1.getDataInicio())
				.append(arg0.getDataFim(), arg1.getDataFim())
				.append(arg0.getId(), arg1.getId())
				.toComparison();  
			}
		});																
		return atividadeDocenciaAssistida;
	}

	public void setAtividadeDocenciaAssistida(
			List<AtividadeDocenciaAssistida> atividadeDocenciaAssistida) {
		this.atividadeDocenciaAssistida = atividadeDocenciaAssistida;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<TurmaDocenciaAssistida> getTurmaDocenciaAssistida() {
		return turmaDocenciaAssistida;
	}

	public void setTurmaDocenciaAssistida(
			List<TurmaDocenciaAssistida> turmaDocenciaAssistida) {
		this.turmaDocenciaAssistida = turmaDocenciaAssistida;
	}

	public PeriodoIndicacaoReuni getPeriodoIndicacaoBolsa() {
		return periodoIndicacaoBolsa;
	}

	public void setPeriodoIndicacaoBolsa(PeriodoIndicacaoReuni periodoIndicacaoBolsa) {
		this.periodoIndicacaoBolsa = periodoIndicacaoBolsa;
	}
	
	/**
	 * Retorna a descrição do status atual
	 * @return
	 */
	@Transient
	public String getDescricaoStatus() {
		return DESCRICOES_STATUS.get(status);
	}	
	
	/**
	 * Retorna a descrição do status passado por parâmetro
	 * @param status
	 * @return
	 */
	@Transient
	public static String getDescricaoStatus(int status) {
		return DESCRICOES_STATUS.get(status);
	}		
	
	/**
	 * Verifica se o plano está com status igual a submetido
	 * @return
	 */
	@Transient
	public boolean isSubmetido(){
		return status == SUBMETIDO;
	}
	
	/**
	 * Verifica se o plano está com status igual a aprovado
	 * @return
	 */
	@Transient
	public boolean isAprovado(){
		return status == APROVADO;
	}	
	
	/**
	 * Verifica se o plano está com status igual a submetido
	 * @return
	 */
	@Transient
	public boolean isReprovado(){
		return status == REPROVADO;
	}
	
	/**
	 * Verifica se o plano está com status igual a cadastrado
	 * @return
	 */
	@Transient
	public boolean isCadastrado(){
		return status == CADASTRADO;
	}
	
	/**
	 * Verifica se o plano está com status igual a Solicitado alteração no plano
	 * @return
	 */
	@Transient
	public boolean isSolicitadoAlteracao(){
		return status == SOLICITADO_ALTERACAO;
	}	
	
	/**
	 * Verifica se o plano está com status igual a Solicitado alteração no relatório
	 * @return
	 */
	@Transient
	public boolean isSolicitadoAlteracaoRelatorio(){
		return status == SOLICITADO_ALTERACAO_RELATORIO;
	}		
	
	/**
	 * Verifica se o plano está com status igual a concluído
	 * @return
	 */
	@Transient
	public boolean isConcluido(){
		return status == CONCLUIDO;
	}	
	
	/**
	 * Verifica se o plano está com status igual a cancelado
	 * @return
	 */
	@Transient
	public boolean isCancelado(){
		return status == CANCELADO;
	}		
	
	/**
	 * Verifica se o plano está com status igual a submetido para análise do relatório
	 * @return
	 */
	@Transient
	public boolean isAnaliseRelatorio(){
		return status == ANALISE_RELATORIO;
	}	
	
	/**
	 * Verifica se o plano é reuni
	 * @return
	 */
	@Transient
	public boolean isReuni(){
		return periodoIndicacaoBolsa != null;
	}
	
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getAnalise() {
		return analise;
	}

	public void setAnalise(String analise) {
		this.analise = analise;
	}

	public String getSugestoes() {
		return sugestoes;
	}

	public void setSugestoes(String sugestoes) {
		this.sugestoes = sugestoes;
	}

	/**
	 * Retorna o ano do plano.
	 * @return
	 */
	public Integer getAno() {
		if (ValidatorUtil.isEmpty(periodoIndicacaoBolsa))
			return ano;
		else
			return periodoIndicacaoBolsa.getAno();
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/**
	 * Retorna o período do plano.
	 * @return
	 */
	public Integer getPeriodo() {
		if (ValidatorUtil.isEmpty(periodoIndicacaoBolsa))
			return periodo;
		else
			return periodoIndicacaoBolsa.getPeriodo();		
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public List<HistoricoPlanoDocenciaAssistida> getHistoricoMovimentacoes() {
		return historicoMovimentacoes;
	}

	public void setHistoricoMovimentacoes(
			List<HistoricoPlanoDocenciaAssistida> historicoMovimentacoes) {
		this.historicoMovimentacoes = historicoMovimentacoes;
	}
	
	/**
	 * Verifica se possui indicação
	 * @return
	 */
	public boolean isPossuiIndicacao(){
		return (this.periodoIndicacaoBolsa != null && periodoIndicacaoBolsa.getAnoPeriodo() != null);
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}
	
	/**
	 * Verifica se é permitido analisar o plano de docência assistida
	 * @return
	 */
	public boolean isPermiteAnalisarPlano(){
		return isSubmetido();
	}
	
	/**
	 * Verifica se é permitido analisar o relatório semestral
	 * @return
	 */
	public boolean isPermiteAnalisarRelatorio(){
		return isAnaliseRelatorio();
	}

	public boolean isModificadoPorPPG() {
		return modificadoPorPPG;
	}

	public void setModificadoPorPPG(boolean modificadoPorPPG) {
		this.modificadoPorPPG = modificadoPorPPG;
	}

	public List<ParecerPlanoDocenciaAssistida> getParecerDocenciaAssistida() {
		return parecerDocenciaAssistida;
	}

	public void setParecerDocenciaAssistida(
			List<ParecerPlanoDocenciaAssistida> parecerDocenciaAssistida) {
		this.parecerDocenciaAssistida = parecerDocenciaAssistida;
	}
}
