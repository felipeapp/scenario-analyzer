/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 04/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio;


/**
 *
 * <p>Enum que representa os campos que o usu�rio pode ordenar os resultados na pesquisa do acervo </p>
 *
 * 
 * @author jadson
 *
 */
public enum CampoOrdenacaoConsultaAcervo {

	/** Campo para ordena��o */
	NUMERO_DO_SISTEMA(0, "N�mero do Sistema", "numero_do_sistema DESC "), 
	/** Campo para ordena��o */
	TITULO(1, "T�tulo", "titulo"), 
	/** Campo para ordena��o */
	AUTOR(2, "Autor", "autor"),
	/** Campo para ordena��o */
	ASSUNTO(3, "Assunto", "assunto"),
	/** Campo para ordena��o */
	ANO(4, "Ano", "ano DESC "),
	/** Campo para ordena��o */
	EDICAO(5, "Edi��o", "edicao DESC"),
	/** Campo para ordena��o */
	QUANTIDADE_MATERIAIS(6, "Com mais Materiais", "quantidade_materiais_ativos_titulo DESC "),
	/** Campo para ordena��o */
	MAIS_RECENTES(7, "Mais Recentes", "numero_do_sistema DESC "), 
	/** Campo para ordena��o */
	MAIS_BUCADOS(8, "Mais Buscados", "quantidade_vezes_consultado DESC "),
	/** Campo para ordena��o */
	MAIS_VISUALIZADOS(9, "Mais Visualizados", "quantidade_vezes_visualizado DESC "),
	/** Campo para ordena��o */
	MAIS_EMPRESTADOS(10, "Mais Emprestados", "quantidade_vezes_emprestado DESC "),
	/** Campo para ordena��o */
	PALAVRAS_CHAVE(11, "Palavras-Chave", "assunto"),
	/** Campo para ordena��o */
	ENTRADA_AUTORIZADA_AUTOR(12, "Entrada Autorizada Autor", "entrada_autorizada_autor"),
	/** Campo para ordena��o */
	ENTRADA_REMISSIVA_AUTOR(13, "Entrada Remissiva Autor", "nomes_remissivos_autor"),
	/** Campo para ordena��o */
	ENTRADA_AUTORIZADA_ASSUNTO(14, "Entrada Autorizada Assunto", "entrada_autorizada_assunto"),
	/** Campo para ordena��o */
	ENTRADA_REMISSIVA_ASSUNTO(15, "Entrada Remissiva Assunto", "nomes_remissivos_assunto");
	
	
	private CampoOrdenacaoConsultaAcervo(int valor, String descricao, String colunaOrdenacao){
		this.valor = valor;
		this.descricao = descricao;
		this.colunaOrdenacao = colunaOrdenacao;
	}
	
	/** O valor que representa o campo de ordena��o */
	private int valor;
	
	/** A descri��o do v�nculo mostrada aos usu�rios */
	private String descricao;
	
	/** A coluna no banco pela qual os resultados v�o ser ordenados */
	private String colunaOrdenacao;
	
	
	/**
	 * Retorna o campo correspondente ao valor passado. Utilizando onde n�o � poss�vel passar um enum
	 *  e depois � necess�rio recuperar o enum a apartir do seu valor.
	 *
	 * @param valorVinculoSelecionado
	 * @return
	 */
	public static CampoOrdenacaoConsultaAcervo getCampoOrdenacao(final Integer valorCampo) {
		
		if(valorCampo == NUMERO_DO_SISTEMA.getValor()){return NUMERO_DO_SISTEMA; }
		if(valorCampo == TITULO.getValor()){return TITULO; }
		if(valorCampo == AUTOR.getValor()){ return AUTOR; }
		if(valorCampo == PALAVRAS_CHAVE.getValor()){ return PALAVRAS_CHAVE; }
		
		if(valorCampo == ASSUNTO.getValor()){return ASSUNTO; }
		if(valorCampo == ANO.getValor()){return ANO; }
		if(valorCampo == EDICAO.getValor()){ return EDICAO; }
		if(valorCampo == QUANTIDADE_MATERIAIS.getValor()){ return QUANTIDADE_MATERIAIS; }
		if(valorCampo == MAIS_BUCADOS.getValor()){return MAIS_BUCADOS; }
		if(valorCampo == MAIS_VISUALIZADOS.getValor()){return MAIS_VISUALIZADOS; }
		if(valorCampo == MAIS_EMPRESTADOS.getValor()){ return MAIS_EMPRESTADOS; }
		
		if(valorCampo == ENTRADA_AUTORIZADA_AUTOR.getValor()){return ENTRADA_AUTORIZADA_AUTOR; }
		if(valorCampo == ENTRADA_REMISSIVA_AUTOR.getValor()){return ENTRADA_REMISSIVA_AUTOR; }
		if(valorCampo == ENTRADA_AUTORIZADA_ASSUNTO.getValor()){ return ENTRADA_AUTORIZADA_ASSUNTO; }
		if(valorCampo == ENTRADA_REMISSIVA_ASSUNTO.getValor()){ return ENTRADA_REMISSIVA_ASSUNTO; }
		
		return null; // nunca era para chegar aqui.
	}
	
	/** Array que cont�m os campos utilizados na ordena��o de t�tulos nas consultas do acervo */
	public static final CampoOrdenacaoConsultaAcervo[] CAMPOS_ORDENACAO_TITULO = {NUMERO_DO_SISTEMA, TITULO, AUTOR, ASSUNTO, ANO, EDICAO, QUANTIDADE_MATERIAIS, MAIS_BUCADOS, MAIS_VISUALIZADOS, MAIS_EMPRESTADOS };
	
	/** Array que cont�m os campos utilizados na ordena��o de t�tulos nas consultas p�blicas do sistema, onde o n�mero do sistema n�o faz sentido, mas indica os mais redentes */
	public static final CampoOrdenacaoConsultaAcervo[] CAMPOS_ORDENACAO_TITULO_PUBLICO = {TITULO, AUTOR, ASSUNTO, ANO, EDICAO, QUANTIDADE_MATERIAIS, MAIS_RECENTES, MAIS_BUCADOS, MAIS_VISUALIZADOS, MAIS_EMPRESTADOS };
	
	/** Array que cont�m os campos utilizados na ordena��o de artigos nas consultas do acervo do usu�rio */
	public static final CampoOrdenacaoConsultaAcervo[] CAMPOS_ORDENACAO_ARTIGOS_PUBLICO = {TITULO, AUTOR, PALAVRAS_CHAVE, MAIS_RECENTES };
	
	/** Array que cont�m os campos utilizados na ordena��o de artigos nas consultas do acervo dos bibliotec�rios */
	public static final CampoOrdenacaoConsultaAcervo[] CAMPOS_ORDENACAO_ARTIGOS = {NUMERO_DO_SISTEMA, TITULO, AUTOR, PALAVRAS_CHAVE };
	
	/** Array que cont�m os campos utilizados na ordena��o de artigos nas consultas do acervo */
	public static final CampoOrdenacaoConsultaAcervo[] CAMPOS_ORDENACAO_AUTORIDADES = {NUMERO_DO_SISTEMA, ENTRADA_AUTORIZADA_AUTOR, ENTRADA_REMISSIVA_AUTOR, ENTRADA_AUTORIZADA_ASSUNTO, ENTRADA_REMISSIVA_ASSUNTO };
	
	/** Array que cont�m os campos utilizados na ordena��o de artigos nas consultas do acervo p�blica. */
	public static final CampoOrdenacaoConsultaAcervo[] CAMPOS_ORDENACAO_AUTORIDADES_PUBLICO = {ENTRADA_AUTORIZADA_AUTOR, ENTRADA_REMISSIVA_AUTOR, ENTRADA_AUTORIZADA_ASSUNTO, ENTRADA_REMISSIVA_ASSUNTO };
	
	/** Verifica se o campo utilizada na busca de T�tulos tamb�m � utilizado de ordena��o de artigos, porque a busca de artigos tamb�m ocorre na pesquisa de t�tulo do sistema */
	public static boolean  isCampoOrdenacaoArtigo(CampoOrdenacaoConsultaAcervo campoOrdenacaoTitulos){
		return  campoOrdenacaoTitulos == MAIS_RECENTES || campoOrdenacaoTitulos == NUMERO_DO_SISTEMA || campoOrdenacaoTitulos == TITULO || campoOrdenacaoTitulos == AUTOR || campoOrdenacaoTitulos == ASSUNTO;
	}
	
	
	public int getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getColunaOrdenacao() {
		return colunaOrdenacao;
	}



	
	
}
