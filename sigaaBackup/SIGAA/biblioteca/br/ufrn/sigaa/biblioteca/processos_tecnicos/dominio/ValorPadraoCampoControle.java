/*
 * ValorPadraoCampoControle.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
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
 *         Os valores padrões dos campo de controle para aparecer nas suas respectivas páginas de edição. 
 *         Não sendo parecido com o padrão MARC, isso foi feito baseado no funcionamento do ALEPH. 
 *         Não procure em nenhuma documentação sobre MARC que não vai encontrar nada falando de 
 *     "valores padrões" de campos. 
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
	
	/* onde o dado começa dentro do campo de controle */
	@Column(name = "posicao_inicial", nullable=false)
	private short posicaoInicial;

	/* onde o dado finaliza dentro do campo de controle */
	@Column(name = "posicao_final", nullable=false)
	private short posicaoFinal;
	
	
	/* guarda o valor padrão que deve aparecer na tela */
	@Column(name = "valor_padrao", nullable=true)
	private String valorPadrao;

	
	/* a descrição do campo para aparecer na tela*/
	@Column(name = "descricao", nullable=false)
	private String descricao;

	
	/* 
	 *    Para os valores padrões do campo LIDER da base bibliográfica, que são diferentes 
	 *  para cada formato do material.
	 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formato_material", referencedColumnName="id_formato_material")
	private FormatoMaterial formatoMaterial;
	
	
	/* 
	 * Para as etiquetas 006 e 008. No caso da 008 vai estar cadastrado valores para a etiqueta 
	 * 008 bibliográfica e outros para a etiqueta 008 de autoridades.
	 * 
	 * Vai precisar cadastrar valores para a etiqueta LIDER de autoridades também usando 
	 * esse relacionamento.
	 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formato_material_etiqueta", referencedColumnName="id_formato_material_etiqueta")
	private FormatoMaterialEtiqueta formatoMaterialEtiqueta;

	
	/*
	 * Aqui são usados nos casos do campo 007 que possui uma categoria do material e não 
	 * possui relação nenhuma com o formato. Base bibliografica também
	 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_categoria_material", referencedColumnName="id_categoria_material")
	private CategoriaMaterial categoriaMaterial;
	

	
	/**
	 *  Construtor padrão
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
		
		this.categoriaMaterial = categoriaMaterial; // necessaria para saber se a posição do campo 007 pode ser editada ou não.
	}

	
	/**
	 * 
	 * Método que retorna o tamanho que o campo deve ter para exibir o valor padrão na tela.
	 *
	 * @return
	 */
	public int getMaxLength(){
		return 1 + (posicaoFinal - posicaoInicial);
	}
	
	
	
	/**
	 *   Para visualizar na página de edição dos campos de controle 
	 *   
	 * @return
	 */
	public String getPosicao(){
		if(posicaoInicial == posicaoFinal){
			return ""+ posicaoInicial;                  // tem apenas uma posição
		}else{
			return ""+posicaoInicial+", "+posicaoFinal; // tem mais de uma posição
		}
	}
	
	
	/**
	 *    Usado no campo 007 porque o usuário não vai poder editar a posição 00, já que essa é a 
	 * própria categoria do material e foi escolhida anteriormente. 
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCompo007
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
