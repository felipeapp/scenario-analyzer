/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 30/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.dominio;



/**
 *
 * <p>Campo de ordenação das buscas de assinaturas de periódicos no sistema.</p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public enum CampoOrdenacaoConsultaAssinatura {

	/** Campo para ordenação */
	CODIGO(0, "Código", " a.codigo "), 
	/** Campo para ordenação */
	TITULO(1, "Título", " a.titulo "), 
	/** Campo para ordenação */
	DATA_INICIO(2, "Data Início", " a.dataInicioAssinatura "),
	/** Campo para ordenação */
	DATA_TERMINO(3, "Data Término", " a.dataTerminoAssinatura "),
	/** Campo para ordenação */
	PERIDICIDADE(4, "Periodicidade", " frequenciaPeriodicos.descricao "),
	/** Campo para ordenação */
	MODALIDADE(5, "Modalidade de Aquisição", " a.modalidadeAquisicao "),
	/** Campo para ordenação */
	INTERNACIONALIZACAO(6, "Internacionalização", " a.internacional ");
	
	
	private CampoOrdenacaoConsultaAssinatura(int valor, String descricao, String campoOrdenacao){
		this.valor = valor;
		this.descricao = descricao;
		this.campoOrdenacao = campoOrdenacao;
	}
	
	/** O valor que representa o campo de ordenação */
	private int valor;
	
	/** A descrição do campo mostrada aos usuários */
	private String descricao;
	
	/** A coluna no banco pela qual os resultados vão ser ordenados */
	private String campoOrdenacao;
	
	
	/**
	 * Retorna o campo correspondente ao valor passado. Utilizando onde não é possível passar um enum
	 *  e depois é necessário recuperar o enum a apartir do seu valor.
	 *
	 * @param valorVinculoSelecionado
	 * @return
	 */
	public static CampoOrdenacaoConsultaAssinatura getCampoOrdenacao(final Integer valorCampo) {
		
		if(valorCampo == CODIGO.getValor()){return CODIGO; }
		if(valorCampo == TITULO.getValor()){return TITULO; }
		if(valorCampo == DATA_INICIO.getValor()){ return DATA_INICIO; }
		if(valorCampo == DATA_TERMINO.getValor()){ return DATA_TERMINO; }
		
		if(valorCampo == PERIDICIDADE.getValor()){return PERIDICIDADE; }
		if(valorCampo == MODALIDADE.getValor()){return MODALIDADE; }
		if(valorCampo == INTERNACIONALIZACAO.getValor()){ return INTERNACIONALIZACAO; }
		
		return null; // nunca era para chegar aqui.
	}
	
	
	/** Array que contém os campos utilizados na ordenação na busca padrão da assinatura */
	public static final CampoOrdenacaoConsultaAssinatura[] CAMPOS_ORDENACAO_BUSCA_PADRAO_ASSINATURA = {CODIGO, TITULO, DATA_INICIO, DATA_TERMINO, PERIDICIDADE, MODALIDADE, INTERNACIONALIZACAO };
	
	public int getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getCampoOrdenacao() {
		return campoOrdenacao;
	}
}
