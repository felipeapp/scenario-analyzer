package br.ufrn.sigaa.assistencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * 
 * Durante o período de atividade acadêmica, determinados discentes que possuem
 * filhos podem solicitar bolsa auxílio-creche. Esta classe representa o
 * período de atividade acadêmica e contém informação sobre onde os 
 * filhos desses discentes estudam.
 *  
 *
 */
@Entity
@Table(name = "periodo_atividade_academica", schema = "sae")
public class PeriodoAtividadeAcademica implements Validatable {

	/** Local default onde o(s) filho(s) do discente que solicitou auxílio-creche estuda(m). */
	public static final int EM_CRECHE = 1;
	
	/** Chave primária do período de atividade acadêmica */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_atividade_periodo")
	private int id;

	/** Local onde os filhos do discente que solicitou bolsa-creche estuda. */
	private String descricao;

	public PeriodoAtividadeAcademica() {
	}

	public PeriodoAtividadeAcademica(int id) {
		this.id = id;
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

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}