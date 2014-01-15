/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
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
 * Entidade que representa as Situações Possíveis da Renovação de Estágio.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "status_renovacao_estagio", schema = "estagio")
public class StatusRenovacaoEstagio implements PersistDB {

	/** Status que indica que está aguardando o preenchimento do relatório semestral */
	public static final int AGUARDANDO_RELATORIO = 1;
	/** Status que indica que foi aprovada a renovação */
	public static final int APROVADO = 2;
	
	/** Construtor Padrão */
	public StatusRenovacaoEstagio() {}
	
	/** Construtor definido o id do status */
	public StatusRenovacaoEstagio(int id){
		this.id = id;
	}
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_status_renovacao_estagio")
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
