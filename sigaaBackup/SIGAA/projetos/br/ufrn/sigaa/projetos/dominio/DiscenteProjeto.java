package br.ufrn.sigaa.projetos.dominio;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/*******************************************************************************
 * <p>
 * Representa um discente no projeto. O discente ingressa no projeto
 * através de um processo seletivo e é associado a um plano de trabalho.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "projetos", name = "discente_projeto")
public class DiscenteProjeto implements Validatable {

	
	/** Fields */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_discente_projeto", unique = true, nullable = false)
	private int id;
	
	/** Informa qual o discente de fato está relacionado ao projeto. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private Discente discente = new Discente();
	
	/** Projeto do qual o discente faz parte. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto;
	
	/** Data de início do Discente no projeto. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data de fim do Discente no projeto. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;
	
	/** Serve para identificar se o Discente está em uso ou não (exclusão lógica). */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo = true;

	/** Registra o registro de entrada do usuário no caso de uso.*/
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")    	
	private RegistroEntrada registroEntrada;
	
	/** Informa o tipo de vinculo do discente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_vinculo_discente")    	
	private TipoVinculoDiscente tipoVinculo;
	
	/**Justificativa para a escolha na seleção para o plano de trabalho.*/ 
	@Column(name = "justificativa_selecao")
	private String justificativa;	
	
	/** Plano de trabalho no qual o discente faz parte. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plano_trabalho_projeto")
	private PlanoTrabalhoProjeto planoTrabalhoProjeto;

	/** Dados bancários do discente. São informados depois da validação da seleção */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_banco")
	private Banco banco = null;

	/** Número da agência bancária do discente de extensão. */
	@Column(name = "agencia")
	private String agencia;

	/** Número da conta bancária do discente de extensão. */
	@Column(name = "conta")
	private String conta;
	
	/** Número da operação da conta bancária do discente. */
	@Column(name = "operacao")
	private String operacao;
	
	/** Situação do discente de extensão. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_projeto")
	private TipoSituacaoDiscenteProjeto situacaoDiscenteProjeto = new TipoSituacaoDiscenteProjeto();
	
	/** Discente Projeto que foi substituído no plano de trabalho. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_projeto_anterior")
	private DiscenteProjeto discenteProjetoAnterior;
	
	/** Descrição textual informando o motivo da substituição do discente no plano de trabalho. */
	@Column(name = "motivo_substituicao")
	private String motivoSubstituicao;
	
	public DiscenteProjeto(int id) {
		this.id = id;
	}
	
	public DiscenteProjeto() {
	}

	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public PlanoTrabalhoProjeto getPlanoTrabalhoProjeto() {
		return planoTrabalhoProjeto;
	}

	public void setPlanoTrabalhoProjeto(PlanoTrabalhoProjeto planoTrabalhoProjeto) {
		this.planoTrabalhoProjeto = planoTrabalhoProjeto;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public TipoSituacaoDiscenteProjeto getSituacaoDiscenteProjeto() {
		return situacaoDiscenteProjeto;
	}

	public void setSituacaoDiscenteProjeto(
			TipoSituacaoDiscenteProjeto situacaoDiscenteProjeto) {
		this.situacaoDiscenteProjeto = situacaoDiscenteProjeto;
	}

	public DiscenteProjeto getDiscenteProjetoAnterior() {
		return discenteProjetoAnterior;
	}

	public void setDiscenteProjetoAnterior(DiscenteProjeto discenteProjetoAnterior) {
		this.discenteProjetoAnterior = discenteProjetoAnterior;
	}

	public String getMotivoSubstituicao() {
		return motivoSubstituicao;
	}

	public void setMotivoSubstituicao(String motivoSubstituicao) {
		this.motivoSubstituicao = motivoSubstituicao;
	}

}
