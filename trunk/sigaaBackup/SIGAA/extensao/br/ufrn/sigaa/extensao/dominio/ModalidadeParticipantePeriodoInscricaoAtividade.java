/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Ralizada a assolica��o entre a modalidade de participante dos cursos e eventos de extens�o 
 * o per�odo de inscri��o. </p>
 *
 * <p> <i> <strong> Define o valor efetivo que o usu�rio vai pagar pelo curso ou evento.  
 * Cada inscri��o pode ter uma valor diferente dependendo da modalidade do usu�rio. </strong> </i> </p>
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

	/** O valor da taxa a ser pago, esse valor depende da modalidade do participante e do per�odo de 
	 *  inscri��o que ele est� associado. */
	@Column(name = "taxa_matricula", nullable=false)
	private BigDecimal taxaMatricula = new BigDecimal(0);
	
	/** A modalidade de participante que vai pagar o valor.*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modalidade_participante", nullable=false)
	private ModalidadeParticipante modalidadeParticipante;
	
	/** O per�odo de inscri��o para o qual o valor da taxa � v�lido. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_inscricao_atividade", nullable=false)
	private InscricaoAtividade periodoInscricao;

	/*** Quando o coordenador do curso ou evento desassocia uma modalidade a relacionamento � desatividado.
	 *   Porque pode ser que algu�m j� tenha feito a inscri��o, ent�o a inscri��o participante fica apontando 
	 *   para a modalidade que fi desassociada.  
	 *   Se no momendo da inscri��o a modalidade existia o usu�rio paga aquele valor.*/
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
	 * Usado para valores da multa digitados pelo usu�rio, porque o JSF dava erro ao tentar converte para BigDecimal
	 *
	 * @param valor
	 */
	public void setTaxaMatriculaAsDouble(Double valor) {
		if(valor != null)
			this.taxaMatricula = new BigDecimal(valor);
	}

	/**
	 * Usado para valores da multa digitados pelo usu�rio, porque o JSF dava erro ao tentar converte para BigDecimal
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
