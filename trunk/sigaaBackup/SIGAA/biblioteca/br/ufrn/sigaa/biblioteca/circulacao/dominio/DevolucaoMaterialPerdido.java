/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 11/04/2012
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * <p>Classe que guarda os dados da devolu��o do material quando esse materail foi perdido pelo usu�rio.</p>
 *
 * <p>Precisamos aguarda informa��es sobre o porque um determinado material que foi perdido pelo usu�rio n�o foi devolvido. Caso ocorra essa situa��o.<p>
 *
 * <p>Utilizado para o relat�rio de comunica��es de perda de material</p>
 *
 * @author Jadson
 * @since 11/04/2012
 * @version 1.0 cria��o da classe
 */
@Entity
@Table (name="devolucao_material_perdido", schema="biblioteca")
public class DevolucaoMaterialPerdido implements PersistDB{

	/** Os tipo de devolu��o de material perdido que podem ocorrer no sistema. */
	public enum TipoDevolucaoMaterialPerdido{
		/** O caso padr�o, onde o usu�rio devolvel o material similar, o material ter� o mesmo c�digo de barras, o anterior n�o ser� baixado. */
		USUARIO_ENTREGOU_MATERIAL_SIMILAR((short) 0),
		/** O caso padr�o, onde o usu�rio devolvel o material, mas ele n�o � similar, o material perido era para ser baixado e criado um novo. */
		USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE((short) 1),
		/** Exce��o que pode ocorrer no sistema, em caso de decis�o judicial, ou algo do tipo. No qual o usu�rio n�o entregou o material mas sua situa��o foi regularizada. */
		USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO((short) 2);
		
		private TipoDevolucaoMaterialPerdido(Short valor){
			this.valor = valor;
		}
		
		/** O valor ordinal do enum */
		private Short valor;

		
		public Short getValor() {
			return valor;
		}

		@Override
		public String toString() {
			return valor.toString();
		}
		
		/** Retorna o enum a partir do seu valor*/
		public static TipoDevolucaoMaterialPerdido getTipoDevolucao(short valor){
			if(valor == USUARIO_ENTREGOU_MATERIAL_SIMILAR.getValor())
				return USUARIO_ENTREGOU_MATERIAL_SIMILAR;
			if(valor == USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE.getValor())
				return USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE;
			if(valor == USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO.getValor())
				return USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO;
			return null;
		}
	}
	
	/** O id*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.prorrogacao_emprestimo_sequence") })
	@Column(name = "id_devolucao_material_perdido", nullable = false)
	private int id;
	
	/** O empr�stimo cujo material foi perdido e gerou essa devolu��o. 
	 */
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="id_emprestimo", referencedColumnName="id_emprestimo")
	private Emprestimo emprestimo;
	
	
	/** O tipo de devolu��o ocorrida. Informa se o usu�rio entregou um material substituto, ou n�o.*/
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as vari�veis s�o declaradas
	@Column(name="tipo", nullable=false)
	private TipoDevolucaoMaterialPerdido tipo;
	
	/** Caso o usu�rio n�o entre um material para ser substitu�do, deve-se informato o motivo para sair no relat�rio. */
	@Column(name="motivo_nao_entrega_material")
	private String motivoNaoEntregaMaterial;

	////////////////////////////INFORMA��ES DE AUDITORIA  ///////////////////////////////////////
	
	/**
	 * informa��es do usu�rio que finalizou o empr�stimo de perda de material, essa informa��o tamb�m se encontra no empr�stimo.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * a data quando o empr�stimos com perda foi finalizado, essa informa��o tamb�m se encontra no empr�stimo.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;
	
	////////////////////////////////////////////////////////////////////////////////
	
	/** Verifica se o tipo da devolu��o foi por um mateiral similar */
	public boolean isUsuarioEntregouMaterialSimilar(){
		return tipo == TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_SIMILAR;
	}
	
	/** Verifica se o tipo da devolu��o foi por um mateiral equivalente */
	public boolean isUsuarioEntregouMaterialEquivalente(){
		return tipo == TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE;
	}
	
	/** Verifica se o tipo da devolu��o foi com a n�o entrega do material pelo usu�rio. */
	public boolean isUsuarioNaoEntregouMaterial(){
		return tipo == TipoDevolucaoMaterialPerdido.USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO;
	}
	
	/**
	 * Retorna os status que s�o considerados ativos.
	 *
	 * @return
	 */
	public static TipoDevolucaoMaterialPerdido[] getDevolucoesRepostas(){
		return new TipoDevolucaoMaterialPerdido[]{TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE, TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_SIMILAR};
	}
	
	
	//// sets e gets ///
	
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public TipoDevolucaoMaterialPerdido getTipo() {return tipo;}
	public void setTipo(TipoDevolucaoMaterialPerdido tipo) {this.tipo = tipo;}
	public String getMotivoNaoEntregaMaterial() {return motivoNaoEntregaMaterial;}
	public void setMotivoNaoEntregaMaterial(String motivoNaoEntregaMaterial) {this.motivoNaoEntregaMaterial = motivoNaoEntregaMaterial;}
	public Emprestimo getEmprestimo() {return emprestimo;}
	public void setEmprestimo(Emprestimo emprestimo) {this.emprestimo = emprestimo;}
	public RegistroEntrada getRegistroCriacao() {return registroCriacao;}
	public void setRegistroCriacao(RegistroEntrada registroCriacao) {this.registroCriacao = registroCriacao;}
	public Date getDataCriacao() {return dataCriacao;}
	public void setDataCriacao(Date dataCriacao) {this.dataCriacao = dataCriacao;}
	
}
