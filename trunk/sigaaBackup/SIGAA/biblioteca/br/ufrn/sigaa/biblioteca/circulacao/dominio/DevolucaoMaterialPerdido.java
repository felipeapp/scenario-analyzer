/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Classe que guarda os dados da devolução do material quando esse materail foi perdido pelo usuário.</p>
 *
 * <p>Precisamos aguarda informações sobre o porque um determinado material que foi perdido pelo usuário não foi devolvido. Caso ocorra essa situação.<p>
 *
 * <p>Utilizado para o relatório de comunicações de perda de material</p>
 *
 * @author Jadson
 * @since 11/04/2012
 * @version 1.0 criação da classe
 */
@Entity
@Table (name="devolucao_material_perdido", schema="biblioteca")
public class DevolucaoMaterialPerdido implements PersistDB{

	/** Os tipo de devolução de material perdido que podem ocorrer no sistema. */
	public enum TipoDevolucaoMaterialPerdido{
		/** O caso padrão, onde o usuário devolvel o material similar, o material terá o mesmo código de barras, o anterior não será baixado. */
		USUARIO_ENTREGOU_MATERIAL_SIMILAR((short) 0),
		/** O caso padrão, onde o usuário devolvel o material, mas ele não é similar, o material perido era para ser baixado e criado um novo. */
		USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE((short) 1),
		/** Exceção que pode ocorrer no sistema, em caso de decisão judicial, ou algo do tipo. No qual o usuário não entregou o material mas sua situação foi regularizada. */
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
	
	/** O empréstimo cujo material foi perdido e gerou essa devolução. 
	 */
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="id_emprestimo", referencedColumnName="id_emprestimo")
	private Emprestimo emprestimo;
	
	
	/** O tipo de devolução ocorrida. Informa se o usuário entregou um material substituto, ou não.*/
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as variáveis são declaradas
	@Column(name="tipo", nullable=false)
	private TipoDevolucaoMaterialPerdido tipo;
	
	/** Caso o usuário não entre um material para ser substituído, deve-se informato o motivo para sair no relatório. */
	@Column(name="motivo_nao_entrega_material")
	private String motivoNaoEntregaMaterial;

	////////////////////////////INFORMAÇÕES DE AUDITORIA  ///////////////////////////////////////
	
	/**
	 * informações do usuário que finalizou o empréstimo de perda de material, essa informação também se encontra no empréstimo.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * a data quando o empréstimos com perda foi finalizado, essa informação também se encontra no empréstimo.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;
	
	////////////////////////////////////////////////////////////////////////////////
	
	/** Verifica se o tipo da devolução foi por um mateiral similar */
	public boolean isUsuarioEntregouMaterialSimilar(){
		return tipo == TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_SIMILAR;
	}
	
	/** Verifica se o tipo da devolução foi por um mateiral equivalente */
	public boolean isUsuarioEntregouMaterialEquivalente(){
		return tipo == TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE;
	}
	
	/** Verifica se o tipo da devolução foi com a não entrega do material pelo usuário. */
	public boolean isUsuarioNaoEntregouMaterial(){
		return tipo == TipoDevolucaoMaterialPerdido.USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO;
	}
	
	/**
	 * Retorna os status que são considerados ativos.
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
