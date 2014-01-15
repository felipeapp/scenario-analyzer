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
 * Entidade que representa os Tipos Possíveis de Relatório de Estágio.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "tipo_relatorio_estagio", schema = "estagio")
public class TipoRelatorioEstagio implements PersistDB {
	
	/** Indica que o tipo relatório de estágio é Periódico */
	public static final int RELATORIO_PERIODICO = 1;
	/** Indica que o tipo relatório de estágio é Final */
	public static final int RELATORIO_FINAL = 2;
	/** Indica que o tipo relatório de estágio é de Renovação */
	public static final int RELATORIO_RENOVACAO = 3;
	
	/** Construtor Padrão */
	public TipoRelatorioEstagio() {}
	
	/** Construtor definido o id do tipo */
	public TipoRelatorioEstagio(int id){
		this.id = id;
	}
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_tipo_relatorio_estagio")
	private int id;
	
	/** Descrição do tipo */
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
