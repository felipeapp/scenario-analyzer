package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Representa os relatórios do monitor (final e parcial). Vale lembrar que o
 * relatório de desistência e o relatório final tem as mesmas informações.
 * </p>
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
/**
 * @author ilueny
 * 
 */
@Entity
@Table(name = "relatorio_monitor", schema = "monitoria")
public class RelatorioMonitor implements Validatable {

	// Fields

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_relatorio_monitor")
	private int id;

	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	private Date dataCadastro;

	@Column(name = "oportunidade_conhecer_projeto")
	private Integer oportunidadeConhecerProjeto = new Integer(0);

	@Column(name = "atividades_desenvolvidas")
	private String atividadesDesenvolvidas;

	@Column(name = "coerencia_atividades")
	private Integer coerenciaAtividades = new Integer(0);

	@Column(name = "coerencia_atividades_justificativa")
	private String coerenciaAtividadesJustificativa;

	@Column(name = "avaliacao_orientacoes")
	private String avaliacaoOrientacoes;

	@Column(name = "avaliacao_participacao")
	private Integer avaliacaoParticipacao = new Integer(0);

	@Column(name = "avaliacao_participacao_justificativa")
	private String avaliacaoParticipacaoJustificativa;

	@Column(name = "contribuicao_programa")
	private String contribuicaoPrograma;

	@Column(name = "pontos_fortes_desempenho")
	private String pontosFortesDesempenho;

	@Column(name = "pontos_fracos_desempenho")
	private String pontosFracosDesempenho;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_relatorio_monitoria")
	private TipoRelatorioMonitoria tipoRelatorio = new TipoRelatorioMonitoria();

	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_monitoria")
	private DiscenteMonitoria discenteMonitoria = new DiscenteMonitoria();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_relatorio")
	private StatusRelatorio status = new StatusRelatorio();

	@Column(name = "ativo")
	private boolean ativo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;

	@ManyToOne
	@JoinColumn(name = "id_registro_entrada_devolucao")
	private RegistroEntrada registroEntradaDevolucaoReedicao;

	
	// ----- dados da validação do relatório de desligamento----//

	@Column(name = "coordenacao_validou_desligamento")
	private Boolean coordenacaoValidouDesligamento;

	@Column(name = "observacao_coordenacao_desligamento")
	private String observacaoCoordenacaoDesligamento;

	@ManyToOne
	@JoinColumn(name = "id_registro_entrada_coordenacao")
	private RegistroEntrada registroValidacaoCoordenacaoDesligamento;

	@Column(name = "prograd_validou_desligamento")
	private Boolean progradValidouDesligamento;

	@Column(name = "observacao_prograd_desligamento")
	private String observacaoProgradDesligamento;

	@ManyToOne
	@JoinColumn(name = "id_registro_entrada_prograd")
	private RegistroEntrada registroValidacaoProgradDesligamento;

	
	// Constructors

	public RegistroEntrada getRegistroEntradaDevolucaoReedicao() {
		return registroEntradaDevolucaoReedicao;
	}

	public void setRegistroEntradaDevolucaoReedicao(
			RegistroEntrada registroEntradaDevolucaoReedicao) {
		this.registroEntradaDevolucaoReedicao = registroEntradaDevolucaoReedicao;
	}

	/** default constructor */
	public RelatorioMonitor() {
	}

	/** minimal constructor */
	public RelatorioMonitor(int idRelatorioFinalMonitor) {
		this.id = idRelatorioFinalMonitor;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int idRelatorioFinalMonitor) {
		this.id = idRelatorioFinalMonitor;
	}

	public String getPontosFortesDesempenho() {
		return pontosFortesDesempenho;
	}

	public void setPontosFortesDesempenho(String pontosFortesDesempenho) {
		this.pontosFortesDesempenho = pontosFortesDesempenho;
	}

	public String getPontosFracosDesempenho() {
		return pontosFracosDesempenho;
	}

	public void setPontosFracosDesempenho(String pontosFracosDesempenho) {
		this.pontosFracosDesempenho = pontosFracosDesempenho;
	}

	public DiscenteMonitoria getDiscenteMonitoria() {
		return discenteMonitoria;
	}

	public void setDiscenteMonitoria(DiscenteMonitoria discenteMonitoria) {
		this.discenteMonitoria = discenteMonitoria;
	}

	public String getAtividadesDesenvolvidas() {
		return atividadesDesenvolvidas;
	}

	public void setAtividadesDesenvolvidas(String atividadesDesenvolvidas) {
		this.atividadesDesenvolvidas = atividadesDesenvolvidas;
	}

	public String getAvaliacaoOrientacoes() {
		return avaliacaoOrientacoes;
	}

	public void setAvaliacaoOrientacoes(String avaliacaoOrientacoes) {
		this.avaliacaoOrientacoes = avaliacaoOrientacoes;
	}

	public Integer getAvaliacaoParticipacao() {
		return avaliacaoParticipacao;
	}

	public void setAvaliacaoParticipacao(Integer avaliacaoParticipacao) {
		this.avaliacaoParticipacao = avaliacaoParticipacao;
	}

	public String getAvaliacaoParticipacaoJustificativa() {
		return avaliacaoParticipacaoJustificativa;
	}

	public void setAvaliacaoParticipacaoJustificativa(
			String avaliacaoParticipacaoJustificativa) {
		this.avaliacaoParticipacaoJustificativa = avaliacaoParticipacaoJustificativa;
	}

	public Integer getCoerenciaAtividades() {
		return coerenciaAtividades;
	}

	public void setCoerenciaAtividades(Integer coerenciaAtividades) {
		this.coerenciaAtividades = coerenciaAtividades;
	}

	public String getCoerenciaAtividadesJustificativa() {
		return coerenciaAtividadesJustificativa;
	}

	public void setCoerenciaAtividadesJustificativa(
			String coerenciaAtividadesJustificativa) {
		this.coerenciaAtividadesJustificativa = coerenciaAtividadesJustificativa;
	}

	public String getContribuicaoPrograma() {
		return contribuicaoPrograma;
	}

	public void setContribuicaoPrograma(String contribuicaoPrograma) {
		this.contribuicaoPrograma = contribuicaoPrograma;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getOportunidadeConhecerProjeto() {
		return oportunidadeConhecerProjeto;
	}

	public void setOportunidadeConhecerProjeto(
			Integer oportunidadeConhecerProjeto) {
		this.oportunidadeConhecerProjeto = oportunidadeConhecerProjeto;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(oportunidadeConhecerProjeto, "Item 1",	lista);
		ValidatorUtil.validateRequired(atividadesDesenvolvidas, "Item 2", lista);
		ValidatorUtil.validateRequired(coerenciaAtividades, "Item 3", lista);
		ValidatorUtil.validateRequired(coerenciaAtividadesJustificativa, "Item 3.1", lista);
		ValidatorUtil.validateRequired(avaliacaoOrientacoes, "Item 4", lista);
		ValidatorUtil.validateRequired(avaliacaoParticipacao, "Item 5", lista);
		ValidatorUtil.validateRequired(avaliacaoParticipacaoJustificativa, "Item 5.1", lista);
		ValidatorUtil.validateRequired(contribuicaoPrograma, "Item 6", lista);
		ValidatorUtil.validateRequired(pontosFortesDesempenho, "Item 7.1", lista);
		ValidatorUtil.validateRequired(pontosFracosDesempenho, "Item 7.2", lista);
		ValidatorUtil.validateRequiredId(tipoRelatorio.getId(),	"Informa o Tipo de Relatório", lista);
		return lista;
	}

	public TipoRelatorioMonitoria getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorioMonitoria tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public StatusRelatorio getStatus() {
		return status;
	}

	public void setStatus(StatusRelatorio status) {
		this.status = status;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	/**
	 * Só pode devolver o relatório enquanto ele não for distribuído para
	 * avaliação pela comissão de monitoria
	 * 
	 * @return
	 */
	public boolean isPermitidoDevolverMonitor() {
		if ((getStatus() != null) && (getStatus().getId() != 0)) {
			return (((getStatus().getId() == StatusRelatorio.AGUARDANDO_AVALIACAO) 
				|| (getStatus().getId() == StatusRelatorio.AGUARDANDO_DISTRIBUICAO)) 
				&& (getDataEnvio() != null));
		}
		return false;
	}

	/**
	 * Só pode alterar o relatório enquanto ele não for enviado para prograd
	 * 
	 * @return
	 */
	public boolean isEnviado() {
		return getDataEnvio() != null;
	}

	public Boolean getCoordenacaoValidouDesligamento() {
		return coordenacaoValidouDesligamento;
	}

	public void setCoordenacaoValidouDesligamento(
			Boolean coordenacaoValidouDesligamento) {
		this.coordenacaoValidouDesligamento = coordenacaoValidouDesligamento;
	}

	public String getObservacaoCoordenacaoDesligamento() {
		return observacaoCoordenacaoDesligamento;
	}

	public void setObservacaoCoordenacaoDesligamento(
			String observacaoCoordenacaoDesligamento) {
		this.observacaoCoordenacaoDesligamento = observacaoCoordenacaoDesligamento;
	}

	public Boolean getProgradValidouDesligamento() {
		return progradValidouDesligamento;
	}

	public void setProgradValidouDesligamento(Boolean progradValidouDesligamento) {
		this.progradValidouDesligamento = progradValidouDesligamento;
	}

	public String getObservacaoProgradDesligamento() {
		return observacaoProgradDesligamento;
	}

	public void setObservacaoProgradDesligamento(
			String observacaoProgradDesligamento) {
		this.observacaoProgradDesligamento = observacaoProgradDesligamento;
	}

	public RegistroEntrada getRegistroValidacaoCoordenacaoDesligamento() {
		return registroValidacaoCoordenacaoDesligamento;
	}

	public void setRegistroValidacaoCoordenacaoDesligamento(
			RegistroEntrada registroValidacaoCoordenacaoDesligamento) {
		this.registroValidacaoCoordenacaoDesligamento = registroValidacaoCoordenacaoDesligamento;
	}

	public RegistroEntrada getRegistroValidacaoProgradDesligamento() {
		return registroValidacaoProgradDesligamento;
	}

	public void setRegistroValidacaoProgradDesligamento(
			RegistroEntrada registroValidacaoProgradDesligamento) {
		this.registroValidacaoProgradDesligamento = registroValidacaoProgradDesligamento;
	}

	/**
	 * Retorna true se o relatório de desligamento estiver passível de avaliação
	 * pela prograd
	 * 
	 * @return
	 */
	public boolean isPermitidoValidacaoPrograd() {

		return (isPermitidoDevolverMonitor() 
				&& (getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR)
				&& (getCoordenacaoValidouDesligamento() != null) 
				&& (getStatus().getId() == StatusRelatorio.AGUARDANDO_AVALIACAO));
		
	}
	
	/**
	 * Retorna true se o relatório de desligamento estiver passível de avaliação
	 * pela coordenação do projeto
	 * 
	 * @return
	 */
	public boolean isPermitidoValidacaoCoordenacao() {

		return (isPermitidoDevolverMonitor() 
				&& (getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR)
				&& (getProgradValidouDesligamento() == null) && (getCoordenacaoValidouDesligamento() == null));
		
	}
	
	/**
	 * Retorna o projeto de monitoria do qual o relatório faz parte
	 * 
	 * @return
	 */
	public ProjetoEnsino getProjetoEnsino() {
		if (discenteMonitoria != null) {
		    return discenteMonitoria.getProjetoEnsino();
		}else
		    return null;
	}
	
	/**
	 * Informa se o relatório do atual é um relatório de desligamento de monitor.
	 * @return
	 */
	public boolean isRelatorioDesligamento() {
	    return getTipoRelatorio().getId() != TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR;
	}

}
