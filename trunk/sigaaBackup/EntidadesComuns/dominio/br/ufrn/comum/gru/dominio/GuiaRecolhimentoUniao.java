/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.Unidade;

/**
 * Guia de Recolhimento da União - GRU. Classe com atributos comuns à GRU
 * Simples e GRU Cobrança.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity 
@Table(name = "guia_recolhimento_uniao", schema = "gru")
public class GuiaRecolhimentoUniao implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="gru.gru_sequence") })
	@Column(name = "id_gru")
	private int id;
	
	/** Nome do constribuinte, ou seja, de quem está pagando a GRU. */
	@Column(name = "nome_contribuinte")
	private String nomeContribuinte;

	/** Unidade que será favorecida com o recolhimento do valor da GRU. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_unidade_favorecida")
	private Unidade unidadeFavorecida;

	/** Data de vencimento da GRU. */
	private Date vencimento;

	/** Instruções Gerais que constarão no boleto da GRU. */
	private String instrucoes;

	/** Valor da GRU. */
	private Double valor;

	/** Desconto dado ao valor da GRU. */
	@Column(name = "desconto_abatimento")
	private Double descontoAbatimento;

	/** Outras deduções dadas ao pagamento da GRU. */
	@Column(name = "outras_deducoes")
	private Double outrasDeducoes;

	/** Juros de mora/multa por atraso na GRU. */
	@Column(name = "mora_multa")
	private Double moraMulta;

	/** Outros acréscimos incluídos no pagamento da GRU. */
	@Column(name = "outros_acrescimos")
	private Double outrosAcrescimos;

	/** Valor total cobrado na GRU. */
	@Column(name = "valor_total")
	private Double valorTotal;

	/** Código de barras da GRU. */
	@Column(name = "codigo_barras")
	private String codigoBarras;

	/** Linha digitável da GRU. */
	@Column(name = "linha_digitavel")
	private String linhaDigitavel;

	/** Tipo interno de arrecadação da GRU. */
	@ManyToOne
	@JoinColumn(name = "id_tipo_arrecadacao")
	private TipoArrecadacao tipoArrecadacao;

	/** Data de pagamento da GRU. */
	@Column(name = "data_pagamento")
	private Date dataPagamento;

	/**Valor pago pela GRU. */
	@Column(name = "valor_pago")
	private Double valorPago;

	/** Indica se a GRU foi quitada, isto é, paga plenamento. */
	private boolean quitada = false;
	
	/** Indica se a GRU foi arrecadada. */
	private boolean arrecadada = false;
	
	/** CPF do sacado. */
	private long cpf;
	
	/** Número de Referência/Nosso Número da GRU. */
	@Column(name="numero_referencia")         
	private long numeroReferenciaNossoNumero;
	
	/** Configuração da GRU utilizada para a geração desta GRU. */
	@ManyToOne
	@JoinColumn(name = "id_configuracao_gru")
	private ConfiguracaoGRU configuracaoGRU;
	
	/** Tipo de GRU. */
	@Enumerated(EnumType.ORDINAL)
	private TipoGRU tipo = TipoGRU.SIMPLES;
	
	// atributos da GRU Simples
	/** Código do Recolhimento utilizado na GRU Simples. */
	@Column(name = "codigo_recolhimento")
	private String codigoRecolhimento;
	
	/** Mês/Ano de competência da GRU. */
	private String competencia;

	/** Juros/Encargos cobrados na GRU. */
	@Column(name="juros_encargos")
	private Double jurosEncargos;
	
	// atributos da GRU Cobrança
	/** Data criação da GRU. */
	@Column(name="data_processamento")
	@CriadoEm
	private Date dataDocumento;

	/**
	 * Número do documento da GRU, podendo ser referência à um número de
	 * inscrição ou número de processo.
	 */
	@Column(name="numero_documento")
	private int numeroDocumento;

	/** Agência do emitente da GRU. */
	private String agencia;

	/** Código de identificação do cedente da GRU. */
	@Column(name="codigo_cedente")
	private String codigoCedente;

	/** Endereço do sacado. */
	@Column(name="endereco_sacado")
	private String enderecoSacado;
	
	/** Número do convênio com o Banco do Brasil. */
	@Column(name = "convenio")
	private int convenio;
	
	public GuiaRecolhimentoUniao() {
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNomeContribuinte() {
		return nomeContribuinte;
	}
	public void setNomeContribuinte(String nomeContribuinte) {
		this.nomeContribuinte = nomeContribuinte;
	}
	public Unidade getUnidadeFavorecida() {
		return unidadeFavorecida;
	}
	public void setUnidadeFavorecida(Unidade unidadeFavorecida) {
		this.unidadeFavorecida = unidadeFavorecida;
	}
	public Date getVencimento() {
		return vencimento;
	}
	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}
	public String getInstrucoes() {
		return instrucoes;
	}
	public void setInstrucoes(String instrucoes) {
		this.instrucoes = instrucoes;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Double getDescontoAbatimento() {
		return descontoAbatimento;
	}
	public void setDescontoAbatimento(Double descontoAbatimento) {
		this.descontoAbatimento = descontoAbatimento;
	}
	public Double getOutrasDeducoes() {
		return outrasDeducoes;
	}
	public void setOutrasDeducoes(Double outrasDeducoes) {
		this.outrasDeducoes = outrasDeducoes;
	}
	public Double getMoraMulta() {
		return moraMulta;
	}
	public void setMoraMulta(Double moraMulta) {
		this.moraMulta = moraMulta;
	}
	public Double getOutrosAcrescimos() {
		return outrosAcrescimos;
	}
	public void setOutrosAcrescimos(Double outrosAcrescimos) {
		this.outrosAcrescimos = outrosAcrescimos;
	}
	public Double getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}
	public String getCodigoBarras() {
		return codigoBarras;
	}
	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}
	public String getLinhaDigitavel() {
		return linhaDigitavel;
	}
	public void setLinhaDigitavel(String linhaDigitavel) {
		this.linhaDigitavel = linhaDigitavel;
	}
	public TipoArrecadacao getTipoArrecadacao() {
		return tipoArrecadacao;
	}
	public void setTipoArrecadacao(TipoArrecadacao tipoArrecadacao) {
		this.tipoArrecadacao = tipoArrecadacao;
	}
	public Date getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	public Double getValorPago() {
		return valorPago;
	}
	public void setValorPago(Double valorPago) {
		this.valorPago = valorPago;
	}
	public boolean isQuitada() {
		return quitada;
	}
	public void setQuitada(boolean quitada) {
		this.quitada = quitada;
	}

	public long getCpf() {
		return cpf;
	}

	public void setCpf(long cpf) {
		this.cpf = cpf;
	}

	public long getNumeroReferenciaNossoNumero() {
		return numeroReferenciaNossoNumero;
	}

	public void setNumeroReferenciaNossoNumero(long numeroReferenciaNossoNumero) {
		this.numeroReferenciaNossoNumero = numeroReferenciaNossoNumero;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(tipoArrecadacao, "Tipo de Arrecadação", lista);
		validateRequired(valor, "Valor", lista);
		validateRequired(cpf, "CPF", lista);
		validateRequired(nomeContribuinte, "Nome do Contribuinte", lista);
		validateRequired(unidadeFavorecida, "Unidade Favorecida", lista);
		validateRequired(valorTotal, "Valor Total", lista);
		validateRequired(configuracaoGRU, "Configuração da GRU", lista);
		if (tipo == TipoGRU.SIMPLES) {
			validateRequired(codigoRecolhimento, "Código de Recolhimento", lista);
			validateRequired(competencia, "Competência", lista);
			CodigoRecolhimentoGRU codigo = new CodigoRecolhimentoGRU(codigoRecolhimento);
			lista.addAll(codigo.validate());
		} else {
			validateRequired(agencia, "Agência", lista);
			validateRequired(codigoCedente, "Código do Cedente", lista);
			validateRequired(convenio, "Nº do Convênio", lista);
			validateRequired(vencimento, "Vencimento", lista); // GRUs simples podem não ter data de vencimento.
		}
		return lista;
	}

	public ConfiguracaoGRU getConfiguracaoGRU() {
		return configuracaoGRU;
	}

	public void setConfiguracaoGRU(ConfiguracaoGRU configuracaoGRU) {
		this.configuracaoGRU = configuracaoGRU;
	}

	public String getCodigoRecolhimento() {
		return codigoRecolhimento;
	}

	public void setCodigoRecolhimento(String codigoRecolhimento) {
		this.codigoRecolhimento = codigoRecolhimento;
	}

	public String getCompetencia() {
		return competencia;
	}

	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}

	public Double getJurosEncargos() {
		return jurosEncargos;
	}

	public void setJurosEncargos(Double jurosEncargos) {
		this.jurosEncargos = jurosEncargos;
	}

	public Date getDataDocumento() {
		return dataDocumento;
	}

	public void setDataDocumento(Date dataDocumento) {
		this.dataDocumento = dataDocumento;
	}

	public int getNumeroDocumento() {
		if (numeroDocumento == 0)
			return getId();
		else
			return numeroDocumento;
	}

	public void setNumeroDocumento(int numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getCodigoCedente() {
		return codigoCedente;
	}

	public void setCodigoCedente(String codigoCedente) {
		this.codigoCedente = codigoCedente;
	}

	public String getEnderecoSacado() {
		return enderecoSacado;
	}

	public void setEnderecoSacado(String enderecoSacado) {
		this.enderecoSacado = enderecoSacado;
	}

	public TipoGRU getTipo() {
		return tipo;
	}

	public void setTipo(TipoGRU tipo) {
		this.tipo = tipo;
	}

	public boolean isGRUSimples() {
		return tipo == TipoGRU.SIMPLES;
	}
	
	public String getCpfFormatado(){
		return Formatador.getInstance().formatarCPF(cpf);
	}

	public void setArrecadada(boolean arrecadada) {
		this.arrecadada = arrecadada;
	}

	public boolean isArrecadada() {
		return arrecadada;
	}

	public int getConvenio() {
		return convenio;
	}

	public void setConvenio(int convenio) {
		this.convenio = convenio;
	}

}
