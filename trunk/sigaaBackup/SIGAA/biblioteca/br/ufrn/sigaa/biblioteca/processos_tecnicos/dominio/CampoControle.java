/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/07/2008
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * 
 * CampoControle guarda os registro de controle (n�o possui indicadores 
 * nem subCampos). Exemplo: campo LIDER e todos os campos que t�m a etiqueta 00x 
 * 
 * 
 * @author jadson
 * @since 18/07/2008
 * @version 1.0 criacao da classe
 * 
 */
@Entity
@Table(name = "campo_controle", schema = "biblioteca")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CampoControle extends CampoVariavel implements PersistDB {
	
	
	/** Indica que se trata de um campo de controle do formato MARC21 */
	public static final char IDENTIFICADOR_CAMPO = 'C';

	/**
	 * O campo lider tem alguns dados fixo e outros vari�veis, a string abaixo cont�m os dados fixos, 
	 * os espa�os em branco s�o as posi��es dos campos vari�veis. <br/>
	 * O campo lider possui 24 posi��es
	 */
	public static final String DADOS_CAMPO_LIDER_BIBLIOGRAFICO = "00000     2200000   4500";



	/** Campo 006 com todas as 18 (de 00 a 17)  posi��es vazias   */
	public static final String DADOS_CAMPO_006_BIBLIOGRAFICO = "                  ";


	/** Campo 007 com todas as 23 (de 00 a 22)  posi��es vazias <br/>
	 * O campo 007 pode ter ATE 23 posi��es, isso depende da categoria do material <br/>
	 * sendo que se uma posi��o for ocupado, todas as anteriores devem conter dados <br/>
	 * ou o caracter cheio '|' caso a pessoa que esteja catalogando n�o veja necessidade dessa  v
	 * informa��o.<br/>
	 */
	public static final String DADOS_CAMPO_007_BIBLIOGRAFICO = "                       ";

	
	/** 
	 * <strong>Retirado do padr�o MARC:</strong><br/>
	 * Campo 008  possui 40 posi��es fixas de caracteres que cont�m informa��es sobre o <br/>
	 * registro como um todo ou sobre aspectos espec�ficos do item que est� sendo catalogado. <br/>
	 * Esses dados codificados podem ser usados para prop�sitos de recupera��o e gerenciamento de dados.<br/><br/>
	 * 
	 * As posi��es 00 a 17 e 35 a 39 s�o definidas igualmente para todas as formas de material, <br/>
	 * com especial considera��o para a posi��o 06. As posi��es 18 a 34 s�o geralmente diferentes <br/>
	 * para cada formato, embora alguns dados s�o definidos para mais de um formato, e em tais <br/>
	 * casos eles ocupam a mesma posi��o, onde quer que estejam definidos.<br/><br/>
	 *  
	 * Os dados tem posi��es definidas. As posi��es que n�o est�o definidas, cont�m um espa�o em<br/>
	 * branco '#'. Todas as posi��es definidas deve conter ou um c�digo ou um caracter cheio '|'<br/>
	 * '|' � usando quando a institui��o catalogadora n�o v� necessidade de catalogar uma determinada<br/>
	 * posi��o. Ou seja, para esse campo � necess�rio cadastrar para cada posi��es o valor '|' <br/>
	 * sen�o vai da erro de valida��o. <br/><br/>
	 *
	 * campo 008 com todas as 40  posi��es vazias <br/>
	 * <strong>yymmdd = data que o arquivo foi gerado precisa ser gerado pelo sistema </strong> <br/><br/>
	 */
	public static final String DADOS_CAMPO_008_BIBLIOGRAFICO = "yymmdd                                  ";

	
	
	public static final String DADOS_CAMPO_LIDER_AUTORIDADE = "00000 z   2200000   4500";
	
	
	/**  yymmdd = data que o arquivo foi gerado precisa ser gerado pelo sistema  */
	public static final String DADOS_CAMPO_008_AUTORIDADE = "yymmdd                                  ";
	
	
	/**
	 *    Dados do campo lider de artigos de peri�dicos. V�o ser fixos, o sistema vai preencher para
	 * o usu�rio.
	 */
	public static final String DADOS_CAMPO_LIDER_ARTIGOS_PERIODICOS = "00000nab# 2200000#ar4500";
	
	
	
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.campo_controle_sequence") })		
	@Column(name="id_campo_controle")
	private int id;


	/**
	 * guarda os dados do registro catalogr�fico
	 */
	@Column(name = "dado", nullable=false)
	private String dado;

	private String codmerg;

	
	/**
	 *    Construtor padr�o
	 */
	public CampoControle(){

	}
	
	/**
	 * Construtor de um campo de controle que n�o est� associado a nenhuma entidade MARC
	 * @param dado
	 * @param etiqueta
	 */
	public CampoControle(String dado, Etiqueta etiqueta, int posicao){
		this.dado = dado;
		this.etiqueta = etiqueta;
		this.setPosicao(posicao);
	}
	

	/**
	 * 
	 * Construtor de um campo de controle para t�tulos
	 * 
	 * @param dados os dados catalogr�ficos 
	 * @param etiqueta e etiqueta dele
	 */
	public CampoControle(String dado, Etiqueta etiqueta, int posicao, TituloCatalografico tituloCatalografico){
		this(dado, etiqueta, posicao);
		this.tituloCatalografico = tituloCatalografico;
		this.tituloCatalografico.addCampoControle(this);
	}

	/**
	 * 
	 * Construtor de um campo de controle para autoridades
	 * 
	 * @param dados os dados catalogr�ficos 
	 * @param etiqueta e etiqueta dele
	 */
	public CampoControle(String dado, Etiqueta etiqueta, int posicao, Autoridade autoridade){
		this(dado, etiqueta, posicao);
		this.autoridade = autoridade;
		this.autoridade.addCampoControle(this);
	}
	
	
	/**
	 * 
	 * Construtor de um campo de controle para artigos de peri�dicos
	 * 
	 * @param dados os dados catalogr�ficos 
	 * @param etiqueta e etiqueta dele
	 */
	public CampoControle(String dado, Etiqueta etiqueta, int posicao,  ArtigoDePeriodico artigo){
		this(dado, etiqueta, posicao);
		this.artigo = artigo;
		this.artigo.addCampoControle(this);
	}


	
	

	
	
	/**
	 * M�todo que adiciona o t�tulo ao campo de controle.
	 *
	 * @param tituloCatalografico
	 */
	public void adicionaTitulo(TituloCatalografico tituloCatalografico){
		this.tituloCatalografico = tituloCatalografico;
		this.tituloCatalografico.addCampoControle(this);
	}
	
	
	/**
	 * M�todo que adiciona a autoridade ao campo de controle.
	 *
	 * @param tituloCatalografico
	 */
	public void adicionaAutoridade(Autoridade autoridade){
		this.autoridade = autoridade;
		this.autoridade.addCampoControle(this);
	}
	
	
	/**
	 * M�todo que adiciona o artigo ao campo de controle.
	 *
	 * @param tituloCatalografico
	 */
	public void adicionaArtigo(ArtigoDePeriodico artigo){
		this.artigo = artigo;
		this.artigo.addCampoControle(this);
	}
	

	/**
	 * 
	 * Substitui ' ' por '^' para mostrar em p�ginas HTML
	 * 
	 * a escolha de ^ foi somente para ficar igual ao ALEPH
	 * 
	 * @return
	 */
	public String getDadoParaExibicao() {

		String dadosTemp = new String(dado);

		while(dadosTemp.contains(" ")){
			dadosTemp = dadosTemp.substring(0, dadosTemp.indexOf(" "))
			+ "^" 
			+dadosTemp.substring(dadosTemp.indexOf(" ")+1, dadosTemp.length());	
		}
		return dadosTemp;
	}


	/**
	 * 
	 * M�todo que completa o campo de dados com at� a quantidade de posi��es passadas
	 * 
	 *    Isso � necess�rio porque o usu�rio deveria, mas pode n�o ter digitado todos os caracteres
	 * para os dados de algum campo de controle. Essa m�todo preenche o tamanho m�nimo.
	 *
	 */
	public void completaDados(int quantidade){
		if(dado != null && dado.length()< quantidade){

			for (int i = dado.length(); i < quantidade; i++) {
				dado = dado + " ";
			}
		}
	}
	
	
	// sets e gets

	public void setDado(String dado) {
		this.dado = dado;
	}

	public int getId() {		
		return id;
	}


	public void setId(int id) {	
		this.id = id;
	}

	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}
	
	@Override
	public Etiqueta getEtiqueta() {
		return etiqueta;
	}


	public String getDado() {
		return dado;
	}

}
