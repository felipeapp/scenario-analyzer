/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 13/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;


/**
 * <p>Cont�m os campos que podem ser utilizados como classifica��o bibliograficas segundo o padr�o MARC </p>
 * 
 * <p>Utilizado para poder apartir da informa��o digita em algum campo marc, poder identificar qual o classica��o utilizada nesse campo. </p>
 * 
 * <p><i>Esses campos s�o fixos pois fazem parte do padr�o e provavelmente n�o devem mudar de uma biblioteca para outra, a n�o ser que o padr�o MARC mude.</i></p>
 * 
 * @author jadson
 *
 */
public enum CamposMarcClassificacaoBibliografica {
	
	/** Representa o campo 080$a, que � o campo onde pode ser inserdo as informa��es da cataloga��o */
	_080a(0, "080", 'a'), 
	/** Representa o campo 082$a, que � o campo onde pode ser inserdo as informa��es da cataloga��o */
	_082a(1, "082", 'a'), 
	/** Representa o campo 083$a, que � o campo onde pode ser inserdo as informa��es da cataloga��o */
	_083a(2, "083", 'a'), 
	/** Representa o campo 084$a, que � o campo onde pode ser inserdo as informa��es da cataloga��o */
	_084a(3, "084", 'a'), 
	/** Representa o campo 085$a, que � o campo onde pode ser inserdo as informa��es da cataloga��o */
	_085a(4, "085", 'a'), 
	/** Representa o campo 086$a, que � o campo onde pode ser inserdo as informa��es da cataloga��o */
	_086a(5, "086", 'a'), 
	/** Representa o campo 088$a, que � o campo onde pode ser inserdo as informa��es da cataloga��o */
	_088a(6, "088", 'a');
	
	/** O c�digo usado nos campos marc por padr�o */
	public static final String CODIGO_CAMPOS_MARC = "$";
	
	/** O valor do campo que ser� salvo no banco */
	private int valor;
	
	/** A descri��o do campo */
	private String campo;
	
	/** A descri��o do sub campo */
	private Character subCampo;
	
	/** Construtor padr�o */
	private CamposMarcClassificacaoBibliografica(int valor, String campo, Character subCampo){
		this.valor = valor;
		this.campo = campo;
		this.subCampo = subCampo;
	}

	/** Retorna o valor ordinal da vari�vel, � o valor salvo no banco. */
	public int getValor() { return valor; }
	
	/** Retorna o campo da classifica��o */
	public String getCampo() {
		return campo;
	}
	/** Retorna o sub campo da classifica��o */
	public Character getSubCampo() {
		return subCampo;
	}

	/** Retorna a descri��o para exibi��o*/
	public String getDescricao() { return campo+CODIGO_CAMPOS_MARC+subCampo; }

	
	/** Retorna os campos utilizados para as classifica��es */
	public static String[] getCamposClassificacao(){
		return new String[]{_080a.campo, _082a.campo, _083a.campo, _084a.campo, _085a.campo, _086a.campo, _088a.campo};
	}
	
	/** Retorna os sub campos utilizados para as classifica��es */
	public static Character[] getSubCamposClassificacao(){
		return new Character[]{_080a.subCampo, _082a.subCampo, _083a.subCampo, _084a.subCampo, _085a.subCampo, _086a.subCampo, _088a.subCampo};
	}
	
	
	/** Retorna o campo de classifica��o, a partir do seu valor */
	public static CamposMarcClassificacaoBibliografica getCampoMarcClassificaoByValor(Integer valor) {
		if (valor.equals(_080a.valor)) {
			return _080a;
		} else if (valor.equals(_082a.valor)) {
			return _082a;
		} else if (valor.equals(_083a.valor)) {
			return _083a;
		} else if (valor.equals(_084a.valor)) {
			return _084a;
		} else if (valor.equals(_085a.valor)) {
			return _085a;
		} else if (valor.equals(_086a.valor)) {
			return _086a;
		} else if (valor.equals(_088a.valor)) {
			return _088a;
		} else {
			return null;
		}
	}
	
	
	/** Retorna o campo utilizado na classifi��o marc, a partir da sua descri��o */
	public static CamposMarcClassificacaoBibliografica getCampoMarcClassificacaoByCampoSubCampo(String campo, Character subCampo) { 
		
		if(_080a.getCampo().equalsIgnoreCase(campo) && _080a.getSubCampo().equals(subCampo))
			return _080a;
		if(_082a.getCampo().equalsIgnoreCase(campo) && _080a.getSubCampo().equals(subCampo))
			return _082a;
		if(_083a.getCampo().equalsIgnoreCase(campo) && _080a.getSubCampo().equals(subCampo))
			return _083a;
		if(_084a.getCampo().equalsIgnoreCase(campo) && _080a.getSubCampo().equals(subCampo))
			return _084a;
		if(_085a.getCampo().equalsIgnoreCase(campo) && _080a.getSubCampo().equals(subCampo))
			return _085a;
		if(_086a.getCampo().equalsIgnoreCase(campo) && _080a.getSubCampo().equals(subCampo))
			return _086a;
		if(_088a.getCampo().equalsIgnoreCase(campo) && _080a.getSubCampo().equals(subCampo))
			return _088a;
		
		return null;
	}
	
	/**  M�todo chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
	@Override
	public String toString() {
		return String.valueOf(valor);
	}
	
}
