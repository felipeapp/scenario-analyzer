/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data Transfer Object para informações dos detalhamentos dos planos de aplicação.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class PlanilhaOrcamentariaDTO implements Serializable {
	
	/** Descrição da planilha orçamentária. */
	private String descricao;

	/** Grupo da planilha orçamentária. */
	private String grupo;

	/** Membro da planilha orçamentária. */
	private String membro;
	
	/** Representa o cargo.*/
	private String cargo;

	/** Representa a função.*/
	private String funcaoDesempenhada;

	/** Representa a observação.*/
	private String observacao;

	/** Representa a finalidade.*/
	private String finalidade;

	/** Representa o tipo de auxilio.*/
	private String tipoAuxilio;

	/** Representa o favorecido.*/
	private String favorecido;

	/** Representa o tipo da passagem.*/
	private String tipoPassagem;

	/** Representa a localidade de destino.*/
	private String localidadeDestino;

	/** Representa o estado de destino.*/
	private String estadoDestino;

	/** Representa a localidade de origem.*/
	private String localidadeOrigem;

	/** Representa o estado de origem.*/
	private String estadoOrigem;

	/** Representa o tipo do tributo.*/
	private String tipoTributo;

	/** Representa a discriminação.*/
	private String discriminacao;

	/** Representa a descrção do bem.*/
	private String descricaoBem;

	/** Representa a disciplina. */
	private String disciplina;

	/** Indica se é cooperativa.*/
	private Boolean cooperativa;

	/** Indica se planilha orçamentária é iternacional. */
	private Boolean internacional;

	/** Indica se planilha orçamentária é mensal. */
	private Boolean mensal;

	/** Quantidade da planilha orçamentária. */
	private Integer quantidade;

	/** Identificador do grupo da planilha orçamentária. */
	private Integer idGrupo;

	/** Representa a quantidade em meses.*/
	private Integer quantidadeMeses;

	/** Representa a quantidade total.*/
	private Integer quantidadeTotal;

	/** Representa o identificador do tipo de auxilio.*/
	private Integer idTipoAuxilio;

	/** Representa o identificador do tipo da passagem.*/
	private Integer idTipoPassagem;

	/** Representa o identificador da localidade de destino.*/
	private Integer idLocalidadeDestino;

	/** Representa o identificador do estado de destino.*/
	private Integer idEstadoDestino;

	/** Representa o identificador da localidade de origem.*/
	private Integer idLocalidadeOrigem;

	/** Representa o identificador do estado de origem.*/
	private Integer idEstadoOrigem;
	
	/** Representa o identificador do tipo do tributo.*/
	private Integer idTipoTributo;

	/** Representa a quantidade de horas.*/
	private Integer quantHoras;

	/** Representa a quantidade de orientandos.*/
	private Integer quantidadeOrientandos;

	/** Representa o identificador da disciplina. */
	private Integer idDisciplina;

	/** Valor da planilha orçamentária. */
	private Double valor;

	/** Representa o valor da hora.*/
	private Double valorHora;

	/** Representa valor da bolsa.*/
	private Double valorBolsa;
	
	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getMembro() {
		return membro;
	}

	public void setMembro(String membro) {
		this.membro = membro;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setInternacional(Boolean internacional) {
		this.internacional = internacional;
	}

	public Boolean getInternacional() {
		return internacional;
	}

	public void setMensal(Boolean mensal) {
		this.mensal = mensal;
	}

	public Boolean getMensal() {
		return mensal;
	}

	public void setFuncaoDesempenhada(String funcaoDesempenhada) {
		this.funcaoDesempenhada = funcaoDesempenhada;
	}

	public String getFuncaoDesempenhada() {
		return funcaoDesempenhada;
	}

	public void setQuantidadeMeses(Integer quantidadeMeses) {
		this.quantidadeMeses = quantidadeMeses;
	}

	public Integer getQuantidadeMeses() {
		return quantidadeMeses;
	}

	public void setQuantidadeTotal(Integer quantidadeTotal) {
		this.quantidadeTotal = quantidadeTotal;
	}

	public Integer getQuantidadeTotal() {
		return quantidadeTotal;
	}

	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}

	public String getFinalidade() {
		return finalidade;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setIdTipoAuxilio(Integer idTipoAuxilio) {
		this.idTipoAuxilio = idTipoAuxilio;
	}

	public Integer getIdTipoAuxilio() {
		return idTipoAuxilio;
	}

	public void setTipoAuxilio(String tipoAuxilio) {
		this.tipoAuxilio = tipoAuxilio;
	}

	public String getTipoAuxilio() {
		return tipoAuxilio;
	}

	public void setValorHora(Double valorHora) {
		this.valorHora = valorHora;
	}

	public Double getValorHora() {
		return valorHora;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setFavorecido(String favorecido) {
		this.favorecido = favorecido;
	}

	public String getFavorecido() {
		return favorecido;
	}

	public void setIdTipoPassagem(Integer idTipoPassagem) {
		this.idTipoPassagem = idTipoPassagem;
	}

	public Integer getIdTipoPassagem() {
		return idTipoPassagem;
	}

	public void setTipoPassagem(String tipoPassagem) {
		this.tipoPassagem = tipoPassagem;
	}

	public String getTipoPassagem() {
		return tipoPassagem;
	}

	public void setIdLocalidadeDestino(Integer idLocalidadeDestino) {
		this.idLocalidadeDestino = idLocalidadeDestino;
	}

	public Integer getIdLocalidadeDestino() {
		return idLocalidadeDestino;
	}

	public void setLocalidadeDestino(String localidadeDestino) {
		this.localidadeDestino = localidadeDestino;
	}

	public String getLocalidadeDestino() {
		return localidadeDestino;
	}

	public void setIdEstadoDestino(Integer idEstadoDestino) {
		this.idEstadoDestino = idEstadoDestino;
	}

	public Integer getIdEstadoDestino() {
		return idEstadoDestino;
	}

	public void setEstadoDestino(String estadoDestino) {
		this.estadoDestino = estadoDestino;
	}

	public String getEstadoDestino() {
		return estadoDestino;
	}

	public void setIdLocalidadeOrigem(Integer idLocalidadeOrigem) {
		this.idLocalidadeOrigem = idLocalidadeOrigem;
	}

	public Integer getIdLocalidadeOrigem() {
		return idLocalidadeOrigem;
	}

	public void setLocalidadeOrigem(String localidadeOrigem) {
		this.localidadeOrigem = localidadeOrigem;
	}

	public String getLocalidadeOrigem() {
		return localidadeOrigem;
	}

	public void setIdEstadoOrigem(Integer idEstadoOrigem) {
		this.idEstadoOrigem = idEstadoOrigem;
	}

	public Integer getIdEstadoOrigem() {
		return idEstadoOrigem;
	}

	public void setEstadoOrigem(String estadoOrigem) {
		this.estadoOrigem = estadoOrigem;
	}

	public String getEstadoOrigem() {
		return estadoOrigem;
	}

	public void setCooperativa(Boolean cooperativa) {
		this.cooperativa = cooperativa;
	}

	public Boolean getCooperativa() {
		return cooperativa;
	}

	public void setIdTipoTributo(Integer idTipoTributo) {
		this.idTipoTributo = idTipoTributo;
	}

	public Integer getIdTipoTributo() {
		return idTipoTributo;
	}

	public void setTipoTributo(String tipoTributo) {
		this.tipoTributo = tipoTributo;
	}

	public String getTipoTributo() {
		return tipoTributo;
	}

	public void setDiscriminacao(String discriminacao) {
		this.discriminacao = discriminacao;
	}

	public String getDiscriminacao() {
		return discriminacao;
	}

	public void setDescricaoBem(String descricaoBem) {
		this.descricaoBem = descricaoBem;
	}

	public String getDescricaoBem() {
		return descricaoBem;
	}

	public void setQuantHoras(Integer quantHoras) {
		this.quantHoras = quantHoras;
	}

	public Integer getQuantHoras() {
		return quantHoras;
	}

	public void setValorBolsa(Double valorBolsa) {
		this.valorBolsa = valorBolsa;
	}

	public Double getValorBolsa() {
		return valorBolsa;
	}

	public void setQuantidadeOrientandos(Integer quantidadeOrientandos) {
		this.quantidadeOrientandos = quantidadeOrientandos;
	}

	public Integer getQuantidadeOrientandos() {
		return quantidadeOrientandos;
	}

	public void setIdDisciplina(Integer idDisciplina) {
		this.idDisciplina = idDisciplina;
	}

	public Integer getIdDisciplina() {
		return idDisciplina;
	}

	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	public String getDisciplina() {
		return disciplina;
	}
	
	

}
