/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * CampoControle guarda os registro de controle (não possui indicadores 
 * nem subCampos). Exemplo: campo LIDER e todos os campos que têm a etiqueta 00x 
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
	 * O campo lider tem alguns dados fixo e outros variáveis, a string abaixo contém os dados fixos, 
	 * os espaços em branco são as posições dos campos variáveis. <br/>
	 * O campo lider possui 24 posições
	 */
	public static final String DADOS_CAMPO_LIDER_BIBLIOGRAFICO = "00000     2200000   4500";



	/** Campo 006 com todas as 18 (de 00 a 17)  posições vazias   */
	public static final String DADOS_CAMPO_006_BIBLIOGRAFICO = "                  ";


	/** Campo 007 com todas as 23 (de 00 a 22)  posições vazias <br/>
	 * O campo 007 pode ter ATE 23 posições, isso depende da categoria do material <br/>
	 * sendo que se uma posição for ocupado, todas as anteriores devem conter dados <br/>
	 * ou o caracter cheio '|' caso a pessoa que esteja catalogando não veja necessidade dessa  v
	 * informação.<br/>
	 */
	public static final String DADOS_CAMPO_007_BIBLIOGRAFICO = "                       ";

	
	/** 
	 * <strong>Retirado do padrão MARC:</strong><br/>
	 * Campo 008  possui 40 posições fixas de caracteres que contém informações sobre o <br/>
	 * registro como um todo ou sobre aspectos específicos do item que está sendo catalogado. <br/>
	 * Esses dados codificados podem ser usados para propósitos de recuperação e gerenciamento de dados.<br/><br/>
	 * 
	 * As posições 00 a 17 e 35 a 39 são definidas igualmente para todas as formas de material, <br/>
	 * com especial consideração para a posição 06. As posições 18 a 34 são geralmente diferentes <br/>
	 * para cada formato, embora alguns dados são definidos para mais de um formato, e em tais <br/>
	 * casos eles ocupam a mesma posição, onde quer que estejam definidos.<br/><br/>
	 *  
	 * Os dados tem posições definidas. As posições que não estão definidas, contém um espaço em<br/>
	 * branco '#'. Todas as posições definidas deve conter ou um código ou um caracter cheio '|'<br/>
	 * '|' é usando quando a instituição catalogadora não vê necessidade de catalogar uma determinada<br/>
	 * posição. Ou seja, para esse campo é necessário cadastrar para cada posições o valor '|' <br/>
	 * senão vai da erro de validação. <br/><br/>
	 *
	 * campo 008 com todas as 40  posições vazias <br/>
	 * <strong>yymmdd = data que o arquivo foi gerado precisa ser gerado pelo sistema </strong> <br/><br/>
	 */
	public static final String DADOS_CAMPO_008_BIBLIOGRAFICO = "yymmdd                                  ";

	
	
	public static final String DADOS_CAMPO_LIDER_AUTORIDADE = "00000 z   2200000   4500";
	
	
	/**  yymmdd = data que o arquivo foi gerado precisa ser gerado pelo sistema  */
	public static final String DADOS_CAMPO_008_AUTORIDADE = "yymmdd                                  ";
	
	
	/**
	 *    Dados do campo lider de artigos de periódicos. Vão ser fixos, o sistema vai preencher para
	 * o usuário.
	 */
	public static final String DADOS_CAMPO_LIDER_ARTIGOS_PERIODICOS = "00000nab# 2200000#ar4500";
	
	
	
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.campo_controle_sequence") })		
	@Column(name="id_campo_controle")
	private int id;


	/**
	 * guarda os dados do registro catalográfico
	 */
	@Column(name = "dado", nullable=false)
	private String dado;

	private String codmerg;

	
	/**
	 *    Construtor padrão
	 */
	public CampoControle(){

	}
	
	/**
	 * Construtor de um campo de controle que não está associado a nenhuma entidade MARC
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
	 * Construtor de um campo de controle para títulos
	 * 
	 * @param dados os dados catalográficos 
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
	 * @param dados os dados catalográficos 
	 * @param etiqueta e etiqueta dele
	 */
	public CampoControle(String dado, Etiqueta etiqueta, int posicao, Autoridade autoridade){
		this(dado, etiqueta, posicao);
		this.autoridade = autoridade;
		this.autoridade.addCampoControle(this);
	}
	
	
	/**
	 * 
	 * Construtor de um campo de controle para artigos de periódicos
	 * 
	 * @param dados os dados catalográficos 
	 * @param etiqueta e etiqueta dele
	 */
	public CampoControle(String dado, Etiqueta etiqueta, int posicao,  ArtigoDePeriodico artigo){
		this(dado, etiqueta, posicao);
		this.artigo = artigo;
		this.artigo.addCampoControle(this);
	}


	
	

	
	
	/**
	 * Método que adiciona o título ao campo de controle.
	 *
	 * @param tituloCatalografico
	 */
	public void adicionaTitulo(TituloCatalografico tituloCatalografico){
		this.tituloCatalografico = tituloCatalografico;
		this.tituloCatalografico.addCampoControle(this);
	}
	
	
	/**
	 * Método que adiciona a autoridade ao campo de controle.
	 *
	 * @param tituloCatalografico
	 */
	public void adicionaAutoridade(Autoridade autoridade){
		this.autoridade = autoridade;
		this.autoridade.addCampoControle(this);
	}
	
	
	/**
	 * Método que adiciona o artigo ao campo de controle.
	 *
	 * @param tituloCatalografico
	 */
	public void adicionaArtigo(ArtigoDePeriodico artigo){
		this.artigo = artigo;
		this.artigo.addCampoControle(this);
	}
	

	/**
	 * 
	 * Substitui ' ' por '^' para mostrar em páginas HTML
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
	 * Método que completa o campo de dados com até a quantidade de posições passadas
	 * 
	 *    Isso é necessário porque o usuário deveria, mas pode não ter digitado todos os caracteres
	 * para os dados de algum campo de controle. Essa método preenche o tamanho mínimo.
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
