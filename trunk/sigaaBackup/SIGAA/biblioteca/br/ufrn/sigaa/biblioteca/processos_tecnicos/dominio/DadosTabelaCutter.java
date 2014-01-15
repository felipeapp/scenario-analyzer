/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> O usu�rio vai escolher dentre os sugeridos qual � o cutter real, o sistema j� vai trazer um 
 * dos c�digos como sendo o c�digo cutter que ele acha o mais correto. </p>
 * <br/>
 * <p> Classe n�o � persistida, s� � utilizada para montar os dados que v�o ser mostrados ao usu�rio
 * na tela na qual ele vai escolher o c�digo cutter a ser usado.</p>
 * 
 * @author jadson
 *
 */
public class DadosTabelaCutter implements Comparable<DadosTabelaCutter>{

	 /** Para identificar o c�digo cutter escolhido */
	private int idTabelaCutter;
	
	/** Caracter inicial do sobre nome em ma�sculo que vai no come�o do c�digo cutter */
	private Character caracterInicialSobrenome;
	
	/** O c�digo cutter propriamente dito, gerado com base no sobre nome do autor */
	private Integer codigoCutter;
	
	/** O sobre nome do autor, mostrado para o bibliotec�rio verificar se o cutter est� correto */
	private String sobreNomeAutor;
	
	/** Caracter inicial do t�tulo em min�sculo que vai no fim do c�digo cutter */
	private Character caracterInicialTitulo;
	
	/** Indica que este foi o c�digo calculado pelo sistema, como sendo o c�digo cutter correto.*/
	private boolean codigoCalculado;
	
	/**
	 * Construtor padr�o
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
	 *   <p>Retorna o c�digo cutter completo, j� formatado como o caracter inicial do Sobre nome em ma�sculo no 
	 *   come�o e o caracter inicial do t�tulo da obra no final em min�sculo.</p>
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
