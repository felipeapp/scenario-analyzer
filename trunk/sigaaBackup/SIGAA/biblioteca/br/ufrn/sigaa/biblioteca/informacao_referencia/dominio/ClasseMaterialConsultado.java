/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em 31/10/08
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Guarda as informa��es da quantidade de materiais consultados por classe CDU e/ou Black  para o registro 
 * de consulta feito.
 * 
 * @author Agostinho
 * @author Jadson
 * @author Br�ulio
 * 
 * @version A classe antes s� fazia refer�ncia � classifica��o CDU. Foi adicionada classe Black.
 * @version 2.0 Jadson - Alterando para pode conter dados de at� 3 classifica��es bibliogr�ficas, n�o importando qual.
 */

@Entity
@Table(name="classe_material_consultado", schema = "biblioteca")
public class ClasseMaterialConsultado implements PersistDB {

	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column (name="id_classe_material_consultado")
	private int id;

	/** Representa o n�mero informado da 1� utilizada classifica��o no sitema  */
	@Column(name="classificacao_1")
	private String classificacao1;
	
	/** Representa o n�mero informado da 2� utilizada classifica��o no sitema  */
	@Column(name="classificacao_2")
	private String classificacao2;
	
	/** Representa o n�mero informado da 3� utilizada classifica��o no sitema  */
	@Column(name="classificacao_3")
	private String classificacao3;
	
	/** A quantidade de materiais consultados para esta classe */
	private int quantidade;

	/** O registro de consulta a que esta classe pertence.*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_consultas_diarias_materiais")
	private RegistroConsultasDiariasMateriais registroConsultaMaterial;

	/** Quando uma classe j� cadastrada � removida pelo usu�rio ela � desativado */
	@Column(name="ativo")
	private boolean ativo = true; 
	
	/////////////////////  Informa��es de  Auditoria
	
	/** A data da cria��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_criacao")
	@CriadoEm
	private Date dataCriacao;
	
	/** Quem criou */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn (name="id_registro_entrada_criacao")
	@CriadoPor
	private RegistroEntrada registroEntradaCriacao;

	/** A data da �ltima altera��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ultima_atualizacao")
	@AtualizadoEm
	private Date ultimaAtualizacao;
	
	/** Quem alterou pela �ltima vez */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn (name="id_registro_entrada_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroEntradaAtualizacao;
	
	//////////////////////////////////////////
	
	
	/**
	 * Construtor padr�o
	 */
	public ClasseMaterialConsultado() {
		super();
	}

	
	/**
	 * Construtor para criar um objeto apenas com a  classe e quantidade
	 */
	public ClasseMaterialConsultado(String classificacao1, int quantidade) {
		super();
		this.classificacao1 = classificacao1;
		this.quantidade = quantidade;
	}
	
	/**
	 * Uma classe CDU de registro de consultas � igual a outra se tiver o mesmo c�digo CDU/Black
	 */
	@Override
	public boolean equals(Object obj){
		return EqualsUtil.testEquals(this, obj, "classificacao1", "classificacao2", "classificacao3");
	}
	
	/**
	 * Uma classe CDU de registro de consultas � igual a outra se tiver o mesmo c�digo CDU/Black
	 */
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(classificacao1, classificacao2, classificacao3);
	}
	
	
	////////////////////  sets e gets ///////////////////////
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	public String getClassificacao1() {
		return classificacao1;
	}

	public void setClassificacao1(String classificacao1) {
		this.classificacao1 = classificacao1;
	}

	public String getClassificacao2() {
		return classificacao2;
	}

	public void setClassificacao2(String classificacao2) {
		this.classificacao2 = classificacao2;
	}

	public String getClassificacao3() {
		return classificacao3;
	}


	public void setClassificacao3(String classificacao3) {
		this.classificacao3 = classificacao3;
	}


	public RegistroConsultasDiariasMateriais getRegistroConsultaMaterial() {
		return registroConsultaMaterial;
	}

	public void setRegistroConsultaMaterial(
			RegistroConsultasDiariasMateriais registoConsultaMaterial) {
		this.registroConsultaMaterial = registoConsultaMaterial;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroEntradaCriacao() {
		return registroEntradaCriacao;
	}

	public void setRegistroEntradaCriacao(RegistroEntrada registroEntradaCriacao) {
		this.registroEntradaCriacao = registroEntradaCriacao;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public RegistroEntrada getRegistroEntradaAtualizacao() {
		return registroEntradaAtualizacao;
	}

	public void setRegistroEntradaAtualizacao(RegistroEntrada registroEntradaAtualizacao) {
		this.registroEntradaAtualizacao = registroEntradaAtualizacao;
	}

}
