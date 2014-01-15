/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 05/12/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroAcessoPublico;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;
import br.ufrn.sigaa.questionario.dominio.Alternativa;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * Registro de inscrição de um candidato em um processo seletivo.
 * 
 * @author Gleydson
 * @author Ricardo Wendell
 * 
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "inscricao_selecao", schema = "ensino")
public class InscricaoSelecao extends AbstractMovimento implements PersistDB, Validatable {

	/**
	 * Atributo que identifica a chave-primária na base. 
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id", nullable = false)
	private int id;

	/**
	 * Atributo que define o processo seletivo associado a inscrição
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivo processoSeletivo;

	/**
	 * Atributo que define os dados pessoais associado a inscrição.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa_inscricao")
	private PessoaInscricao pessoaInscricao;

	/**
	 * Atributo que define o ip do candidato no momento da inscrição.
	 */
	private String ip;

	/**
	 * Atributo que define o número da inscrição que servirá de orientação para o candidato.
	 */
	@Column(name = "numero_inscricao")
	private long numeroInscricao;

	/**
	 * Atributo que define a data em que a inscrição foi realizada.
	 */
	@Column(name = "data_inscricao")
	@CriadoEm
	private Date dataInscricao;
	
	/**
	 * Utilizado no processo seletivo de TRANSFERÊNCIA VOLUNTÁRIA,
	 * corresponde a data agendada para entrega dos documentos conforme edital
	 */
	@Deprecated
	@Column(name = "data_agendada")
	private Date dataAgendada;
	
	/**
	 * Atributo que define a data de agendamento do candidato.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_agenda_processo_seletivo",nullable = true)
	private AgendaProcessoSeletivo agenda; 

	/**
	 * Atributo que define a linha de pesquisa escolhida.
	 * Utilizado somente para os casos de processo seletivos de Stricto-Sensu
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_linha_pesquisa")
	private LinhaPesquisaStricto linhaPesquisa;

	/**
	 * Atributo que define a área de concentração selecionada pelo candidato.
	 * Utilizado somente para os casos de processo seletivos de Stricto-Sensu
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_concentracao")
	private AreaConcentracao areaConcentracao;

	/**
	 * Atributo que define o orientador selecionado pelo candidato.
	 * Utilizado somente para os casos de processo seletivos de Stricto-Sensu
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_equipe_programa")
	private EquipePrograma orientador;

	/** Observações acerca da inscrição. */
	private String observacoes;
	
	/** Status da inscrição. */
	private int status;
	
	/** ID do arquivo PDF do projeto enviado pelo candidato. */
	@Column(name="id_arquivo_projeto")
	private Integer idArquivoProjeto;
	
	/** Utilizado na listagem do relatório da TRANSFERÊNCIA VOLUNTÁRIA 
	 *  onde o questionário tem uma resposta única para o(idioma) escolhido
	 *  do inscrito */
	@Transient
	private Alternativa alternativa;
	
	/** Número de Referência utilizado na GRU quando o concurso tem taxa de inscrição. */
	@Column(name="numero_referencia_gru")
	private Long numeroReferenciaGRU;
	
	/** Indica se a inscrição está selecionada ou não em uma lista de inscrições. Utilizado na validação de inscrições. */
	@Transient
	private boolean selecionado;
	
	/** GRU gerada para o pagamento da taxa desta inscrição. */
	@Column(name = "id_gru")
	private Integer idGRU;
	
	/** Atributo que define o responsável pelo cadastro através gerencia de processo seletivos. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada criadoPor;
	
	/** Atributo que define o responsável pela atualização através gerencia de processo seletivos. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada atualizadoPor;
	
	/** Atributo que define o responsável pela inscrição no processo seltivo (Portal Público) */
	@ManyToOne(fetch = FetchType.LAZY)	
	@JoinColumn(name = "id_registro_publico")
	private RegistroAcessoPublico registroPublico;	

	/** Indica se a GRU foi quitada (paga). */
	@Column(name = "gru_quitada")
	private boolean gruQuitada;
	
	public InscricaoSelecao() {
		this.status = StatusInscricaoSelecao.SUBMETIDA;
	}
	
	/**
	 * Retorna um hash code com os dados relevantes do inscrito
	 * @return
	 */
	public String getCodigoHash(){

		return UFRNUtils.toSHA1Digest(
				"PS"+getProcessoSeletivo().getId()
				+getId()
				+getPessoaInscricao().getCpf()).substring(0, 20);
		
	}
	
	/** Instancia atributos que estão nulos. */
	public void prepararDados() {
		if (agenda           == null) this.agenda            = new AgendaProcessoSeletivo();
		if (alternativa      == null) this.alternativa       = new Alternativa();
		if (areaConcentracao == null) this.areaConcentracao  = new AreaConcentracao();
		if (linhaPesquisa    == null) this.linhaPesquisa     = new LinhaPesquisaStricto();
		if (orientador       == null) this.orientador        = new EquipePrograma();
	}
	
	/**
	 * Define como null atributos que estiverem com valores vazios
	 */
	public void anularAtributosVazios() {
		if (isEmpty(agenda          )) this.agenda            = null;
		if (isEmpty(alternativa     )) this.alternativa       = null;
		if (isEmpty(areaConcentracao)) this.areaConcentracao  = null;
		if (isEmpty(linhaPesquisa   )) this.linhaPesquisa     = null;
		if (isEmpty(orientador      )) this.orientador        = null;
	}

	public LinhaPesquisaStricto getLinhaPesquisa() {
		return linhaPesquisa;
	}

	public void setLinhaPesquisa(LinhaPesquisaStricto linhaPesquisa) {
		this.linhaPesquisa = linhaPesquisa;
	}

	public EquipePrograma getOrientador() {
		return orientador;
	}

	public void setOrientador(EquipePrograma orientador) {
		this.orientador = orientador;
	}

	public InscricaoSelecao(ProcessoSeletivo processo) {
		this.processoSeletivo = processo;
	}

	public Date getDataInscricao() {
		return dataInscricao;
	}

	public void setDataInscricao(Date dataInscricao) {
		this.dataInscricao = dataInscricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public ProcessoSeletivo getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivo selecao) {
		this.processoSeletivo = selecao;
	}

	public PessoaInscricao getPessoaInscricao() {
		return this.pessoaInscricao;
	}

	public void setPessoaInscricao(PessoaInscricao pessoaInscricao) {
		this.pessoaInscricao = pessoaInscricao;
	}

	public long getNumeroInscricao() {
		return this.numeroInscricao;
	}

	public void setNumeroInscricao(long numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	public AreaConcentracao getAreaConcentracao() {
		return areaConcentracao;
	}

	public void setAreaConcentracao(AreaConcentracao areaConcentracao) {
		this.areaConcentracao = areaConcentracao;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Date getDataAgendada() {
		return dataAgendada;
	}

	public void setDataAgendada(Date dataAgendada) {
		this.dataAgendada = dataAgendada;
	}

	public boolean isAprovada() {
		return this.status == StatusInscricaoSelecao.APROVADO_SELECAO;
	}
	
	/**
	 * Retorna a descrição do status da inscrição
	 * 
	 * @return
	 */
	public String getDescricaoStatus() {
		return StatusInscricaoSelecao.todosStatus.get(status);
	}
	
	public Questionario getQuestionario() {
		return processoSeletivo.getQuestionario();
	}
	
	/**
	 * Retorna o nível de ensino do processo seletivo
	 * 
	 * @return
	 */
	public char getNivelEnsino() {
		return getProcessoSeletivo().getNivelEnsino();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		erros.addAll(pessoaInscricao.validate());

		return erros;
	}

	public Alternativa getAlternativa() {
		return alternativa;
	}

	public void setAlternativa(Alternativa alternativa) {
		this.alternativa = alternativa;
	}

	public Integer getIdArquivoProjeto() {
		return idArquivoProjeto;
	}

	public void setIdArquivoProjeto(Integer idArquivoProjeto) {
		this.idArquivoProjeto = idArquivoProjeto;
	}

	public AgendaProcessoSeletivo getAgenda() {
		return agenda;
	}

	public void setAgenda(AgendaProcessoSeletivo agenda) {
		this.agenda = agenda;
	}

	public Long getNumeroReferenciaGRU() {
		return numeroReferenciaGRU;
	}

	public void setNumeroReferenciaGRU(Long numeroReferenciaGRU) {
		this.numeroReferenciaGRU = numeroReferenciaGRU;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Integer getIdGRU() {
		return idGRU;
	}

	public void setIdGRU(Integer idGRU) {
		this.idGRU = idGRU;
	}

	public boolean isGruQuitada() {
		return gruQuitada;
	}

	public void setGruQuitada(boolean gruQuitada) {
		this.gruQuitada = gruQuitada;
	}

	public RegistroEntrada getAtualizadoPor() {
		return atualizadoPor;
	}

	public void setAtualizadoPor(RegistroEntrada atualizadoPor) {
		this.atualizadoPor = atualizadoPor;
	}

	public RegistroAcessoPublico getRegistroPublico() {
		return registroPublico;
	}

	public void setRegistroPublico(RegistroAcessoPublico registroPublico) {
		this.registroPublico = registroPublico;
	}

}
