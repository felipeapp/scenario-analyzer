/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Entidade que representa as Situa��es Poss�veis da Solicita��o de Trancamento de programa
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "status_solicitacao_trancamento_programa", schema = "ensino")
public class StatusSolicitacaoTrancamentoPrograma implements PersistDB  {
	
	/**
	 * situa��es poss�veis da solicita��o
	 */
	/** Solicitado o trancamento */
	public static final int SOLICITADO 	= 1;
	/** Trancamento Aceito */
	public static final int TRANCADO 	= 2;
	/** Aluno desistiu de trancar */
	public static final int CANCELADO	= 3;
	/** Trancamento N�o Aceito */
	public static final int INDEFERIDO  = 4;	
	
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.hibernate_sequence") })	
	@Column(name = "id_status_solicitacao_trancamento_programa")
	private int id;
	
	/** Descri��o do Status */
	private String descricao;
	
	/**
	 * Retorna a descri��o da situa��o atual do trancamento
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
