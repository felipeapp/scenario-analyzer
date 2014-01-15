/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2009
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * <p>Representa um per�odo de inscri��o aberto pelo portal p�blico, para viabilizar inscri��es 
 * de participantes em um curso ou evento de extens�o.</p>
 * 
 * <p>Cada curso ou evento pode ter v�rias inscri��es ativas por vez, desde que essas inscri��es n�o 
 * possuam datas coincidentes.
 * <strong>Isso suporta per�odos de inscri��o com valores de taxa diferentes.</strong>
 * </p>
 * <p>
 * Os inscritos aceitos pelo coordenador do curso/evento s�o cadastrados tamb�m 
 * como participantes de a��es de extens�o #{@link br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao}.
 * </p>
 * 
 * @author Daniel Augusto
 * 
 * @version 1.5 - Jadson - Adicionando as inforama��es sobre cobran�a de taxa que ficava anteriormente 
 * na entidade CursoEventoExtensao.java, agora cada inscri��o vai poder ter um valor, inclusive esse 
 * valor vai poder ser diferente dependendo da modalidade do participante.
 * 
 */
@Entity
@Table(schema = "extensao", name = "inscricao_atividade")
public class InscricaoAtividade implements Validatable {
	
	/** Coordenador da atividade confirma a inscri��o do participante. */
	public static final int COM_CONFIRMACAO = 1;
	/** Ao se inscrever ocupa a vaga da atividade automaticamente, sem a necessidadede interven��o do coordenador da a��o. */
	public static final int PREENCHIMENTO_AUTOMATICO = 2;
	
	/** Identificador InscricaoAtividade */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="extensao.inscricao_atividade_sequence") })
	@Column(name = "id_inscricao_atividade", nullable = false)
	private int id;

	/** 
	 *  Quantidade *** TOTAL *** de vagas dispon�veis para inscri��o.
	 *  � o n�mero efetivo que realmente conta. Tem outro n�mero de vagas informado no 
	 *  cadastro de cursos e eventos que n�o serve para muita coisa.  
	 */
	@Column(name="quantidade_vagas", nullable=false)
	private int quantidadeVagas;	
	
	
	/** M�todo de Preenchimento das vagas: 1 - Com confirma��o ( default ) 2 - Preenchimento autom�tico */
	@Column(name="metodo_preenchimento")
	private Integer metodoPreenchimento = COM_CONFIRMACAO;	

	/** Instru��es para inscri��o. 
	 *  Informadas pelo coordenador no momento do cadastro e visualizadas pelo usu�rio no momento da inscri��o. 
	 */
	@Column(name="instrucoes_inscricao")
	private String instrucoesInscricao;

	
	////////////////////////////// Dados para Auditoria  ///////////////////////////////////////////
	
	/** Data de cadastro. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/** Registro de entrada do respons�vel pelo cadastro da inscri��o. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	
	/** Data de cadastro. */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_ultima_atualizacao")
	private Date dataUltimaAtualizacao;

	/** Registro de entrada do respons�vel pelo cadastro da inscri��o. */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	private RegistroEntrada registroUltimaAtualizacao;
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/** In�cio das inscri��es */
	@Temporal(TemporalType.DATE)
	@Column(name="periodo_inicio")
	private Date periodoInicio;
	
	/** Fim das inscri��es */
	@Temporal(TemporalType.DATE)
	@Column(name="periodo_fim")
	private Date periodoFim;

	/** <p>Informa��es que o coordenador deseja passar para os participantes.</p>
	 *  
	 *  <p>Esses informa��es s�o cadastradas pelo coordenador no momento da abertura da inscri��o e s�o 
	 *  visualizadas pelo participante quando ele entra na sua �rea privada para gerenciar as inscri��es 
	 *  que ele tenha realizado. </p>
	 */
	private String observacoes;

	
	/** Motivo do cancelamento da inscri��o */
	@Column(name="motivo_cancelamento")
	private String motivoCancelamento;

	
	/** 
	 *  Informa se a inscri��o esta ativa no sistema. 
	 *  Ao ser removida, caso haja inscritos, esses inscri��es devem ser canceladas e os inscritos avisados.
	 *  
	 *  Uma inscri��o � desativada pelo opera��o "Suspender Inscri��o"
	 *  
	 */
	@Column(name="ativo", nullable=false)
	private boolean ativo = true;

	
	/** Torna o envio de arquivo obrigat�rio pelo participante do curso/evento. */
	@Column(name="envio_arquivo_obrigatorio")
	private boolean envioArquivoObrigatorio = false;
	
	/** sequ�ncia curso/evento */
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "sequencia", unique = true, nullable = false)
	private int sequencia;

	/////////////////////////////////////////////////////////////////////////////
	
	/** A��o de Extens�o vinculada a inscri��o. 
	 *  <strong>***IMPORTANTE:***</strong> Uma incri��o n�o pode ser da atividade e da sub atividade ao mesmo tempo. OU � de uma ou � da outra. 
	 *  Existe uma constante no banco para isso. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_atividade")
	private AtividadeExtensao atividade;
	
	/** SubAtividadeExtensao vinculada a inscri��o.<br/>
	 *  <strong>***IMPORTANTE:***</strong> Uma incri��o n�o pode ser da atividade e da sub atividade ao mesmo tempo. OU � de uma ou � da outra. 
	 *  Existe uma constante no banco para isso. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_sub_atividade_extensao")
	private SubAtividadeExtensao subAtividade;
	
	/////////////////////////////////////////////////////////////////////////////
	
	
	
	/** Atributo que define o question�rio para a inscri��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_questionario")
	private Questionario questionario;

	
	/** Cole��o de participantes da inscri��o */
	@OneToMany(mappedBy = "inscricaoAtividade")
	private List<InscricaoAtividadeParticipante> inscricoesParticipantes = new ArrayList<InscricaoAtividadeParticipante>();
	
	
	/** 
	 * Informa se o curso � gratuito ou n�o. O valor da taxa vai depender da ModalidadeParticipante do participante. 
	 * (se � profissional, estudante, etc...). Pode ter valores diferentes para cada um deles.
	 */
	@Column(name = "cobranca_taxa_matricula", nullable=true)
	private boolean cobrancaTaxaMatricula =false;


	/**  
	 * <p>A data de vencimento para a emiss�o da GRU para essa inscri��o, s� usado caso seja cobrada uma taxa de matr�cula.</p> 
	 * 
	 * <p>Caso seja cobrado e esse valor n�o seja informado, a data de vencimento ser� por padr�o data de in�cio do evento.</p> 
	 */
	@Column(name = "data_vencimento_gru", nullable=true)
	private Date dataVencimentoGRU = null;
	
	
	/** As modalidades de inscri��o caso a inscri��o possua coban�a de taxa de inscri��o. 
	 *  Cont�m o valor cobrado para cada modalidade participante.*/
	@OneToMany(mappedBy = "periodoInscricao")
	private List<ModalidadeParticipantePeriodoInscricaoAtividade> modalidadesParticipantes;
	
	
	/** DataModel para facilitar a adi��o e remo��o da listagem de modalidades na p�ginas JSF */
	@Transient
	private DataModel modalidadesParticipantesDataModel;
	
	
	/** 
	 * Quantidade de usu�rios apenas inscritos ainda n�o aprovados. S� usado de a inscri��o exije que o coordenado aprove.
	 * A quantidade de inscritos N�O considera a quantidade de inscritos aprovados. */
	@Transient
	private Integer quantidadeInscritos = 0;	
	
	
	/** Quantidade de usu�rios inscritos Aprovados. 
	 * Aprovados automaticamente ou pelo coordendor, caso a inscri��o exija uma aprova��o pelo coordenador. */
	@Transient
	private Integer quantidadeInscritosAprovados = 0;	
	
	/** Guarda temporariamente para as inscri��es em atividades a quantidade de per�odos de inscri��es em mini atividades existentes */
	@Transient
	private Integer quantidadePeriodosInscricoesMiniAtividade = 0;
	
	/** Guarda temporariamente para as inscri��es em atividades se o usu�rio atualmente logado est� inscrito. */
	@Transient
	private boolean estouInscrito = false;
	
	
	/**
	 * Construtor padr�o
	 */
	public InscricaoAtividade() {}

	/**
	 * Construtor parametrizado: id
	 */
	public InscricaoAtividade(int id) { 
		this.id = id;
	}

	/**
	 * Construtor parametrizado: atividade
	 */
	public InscricaoAtividade(AtividadeExtensao atividade) { 
		this.atividade = atividade;
	}
	
	
	/**
	 * <p>Adiciona uma nova modalide inscri��o � inscri��o</p>
	 *
	 *  <p> � atravez dessa modalidade que se define o valor a ser combrado para o participante. <p>
	 *
	 * @param modalidade
	 */
	public void adicionaMolidadeParticipante(ModalidadeParticipantePeriodoInscricaoAtividade modalidade){
		if( modalidadesParticipantes == null )
			this.modalidadesParticipantes = new ArrayList<ModalidadeParticipantePeriodoInscricaoAtividade>();
		
		modalidade.setPeriodoInscricao(this);
		this.modalidadesParticipantes.add(modalidade);
		
		modalidadesParticipantesDataModel = new ListDataModel(this.modalidadesParticipantes);
	}
	
	

	/** Calcula quantidade de vagas restantes
	 * 
	 * � a quantidade de vagas total - a quantidade de vagas do aprovados - a quantidade de vagas dos aceitos (caso exista)
	 *
	 * IMPORTANTE:
	 * 
	 * "quantidadeInscritosAprovados" e "quantidadeInscritos" s�o calculadas "on-line" n�o s�o salvas 
	 * no banco, ent�o antes de usar esse m�todo certifique-se que os dados est�o populados para n�o gerar inconsist�ncia para o usu�rio
	 */
	public int getQuantidadeVagasRestantes() {
		return quantidadeVagas - quantidadeInscritosAprovados - quantidadeInscritos;
	}
	
	
	/** retorna c�digo gerado para curso/evento */
	public String getCodigo() {
		char codigo = 'X';
		
		// Verifica se � inscri��o de atividade ou sub atividade //
		// Nunca pode ser as duas ao mesmo tempo                 //
		
		AtividadeExtensao atividadeGerarCodigo = null;
		
		if(this.atividade != null)
			atividadeGerarCodigo = this.atividade;
		else
			atividadeGerarCodigo =  this.subAtividade.getAtividade();
		

		if (atividadeGerarCodigo.getTipoAtividadeExtensao() != null) {
			switch (atividadeGerarCodigo.getTipoAtividadeExtensao().getId()) {
			case TipoAtividadeExtensao.CURSO:
				codigo = 'C';
				break;
			case TipoAtividadeExtensao.EVENTO:
				codigo = 'E';
				break;
			}
		}
		NumberFormat formatter = new DecimalFormat("000");
		return "I" + codigo + "-" + formatter.format(sequencia);
	}
	
	
	
	/** Valida as regras de neg�cio no preencimento dos dados de uma inscri��o */
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validaInt(this.quantidadeVagas, "Quantidade de Vagas", lista);
		ValidatorUtil.validateRequiredId(metodoPreenchimento, "M�todo de Preenchimento das vagas", lista);
		if (this.periodoInicio == null) {
			lista.addErro("Data Inicial no Per�odo de Inscri��o: Campo obrigat�rio n�o informado ou data inv�lida.");
		}
		if (this.periodoFim == null) {
			lista.addErro("Data Final no Per�odo de Inscri��o: Campo obrigat�rio n�o informado ou data inv�lida.");
		}
		ValidatorUtil.validaInicioFim(this.periodoInicio, this.periodoFim, "Per�odo de Inscri��o", lista);
		ValidatorUtil.validateRequired(this.instrucoesInscricao, "Instru��es da Inscri��o", lista);
		ValidatorUtil.validateRequired(this.observacoes, "Informa��es Gerais", lista);
		
		ValidatorUtil.validateRequired(this.quantidadeVagas, "Quantidade de Vagas", lista);
		
		if ( quantidadeVagas <= 0 ) {
			lista.addErro("O n�mero de vagas n�o pode ser menor ou igual a zero");
		}
		
		if(cobrancaTaxaMatricula){
			if(modalidadesParticipantes == null || modalidadesParticipantes.size() ==0 ){
				lista.addErro("� preciso informar pelo menos um valor para a taxa de inscri��o");
			}else{
				for(ModalidadeParticipantePeriodoInscricaoAtividade modalidade : modalidadesParticipantes){
					
					if(modalidade.getModalidadeParticipante() == null || modalidade.getModalidadeParticipante().getId() <= 0 ){
						lista.addErro("Informa��o da modalidade de participante incorreta. ");
					}else{
					
						// Verifica se o usu�rio informou o valor da taxa, pode ser zero caso alguma modalidade seja gratuita //
						if(modalidade.getTaxaMatricula() == null || modalidade.getTaxaMatricula().compareTo(new BigDecimal(0) ) < 0 ){
							lista.addErro("� preciso informar o valor para a taxa de inscri��o para a molidade de participante: "+modalidade.getModalidadeParticipante().getNome());
						}
					}
				}
			}
		}
		
		return lista;
	}

	
	
	
	///// sets e gets /////
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public void setInscricoesParticipantes(List<InscricaoAtividadeParticipante> inscricoesParticipantes) {
		this.inscricoesParticipantes = inscricoesParticipantes;
	}

	public Collection<InscricaoAtividadeParticipante> getInscricoesParticipantes() {
		return inscricoesParticipantes;
	}

	public int getQuantidadeVagas() {
		return quantidadeVagas;
	}

	public void setQuantidadeVagas(int quantidadeVagas) {
		this.quantidadeVagas = quantidadeVagas;
	}

	public String getInstrucoesInscricao() {
		return instrucoesInscricao;
	}

	public void setInstrucoesInscricao(String instrucoesInscricao) {
		this.instrucoesInscricao = instrucoesInscricao;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public int getSequencia() {
		return sequencia;
	}

	public void setSequencia(int sequencia) {
		this.sequencia = sequencia;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	public Date getPeriodoInicio() {
		return periodoInicio;
	}

	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	public Date getPeriodoFim() {
		return periodoFim;
	}

	/** Retorna a data inicial do per�odo de inscri��o formatado para exibi��o para o usu�rio. */
	public String getPeriodoInicioFormatado() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(periodoInicio);
	}
	
	/** Retorna a data final do per�odo de inscri��o formatado para exibi��o para o usu�rio. */
	public String getPeriodoFimFormatado() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(periodoFim);
	}
	

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}

	/** Verifica se o per�odo de inscri��o est� aberto, s� pode ter um aberto por fez para uma atividade ou mini atividade. */
	public boolean isAberta() {
		return CalendarUtils.isDentroPeriodo(periodoInicio, periodoFim);
	}

	/** Verifica se o per�odo de inscri��o � um per�odo que vai ser aberto no futuro. 
	 * Uma atividade pode ter mais de um per�odo de inscri��o para suportar diferentes valores a serem 
	 * cobrados, dependendo de quando o usu�rio fez a inscri��o. */
	public boolean isFutura() {
		return CalendarUtils.compareTo(periodoInicio, CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0) ) > 0;
	}
	
	/** Verifica se o per�odo de inscri��o j� passou. */
	public boolean isEncerrada() {
		return ! isAberta() && ! isFutura();
	}
	
	/** Verifica se a inscri��o � de uma atividade ou de uma sub atividade. N�O PODE SER DOS DOIS AO MEMSO TEMPO. */
	public boolean isInscricaoAtividade() {
		return atividade != null && subAtividade == null;
	}

	
	/** Verifica o m�todo de preenchimento � com confirma��o. */
	public boolean isMetodoDePreenchimentoComConfirmacao() {
		return this.metodoPreenchimento != null && this.metodoPreenchimento.equals(COM_CONFIRMACAO);
	}
	
	public boolean isPossuiVagasDisponiveis() {
		return getQuantidadeVagasRestantes() > 0;
	}

	public Integer getQuantidadeInscritos() {
		return quantidadeInscritos;
	}

	public void setQuantidadeInscritos(Integer quantidadeInscritos) {
		this.quantidadeInscritos = quantidadeInscritos;
	}

	public Integer getQuantidadeInscritosAprovados() {
		return quantidadeInscritosAprovados;
	}

	public void setQuantidadeInscritosAprovados(Integer quantidadeInscritosAprovados) {
		this.quantidadeInscritosAprovados = quantidadeInscritosAprovados;
	}

	public boolean isEnvioArquivoObrigatorio() {
		return envioArquivoObrigatorio;
	}

	public void setEnvioArquivoObrigatorio(boolean envioArquivoObrigatorio) {
		this.envioArquivoObrigatorio = envioArquivoObrigatorio;
	}

	public boolean isCobrancaTaxaMatricula() {
		return cobrancaTaxaMatricula;
	}

	public void setCobrancaTaxaMatricula(boolean cobrancaTaxaMatricula) {
		this.cobrancaTaxaMatricula = cobrancaTaxaMatricula;
	}
	
	public Date getDataVencimentoGRU() {
		return dataVencimentoGRU;
	}

	public void setDataVencimentoGRU(Date dataVencimentoGRU) {
		this.dataVencimentoGRU = dataVencimentoGRU;
	}
	
	public Integer getMetodoPreenchimento() {
		return metodoPreenchimento;
	}

	public void setMetodoPreenchimento(Integer metodoPreenchimento) {
		this.metodoPreenchimento = metodoPreenchimento;
	}

	public SubAtividadeExtensao getSubAtividade() {
		return subAtividade;
	}

	public void setSubAtividade(SubAtividadeExtensao subAtividade) {
		this.subAtividade = subAtividade;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public List<ModalidadeParticipantePeriodoInscricaoAtividade> getModalidadesParticipantes() {
		return modalidadesParticipantes;
	}

	public void setModalidadesParticipantes(
			List<ModalidadeParticipantePeriodoInscricaoAtividade> modalidadesParticipantes) {
		this.modalidadesParticipantes = modalidadesParticipantes;
	}

	public DataModel getModalidadesParticipantesDataModel() {
		return modalidadesParticipantesDataModel;
	}
	public void setModalidadesParticipantesDataModel(DataModel modalidadesParticipantesDataModel) {
		this.modalidadesParticipantesDataModel = modalidadesParticipantesDataModel;
	}
	public Integer getQuantidadePeriodosInscricoesMiniAtividade() {
		return quantidadePeriodosInscricoesMiniAtividade;
	}
	public void setQuantidadePeriodosInscricoesMiniAtividade(Integer quantidadePeriodosInscricoesMiniAtividade) {
		this.quantidadePeriodosInscricoesMiniAtividade = quantidadePeriodosInscricoesMiniAtividade;
	}

	public boolean isEstouInscrito() {
		return estouInscrito;
	}
	public void setEstouInscrito(boolean estouInscrito) {
		this.estouInscrito = estouInscrito;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}
	
	
}
