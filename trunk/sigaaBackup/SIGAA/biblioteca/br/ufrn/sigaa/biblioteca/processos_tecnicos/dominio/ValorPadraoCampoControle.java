/*
 * ValorPadraoCampoControle.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 *         Os valores padr�es dos campo de controle para aparecer nas suas respectivas p�ginas de edi��o. 
 *         N�o sendo parecido com o padr�o MARC, isso foi feito baseado no funcionamento do ALEPH. 
 *         N�o procure em nenhuma documenta��o sobre MARC que n�o vai encontrar nada falando de 
 *     "valores padr�es" de campos. 
 *
 * @author jadson
 * @since 07/08/2008
 * @version 1.0 criacao da classe
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "valor_padrao_campo_controle", schema = "biblioteca")
public class ValorPadraoCampoControle  implements PersistDB{

	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_valor_padrao_campo_controle")
	private int id;	
	
	/* onde o dado come�a dentro do campo de controle */
	@Column(name = "posicao_inicial", nullable=false)
	private short posicaoInicial;

	/* onde o dado finaliza dentro do campo de controle */
	@Column(name = "posicao_final", nullable=false)
	private short posicaoFinal;
	
	
	/* guarda o valor padr�o que deve aparecer na tela */
	@Column(name = "valor_padrao", nullable=true)
	private String valorPadrao;

	
	/* a descri��o do campo para aparecer na tela*/
	@Column(name = "descricao", nullable=false)
	private String descricao;

	
	/* 
	 *    Para os valores padr�es do campo LIDER da base bibliogr�fica, que s�o diferentes 
	 *  para cada formato do material.
	 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formato_material", referencedColumnName="id_formato_material")
	private FormatoMaterial formatoMaterial;
	
	
	/* 
	 * Para as etiquetas 006 e 008. No caso da 008 vai estar cadastrado valores para a etiqueta 
	 * 008 bibliogr�fica e outros para a etiqueta 008 de autoridades.
	 * 
	 * Vai precisar cadastrar valores para a etiqueta LIDER de autoridades tamb�m usando 
	 * esse relacionamento.
	 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formato_material_etiqueta", referencedColumnName="id_formato_material_etiqueta")
	private FormatoMaterialEtiqueta formatoMaterialEtiqueta;

	
	/*
	 * Aqui s�o usados nos casos do campo 007 que possui uma categoria do material e n�o 
	 * possui rela��o nenhuma com o formato. Base bibliografica tamb�m
	 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_categoria_material", referencedColumnName="id_categoria_material")
	private CategoriaMaterial categoriaMaterial;
	

	
	/**
	 *  Construtor padr�o
	 */
	public ValorPadraoCampoControle(){

	}


	/**
	 * Construtor que deve ser usado
	 * 
	 * @param descricao
	 * @param posicaoInicial
	 * @param posicaoFinal
	 * @param valorPadrao
	 */
	public ValorPadraoCampoControle(String descricao, short posicaoInicial, short posicaoFinal, String valorPadrao, CategoriaMaterial categoriaMaterial) {
		this.descricao = descricao;
		this.posicaoInicial = posicaoInicial;
		this.posicaoFinal = posicaoFinal;
		
		if(StringUtils.isNotEmpty(valorPadrao))
			this.valorPadrao = valorPadrao.trim();
		else
			this.valorPadrao = valorPadrao;
		
		this.categoriaMaterial = categoriaMaterial; // necessaria para saber se a posi��o do campo 007 pode ser editada ou n�o.
	}

	
	/**
	 * 
	 * M�todo que retorna o tamanho que o campo deve ter para exibir o valor padr�o na tela.
	 *
	 * @return
	 */
	public int getMaxLength(){
		return 1 + (posicaoFinal - posicaoInicial);
	}
	
	
	
	/**
	 *   Para visualizar na p�gina de edi��o dos campos de controle 
	 *   
	 * @return
	 */
	public String getPosicao(){
		if(posicaoInicial == posicaoFinal){
			return ""+ posicaoInicial;                  // tem apenas uma posi��o
		}else{
			return ""+posicaoInicial+", "+posicaoFinal; // tem mais de uma posi��o
		}
	}
	
	
	/**
	 *    Usado no campo 007 porque o usu�rio n�o vai poder editar a posi��o 00, j� que essa � a 
	 * pr�pria categoria do material e foi escolhida anteriormente. 
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCompo007
	 * @return
	 */
	public boolean isPosicaoEditavel(){
		if(posicaoInicial == posicaoFinal && posicaoInicial == 0 && categoriaMaterial != null){
			return false;
		}else{
			return true;
		}
	}
	
	
	
	
	// sets e gets

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;	
	}

	public short getPosicaoInicial() {
		return posicaoInicial;
	}

	public short getPosicaoFinal() {
		return posicaoFinal;
	}

	public String getValorPadrao() {
		return valorPadrao;
	}

	public String getDescricao() {
		return descricao;
	}

	public FormatoMaterialEtiqueta getFormatoMaterialEtiqueta() {
		return formatoMaterialEtiqueta;
	}

	public CategoriaMaterial getCategoriaMaterial() {
		return categoriaMaterial;
	}


	public FormatoMaterial getFormatoMaterial() {
		return formatoMaterial;
	}

	public void setValorPadrao(String valorPadrao) {
		this.valorPadrao = valorPadrao;
	}

	
	
}
