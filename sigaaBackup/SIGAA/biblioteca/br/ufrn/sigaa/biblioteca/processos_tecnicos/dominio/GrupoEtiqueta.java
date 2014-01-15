/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 13/06/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

/**
*
* <p>Representa a grupos de tags do padrão MARC.</p>
* <p>Utilzidado para organizar os campos MARC na tela de catalogação para o usuário de acordo com o grupo que ele pertence.</p>
* 
* <p> <i> Como essa informação é fixa e não varia de uma biblioteca para outra, não é salvo no banco, fica fixo no código. </i> </p>
* 
* @author jadson
*
*/
public class GrupoEtiqueta implements Cloneable{

	/** 01X-09X: Numbers and Code Fields  (Campos de Numerações e Códigos) */
	public final static GrupoEtiqueta CAMPOS_DE_NUMEROCAO_E_CODIGO = new GrupoEtiqueta(10, 99, "Campos de Numerações e Códigos", TipoCatalogacao.BIBLIOGRAFICA);
	/** 1XX: Main Entry Fields  (Campos de Entrada Principal) */
	public final static GrupoEtiqueta CAMPOS_DE_ENTRADA_PRINCIPAL = new GrupoEtiqueta(100, 199, "Campos de Entrada Principal", TipoCatalogacao.BIBLIOGRAFICA);
	/** 20X-24X: Title and Title-Related Fields (Campos de Título) */
	public final static GrupoEtiqueta CAMPOS_DE_TITULO_E_TITULOS_RELACIONADOS = new GrupoEtiqueta(200, 249, "Campos de Título e Títulos Relacionados", TipoCatalogacao.BIBLIOGRAFICA);
	/** 25X-28X: Edition, Imprint, Etc. Fields (Campos de Edição e Impressão)*/
	public final static GrupoEtiqueta CAMPOS_DE_EDICAO_E_IMPRESSAO = new GrupoEtiqueta(250, 280, "Campos de Edição e Impressão", TipoCatalogacao.BIBLIOGRAFICA);
	/** 3XX: Physical Description, Etc. Fields (Campos de Descrição Física) */
	public final static GrupoEtiqueta CAMPOS_DE_DESCRICAO_FISICA = new GrupoEtiqueta(300, 399, "Campos de Descrição Física", TipoCatalogacao.BIBLIOGRAFICA);
	/** 4XX: Series Statement Fields (Campos de Declaração de Séries )*/
	public final static GrupoEtiqueta CAMPOS_DE_DECLARACAO_DE_SERIES = new GrupoEtiqueta(400, 499, "Campos de Declaração de Séries", TipoCatalogacao.BIBLIOGRAFICA);
	/** 5XX: Note Fields (Campos de Notas ) */
	public final static GrupoEtiqueta CAMPOS_DE_NOTAS = new GrupoEtiqueta(500, 599, "Campos de Notas", TipoCatalogacao.BIBLIOGRAFICA);
	/** 6XX: Subject Access Fields (Campos de Assunto)*/
	public final static GrupoEtiqueta CAMPOS_DE_ASSUNTO = new GrupoEtiqueta(600, 699, "Campos de Assunto", TipoCatalogacao.BIBLIOGRAFICA);
	/** 70X-75X: Added Entry Fields (Campos de Entradas Adicionais) */
	public final static GrupoEtiqueta CAMPOS_DE_ENTRADAS_ADICIONAIS  = new GrupoEtiqueta(700, 759, "Campos de Entradas Adicionais", TipoCatalogacao.BIBLIOGRAFICA);
	/** 76X-78X: Linking Entry Fields (Campos de Ligação) */
	public final static GrupoEtiqueta CAMPOS_DE_LIGACAO = new GrupoEtiqueta(760, 789, "Campos de Ligação", TipoCatalogacao.BIBLIOGRAFICA);
	/** 80X-83X: Series Added Entry Fields (Campos de Sérias Adicionais) */
	public final static GrupoEtiqueta CAMPOS_DE_SERIES_ADICIONAIS = new GrupoEtiqueta(800, 839, "Campos de Séries Adicionais", TipoCatalogacao.BIBLIOGRAFICA);
	/** 841-88X: Holdings, Location, Alternate Graphics, Etc. Fields (Campos de Gerenciamento, Localização e Gráficos Alternativos ) */
	public final static GrupoEtiqueta CAMPOS_DE_GERENCIAMENTO_LOCALIZACAO_GRAFICOS_ALTERNATIVOS = new GrupoEtiqueta (841, 889, "Campos de Gerenciamento, Localização e Gráficos Alternativos", TipoCatalogacao.BIBLIOGRAFICA);

	
	/** 01X-09X: Numbers and Code Fields  (Campos de Numerações e Códigos) */
	public final static GrupoEtiqueta CAMPOS_DE_NUMEROCAO_E_CODIGO_AUTORIDADE = new GrupoEtiqueta(10, 99, "Campos de Numerações e Códigos", TipoCatalogacao.AUTORIDADE);
	/** 1XX, 3XX: Heading Information Fields */
	public final static GrupoEtiqueta CAMPOS_DE_INFORMACAO_DO_TITULO = new GrupoEtiqueta(100, 399, "Campos de Informação do Título", TipoCatalogacao.AUTORIDADE);
	/** 260, 360: Complex Subject Reference Fields */
	public final static GrupoEtiqueta CAMPOS_DE_REFERENCIA_DE_ASSUNTOS_COMPLEXOS = new GrupoEtiqueta(260, 360, "Campos de Referência de Assuntos Complexos", TipoCatalogacao.AUTORIDADE);
	/** 4XX: See From Tracing Fields */
	public final static GrupoEtiqueta CAMPOS_DE_RASTREAMENTO_REMISSIVOS_VER = new GrupoEtiqueta(400, 499, "Campos de Rastreamento Remissivo VER", TipoCatalogacao.AUTORIDADE);
	/** 5XX: See Also From Tracing Fields*/
	public final static GrupoEtiqueta CAMPOS_DE_RASTREAMENTO_REMISSIVOS_VER_TAMBEM = new GrupoEtiqueta(500, 599, "Campos de Rastreamento Remissivo VER TAMBÉM", TipoCatalogacao.AUTORIDADE);
	/** 64X: Series Treatment Fields */
	public final static GrupoEtiqueta CAMPOS_DE_TRATAMENTO_DE_SERIE = new GrupoEtiqueta(640, 662, "Campos de Tratamento de Série", TipoCatalogacao.AUTORIDADE);
	/** 663-666: Complex Name Reference Fields */
	public final static GrupoEtiqueta CAMPOS_DE_REFERENCIA_DE_NOMES_COMPLEXOS = new GrupoEtiqueta(663, 666, "Campos de Referência de Nomes Complexos", TipoCatalogacao.AUTORIDADE);
	/** 667-68X: Note Fields */
	public final static GrupoEtiqueta CAMPOS_DE_NOTAS_AUTORIDADE = new GrupoEtiqueta(667, 689, "Campos de Notas", TipoCatalogacao.AUTORIDADE);
	/** 7XX: Heading Linking Entry Fields */
	public final static GrupoEtiqueta CAMPOS_DE_ENTRADA_DE_LIGACAO_TITULO = new GrupoEtiqueta(700, 799, "Campos de Entrada de Ligação do Título", TipoCatalogacao.AUTORIDADE);
	/** 8XX: Location and Alternate Graphic Fields */
	public final static GrupoEtiqueta CAMPOS_DE_LOCALIZACAO_E_GRAFICOS_ALTERNATIVOS = new GrupoEtiqueta(800, 999, "Campos de Localização e Gráficos Alternativos", TipoCatalogacao.AUTORIDADE);
	
	
	/** O valor da primeira etiqueta do grupo */
	private int valorInicial;
	/** O valor da última etiqueta do grupo */
	private int valorFinal;
	
	/** A descrição do grupo */
	private String descricao;

	/** Se o grupamento é referente ao campo bibliográfico ou autoridades */
	private int tipo;
	
	
	public GrupoEtiqueta(int valorInicial, int valorFinal, String descricao, int tipo) {
		this.valorInicial = valorInicial;
		this.valorFinal = valorFinal;
		this.descricao = descricao;
		this.tipo = tipo;
	}
	
	/** Verifica de acordo com os valores da etiqueta passada se ela pertence a esse grupo */
	public boolean isGrupoEtiqueta(String tagEtiqueta, int tipoEtiqueta){
		Integer valorTag = Integer.parseInt(tagEtiqueta);
		
		// suporta o caso de uso de autoridades que só tem dois valores 260 e 360 //
		if(valorTag == valorInicial || valorTag == valorFinal && tipo == tipoEtiqueta)
			return true;
			
		if(valorTag >= valorInicial && valorTag <= valorFinal && tipo == tipoEtiqueta)
			return true;
	
		return false;
	}
	

	public String getDescricao() {
		return descricao;
	}
	

	@Override
	public String toString() {
		return "GrupoEtiqueta [descricao=" + descricao + "]"+" de "+valorInicial+" a "+valorFinal+" tipo: "+tipo;
	}

	
	/** Retorna uma novo objeto de grupo de etiqueta clone desse. */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new GrupoEtiqueta(valorInicial, valorFinal, descricao, tipo);
	}


	/** Um grupo de etiquetas é igual ao outro, se possui todas as suas informações iguais, menos o campo {@link #visivel} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + tipo;
		result = prime * result + valorFinal;
		result = prime * result + valorInicial;
		return result;
	}

	/** Um grupo de etiquetas é igual ao outro, se possui todas as suas informações iguais, menos o campo {@link #visivel} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrupoEtiqueta other = (GrupoEtiqueta) obj;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (tipo != other.tipo)
			return false;
		if (valorFinal != other.valorFinal)
			return false;
		if (valorInicial != other.valorInicial)
			return false;
		return true;
	}




	/** Grupos de etiquetas bibliograficas*/
	public static final GrupoEtiqueta[] GRUPOS_ETIQUETAS_BIBLIOGRAFICOS = new GrupoEtiqueta[]{
		  GrupoEtiqueta.CAMPOS_DE_NUMEROCAO_E_CODIGO
		, GrupoEtiqueta.CAMPOS_DE_ENTRADA_PRINCIPAL
		, GrupoEtiqueta.CAMPOS_DE_TITULO_E_TITULOS_RELACIONADOS
		, GrupoEtiqueta.CAMPOS_DE_EDICAO_E_IMPRESSAO
		, GrupoEtiqueta.CAMPOS_DE_DESCRICAO_FISICA
		, GrupoEtiqueta.CAMPOS_DE_DECLARACAO_DE_SERIES
		, GrupoEtiqueta.CAMPOS_DE_NOTAS
		, GrupoEtiqueta.CAMPOS_DE_ASSUNTO
		, GrupoEtiqueta.CAMPOS_DE_ENTRADAS_ADICIONAIS
		, GrupoEtiqueta.CAMPOS_DE_LIGACAO
		, GrupoEtiqueta.CAMPOS_DE_SERIES_ADICIONAIS
		, GrupoEtiqueta.CAMPOS_DE_GERENCIAMENTO_LOCALIZACAO_GRAFICOS_ALTERNATIVOS};
	
	
	/** Grupos de etiquetas de autoridades*/
	public static final GrupoEtiqueta[] GRUPOS_ETIQUETAS_AUTORIDADES = new GrupoEtiqueta[]{
		 CAMPOS_DE_NUMEROCAO_E_CODIGO_AUTORIDADE
		,CAMPOS_DE_INFORMACAO_DO_TITULO
		,CAMPOS_DE_REFERENCIA_DE_ASSUNTOS_COMPLEXOS
		,CAMPOS_DE_RASTREAMENTO_REMISSIVOS_VER
		,CAMPOS_DE_RASTREAMENTO_REMISSIVOS_VER_TAMBEM
		,CAMPOS_DE_TRATAMENTO_DE_SERIE
		,CAMPOS_DE_REFERENCIA_DE_NOMES_COMPLEXOS
		,CAMPOS_DE_NOTAS_AUTORIDADE
		,CAMPOS_DE_ENTRADA_DE_LIGACAO_TITULO
		,CAMPOS_DE_LOCALIZACAO_E_GRAFICOS_ALTERNATIVOS};
	

}