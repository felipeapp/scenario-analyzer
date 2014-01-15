/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 28/07/2008
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.StringUtils;

/**
 *
 *    Guarda as informa��es de um campo de dados do padr�o MARC. S�o registros que possuem indicadores e subcampos.
 *
 * @author jadson
 * @since 28/07/2008
 * @version 1.0 cria��o da classe
 *
 */
@Entity
@Table(name = "campo_dados", schema = "biblioteca")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CampoDados  extends CampoVariavel implements PersistDB {


	/**
	 * Identificador de um campo de dados.
	 */
	public static final char IDENTIFICADOR_CAMPO = 'D';

	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.campo_dados_sequence") })
	@Column(name="id_campo_dados")
	private int id;

	/**
	 * Guarda o 1 indicador do registro Ex. 10, ##, #1 (os '#' foram trocados por espa�o em branco).
	 */
	@Column(name = "indicador1", nullable=false)
	private Character  indicador1 = new Character(' ');

	/**
	 * Guarda o 2 indicador do registro Ex. 10, ##, #1 (os '#' foram trocados por espa�o em branco).
	 */
	@Column(name = "indicador2", nullable=false)
	private Character indicador2 = new Character(' ');
	
	
	/**
	 * Os sub campos que esse registro catalogr�fico pode possuir.
	 */
	@OneToMany(cascade = { CascadeType.ALL, CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy="campoDados")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<SubCampo> subCampos = new ArrayList<SubCampo>();

	
	/** C�digo de migra��o. */
	private String codmerg;
	

	/** Encapsula a cole��o de subcampos para acessa-los das p�ginas JSF.  */
	@Transient
	private DataModel dataModelSubCampos;
	
	
	/** <p>Define se o agrupamento da etiqueta � vis�vel ou n�o para o usu�rio. 
	 *  Uma forma de agrupar os resultados na p�gina de cataloga��o para o usu�rio.</p> 
	 * 
	 *   @see Etiqueta
	 *   @see GrupoEtiqueta
	 */
	@Transient
	private boolean grupoEtiquetaVisivel = false;
	
	
	/**
	 * Cria um campo de dados vazio.
	 */
	public CampoDados(){
	}

	/**
	 * Cria um campo de dados j� com uma etiqueta vazia.
	 */
	public CampoDados(Etiqueta e, TituloCatalografico titulo, int posicao){
		this.etiqueta = e;
		this.setPosicao(posicao);
		this.tituloCatalografico = titulo;
		this.tituloCatalografico.addCampoDados(this);
	}

	/**
	 * Cria um campo de dados j� com uma etiqueta e com o subcampo preenchido com o dado informado.
	 */
	public CampoDados(Etiqueta e, TituloCatalografico titulo, Character subCampo, String dado, int posicaoCampoDados, int posicaoSubCampo){
		this(e, titulo, posicaoCampoDados);
		new SubCampo( subCampo, dado, this, posicaoSubCampo);
	}

	
	/**
	 * Construtor que cria um campo de dados com t�tulo indicador e etiqueta.
	 */
	public CampoDados(Etiqueta e, Character indicador1, Character indicador2, TituloCatalografico titulo, int posicaoCampoDados){
		this(e, titulo, posicaoCampoDados);
		this.setIndicador1(indicador1);
		this.setIndicador2(indicador2);
	}

	
	/**
	 * Construtor que cria um campo de dados com autoridade indicador e etiqueta.
	 */
	public CampoDados(Etiqueta e, Character indicador1, Character indicador2, Autoridade autoridade, int posicaoCampoDados){
		this.etiqueta = e;
		this.autoridade = autoridade;
		this.setPosicao(posicaoCampoDados);
		this.autoridade.addCampoDados(this);
		this.setIndicador1(indicador1);
		this.setIndicador2(indicador2);
	}
	

	/**
	 * Cria um campo de dados completo.
	 */
	public CampoDados(Etiqueta e, TituloCatalografico titulo, Character indicador1, Character indicador2, char subCampo, String dado, int posicaoCampoDados, int posicaoSubCampo){
		this(e, indicador1, indicador2, titulo, posicaoCampoDados);
		new SubCampo( subCampo, dado, this, posicaoSubCampo);
	}
	
	
	/**
	 * Construtor que cria um campo de dados para o artigo.
	 */
	public CampoDados(Etiqueta e, Character indicador1, Character indicador2, ArtigoDePeriodico artigo, int posicaoCampoDados){
		this.etiqueta = e;
		this.artigo = artigo;
		this.setPosicao(posicaoCampoDados);
		this.artigo.addCampoDados(this);
		this.setIndicador1(indicador1);
		this.setIndicador2(indicador2);
	}
	
	
	/**
	 * Cria um campo de dados completo para artigos de peri�dicos, j� com um sub campo.
	 * @param e
	 */
	public CampoDados(Etiqueta e, ArtigoDePeriodico artigo, Character indicador1, Character indicador2, char subCampo, String dado, int posicaoCampoDados, int posicaoSubCampo){
		this(e, indicador1, indicador2, artigo, posicaoCampoDados);
		this.setPosicao(posicaoCampoDados);
		new SubCampo( subCampo, dado, this, posicaoSubCampo);
	}
	
	
	
	
	/**
	 * <p>Adiciona um novo subCampo a este campo de dados.</p>
	 * 
	 * <p>E atualiza o data model para ser usado na tela de cataloga��o.</p>
	 * 
	 * @param sub o sub campo
	 */
	public void addSubCampo(SubCampo sub){
		subCampos.add(sub);
		dataModelSubCampos = new ListDataModel(subCampos);
	}
	

	/**
	 * 
	 * Verifica se o campo � um campo local (tem sentido somente para as bibliotecas que usam o sistema).
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta#isEquetaLocal()
	 */
	public boolean isCampoLocal(){
		
		if(etiqueta != null && etiqueta.getTag() != null && etiqueta.isEquetaLocal() ){
			return true;
		}else{
			return false;
		}
	}

	
	
	/**
	 * 
	 * Verifica se o campo � um campo local(tem sentido somente para as bibliotecas que usam o sistema)
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta#isEquetaLocal()
	 */
	public boolean isCampoDeUsoReservador(){
		
		if(etiqueta != null && etiqueta.isEquetaDeUsoReservado() ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Verifica se o campo � um campo � um campo que pode ser preenchido com as informa��es da base de autoridades de autores.
	 */
	public boolean isCampoPrenchidoComAutoridadesAutores(){
		
	
		if(etiqueta != null ){
			if("100".equals(etiqueta.getTag()) || "110".equals(etiqueta.getTag()) || "111".equals(etiqueta.getTag())
				|| "700".equals(etiqueta.getTag()) || "710".equals(etiqueta.getTag()) || "711".equals(etiqueta.getTag())  ){
				
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Verifica se o campo � um campo � um campo que pode ser preenchido com as informa��es da base de autoridades de assuntos.
	 */
	public boolean isCampoPrenchidoComAutoridadesAssuntos(){
		
		if(etiqueta != null ){
			if("650".equals(etiqueta.getTag()) || "651".equals(etiqueta.getTag()) || "600".equals(etiqueta.getTag())
				|| "610".equals(etiqueta.getTag()) || "611".equals(etiqueta.getTag()) || "653".equals(etiqueta.getTag()) 
				|| "650".equals(etiqueta.getTag()) ){
				
				return true;
			}
			
		}
		
		return false;
	}
	
	
	
	/** Verifica se o campo de dados possui o sub campo passado*/
	public boolean isContemSubCampo(Character codigo){
		if(subCampos == null) return false;
		
		for (SubCampo subCampo : subCampos) {
			if(codigo.equals(subCampo.getCodigo()))
				return true;
		}
		
		return false;
	}
	
	
	/**
	 *    M�todo que verifica se um campo de dados est� vazio. Um campo de dados est� vazio se
	 * n�o tem etiqueta ou n�o tem subcampo ou todos os seus subcampo est�o vazios.
	 * 
	 *    Esses campos n�o deve ser validados nem salvos pelo sistema.
	 */
	public boolean isCampoVazio(){
	
		if(this.getEtiqueta() == null || StringUtils.isEmpty(this.getEtiqueta().getDescricao()) )
			return true;
		
		
		if(this.getSubCampos() == null || this.getSubCampos().size() == 0)
			return true;
		
		
		for (SubCampo subCampo : this.getSubCampos() ) {
			if(! subCampo.isSubCampoVazio()) // Se pelo menos um subcampo n�o est� vazio, ent�o o campo n�o est� vazio
				return false;
		}
		
		return true;
	}
	
	
	
	
	/**
	 * Verifica se o campo de dados � utilizado como campo para armazenar a classifica��o bibliogr�fica.
	 */
	public boolean isCampoArmazenamentoClassificacaoBibliografica(){
		
		if(etiqueta != null && etiqueta.getTag() != null ){
			
			return  Arrays.asList(CamposMarcClassificacaoBibliografica.getCamposClassificacao()).contains(etiqueta.getTag());
		}else{
			return false;
		}
	}
	
	
	
	

	/**
	 *     <p>Retorna menor posi��o do subcampo (ou seja o 1� SubCampo) que apresenta o c�digo passado dentro deste campos de dados.<br/>
	 *     Por exemplo; se o campo tiver v�rios subcampos 'a', retorna a posi��o do primeiro dentro da lista.</p>
	 *      
	 *     <p>� retornado o primeiro sub campo que n�o esteja na lista de posicoesJaRetornadas, para se chamar novamente retornar o 2� sub campo, 
	 *     em uma nova chamada retornar o 3�, e assim sucessivamente para n�o ficar sempre retorando o mesmo.</p>   
	 *  
	 *     <p>Observa��o: Se o campo n�o tiver o subcampo retorna  -1</p>
	 */
	public int getMenorPosicaoSubCampo(Character codigo, Integer ultimaPosicaoUsada){
		
		int posicao = -1; // -1 == sub campo n�o existe
		
		if(ultimaPosicaoUsada == null)
			ultimaPosicaoUsada = -1;
		
		
		for (int i = 0 ; i < subCampos.size() ; i++) {
			
			if(subCampos.get(i).getCodigo() != null)
				if(subCampos.get(i).getCodigo().equals(codigo) && (  posicao > i ||  posicao <= ultimaPosicaoUsada  || posicao == -1) ){
					posicao = i;
				}
		}
		
		if( posicao <= ultimaPosicaoUsada ) // se � melhor � porque todos os subcampo j� foram usados
			posicao = -1;
		
		return posicao;
	}
	
	
	
	

	// sets e gets

	@Override
	public int getId() {
		return id;
	}


	@Override
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o primeiro indicador ou espa�o em branco caso n�o exista. */
	public Character getIndicador1() {
		if(indicador1 == null){
			indicador1 =  new Character(' ');
		}
		return indicador1;
	}

	/** Configura as informa��o do primeiro indicador, espa�o em branco se n�o for informado */
	public void setIndicador1(Character indicador1) {
		
		if(indicador1 == null)
			this.indicador1 = new Character(' ');
		else
			this.indicador1 = indicador1;
	}

	/** Retorna o segundo indicador ou espa�o em branco caso n�o exista. */
	public Character getIndicador2() {
		if(indicador2 == null){
			indicador2 =  new Character(' ');
		}
		return indicador2;
	}

	/** Configura as informa��o do segundo indicador, espa�o em branco se n�o for informado */
	public void setIndicador2(Character indicador2) {
		
		if(indicador2 == null)
			this.indicador2 = new Character(' ');
		else
			this.indicador2 = indicador2;
	}



	public List<SubCampo> getSubCampos() {
		return subCampos;
	}

	/** Configura os subcampos do campo de dados, j� adicionado-os ao data model*/
	public void setSubCampos(List<SubCampo> subCampos) {
		this.subCampos = subCampos;
		this.dataModelSubCampos = new ListDataModel(subCampos);
	}



	public DataModel getDataModelSubCampos() {
		return dataModelSubCampos;
	}

	public void setDataModelSubCampos(DataModel dataModelSubCampos) {
		this.dataModelSubCampos = dataModelSubCampos;
	}

	/**
	 * Junta os indicadores 1 e 2 e retorna. Geralmente usado para mostra-los nas p�ginas.
	 */
	public String getIndicadores() {
		return indicador1.toString()+ indicador2.toString();
	}


	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}

	public boolean isGrupoEtiquetaVisivel() {
		return grupoEtiquetaVisivel;
	}

	public void setGrupoEtiquetaVisivel(boolean grupoEtiquetaVisivel) {
		this.grupoEtiquetaVisivel = grupoEtiquetaVisivel;
	}
	
	
	
}
