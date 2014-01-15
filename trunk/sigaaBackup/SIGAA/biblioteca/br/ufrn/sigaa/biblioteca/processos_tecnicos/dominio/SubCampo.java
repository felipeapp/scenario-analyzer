/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 28/07/2008
*
*/
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *     Classe que compõe um campo de dados. É ela que realmente guarda 
 * os dados da catalogação.
 * 
 * @author jadson
 * @since 28/07/2008
 * @version 1.0 criacao da classe.
 */
@Entity
@Table(name = "sub_campo", schema = "biblioteca")
public class SubCampo implements Validatable{

	/** Constantes que representam os valores dos códigos do sub campos A.*/
	public static final char SUB_CAMPO_A = 'a';
	/** Constantes que representam os valores dos códigos do sub campos B.*/
	public static final char SUB_CAMPO_B = 'b';
	/** Constantes que representam os valores dos códigos do sub campos C.*/
	public static final char SUB_CAMPO_C = 'c';
	/** Constantes que representam os valores dos códigos do sub campos D.*/
	public static final char SUB_CAMPO_D = 'd';
	/** Constantes que representam os valores dos códigos do sub campos E.*/
	public static final char SUB_CAMPO_E = 'e';
	/** Constantes que representam os valores dos códigos do sub campos F.*/
	public static final char SUB_CAMPO_F = 'f';
	/** Constantes que representam os valores dos códigos do sub campos U.*/
	public static final char SUB_CAMPO_U = 'u';
	/** Constantes que representam os valores dos códigos do sub campos X.*/
	public static final char SUB_CAMPO_X = 'x';
	/** Constantes que representam os valores dos códigos do sub campos G.*/
	public static final char SUB_CAMPO_G = 'g';
	/** Constantes que representam os valores dos códigos do sub campos W.*/
	public static final char SUB_CAMPO_W = 'w';
	/** Constantes que representam os valores dos códigos do sub campos T.*/
	public static final char SUB_CAMPO_T = 't';
	/** Constantes que representam os valores dos códigos do sub campos N.*/
	public static final char SUB_CAMPO_N = 'n';
	/** Constantes que representam os valores dos códigos do sub campos P.*/
	public static final char SUB_CAMPO_P = 'p';
	/** Constantes que representam os valores dos códigos do sub campos V.*/
	public static final char SUB_CAMPO_V = 'v';
	/** Constantes que representam os valores dos códigos do sub campos Y.*/
	public static final char SUB_CAMPO_Y = 'y';
	/** Constantes que representam os valores dos códigos do sub campos Z.*/
	public static final char SUB_CAMPO_Z = 'z';
	/** Constantes que representam os valores dos códigos do sub campos H.*/
	public static final char SUB_CAMPO_H = 'h';
	
	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.sub_campo_sequence") })
	@Column(name="id_sub_campo")
	private int id;

	/**
	 * Guarda os identificadores do subCampo. Ex. $a, $b, $c
	 */
	@Column(name = "codigo", nullable=false)
	private Character codigo;

	/**
	 * Aqui onde realmente fica os dados da catalogação.
	 */
	@Column(name = "dado", nullable=false)
	private String dado;

	/** Guarda o dados em formato ASCII apenas para realizar a pesquisa sem considerar os acentos.*/
	@Column(name = "dado_ascii", nullable=false)
	private String dadoAscii;

	
	/** A posição do subcampo dentro do campo de dados. hibernate usa para manter a posição dos subcampos  */
	@Column(name = "posicao", nullable= false)
	private int posicao = -1;
	
	/**
	 *  O campo de dados que esse título pertence. 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_campo_dados", referencedColumnName = "id_campo_dados", nullable=false)
	private CampoDados campoDados;
	
	/** Identifica a entidade no sistema anterior, caso ela tenha sido migrada. */
	private String codmerg;

	
	/**
	 *     <p> Se os dados de um subcampo foram gerados com dados de um subcampo de uma autoridade 
	 *    (subcampos de assunto e autor), guarda essa referência aqui para na hora da alteração de
	 *    uma autoridade alterar os dados do título automaticamente.</p>
	 *    
	 *    <p>Obs.: No momento que o usuário atribui um valor de um subcampo de autoridade a um subcampo
	 *    de um Título esse valor não pode ser mais alterado manualmente pelo usuário. Vai alterar 
	 *    quando ele altera a autoridade.</p>
	 *    
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_sub_campo_autoridade", referencedColumnName = "id_sub_campo")
	private SubCampo subCampoAutoridade;
	
	
	/**
	 * <p>Só para exibir a descrição sub campo na tela para o usuário.</p>
	 * <p>A descrição do subcampo está localizada no descritor na etiqueta do campo de dados a qual esse sub campo pertence. </p> 
	 */
	@Transient
	private String descricaoSubCampo;
	
	/**
	 * <p> Guarda temporariamente o id da planilha de catalogação a partir da qual esse sub campo foi gerado.</p>
	 * 
	 * <p> Quando é usado a catalogação simplificada o sistema adiciona todos os sub campos presentes na 
	 * planilha escolhida pelo usuário, caso o usuário escolha outra planilha os sub campos adicionados pela planilha anterior devem ser removidos.</p>
	 */
	@Transient
	private int idPlanilhaGerado;
	
	/** Guarda um identificador temporário para pode recuperar informações dos campos ainda não persistidos. */
	@Transient
	private int identificadorTemp;
	
	/**
	 * Evite usar para não criar sub campos inconsistentes pelo sistema, se for usar tenha certeza que
	 * sabe o que está fazendo, por exemplo, você vai ter que lembrar de chamar o método 
	 * setCamposDados(), senão o hibernate não vai salvar os sub campos desse campo de dados
	 */
	public SubCampo(){

	}

	/**
	 * Construtor para criar um subcampo vazio, no caso de uma catalogação
	 * 
	 */
	public SubCampo(CampoDados campoDados){
		this.campoDados = campoDados;
		this.campoDados.addSubCampo(this);
	}

	/**
	 * Cria um subcampo com o dado sem nada. Usado no cadastro de título com planilha
	 *  
	 * @param codigo
	 * @param dado
	 * @param campoDados
	 */
	public SubCampo(Character codigo, CampoDados campoDados, int posicao){
		this(campoDados);
		this.posicao = posicao;
		this.codigo = codigo;
	}

	/**
	 * Cria um subcampo completo
	 *  
	 * @param codigo
	 * @param dado
	 * @param campoDados
	 */
	public SubCampo(Character codigo, String dado, CampoDados campoDados, int posicao){
		this(codigo, campoDados, posicao);
		this.dado = dado;
		if(dado != null)
			this.dadoAscii =  StringUtils.toAsciiAndUpperCase(dado);
	}

	
	
	/**
	 * Cria um subcampo completo com dados de um subcampo de autoridade.
	 *  
	 * @param codigo
	 * @param dado
	 * @param campoDados
	 */
	public SubCampo(Character codigo, String dado, CampoDados campoDados, SubCampo subCampoAutoridade, int posicao){
		this(codigo, dado, campoDados, posicao);
		this.subCampoAutoridade = subCampoAutoridade;
	}
	
	
	
	/**
	 *   Verifica se um subcampo está vazio, um subcampo está vazio se não tem código ou a 
	 *   variável dados está vazia.
	 * 
	 *   <p>Esses subcampos não devem ser validados nem salvos pelo sistema.</p>
	 * 
	 * @return
	 */
	public boolean isSubCampoVazio(){
		
		if( this.getCodigo() == null || ( !Character.isLetter(this.getCodigo()) &&  !Character.isDigit(this.getCodigo()) ) 
				|| StringUtils.isEmpty(this.getDado()) )
			return true;
		else
			return false;
		
	}
	
	
	/**
	 * Verifica se o campo de dados é utilizado como campo para armazenar a classificação bibliográfica.
	 */
	public boolean isSubCampoArmazenamentoClassificacaoBibliografica(){
		
		if(this.getCodigo() != null ){
			return  Arrays.asList(CamposMarcClassificacaoBibliografica.getSubCamposClassificacao()).contains(this.getCodigo());
		}else{
			return false;
		}
	}
	
	
	/**
	 * Ver comentário na classe pai
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}




	/**
	 *    PARA UMA MESMA ETIQUETA, dois subcampos são iguais se ele possuírem o mesmo código. 
	 *  Apesar de que em alguns casos o mesmo subcampo pode se repetir com dados diferentes
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubCampo other = (SubCampo) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	// sets e gets


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public CampoDados getCampoDados() {
		return campoDados;
	}


	public void setCampoDados(CampoDados campoDados) {
		this.campoDados = campoDados;
	}


	public Character getCodigo() {
		return codigo;
	}


	public void setCodigo(Character codigo) {
		this.codigo = codigo;
	}


	public String getDado() {
		return dado;
	}

	/**
	 *   Retorna os dados dos campo sem a pontuação AACR2 (Anglo-American Cataloging Rules, 2ª edition) colocada 
	 *   no final pelos bibliotecários.
	 * @return
	 */
	public String getDadoSemFormatacaoAACR2() {
		return CatalogacaoUtil.retiraPontuacaoAACR2(dado, true);
	}
	
	/** Seta o dado e dado asscii do sub campo */
	public void setDado(String dado) {
		this.dado = dado;
		if(dado != null)
			this.dadoAscii = StringUtils.toAsciiAndUpperCase(dado);
	}

	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}
	
	public String getDadoAscii() {
		return dadoAscii;
	}

	
	/**
	 *  Usado na página de formDadsotituloCatalografico para saber se o código de subCampo é o 'a' 
	 * @return
	 */
	public boolean isSubCampoA(){
		if(codigo == null) 
			return false;
		else
			return codigo.equals(SUB_CAMPO_A);
	}
	
	/**
	 *  Usado na página de formDadsotituloCatalografico para saber se o código de subCampo é o 'b' 
	 * @return
	 */
	public boolean isSubCampoB(){
		if(codigo == null) 
			return false;
		else
			return codigo.equals(SUB_CAMPO_B);
	}
	
	/**
	 *  Usado na página de formDadsotituloCatalografico para saber se o código de subCampo é o 'd' 
	 * @return
	 */
	public boolean isSubCampoD(){
		if(codigo == null) 
			return false;
		else
			return codigo.equals(SUB_CAMPO_D);
	}
	
	/**
	 *  Usado na página de formDadsotituloCatalografico para saber se o código de subCampo é o 'x' 
	 * @return
	 */
	public boolean isSubCampoX(){
		if(codigo == null) 
			return false;
		else
			return codigo.equals(SUB_CAMPO_X);
	}
	
	
	/**
	 * 
	 * As regras de validação dessa classe
	 *
	 * @return
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		if(codigo == null || (!Character.isDigit(codigo) && !Character.isLetter(codigo) )){
			erros.addErro("Código para sub campo inválido");
		}

		if(dado == null || "".equals(dado)){
			erros.addErro("Os dados do sub campo estão vazios");
		}

		return erros;
	}

	

	public SubCampo getSubCampoAutoridade() {
		return subCampoAutoridade;
	}

	public void setSubCampoAutoridade(SubCampo subCampoAutoridade) {
		this.subCampoAutoridade = subCampoAutoridade;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	public String getDescricaoSubCampo() {
		return descricaoSubCampo;
	}

	public void setDescricaoSubCampo(String descricaoSubCampo) {
		this.descricaoSubCampo = descricaoSubCampo;
	}

	public int getIdPlanilhaGerado() {
		return idPlanilhaGerado;
	}

	public void setIdPlanilhaGerado(int idPlanilhaGerado) {
		this.idPlanilhaGerado = idPlanilhaGerado;
	}

	public int getIdentificadorTemp() {
		return identificadorTemp;
	}

	public void setIdentificadorTemp(int identificadorTemp) {
		this.identificadorTemp = identificadorTemp;
	}
	
	
}
