/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import br.ufrn.arq.util.StringUtils;

/**
 *
 * <p> Classe que vai guardar temporariamente os dados da tabela cutter sugeridos pelo sistema </p>
 * <p> O usuário vai escolher dentre os sugeridos qual é o cutter real, o sistema já vai trazer um 
 * dos códigos como sendo o código cutter que ele acha o mais correto. </p>
 * <br/>
 * <p> Classe não é persistida, só é utilizada para montar os dados que vão ser mostrados ao usuário
 * na tela na qual ele vai escolher o código cutter a ser usado.</p>
 * 
 * @author jadson
 *
 */
public class DadosTabelaCutter implements Comparable<DadosTabelaCutter>{

	 /** Para identificar o código cutter escolhido */
	private int idTabelaCutter;
	
	/** Caracter inicial do sobre nome em maúsculo que vai no começo do código cutter */
	private Character caracterInicialSobrenome;
	
	/** O código cutter propriamente dito, gerado com base no sobre nome do autor */
	private Integer codigoCutter;
	
	/** O sobre nome do autor, mostrado para o bibliotecário verificar se o cutter está correto */
	private String sobreNomeAutor;
	
	/** Caracter inicial do título em minúsculo que vai no fim do código cutter */
	private Character caracterInicialTitulo;
	
	/** Indica que este foi o código calculado pelo sistema, como sendo o código cutter correto.*/
	private boolean codigoCalculado;
	
	/**
	 * Construtor padrão
	 * 
	 * @param idTabelaCutter
	 * @param caracterInicialSobrenome
	 * @param codigoCutter
	 * @param sobreNomeAutor
	 * @param caracterInicialTitulo
	 * @param codigoCalculado
	 */
	public DadosTabelaCutter(int idTabelaCutter, Character caracterInicialSobrenome, Integer codigoCutter,
			String sobreNomeAutor, Character caracterInicialTitulo) {
		this.idTabelaCutter = idTabelaCutter;
		this.caracterInicialSobrenome = removeAcento(caracterInicialSobrenome);
		this.codigoCutter = codigoCutter;
		this.sobreNomeAutor = sobreNomeAutor;
		this.caracterInicialTitulo = removeAcento(caracterInicialTitulo);
	}
	
	/** Remove o acento do Caractere. 
	 * 
	 * @param character
	 * @return
	 */
	private Character removeAcento(Character character) {
		if (character != null)
			return StringUtils.toAscii(character.toString()).charAt(0);
		else
			return null;
	}
	
	/**
	 *   <p>Retorna o código cutter completo, já formatado como o caracter inicial do Sobre nome em maúsculo no 
	 *   começo e o caracter inicial do título da obra no final em minúsculo.</p>
	 *
	 * @return
	 */
	public String getCodigoCutterCompleto(){
		
		String cutterCompleto = null;
		
		if( caracterInicialSobrenome != null && caracterInicialTitulo != null )
			cutterCompleto = Character.toUpperCase(caracterInicialSobrenome) + "" + codigoCutter + "" + Character.toLowerCase(caracterInicialTitulo);
		else
			if( caracterInicialSobrenome != null && caracterInicialTitulo == null )
				cutterCompleto = Character.toUpperCase(caracterInicialSobrenome) + "" + codigoCutter;
			else
				if( caracterInicialSobrenome == null && caracterInicialTitulo != null )
					cutterCompleto = codigoCutter + "" + Character.toLowerCase(caracterInicialTitulo);
				else
					if( caracterInicialSobrenome == null && caracterInicialTitulo == null )
						cutterCompleto  = ""+codigoCutter;
		
		return cutterCompleto;
	}
	
	

	public int getIdTabelaCutter() {
		return idTabelaCutter;
	}

	public Character getCaracterInicialSobrenome() {
		
		if(caracterInicialSobrenome != null)
			return Character.toUpperCase(caracterInicialSobrenome);
		
		return caracterInicialSobrenome;
	}

	public Integer getCodigoCutter() {
		return codigoCutter;
	}

	public String getSobreNomeAutor() {
		return sobreNomeAutor;
	}

	public Character getCaracterInicialTitulo() {
		if(caracterInicialTitulo != null)
			 return Character.toLowerCase(caracterInicialTitulo);
		
		return caracterInicialTitulo;
	}

	public boolean isCodigoCalculado() {
		return codigoCalculado;
	}

	public void setCodigoCalculado(){
		codigoCalculado = true;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idTabelaCutter;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DadosTabelaCutter other = (DadosTabelaCutter) obj;
		if (idTabelaCutter != other.idTabelaCutter)
			return false;
		return true;
	}


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DadosTabelaCutter o) {
		return new Integer(this.idTabelaCutter).compareTo(o.getIdTabelaCutter());
	}
	
	
	
	
}
