/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 30/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.dominio;



/**
 *
 * <p>Campo de ordena��o das buscas de assinaturas de peri�dicos no sistema.</p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public enum CampoOrdenacaoConsultaAssinatura {

	/** Campo para ordena��o */
	CODIGO(0, "C�digo", " a.codigo "), 
	/** Campo para ordena��o */
	TITULO(1, "T�tulo", " a.titulo "), 
	/** Campo para ordena��o */
	DATA_INICIO(2, "Data In�cio", " a.dataInicioAssinatura "),
	/** Campo para ordena��o */
	DATA_TERMINO(3, "Data T�rmino", " a.dataTerminoAssinatura "),
	/** Campo para ordena��o */
	PERIDICIDADE(4, "Periodicidade", " frequenciaPeriodicos.descricao "),
	/** Campo para ordena��o */
	MODALIDADE(5, "Modalidade de Aquisi��o", " a.modalidadeAquisicao "),
	/** Campo para ordena��o */
	INTERNACIONALIZACAO(6, "Internacionaliza��o", " a.internacional ");
	
	
	private CampoOrdenacaoConsultaAssinatura(int valor, String descricao, String campoOrdenacao){
		this.valor = valor;
		this.descricao = descricao;
		this.campoOrdenacao = campoOrdenacao;
	}
	
	/** O valor que representa o campo de ordena��o */
	private int valor;
	
	/** A descri��o do campo mostrada aos usu�rios */
	private String descricao;
	
	/** A coluna no banco pela qual os resultados v�o ser ordenados */
	private String campoOrdenacao;
	
	
	/**
	 * Retorna o campo correspondente ao valor passado. Utilizando onde n�o � poss�vel passar um enum
	 *  e depois � necess�rio recuperar o enum a apartir do seu valor.
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
	
	
	/** Array que cont�m os campos utilizados na ordena��o na busca padr�o da assinatura */
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
