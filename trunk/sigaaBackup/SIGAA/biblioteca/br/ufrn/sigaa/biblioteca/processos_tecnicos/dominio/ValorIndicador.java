/*
' * ValorIndicador.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import static br.ufrn.arq.util.StringUtils.unescapeHTML;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;

/**
 *
 *        Possui todos os valores que o  indicador pode assumir para um determinado campo
 * apontado por uma determinada etiqueta.
 *
 * @author jadson
 * @since 28/07/2008
 * @version
 */
@Entity
@Table(name = "valor_indicador", schema = "biblioteca")
public class ValorIndicador  implements Validatable{

	public static final short PRIMEIRO = 1;
	public static final short SEGUNDO = 2;

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_valor_indicador")
	private int id;

	/* Informa se é o primeiro ou o segundo indicador, ver constantes*/
	@Column(name = "numero_indicador", nullable=false)
	private short numeroIndicador;

	@Column(name = "valor", nullable=false)
	private Character valor = new Character(' ');

	@Column(name = "descricao", nullable=false)
	private String descricao;
	
	/** um conteúdo informativo ao usuário sobre o valor do indicador*/ 
	private String info;

	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_etiqueta", referencedColumnName="id_etiqueta")
	private Etiqueta etiqueta;

	
	
	/**
	 * Construtor para hibernate e jsf
	 */
	public ValorIndicador() {
	}

	/**
	 * 
	 * @param numeroIndicador
	 * @param valor
	 */
	public ValorIndicador(short numeroIndicador, char valor) {
		this.numeroIndicador = numeroIndicador;
		this.valor = valor;
	}

	
	/**
	 * @param a etiqueta
	 */
	public ValorIndicador(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	/**
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(this.valor, this.numeroIndicador);
	}

	
	/**
	 * <b> ESSE METODO NAO LEVA EM CONSIDERACAO A ETIQUETA </b>
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "valor", "numeroIndicador");
	}

	
	/** 
	 * Valida os dados para o cadastro de campos locais
	 *
	 * @return
	 */
	public ListaMensagens validate() {
		
		ListaMensagens mensagens = new ListaMensagens();
		
		if(this.etiqueta == null){
			mensagens.addErro("O Indicador precisa possuir uma etiqueta para ser salvo.");
			return mensagens; // não dá para validar mais nada
		}
		
		// validações para etiquetas locais, pois somente etiqueta locais podem ser cadastradas.
		if ( valor == null)
			mensagens.addErro("O valor do "+(getNumeroIndicador() == ValorIndicador.PRIMEIRO ? " 1º Indicador " : " 2º Indicador ")+" deve ser informado.");
		
		if ( StringUtils.isEmpty(descricao)){
			mensagens.addErro("A descrição do valor do "+(getNumeroIndicador() == ValorIndicador.PRIMEIRO ? " 1º Indicador " : " 2º Indicador ")+" deve ser informada.");
		}
		
		if(numeroIndicador <= 0 ){
			mensagens.addErro("O número do indicador deve ser informado.");
		}
		
		return mensagens;
	}

	
	
	@Override
	public String toString() {
		return "ValorIndicador [id=" + id + ", numeroIndicador="
				+ numeroIndicador + ", valor=" + valor + ", descricao="
				+ descricao + ", info=" + info + ", etiqueta=" + etiqueta + "]";
	}

	

	// sets e gets



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;		
	}

	public short getNumeroIndicador() {
		return numeroIndicador;
	}

	public void setNumeroIndicador(short numeroIndicador) {
		this.numeroIndicador = numeroIndicador;
	}

	public Character getValor() {
		return valor;
	}

	public void setValor(Character valor) {
		if(valor == null){
			valor =  new Character(' '); 
		}
		this.valor = valor;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = unescapeHTML(info);
	}

	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = unescapeHTML(descricao);
	}

	public short getValorPrimeiro() {
		return PRIMEIRO;
	}

	public short getValorSegundo() {
		return SEGUNDO;
	}


	
}