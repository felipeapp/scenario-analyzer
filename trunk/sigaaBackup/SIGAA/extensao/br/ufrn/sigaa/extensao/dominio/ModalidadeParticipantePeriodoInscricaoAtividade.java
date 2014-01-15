/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 01/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.dominio;

import java.math.BigDecimal;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 *
 * <p>Ralizada a assolicação entre a modalidade de participante dos cursos e eventos de extensão 
 * o período de inscrição. </p>
 *
 * <p> <i> <strong> Define o valor efetivo que o usuário vai pagar pelo curso ou evento.  
 * Cada inscrição pode ter uma valor diferente dependendo da modalidade do usuário. </strong> </i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(schema = "extensao", name = "modalidade_participante_periodo_inscricao_atividade")
public class ModalidadeParticipantePeriodoInscricaoAtividade implements PersistDB{

	/** O id */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="extensao.modalidade_participante_sequence") })
	@Column(name = "id_modalidade_participante_periodo_inscricao_atividade", nullable = false)
	private int id;

	/** O valor da taxa a ser pago, esse valor depende da modalidade do participante e do período de 
	 *  inscrição que ele está associado. */
	@Column(name = "taxa_matricula", nullable=false)
	private BigDecimal taxaMatricula = new BigDecimal(0);
	
	/** A modalidade de participante que vai pagar o valor.*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modalidade_participante", nullable=false)
	private ModalidadeParticipante modalidadeParticipante;
	
	/** O período de inscrição para o qual o valor da taxa é válido. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_inscricao_atividade", nullable=false)
	private InscricaoAtividade periodoInscricao;

	/*** Quando o coordenador do curso ou evento desassocia uma modalidade a relacionamento é desatividado.
	 *   Porque pode ser que alguém já tenha feito a inscrição, então a inscrição participante fica apontando 
	 *   para a modalidade que fi desassociada.  
	 *   Se no momendo da inscrição a modalidade existia o usuário paga aquele valor.*/
	@Column(name="ativo", nullable=false)
	private boolean ativo = true;
	
	
	
	public ModalidadeParticipantePeriodoInscricaoAtividade(){
		
	}
	
	public ModalidadeParticipantePeriodoInscricaoAtividade(int id, BigDecimal taxaMatricula, boolean ativo, int idModalidade, String nomeModalidade, boolean ativoModalidade, InscricaoAtividade periodoInscricao){
		this.id = id;
		this.taxaMatricula = taxaMatricula;
		this.ativo = true;
		this.modalidadeParticipante = new ModalidadeParticipante(idModalidade, nomeModalidade, ativoModalidade);
		this.periodoInscricao = periodoInscricao;
	}
	
	/**
	 * Usado para valores da multa digitados pelo usuário, porque o JSF dava erro ao tentar converte para BigDecimal
	 *
	 * @param valor
	 */
	public void setTaxaMatriculaAsDouble(Double valor) {
		if(valor != null)
			this.taxaMatricula = new BigDecimal(valor);
	}

	/**
	 * Usado para valores da multa digitados pelo usuário, porque o JSF dava erro ao tentar converte para BigDecimal
	 */
	public Double getTaxaMatriculaAsDouble() {
		if(taxaMatricula == null)
			return 0d;
		return taxaMatricula.doubleValue();
	}
	
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(periodoInscricao, modalidadeParticipante);
	}
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "periodoInscricao", "modalidadeParticipante");
	}
	
	
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public BigDecimal getTaxaMatricula() {return taxaMatricula;}
	public void setTaxaMatricula(BigDecimal taxaMatricula) {	this.taxaMatricula = taxaMatricula;}
	public ModalidadeParticipante getModalidadeParticipante() {	return modalidadeParticipante;}
	public void setModalidadeParticipante(ModalidadeParticipante modalidadeParticipante) {this.modalidadeParticipante = modalidadeParticipante;}
	public InscricaoAtividade getPeriodoInscricao() {	return periodoInscricao;}
	public void setPeriodoInscricao(InscricaoAtividade periodoInscricao) {	this.periodoInscricao = periodoInscricao;}
	
}
