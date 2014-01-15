/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/04/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.Date;

/**
 * <p>Organiza os dados dos <strong>relat�rio de materiais baixados</strong> para serem mostrados na p�gina.</p>
 * 
 * @author Jadson
 * @author Br�ulio
 */
public class DadosRelatorioMateriaisBaixados
		implements Comparable<DadosRelatorioMateriaisBaixados>{

	/** C�digo de barras do material. */
	private String codigoBarras;
	
	/** N�mero de patrim�nio do material. */
	private String numeroPatrimonio;
	
	/** Motivo pelo qual o material foi baixado. */
	private String motivoBaixa;
	
	/** Id da biblioteca onde se encontrava o material. */
	private int idBiblioteca;
	
	/** Nome da biblioteca onde se encontrava o material. */
	private String descricaoBiblioteca;
	
	/** Nome do usu�rio que fez a baixa do material. */
	private String nomeUsuarioBaixa;
	
	/** Data na qual o material foi baixado. */
	private Date dataBaixa;
	
	/** Id do material baixado. */
	private int idMaterial;
	
	/** Classifica��o 1 do material. */
	private String classificacao1;
	
	/** Classifica��o 2 do material. */
	private String classificacao2;
	
	/** Classifica��o 3 do material. */
	private String classificacao3;
	
	/** Cole��o � qual pertencia o material. */
	private String colecao;
	
	/** Tipo de material do material. */
	private String tipoDeMaterial;
	
	/** Id do t�tulo associado ao material. */
	private int idTitulo;
	
	/** Informa��es do T�tulo do material */
	private String informacoesTitulo;
	
	/**
	 * Construtor padr�o.
	 */
	public DadosRelatorioMateriaisBaixados(
			int idMaterial, String codigoBarras, String numeroPatrimonio, String motivoBaixa,
			int idBiblioteca, String descricaoBiblioteca, String nomeUsuarioBaixa,	
			Date dataBaixa, String informacoesTitulo, String classificacao1, String classificacao2, String classificacao3,
			String colecao, String tipoDeMaterial, int idTitulo ) {
		this.idMaterial = idMaterial;
		this.codigoBarras = codigoBarras;
		this.numeroPatrimonio = numeroPatrimonio;
		this.motivoBaixa = motivoBaixa;
		this.idBiblioteca = idBiblioteca;
		this.descricaoBiblioteca = descricaoBiblioteca;
		this.nomeUsuarioBaixa = nomeUsuarioBaixa;
		this.dataBaixa = dataBaixa;
		this.informacoesTitulo = informacoesTitulo;
		this.classificacao1 = classificacao1;
		this.classificacao2 = classificacao2;
		this.classificacao3 = classificacao3;
		this.colecao = colecao;
		this.tipoDeMaterial = tipoDeMaterial;
		this.idTitulo = idTitulo;
	}
	
	@Override
	public int compareTo(DadosRelatorioMateriaisBaixados o) {
		int resultado = new Integer(this.idBiblioteca).compareTo(o.idBiblioteca);
		
		if(resultado == 0) // Mesma biblioteca
			resultado =  - (this.dataBaixa.compareTo(o.dataBaixa));
		
		return resultado;
	}
	
	/// GETs

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public String getMotivoBaixa() {
		return motivoBaixa;
	}

	public int getIdBiblioteca() {
		return idBiblioteca;
	}

	public String getDescricaoBiblioteca() {
		return descricaoBiblioteca;
	}

	public String getNomeUsuarioBaixa() {
		return nomeUsuarioBaixa;
	}

	public Date getDataBaixa() {
		return dataBaixa;
	}

	public String getInformacoesTitulo() {
		return informacoesTitulo;
	}

	public int getIdMaterial() {
		return idMaterial;
	}
	
	public String getNumeroPatrimonio() {
		return numeroPatrimonio;
	}
	
	public String getClassificacao1() {
		return classificacao1;
	}

	public String getClassificacao2() {
		return classificacao2;
	}

	public String getClassificacao3() {
		return classificacao3;
	}

	public String getColecao() {
		return colecao;
	}
	
	public String getTipoDeMaterial() {
		return tipoDeMaterial;
	}
	
	public int getIdTitulo() {
		return idTitulo;
	}

	public void setIdTitulo(int idTitulo) {
		this.idTitulo = idTitulo;
	}
	
}