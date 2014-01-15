/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/11/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Entidade que representa as Situações Possíveis do Relatório de Estágio.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "status_relatorio_estagio", schema = "estagio")
public class StatusRelatorioEstagio implements PersistDB {

	/** Indica que o relatório de estágio está Pendente */
	public static final int PENDENTE = 1;
	/** Indica que o relatório de estágio está Aprovado */
	public static final int APROVADO = 2;
	
	/** Construtor Padrão */
	public StatusRelatorioEstagio() {}
	
	/** Construtor definido o id do status */
	public StatusRelatorioEstagio(int id){
		this.id = id;
	}
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_status_relatorio_estagio")
	private int id;
	
	/** Descrição do Status */
	private String descricao;

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
