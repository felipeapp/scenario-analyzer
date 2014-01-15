/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 14/03/2011
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa as Situações Possíveis da Solicitação de Trancamento de programa
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "status_solicitacao_trancamento_programa", schema = "ensino")
public class StatusSolicitacaoTrancamentoPrograma implements PersistDB  {
	
	/**
	 * situações possíveis da solicitação
	 */
	/** Solicitado o trancamento */
	public static final int SOLICITADO 	= 1;
	/** Trancamento Aceito */
	public static final int TRANCADO 	= 2;
	/** Aluno desistiu de trancar */
	public static final int CANCELADO	= 3;
	/** Trancamento Não Aceito */
	public static final int INDEFERIDO  = 4;	
	
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.hibernate_sequence") })	
	@Column(name = "id_status_solicitacao_trancamento_programa")
	private int id;
	
	/** Descrição do Status */
	private String descricao;
	
	/**
	 * Retorna a descrição da situação atual do trancamento
	 * @return
	 */
	public static String getSituacaoDescricao(int situacao) {
		switch (situacao) {
		case StatusSolicitacaoTrancamentoPrograma.SOLICITADO:
			return "SOLICITADO";
		case StatusSolicitacaoTrancamentoPrograma.TRANCADO:
			return "TRANCADO";
		case StatusSolicitacaoTrancamentoPrograma.CANCELADO:
			return "CANCELADO";
		case StatusSolicitacaoTrancamentoPrograma.INDEFERIDO:
			return "INDEFERIDO";			
		default:
			return "INDEFINIDO";
		}
	}	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	

}
