/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos da ajuda de custo.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class PassagemDTO extends OrcamentoItemDTO{

	/** Representa a finalidade da passagem..*/
	private String finalidade;

	/** Representa o identificador do tipo da passagem.*/
	private Integer idTipoPassagem;

	/** Representa o tipo da passagem.*/
	private String tipoPassagem;

	/** Indica se a passagem é internacional. */
	private Boolean internacional;

	/** Representa o identificador da localidade de destino.*/
	private Integer idLocalidadeDestino;

	/** Representa a localidade de destino.*/
	private String localidadeDestino;

	/** Representa o identificador do estado de destino.*/
	private Integer idEstadoDestino;

	/** Representa o favorecido.*/
	private String favorecido;

	/** Representa o estado de destino.*/
	private String estadoDestino;

	/** Representa o identificador da localidade de origem.*/
	private Integer idLocalidadeOrigem;

	/** Representa o identificador do estado de origem.*/
	private Integer idEstadoOrigem;

	/** Representa a localidade de origem.*/
	private String localidadeOrigem;

	/** Representa o estado de origem.*/
	private String estadoOrigem;
	
	/** Representa a quantidade de passagens.*/
	private Integer quantidade;
	
	private Double valorUnitario;
	
	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}

	public String getFinalidade() {
		return finalidade;
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

	public void setInternacional(Boolean internacional) {
		this.internacional = internacional;
	}

	public Boolean getInternacional() {
		return internacional;
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

	public void setFavorecido(String favorecido) {
		this.favorecido = favorecido;
	}

	public String getFavorecido() {
		return favorecido;
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

	public void setIdEstadoOrigem(Integer idEstadoOrigem) {
		this.idEstadoOrigem = idEstadoOrigem;
	}

	public Integer getIdEstadoOrigem() {
		return idEstadoOrigem;
	}

	public void setLocalidadeOrigem(String localidadeOrigem) {
		this.localidadeOrigem = localidadeOrigem;
	}

	public String getLocalidadeOrigem() {
		return localidadeOrigem;
	}

	public void setEstadoOrigem(String estadoOrigem) {
		this.estadoOrigem = estadoOrigem;
	}

	public String getEstadoOrigem() {
		return estadoOrigem;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
	
}
