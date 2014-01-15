/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 13/06/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

/**
*
* <p>Representa a grupos de tags do padr�o MARC.</p>
* <p>Utilzidado para organizar os campos MARC na tela de cataloga��o para o usu�rio de acordo com o grupo que ele pertence.</p>
* 
* <p> <i> Como essa informa��o � fixa e n�o varia de uma biblioteca para outra, n�o � salvo no banco, fica fixo no c�digo. </i> </p>
* 
* @author jadson
*
*/
public class GrupoEtiqueta implements Cloneable{

	/** 01X-09X: Numbers and Code Fields  (Campos de Numera��es e C�digos) */
	public final static GrupoEtiqueta CAMPOS_DE_NUMEROCAO_E_CODIGO = new GrupoEtiqueta(10, 99, "Campos de Numera��es e C�digos", TipoCatalogacao.BIBLIOGRAFICA);
	/** 1XX: Main Entry Fields  (Campos de Entrada Principal) */
	public final static GrupoEtiqueta CAMPOS_DE_ENTRADA_PRINCIPAL = new GrupoEtiqueta(100, 199, "Campos de Entrada Principal", TipoCatalogacao.BIBLIOGRAFICA);
	/** 20X-24X: Title and Title-Related Fields (Campos de T�tulo) */
	public final static GrupoEtiqueta CAMPOS_DE_TITULO_E_TITULOS_RELACIONADOS = new GrupoEtiqueta(200, 249, "Campos de T�tulo e T�tulos Relacionados", TipoCatalogacao.BIBLIOGRAFICA);
	/** 25X-28X: Edition, Imprint, Etc. Fields (Campos de Edi��o e Impress�o)*/
	public final static GrupoEtiqueta CAMPOS_DE_EDICAO_E_IMPRESSAO = new GrupoEtiqueta(250, 280, "Campos de Edi��o e Impress�o", TipoCatalogacao.BIBLIOGRAFICA);
	/** 3XX: Physical Description, Etc. Fields (Campos de Descri��o F�sica) */
	public final static GrupoEtiqueta CAMPOS_DE_DESCRICAO_FISICA = new GrupoEtiqueta(300, 399, "Campos de Descri��o F�sica", TipoCatalogacao.BIBLIOGRAFICA);
	/** 4XX: Series Statement Fields (Campos de Declara��o de S�ries )*/
	public final static GrupoEtiqueta CAMPOS_DE_DECLARACAO_DE_SERIES = new GrupoEtiqueta(400, 499, "Campos de Declara��o de S�ries", TipoCatalogacao.BIBLIOGRAFICA);
	/** 5XX: Note Fields (Campos de Notas ) */
	public final static GrupoEtiqueta CAMPOS_DE_NOTAS = new GrupoEtiqueta(500, 599, "Campos de Notas", TipoCatalogacao.BIBLIOGRAFICA);
	/** 6XX: Subject Access Fields (Campos de Assunto)*/
	public final static GrupoEtiqueta CAMPOS_DE_ASSUNTO = new GrupoEtiqueta(600, 699, "Campos de Assunto", TipoCatalogacao.BIBLIOGRAFICA);
	/** 70X-75X: Added Entry Fields (Campos de Entradas Adicionais) */
	public final static GrupoEtiqueta CAMPOS_DE_ENTRADAS_ADICIONAIS  = new GrupoEtiqueta(700, 759, "Campos de Entradas Adicionais", TipoCatalogacao.BIBLIOGRAFICA);
	/** 76X-78X: Linking Entry Fields (Campos de Liga��o) */
	public final static GrupoEtiqueta CAMPOS_DE_LIGACAO = new GrupoEtiqueta(760, 789, "Campos de Liga��o", TipoCatalogacao.BIBLIOGRAFICA);
	/** 80X-83X: Series Added Entry Fields (Campos de S�rias Adicionais) */
	public final static GrupoEtiqueta CAMPOS_DE_SERIES_ADICIONAIS = new GrupoEtiqueta(800, 839, "Campos de S�ries Adicionais", TipoCatalogacao.BIBLIOGRAFICA);
	/** 841-88X: Holdings, Location, Alternate Graphics, Etc. Fields (Campos de Gerenciamento, Localiza��o e Gr�ficos Alternativos ) */
	public final static GrupoEtiqueta CAMPOS_DE_GERENCIAMENTO_LOCALIZACAO_GRAFICOS_ALTERNATIVOS = new GrupoEtiqueta (841, 889, "Campos de Gerenciamento, Localiza��o e Gr�ficos Alternativos", TipoCatalogacao.BIBLIOGRAFICA);

	
	/** 01X-09X: Numbers and Code Fields  (Campos de Numera��es e C�digos) */
	public final static GrupoEtiqueta CAMPOS_DE_NUMEROCAO_E_CODIGO_AUTORIDADE = new GrupoEtiqueta(10, 99, "Campos de Numera��es e C�digos", TipoCatalogacao.AUTORIDADE);
	/** 1XX, 3XX: Heading Information Fields */
	public final static GrupoEtiqueta CAMPOS_DE_INFORMACAO_DO_TITULO = new GrupoEtiqueta(100, 399, "Campos de Informa��o do T�tulo", TipoCatalogacao.AUTORIDADE);
	/** 260, 360: Complex Subject Reference Fields */
	public final static GrupoEtiqueta CAMPOS_DE_REFERENCIA_DE_ASSUNTOS_COMPLEXOS = new GrupoEtiqueta(260, 360, "Campos de Refer�ncia de Assuntos Complexos", TipoCatalogacao.AUTORIDADE);
	/** 4XX: See From Tracing Fields */
	public final static GrupoEtiqueta CAMPOS_DE_RASTREAMENTO_REMISSIVOS_VER = new GrupoEtiqueta(400, 499, "Campos de Rastreamento Remissivo VER", TipoCatalogacao.AUTORIDADE);
	/** 5XX: See Also From Tracing Fields*/
	public final static GrupoEtiqueta CAMPOS_DE_RASTREAMENTO_REMISSIVOS_VER_TAMBEM = new GrupoEtiqueta(500, 599, "Campos de Rastreamento Remissivo VER TAMB�M", TipoCatalogacao.AUTORIDADE);
	/** 64X: Series Treatment Fields */
	public final static GrupoEtiqueta CAMPOS_DE_TRATAMENTO_DE_SERIE = new GrupoEtiqueta(640, 662, "Campos de Tratamento de S�rie", TipoCatalogacao.AUTORIDADE);
	/** 663-666: Complex Name Reference Fields */
	public final static GrupoEtiqueta CAMPOS_DE_REFERENCIA_DE_NOMES_COMPLEXOS = new GrupoEtiqueta(663, 666, "Campos de Refer�ncia de Nomes Complexos", TipoCatalogacao.AUTORIDADE);
	/** 667-68X: Note Fields */
	public final static GrupoEtiqueta CAMPOS_DE_NOTAS_AUTORIDADE = new GrupoEtiqueta(667, 689, "Campos de Notas", TipoCatalogacao.AUTORIDADE);
	/** 7XX: Heading Linking Entry Fields */
	public final static GrupoEtiqueta CAMPOS_DE_ENTRADA_DE_LIGACAO_TITULO = new GrupoEtiqueta(700, 799, "Campos de Entrada de Liga��o do T�tulo", TipoCatalogacao.AUTORIDADE);
	/** 8XX: Location and Alternate Graphic Fields */
	public final static GrupoEtiqueta CAMPOS_DE_LOCALIZACAO_E_GRAFICOS_ALTERNATIVOS = new GrupoEtiqueta(800, 999, "Campos de Localiza��o e Gr�ficos Alternativos", TipoCatalogacao.AUTORIDADE);
	
	
	/** O valor da primeira etiqueta do grupo */
	private int valorInicial;
	/** O valor da �ltima etiqueta do grupo */
	private int valorFinal;
	
	/** A descri��o do grupo */
	private String descricao;

	/** Se o grupamento � referente ao campo bibliogr�fico ou autoridades */
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
		
		// suporta o caso de uso de autoridades que s� tem dois valores 260 e 360 //
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


	/** Um grupo de etiquetas � igual ao outro, se possui todas as suas informa��es iguais, menos o campo {@link #visivel} */
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

	/** Um grupo de etiquetas � igual ao outro, se possui todas as suas informa��es iguais, menos o campo {@link #visivel} */
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