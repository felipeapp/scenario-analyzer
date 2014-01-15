/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/04/2010 
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Motivos de trancamento pré-definidos para utilização na solicitação 
 * de trancamento de Programa 
 *
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "motivo_trancamento_programa", schema = "ensino", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class MotivoTrancamentoPrograma implements PersistDB{
	
	/**
	 * esta constante deve estar sincronizada com o id da opção OUTRO da tabela.
	 */
	public static final int OUTRO = 5;

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_motivo_trancamento_programa")
	private int id;

	/** Descrição do motivo */
	@Column(name = "descricao")
	private String descricao;

	/**
	 * informa se esta opção deve ser exibida no SELECT nas JSPs
	 */
	private boolean ativo;

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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}
